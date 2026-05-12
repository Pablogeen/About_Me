package com.ben.my_portfolio.users.domain;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class AccountNotVerifiedException extends RuntimeException{

    public AccountNotVerifiedException(String message) {
        super(message);
    }

}
