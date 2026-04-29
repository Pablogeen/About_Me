package com.ben.my_portfolio.articles.domain;

import com.ben.my_portfolio.users.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepo;
    private final ModelMapper modelMapper;


    @Transactional
    public ArticleResponseDto writeArticle(@Valid ArticleRequestDto articleRequestDto, User user) {
        log.info("User with id {} about to write an article: ",user.getId());

        Article article = modelMapper.map(articleRequestDto, Article.class);
        log.info("Mapped request article to article entity: {}",article);

        article.setId(UUID.randomUUID());
        article.setCreatedAt(LocalDateTime.now());
        article.setStatus(Status.PENDING);
        article.setUser(user);

        articleRepo.save(article);
        log.info("Article saved successfully");

        ArticleResponseDto savedArticle = new ArticleResponseDto();
        savedArticle.setId(article.getId());
        savedArticle.setTitle(article.getTitle());
        savedArticle.setContent(article.getContent());
        savedArticle.setName(article.getName());
        savedArticle.setAbout(article.getAbout());
        savedArticle.setCreatedAt(article.getCreatedAt());
        savedArticle.setUserId(article.getUser().getId());
        log.info("Article response: {}",savedArticle);

        return savedArticle;
    }
}
