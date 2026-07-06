package com.ben.my_portfolio.general.security;

import com.ben.my_portfolio.users.Role;
import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.domain.UserRepository;
import com.ben.my_portfolio.users.security.GoggleAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoggleAuthService googleAuthService;

    private static final String TEST_EMAIL = "ben@example.com";

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setEmail(TEST_EMAIL);
        existingUser.setRole(Role.USER);
        existingUser.setIsVerified(true);
    }

    // ─────────────────────────────────────────────────────────────
    // findOrCreateGoogleUser — user already exists
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findOrCreateGoogleUser: returns existing user without saving")
    void findOrCreateGoogleUser_userExists_returnsExistingUser() {
        // arrange
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(existingUser));

        // act
        User result = googleAuthService.findOrCreateGoogleUser(TEST_EMAIL);

        // assert
        assertThat(result).isEqualTo(existingUser);
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);

        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(userRepository, never()).save(any(User.class));
    }

    // ─────────────────────────────────────────────────────────────
    // findOrCreateGoogleUser — user does NOT exist (creation path)
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findOrCreateGoogleUser: creates and saves new user when not found")
    void findOrCreateGoogleUser_userNotFound_createsAndSavesNewUser() {
        // arrange
        User savedUser = new User();
        savedUser.setEmail(TEST_EMAIL);
        savedUser.setRole(Role.USER);
        savedUser.setIsVerified(true);
        savedUser.setPassword(null);

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // act
        User result = googleAuthService.findOrCreateGoogleUser(TEST_EMAIL);

        // assert — returned value
        assertThat(result).isEqualTo(savedUser);

        // assert — exactly what was passed to save()
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User captured = captor.getValue();
        assertThat(captured.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(captured.getRole()).isEqualTo(Role.USER);
        assertThat(captured.getIsVerified()).isTrue();
        assertThat(captured.getPassword()).isNull();
        assertThat(captured.getCreatedAt())
                .isNotNull()
                .isBeforeOrEqualTo(LocalDateTime.now());

        verify(userRepository).findByEmail(TEST_EMAIL);
    }
}
