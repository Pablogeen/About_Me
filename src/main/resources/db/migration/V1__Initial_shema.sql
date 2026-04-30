
CREATE TABLE user (
                      id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                      email      VARCHAR(50),
                      password   VARCHAR(255),
                      role       VARCHAR(50),
                      is_verified BIT(1),
                      created_at DATETIME
);

CREATE TABLE article (
                         id         VARCHAR(36) PRIMARY KEY,
                         title      VARCHAR(100)  NOT NULL,
                         content    LONGTEXT      NOT NULL,
                         name       VARCHAR(50)   NOT NULL,
                         about      VARCHAR(50)   NOT NULL,
                         created_at DATETIME      NOT NULL,
                         status     VARCHAR(20)   NOT NULL,
                         user_id    BIGINT        NOT NULL,
                         CONSTRAINT fk_article_user FOREIGN KEY (user_id) REFERENCES user(id)
);