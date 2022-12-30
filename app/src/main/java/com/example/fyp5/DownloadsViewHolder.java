package com.example.fyp5;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadsViewHolder extends RecyclerView.ViewHolder {

    View v;
    TextView username;

    public DownloadsViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.DCV_USN);
        v = itemView;
    }
}
