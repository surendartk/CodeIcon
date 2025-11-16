package com.bidgrove.authservice.dto;
public class UserResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;

    // ---- CONSTRUCTOR ----
    public UserResponse(Long id, String username, String name, String email,
                        String phone, String address, String role) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
    }

    // ---- GETTERS & SETTERS ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
