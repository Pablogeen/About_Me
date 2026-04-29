package com.ben.my_portfolio.user;

import com.ben.my_portfolio.user.domain.Role;
import lombok.Data;

@Data
public class UserResponse{
    private Long id;
    private String email;
    private boolean isVerified;
    private Role role;
}


