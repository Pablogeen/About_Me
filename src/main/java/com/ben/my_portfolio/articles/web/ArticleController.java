package com.ben.my_portfolio.articles.web;

import com.ben.my_portfolio.articles.domain.ArticleRequestDto;
import com.ben.my_portfolio.articles.domain.ArticleResponseDto;
import com.ben.my_portfolio.articles.domain.ArticleResponseForUsersDto;
import com.ben.my_portfolio.articles.domain.ArticleService;
import com.ben.my_portfolio.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/articles")
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/write-article")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<ArticleResponseForUsersDto> writeArticle(
            @RequestBody @Valid ArticleRequestDto articleRequestDto, @AuthenticationPrincipal User user){
        log.info("Request made to write an article: {}",user);
        ArticleResponseForUsersDto articleResponse = articleService.writeArticle(articleRequestDto, user);
        log.info("Article has been written successfully: {}",articleResponse);
        return new ResponseEntity<>(articleResponse, HttpStatus.OK);
    }

    @GetMapping("/read-all-articles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ArticleResponseDto>> readAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== AUTH: " + auth);
        System.out.println("=== AUTHORITIES: " + auth.getAuthorities());
        System.out.println("=== PRINCIPAL TYPE: " + auth.getPrincipal().getClass().getName());

        log.info("Request made to read articles - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponseDto> articles = articleService.readAllArticles(pageable);
        log.info("{} articles", articles.getTotalElements());
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<Page<ArticleResponseForUsersDto>> readApprovedArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        log.info("Request made to read approved articles - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponseForUsersDto> articles = articleService.readApprovedArticles(pageable);
        log.info("Returning {} all approved articles", articles.getTotalElements());
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    @PostMapping("/{id}/approve-article")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> approveArticle(@PathVariable UUID id){
        log.info("Request to approve article with id: {}",id);
        String response = articleService.approveArticle(id);
        log.info("Article has been approved ");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{id}/reject-article")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> rejectArticle(@PathVariable UUID id){
        log.info("Request to reject article with id: {}",id);
        String response = articleService.rejectArticle(id);
        log.info("Article has been rejected");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseForUsersDto> getArticleById(@PathVariable UUID id){
        log.info("Request made to get an article by id: {}",id);
        ArticleResponseForUsersDto articleResponse = articleService.getArticleById(id);
        log.info("Article gotten: {}", articleResponse);
        return new ResponseEntity<>(articleResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("@ArticleSecurity.isOnwer(#id, authentication)")
    public ResponseEntity<ArticleResponseForUsersDto>
                        updateArticle(@PathVariable UUID id, @RequestBody @Valid ArticleRequestDto requestDto){
        log.info("Call made to update article with id: {}",id);
        ArticleResponseForUsersDto articleResponse = articleService.updateArticle(id, requestDto);
        log.info("Article Updated successfully");
        return new ResponseEntity<>(articleResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("@ArticleSecurity.isOnwer(#id, authentication)")
    public ResponseEntity<String> deleteArticle(@PathVariable UUID id){
        log.info("Call made to delete article with id: {}",id);
        String articleResponse = articleService.deleteArticle(id);
        log.info("Article Deleted successfully");
        return new ResponseEntity<>(articleResponse, HttpStatus.NO_CONTENT);
    }



}
