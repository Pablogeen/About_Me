package com.ben.my_portfolio.users.web;

import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.domain.LoginResponse;
import com.ben.my_portfolio.users.domain.UserRequest;
import com.ben.my_portfolio.users.UserResponse;
import com.ben.my_portfolio.users.domain.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/confirm-account")
    public ResponseEntity<String>confirmAccount(@RequestParam String token){
        log.info("Call made to confirmAccount:");
       String confirmedAccount = userService.confirmAccount(token);
       log.info("Account has been verified");
        return new ResponseEntity<>(confirmedAccount, HttpStatus.OK);
    }

    @GetMapping("/get-all-users")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponse>>getUsers(
                                                   @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size){
            log.info("Request made to get Users - page: {}, size: {}", page, size);
            Pageable pageable = PageRequest.of(page, size);
            List<UserResponse> requestedUsers = userService.getAllUsers(pageable);
            log.info("Requested users: requestedUsers : {}",requestedUsers);
            return new ResponseEntity<>(requestedUsers, HttpStatus.OK);

    }


}
