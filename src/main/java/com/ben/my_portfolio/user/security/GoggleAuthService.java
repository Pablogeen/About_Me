package com.ben.my_portfolio.user.security;

import com.ben.my_portfolio.user.domain.Role;
import com.ben.my_portfolio.user.domain.User;
import com.ben.my_portfolio.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class GoggleAuthService {

    private final UserRepository userRepository;
    private final JwtHelper jwtHelper;

    public User findOrCreateGoogleUser(String email) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setRole(Role.USER);
                    newUser.setIsVerified(true);
                    newUser.setPassword(null);
                    newUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });
    }
}