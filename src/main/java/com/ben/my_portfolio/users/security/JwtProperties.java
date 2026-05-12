package com.ben.my_portfolio.users.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    private String secret;
    private String algorithm;
    private String issuer;
    private long accessTokenExpiry;
}
