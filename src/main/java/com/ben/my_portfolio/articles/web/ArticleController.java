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
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping("/write-article")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<ArticleResponseDto> writeArticle(
            @RequestBody @Valid ArticleRequestDto articleRequestDto, @AuthenticationPrincipal User user){
        log.info("Request made to write an article: {}",user);
        ArticleResponseDto articleResponse = articleService.writeArticle(articleRequestDto, user);
        log.info("Article has been written successfully: {}",articleResponse);
        return new ResponseEntity<>(articleResponse, HttpStatus.OK);
    }

    @GetMapping("/read-all-articles")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<ArticleResponseDto>> readAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        log.info("Request made to read articles - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<ArticleResponseDto> articles = articleService.readAllArticles(pageable);
        log.info("Returning {} articles", articles.getTotalElements());
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


}
