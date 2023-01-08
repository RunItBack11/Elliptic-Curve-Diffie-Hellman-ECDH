package com.example.fyp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
//import org.apache.commons.codec.binary.Base64;
import java.util.Base64;
//import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DataRetrieval extends AppCompatActivity{

    RecyclerView recyclerView;
    TextView textView, textView2;
    DatabaseReference databaseReference, mRef;
    DataRetrievalAdapter dataRetrievalAdapter;
    FirebaseAuth firebaseAuth;
    SecretKey originalKey;
    byte[] ciphertextByte;
    Button button;
    String currentUserdId,receiverId;
    ArrayList<DataRetrievalCardViewInput> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_retrieval);

        button = findViewById(R.id.DR_ENDSESSION);
        textView = findViewById(R.id.DR_TV);

        databaseReference = FirebaseDatabase.getInstance().getReference("Data");
        mRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserdId = firebaseAuth.getCurrentUser().getUid();

        String privKey = getIntent().getStringExtra("senderPrivKey");
        String pubKeyX = getIntent().getStringExtra("receiverPubKeyX");
        String pubKeyY = getIntent().getStringExtra("receiverPubKeyY");
        String secretKey = getIntent().getStringExtra("secretKey");
        receiverId= getIntent().getStringExtra("receiverId");

        System.out.println(privKey);
        System.out.println(pubKeyX);
        System.out.println(pubKeyY);

        databaseReference.child(receiverId+currentUserdId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        DataRetrievalCardViewInput data = ds.getValue(DataRetrievalCardViewInput.class);

                        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
                        originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                        ciphertextByte = Base64.getDecoder().decode(data.getData());
                        System.out.println(originalKey);
                        System.out.println(data.getData());

                        try {
                            String decrypted = decryptMsg(ciphertextByte,originalKey);
                            System.out.println(decrypted);
                            data.setPlaintext(decrypted);
                            list.add(data);
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidParameterSpecException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        } catch (BadPaddingException e) {
                            e.printStackTrace();
                        } catch (IllegalBlockSizeException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        // tap on each cardview, decrypt one by one

                    }
                    dataRetrievalAdapter = new DataRetrievalAdapter(getApplicationContext(), list);
                    recyclerView = findViewById(R.id.DR_RV);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(dataRetrievalAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DataRetrieval.this, "Oops, something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRef.child("Data").child(receiverId+currentUserdId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            mRef.child("Data").child(receiverId+currentUserdId).removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                mRef.child("pubKey").child(pubKeyX).removeValue();
                mRef.child("DataStatus").child(receiverId+currentUserdId).removeValue();
                Intent intent = new Intent(DataRetrieval.this, MainActivity.class);
                Toast.makeText(DataRetrieval.this, "Session ended", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });


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

//                        decodedCipherText = android.util.Base64.decode(data.getData(), android.util.Base64.DEFAULT);
//                        string = new String(Base64.getDecoder().decode(data.getData()));
//                        String decodedStr = new String(decodedCipherText, StandardCharsets.UTF_8);

//                        System.out.println(decodedCipherText);
//                        System.out.println(decodedStr);

//        BigInteger x = new BigInteger(pubKeyX,10);
//        BigInteger y = new BigInteger(pubKeyY,10);
//        BigInteger privateKey = new BigInteger(privKey,10);
//
//        BigInteger[] pubKeyXY = {x,y};
//
//        for(int i=0;i<pubKeyXY.length;i++)
//        {
//            System.out.println(pubKeyXY[i]);
//        }