package com.example.fyp5;

import static com.example.fyp5.KeyGeneration.Conditions;
import static com.example.fyp5.KeyGeneration.convertBytesToHex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

public class KeyRetrieval extends AppCompatActivity {

    DatabaseReference databaseReference;
    String current_userId, other_userId, key, combi, pubKeyCheck;
    FirebaseAuth firebaseAuth;
    Button downloadFiles;
    TextView textView1, subtext;
    ImageView imageView;
    BigInteger privateKey;
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
        subtext = findViewById(R.id.D_SUBTEXT);
        imageView = findViewById(R.id.D_KEYICON);

        subtext.setVisibility(View.INVISIBLE);
        downloadFiles.setVisibility(View.INVISIBLE);
        //error utk yg x de send key so value retrieved is null

        Query query = databaseReference.child("pubKey").orderByChild("combination").equalTo(other_userId+current_userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    key = ds.getKey();
                    System.out.println(key);
                }

                databaseReference.child("pubKey").child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String state = snapshot.child("state").getValue(String.class);
                        System.out.println(state);
                        if(state.equals("sent"))
                        {
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

                                    }while(repeat == false);

                                    exist = true;

                                    EllipticCurveFramework keyGen = new EllipticCurveFramework();
                                    BigInteger[] publicKeyXY = keyGen.publicKeyGeneration(keyGen.gPoint, privateKey);

                                    for(int i=0; i<2; i++)
                                    {
                                        System.out.println(publicKeyXY[i]);
                                    }

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
                                                databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("key").setValue(publicKeyXY[0].toString());
                                                databaseReference.child("pubKey").child(publicKeyXY[0].toString()).child("combination").setValue(current_userId+other_userId);

                                                Toast.makeText(KeyRetrieval.this, "Key has been created", Toast.LENGTH_SHORT).show();
                                                pubKeyCheck = publicKeyXY[0].toString();


                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    subtext.setVisibility(View.VISIBLE);
                                    imageView.setVisibility(View.INVISIBLE);
                                    downloadFiles.setVisibility(View.VISIBLE);
                                    textView1.setText("Press the button to \n download your files");

                                }



                            }

                            catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
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











    }
}