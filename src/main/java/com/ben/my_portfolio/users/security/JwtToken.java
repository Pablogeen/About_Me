package com.ben.my_portfolio.users.security;

import java.time.Instant;

public record JwtToken(String token, Instant expiresAt) {}
