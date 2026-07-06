package com.ben.my_portfolio.general.security;


import com.ben.my_portfolio.users.Role;
import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.domain.LoginResponse;
import com.ben.my_portfolio.users.security.GoggleAuthService;
import com.ben.my_portfolio.users.security.JwtHelper;
import com.ben.my_portfolio.users.security.JwtToken;
import com.ben.my_portfolio.users.security.Oauth2SuccessHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class Oauth2SuccessHandlerTest {


    @Mock private JwtHelper jwtHelper;
    @Mock private ObjectMapper       objectMapper;
    @Mock private GoggleAuthService authService;
    @Mock private HttpServletRequest  request;
    @Mock private HttpServletResponse response;
    @Mock private Authentication      authentication;
    @Mock private OAuth2User          oauth2User;

    @InjectMocks
    private Oauth2SuccessHandler handler;

    private static final String EMAIL      = "ben@benandco.dev";
    private static final String RAW_TOKEN  = "header.payload.sig";

    private User         user;
    private JwtToken jwtToken;
    private StringWriter responseBody;

    @BeforeEach
    void setUp() throws IOException {
        user = new User();
        user.setEmail(EMAIL);
        user.setRole(Role.USER);

        Instant expiresAt = Instant.now().plusSeconds(3600);
        jwtToken = new JwtToken(RAW_TOKEN, expiresAt);

        // capture whatever is written to the response writer
        responseBody = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseBody));
    }

    // ─────────────────────────────────────────────────────────────
    // Happy path — full interaction chain
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("onAuthenticationSuccess: sets content type, status, and writes JWT response")
    void onAuthenticationSuccess_validOAuth2User_writesJwtResponse() throws IOException {
        // arrange
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(EMAIL);
        when(authService.findOrCreateGoogleUser(EMAIL)).thenReturn(user);
        when(jwtHelper.generateToken(user)).thenReturn(jwtToken);

        String expectedJson = "{\"token\":\"" + RAW_TOKEN + "\"}";
        when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

        // act
        handler.onAuthenticationSuccess(request, response, authentication);

        // assert — response metadata
        verify(response).setContentType("application/json");
        verify(response).setStatus(HttpServletResponse.SC_OK);

        // assert — correct LoginResponse was serialised
        ArgumentCaptor<LoginResponse> captor = ArgumentCaptor.forClass(LoginResponse.class);
        verify(objectMapper).writeValueAsString(captor.capture());

        LoginResponse captured = captor.getValue();
        assertThat(captured.token()).isEqualTo(RAW_TOKEN);
        assertThat(captured.expiresAt()).isEqualTo(jwtToken.expiresAt());
        assertThat(captured.email()).isEqualTo(EMAIL);
        assertThat(captured.role()).isEqualTo("USER");

        // assert — JSON was written to the response body
        assertThat(responseBody.toString()).isEqualTo(expectedJson);
    }

    // ─────────────────────────────────────────────────────────────
    // Interaction order — each service is called exactly once
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("onAuthenticationSuccess: calls findOrCreateGoogleUser then generateToken in order")
    void onAuthenticationSuccess_callsServicesInOrder() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(EMAIL);
        when(authService.findOrCreateGoogleUser(EMAIL)).thenReturn(user);
        when(jwtHelper.generateToken(user)).thenReturn(jwtToken);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        handler.onAuthenticationSuccess(request, response, authentication);

        var inOrder = inOrder(authService, jwtHelper);
        inOrder.verify(authService).findOrCreateGoogleUser(EMAIL);
        inOrder.verify(jwtHelper).generateToken(user);
    }

    // ─────────────────────────────────────────────────────────────
    // IOException propagation — objectMapper failure bubbles up
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("onAuthenticationSuccess: propagates IOException from objectMapper")
    void onAuthenticationSuccess_objectMapperThrows_propagatesIOException() throws IOException {
        when(authentication.getPrincipal()).thenReturn(oauth2User);
        when(oauth2User.getAttribute("email")).thenReturn(EMAIL);
        when(authService.findOrCreateGoogleUser(EMAIL)).thenReturn(user);
        when(jwtHelper.generateToken(user)).thenReturn(jwtToken);
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("serialisation failure") {});

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        handler.onAuthenticationSuccess(request, response, authentication))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("serialisation failure");
    }
}
