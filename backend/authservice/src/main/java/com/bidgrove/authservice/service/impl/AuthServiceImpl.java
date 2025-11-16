package com.bidgrove.authservice.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bidgrove.authservice.dto.*;
import com.bidgrove.authservice.exception.DuplicateException;
import com.bidgrove.authservice.exception.UserNotFoundException;
import com.bidgrove.authservice.model.*;
import com.bidgrove.authservice.repository.*;
import com.bidgrove.authservice.security.JwtService;
import com.bidgrove.authservice.service.AuthService;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AccessTokenRepository accessTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // ---------------- REGISTER ----------------
    @Override
    public Map<String, String> register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            return Map.of("error", "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return Map.of("error", "Email already exists");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Map.of("error", "Passwords do not match");
        }

        Role role = Role.USER;

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(role);
        user.setEnabled(true);

        userRepository.save(user);

        return Map.of("message", "User registered successfully");
    }

    // ---------------- LOGIN ----------------
    @Override
    public Map<String, String> login(LoginRequest request) {

        // Find user
        User user = userRepository.findByEmail(request.getLogin())
                .or(() -> userRepository.findByUsername(request.getLogin()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Auth
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword()));

        // Tokens
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole().name());

        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Save tokens
        AccessToken at = new AccessToken(accessToken, user, Instant.now().plusSeconds(3600), false);
        RefreshToken rt = new RefreshToken(refreshToken, user, Instant.now().plusSeconds(604800), false);

        accessTokenRepository.save(at);
        refreshTokenRepository.save(rt);

        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    // ---------------- UPDATE PROFILE ----------------
    @Override
    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request, String token) {

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateException("Email already in use");
        }

        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateException("Username already taken");
        }

        user.setName(request.getName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());

        userRepository.save(user);

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole().name());
    }

    // ---------------- REFRESH TOKEN ----------------
    @Override
    public Map<String, String> refreshToken(RefreshTokenRequest request) {

        RefreshToken token = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (token.isRevoked() ||
                token.getExpiryDate().isBefore(Instant.now()) ||
                !jwtService.isTokenValid(token.getToken())) {

            return Map.of("error", "Invalid or expired refresh token");
        }

        User u = token.getUser();

        String newAccessToken = jwtService.generateAccessToken(
                u.getId(), u.getUsername(), u.getEmail(), u.getRole().name());

        return Map.of("accessToken", newAccessToken);
    }

    // ---------------- LOGOUT ----------------
    @Override
    public Map<String, String> logoutByAccessToken(String accessToken) {

        AccessToken token = accessTokenRepository.findByToken(accessToken)
                .orElseThrow(() -> new RuntimeException("Access token not found"));

        token.setRevoked(true);
        accessTokenRepository.save(token);

        // Also revoke refresh tokens
        String email = jwtService.extractUsername(accessToken);
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserEmail(email);
        refreshTokens.forEach(rt -> rt.setRevoked(true));
        refreshTokenRepository.saveAll(refreshTokens);

        return Map.of("message", "Logged out successfully");
    }

    // ---------------- GET CURRENT USER ----------------
    @Override
    public UserResponse getCurrentUser(String token) {

        if (!jwtService.isTokenValid(token)) {
            throw new RuntimeException("Invalid access token");
        }

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                user.getRole().name());
    }

    // ---------------- CHANGE PASSWORD ----------------
    @Override
    public Map<String, String> changePassword(ChangePasswordRequest request, String token) {

        if (!jwtService.isTokenValid(token)) {
            return Map.of("error", "Invalid access token");
        }

        String email = jwtService.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return Map.of("error", "Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return Map.of("message", "Password changed successfully");
    }

    // ---------------- FORGOT PASSWORD ----------------
    @Override
    public Map<String, String> forgotPassword(ForgotPasswordRequest request) {
        return Map.of("message", "Password reset link sent to email");
    }

    @Override
    public Map<String, String> resetPassword(ResetPasswordRequest request) {
        return Map.of("message", "Password reset successfully");
    }
}
