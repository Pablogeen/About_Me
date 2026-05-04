package com.ben.my_portfolio.articles.domain;

import com.ben.my_portfolio.articles.ArticleContributedEvent;
import com.ben.my_portfolio.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public ArticleResponseForUsersDto writeArticle(@Valid ArticleRequestDto articleRequestDto, User user) {
        log.info("User with id {} about to write an article: ",user.getId());

        Article article = modelMapper.map(articleRequestDto, Article.class);
        log.info("Mapped request article to article entity: {}",article);

        article.setTitle(articleRequestDto.getTitle().toUpperCase());
        article.setCreatedAt(LocalDateTime.now());
        article.setStatus(Status.PENDING);
        article.setUser(user);

        articleRepo.save(article);
        log.info("Article saved successfully");

        ArticleResponseForUsersDto savedArticle = new ArticleResponseForUsersDto();
        savedArticle.setTitle(article.getTitle());
        savedArticle.setContent(article.getContent());
        savedArticle.setName(article.getName());
        savedArticle.setAbout(article.getAbout());
        log.info("Article response: {}",savedArticle);

        eventPublisher.publishEvent(new ArticleContributedEvent(article.getUser().getEmail(), article.getTitle()));

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

    @Transactional
    public String approveArticle(UUID id) {
        Article article = articleRepo.findById(id).
                orElseThrow(() -> new ArticleNotFoundException("ARTICLE NOT FOUND"));
        log.info("Article with id exist {}:",id);

        if(!Status.PENDING.equals(article.getStatus())){
            log.info("Article has already been inspected. Status: {}",article.getStatus());
            throw new ArticleAlreadyInspectedException("ARTICLE HAS ALREADY BEEN INSPECTED");
        }

        article.setStatus(Status.APPROVED);
        log.info("Status set to approved");

        articleRepo.save(article);
        log.info("Article has been saved");

        return "ARTICLE HAS BEEN APPROVED SUCCESSFULLY";
    }

    @Transactional
    public String rejectArticle(UUID id) {
        Article article = articleRepo.findById(id).
                orElseThrow(() -> new ArticleNotFoundException("ARTICLE NOT FOUND"));
        log.info("A check has been made if article with id {} is in the db:",id);

        if(!Status.REJECTED.equals(article.getStatus())){
            log.info("Article been inspected. Status: {}",article.getStatus());
            throw new ArticleAlreadyInspectedException("ARTICLE HAS ALREADY BEEN INSPECTED");
        }

        article.setStatus(Status.REJECTED);
        log.info("Status set to rejected");

        articleRepo.save(article);
        log.info("Status has been saved");

        return "ARTICLE HAS BEEN REJECTED SUCCESSFULLY";
    }

    @Transactional(readOnly = true)
    public ArticleResponseForUsersDto getArticleById(UUID id) {
        log.info("About to get article of id: {}",id);
        Article article = articleRepo.findById(id).
                orElseThrow(() -> new ArticleNotFoundException("ARTICLE NOT FOUND"));

        ArticleResponseForUsersDto articleResponse = modelMapper.map(article, ArticleResponseForUsersDto.class);
        log.info("Article with id:{}, {}",id, articleResponse);

        return articleResponse;
    }

    @Transactional
    public ArticleResponseForUsersDto updateArticle(UUID id, @Valid ArticleRequestDto requestDto, User user) {
        log.info("About to update request with id: {} and user {}",id,user);

        Article article = articleRepo.findById(id).
                orElseThrow(() -> new ArticleNotFoundException("ARTICLE NOT FOUND"));
        log.info("Article found: {}",article);

        Long articleOwnerId = article.getUser().getId();
        Long requestingUserId = user.getId();

        log.info("Article owner ID: {}", articleOwnerId);
        log.info("Requesting user ID: {}", requestingUserId);

        if (!articleOwnerId.equals(requestingUserId)) {
            log.info("You're not the owner of the article");
            throw new AccessDeniedException("YOU ARE NOT THE OWNER OF THIS ARTICLE");
        }

        article.setTitle(requestDto.getTitle());
        article.setContent(requestDto.getContent());
        article.setName(requestDto.getName());
        article.setAbout(requestDto.getAbout());

        articleRepo.save(article);
        log.info("Article has been updated");

        ArticleResponseForUsersDto updatedResponse = modelMapper.map(article, ArticleResponseForUsersDto.class);
        log.info("Mapped entity response to dto: {}",updatedResponse);

        return updatedResponse;
    }

    @Transactional
    public String deleteArticle(UUID id, User user) {
        log.info("About to delete article with id: {}",id);
        Article article = articleRepo.findById(id).
                orElseThrow(() -> new ArticleNotFoundException("ARTICLE NOT FOND"));
        log.info("Article gotten: {}",article);

        Long articleOwnerId = article.getUser().getId();
        Long requestingUserId = user.getId();

        log.info("Owner ID: {}", articleOwnerId);
        log.info("Current User ID: {}", requestingUserId);

        if (!articleOwnerId.equals(requestingUserId)) {
            log.info("You're not the actual owner of the article");
            throw new AccessDeniedException("YOU ARE NOT THE OWNER OF THIS ARTICLE");
        }

        articleRepo.delete(article);
        log.info("Article has been deleted");

        return "ARTICLE HAS BEEN DELETED SUCCESSFULLY";
    }
}
