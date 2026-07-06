package com.ben.my_portfolio.general.security;

import com.ben.my_portfolio.users.security.JwtConfig;
import com.ben.my_portfolio.users.security.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtConfigTest {

    @Mock
    private JwtProperties jwtProperties;

    @InjectMocks
    private JwtConfig jwtConfig;


    private static final String VALID_SECRET = "my-super-secret-key-that-is-long-enough-for-hs256";
    private static final String ALGORITHM    = "HmacSHA256";

    @BeforeEach
    void setUp() {
        when(jwtProperties.getSecret()).thenReturn(VALID_SECRET);
        when(jwtProperties.getAlgorithm()).thenReturn(ALGORITHM);
    }

    // ─────────────────────────────────────────────────────────────
    // jwtEncoder()
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("jwtEncoder: returns a NimbusJwtEncoder bean")
    void jwtEncoder_returnsNimbusJwtEncoder() {
        JwtEncoder encoder = jwtConfig.jwtEncoder();

        assertThat(encoder)
                .isNotNull()
                .isInstanceOf(NimbusJwtEncoder.class);
    }

    @Test
    @DisplayName("jwtEncoder: throws when secret is empty")
    void jwtEncoder_emptySecret_throws() {
        when(jwtProperties.getSecret()).thenReturn("");

        assertThatThrownBy(() -> jwtConfig.jwtEncoder())
                .isInstanceOf(Exception.class);
    }

    // ─────────────────────────────────────────────────────────────
    // jwtDecoder()
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("jwtDecoder: returns a NimbusJwtDecoder bean")
    void jwtDecoder_returnsNimbusJwtDecoder() {
        JwtDecoder decoder = jwtConfig.jwtDecoder();

        assertThat(decoder)
                .isNotNull()
                .isInstanceOf(NimbusJwtDecoder.class);
    }

    @Test
    @DisplayName("jwtDecoder: throws when secret is empty")
    void jwtDecoder_emptySecret_throws() {
        when(jwtProperties.getSecret()).thenReturn("");

        assertThatThrownBy(() -> jwtConfig.jwtDecoder())
                .isInstanceOf(Exception.class);
    }
}
