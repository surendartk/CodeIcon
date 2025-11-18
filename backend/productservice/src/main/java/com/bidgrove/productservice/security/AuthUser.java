package com.bidgrove.productservice.security;

public class AuthUser {

    private Long userId;
    private String role;

    public AuthUser(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}
