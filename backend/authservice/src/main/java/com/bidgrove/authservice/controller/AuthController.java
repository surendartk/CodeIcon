package com.bidgrove.authservice.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bidgrove.authservice.dto.*;
import com.bidgrove.authservice.exception.DuplicateException;
import com.bidgrove.authservice.exception.UserNotFoundException;
import com.bidgrove.authservice.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // ---------------- TEST ----------------
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("message", "Auth Service Running ✅"));
    }

    // ---------------- REGISTER ----------------
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Object res = authService.register(request);

        if (res instanceof Map && ((Map<?, ?>) res).containsKey("error")) {
            return ResponseEntity.badRequest().body(res); // 400 Bad Request
        }

        return ResponseEntity.status(201).body(res); // 201 Created
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Object res = authService.login(request);

            if (res instanceof Map && ((Map<?, ?>) res).containsKey("error")) {
                return ResponseEntity.status(401).body(res); // Unauthorized
            }

            return ResponseEntity.ok(res); // 200 OK
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); // Not Found
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")); // Unauthorized
        }
    }

    // ---------------- REFRESH TOKEN ----------------
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        Object res = authService.refreshToken(new RefreshTokenRequest(refreshToken));

        if (res instanceof Map && ((Map<?, ?>) res).containsKey("error")) {
            return ResponseEntity.status(401).body(res); // Unauthorized
        }

        return ResponseEntity.ok(res); // 200 OK
    }

    // ---------------- LOGOUT ----------------
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        Object res = authService.logoutByAccessToken(token);
        return ResponseEntity.ok(res); // 200 OK
    }

    // ---------------- GET MY PROFILE ----------------
    @GetMapping("/my-profile")
    public ResponseEntity<?> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        try {
            Object res = authService.getCurrentUser(token);
            return ResponseEntity.ok(res); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid or expired access token")); // Unauthorized
        }
    }

    // ---------------- UPDATE PROFILE ----------------
    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

        try {
            Object res = authService.updateProfile(request, token);
            return ResponseEntity.ok(res); // 200 OK
        } catch (DuplicateException e) {
            return ResponseEntity.status(409).body(Map.of("error", e.getMessage())); // Conflict
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage())); // Not Found
        }
    }

    // ---------------- CHANGE PASSWORD ----------------
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
        Object res = authService.changePassword(request, token);

        if (res instanceof Map && ((Map<?, ?>) res).containsKey("error")) {
            return ResponseEntity.badRequest().body(res); // 400 Bad Request
        }

        return ResponseEntity.ok(res); // 200 OK
    }

    // ---------------- FORGOT PASSWORD ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Object res = authService.forgotPassword(request);
        return ResponseEntity.ok(res); // 200 OK
    }

    // ---------------- RESET PASSWORD ----------------
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        Object res = authService.resetPassword(request);
        return ResponseEntity.ok(res); // 200 OK
    }
}
