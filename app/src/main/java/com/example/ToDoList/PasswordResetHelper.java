package com.example.ToDoList;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetHelper {
    private FirebaseAuth mAuth;

    public PasswordResetHelper() {
        // FirebaseAuth'ı başlat
        mAuth = FirebaseAuth.getInstance();
    }

    public void resetPassword(String email, final Activity activity) {
        // Şifre sıfırlama e-postası gönder
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // E-posta başarıyla gönderildi
                            Toast.makeText(activity, "Şifre sıfırlama e-postası gönderildi. Lütfen e-posta adresinizi kontrol edin.", Toast.LENGTH_LONG).show();
                        } else {
                            // E-posta gönderimi başarısız oldu
                            Toast.makeText(activity, "Şifre sıfırlama e-postası gönderilemedi. Lütfen e-posta adresini kontrol edin.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void resetPassword(String email, final Fragment fragment) {
        // Şifre sıfırlama e-postası gönder
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(fragment.requireActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // E-posta başarıyla gönderildi
                            Toast.makeText(fragment.requireContext(), "Şifre sıfırlama e-postası gönderildi. Lütfen e-posta adresinizi kontrol edin.", Toast.LENGTH_LONG).show();
                        } else {
                            // E-posta gönderimi başarısız oldu
                            Toast.makeText(fragment.requireContext(), "Şifre sıfırlama e-postası gönderilemedi. Lütfen e-posta adresini kontrol edin.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
