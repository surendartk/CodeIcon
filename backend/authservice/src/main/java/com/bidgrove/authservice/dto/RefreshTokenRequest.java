package com.bidgrove.authservice.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class RefreshTokenRequest {
    private String refreshToken;

	public RefreshTokenRequest(String refreshToken) {
		super();
		this.refreshToken = refreshToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
