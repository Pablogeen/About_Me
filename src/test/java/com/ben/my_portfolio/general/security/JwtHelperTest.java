package com.ben.my_portfolio.general.security;


import com.ben.my_portfolio.users.Role;
import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.security.JwtHelper;
import com.ben.my_portfolio.users.security.JwtProperties;
import com.ben.my_portfolio.users.security.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtHelperTest {

    @Mock private JwtEncoder encoder;
    @Mock private JwtDecoder decoder;
    @Mock private JwtProperties jwtProperties;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private JwtHelper jwtHelper;

    private static final String EMAIL    = "ben@benandco.dev";
    private static final String RAW_TOKEN = "header.payload.signature";
    private static final long   EXPIRY   = 3600L; // 1 hour

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail(EMAIL);
        user.setRole(Role.USER);
        user.setIsVerified(true);
        user.setId(1L);
    }

    // ─────────────────────────────────────────────────────────────
    // generateToken
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("generateToken: builds correct claims and returns JwtToken")
    void generateToken_validUser_returnsJwtToken() {
        // arrange
        when(jwtProperties.getAccessTokenExpiry()).thenReturn(EXPIRY);
        when(jwtProperties.getIssuer()).thenReturn("ben&co");

        Jwt fakeJwt = mock(Jwt.class);
        when(fakeJwt.getTokenValue()).thenReturn(RAW_TOKEN);
        when(encoder.encode(any(JwtEncoderParameters.class))).thenReturn(fakeJwt);

        Instant before = Instant.now();

        // act
        JwtToken result = jwtHelper.generateToken(user);

        // assert — token value
        assertThat(result.token()).isEqualTo(RAW_TOKEN);

        // assert — expiresAt is roughly now + EXPIRY
        assertThat(result.expiresAt())
                .isAfterOrEqualTo(before.plusSeconds(EXPIRY))
                .isBeforeOrEqualTo(Instant.now().plusSeconds(EXPIRY));

        // assert — encoder was called with parameters that include the right claims
        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(encoder).encode(captor.capture());

        var claims = captor.getValue().getClaims();
        assertThat(claims.getSubject()).isEqualTo(EMAIL);
        assertThat(claims.<String>getClaim("issuer")).isEqualTo("ben&co");
        assertThat(claims.<Long>getClaim("user_id")).isEqualTo(1L);
        assertThat(claims.<String>getClaim("role")).isEqualTo("USER");
        assertThat(claims.<Boolean>getClaim("isVerified")).isTrue();
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiresAt()).isNotNull();
    }

    // ─────────────────────────────────────────────────────────────
    // extractUsername
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("extractUsername: returns subject when token is valid")
    void extractUsername_validToken_returnsSubject() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(EMAIL);
        when(decoder.decode(RAW_TOKEN)).thenReturn(jwt);

        String result = jwtHelper.extractUsername(RAW_TOKEN);

        assertThat(result).isEqualTo(EMAIL);
    }

    @Test
    @DisplayName("extractUsername: returns null when decoder throws JwtException")
    void extractUsername_invalidToken_returnsNull() {
        when(decoder.decode(RAW_TOKEN)).thenThrow(new JwtException("bad token"));

        String result = jwtHelper.extractUsername(RAW_TOKEN);

        assertThat(result).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // extractRole
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("extractRole: returns role claim when token is valid")
    void extractRole_validToken_returnsRole() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.<String>getClaim("role")).thenReturn("USER");
        when(decoder.decode(RAW_TOKEN)).thenReturn(jwt);

        String result = jwtHelper.extractRole(RAW_TOKEN);

        assertThat(result).isEqualTo("USER");
    }

    @Test
    @DisplayName("extractRole: returns null when decoder throws JwtException")
    void extractRole_invalidToken_returnsNull() {
        when(decoder.decode(RAW_TOKEN)).thenThrow(new JwtException("bad token"));

        String result = jwtHelper.extractRole(RAW_TOKEN);

        assertThat(result).isNull();
    }

    // ─────────────────────────────────────────────────────────────
    // isTokenValid — happy path
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid: returns true when username matches and token is not expired")
    void isTokenValid_validTokenMatchingUser_returnsTrue() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(EMAIL);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(decoder.decode(RAW_TOKEN)).thenReturn(jwt);
        when(userDetails.getUsername()).thenReturn(EMAIL);

        boolean result = jwtHelper.isTokenValid(RAW_TOKEN, userDetails);

        assertThat(result).isTrue();
    }

    // ─────────────────────────────────────────────────────────────
    // isTokenValid — expired
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid: returns false when token is expired")
    void isTokenValid_expiredToken_returnsFalse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(EMAIL);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().minusSeconds(1));
        when(decoder.decode(RAW_TOKEN)).thenReturn(jwt);
        when(userDetails.getUsername()).thenReturn(EMAIL);

        boolean result = jwtHelper.isTokenValid(RAW_TOKEN, userDetails);

        assertThat(result).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // isTokenValid — null expiresAt
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid: returns false when expiresAt is null")
    void isTokenValid_nullExpiresAt_returnsFalse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(EMAIL);
        when(jwt.getExpiresAt()).thenReturn(null);
        when(decoder.decode(RAW_TOKEN)).thenReturn(jwt);
        when(userDetails.getUsername()).thenReturn(EMAIL);

        boolean result = jwtHelper.isTokenValid(RAW_TOKEN, userDetails);

        assertThat(result).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // isTokenValid — username mismatch
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid: returns false when username does not match")
    void isTokenValid_usernameMismatch_returnsFalse() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(EMAIL);
        when(jwt.getExpiresAt()).thenReturn(Instant.now().plusSeconds(3600));
        when(decoder.decode(RAW_TOKEN)).thenReturn(jwt);
        when(userDetails.getUsername()).thenReturn("other@email.com");

        boolean result = jwtHelper.isTokenValid(RAW_TOKEN, userDetails);

        assertThat(result).isFalse();
    }

    // ─────────────────────────────────────────────────────────────
    // isTokenValid — JwtException
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("isTokenValid: returns false when decoder throws JwtException")
    void isTokenValid_jwtException_returnsFalse() {
        when(decoder.decode(RAW_TOKEN)).thenThrow(new JwtException("tampered"));

        boolean result = jwtHelper.isTokenValid(RAW_TOKEN, userDetails);

        assertThat(result).isFalse();
    }
}
