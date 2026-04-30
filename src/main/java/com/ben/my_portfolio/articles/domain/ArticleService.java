package com.ben.my_portfolio.articles.domain;

import com.ben.my_portfolio.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        savedArticle.setTitle(article.getTitle());
        savedArticle.setContent(article.getContent());
        savedArticle.setName(article.getName());
        savedArticle.setAbout(article.getAbout());
        log.info("Article response: {}",savedArticle);

        return savedArticle;
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponseDto> readAllArticles(Pageable pageable) {
        return articleRepo.findAll(pageable)
                .map(article -> {
                    ArticleResponseDto response = new ArticleResponseDto();
                    response.setTitle(article.getTitle());
                    response.setContent(article.getContent());
                    response.setAbout(article.getAbout());
                    response.setCreatedAt(article.getCreatedAt());
                    response.setStatus(String.valueOf(article.getStatus()));
                    response.setName(article.getName());
                    response.setUserId(article.getUser().getId());
                    return response;
                });
    }

    @Transactional(readOnly = true)
    public Page<ArticleResponseForUsersDto> readApprovedArticles(Pageable pageable) {
        log.info("About fetching all approved articles");
        return articleRepo.findByStatus(Status.APPROVED, pageable)
                .map(article -> modelMapper.map(article, ArticleResponseForUsersDto.class));

    }
}
