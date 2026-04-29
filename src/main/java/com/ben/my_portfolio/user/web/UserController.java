package com.ben.my_portfolio.user.web;

import com.ben.my_portfolio.user.domain.LoginResponse;
import com.ben.my_portfolio.user.domain.UserRequest;
import com.ben.my_portfolio.user.UserResponse;
import com.ben.my_portfolio.user.domain.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/users")
@RestController
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRequest registerRequest){
        log.info("Call made to register a user: {}",registerRequest.getEmail());
        UserResponse registeredUser = userService.registerUser(registerRequest);
        log.info("User has been registered successfully: {}",registeredUser);
        return new ResponseEntity<>(registeredUser, HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid UserRequest loginRequest){
        log.info("Call made to login in a user: {}",loginRequest.getEmail());
        LoginResponse loggedInUser = userService.loginUser(loginRequest);
        log.info("User logged in successfully: ");
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);

    }


}
