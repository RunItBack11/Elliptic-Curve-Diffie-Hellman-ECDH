package com.example.fyp5;

import static com.example.fyp5.KeyGeneration.Conditions;
import static com.example.fyp5.KeyGeneration.convertBytesToHex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public class KeyRetrieval extends AppCompatActivity {

    DatabaseReference databaseReference;
    String current_userId, other_userId, key, combi, pubKeyCheck, key1, key2,  state1, combination1, pubKey2, privKey, s, status;
    FirebaseAuth firebaseAuth;
    Button downloadFiles;
    TextView textView1, textView2, textView3, subtext;
    ImageView imageView;
    BigInteger privateKey;
    Intent intent;
    boolean exist;
    boolean repeat;

//    "Press the Icon to \n to retrieve key"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_retrieval);

        firebaseAuth = FirebaseAuth.getInstance();
        current_userId = firebaseAuth.getCurrentUser().getUid();
        other_userId = getIntent().getStringExtra("UserId");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        ArrayList<String> combination = new ArrayList<>();
        final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        final BigInteger zero = new BigInteger("0", 10);

        downloadFiles = findViewById(R.id.D_DOWNLOADBTN);
        textView1 = findViewById(R.id.D_TEXT1);
        textView2 = findViewById(R.id.D_TEXT2);
        textView3 = findViewById(R.id.D_TEXT3);
        subtext = findViewById(R.id.D_SUBTEXT);
        imageView = findViewById(R.id.D_KEYICON);

        textView3.setVisibility(View.INVISIBLE);
        subtext.setVisibility(View.INVISIBLE);
        downloadFiles.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        //error utk yg x de send key so value retrieved is null

        Query query = databaseReference.child("pubKey").orderByChild("combination").equalTo(other_userId+current_userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    key = ds.getKey();
                    System.out.println(key);
                }

                databaseReference.child("pubKey").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String state = snapshot.child("state").getValue(String.class);
                            System.out.println(state);
                            if(state.equals("sent"))
                            {
                                databaseReference.child("pubKey").child(key).child("state").setValue("received");

                                try
                                {
                                    SecureRandom secureRandom =  SecureRandom.getInstance("SHA1PRNG");

                                    while(!exist)
                                    {
                                        do
                                        {
                                            byte[] seed = secureRandom.generateSeed(32);
                                            System.out.println(Arrays.toString(seed));
                                            // generate seed

                                            secureRandom.nextBytes(seed);
                                            System.out.println(Arrays.toString(seed));
                                            // go into PRNG

                                            String hex = convertBytesToHex(seed);
                                            System.out.println(hex);
                                            // convert from seed to Hex

                                            EllipticCurveFramework object = new EllipticCurveFramework();
                                            privateKey = object.HextoBinary(hex);
                                            System.out.println(privateKey);

                                            repeat = Conditions(privateKey, n, zero, repeat, getApplicationContext());

                                        }while(!repeat);

                                        exist = true;

                                        EllipticCurveFramework keyGen = new EllipticCurveFramework();
                                        BigInteger[] publicKeyXY = keyGen.publicKeyGeneration(keyGen.gPoint, privateKey);

                                        for(int i=0; i<2; i++)
                                        {
                                            System.out.println(publicKeyXY[i]);
                                        }

                                        privKey = privateKey.toString();

                                        databaseReference.orderByChild("pubKey").equalTo(publicKeyXY[0].toString()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                if(snapshot.exists())
                                                {
                                                    Toast.makeText(KeyRetrieval.this, "public key already exists", Toast.LENGTH_SHORT).show();
                                                    exist = false;
                                                }
                                                else
                                                {
                                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("senderID").setValue(current_userId);
                                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("receiverID").setValue(other_userId);
                                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("state").setValue("sent");
                                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("keyX").setValue(publicKeyXY[0].toString());
                                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("keyY").setValue(publicKeyXY[1].toString());
                                                    databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("combination").setValue(current_userId+other_userId);

                                                    Toast.makeText(KeyRetrieval.this, "Key has been created", Toast.LENGTH_SHORT);
                                                    pubKeyCheck = publicKeyXY[0].toString();

                                                    new CountDownTimer(30000,1000) {
                                                        @Override
                                                        public void onTick(long l) {

                                                            downloadFiles.setVisibility(View.INVISIBLE);
                                                            textView1.setVisibility(View.INVISIBLE);
                                                            subtext.setVisibility(View.INVISIBLE);
                                                            imageView.setVisibility(View.INVISIBLE);
                                                            textView2.setVisibility(View.VISIBLE);

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

                                                                                            if(key1.equals(pubKeyCheck) && combination1.equals(current_userId+other_userId) && state1.equals("completed"))
                                                                                            {
                                                                                                Toast.makeText(KeyRetrieval.this, "Key successfully retrieved", Toast.LENGTH_SHORT).show();
                                                                                                // tambah buat apa after received

                                                                                                downloadFiles.setVisibility(View.INVISIBLE);
                                                                                                textView1.setVisibility(View.INVISIBLE);
                                                                                                subtext.setVisibility(View.INVISIBLE);
                                                                                                imageView.setVisibility(View.INVISIBLE);
                                                                                                textView2.setVisibility(View.INVISIBLE);
                                                                                                textView3.setVisibility(View.VISIBLE);

                                                                                                databaseReference.child("DataStatus").child(other_userId+current_userId).child("state").addValueEventListener(new ValueEventListener() {
                                                                                                    @Override
                                                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                                                        if(snapshot.exists())
                                                                                                        {
                                                                                                            status = snapshot.getValue(String.class);
                                                                                                            if(status.equals("completed"))
                                                                                                            {
                                                                                                                downloadFiles.setVisibility(View.VISIBLE);
                                                                                                                textView1.setText("Press the button to \n download data");
                                                                                                                textView1.setVisibility(View.VISIBLE);
                                                                                                                subtext.setVisibility(View.INVISIBLE);
                                                                                                                imageView.setVisibility(View.INVISIBLE);
                                                                                                                textView2.setVisibility(View.INVISIBLE);
                                                                                                                textView3.setVisibility(View.INVISIBLE);
                                                                                                            }
                                                                                                            else
                                                                                                            {
                                                                                                                Toast.makeText(KeyRetrieval.this, "Data not ready yet", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                                                    }
                                                                                                });

                                                                                                exist = true;
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
                                                                            databaseReference.child("pubKey").child(key).removeValue();
                                                                            exist = false;
                                                                            Toast.makeText(KeyRetrieval.this, "Key was not fetched", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });

                                                            downloadFiles.setVisibility(View.INVISIBLE);
                                                            textView1.setVisibility(View.VISIBLE);
                                                            subtext.setVisibility(View.INVISIBLE);
                                                            imageView.setVisibility(View.VISIBLE);
                                                            textView2.setVisibility(View.INVISIBLE);

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
                        else
                        {
                            Toast.makeText(KeyRetrieval.this, "There are no incoming keys from this person", Toast.LENGTH_SHORT).show();
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
        };

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child("pubKey").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            combi = ds.child("combination").getValue().toString();
                            combination.add(combi);
                        }

                        for(int i=0; i<combination.size(); i++)
                        {
                            System.out.println(combination.get(i));
                        }

                        if(combination.contains(other_userId+current_userId))
                        {
                            query.addListenerForSingleValueEvent(valueEventListener);
                        }
                        else
                        {
                            Toast.makeText(KeyRetrieval.this, "There are no incoming keys", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        downloadFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                databaseReference.child("pubKey").child(key).child("keyY").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        pubKey2 = snapshot.getValue(String.class);

                        BigInteger x = new BigInteger(key,10);
                        BigInteger y = new BigInteger(pubKey2,10);
                        System.out.println(y);
                        BigInteger privateKey = new BigInteger(privKey,10);

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

                        intent= new Intent(KeyRetrieval.this, DataRetrieval.class);
                        intent.putExtra("senderPrivKey", privKey);
                        intent.putExtra("receiverPubKeyX", key);
                        intent.putExtra("receiverPubKeyY", pubKey2);
                        intent.putExtra("receiverId", other_userId);
                        intent.putExtra("secretKey", s);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });











    }
}