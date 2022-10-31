package com.example.fyp5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Downloads extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        BottomNavigationView bottomNavigationView  = findViewById(R.id.DbottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {

                case R.id.HM:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    break;

                case R.id.DW:
                    break;

                case R.id.FR:
                    startActivity(new Intent(getApplicationContext(), Friends.class));
                    overridePendingTransition(0,0);
                    break;
            }
            return true;
        });
    }
}