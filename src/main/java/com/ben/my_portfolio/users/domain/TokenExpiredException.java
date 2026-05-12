package com.ben.my_portfolio.users.domain;

public class TokenExpiredException extends  RuntimeException{
    public TokenExpiredException(String message) {
        super(message);
    }
}
