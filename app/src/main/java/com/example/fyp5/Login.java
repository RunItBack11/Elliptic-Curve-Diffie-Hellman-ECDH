package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    TextView l_email, l_password, l_regTxt;
    FirebaseAuth mAuth;
    Button l_lgnBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        l_email = findViewById(R.id.L_EA);
        l_password = findViewById(R.id.L_PW);
        l_regTxt = findViewById(R.id.L_REGTXT);
        l_lgnBtn = findViewById(R.id.L_LGNBTN);

        mAuth = FirebaseAuth.getInstance();

        l_lgnBtn.setOnClickListener(view -> {
            loginUser();
        });

        l_regTxt.setOnClickListener(view -> {
            startActivity(new Intent(Login.this, Register.class));
        });
    }

    private void loginUser(){
        String email = l_email.getText().toString();
        String password = l_password.getText().toString();

        if(email.isEmpty())
        {
            l_email.setError("Email Address cannot be empty");
            l_email.requestFocus();
        }
        else if(password.isEmpty())
        {
            l_password.setError("Password cannot be empty");
            l_password.requestFocus();
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Login.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(Login.this, "Login error: "+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}