package com.ben.my_portfolio.general.domain;

import com.ben.my_portfolio.users.*;
import com.ben.my_portfolio.users.domain.*;
import com.ben.my_portfolio.users.security.JwtHelper;
import com.ben.my_portfolio.users.security.JwtToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private ModelMapper              modelMapper;
    @Mock private UserRepository userRepo;
    @Mock private BCryptPasswordEncoder    passwordEncoder;
    @Mock private AuthenticationManager    manager;
    @Mock private JwtHelper                jwtHelper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private ConfirmationTokenHelper tokenHelper;
    @Mock private ConfirmationTokenService tokenService;

    @InjectMocks
    private UserService userService;

    private static final String EMAIL    = "ben@benandco.dev";
    private static final String PASSWORD = "Password123!";
    private static final Long   USER_ID  = 1L;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setRole(Role.USER);
        user.setIsVerified(true);
        user.setCreatedAt(LocalDateTime.now());
    }

    // ─────────────────────────────────────────────────────────────
    // registerUser
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registerUser: throws EmailAlreadyExistException when email is taken")
    void registerUser_emailAlreadyExists_throwsEmailAlreadyExistException() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);
        request.setConfirmPassword(PASSWORD);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessageContaining("EMAIL ALREADY TAKEN");

        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("registerUser: throws PasswordMismatchedException when passwords don't match")
    void registerUser_passwordMismatch_throwsPasswordMismatchedException() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);
        request.setConfirmPassword("DifferentPassword!");

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(PasswordMismatchedException.class)
                .hasMessageContaining("PASSWORD MISMATCHED");

        verify(userRepo, never()).save(any());
    }

    @Test
    @DisplayName("registerUser: saves user, generates token, publishes event and returns response")
    void registerUser_validRequest_savesUserAndReturnsResponse() {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);
        request.setConfirmPassword(PASSWORD);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setEmail(EMAIL);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(passwordEncoder.encode(PASSWORD)).thenReturn("encodedPassword");
        when(userRepo.save(user)).thenReturn(user);
        when(tokenHelper.saveConfirmationToken(user)).thenReturn("token123");
        when(modelMapper.map(user, UserResponse.class)).thenReturn(expectedResponse);

        UserResponse result = userService.registerUser(request);

        assertThat(result.getEmail()).isEqualTo(EMAIL);

        verify(userRepo).save(user);
        verify(tokenHelper).saveConfirmationToken(user);

        ArgumentCaptor<UserRegisteredEvent> eventCaptor =
                ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().email()).isEqualTo(EMAIL);
        assertThat(eventCaptor.getValue().token()).isEqualTo("token123");
    }

    // ─────────────────────────────────────────────────────────────
    // loginUser
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("loginUser: throws UserNotFoundException when email not found")
    void loginUser_emailNotFound_throwsUserNotFoundException() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loginUser(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("EMAIL NOT FOUND");
    }

    @Test
    @DisplayName("loginUser: throws AccountNotVerifiedException when account not verified")
    void loginUser_accountNotVerified_throwsAccountNotVerifiedException() {
        user.setIsVerified(false);
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.loginUser(request))
                .isInstanceOf(AccountNotVerifiedException.class)
                .hasMessageContaining("ACCOUNT NOT VERIFIED");
    }

    @Test
    @DisplayName("loginUser: throws InvalidCredentialsException when password is wrong")
    void loginUser_badCredentials_throwsInvalidCredentialsException() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(EMAIL);
        request.setPassword("wrongPassword");

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        doThrow(new BadCredentialsException("bad credentials"))
                .when(manager).authenticate(any());

        assertThatThrownBy(() -> userService.loginUser(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("INVALID CREDENTIALS");
    }

    @Test
    @DisplayName("loginUser: returns LoginResponse on valid credentials")
    void loginUser_validCredentials_returnsLoginResponse() {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(EMAIL);
        request.setPassword(PASSWORD);

        JwtToken jwtToken = new JwtToken("jwt.token.value", Instant.now().plusSeconds(3600));

        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(manager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD));
        when(jwtHelper.generateToken(user)).thenReturn(jwtToken);

        LoginResponse result = userService.loginUser(request);

        assertThat(result.token()).isEqualTo("jwt.token.value");
        assertThat(result.email()).isEqualTo(EMAIL);
        assertThat(result.role()).isEqualTo("USER");
    }

    // ─────────────────────────────────────────────────────────────
    // getUserById
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserById: throws UserNotFoundException when user not found")
    void getUserById_notFound_throwsUserNotFoundException() {
        when(userRepo.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("USER NOT FOUND");
    }

    @Test
    @DisplayName("getUserById: returns UserResponse when user found")
    void getUserById_found_returnsUserResponse() {
        UserResponse expected = new UserResponse();
        expected.setEmail(EMAIL);

        when(userRepo.findById(USER_ID)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(expected);

        UserResponse result = userService.getUserById(USER_ID);

        assertThat(result.getEmail()).isEqualTo(EMAIL);
        verify(userRepo).findById(USER_ID);
    }

    // ─────────────────────────────────────────────────────────────
    // getUserEmail
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getUserEmail: throws UserNotFoundException when user not found")
    void getUserEmail_notFound_throwsUserNotFoundException() {
        when(userRepo.findEmailById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserEmail(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("USER NOT FOUND");
    }

    @Test
    @DisplayName("getUserEmail: returns email when user found")
    void getUserEmail_found_returnsEmail() {
        when(userRepo.findEmailById(USER_ID)).thenReturn(Optional.of(EMAIL));

        String result = userService.getUserEmail(USER_ID);

        assertThat(result).isEqualTo(EMAIL);
    }

    // ─────────────────────────────────────────────────────────────
    // confirmAccount
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("confirmAccount: throws TokenNotFoundException when token not found")
    void confirmAccount_tokenNotFound_throwsTokenNotFoundException() {
        when(tokenService.getToken("badtoken")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.confirmAccount("badtoken"))
                .isInstanceOf(TokenNotFoundException.class)
                .hasMessageContaining("TOKEN NOT FOUND");
    }

    @Test
    @DisplayName("confirmAccount: throws TokenAlreadyConfirmedException when already confirmed")
    void confirmAccount_alreadyConfirmed_throwsTokenAlreadyConfirmedException() {
        ConfirmationToken token = new ConfirmationToken();
        token.setConfirmedAt(LocalDateTime.now().minusDays(1));
        token.setUser(user);

        when(tokenService.getToken("alreadyConfirmed")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> userService.confirmAccount("alreadyConfirmed"))
                .isInstanceOf(TokenAlreadyConfirmedException.class)
                .hasMessageContaining("TOKEN ALREADY CONFIRMED");
    }

    @Test
    @DisplayName("confirmAccount: throws TokenExpiredException when token is expired")
    void confirmAccount_tokenExpired_throwsTokenExpiredException() {
        ConfirmationToken token = new ConfirmationToken();
        token.setConfirmedAt(null);
        token.setExpires(LocalDateTime.now().minusMinutes(10));
        token.setUser(user);

        when(tokenService.getToken("expiredToken")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> userService.confirmAccount("expiredToken"))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessageContaining("TOKEN EXPIRED");
    }

    @Test
    @DisplayName("confirmAccount: confirms account and returns success message")
    void confirmAccount_validToken_confirmsAccountAndReturnsMessage() {
        ConfirmationToken token = new ConfirmationToken();
        token.setConfirmedAt(null);
        token.setExpires(LocalDateTime.now().plusMinutes(10));
        token.setUser(user);

        when(tokenService.getToken("validToken")).thenReturn(Optional.of(token));

        String result = userService.confirmAccount("validToken");

        assertThat(result).isEqualTo("ACCOUNT WAS SUCCESSFULLY VERIFIED");
        verify(tokenService).setConfirmationDetails("validToken");
        verify(userRepo).verifyUser(EMAIL);
    }

    // ─────────────────────────────────────────────────────────────
    // getAllUsers
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsers: returns mapped list of UserResponse")
    void getAllUsers_returnsUserResponseList() {
        Pageable pageable = PageRequest.of(0, 10);
        UserResponse response = new UserResponse();
        response.setEmail(EMAIL);

        when(userRepo.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(user)));
        when(modelMapper.map(user, UserResponse.class)).thenReturn(response);

        List<UserResponse> result = userService.getAllUsers(pageable);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo(EMAIL);
    }

    // ─────────────────────────────────────────────────────────────
    // resendVerificationToken
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("resendVerificationToken: throws UserNotFoundException when email not found")
    void resendVerificationToken_emailNotFound_throwsUserNotFoundException() {
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.resendVerificationToken(EMAIL))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("EMAIL NOT FOUND");
    }

    @Test
    @DisplayName("resendVerificationToken: throws AccountAlreadyVerifiedException when already verified")
    void resendVerificationToken_alreadyVerified_throwsAccountAlreadyVerifiedException() {
        user.setIsVerified(true);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.resendVerificationToken(EMAIL))
                .isInstanceOf(AccountAlreadyVerifiedException.class)
                .hasMessageContaining("ACCOUNT ALREADY VERIFIED");
    }

    @Test
    @DisplayName("resendVerificationToken: saves token, publishes event and returns success message")
    void resendVerificationToken_validUnverifiedUser_sendsTokenAndReturnsMessage() {
        user.setIsVerified(false);
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(tokenHelper.saveConfirmationToken(user)).thenReturn("newToken");

        String result = userService.resendVerificationToken(EMAIL);

        assertThat(result).isEqualTo("Email Sent");
        verify(tokenHelper).saveConfirmationToken(user);

        ArgumentCaptor<UserRegisteredEvent> captor =
                ArgumentCaptor.forClass(UserRegisteredEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo(EMAIL);
        assertThat(captor.getValue().token()).isEqualTo("newToken");
    }

    // ─────────────────────────────────────────────────────────────
    // sendContactEmail
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("sendContactEmail: publishes ContactMeRequestEvent and returns success message")
    void sendContactEmail_validRequest_publishesEventAndReturnsMessage() {
        ContactMeRequest request = new ContactMeRequest();
        request.setEmail(EMAIL);
        request.setPhoneNumber("+233501234567");
        request.setReasonForContact("Collaboration");
        request.setMessage("Hi Ben, let's work together.");

        String result = userService.sendContactEmail(request);

        assertThat(result).isEqualTo("CONTACT REQUEST SENT SUCCESSFULLY");

        ArgumentCaptor<ContactMeRequestEvent> captor =
                ArgumentCaptor.forClass(ContactMeRequestEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());

        ContactMeRequestEvent fired = captor.getValue();
        assertThat(fired.email()).isEqualTo(EMAIL);
        assertThat(fired.phoneNumber()).isEqualTo("+233501234567");
        assertThat(fired.reasonForContact()).isEqualTo("Collaboration");
        assertThat(fired.message()).isEqualTo("Hi Ben, let's work together.");
    }
}
