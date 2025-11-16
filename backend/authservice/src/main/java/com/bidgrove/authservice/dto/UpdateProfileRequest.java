package com.bidgrove.authservice.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
	
	
	private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
    
    public UpdateProfileRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UpdateProfileRequest(String name, String username, String email, String phone, String address) {
		super();
		this.name = name;
		this.username = username;
		this.email = email;
		this.phone = phone;
		this.address = address;
	}
    
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	
    
    
}
