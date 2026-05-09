package com.ben.my_portfolio.articles.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponseDto implements Serializable {

        private String title;
        private String content;
        private String name;
        private String about;
        private String status;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Instant createdAt;
        private Long userId;

        }
