package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TransferDataFriendsList extends AppCompatActivity {

//    EditText inputSearch;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<TransferDataCardViewInput> options;
    FirebaseRecyclerAdapter<TransferDataCardViewInput,TransferDataViewHolder> adapter;
    DatabaseReference friendRef,userRef;
    FirebaseAuth firebaseAuth;
    String current_userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_data_friends_list);

//        inputSearch = findViewById(R.id.F_SB);
        recyclerView = findViewById(R.id.TD_RV);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        firebaseAuth = FirebaseAuth.getInstance();
        current_userId = firebaseAuth.getCurrentUser().getUid();
        friendRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(current_userId);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        DisplayFriends();

//        inputSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//                if(editable.toString()!= null)
//                {
//                    Search(editable.toString());
//                }
//                else
//                {
//                    Search("");
//                }
//
//            }
//        });

    }

    private void DisplayFriends()
    {
        Query query = friendRef.orderByChild("username");

        options = new FirebaseRecyclerOptions.Builder<TransferDataCardViewInput>().setQuery(query, TransferDataCardViewInput.class).build();
        adapter = new FirebaseRecyclerAdapter<TransferDataCardViewInput, TransferDataViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TransferDataViewHolder holder, int position, @NonNull TransferDataCardViewInput model) {

                final String other_userId = getRef(position).getKey();
                userRef.child(other_userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            final String username = snapshot.child("username").getValue().toString();
                            holder.username.setText(username);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TransferDataFriendsList.this, KeyGeneration.class);
                        intent.putExtra("UserId",getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public TransferDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.td_single_view, parent ,false);
                return new TransferDataViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

//        private void Search(String data)
//    {
//        Query query = userRef.orderByChild("username").startAt(data).endAt(data+"\uf8ff");
//
//        options = new FirebaseRecyclerOptions.Builder<TransferDataCardViewInput>().setQuery(query, TransferDataCardViewInput.class).build();
//        adapter = new FirebaseRecyclerAdapter<TransferDataCardViewInput, TransferDataViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull TransferDataViewHolder holder, int position, @NonNull TransferDataCardViewInput model) {
//
//                final String other_userId = getRef(position).getKey();
//                System.out.println(other_userId);
//                friendRef.child(other_userId).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if(snapshot.hasChild()
//                        {
//                            String username = model.getUsername();
//                            holder.username.setText(username);
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public TransferDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.td_single_view, parent, false);
//                return new TransferDataViewHolder(v);
//            }
//        };
//
//        adapter.startListening();
//        recyclerView.setAdapter(adapter);
//
//    }

}