package com.example.fyp5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.fyp5.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View bind = binding.getRoot();
        setContentView(bind);

        binding.HbottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {

                case R.id.DW:
                    startActivity(new Intent(getApplicationContext(), Downloads.class));
                    overridePendingTransition(0,0);
                    break;

                case R.id.FR:
                    startActivity(new Intent(getApplicationContext(), Friends.class));
                    overridePendingTransition(0,0);
                    break;
            }
            return true;
        });

        mAuth = FirebaseAuth.getInstance();

        binding.MALGTBTN.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, Login.class));
        });

        binding.MATD.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, TransferDataFriendsList.class));
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(MainActivity.this, Login.class));
        }
    }
}