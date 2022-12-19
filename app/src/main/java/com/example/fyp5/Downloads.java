package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Downloads extends AppCompatActivity {

    DatabaseReference databaseReference;
    String currentUserId;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

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

        databaseReference.child("pubKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // retrieve all pubKey children
                // filter buffer contents
                // check which public key belongs to who
                List <String> buffer = new ArrayList<>();
                for(DataSnapshot ds : snapshot.child("pubKey").getChildren())
                {
                    String item = ds.getValue(String.class);
                    buffer.add(item);
                }
                for(int i =0; i<buffer.size(); i++)
                {
                    System.out.println(buffer.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}