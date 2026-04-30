package com.ben.my_portfolio.articles.domain;

import com.ben.my_portfolio.users.User;
import com.ben.my_portfolio.users.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("articleSecurity")
@RequiredArgsConstructor
public class ArticleSecurity {

    private final ArticleRepository articleRepo;

    public boolean isOwner(UUID articleId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new IllegalStateException("ARTICLE NOT FOUND"));

        return article.getUser().getId().equals(user.getId());
    }
}
