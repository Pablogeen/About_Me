CREATE TABLE user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(50),
    password VARCHAR(255),
    role VARCHAR(20),
    is_verified BIT(1),
    created_at DATETIME(6),
    PRIMARY KEY (id)
);


CREATE TABLE article (
    id VARCHAR(36) NOT NULL,
    title VARCHAR(100) NOT NULL,
    content LONGTEXT NOT NULL,
    name VARCHAR(50) NOT NULL,
    about VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_article_user FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE event_publication (
    id VARCHAR(36) NOT NULL,
    listener_id VARCHAR(512) NOT NULL,
    event_type VARCHAR(512) NOT NULL,
    serialized_event VARCHAR(2048) NOT NULL,
    publication_date DATETIME(6) NOT NULL,
    completion_date DATETIME(6),
    PRIMARY KEY (id)
);