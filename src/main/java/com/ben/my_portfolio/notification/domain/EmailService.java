package com.ben.my_portfolio.notification.domain;


import com.ben.my_portfolio.articles.ArticleContributedEvent;
import com.ben.my_portfolio.users.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    @Value("${app.admin.email}")
    private String adminEmail;


    private final EmailBuilder emailBuilder;
    private final EmailSender emailSender;

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        emailSender.sendEmail(
                event.email(),
                "Verify Your Email — BEN & CO",
                emailBuilder.buildVerificationEmailHtml(event.token())
        );
    }

    @ApplicationModuleListener
    public void onArticleContributed(ArticleContributedEvent event) {
        emailSender.sendEmail(
                event.email(),
                "Thank You for Your Contribution — BEN & CO",
                emailBuilder.buildContributionEmailHtml(event.articleTitle())
        );
    }

    @ApplicationModuleListener
    public void onArticleContributedNotifyAdmin(ArticleContributedEvent event) {
        emailSender.sendEmail(
                adminEmail,
                "New Article Submission — BEN & CO",
                emailBuilder.buildAdminNotificationEmailHtml(event.articleTitle(), event.email())
        );
    }





}