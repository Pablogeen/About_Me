package com.ben.my_portfolio.users.domain;

public class AccountAlreadyVerifiedException extends RuntimeException{

    public AccountAlreadyVerifiedException(String message) {
        super(message);
    }
}
