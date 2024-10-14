package com.example.backend.dto;

import java.time.LocalDateTime;

import com.example.backend.entity.Article;

import lombok.Getter;

@Getter
public class ArticleResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createdAt;

    public ArticleResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
    }
}
