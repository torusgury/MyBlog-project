package com.example.backend.config.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;
    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            UserDetailsService userDetailsService) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityInSeconds = accessTokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenValidityInSeconds, "ACCESS");
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidityInSeconds, "REFRESH");
    }

    private String createToken(Authentication authentication, long validityInSeconds, String tokenType) {
        Instant now = Instant.now();
        Instant validity = now.plus(validityInSeconds, ChronoUnit.SECONDS);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(validity))
                .claim("token_type", tokenType)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public long getAccessTokenValidityInSeconds() {
        return this.accessTokenValidityInSeconds;
    }

    public long getRefreshTokenValidityInSeconds() {
        return this.refreshTokenValidityInSeconds;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isAccessToken(String token) {
        Claims claims = parseToken(token);
        return "ACCESS".equals(claims.get("token_type"));
    }

    public boolean isRefreshToken(String token) {
        Claims claims = parseToken(token);
        return "REFRESH".equals(claims.get("token_type"));
    }

    public String createTokenFromUsername(String username) {
        Instant now = Instant.now();
        Instant validity = now.plus(accessTokenValidityInSeconds, ChronoUnit.SECONDS);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(validity))
                .signWith(key)
                .compact();
    }
}
