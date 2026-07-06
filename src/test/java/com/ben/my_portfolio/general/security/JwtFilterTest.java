package com.ben.my_portfolio.general.security;


import com.ben.my_portfolio.users.Role;
import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.domain.UserNotFoundException;
import com.ben.my_portfolio.users.domain.UserRepository;
import com.ben.my_portfolio.users.security.JwtFilter;
import com.ben.my_portfolio.users.security.JwtHelper;
import com.ben.my_portfolio.users.security.SecurityUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class JwtFilterTest {

    @Mock private JwtHelper jwtHelper;
    @Mock private SecurityUserDetailsService userDetailsService;
    @Mock private UserRepository             userRepo;
    @Mock private HttpServletRequest         request;
    @Mock private HttpServletResponse        response;
    @Mock private FilterChain                filterChain;
    @Mock private UserDetails                userDetails;

    @InjectMocks
    private JwtFilter jwtFilter;

    private static final String TOKEN       = "header.payload.sig";
    private static final String BEARER      = "Bearer " + TOKEN;
    private static final String EMAIL       = "ben@benandco.dev";
    private static final String SECURE_PATH = "/api/profile";

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail(EMAIL);
        user.setRole(Role.USER);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ① — public path → pass straight through
    // ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "public path [{0}] is passed through without JWT check")
    @ValueSource(strings = {
            "/oauth2/authorization/google",
            "/login/oauth2/code/google",
            "/users/sign-in",
            "/users/sign-up"
    })
    void doFilter_publicPath_passesWithoutJwtCheck(String path) throws Exception {
        when(request.getServletPath()).thenReturn(path);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtHelper, userDetailsService, userRepo);
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ② — no / bad Authorization header → pass through
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("no Authorization header → chain passes, no JWT processing")
    void doFilter_noAuthorizationHeader_passesThrough() throws Exception {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtHelper, userDetailsService, userRepo);
    }

    @Test
    @DisplayName("Authorization header without Bearer prefix → chain passes, no JWT processing")
    void doFilter_nonBearerHeader_passesThrough() throws Exception {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtHelper, userDetailsService, userRepo);
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ③a — email is null (invalid token) → pass through
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("extractUsername returns null → authentication skipped, chain passes")
    void doFilter_nullEmail_skipsAuthentication() throws Exception {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn(BEARER);
        when(jwtHelper.extractUsername(TOKEN)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService, userRepo);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ③b — authentication already set → pass through
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("SecurityContext already has authentication → skips re-authentication")
    void doFilter_alreadyAuthenticated_skipsAuthentication() throws Exception {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn(BEARER);
        when(jwtHelper.extractUsername(TOKEN)).thenReturn(EMAIL);

        // pre-seed a non-null authentication in context
        var existingAuth = new org.springframework.security.authentication
                .UsernamePasswordAuthenticationToken(EMAIL, null);
        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService, userRepo);
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ④ — token invalid → skip auth, chain passes
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid returns false → authentication skipped, chain passes")
    void doFilter_invalidToken_skipsAuthentication() throws Exception {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn(BEARER);
        when(jwtHelper.extractUsername(TOKEN)).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtHelper.isTokenValid(TOKEN, userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userRepo);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ⑤ — user not found in DB → UserNotFoundException thrown
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("user not found in repository → UserNotFoundException is thrown")
    void doFilter_userNotFound_throwsUserNotFoundException() {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn(BEARER);
        when(jwtHelper.extractUsername(TOKEN)).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtHelper.isTokenValid(TOKEN, userDetails)).thenReturn(true);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                jwtFilter.doFilterInternal(request, response, filterChain))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("USER NOT FOUND");

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // Branch ⑥ — happy path → auth set in SecurityContext
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("valid token and existing user → authentication set in SecurityContext")
    void doFilter_validToken_setsAuthentication() throws Exception {
        when(request.getServletPath()).thenReturn(SECURE_PATH);
        when(request.getHeader("Authorization")).thenReturn(BEARER);
        when(jwtHelper.extractUsername(TOKEN)).thenReturn(EMAIL);
        when(userDetailsService.loadUserByUsername(EMAIL)).thenReturn(userDetails);
        when(jwtHelper.isTokenValid(TOKEN, userDetails)).thenReturn(true);
        when(userDetails.getUsername()).thenReturn(EMAIL);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(jwtHelper.extractRole(TOKEN)).thenReturn("ROLE_USER");

        jwtFilter.doFilterInternal(request, response, filterChain);

        // SecurityContext must be populated
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(user);
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");

        // chain must still proceed after auth is set
        verify(filterChain).doFilter(request, response);
    }
}
