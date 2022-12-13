package com.example.fyp5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

                } while (privateKey.compareTo(zero) == 0 || privateKey.compareTo(n) == 0 || privateKey.compareTo(n) == -1);

                BigInteger[] publicKeyXY = object.publicKeyGeneration(object.gPoint, privateKey);;

                for(int i =0; i<publicKeyXY.length; i++)
                {
                    System.out.println(publicKeyXY[i]);
                }




            }
            //if exists
            //nested loop


            // convert hex to decimal big int


            // make sure private key valid, pakai extended euclidean algo
            // kene check database, kalau key public key dh wujud, buat private key baru
            //kene letak loop dgn if dkt sini kalau lebih drpd 1157920892373161954235709850086879078532
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