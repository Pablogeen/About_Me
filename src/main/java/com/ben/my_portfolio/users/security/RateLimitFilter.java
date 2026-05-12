package com.ben.my_portfolio.users.security;

import com.ben.my_portfolio.ErrorDetails;
import com.ben.my_portfolio.RateLimitException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter{

    private final LettuceBasedProxyManager<String> proxyManager;
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        String ip = getClientIp(request);

        BucketConfiguration config = getBucketConfig(path);

        if (config == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String bucketKey = ip + ":" + path;

        Bucket bucket = proxyManager.builder()
                .build(bucketKey, () -> config);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            long waitSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            log.warn("Rate limit exceeded for IP: {} on path: {}", ip, path);
            ErrorDetails errorDetails = new ErrorDetails(
                    "Too many requests. Please try again in " + waitSeconds + " seconds.",
                    "RATE_LIMIT_EXCEEDED",
                    "uri=" + request.getServletPath(),
                    LocalDateTime.now());

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.addHeader("Retry-After", String.valueOf(waitSeconds));
            response.addHeader("X-Rate-Limit-Remaining", "0");
            response.getWriter().write(
                    objectMapper.writeValueAsString(errorDetails)
            );
        }
    }

    private BucketConfiguration getBucketConfig(String path) {
        if (path.equals("/users/sign-in")) {
            return rateLimitConfig.signInConfig();
        } else if (path.equals("/users/sign-up")) {
            return rateLimitConfig.signUpConfig();
        } else if (path.equals("/users/confirm-account")) {
            return rateLimitConfig.confirmAccountConfig();
        } else if (path.startsWith("/oauth2") || path.startsWith("/login/oauth2")) {
            return rateLimitConfig.oauth2Config();
        } else if (path.startsWith("/articles") ||
                path.startsWith("/profile")) {
            return rateLimitConfig.publicConfig();
        }
        return null;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}