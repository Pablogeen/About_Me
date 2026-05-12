package com.ben.my_portfolio.users.domain;

public class PasswordMismatchedException extends RuntimeException{

    public PasswordMismatchedException(String message) {
        super(message);
    }
}
