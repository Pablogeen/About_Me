package com.ben.my_portfolio.notification.domain;


import com.ben.my_portfolio.articles.ArticleContributedEvent;
import com.ben.my_portfolio.articles.ArticleReviewedApprovedEvent;
import com.ben.my_portfolio.articles.ArticleReviewedRejectedEvent;
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

    private final EmailOutboxRepository outboxRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        outboxRepository.save(new EmailOutbox(
                event.email(),
                "Verify Your Email — BEN & CO",
                EmailType.VERIFICATION,
                event.token()));

    }

    @ApplicationModuleListener
    public void onArticleContributed(ArticleContributedEvent event) {
        outboxRepository.save(new EmailOutbox(
                event.email(),
                "Thank You for Your Contribution — BEN & CO",
                EmailType.CONTRIBUTION,
                event.articleTitle()));

    }

    @ApplicationModuleListener
    public void onArticleContributedNotifyAdmin(ArticleContributedEvent event) {
        outboxRepository.save(new EmailOutbox(
                adminEmail,
                "New Article Submission — BEN & CO",
                EmailType.ADMIN_NOTIFICATION,
                event.articleTitle() + "|" + event.email()));

    }

    @ApplicationModuleListener
    public void onArticleApproved(ArticleReviewedApprovedEvent event) {
        outboxRepository.save(new EmailOutbox(
                event.contributorEmail(),
                "Your Article Has Been Approved — BEN & CO",
                EmailType.ARTICLE_APPROVED,
                event.articleTitle()));

    }

    @ApplicationModuleListener
    public void onArticleRejected(ArticleReviewedRejectedEvent event) {
        outboxRepository.save(new EmailOutbox(
                event.contributorEmail(),
                "Regarding Your Article Submission — BEN & CO",
                EmailType.ARTICLE_REJECTED,
                event.articleTitle()));

    }






}