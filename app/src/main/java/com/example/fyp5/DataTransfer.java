package com.example.fyp5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DataTransfer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        String privKey = getIntent().getStringExtra("senderPrivKey");
        String pubKeyX = getIntent().getStringExtra("receiverPubKeyX");
        String pubKeyY = getIntent().getStringExtra("receiverPubKeyY");

        System.out.println(privKey);
        System.out.println(pubKeyX);
        System.out.println(pubKeyY);

    }
}