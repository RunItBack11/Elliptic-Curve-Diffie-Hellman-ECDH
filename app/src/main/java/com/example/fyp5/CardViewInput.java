package com.example.fyp5;
// friends cardview
public class CardViewInput {

    private String username;
    private String phoneNum;

    //sangat2 penting, kalau x letak empty constructor mmg
    // x jalan kt firebase
    public CardViewInput(){}

    public CardViewInput(String username, String phoneNum) {
        this.username = username;
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
