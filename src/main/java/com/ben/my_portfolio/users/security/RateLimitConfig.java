package com.ben.my_portfolio.users.security;

import io.github.bucket4j.BucketConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

    public BucketConfiguration signInConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(1)))
                .build();
    }

    public BucketConfiguration signUpConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(3)
                        .refillIntervally(3, Duration.ofMinutes(1)))
                .build();
    }

    public BucketConfiguration confirmAccountConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(5)))
                .build();
    }

    public BucketConfiguration oauth2Config() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(10)
                        .refillIntervally(10, Duration.ofMinutes(1)))
                .build();
    }

    public BucketConfiguration publicConfig() {
        return BucketConfiguration.builder()
                .addLimit(limit -> limit
                        .capacity(60)
                        .refillGreedy(60, Duration.ofMinutes(1)))
                .build();
    }
}
