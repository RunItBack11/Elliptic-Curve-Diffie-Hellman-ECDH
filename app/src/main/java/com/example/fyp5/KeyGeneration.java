package com.example.fyp5;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import org.apache.commons.codec.binary.Base64;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class KeyGeneration extends AppCompatActivity {

    String other_userId, current_userId, pubKeyCheck, pubKeyCheck2, combination, key1, key2, combination1, state1, combi, receiverKey, privKeyTransfer, pubKey2, s, key;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    TextView textView, countDown, textView2;
    Button transferData;
    ImageView keyIcon;
    BigInteger privateKey;
    Intent intent;
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
        textView2  = findViewById(R.id.KG_SUBTEXT);
        countDown = findViewById(R.id.KG_TEXT2);
        transferData.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        countDown.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();
        current_userId = firebaseAuth.getCurrentUser().getUid();
        other_userId = getIntent().getStringExtra("UserId");
        combination = current_userId+other_userId;


        final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        final BigInteger zero = new BigInteger("0", 10);
        ArrayList<String> combinationArray = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        keyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long startTime = System.nanoTime();
                databaseReference.child("DataStatus").child(current_userId+other_userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists())
                        {
                            Toast.makeText(KeyGeneration.this, "The other person has not yet ended their session, please try again later.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
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
                                    long endTime = System.nanoTime();
                                    long duration = (endTime - startTime)/1000000;
                                    System.out.println("Time in Milliseconds: " + duration);
                                    for(int i=0; i<2; i++)
                                    {
                                        System.out.println(publicKeyXY[i]);
                                    }

                                    privKeyTransfer = privateKey.toString();
                                    pubKeyCheck2 = publicKeyXY[1].toString();

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
                                                databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("keyX").setValue(publicKeyXY[0].toString());
                                                databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("keyY").setValue(publicKeyXY[1].toString());
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

                                                        DatabaseReference keyX = ref.child("pubKey").child(pubKeyCheck).child("keyX");
                                                        DatabaseReference keyY = ref.child("pubKey").child(pubKeyCheck).child("keyY");
                                                        DatabaseReference state = ref.child("pubKey").child(pubKeyCheck).child("state");
                                                        DatabaseReference combinationn = ref.child("pubKey").child(pubKeyCheck).child("combination");


                                                        keyX.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                key1 = snapshot.getValue(String.class);

                                                                keyY.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        key2 = snapshot.getValue(String.class);

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
                                                                                            databaseReference.child("pubKey").addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                    for(DataSnapshot ds : snapshot.getChildren()) {
                                                                                                        combi = ds.child("combination").getValue().toString();
                                                                                                        combinationArray.add(combi);
                                                                                                    }

                                                                                                    if(combinationArray.contains(other_userId+current_userId))
                                                                                                    {
                                                                                                        databaseReference.child("pubKey").orderByChild("combination").equalTo(other_userId+current_userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                            @Override
                                                                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                                for(DataSnapshot ds : snapshot.getChildren()) {
                                                                                                                    receiverKey = ds.getKey();
                                                                                                                    System.out.println(receiverKey);
                                                                                                                }

                                                                                                                if(receiverKey != null)
                                                                                                                {
                                                                                                                    databaseReference.child("pubKey").child(receiverKey).child("state").setValue("completed");

                                                                                                                    keyIcon.setVisibility(View.INVISIBLE);
                                                                                                                    textView.setText("Press the button to \n transfer data");
                                                                                                                    textView.setVisibility(View.VISIBLE);
                                                                                                                    textView2.setVisibility(View.INVISIBLE);
                                                                                                                    transferData.setVisibility(View.VISIBLE);
                                                                                                                    countDown.setVisibility(View.INVISIBLE);
                                                                                                                    exist = true;
                                                                                                                    Toast.makeText(KeyGeneration.this, "Key successfully transferred", Toast.LENGTH_SHORT).show();
                                                                                                                    cancel();
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
                                                                                        else if(key1.equals(pubKeyCheck) && combination1.equals(current_userId+other_userId) && state1.equals("declined"))
                                                                                        {
                                                                                            databaseReference.child("pubKey").child(pubKeyCheck).removeValue(new DatabaseReference.CompletionListener() {
                                                                                                @Override
                                                                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                                                                    Toast.makeText(KeyGeneration.this, "Key transfer was declined", Toast.LENGTH_SHORT).show();
                                                                                                    exist = false;
                                                                                                    keyIcon.setVisibility(View.VISIBLE);
                                                                                                    textView.setVisibility(View.VISIBLE);
                                                                                                    transferData.setVisibility(View.INVISIBLE);
                                                                                                    countDown.setVisibility(View.INVISIBLE);
                                                                                                    cancel();
                                                                                                }
                                                                                            });
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

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
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
                                                                        exist = false;
                                                                    }
                                                                });
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });

                                                        keyIcon.setVisibility(View.VISIBLE);
                                                        textView.setVisibility(View.VISIBLE);
                                                        transferData.setVisibility(View.INVISIBLE);
                                                        countDown.setVisibility(View.INVISIBLE);

                                                    }
                                                }.start();


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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        transferData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child("pubKey").child(receiverKey).child("keyY").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        pubKey2 = snapshot.getValue(String.class);

                        BigInteger x = new BigInteger(receiverKey,10);
                        BigInteger y = new BigInteger(pubKey2,10);
                        BigInteger privateKey = new BigInteger(privKeyTransfer,10);

                        BigInteger[] pubKeyXY = {x,y};
                        EllipticCurveFramework object = new EllipticCurveFramework();
                        BigInteger[] output = object.publicKeyGeneration(pubKeyXY, privateKey);

                        String value = new BigInteger(output[0].toString()
                                , 10)
                                .toString(16)
                                .toUpperCase();


                        System.out.println("hexadecimal: " + value);

                        BigInteger bigint = new BigInteger(value, 16);

                        StringBuilder sb = new StringBuilder();
                        byte[] ba = Base64.encodeInteger(bigint);
                        for (byte b : ba) {
                            sb.append((char)b);
                        }

                        s = sb.toString();
                        System.out.println(s);

                        intent= new Intent(KeyGeneration.this, DataTransfer.class);
                        intent.putExtra("senderPrivKey", privKeyTransfer);
                        intent.putExtra("senderPubKeyX", pubKeyCheck);
                        intent.putExtra("receiverPubKeyX", receiverKey);
                        intent.putExtra("receiverPubKeyY", pubKey2);
                        intent.putExtra("secretKey", s);
                        intent.putExtra("receiverId", other_userId);

                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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