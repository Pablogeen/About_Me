package com.ben.my_portfolio.users.domain;

public class TokenAlreadyConfirmedException extends  RuntimeException{
    public TokenAlreadyConfirmedException(String message) {
        super(message);
    }
}
