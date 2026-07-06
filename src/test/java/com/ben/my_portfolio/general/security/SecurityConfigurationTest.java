package com.ben.my_portfolio.general.security;


import com.ben.my_portfolio.users.security.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {})
@Import(SecurityConfiguration.class)
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SecurityUserDetailsService userDetailsService;
    @MockitoBean
    private JwtFilter jwtFilter;
    @MockitoBean
    private Oauth2SuccessHandler oAuth2SuccessHandler;
    @MockitoBean
    private RateLimitFilter rateLimitFilter;

// ─────────────────────────────────────────────────────────────
// Public endpoints — no auth required
// ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "public endpoint [{0}] is accessible without authentication")
    @ValueSource(strings = {
            "/users/sign-in",
            "/users/sign-up",
            "/users/confirm-account",
            "/users/resend-verification",
            "/users/contact",
            "/articles",
            "/swagger-ui/index.html",
            "/v3/api-docs/swagger-config",
            "/swagger-ui.html"
    })
    @DisplayName("public endpoints: accessible without authentication")
    void publicEndpoints_noAuth_notUnauthorized(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().is(not(401)));
    }

    @Test
    @DisplayName("public endpoint: /oauth2/** accessible without authentication")
    void oauth2Endpoint_noAuth_notUnauthorized() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is(not(401)));
    }

    @Test
    @DisplayName("public endpoint: /login/oauth2/** accessible without authentication")
    void loginOauth2Endpoint_noAuth_notUnauthorized() throws Exception {
        mockMvc.perform(get("/login/oauth2/code/google"))
                .andExpect(status().is(not(401)));
    }

    @Test
    @DisplayName("public endpoint: /articles/{id} accessible without authentication")
    void articleById_noAuth_notUnauthorized() throws Exception {
        mockMvc.perform(get("/articles/42"))
                .andExpect(status().is(not(401)));
    }

// ─────────────────────────────────────────────────────────────
// Protected endpoints — 401 without auth
// ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "protected endpoint [{0}] returns 401 without auth")
    @ValueSource(strings = {
            "/profile",
            "/admin/dashboard",
            "/api/private"
    })
    @DisplayName("protected endpoints: return 401 without authentication")
    void protectedEndpoints_noAuth_returns401(String path) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.error")
                        .value("Unauthorized - Please provide a valid JWT token"));
    }

// ─────────────────────────────────────────────────────────────
// Protected endpoints — accessible with valid auth
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("protected endpoint: accessible with authenticated user")
    @WithMockUser
    void protectedEndpoint_withAuth_notUnauthorized() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is(not(401)));
    }

// ─────────────────────────────────────────────────────────────
// CSRF disabled — POST without token must not return 403
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("CSRF disabled: POST to public endpoint without CSRF token is not 403")
    void csrfDisabled_postWithoutToken_notForbidden() throws Exception {
        mockMvc.perform(post("/users/sign-in"))
                .andExpect(status().is(not(403)));
    }

// ─────────────────────────────────────────────────────────────
// passwordEncoder bean
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("passwordEncoder: BCryptPasswordEncoder encodes and matches correctly")
    void passwordEncoder_encodesAndMatchesPassword() {
        SecurityConfiguration config = new SecurityConfiguration(
                userDetailsService, jwtFilter, oAuth2SuccessHandler, rateLimitFilter);

        var encoder = config.passwordEncoder();
        String raw = "securePassword123";
        String encoded = encoder.encode(raw);

        assertThat(encoded).isNotEqualTo(raw);
        assertThat(encoder.matches(raw, encoded)).isTrue();
        assertThat(encoder.matches("wrongPassword", encoded)).isFalse();
    }

// ─────────────────────────────────────────────────────────────
// authenticationProvider bean
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("provider: returns DaoAuthenticationProvider with correct type")
    void provider_returnsDaoAuthenticationProvider() {
        SecurityConfiguration config = new SecurityConfiguration(
                userDetailsService, jwtFilter, oAuth2SuccessHandler, rateLimitFilter);

        var provider = config.provider();

        assertThat(provider)
                .isNotNull()
                .isInstanceOf(
                        org.springframework.security.authentication.dao.DaoAuthenticationProvider.class);
    }

}
