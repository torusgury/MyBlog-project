package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.Article;

public interface BlogRepository extends JpaRepository<Article, Long>{
    
}
