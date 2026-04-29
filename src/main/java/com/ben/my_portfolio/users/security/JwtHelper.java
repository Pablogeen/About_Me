package com.ben.my_portfolio.users.security;

import com.ben.my_portfolio.users.domain.Constants;
import com.ben.my_portfolio.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtHelper {


    private final JwtEncoder encoder;
    private final JwtDecoder decoder;


    public JwtToken generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(Constants.ACCESS_TOKEN_EXPIRY);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(Constants.JWT_ISSUER)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("user_id", user.getId())
                .claim("role", user.getRole().name())
                .claim("isVerified", user.getIsVerified())
                .build();
        var token = this.encoder.encode(
                JwtEncoderParameters.from(
                        JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new JwtToken(token, expiresAt);
    }

    // Add these validation methods
    public String extractUsername(String token) {
        try {
            var jwt = decoder.decode(token);
            return jwt.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            var jwt = decoder.decode(token);
            String username = jwt.getSubject();
            Instant expiresAt = jwt.getExpiresAt();
            return username.equals(userDetails.getUsername()) &&
                    expiresAt != null &&
                    expiresAt.isAfter(Instant.now());
        } catch (JwtException e) {
            return false;
        }
    }

}
