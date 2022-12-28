package com.example.fyp5;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

public class KeyGeneration extends AppCompatActivity {

    String other_userId, current_userId, pubKeyCheck, combination, key1, combination1, state1;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    TextView textView, countDown;
    Button transferData;
    ImageView keyIcon;
    BigInteger privateKey;
    EllipticCurveFramework object = new EllipticCurveFramework();
    boolean exist;
    boolean repeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_generation);

        keyIcon = findViewById(R.id.KG_KEYICON);
        transferData = findViewById(R.id.KG_TDBTN);
        textView = findViewById(R.id.KG_TEXT);
        countDown = findViewById(R.id.KG_SUBTEXT);
        countDown.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        current_userId = firebaseAuth.getCurrentUser().getUid();
        other_userId = getIntent().getStringExtra("UserId");
        combination = current_userId+other_userId;


        final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        final BigInteger zero = new BigInteger("0", 10);
        Toast.makeText(this, other_userId, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, current_userId, Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        keyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    SecureRandom secureRandom =  SecureRandom.getInstance("SHA1PRNG");

                    while(exist == false) {
                        do {
                            byte[] seed = secureRandom.generateSeed(32);
                            System.out.println(Arrays.toString(seed));
                            // generate seed

                            secureRandom.nextBytes(seed);
                            System.out.println(Arrays.toString(seed));
                            // go into PRNG

                            String hex = convertBytesToHex(seed);
                            System.out.println(hex);
                            // convert from seed to Hex

                            privateKey = object.HextoBinary(hex);
                            System.out.println(privateKey);

                            repeat = Conditions(privateKey, n, zero, repeat, getApplicationContext());

                        } while (repeat == false);

                        exist = true;
                        BigInteger[] publicKeyXY = object.publicKeyGeneration(object.gPoint, privateKey);
                        for(int i=0; i<2; i++)
                        {
                            System.out.println(publicKeyXY[i]);
                        }

                        databaseReference.orderByChild("pubKey").equalTo(publicKeyXY[0].toString()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    Toast.makeText(KeyGeneration.this, "public key already exists", Toast.LENGTH_SHORT).show();
                                    exist = false;
                                }
                                else
                                {
                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("senderID").setValue(current_userId);
                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("receiverID").setValue(other_userId);
                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("state").setValue("sent");
                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("key").setValue(publicKeyXY[0].toString());
                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("combination").setValue(combination);

                                    Toast.makeText(KeyGeneration.this, "Key has been created", Toast.LENGTH_SHORT).show();
                                    pubKeyCheck = publicKeyXY[0].toString();

                                    new CountDownTimer(30000,1000) {
                                        @Override
                                        public void onTick(long l) {

                                            keyIcon.setVisibility(View.INVISIBLE);
                                            textView.setVisibility(View.INVISIBLE);
                                            transferData.setVisibility(View.INVISIBLE);
                                            countDown.setVisibility(View.VISIBLE);

                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                                            DatabaseReference key = ref.child("pubKey").child(pubKeyCheck).child("key");
                                            DatabaseReference state = ref.child("pubKey").child(pubKeyCheck).child("state");
                                            DatabaseReference combinationn = ref.child("pubKey").child(pubKeyCheck).child("combination");


                                            key.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    key1 = snapshot.getValue(String.class);
//                                                    Toast.makeText(KeyGeneration.this, pubKeyCheck, Toast.LENGTH_SHORT).show();

                                                    state.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            state1 = snapshot.getValue(String.class);

                                                            combinationn.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    combination1 = snapshot.getValue(String.class);

                                                                    if(key1.equals(pubKeyCheck) && combination1.equals(current_userId+other_userId) && state1.equals("received"))
                                                                    {
                                                                        Toast.makeText(KeyGeneration.this, "dh jadi", Toast.LENGTH_SHORT).show();
                                                                        // tambah buat apa after received
                                                                        keyIcon.setVisibility(View.VISIBLE);
                                                                        textView.setVisibility(View.VISIBLE);
                                                                        transferData.setVisibility(View.VISIBLE);
                                                                        countDown.setVisibility(View.INVISIBLE);
                                                                        cancel();
                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

//                            combinationn.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    combination1 = snapshot.getValue(String.class);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
//
//                            state.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    state1 = snapshot.getValue(String.class);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });



                                        }

                                        @Override
                                        public void onFinish() {

                                            databaseReference.orderByChild("pubKey").equalTo(pubKeyCheck).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    databaseReference.child("pubKey").child(pubKeyCheck).removeValue(new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                            Toast.makeText(KeyGeneration.this, "Key was not transferred", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            keyIcon.setVisibility(View.VISIBLE);
                                            textView.setVisibility(View.VISIBLE);
                                            transferData.setVisibility(View.VISIBLE);
                                            countDown.setVisibility(View.INVISIBLE);

                                        }
                                    }.start();

                                    exist = true;



                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }






                }

                catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    //if exists
    //nested loop


    // convert hex to decimal big int

    // kene check database, kalau key public key dh wujud, buat private key baru
    // 69984665640564039457584007913129639935, kene try balik smpai
    // dpt value smaller than nombor atas but bigger than 0 (kene check nk kene panjang 256 jugak ke?

    public static String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }

    public static boolean Conditions(BigInteger privateKey, BigInteger n, BigInteger zero, boolean repeat, Context context)
    {
        if(privateKey.compareTo(zero) == 0)
        {
            repeat = false;
            Toast.makeText(context, "Key = 0, repeat", Toast.LENGTH_SHORT).show();
        }
        else if(privateKey.compareTo(zero) < 0)
        {
            repeat = false;
            Toast.makeText(context, "Key < 0, repeat", Toast.LENGTH_SHORT).show();
        }
        else if(privateKey.compareTo(n) == 0)
        {
            repeat = false;
            Toast.makeText(context, "Key = n, repeat", Toast.LENGTH_SHORT).show();
        }
        else if(privateKey.compareTo(n) > 0)
        {
            repeat = false;
            Toast.makeText(context, "Key > n, repeat", Toast.LENGTH_SHORT).show();
        }
        else if(privateKey.compareTo(zero) > 0)
        {
            if(privateKey.compareTo(n) < 0)
            {
                Toast.makeText(context, "Key is valid, proceed", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Key is corrupted, Please try again", Toast.LENGTH_SHORT).show();
            }
            repeat = true;
        }
        else
        {
            Toast.makeText(context, "Key is corrupted, Please try again", Toast.LENGTH_SHORT).show();
            repeat = true;
        }
        return repeat;
    }

}