package com.ben.my_portfolio.user.domain;

public class AccountNotVerifiedException extends RuntimeException{

    public AccountNotVerifiedException(String message) {
        super(message);
    }
}
