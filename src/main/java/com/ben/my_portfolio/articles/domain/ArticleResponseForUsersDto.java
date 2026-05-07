package com.ben.my_portfolio.articles.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponseForUsersDto implements Serializable {

        private String title;
        private String content;
        private String name;
        private String about;

        }
