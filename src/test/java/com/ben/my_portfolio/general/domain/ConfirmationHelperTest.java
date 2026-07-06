package com.ben.my_portfolio.general.domain;

import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.domain.ConfirmationToken;
import com.ben.my_portfolio.users.domain.ConfirmationTokenHelper;
import com.ben.my_portfolio.users.domain.ConfirmationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmationTokenHelperTest {

    @Mock
    private ConfirmationTokenService tokenService;

    @InjectMocks
    private ConfirmationTokenHelper tokenHelper;

    private User buildUser() {
        User user = new User();
        user.setEmail("ben@benandco.dev");
        return user;
    }

    // ─────────────────────────────────────────────────────────────
    // token format
    // ─────────────────────────────────────────────────────────────

    @RepeatedTest(10)
    @DisplayName("saveConfirmationToken: returns exactly 6-digit zero-padded numeric token")
    void saveConfirmationToken_returnsValidSixDigitToken() {
        String token = tokenHelper.saveConfirmationToken(buildUser());

        assertThat(token)
                .hasSize(6)
                .matches("\\d{6}");
    }

    // ─────────────────────────────────────────────────────────────
    // persistence — correct ConfirmationToken passed to service
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveConfirmationToken: saves token with correct user, value, and expiry window")
    void saveConfirmationToken_savesCorrectConfirmationToken() {
        User user = buildUser();
        LocalDateTime before = LocalDateTime.now();

        String returnedToken = tokenHelper.saveConfirmationToken(user);

        LocalDateTime after = LocalDateTime.now();

        ArgumentCaptor<ConfirmationToken> captor =
                ArgumentCaptor.forClass(ConfirmationToken.class);
        verify(tokenService).saveConfirmationToken(captor.capture());

        ConfirmationToken saved = captor.getValue();

        // token value matches what was returned
        assertThat(saved.getToken()).isEqualTo(returnedToken);

        // user is wired correctly
        assertThat(saved.getUser()).isEqualTo(user);

        // createdAt is within the test window
        assertThat(saved.getCreated())
                .isAfterOrEqualTo(before)
                .isBeforeOrEqualTo(after);

        // expires is ~5 minutes after createdAt
        assertThat(saved.getExpires())
                .isAfterOrEqualTo(before.plusMinutes(4).plusSeconds(59))
                .isBeforeOrEqualTo(after.plusMinutes(5).plusSeconds(1));
    }

    // ─────────────────────────────────────────────────────────────
    // tokenService called exactly once
    // ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveConfirmationToken: delegates to tokenService exactly once")
    void saveConfirmationToken_callsTokenServiceExactlyOnce() {
        tokenHelper.saveConfirmationToken(buildUser());

        verify(tokenService).saveConfirmationToken(
                org.mockito.ArgumentMatchers.any(ConfirmationToken.class));
    }
}
