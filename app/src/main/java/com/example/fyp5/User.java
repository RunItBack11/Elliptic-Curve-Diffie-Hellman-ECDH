package com.example.fyp5;

public class User {

    public String username;
    public String email;
    public String phoneNum;
    public String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public User() {

    }

    public User(String userId, String username, String email, String phoneNum) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
    }
}
