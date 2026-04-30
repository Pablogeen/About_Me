package com.ben.my_portfolio.articles.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, UUID> {

    @EntityGraph(attributePaths = {"user"})
    Page<Article> findAll(Pageable pageable);

    Page<Article> findByStatus(@Param("status") Status status, Pageable pageable);
}
