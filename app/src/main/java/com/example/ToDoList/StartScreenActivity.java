package com.example.ToDoList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class StartScreenActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT =  2000; // 2 saniye

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ana Aktivitenin başlamasını geciktir
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Ana aktiviteyi başlat
                Intent mainIntent = new Intent(StartScreenActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_TIMEOUT);
    }
}
