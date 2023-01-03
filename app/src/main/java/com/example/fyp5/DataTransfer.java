package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.Object;

import java.math.BigInteger;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import java.util.Base64;
import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.binary.Base64;


import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DataTransfer extends AppCompatActivity implements Key{

    ImageButton imageButton;
    TextView textView1, textView2;
    Button button;
    String str;
    String stringKey;
    boolean time;
    SecretKey secretKey;
    String encodedKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        imageButton = findViewById(R.id.DT_UPLOAD);
        textView1 = findViewById(R.id.DT_TEXT1);
        textView2 = findViewById(R.id.DT_TEXT2);
        button = findViewById(R.id.DT_UPLOADBTN);

        textView2.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);


        try {
            secretKey = KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String privKey = getIntent().getStringExtra("senderPrivKey");
        String pubKeyX = getIntent().getStringExtra("receiverPubKeyX");
        String pubKeyY = getIntent().getStringExtra("receiverPubKeyY");

        System.out.println(privKey);
        System.out.println(pubKeyX);
        System.out.println(pubKeyY);

        BigInteger x = new BigInteger(pubKeyX, 10);
        BigInteger y = new BigInteger(pubKeyY, 10);
        BigInteger privateKey = new BigInteger(privKey, 10);

        BigInteger[] pubKeyXY = {x, y};

        for (int i = 0; i < pubKeyXY.length; i++) {
            System.out.println(pubKeyXY[i]);
        }

        EllipticCurveFramework object = new EllipticCurveFramework();
        BigInteger[] output = object.publicKeyGeneration(pubKeyXY, privateKey);

        for (int i = 0; i < output.length; i++) {
            System.out.println(output[i]);
        }

        String value = new BigInteger(output[0].toString()
                , 10)
                .toString(16)
                .toUpperCase();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        Toast.makeText(this, "You have 5 minutes to transfer files", Toast.LENGTH_SHORT).show();


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                        String hexadecimal = value;
//                        System.out.println("hexadecimal: " + hexadecimal);
//
//                        BigInteger bigint = new BigInteger(hexadecimal, 16);
//
//                        StringBuilder sb = new StringBuilder();
//                        byte[] ba = Base64.encodeInteger(bigint);
//                        for (byte b : ba) {
//                            sb.append((char)b);
//                        }
//                        String s = sb.toString();
//                        System.out.println("base64: " + s);
//                        System.out.println("encoded: " + Base64.isBase64(s));


                SecretKey key = null;
                try {
                    key = KeyGenerator.getInstance("AES").generateKey();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (key != null)
                {
//                    stringKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
                    encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
                }
//                System.out.println(stringKey);
                System.out.println(encodedKey);

            }
        });

//        new CountDownTimer(10000, 1000) {
//            @Override
//            public void onTick(long l) {
//                Toast.makeText(DataTransfer.this, "The clock is running", Toast.LENGTH_SHORT).show();
//                //technically ade problem sbb x accurate on tick (after 5 ticks dia run on finish punya command tpi still sambung ontick)
//            }
//
//            @Override
//            public void onFinish() {
//                Intent intent = new Intent(DataTransfer.this, TransferDataFriendsList.class);
//                Toast.makeText(DataTransfer.this, "Your time is up", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
//            }
//        }.start();

        }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 28){
            if (resultCode == RESULT_OK){
                if(data == null){
                    return;
                }else{
                    Uri u = data.getData();
                }
            }
        }
    }


    public void chooseFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent,28);
    }

    @Override
    public String getAlgorithm() {
        return null;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public byte[] getEncoded() {
        return new byte[0];
    }
}