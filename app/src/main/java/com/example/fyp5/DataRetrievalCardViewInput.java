package com.example.fyp5;

public class DataRetrievalCardViewInput {

    String data;
    String plaintext;

    public String getPlaintext() {
        return plaintext;
    }

    public void setPlaintext(String plaintext) {
        this.plaintext = plaintext;
    }

    public DataRetrievalCardViewInput(){}

    public DataRetrievalCardViewInput(String data) {
        this.data = data;
    }

//    public void changeText(String text)
//    {
//        data = text;
//    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }



}
