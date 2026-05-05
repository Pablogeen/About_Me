CREATE TABLE confirmation_token (
                                    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    token        VARCHAR(255),
                                    created      DATETIME,
                                    expires      DATETIME,
                                    confirmed_at DATETIME,
                                    user_id      BIGINT NOT NULL,
                                    CONSTRAINT fk_confirmation_token_user FOREIGN KEY (user_id) REFERENCES user(id)
);