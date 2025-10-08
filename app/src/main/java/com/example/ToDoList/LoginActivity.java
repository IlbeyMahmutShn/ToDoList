package com.example.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView forgotPasswordTextView;
    private FirebaseAuth mAuth;
    private PasswordResetHelper passwordResetHelper;
    private boolean isNetworkAvailable = true;
    private NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        passwordResetHelper = new PasswordResetHelper();

        editTextEmail = findViewById(R.id.editTextTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        forgotPasswordTextView = findViewById(R.id.textView23);
        ImageView imageViewLogin = findViewById(R.id.imageView5);
        TextView registerTextView = findViewById(R.id.textView5);

        imageViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(); // Giriş yapma işlemini başlat
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    editTextEmail.setError("Email gerekli"); // Email zorunlu hatası
                    editTextEmail.requestFocus();
                } else {
                    passwordResetHelper.resetPassword(email, LoginActivity.this); // Şifre sıfırlama işlemi
                }
            }
        });

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signUpIntent); // Kayıt olma sayfasına git
            }
        });

        // Ağ değişikliği alıcısını kaydet
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ağ değişikliği alıcısını kayıttan çıkar
        unregisterReceiver(networkChangeReceiver);
    }

    private void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!isNetworkAvailable) {
            editTextEmail.setEnabled(false);
            editTextPassword.setEnabled(false);
            Toast.makeText(LoginActivity.this, "İnternet bağlantısı yok. Lütfen ağ ayarlarınızı kontrol edin.", Toast.LENGTH_LONG).show(); // İnternet bağlantısı yok hatası
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email gerekli"); // Email zorunlu hatası
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Şifre gerekli"); // Şifre zorunlu hatası
            editTextPassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Giriş başarılı", Toast.LENGTH_SHORT).show(); // Giriş başarılı mesajı
                            startActivity(new Intent(LoginActivity.this, MainScreenActivity.class)); // Ana ekrana git
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Giriş başarısız. Lütfen bilgilerinizi kontrol edin", Toast.LENGTH_SHORT).show(); // Giriş başarısız hatası
                        }
                    }
                });
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                isNetworkAvailable = true;
                editTextEmail.setEnabled(true);
                editTextPassword.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Ağ bağlantısı mevcut", Toast.LENGTH_SHORT).show(); // Ağ bağlantısı mevcut mesajı
            } else {
                isNetworkAvailable = false;
                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                Toast.makeText(LoginActivity.this, "İnternet bağlantısı yok. Lütfen ağ ayarlarınızı kontrol edin.", Toast.LENGTH_SHORT).show(); // İnternet bağlantısı yok hatası
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected(); // Ağ bağlantısı kontrolü
    }
}
