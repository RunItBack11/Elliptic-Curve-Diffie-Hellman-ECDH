package com.example.fyp5;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.Objects;

public class TransferDataViewHolder extends RecyclerView.ViewHolder {

    View v;
    TextView username;

    public TransferDataViewHolder(@NonNull View itemView) {
        super(itemView);
        username = itemView.findViewById(R.id.TDCV_USN);
        v=itemView;

    }

//    public void setUsername (String username)
//    {
//        TextView name = (TextView) v.findViewById(R.id.TDCV_USN);
//        name.setText(username);
//    }

}
