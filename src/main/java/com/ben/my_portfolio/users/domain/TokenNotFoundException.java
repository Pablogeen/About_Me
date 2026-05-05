package com.ben.my_portfolio.users.domain;

public class TokenNotFoundException extends  RuntimeException{
    public TokenNotFoundException(String message) {
        super(message);
    }
}
