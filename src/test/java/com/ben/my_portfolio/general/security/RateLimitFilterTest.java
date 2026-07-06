package com.ben.my_portfolio.general.security;

import com.ben.my_portfolio.ErrorDetails;
import com.ben.my_portfolio.users.security.RateLimitConfig;
import com.ben.my_portfolio.users.security.RateLimitFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimitFilterTest{

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private LettuceBasedProxyManager<String> proxyManager;

    @Mock
    private RateLimitConfig rateLimitConfig;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private Bucket bucket;
    @Mock
    private ConsumptionProbe probe;

    @InjectMocks
    private RateLimitFilter rateLimitFilter;

    private static final String IP = "192.168.1.1";

    @BeforeEach
    void setUp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(IP);
    }

    // helper — stubs the deep chain and token consumption in one place
    private void stubBucketChain(BucketConfiguration config, boolean consumed, long remaining) {
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(consumed);
        if (consumed) {
            when(probe.getRemainingTokens()).thenReturn(remaining);
        }
    }

// ─────────────────────────────────────────────────────────────
// getBucketConfig — null (unknown path) → pass through
// ─────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "unknown path [{0}] → null config → passes through")
    @ValueSource(strings = {"/api/other", "/admin/dashboard", "/health"})
    @DisplayName("doFilter: unknown path → null config → chain passes, no bucket interaction")
    void doFilter_unknownPath_passesThrough(String path) throws Exception {
        when(request.getServletPath()).thenReturn(path);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(bucket);
    }

// ─────────────────────────────────────────────────────────────
// getBucketConfig — each named path routes to the right config
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getBucketConfig: /users/sign-in → signInConfig")
    void doFilter_signInPath_usesSignInConfig() throws Exception {
        BucketConfiguration config = mock(BucketConfiguration.class);
        when(request.getServletPath()).thenReturn("/users/sign-in");
        when(rateLimitConfig.signInConfig()).thenReturn(config);
        stubBucketChain(config, true, 4L);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimitConfig).signInConfig();
    }

    @Test
    @DisplayName("getBucketConfig: /users/sign-up → signUpConfig")
    void doFilter_signUpPath_usesSignUpConfig() throws Exception {
        BucketConfiguration config = mock(BucketConfiguration.class);
        when(request.getServletPath()).thenReturn("/users/sign-up");
        when(rateLimitConfig.signUpConfig()).thenReturn(config);
        stubBucketChain(config, true, 2L);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimitConfig).signUpConfig();
    }

    @Test
    @DisplayName("getBucketConfig: /users/confirm-account → confirmAccountConfig")
    void doFilter_confirmAccountPath_usesConfirmAccountConfig() throws Exception {
        BucketConfiguration config = mock(BucketConfiguration.class);
        when(request.getServletPath()).thenReturn("/users/confirm-account");
        when(rateLimitConfig.confirmAccountConfig()).thenReturn(config);
        stubBucketChain(config, true, 4L);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimitConfig).confirmAccountConfig();
    }

    @ParameterizedTest(name = "oauth2 path [{0}] → oauth2Config")
    @ValueSource(strings = {"/oauth2/authorization/google", "/login/oauth2/code/google"})
    @DisplayName("getBucketConfig: oauth2 paths → oauth2Config")
    void doFilter_oauth2Paths_usesOauth2Config(String path) throws Exception {
        BucketConfiguration config = mock(BucketConfiguration.class);
        when(request.getServletPath()).thenReturn(path);
        when(rateLimitConfig.oauth2Config()).thenReturn(config);
        stubBucketChain(config, true, 9L);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimitConfig).oauth2Config();
    }

    @ParameterizedTest(name = "public path [{0}] → publicConfig")
    @ValueSource(strings = {"/articles/latest", "/profile/ben"})
    @DisplayName("getBucketConfig: articles/profile paths → publicConfig")
    void doFilter_publicPaths_usesPublicConfig(String path) throws Exception {
        BucketConfiguration config = mock(BucketConfiguration.class);
        when(request.getServletPath()).thenReturn(path);
        when(rateLimitConfig.publicConfig()).thenReturn(config);
        stubBucketChain(config, true, 59L);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(rateLimitConfig).publicConfig();
    }

// ─────────────────────────────────────────────────────────────
// doFilterInternal — token consumed (happy path)
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("doFilter: token consumed → X-Rate-Limit-Remaining header set, chain passes")
    void doFilter_tokenConsumed_setsHeaderAndPassesThrough() throws Exception {
        when(request.getServletPath()).thenReturn("/users/sign-in");
        when(rateLimitConfig.signInConfig()).thenReturn(mock(BucketConfiguration.class));
        stubBucketChain(null, true, 3L);

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(response).addHeader("X-Rate-Limit-Remaining", "3");
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

// ─────────────────────────────────────────────────────────────
// doFilterInternal — rate limit exceeded (429 path)
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("doFilter: rate limit exceeded → 429 with Retry-After header and error body")
    void doFilter_rateLimitExceeded_returns429WithBody() throws Exception {
        StringWriter responseBody = new StringWriter();

        when(request.getServletPath()).thenReturn("/users/sign-in");
        when(rateLimitConfig.signInConfig()).thenReturn(mock(BucketConfiguration.class));
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(probe);
        when(probe.isConsumed()).thenReturn(false);
        when(probe.getNanosToWaitForRefill()).thenReturn(30_000_000_000L); // 30 seconds
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"error\":\"Too many requests\"}");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(response).setContentType("application/json");
        verify(response).addHeader("Retry-After", "30");
        verify(response).addHeader("X-Rate-Limit-Remaining", "0");
        assertThat(responseBody.toString()).isEqualTo("{\"error\":\"Too many requests\"}");
        verify(filterChain, never()).doFilter(any(), any());

        ArgumentCaptor<ErrorDetails> captor = ArgumentCaptor.forClass(ErrorDetails.class);
        verify(objectMapper).writeValueAsString(captor.capture());
        ErrorDetails captured = captor.getValue();
        assertThat(captured.message()).contains("30 seconds");
        assertThat(captured.errorCode()).isEqualTo("RATE_LIMIT_EXCEEDED");
    }

// ─────────────────────────────────────────────────────────────
// getClientIp — all branches
// ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getClientIp: uses X-Forwarded-For when present and valid")
    void getClientIp_xForwardedForPresent_usesIt() throws Exception {
        when(request.getServletPath()).thenReturn("/health");
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("getClientIp: X-Forwarded-For null → falls back to X-Real-IP")
    void getClientIp_xForwardedForNull_usesXRealIp() throws Exception {
        when(request.getServletPath()).thenReturn("/health");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("10.0.0.2");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("getClientIp: both headers null → falls back to RemoteAddr")
    void getClientIp_bothHeadersNull_usesRemoteAddr() throws Exception {
        when(request.getServletPath()).thenReturn("/health");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("10.0.0.3");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("getClientIp: comma-separated X-Forwarded-For → uses first IP only")
    void getClientIp_commaSeparatedForwardedFor_usesFirstIp() throws Exception {
        when(request.getServletPath()).thenReturn("/health");
        when(request.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2, 10.0.0.3");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("getClientIp: 'unknown' X-Forwarded-For → falls back to X-Real-IP")
    void getClientIp_unknownForwardedFor_fallsBackToXRealIp() throws Exception {
        when(request.getServletPath()).thenReturn("/health");
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getHeader("X-Real-IP")).thenReturn("10.0.0.4");

        rateLimitFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

}