package com.example.ToDoList;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Contact extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap mMap;
    private DatabaseReference usersRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        // MapView'i başlat
        mapView = view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Firebase Realtime Database'i başlat
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");

        // Görünümlere referansları al
        final EditText messageEditText = view.findViewById(R.id.message);
        final EditText emailAddressEditText = view.findViewById(R.id.editTextTextEmailAddress);
        Button sendMessageButton = view.findViewById(R.id.sendMessage);

        // Mesaj gönderme butonuna tıklama dinleyicisi ekle
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kullanıcının girdiği değerleri al
                String message = messageEditText.getText().toString().trim();
                String emailAddress = emailAddressEditText.getText().toString().trim();

                // Herhangi bir EditText alanı boş mu kontrol et
                if (message.isEmpty() || emailAddress.isEmpty()) {
                    showToast("Lütfen tüm alanları doldurun");
                    return;
                }

                // E-posta adresini doğrula
                if (!isValidEmail(emailAddress)) {
                    showToast("Geçersiz e-posta adresi");
                    return;
                }

                // Mevcut tarih ve saat oluştur
                String datetime = getCurrentDateTime();

                // Firebase Authentication ile oturum açmış kullanıcıyı al
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference userRef = usersRef.child(userId).child("feedback");
                DatabaseReference feedbackRef = userRef.child(datetime);
                feedbackRef.child("message").setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error == null) {
                            // Mesaj başarıyla kaydedildi
                            showToast("Mesaj başarıyla gönderildi");
                            // EditText alanlarını temizle
                            messageEditText.setText("");
                            emailAddressEditText.setText("");
                        } else {
                            // Veri kaydetme sırasında hata oluştu
                            showToast("Hata: " + error.getMessage());
                        }
                    }
                });
                feedbackRef.child("emailAddress").setValue(emailAddress);
            }
        });

        // E-posta adresi alanı için odak değişimi dinleyicisi ayarla
        emailAddressEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // E-posta adresinin geçerli olup olmadığını kontrol et
                    String emailAddress = emailAddressEditText.getText().toString().trim();
                    if (!isValidEmail(emailAddress)) {
                        emailAddressEditText.setError("Geçersiz e-posta adresi");
                    } else {
                        emailAddressEditText.setError(null);
                    }
                }
            }
        });

        return view;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private boolean isValidEmail(CharSequence target) {
        return Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Belirli bir konumda bir işaretçi ekle ve kamerayı o konuma hareket ettir
        LatLng location = new LatLng(-6.814018774740469, 39.28006551534149); // İstenilen enlem ve boylam ile değiştir
        float zoomLevel = 12.0f; // İstenilen yakınlaştırma seviyesini değiştir
        mMap.addMarker(new MarkerOptions().position(location).title("İlbey.ms Teknoloji Enstitüsü"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

        // İşaretçi tıklama dinleyicisini ayarla
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // İşaretçiye tıklandığında yakınlaştır
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17.0f));
                return true;
            }
        });
    }
}
