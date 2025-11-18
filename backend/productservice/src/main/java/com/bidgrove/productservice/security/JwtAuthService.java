package com.bidgrove.productservice.security;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Service
public class JwtAuthService {

    private final String SECRET_KEY = "zasxrdctfvybguhinjlmi5676879jhbe"; // same as auth-service

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public AuthUser getUserFromToken(String token) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = claims.get("userId", Long.class);
            String role = claims.get("role", String.class);

            return new AuthUser(userId, role);

        } catch (Exception e) {
            return null; // invalid or expired token
        }
    }
}
