package com.ben.my_portfolio.articles.web;

import com.ben.my_portfolio.articles.domain.ArticleRequestDto;
import com.ben.my_portfolio.articles.domain.ArticleResponseDto;
import com.ben.my_portfolio.articles.domain.ArticleService;
import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.domain.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=== AUTH: " + auth);
        System.out.println("=== PRINCIPAL TYPE: " + auth.getPrincipal().getClass().getName());
        System.out.println("=== PRINCIPAL VALUE: " + auth.getPrincipal());
        System.out.println("=== USER FROM ANNOTATION: " + user);
        ArticleResponseDto articleResponse = userService.writeArticle(articleRequestDto, user);
        log.info("Article has been written successfully: {}",articleResponse);
        return new ResponseEntity<>(articleResponse, HttpStatus.OK);
    }


}
