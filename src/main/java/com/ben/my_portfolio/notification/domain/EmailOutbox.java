package com.ben.my_portfolio.notification.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "email_outbox")
public class EmailOutbox {
    private static final int MAX_RETRIES = 3;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String recipient;

        @Column(nullable = false)
        private String subject;

        @Enumerated(EnumType.STRING)
        @Column(name = "email_type", nullable = false)
        private EmailType emailType;

        @Column(name = "reference")
        private String reference;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private EmailStatus status;

        @Column(name = "retry_count", nullable = false)
        private int retryCount;

        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;

        @Column(name = "sent_at")
        private LocalDateTime sentAt;

        protected EmailOutbox() {}

        public EmailOutbox(String recipient, String subject, EmailType emailType, String reference) {
            this.recipient = recipient;
            this.subject = subject;
            this.emailType = emailType;
            this.reference = reference;
            this.status = EmailStatus.PENDING;
            this.retryCount = 0;
            this.createdAt = LocalDateTime.now();
        }

        public void markSent() {
            this.status = EmailStatus.SENT;
            this.sentAt = LocalDateTime.now();
        }

        public void markFailed() {
            this.retryCount++;
            if (this.retryCount >= MAX_RETRIES) {
                this.status = EmailStatus.FAILED;
            }
        }


    }
