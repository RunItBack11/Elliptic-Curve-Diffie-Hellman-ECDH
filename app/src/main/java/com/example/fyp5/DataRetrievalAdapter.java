package com.example.fyp5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class DataRetrievalAdapter extends RecyclerView.Adapter<DataRetrievalAdapter.DataRetrievalViewHolder> {

    Context context;
    ArrayList<DataRetrievalCardViewInput> list;

    public DataRetrievalAdapter(Context context, ArrayList<DataRetrievalCardViewInput> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DataRetrievalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dr_single_view, parent, false);
        return new DataRetrievalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataRetrievalViewHolder holder, int position) {

        DataRetrievalCardViewInput data = list.get(position);
        holder.dataText.setText(data.getData());
        holder.dataText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.dataText.setText(data.getPlaintext());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class DataRetrievalViewHolder extends RecyclerView.ViewHolder{

        TextView dataText;

        public DataRetrievalViewHolder(@NonNull View itemView) {
            super(itemView);

            dataText = itemView.findViewById(R.id.DR_STR);
        }
    }
}
