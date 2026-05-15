package com.ben.my_portfolio.users.web;

import com.ben.my_portfolio.users.UserResponse;
import com.ben.my_portfolio.users.domain.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Articles", description = "Endpoints for managing articles")
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
    public ResponseEntity<LoginResponse> loginUser(@RequestBody @Valid UserLoginRequest loginRequest){
        log.info("Call made to login in a user: {}",loginRequest.getEmail());
        LoginResponse loggedInUser = userService.loginUser(loginRequest);
        log.info("User logged in successfully: ");
        return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
    }

    @GetMapping("/confirm-account")
    public ResponseEntity<String>confirmAccount(@RequestParam  @NotBlank(message = "Token cannot be blank")
                                                    @Size(max = 256, message = "Invalid token")
                                                    @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "Invalid token format")String token){
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

    @GetMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationToken(@RequestParam  @NotBlank(message = "Email cannot be blank")
                                                              @Email(message = "Invalid email format")
                                                              @Size(max = 254, message = "Email too long")String email){
        log.info("Request made to resendVerificationToken: {}",email);
      String response =  userService.resendVerificationToken(email);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    @PostMapping("/contact")
    public ResponseEntity<String> contactAdmin(@Valid @RequestBody ContactMeRequest request) {
        log.info("About to contact the admin");
        String result =userService.sendContactEmail(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



}
