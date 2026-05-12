package com.ben.my_portfolio.users.domain;

import com.ben.my_portfolio.users.*;
import com.ben.my_portfolio.users.security.JwtHelper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager manager;
    private final JwtHelper jwtHelper;
    private final ApplicationEventPublisher eventPublisher;
    private final ConfirmationTokenHelper tokenHelper;
    private final ConfirmationTokenService tokenService;

    @Transactional
    public UserResponse registerUser(@Valid UserRequest registerRequest) {
        log.info("About to register");

        String email = registerRequest.getEmail();
        log.info("Email {}",email);

        boolean userExisted = userRepo.findByEmail(email.strip()).isPresent();

        if(userExisted){
            throw new EmailAlreadyExistException("EMAIL ALREADY TAKEN");
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

        String token = tokenHelper.saveConfirmationToken(savedUser);
        log.info("Token has been saved");

        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser.getEmail(), token));

        UserResponse userResponse = modelMapper.map(savedUser, UserResponse.class);
        log.info("User response: {}",userResponse);

        return userResponse;
    }

    public LoginResponse loginUser(@Valid UserRequest loginRequest) {

        var user = userRepo.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("EMAIL NOT FOUND"));
        log.info("Login attempt for user: {}", loginRequest.getEmail());

        if (!Boolean.TRUE.equals(user.getIsVerified())) {
            throw new AccountNotVerifiedException("ACCOUNT NOT VERIFIED. PLEASE VERIFY YOUR EMAIL");
        }

        try {
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail().strip(), loginRequest.getPassword().strip());
            manager.authenticate(authentication);
            var jwtToken = jwtHelper.generateToken(user);
            return new LoginResponse(jwtToken.token(), jwtToken.expiresAt(), user.getEmail(), user.getRole().name());
        }catch(BadCredentialsException ex){
            throw new InvalidCredentialsException("INVALID CREDENTIALS");
        }
    }

    @Transactional
    public UserResponse getUserById(Long id) {
        log.info("Getting user with id: {}",id);
        User user = userRepo.findById(id).
                orElseThrow(() -> new UserNotFoundException("USER NOT FOUND"));

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        log.info("Mapped user to userResponse: {}",userResponse);
        return userResponse;
    }

    public String getUserEmail(Long id) {
        log.info("About getting email of user with id: {}",id);
        String email = userRepo.findEmailById(id).
                orElseThrow(() -> new UserNotFoundException("USER NOT FOUND"));
        log.info("Gotten email: {}",email);
        return email;
    }


    @Transactional
    public String confirmAccount(String token) {
        log.info("About to confirm token:");
        ConfirmationToken confirmToken = tokenService.getToken(token)
                .orElseThrow(()-> new TokenNotFoundException("TOKEN NOT FOUND"));
        log.info("Confirmation Token from the db");

        if (confirmToken.getConfirmedAt() !=null){
            log.error("Token already confirmed");
            throw new TokenAlreadyConfirmedException("TOKEN ALREADY CONFIRMED");
        }

        if(confirmToken.getExpires().isBefore(LocalDateTime.now())){
            log.error("Token expired");
            throw new TokenExpiredException("TOKEN EXPIRED");
        }

        tokenService.setConfirmationDetails(token);

        String email = confirmToken.getUser().getEmail();
        verifyUser(email);
        return "ACCOUNT WAS SUCCESSFULLY VERIFIED";
    }

    public void verifyUser(String email){
         userRepo.verifyUser(email);
    }

    public List<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Call made to get all users");

        return userRepo.findAll(pageable)
                .map(user -> modelMapper.map(user, UserResponse.class)).getContent();
    }

    @Transactional
    public String resendVerificationToken(String email) {
        log.info("About to make cal to resend token");
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("EMAIL NOT FOUND"));

        if(user.getIsVerified()){
            log.info("User is already verified");
            throw new AccountAlreadyVerifiedException("ACCOUNT ALREADY VERIFIED");
        }

       String token = tokenHelper.saveConfirmationToken(user);
        log.info("ConfirmationToken has been saved in the db");

        eventPublisher.publishEvent(new UserRegisteredEvent(user.getEmail(), token));
        log.info("Event fired to resend verification token email");

        return "Email Sent";

    }

    @Transactional
    public String sendContactEmail(@Valid ContactMeRequest request) {
        log.info("Publishing contact me event for: {}", request.getEmail());

        eventPublisher.publishEvent(
                new ContactMeRequestEvent(request.getEmail(), request.getPhoneNumber(), request.getReasonForContact(), request.getMessage()));

        log.info("Contact me event published successfully");
        return "CONTACT REQUEST SENT SUCCESSFULLY";

    }
}

