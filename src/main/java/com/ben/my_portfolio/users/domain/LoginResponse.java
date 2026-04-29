package com.ben.my_portfolio.users.domain;

import java.time.Instant;

public record LoginResponse(String token, Instant expiresAt, String email, String role) {}

