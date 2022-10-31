package com.example.fyp5;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class FriendAddViewHolder extends RecyclerView.ViewHolder {

    TextView username;
    View v;

    public FriendAddViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.CV_USN);
        v=itemView;
    }
}
