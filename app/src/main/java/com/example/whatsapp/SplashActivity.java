package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Handler mHandler = new Handler();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user!=null){
                    Toast.makeText(getApplicationContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainPage.class));
                    finish();
                }else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        },1000);
    }
}
