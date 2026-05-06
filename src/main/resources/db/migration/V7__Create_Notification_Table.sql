CREATE TABLE email_outbox
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient   VARCHAR(255)  NOT NULL,
    subject     VARCHAR(500)  NOT NULL,
    email_type  VARCHAR(50)   NOT NULL,
    reference   VARCHAR(500)  NULL,
    status      VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    retry_count INT           NOT NULL DEFAULT 0,
    created_at  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at     DATETIME      NULL
);