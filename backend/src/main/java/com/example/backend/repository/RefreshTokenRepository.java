package com.example.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByUserEmail(String userEmail);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);
    void deleteByRefreshToken(String refreshToken);
}
