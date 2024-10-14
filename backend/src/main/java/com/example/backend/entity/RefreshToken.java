package com.example.backend.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "userEmail", nullable = false)
    private String userEmail;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    public RefreshToken(Long userId, String userEmail, String refreshToken, Instant expiryDate) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
    }

    public RefreshToken update(String newRefreshToken, Instant newExpiryDate) {
        this.refreshToken = newRefreshToken;
        this.expiryDate = newExpiryDate;
        return this;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }
}
