package com.ben.my_portfolio.articles.web;

import com.ben.my_portfolio.articles.domain.ArticleRequestDto;
import com.ben.my_portfolio.articles.domain.ArticleResponseDto;
import com.ben.my_portfolio.articles.domain.ArticleService;
import com.ben.my_portfolio.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/articles")
@Slf4j
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService userService;

    @PostMapping("/write-article")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<ArticleResponseDto> writeArticle(
            @RequestBody @Valid ArticleRequestDto articleRequestDto, @AuthenticationPrincipal User user){
        log.info("Request made to write an article: {}",user);
        ArticleResponseDto articleResponse = userService.writeArticle(articleRequestDto, user);
        log.info("Article has been written successfully: {}",articleResponse);
        return new ResponseEntity<>(articleResponse, HttpStatus.OK);
    }


}
