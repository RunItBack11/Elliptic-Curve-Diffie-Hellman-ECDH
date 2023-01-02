package com.example.fyp5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.math.BigInteger;

public class DataRetrieval extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrieval);

        String privKey = getIntent().getStringExtra("senderPrivKey");
        String pubKeyX = getIntent().getStringExtra("receiverPubKeyX");
        String pubKeyY = getIntent().getStringExtra("receiverPubKeyY");

        System.out.println(privKey);
        System.out.println(pubKeyX);
        System.out.println(pubKeyY);

        BigInteger x = new BigInteger(pubKeyX,10);
        BigInteger y = new BigInteger(pubKeyY,10);
        BigInteger privateKey = new BigInteger(privKey,10);

        BigInteger[] pubKeyXY = {x,y};

        for(int i=0;i<pubKeyXY.length;i++)
        {
            System.out.println(pubKeyXY[i]);
        }

        EllipticCurveFramework object = new EllipticCurveFramework();
        BigInteger[] output = object.publicKeyGeneration(pubKeyXY, privateKey);

        for(int i=0;i<output.length;i++)
        {
            System.out.println(output[i]);
        }

    }
}