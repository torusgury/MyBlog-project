package com.example.backend.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.entity.RefreshToken;
import com.example.backend.entity.User;
import com.example.backend.repository.RefreshTokenRepository;
import com.example.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    @Value("${jwt.refresh-token-validity-in-seconds}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        RefreshToken refreshToken = new RefreshToken(
            userId,
            user.getEmail(),
            UUID.randomUUID().toString(),
            Instant.now().plusMillis(refreshTokenDurationMs * 1000)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.findByRefreshToken(token)
            .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void deleteByUsername(String username) {
        userRepository.findByEmail(username)
            .ifPresent(user -> refreshTokenRepository.deleteByUserId(user.getId()));
    }
}
