package com.ben.my_portfolio;

public class RateLimitException extends RuntimeException{
    public RateLimitException(String message) {
        super(message);
    }
}
