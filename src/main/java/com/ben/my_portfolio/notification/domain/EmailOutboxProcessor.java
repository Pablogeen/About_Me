package com.ben.my_portfolio.notification.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailOutboxProcessor {


    private final EmailOutboxRepository outboxRepository;
    private final EmailSender emailSender;
    private final EmailBuilder emailBuilder;


    @Scheduled(fixedDelay = 30000)
    public void process() {
        List<EmailOutbox> pending = outboxRepository
                                    .findByStatus(EmailStatus.PENDING);
        log.info("Pending Emails: {}",pending);

        for (EmailOutbox outbox : pending) {
            try {
                String html = buildHtml(outbox);
                emailSender.sendEmail(outbox.getRecipient(), outbox.getSubject(), html);
                log.info("Email sent to :{}",outbox.getRecipient());
                outbox.markSent();
                log.info("Email sent successfully to {} [type={}]", outbox.getRecipient(), outbox.getEmailType());
            } catch (Exception e) {
                outbox.markFailed();
                log.error("Failed to send email to {} [type={}, attempt={}]: {}",
                        outbox.getRecipient(), outbox.getEmailType(), outbox.getRetryCount(), e.getMessage());
            }
            outboxRepository.save(outbox);
        }
    }

    private String buildHtml(EmailOutbox outbox) {

        return switch (outbox.getEmailType()) {
            case VERIFICATION ->
                    emailBuilder.buildVerificationEmailHtml(outbox.getReference());
            case CONTRIBUTION ->
                    emailBuilder.buildContributionEmailHtml(outbox.getReference());
            case ADMIN_NOTIFICATION -> {
                String[] parts = outbox.getReference().split("\\|");
                yield emailBuilder.buildAdminNotificationEmailHtml(parts[0], parts[1]);
            }
            case ARTICLE_APPROVED ->
                    emailBuilder.buildArticleApprovedEmailHtml(outbox.getReference());
            case ARTICLE_REJECTED ->
                    emailBuilder.buildArticleRejectedEmailHtml(outbox.getReference());
            case CONTACT_ME -> {
                String[] parts = outbox.getReference().split("\\|");
                yield emailBuilder.buildContactRequestEmail(parts[0], parts[1], parts[2],parts[3]);
            }
        };
    }

}
