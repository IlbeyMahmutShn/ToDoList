package com.example.ToDoList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private AppCompatButton loginButton;
    private AppCompatButton signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Authentication'ı başlat
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Butonları başlat
        loginButton = findViewById(R.id.button);
        signUpButton = findViewById(R.id.button2);

        if (currentUser != null) {
            // Kullanıcı kimlik doğrulandı, dashboard aktivitesine git
            Intent dashboardIntent = new Intent(MainActivity.this, MainScreenActivity.class);
            startActivity(dashboardIntent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giriş butonu tıklama işlemini yönet
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kayıt ol butonu tıklama işlemini yönet
                Intent signUpIntent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(signUpIntent);
            }
        });
    }
}
