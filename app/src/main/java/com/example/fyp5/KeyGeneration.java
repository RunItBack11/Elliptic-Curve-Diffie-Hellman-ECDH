package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Locale;

public class KeyGeneration extends AppCompatActivity {

    String other_userId, current_userId;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    BigInteger privateKey;
    EllipticCurveFramework object = new EllipticCurveFramework();
    boolean exist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_generation);

        firebaseAuth = FirebaseAuth.getInstance();
        current_userId = firebaseAuth.getCurrentUser().getUid();
        other_userId = getIntent().getStringExtra("UserId");

        final BigInteger n = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
        final BigInteger zero = new BigInteger("0000000000000000000000000000000000000000000000000000000000000000", 16);
        Toast.makeText(this, other_userId, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, current_userId, Toast.LENGTH_SHORT).show();

        databaseReference = FirebaseDatabase.getInstance().getReference();

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

                } while (privateKey.compareTo(zero) == 0 || privateKey.compareTo(n) == 0 || privateKey.compareTo(n) == 1);
//                wrong calculation somewhere, output different from python
                BigInteger[] publicKeyXY = object.publicKeyGeneration(object.gPoint, privateKey);
                for(int i=0; i<2; i++)
                {
                    System.out.println(publicKeyXY[i]);
                }

                exist = true;

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

                            Toast.makeText(KeyGeneration.this, "database updated", Toast.LENGTH_SHORT).show();
                            exist = true;

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            //if exists
            //nested loop


            // convert hex to decimal big int

            // kene check database, kalau key public key dh wujud, buat private key baru
            // 69984665640564039457584007913129639935, kene try balik smpai
            // dpt value smaller than nombor atas but bigger than 0 (kene check nk kene panjang 256 jugak ke?


        }

        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


    }

    private static String convertBytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte temp : bytes) {
            result.append(String.format("%02x", temp));
        }
        return result.toString();
    }

}