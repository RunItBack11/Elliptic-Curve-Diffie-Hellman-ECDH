package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class Friends extends AppCompatActivity {


    EditText inputSearch;
    RecyclerView recyclerView;
    FirebaseRecyclerOptions<CardViewInput> options;
    FirebaseRecyclerAdapter<CardViewInput,FriendAddViewHolder> adapter;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        BottomNavigationView bottomNavigationView  = findViewById(R.id.FbottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {

                case R.id.HM:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0,0);
                    break;

                case R.id.DW:
                    startActivity(new Intent(getApplicationContext(), Downloads.class));
                    overridePendingTransition(0,0);
                    break;

                case R.id.FR:
                    break;
            }
            return true;
        });

        inputSearch = findViewById(R.id.F_SB);
        recyclerView = findViewById(R.id.F_RV);

        databaseReference= FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        LoadData("");

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString()!= null)
                {
                    LoadData(editable.toString());
                }
                else
                {
                    LoadData("");
                }

            }
        });


    }

    private void LoadData(String data){

        Query query = databaseReference.orderByChild("username").startAt(data).endAt(data+"\uf8ff");

        options = new FirebaseRecyclerOptions.Builder<CardViewInput>().setQuery(query, CardViewInput.class).build();
        adapter = new FirebaseRecyclerAdapter<CardViewInput, FriendAddViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FriendAddViewHolder holder, int position, @NonNull CardViewInput model) {

                holder.username.setText(model.getUsername());
                holder.v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Friends.this, FriendAddUserProfile.class);
                        intent.putExtra("UserId",getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public FriendAddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.fa_single_view, parent, false);
                return new FriendAddViewHolder(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }
}