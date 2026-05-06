package com.ben.my_portfolio.notification.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, Long> {

    List<EmailOutbox> findByStatus(EmailStatus status);


}
