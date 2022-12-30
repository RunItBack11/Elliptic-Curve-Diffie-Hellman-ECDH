package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FriendAddUserProfile extends AppCompatActivity {

    TextView textView;
    DatabaseReference databaseReference, friendReqRef, friendAcceptRef, unfriendRef;
    Button friendRequestBtn, declineRequestBtn;
    String current_userId, other_userId, currentState, saveCurrentDate;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_add_user_profile);

        textView = findViewById(R.id.F_USP);
        friendRequestBtn = findViewById(R.id.F_ADDFRIENDBTN);
        declineRequestBtn =findViewById(R.id.F_DECFRIENDBTN);
        currentState ="not_friends";

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        friendReqRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        friendAcceptRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        unfriendRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        current_userId = firebaseAuth.getCurrentUser().getUid();
        other_userId = getIntent().getStringExtra("UserId");

        databaseReference.child(other_userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {

                    String username = snapshot.child("username").getValue().toString();
                    textView.append(username);

                    ButtonMaintenance();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(FriendAddUserProfile.this, "Unable to retrieve user info", Toast.LENGTH_SHORT).show();

            }
        });

        declineRequestBtn.setVisibility(View.INVISIBLE);
        declineRequestBtn.setEnabled(false);

        if(!current_userId.equals(other_userId))
        {
            friendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    friendRequestBtn.setEnabled(false);

                    if (currentState.equals("not_friends"))
                    {
                        SendFriendRequest();
                    }

                    if (currentState.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }

                    if (currentState.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }

                    if (currentState.equals(("friends")))
                    {
                        Unfriend();
                    }
                }
            });
        }
        else
        {
            friendRequestBtn.setVisibility(View.INVISIBLE);
            declineRequestBtn.setVisibility(View.INVISIBLE);
        }


    }


    private void ButtonMaintenance()
    {
        friendReqRef.child(current_userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(other_userId))
                {
                    String request_type = snapshot.child(other_userId).child("request_state").getValue().toString();

                    if(request_type.equals("sent"))
                    {
                        currentState= "request_sent";
                        friendRequestBtn.setText("Cancel Friend Request");

                        declineRequestBtn.setVisibility(View.INVISIBLE);
                        declineRequestBtn.setEnabled(false);
                    }

                    else if(request_type.equals(("received")))
                    {
                        currentState= "request_received";
                        friendRequestBtn.setText("Accept Friend Request");

                        declineRequestBtn.setVisibility(View.VISIBLE);
                        declineRequestBtn.setEnabled(true);

                        declineRequestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                CancelFriendRequest();
                            }
                        });


                    }
                }
                else
                {
                    friendAcceptRef.child(current_userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(other_userId))
                            {
                                currentState = "friends";
                                friendRequestBtn.setText("UNFRIEND");

                                declineRequestBtn.setVisibility(View.INVISIBLE);
                                declineRequestBtn.setEnabled(false);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SendFriendRequest()
    {
        friendReqRef.child(current_userId).child(other_userId)
                .child("request_state").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            friendReqRef.child(other_userId).child(current_userId)
                                    .child("request_state").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                friendRequestBtn.setEnabled(true);
                                                currentState = "request_sent";
                                                friendRequestBtn.setText("Cancel Friend Request");
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest() {

        friendReqRef.child(current_userId).child(other_userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            friendReqRef.child(other_userId).child(current_userId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                friendRequestBtn.setEnabled(true);
                                                currentState = "not_friends";
                                                friendRequestBtn.setText("ADD FRIEND");

                                                declineRequestBtn.setVisibility(View.INVISIBLE);
                                                declineRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        friendAcceptRef.child(current_userId).child(other_userId).child("date").setValue(saveCurrentDate).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            friendAcceptRef.child(other_userId).child(current_userId).child("date").setValue(saveCurrentDate).
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful())
                                            {
                                                friendReqRef.child(current_userId).child(other_userId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful())
                                                                {
                                                                    friendReqRef.child(other_userId).child(current_userId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        friendRequestBtn.setEnabled(true);
                                                                                        currentState = "friends";
                                                                                        friendRequestBtn.setText("UNFRIEND");

                                                                                        declineRequestBtn.setVisibility(View.INVISIBLE);
                                                                                        declineRequestBtn.setEnabled(false);


                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void Unfriend()
    {
        friendAcceptRef.child(current_userId).child(other_userId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            friendAcceptRef.child(other_userId).child(current_userId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                friendRequestBtn.setEnabled(true);
                                                currentState = "not_friends";
                                                friendRequestBtn.setText("ADD FRIEND");

                                                declineRequestBtn.setVisibility(View.INVISIBLE);
                                                declineRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }


}