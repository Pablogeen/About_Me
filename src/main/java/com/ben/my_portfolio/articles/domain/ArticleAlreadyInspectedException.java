package com.ben.my_portfolio.articles.domain;

public class ArticleAlreadyInspectedException extends RuntimeException{

    public ArticleAlreadyInspectedException(String message) {
        super(message);
    }
}
