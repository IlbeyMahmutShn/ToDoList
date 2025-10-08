package com.example.ToDoList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private TextView userNameTextView;
    private TextView userEmailTextView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Toolbar'ı başlat
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // DrawerLayout ve NavigationView'i başlat
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Görünümleri başlat
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.userName);
        userEmailTextView = headerView.findViewById(R.id.userEmail);

        // Firebase bileşenlerini başlat
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Firebase Authentication'dan kullanıcının e-posta adresini al
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userEmail = user.getEmail();
            userEmailTextView.setText(userEmail);
        }

        // Firebase Realtime Database'den kullanıcının adını al
        String userId = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userRef = databaseReference.child("users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    userNameTextView.setText(userName);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hata durumunu yönet
                Toast.makeText(MainScreenActivity.this, "Kullanıcı verileri alınamadı: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Kullanıcı adını tıklama dinleyicisi
        userNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fragment'i Account ile değiştir
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Account()).commit();
                // Drawer'ı kapat (isteğe bağlı)
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Kullanıcı e-posta adresini tıklama dinleyicisi
        userEmailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fragment'i Account ile değiştir
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Account()).commit();
                // Drawer'ı kapat (isteğe bağlı)
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // Drawer'ı açma ve kapama için ActionBarDrawerToggle oluştur
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // İlk açılışta HomeFragment'i göster
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        // Navigation menüsündeki itemlara göre işlemleri gerçekleştir
        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (itemId == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Settings()).commit();
        } else if (itemId == R.id.nav_share) {
            // Paylaşım seçeneklerini göster
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "ToDoList Uygulaması");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "ToDoList Uygulamasını keşfedin: https://play.google.com/store/apps?hl=en&gl=US");
            startActivity(Intent.createChooser(shareIntent, "Uygulamayı Paylaş"));
        } else if (itemId == R.id.nav_help) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
        } else if (itemId == R.id.nav_account) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Account()).commit();
        } else if (itemId == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new About()).commit();
        } else if (itemId == R.id.nav_contact) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Contact()).commit();
        } else if (itemId == R.id.nav_privacy) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Privacy()).commit();
        } else if (itemId == R.id.nav_logout) {
            // Çıkış işlemi
            FirebaseAuth.getInstance().signOut();

            // Giriş veya splash ekranına yönlendir
            Toast.makeText(this, "Başarıyla çıkış yapıldı!", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(MainScreenActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // İsteğe bağlı: mevcut aktiviteyi bitir
        }
        drawerLayout.closeDrawer(GravityCompat.START); // Yan menüyü kapat
        return true;
    }

    @Override
    public void onBackPressed() {
        // Eğer Drawer açıksa kapat
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
