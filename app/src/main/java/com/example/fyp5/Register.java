package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity implements UserInfo {

    FirebaseAuth mAuth;
    EditText r_username, r_email, r_password, r_confirmPassword, r_phoneNum;
    Button r_registerBtn;
    String zero1, one1;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        r_username = findViewById(R.id.R_UN);
        r_email = findViewById(R.id.R_EA);
        r_password = findViewById(R.id.R_PW);
        r_confirmPassword = findViewById(R.id.R_CPW);
        r_phoneNum = findViewById(R.id.R_PN);
        TextView r_loginTxt = findViewById(R.id.R_LGNTXT);
        r_registerBtn = findViewById(R.id.R_REGBTN);

        mAuth = FirebaseAuth.getInstance();

        r_registerBtn.setOnClickListener(view ->{
            createUser();
        });

        r_loginTxt.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, Login.class));
        });



    }

    private void createUser(){
        String email = r_email.getText().toString().trim();
        String password = r_password.getText().toString().trim();
        String confirmPassword = r_confirmPassword.getText().toString().trim();
        String username = r_username.getText().toString().trim();
        String phoneNum = r_phoneNum.getText().toString().trim();



        databaseReference = FirebaseDatabase.getInstance().getReference();

        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern numbers = Pattern.compile("[0-9]");
        Pattern specialCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        if(username.isEmpty())
        {
           r_username.setError("Username cannot be empty");
        }

        else if(!username.trim().isEmpty())
        {
            databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<String> list = new ArrayList<>();
                    ArrayList<String> list1 = new ArrayList<>();
                    if(snapshot.exists())
                    {
                        for(DataSnapshot ds : snapshot.getChildren())
                        {
                            String username = ds.child("username").getValue().toString();
                            String email = ds.child("email").getValue().toString();
                            list.add(username);
                            list1.add(email);
                        }

                        if(list.contains(username))
                        {
                            r_username.setError("Username already exists");
                        }

                        else if (email.isEmpty())
                        {
                            r_email.setError(("Email Address cannot be empty"));
                        }

                        else if (list1.contains(email))
                        {
                            r_email.setError(("Email Address is already in use"));
                        }

                        else if (password.isEmpty())
                        {
                            r_password.setError(("Password cannot be empty"));
                        }

                        else if(password.length() < 11)
                        {
                            r_password.setError(("Password must at least be 12 characters long"));
                        }

                        else if(!lowerCase.matcher(password).find())
                        {
                            r_password.setError(("Password must contain a lowercase letter"));
                        }

                        else if(!upperCase.matcher(password).find())
                        {
                            r_password.setError(("Password must contain an uppercase letter"));
                        }

                        else if(!numbers.matcher(password).find())
                        {
                            r_password.setError(("Password must contain a number"));
                        }

                        else if(!specialCharPatten.matcher(password).find())
                        {
                            r_password.setError(("Password must contain a special character"));
                        }

                        else if (confirmPassword.isEmpty()) {
                            r_confirmPassword.setError(("Confirm Password cannot be empty"));
                        }

                        else if (!password.equals(confirmPassword)) {
                            r_confirmPassword.setError(("Passwords do not match"));
                        }

                        else if(phoneNum.isEmpty())
                        {
                            r_phoneNum.setError("Phone Number cannot be empty");
                        }

                        else if(phoneNum.length() < 10 || phoneNum.length() > 11)
                        {
                            r_phoneNum.setError("Phone Number must be 10 or 11 digits long");
                        }

                        else
                        {
                            char zero = phoneNum.charAt(0);
                            char one = phoneNum.charAt(1);

                            zero1 = String.valueOf(zero);
                            one1 = String.valueOf(one);

                            if(!(zero1.equals("0") && one1.equals("1")))
                            {
                                r_phoneNum.setError("Phone Number is not valid, it must start with '01'");
                            }

                            else
                            {
                                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            String userId = FirebaseAuth.getInstance().getUid();

                                            firebaseDatabase = FirebaseDatabase.getInstance();
                                            databaseReference = firebaseDatabase.getReference("users");


                                            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if(snapshot.hasChild(userId))
                                                    {
                                                        finish();
                                                    }

                                                    else
                                                    {
                                                        User user = new User(userId, username, email, phoneNum);
                                                        databaseReference.child(userId).setValue(user);

                                                        Toast.makeText(Register.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            startActivity(new Intent(Register.this, Login.class));

                                        }
                                        else{
                                            Toast.makeText(Register.this, "Registration Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            }

                        }


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Nullable
    @Override
    public Uri getPhotoUrl() {
        return null;
    }

    @Nullable
    @Override
    public String getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public String getEmail() {
        return null;
    }

    @Nullable
    @Override
    public String getPhoneNumber() {
        return null;
    }

    @NonNull
    @Override
    public String getProviderId() {
        return null;
    }

    @NonNull
    @Override
    public String getUid() {
        return null;
    }

    @Override
    public boolean isEmailVerified() {
        return false;
    }
}