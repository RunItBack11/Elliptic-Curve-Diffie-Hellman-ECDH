package com.example.fyp5;

public class CardViewInput {

    private String username;

    //sangat2 penting, kalau x letak empty constructor mmg
    // x jalan kt firebase
    public CardViewInput(){}

    public CardViewInput(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
