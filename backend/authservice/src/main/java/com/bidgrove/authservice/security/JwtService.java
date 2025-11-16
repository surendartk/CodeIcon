package com.bidgrove.authservice.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.bidgrove.authservice.exception.InvalidTokenException;
import com.bidgrove.authservice.model.AccessToken;
import com.bidgrove.authservice.repository.AccessTokenRepository;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.expirationMs}")
    private long ACCESS_TOKEN_EXPIRATION;

    @Value("${jwt.refreshExpirationMs}")
    private long REFRESH_TOKEN_EXPIRATION;

    @Autowired
    private AccessTokenRepository accessTokenRepository;

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateAccessToken(Long userId, String userName, String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("userName", userName)
                .claim("userId", userId)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token) {
        String username = extractUsername(token);
        if (username == null)
            throw new InvalidTokenException("Invalid JWT token");
        if (isTokenExpired(token))
            throw new InvalidTokenException("JWT token has expired");

        AccessToken access = accessTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Access token not found"));
        if (access.isRevoked())
            throw new InvalidTokenException("Token has been revoked. Please login again.");

        return true;
    }

    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return tokenUsername != null && tokenUsername.equals(username) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
    
    public Long extractUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);
        } catch (JwtException e) {
            return null;
        }
    }

    public String extractRole(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("role", String.class);
        } catch (JwtException e) {
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
