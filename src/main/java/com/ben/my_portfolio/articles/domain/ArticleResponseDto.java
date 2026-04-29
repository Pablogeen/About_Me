package com.ben.my_portfolio.articles.domain;

import com.ben.my_portfolio.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponseDto{

        private UUID id;
        private String title;
        private String content;
        private String name;
        private String about;
        private LocalDateTime createdAt;
        private Long userId;

        }
