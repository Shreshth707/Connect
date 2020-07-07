package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText mPhoneNumber, mCode;
    private Button mSend;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        userIsLoggedIn();

        mPhoneNumber = findViewById(R.id.phonenumber);
        mCode = findViewById(R.id.code);
        mSend = findViewById(R.id.send);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVerificationId != null){
                    verifyPhoneNumberWithCode();
                }else
                    startPhoneNumberVerification();
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(),"Verification Failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String VerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(VerificationId, forceResendingToken);
                mVerificationId = VerificationId;
                mSend.setText("Verify Code");
            }
        };
    }

    private void verifyPhoneNumberWithCode(){
        String code = mCode.getText().toString();
        if(code.length()==0){
            mCode.requestFocus();
            mCode.setError("Enter Verification Code");
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,mCode.getText().toString());
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if(user != null){
                        final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    userIsLoggedIn();
                                }
                                if(!dataSnapshot.exists()){
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("phone", user.getPhoneNumber());
                                    userMap.put("name", user.getPhoneNumber());
                                    mUserDB.updateChildren(userMap);
                                    Intent intent = new Intent(getApplicationContext(),AddUserDetailsActivity.class);
                                    Toast.makeText(getApplicationContext(),"Welcome to Connect",Toast.LENGTH_SHORT).show();
                                    startActivityForResult(intent,1);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"Verification Failed", Toast.LENGTH_SHORT).show();
                    mCode.setError("Please Enter Correct Code");
                }

            }
        });
    }

    private void userIsLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),MainPage.class));
            finish();
            return;
        }
    }

    private void startPhoneNumberVerification() {
        String phoneno = mPhoneNumber.getText().toString();
        if(phoneno.length()==0){
            mPhoneNumber.requestFocus();
            mPhoneNumber.setError("Enter Phone Number");
            return;
        }else if(phoneno.length()<10){
            mPhoneNumber.setError("Enter a Valid Phone No");
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+mPhoneNumber.getText().toString(),60, TimeUnit.SECONDS,this,mCallbacks);
    }


    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == 1){
                userIsLoggedIn();
            }
        }
        finish();
    }

}
