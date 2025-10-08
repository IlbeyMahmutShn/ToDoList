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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextPassword, editTextPassword2;
    private ImageView imageViewSignUp;
    private TextView loginTextView;
    private FirebaseAuth firebaseAuth;
    private boolean isNetworkAvailable = true;
    private SignupActivity.NetworkChangeReceiver networkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Firebase kimlik doğrulamasını başlat
        firebaseAuth = FirebaseAuth.getInstance();

        // Görünümleri ID ile bul
        editTextName = findViewById(R.id.editTextTextName);
        editTextEmail = findViewById(R.id.editTextTextEmail);
        editTextPassword = findViewById(R.id.editTextTextPassword);
        editTextPassword2 = findViewById(R.id.editTextTextPassword2);
        imageViewSignUp = findViewById(R.id.imageView5);
        loginTextView = findViewById(R.id.textView5);

        imageViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Giriş metnine tıklama işlemini yönet
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        // Ağ değişikliği alıcısını kaydet
        networkChangeReceiver = new SignupActivity.NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ağ değişikliği alıcısını kaldır
        unregisterReceiver(networkChangeReceiver);
    }

    private void signUp() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String password2 = editTextPassword2.getText().toString().trim();

        if (!isNetworkAvailable) {
            // Ağ bağlantısı yok, giriş alanlarını devre dışı bırak
            editTextName.setEnabled(false);
            editTextEmail.setEnabled(false);
            editTextPassword.setEnabled(false);
            editTextPassword2.setEnabled(false);
            Toast.makeText(SignupActivity.this, "İnternet bağlantısı yok. Lütfen ağ ayarlarını kontrol edin.", Toast.LENGTH_LONG).show();
            return;
        }

        // Giriş alanlarını doğrula
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password2)) {
            Toast.makeText(SignupActivity.this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Kimlik Doğrulaması ile yeni bir kullanıcı oluştur
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Yeni oluşturulan kullanıcının kullanıcı ID'sini al
                            String userId = firebaseAuth.getCurrentUser().getUid();

                            // Adı Firebase Realtime Database altında kullanıcının ID'sine kaydet
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                            usersRef.child(userId).child("name").setValue(name);

                            // Kayıt işlemi başarılı
                            Toast.makeText(SignupActivity.this, "Kayıt başarılı", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));

                            // Burada ana ekrana yönlendirme gibi mantık ekleyebilirim.
                        } else {
                            // Kayıt başarısız
                            Toast.makeText(SignupActivity.this, "Kayıt Başarısız: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isNetworkAvailable()) {
                isNetworkAvailable = true;
                editTextName.setEnabled(true);
                editTextEmail.setEnabled(true);
                editTextPassword.setEnabled(true);
                editTextPassword2.setEnabled(true);
                Toast.makeText(SignupActivity.this, "Ağ bağlantısı mevcut", Toast.LENGTH_SHORT).show();
            } else {
                isNetworkAvailable = false;
                editTextName.setEnabled(false);
                editTextEmail.setEnabled(false);
                editTextPassword.setEnabled(false);
                editTextPassword2.setEnabled(false);
                Toast.makeText(SignupActivity.this, "İnternet bağlantısı yok. Lütfen ağ ayarlarını kontrol edin.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
