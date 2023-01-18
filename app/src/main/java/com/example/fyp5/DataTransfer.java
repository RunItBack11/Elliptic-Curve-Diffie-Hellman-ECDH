package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
//import android.util.Base64;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.lang.Object;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Locale;

import java.util.Base64;
import org.apache.commons.codec.binary.Hex;
//import org.apache.commons.codec.binary.Base64;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DataTransfer extends AppCompatActivity {

    ImageButton imageButton;
    EditText editText;
    Button button1, button2;
    String string, ciphertextBase64;
    String stringKey;
    boolean time;
    String encodedKey;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_transfer);

        button1 = findViewById(R.id.DT_UPLOADBTN);
        button2 = findViewById(R.id.DT_ENDSESSION);
        editText = findViewById(R.id.DT_INPUT);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        String privKey = getIntent().getStringExtra("senderPrivKey");
        String pubKeyX = getIntent().getStringExtra("receiverPubKeyX");
        String pubKeyY = getIntent().getStringExtra("receiverPubKeyY");
        String secretKey = getIntent().getStringExtra("secretKey");
        String receiverId = getIntent().getStringExtra("receiverId");
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

                new CountDownTimer(300000, 300001) {
            @Override
            public void onTick(long l) {
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child("pubKey").child(pubKeyX).removeValue();
                        databaseReference.child("DataStatus").child(currentUserId+receiverId).child("state").setValue("completed");

                        Intent intent = new Intent(DataTransfer.this, MainActivity.class);
                        Toast.makeText(DataTransfer.this, "Session ended", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        cancel();
                    }
                });
            }

            @Override
            public void onFinish() {
                databaseReference.child("pubKey").child(pubKeyX).removeValue();
                databaseReference.child("DataStatus").child(currentUserId+receiverId).child("state").setValue("completed");

                Intent intent = new Intent(DataTransfer.this, MainActivity.class);
                Toast.makeText(DataTransfer.this, "Your time is up", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }.start();

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

        Toast.makeText(this, "You have 5 minutes to transfer data", Toast.LENGTH_SHORT).show();


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String input = editText.getText().toString().trim();

                if (input.isEmpty())
                {
                    editText.setError("Please enter a sentence before clicking encrypt sentence");
                }

                else
                {
                    editText.getText().clear();
                    byte[] decodedKey = Base64.getDecoder().decode(secretKey);
                    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                    System.out.println(originalKey);

                    try {
                        byte[] cipherText = encryptMsg(input, originalKey);
//                    string = new String(cipherText);
                        ciphertextBase64 = Base64.getEncoder().encodeToString(cipherText);
//                    System.out.println(string);
                        System.out.println("encrypted: " + encryptMsg(input, originalKey).toString());
                        System.out.println(ciphertextBase64);
                        string = new String(cipherText);
                        System.out.println(string);

//                   String string = new String(Base64.getDecoder().decode(ciphertextBase64));
//                   System.out.println(string);
//                   System.out.println(decryptMsg(Base64.getDecoder().decode(ciphertextBase64), originalKey));


                        databaseReference.child("Data").child(currentUserId + receiverId).child(String.valueOf(count)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                databaseReference.child("Data").child(currentUserId + receiverId).child(String.valueOf(count)).child("senderID").setValue(currentUserId);
                                databaseReference.child("Data").child(currentUserId + receiverId).child(String.valueOf(count)).child("receiverID").setValue(receiverId);
                                databaseReference.child("Data").child(currentUserId + receiverId).child(String.valueOf(count)).child("combination").setValue(currentUserId + receiverId);
                                databaseReference.child("Data").child(currentUserId + receiverId).child(String.valueOf(count)).child("sequence").setValue(String.valueOf(count));
                                databaseReference.child("Data").child(currentUserId + receiverId).child(String.valueOf(count)).child("data").setValue(ciphertextBase64);

                                count++;
                                Toast.makeText(DataTransfer.this, "Sentence successfully encrypted and sent", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //use count
                        //int count = 0
                        //count++;
                        //masukkan dlm database as string number: 1,2,3...
                        //other side: getchildren()
                        //add all numbers into an array
                        //masuk balik database, use the string number as the query and print all the

                        //other side:
                    } catch (NoSuchAlgorithmException e) {
                        System.out.print("invalid algo");
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        System.out.print("invalid padding");
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        System.out.print("invalid key");
                        e.printStackTrace();
                    } catch (InvalidParameterSpecException e) {
                        System.out.print("invalid param");
                        e.printStackTrace();
                    } catch (IllegalBlockSizeException e) {
                        System.out.print("invalid blocksize");
                        e.printStackTrace();
                    } catch (BadPaddingException e) {
                        System.out.print("bad padding");
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        System.out.print("oops");
                        e.printStackTrace();
                    } /*catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }*/

//                SecretKey key = null;
//                try {
//                    key = KeyGenerator.getInstance("AES").generateKey();
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//                if (key != null)
//                {
//                  stringKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
////                    encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
//                }
//                System.out.println(stringKey);
////                System.out.println(encodedKey);

                }
            }
        });


        }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 28){
//            if (resultCode == RESULT_OK){
//                if(data == null){
//                    return;
//                }else{
//                    Uri u = data.getData();
//                }
//            }
//        }
//    }

    public static byte[] encryptMsg(String message, SecretKey secret)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
    {
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secret);
        byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
        return cipherText;
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException
    {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secret);
        String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
        return decryptString;
    }

}