package com.example.ad5;

import java.io.Serializable;

public class User implements Serializable {
    private int user_id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String role;   // 'admin' hoặc 'user'
    private int status;    // 1: hoạt động, 0: khóa

    // ✅ Constructor mặc định
    public User() {}

    // ✅ Constructor đầy đủ
    public User(int user_id, String username, String email, String phone,
                String address, String password, String role, int status) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    // ✅ Getter & Setter
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // ✅ Hàm kiểm tra quyền admin
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }
}
