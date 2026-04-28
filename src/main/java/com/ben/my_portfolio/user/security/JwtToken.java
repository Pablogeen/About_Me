package com.ben.my_portfolio.user.security;

import java.time.Instant;

public record JwtToken(String token, Instant expiresAt) {}
