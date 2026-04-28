package com.ben.my_portfolio.user.domain;

import com.ben.my_portfolio.user.security.JwtHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final JwtHelper jwtHelper;

    public UserResponse registerUser(@Valid UserRequest registerRequest) {
        log.info("About to register");

        String email = registerRequest.getEmail();
        log.info("Email {}",email);

        boolean userExisted = userRepo.findByEmail(email.strip()).isPresent();

        if(userExisted){
            throw new IllegalStateException("USERNAME ALREADY TAKEN");
        }

        User mappedUser = modelMapper.map(registerRequest, User.class);
        log.info("Mapped user into entity");

        mappedUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        log.info("Encoded password");

        mappedUser.setCreatedAt(LocalDateTime.now());

        mappedUser.setRole(Role.USER);
        log.info("Role has been set to user:");

        User savedUser = userRepo.save(mappedUser);
        log.info("User has been saved");

        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        log.info("User response: {}",userResponse);

        return userResponse;
    }

    public LoginResponse loginUser(@Valid UserRequest loginRequest) {

        var user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new IllegalStateException("INVALID CREDENTIALS"));
        log.info("Login attempt for user: {}", loginRequest.getEmail());

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new IllegalStateException("ACCOUNT NOT VERIFIED. PLEASE VERIFY YOUR EMAIL");
        }

        try {
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail().strip(), loginRequest.getPassword().strip());
            manager.authenticate(authentication);
            var jwtToken = jwtHelper.generateToken(user);
            return new LoginResponse(jwtToken.token(), jwtToken.expiresAt(), user.getEmail(), user.getRole().name());
        }catch(BadCredentialsException ex){
            throw new IllegalStateException("INVALID CREDENTIALS");
        }
    }

    }

