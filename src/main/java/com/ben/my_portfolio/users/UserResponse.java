package com.ben.my_portfolio.users;

import lombok.Data;

@Data
public class UserResponse{
    private Long id;
    private String email;
    private boolean isVerified;
    private Role role;
}


