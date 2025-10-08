package com.example.ToDoList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends DialogFragment {
    private PasswordResetHelper passwordResetHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        passwordResetHelper = new PasswordResetHelper();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Şifre Değiştir")
                .setMessage("Şifrenizi değiştirmek ister misiniz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Geçerli kullanıcının e-posta adresini al
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String userEmail = user.getEmail();

                        if (userEmail != null) {
                            // Kullanıcının e-posta adresiyle şifre sıfırlama yöntemini çağır
                            passwordResetHelper.resetPassword(userEmail, requireActivity());
                        }
                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Kullanıcı "Hayır" seçeneğini seçtiğinde dialog'u kapat
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
