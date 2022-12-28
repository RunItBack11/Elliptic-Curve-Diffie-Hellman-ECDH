package com.example.fyp5;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Downloads extends AppCompatActivity {

    DatabaseReference databaseReference;
    String current_userId, pubKeyCheck, key1, combination1, state1, keyOutput, senderIDOutput;
    FirebaseAuth firebaseAuth;
    boolean exist = false;
    boolean repeat = false;
    BigInteger privateKey;
    ArrayList<String> keyArray = new ArrayList<>();
    final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    final BigInteger zero = new BigInteger("0", 10);
    ArrayList<String> keyArrayFilter = new ArrayList<>();
    ArrayList<String> senderIdArray = new ArrayList<>();
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        current_userId = firebaseAuth.getCurrentUser().getUid();



        BottomNavigationView bottomNavigationView = findViewById(R.id.DbottomNavigationView);
        TextView textView = findViewById(R.id.DOWNLOADS);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.HM:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    break;

                case R.id.DW:
                    break;

                case R.id.FR:
                    startActivity(new Intent(getApplicationContext(), Friends.class));
                    overridePendingTransition(0, 0);
                    break;
            }
            return true;
        });


        databaseReference.child("pubKey").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String key = ds.child("key").getValue().toString();
                    keyArray.add(key);
                    System.out.println(keyArray.get(i));
                    System.out.println(keyArray.size());
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getFilteredKeys(Callback callback)
    {
        for (int x = 0; x < keyArray.size(); x++) {
            int xInside = x;
            exist = false;

            databaseReference.child("pubKey").child(keyArray.get(x)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String receiverId = snapshot.child("receiverID").getValue().toString();
                    String state = snapshot.child("state").getValue().toString();
                    if (receiverId.equals(current_userId) && state.equals("sent")) {
                        keyArrayFilter.add(snapshot.child("key").getValue().toString());
                        senderIdArray.add(snapshot.child("senderID").getValue().toString());
                        System.out.println(keyArrayFilter.get(i));
                        System.out.println(senderIdArray.get(i));
                        System.out.println(keyArrayFilter.size());

                        try {
                            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

                            while (!exist) {
                                do {
                                    byte[] seed = secureRandom.generateSeed(32);
                                    System.out.println(Arrays.toString(seed));
                                    // generate seed

                                    secureRandom.nextBytes(seed);
                                    System.out.println(Arrays.toString(seed));

                                    KeyGeneration object = new KeyGeneration();
                                    String hex = object.convertBytesToHex(seed);
                                    System.out.println(hex);

                                    EllipticCurveFramework object2 = new EllipticCurveFramework();
                                    privateKey = object2.HextoBinary(hex);
                                    System.out.println(privateKey);

                                    repeat = object.Conditions(privateKey, n, zero, repeat, getApplicationContext());
                                } while (!repeat);

                                exist = true;
                                EllipticCurveFramework object3 = new EllipticCurveFramework();
                                BigInteger[] publicKeyXY = object3.publicKeyGeneration(object3.gPoint, privateKey);

                                for (int i = 0; i < 2; i++) {
                                    System.out.println(publicKeyXY[i]);
                                }

                                databaseReference.child("pubKey").equalTo(publicKeyXY[0].toString()).addValueEventListener(new ValueEventListener() {
                                    @Override public void onDataChange(@NonNull DataSnapshot snapshot)
                                    {databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("senderID").setValue(current_userId);
                                        databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("receiverID").setValue(senderIdArray.get(xInside));
                                        databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("state").setValue("sent");
                                        databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("key").setValue(publicKeyXY[0].toString());
                                        databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("combination").setValue(current_userId + senderIDOutput);
                                        Toast.makeText(Downloads.this, "Key has been created", Toast.LENGTH_SHORT).show();pubKeyCheck = publicKeyXY[0].toString();}
                                    @Override public void onCancelled(@NonNull DatabaseError error) {
                                    }});
                                callback.onCallback(senderIdArray);
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }

                        i++;


                    }

                    else
                    {
                        System.out.println("this key is not yours");
                        System.out.println(keyArrayFilter.size());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private interface Callback
    {
        void onCallback(ArrayList<String> list);
    }






}




//        databaseReference.child("pubKey").child().addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        String receiverId = ds.child("receiverID").getValue().toString();
//                        String senderId = ds.child("senderID").getValue().toString();
//                        String state = ds.child("state").getValue().toString();
//                        String key = ds.child("key").getValue().toString();
//
//                        if (receiverId.equals(current_userId))
//                        {
//                            if (state.equals("sent")) {
//                                keyArray.add(key);
//                                senderIDArray.add(senderId);
//                                keyOutput = keyArray.get(keyArray.size() - 1);
//                                senderIDOutput = senderIDArray.get(senderIDArray.size() - 1);
//                                databaseReference.child("pubKey").child(keyOutput).child("state").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        System.out.println(keyOutput);
//                                        System.out.println(senderIDOutput);
//                                        System.out.println("state value updated");
//
//                                        try {
//                                            SecureRandom secureRandom =  SecureRandom.getInstance("SHA1PRNG");
//
//                                            while(!exist)
//                                            {
//                                                do{
//                                                    byte[] seed = secureRandom.generateSeed(32);
//                                                    System.out.println(Arrays.toString(seed));
//                                                    // generate seed
//
//                                                    secureRandom.nextBytes(seed);
//                                                    System.out.println(Arrays.toString(seed));
//
//                                                    KeyGeneration object = new KeyGeneration();
//                                                    String hex = object.convertBytesToHex(seed);
//                                                    System.out.println(hex);
//
//                                                    EllipticCurveFramework object2 = new EllipticCurveFramework();
//                                                    privateKey = object2.HextoBinary(hex);
//                                                    System.out.println(privateKey);
//
//                                                    repeat = object.Conditions(privateKey, n, zero, repeat, getApplicationContext());
//
//                                                }while(!repeat);
//
//                                                exist = true;
//                                                EllipticCurveFramework object3 = new EllipticCurveFramework();
//                                                BigInteger[] publicKeyXY = object3.publicKeyGeneration(object3.gPoint, privateKey);
//                                                for(int i=0; i<2; i++)
//                                                {
//                                                    System.out.println(publicKeyXY[i]);
//                                                }
//
//                                                databaseReference.orderByChild("pubKey").equalTo(publicKeyXY[0].toString()).addValueEventListener(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        if(!snapshot.exists())
//                                                        {
//                                                            databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("senderID").setValue(current_userId);
//                                                            databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("receiverID").setValue(senderIDOutput);
//                                                            databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("state").setValue("sent");
//                                                            databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("key").setValue(publicKeyXY[0].toString());
//                                                            databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("combination").setValue(current_userId+senderIDOutput);
//
//                                                            Toast.makeText(Downloads.this, "Key has been created", Toast.LENGTH_SHORT).show();
//                                                            pubKeyCheck = publicKeyXY[0].toString();
//
//                                                            new CountDownTimer(30000,1000) {
//                                                                @Override
//                                                                public void onTick(long l) {
//
//                                                                    textView.setText("Transporting Key...");
//
//                                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("pubKey").child(pubKeyCheck);
//
//                                                                    ref.addValueEventListener(new ValueEventListener() {
//                                                                        @Override
//                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                            key1 = snapshot.child("key").getValue(String.class);
//                                                                            state1 = snapshot.child("state").getValue(String.class);
//                                                                            combination1 = snapshot.child("combination").getValue(String.class);
//
//                                                                            if(key1.equals(pubKeyCheck) && combination1.equals(current_userId+senderIDOutput) && state1.equals("received"))
//                                                                            {
//                                                                                Toast.makeText(Downloads.this, "dh jadi", Toast.LENGTH_SHORT).show();
//
//                                                                                cancel();
//                                                                            }
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                        }
//                                                                    });
//                                                                }
//
//                                                                @Override
//                                                                public void onFinish() {
//
//                                                                    textView.setText("downloads");
//
//                                                                    databaseReference.orderByChild("pubKey").equalTo(pubKeyCheck).addValueEventListener(new ValueEventListener() {
//                                                                        @Override
//                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                                            databaseReference.child("pubKey").child(pubKeyCheck).removeValue(new DatabaseReference.CompletionListener() {
//                                                                                @Override
//                                                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                                                                                    Toast.makeText(Downloads.this, "Key was not transferred", Toast.LENGTH_SHORT).show();
//                                                                                }
//                                                                            });
//                                                                        }
//
//                                                                        @Override
//                                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                        }
//                                                                    });
//                                                                }
//                                                            }.start();
//
//                                                            exist = true;
//
//                                                        }
//                                                        else
//                                                        {
//                                                            Toast.makeText(Downloads.this, "public key already exists", Toast.LENGTH_SHORT).show();
//                                                            //kene buang bnde yg dh send drpd belah A
//                                                            exist = false;
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//
//                                            }
//
//
//                                        } catch (NoSuchAlgorithmException e) {
//                                            e.printStackTrace();
//                                        }
////                                    }
//                                });
//                            }
//
//                            else if(state.equals("received"))
//                            {
//                                System.out.println("There are no incoming keys");
//                            }
//                        }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//        int pid = android.os.Process.myPid();
//        String whiteList = "logcat -P '" + pid + "'";
//        try {
//            Runtime.getRuntime().exec(whiteList).waitFor();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }





