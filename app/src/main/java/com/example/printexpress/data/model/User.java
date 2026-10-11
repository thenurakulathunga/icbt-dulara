package com.example.printexpress.data.model;

/** A customer account (the password hash is never put in this object). */
public class User {
    private final long userId;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String createdAt;

    public User(long userId, String fullName, String email, String phone, String createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCreatedAt() { return createdAt; }

    /** "Demo Customer" -> "Demo" */
    public String getFirstName() {
        return fullName == null ? "" : fullName.trim().split(" ")[0];
    }
}
