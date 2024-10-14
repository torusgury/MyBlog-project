package com.example.backend.dto;

import com.example.backend.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    
    private String title;
    private String content;

    public Article toEntity() {
        return Article.builder()
            .title(title)
            .content(content)
            .build();
    }
}
