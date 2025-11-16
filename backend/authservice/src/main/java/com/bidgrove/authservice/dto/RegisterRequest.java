package com.bidgrove.authservice.dto;

import lombok.Data;

/**
 * 
 */
@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String name;
    private String phone;
    private String address;
    private String role; // ✅ Optional role field

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String confirmPassword,
                           String name, String phone, String address, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    // Getters and Setters (you already use @Data but including for clarity)
}
