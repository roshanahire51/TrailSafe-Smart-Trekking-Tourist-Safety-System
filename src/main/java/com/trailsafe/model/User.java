package com.trailsafe.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class User {
    private int userId;
    private String name;
    private String email;
    private String phone;
    private String password;

    public User() {}

    public User(int userId, String name, String email, String phone, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userId", userId);
        m.put("name", name);
        m.put("email", email);
        m.put("phone", phone);
        return m;
    }
}
