package com.ben.my_portfolio.general.security;

import com.ben.my_portfolio.users.security.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimitConfigTest {

    private RateLimitConfig rateLimitConfig;

    @BeforeEach
    void setUp() {
        rateLimitConfig = new RateLimitConfig();
    }

    // helper — builds a real bucket from the config and checks available tokens
    private long availableTokens(BucketConfiguration config) {
        return Bucket.builder()
                .addLimit(config.getBandwidths()[0])
                .build()
                .getAvailableTokens();
    }

    // helper — checks the bandwidth array has exactly one entry and reads its capacity
    private long capacity(BucketConfiguration config) {
        assertThat(config.getBandwidths()).hasSize(1);
        return config.getBandwidths()[0].getCapacity();
    }

    // ─────────────────────────────────────────────────────────────
    // signInConfig
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("signInConfig: capacity 5, bucket starts full")
    void signInConfig_returnsCorrectBucketConfiguration() {
        BucketConfiguration config = rateLimitConfig.signInConfig();

        assertThat(config).isNotNull();
        assertThat(capacity(config)).isEqualTo(5);
        assertThat(availableTokens(config)).isEqualTo(5);
    }

    // ─────────────────────────────────────────────────────────────
    // signUpConfig
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("signUpConfig: capacity 3, bucket starts full")
    void signUpConfig_returnsCorrectBucketConfiguration() {
        BucketConfiguration config = rateLimitConfig.signUpConfig();

        assertThat(config).isNotNull();
        assertThat(capacity(config)).isEqualTo(3);
        assertThat(availableTokens(config)).isEqualTo(3);
    }

    // ─────────────────────────────────────────────────────────────
    // confirmAccountConfig
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("confirmAccountConfig: capacity 5, bucket starts full")
    void confirmAccountConfig_returnsCorrectBucketConfiguration() {
        BucketConfiguration config = rateLimitConfig.confirmAccountConfig();

        assertThat(config).isNotNull();
        assertThat(capacity(config)).isEqualTo(5);
        assertThat(availableTokens(config)).isEqualTo(5);
    }

    // ─────────────────────────────────────────────────────────────
    // oauth2Config
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("oauth2Config: capacity 10, bucket starts full")
    void oauth2Config_returnsCorrectBucketConfiguration() {
        BucketConfiguration config = rateLimitConfig.oauth2Config();

        assertThat(config).isNotNull();
        assertThat(capacity(config)).isEqualTo(10);
        assertThat(availableTokens(config)).isEqualTo(10);
    }

    // ─────────────────────────────────────────────────────────────
    // publicConfig
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("publicConfig: capacity 60, bucket starts full")
    void publicConfig_returnsCorrectBucketConfiguration() {
        BucketConfiguration config = rateLimitConfig.publicConfig();

        assertThat(config).isNotNull();
        assertThat(capacity(config)).isEqualTo(60);
        assertThat(availableTokens(config)).isEqualTo(60);
    }
}