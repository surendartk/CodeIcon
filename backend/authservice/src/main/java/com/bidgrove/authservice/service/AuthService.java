package com.bidgrove.authservice.service;



import org.springframework.stereotype.Service;

import com.bidgrove.authservice.dto.ChangePasswordRequest;
import com.bidgrove.authservice.dto.ForgotPasswordRequest;
import com.bidgrove.authservice.dto.LoginRequest;
import com.bidgrove.authservice.dto.RefreshTokenRequest;
import com.bidgrove.authservice.dto.RegisterRequest;
import com.bidgrove.authservice.dto.ResetPasswordRequest;
import com.bidgrove.authservice.dto.UpdateProfileRequest;
import com.bidgrove.authservice.dto.UserResponse;

@Service
public interface AuthService {
    Object register(RegisterRequest request);
    Object login(LoginRequest request);
    Object refreshToken(RefreshTokenRequest request);
    
    Object getCurrentUser(String token);
    Object changePassword(ChangePasswordRequest request, String token);
    Object forgotPassword(ForgotPasswordRequest request);
    Object resetPassword(ResetPasswordRequest request);
	Object logoutByAccessToken(String accessToken);
	
	UserResponse updateProfile(UpdateProfileRequest request, String token);

}
