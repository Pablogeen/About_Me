package com.ben.my_portfolio.general;


import com.ben.my_portfolio.users.UserResponse;
import com.ben.my_portfolio.users.domain.*;
import com.ben.my_portfolio.users.web.UserController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired private MockMvc       mockMvc;
    @Autowired private ObjectMapper  objectMapper;

    @MockitoBean private UserService userService;

    private static final String BASE = "/v1/users";
    private static final String EMAIL = "ben@benandco.dev";

    // ─────────────────────────────────────────────────────────────
    // POST /sign-up
    // ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /sign-up: valid request → 200 with UserResponse")
    void registerUser_validRequest_returns200() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail(EMAIL);
        request.setPassword("Password123!");
        request.setConfirmPassword("Password123!");

        UserResponse response = new UserResponse();
        response.setEmail(EMAIL);

        when(userService.registerUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE + "/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(EMAIL));

        verify(userService).registerUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /sign-up: invalid request body → 400")
    void registerUser_invalidRequest_returns400() throws Exception {
        // empty body — validation should reject it
        mockMvc.perform(post(BASE + "/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /sign-in
    // ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /sign-in: valid credentials → 200 with LoginResponse")
    void loginUser_validRequest_returns200() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(EMAIL);
        request.setPassword("Password123!");

        LoginResponse response = new LoginResponse(
                "jwt.token.value",
                Instant.now().plusSeconds(3600),
                EMAIL,
                "USER");

        when(userService.loginUser(any(UserLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE + "/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.value"))
                .andExpect(jsonPath("$.email").value(EMAIL))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService).loginUser(any(UserLoginRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /sign-in: invalid request body → 400")
    void loginUser_invalidRequest_returns400() throws Exception {
        mockMvc.perform(post(BASE + "/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    // ─────────────────────────────────────────────────────────────
    // GET /confirm-account
    // ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /confirm-account: valid token → 200 with success message")
    void confirmAccount_validToken_returns200() throws Exception {
        when(userService.confirmAccount("123456"))
                .thenReturn("ACCOUNT WAS SUCCESSFULLY VERIFIED");

        mockMvc.perform(get(BASE + "/confirm-account")
                        .param("token", "123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("ACCOUNT WAS SUCCESSFULLY VERIFIED"));

        verify(userService).confirmAccount("123456");
    }

    @Test
    @WithMockUser
    @DisplayName("GET /confirm-account: blank token → 400")
    void confirmAccount_blankToken_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/confirm-account")
                        .param("token", ""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /confirm-account: token with invalid characters → 400")
    void confirmAccount_invalidTokenFormat_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/confirm-account")
                        .param("token", "invalid token!@#"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    // ─────────────────────────────────────────────────────────────
    // GET /get-all-users
    // ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("GET /get-all-users: ADMIN role → 200 with user list")
    void getUsers_adminRole_returns200() throws Exception {
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(EMAIL);

        when(userService.getAllUsers(any())).thenReturn(List.of(userResponse));

        mockMvc.perform(get(BASE + "/get-all-users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(EMAIL));

        verify(userService).getAllUsers(any());
    }

    @Test
    @WithMockUser(authorities = "USER")
    @DisplayName("GET /get-all-users: USER role → 403 Forbidden")
    void getUsers_userRole_returns403() throws Exception {
        mockMvc.perform(get(BASE + "/get-all-users"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    @DisplayName("GET /get-all-users: uses default pagination when params omitted")
    void getUsers_defaultPagination_returns200() throws Exception {
        when(userService.getAllUsers(any())).thenReturn(List.of());

        mockMvc.perform(get(BASE + "/get-all-users"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers(any());
    }

    // ─────────────────────────────────────────────────────────────
    // GET /resend-verification
    // ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("GET /resend-verification: valid email → 200 with success message")
    void resendVerificationToken_validEmail_returns200() throws Exception {
        when(userService.resendVerificationToken(EMAIL)).thenReturn("Email Sent");

        mockMvc.perform(get(BASE + "/resend-verification")
                        .param("email", EMAIL))
                .andExpect(status().isOk())
                .andExpect(content().string("Email Sent"));

        verify(userService).resendVerificationToken(EMAIL);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /resend-verification: blank email → 400")
    void resendVerificationToken_blankEmail_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/resend-verification")
                        .param("email", ""))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser
    @DisplayName("GET /resend-verification: invalid email format → 400")
    void resendVerificationToken_invalidEmailFormat_returns400() throws Exception {
        mockMvc.perform(get(BASE + "/resend-verification")
                        .param("email", "not-an-email"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    // ─────────────────────────────────────────────────────────────
    // POST /contact
    // ─────────────────────────────────────────────────────────────

    @Test
    @WithMockUser
    @DisplayName("POST /contact: valid request → 200 with success message")
    void contactAdmin_validRequest_returns200() throws Exception {
        ContactMeRequest request = new ContactMeRequest();
        request.setEmail(EMAIL);
        request.setPhoneNumber("+233501234567");
        request.setReasonForContact("Collaboration");
        request.setMessage("Hi Ben, let's work together.");

        when(userService.sendContactEmail(any(ContactMeRequest.class)))
                .thenReturn("CONTACT REQUEST SENT SUCCESSFULLY");

        mockMvc.perform(post(BASE + "/contact")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("CONTACT REQUEST SENT SUCCESSFULLY"));

        verify(userService).sendContactEmail(any(ContactMeRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /contact: invalid request body → 400")
    void contactAdmin_invalidRequest_returns400() throws Exception {
        mockMvc.perform(post(BASE + "/contact")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }
}