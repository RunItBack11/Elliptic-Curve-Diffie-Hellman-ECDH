package com.example.fyp5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        button = findViewById(R.id.S_LGTBTN);

        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(view -> {
            firebaseAuth.signOut();
            startActivity(new Intent(Settings.this, Login.class));
        });
    }
}