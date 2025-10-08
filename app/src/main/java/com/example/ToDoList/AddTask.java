package com.example.ToDoList;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class AddTask extends Fragment {
    // Gerekli view'lar ve Firebase değişkenlerini tanımla
    public class Task {
        private String title;
        private String content;
        private String date;
        private String time;

        public Task() {
            // Firebase için gerekli varsayılan yapılandırıcı
        }
        public Task(String title, String date, String time, String content) {
            this.title = title;
            this.date = date;
            this.time = time;
            this.content = content;
        }
        public String getTitle() {
            return title;
        }
        public String getContent() {
            return content;
        }
        public String getDate() {
            return date;
        }
        public String getTime() {
            return time;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Bu fragment için layout'u şişir
        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    // Seçilen tarih ve saat için değişkenleri tanımla
    private int year, month, day, hour, minute;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View'ları bul
        TextInputEditText titleEditText = view.findViewById(R.id.titleEditText);
        TextView dateTextView = view.findViewById(R.id.dateTextView);
        ImageView calendarImageView = view.findViewById(R.id.calendarImageView);
        TextView timeTextView = view.findViewById(R.id.timeTextView);
        ImageView clockImageView = view.findViewById(R.id.clockImageView);
        TextInputEditText contentEditText = view.findViewById(R.id.contentEditText);
        Button saveButton = view.findViewById(R.id.saveButton);

        // Takvim resmine tıklama dinleyicisi ayarla
        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mevcut tarihi al
                final Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                // Kullanıcının tarih seçmesine izin vermek için DatePickerDialog oluştur
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Seçilen tarihle bir Calendar nesnesi oluştur
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, monthOfYear, dayOfMonth);

                        // Mevcut tarihi al
                        Calendar currentDate = Calendar.getInstance();

                        // Seçilen tarihi mevcut tarih ile karşılaştır
                        if (selectedDate.before(currentDate)) {
                            // Seçilen tarih geçmişte, hata mesajı göster
                            Toast.makeText(getActivity(), "Lütfen gelecekte bir tarih seçin", Toast.LENGTH_SHORT).show();
                        } else {
                            // Seçilen tarihi değişkenlerde sakla
                            AddTask.this.year = year;
                            month = monthOfYear;
                            day = dayOfMonth;

                            // Seçilen tarihi göster
                            String selectedDateStr = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            dateTextView.setText(selectedDateStr);
                        }
                    }
                }, currentYear, currentMonth, currentDay);

                // DatePickerDialog'ı göster
                datePickerDialog.show();
            }
        });

        // Saat resmine tıklama dinleyicisi ayarla
        clockImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mevcut saati al
                final Calendar currentTime = Calendar.getInstance();

                // Kullanıcının saat seçmesine izin vermek için TimePickerDialog oluştur
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Seçilen saatle bir Calendar nesnesi oluştur
                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);

                        // Seçilen saati mevcut saat ile karşılaştır
                        if (selectedTime.after(currentTime)) {
                            // Seçilen saat gelecekte
                            // Seçilen saati değişkenlerde sakla
                            hour = hourOfDay;
                            AddTask.this.minute = minute;

                            // Seçilen saati göster
                            String selectedTimeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                            timeTextView.setText(selectedTimeStr);
                        } else {
                            // Seçilen saat geçmişte veya mevcut zamana çok yakın
                            Toast.makeText(getActivity(), "Lütfen gelecekte bir saat seçin", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);

                // TimePickerDialog'ı göster
                timePickerDialog.show();
            }
        });

        // Kaydet butonuna tıklama dinleyicisi ayarla
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEditText.getText().toString().trim();
                String date = dateTextView.getText().toString().trim();
                String time = timeTextView.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();

                // Girişi doğrula
                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                    // Başlık, tarih veya saat boşsa hata mesajı göster
                    Toast.makeText(getActivity(), "Lütfen başlık, tarih ve saat girin", Toast.LENGTH_SHORT).show();
                } else {
                    // İçeriğin maksimum satır sayısını aşıp aşmadığını kontrol et
                    int lineCount = contentEditText.getLineCount();
                    if (lineCount > 3) {
                        // Kullanıcıya hata mesajı göster
                        Toast.makeText(getActivity(), "İçerik 3 satırı/30 kelimeyi geçmemelidir", Toast.LENGTH_SHORT).show();
                    } else {
                        // Mevcut zamanı al
                        final Calendar currentTime = Calendar.getInstance();

                        // Seçilen tarih ve saati al
                        Calendar selectedDateTime = Calendar.getInstance();
                        selectedDateTime.set(year, month, day, hour, minute);

                        // Seçilen zaman ile mevcut zaman arasındaki farkı hesapla
                        long timeDifferenceInMillis = selectedDateTime.getTimeInMillis() - currentTime.getTimeInMillis();
                        int timeDifferenceInMinutes = (int) (timeDifferenceInMillis / (60 * 1000));

                        // Seçilen saatin en az altı dakika sonrasında olup olmadığını kontrol et
                        if (timeDifferenceInMinutes < 6) {
                            // Kullanıcıya hata mesajı göster
                            Toast.makeText(getActivity(), "Lütfen en az altı dakika sonra bir saat seçin", Toast.LENGTH_SHORT).show();
                        } else {
                            // Görevi Firebase'e kaydet
                            saveTaskToFirebase(title, date, time, content);
                        }
                    }
                }
            }
        });
    }

    private void saveTaskToFirebase(String title, String date, String time, String content) {
        // Geçerli kullanıcı kimliğini al (Firebase Kimlik Doğrulamayı uyguladığınız varsayılarak)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Kullanıcı kimliği doğrulanmamış, buna göre yönet
            Toast.makeText(getActivity(), "Giriş yapılmamış!", Toast.LENGTH_SHORT).show();
            // Giriş sayfasına yönlendir
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        String userId = currentUser.getUid();

        // Kullanıcının görevler düğümüne referans oluştur
        DatabaseReference userTasksRef = FirebaseDatabase.getInstance().getReference("users")
                .child(userId)
                .child("tasks");

        // Görev için benzersiz bir anahtar oluştur
        String taskId = userTasksRef.push().getKey();

        // Bir Görev nesnesi oluştur
        Task task = new Task(title, date, time, content);

        // İnternet bağlantısı kontrolü
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            // İnternet bağlantısı mevcut, görevi Firebase'e kaydet
            userTasksRef.child(taskId).setValue(task)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Görev başarıyla kaydedildi
                            Toast.makeText(getActivity(), "Görev başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                            navigateToHomeFragment();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Görevi kaydederken hata oluştu
                            Toast.makeText(getActivity(), "Görev kaydedilemedi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // İnternet bağlantısı yok, görevi yerel olarak kaydet ve anasayfaya yönlendir
            userTasksRef.child(taskId).setValue(task);
            Toast.makeText(getActivity(), "Görev yerel olarak kaydedildi. İnternet bağlantısı yok.", Toast.LENGTH_SHORT).show();
            navigateToHomeFragment();
        }

        // Hatırlatma için...
        // Seçilen tarih ve saatle bir Calendar nesnesi oluştur
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute);
        calendar.set(Calendar.SECOND, 0); // Bildirimin tam dakikada tetiklenmesini sağla, saniye gecikmesi olmadan.

        // Mevcut zamanı milisaniye cinsinden al
        long currentTimeMillis = System.currentTimeMillis();

        // Mevcut zaman ile seçilen zaman arasındaki farkı hesapla
        long timeDifference = calendar.getTimeInMillis() - currentTimeMillis;

        // HatırlatmaBroadcastReceiver'ı tetiklemek için bir Intent oluştur
        Intent reminderIntent = new Intent(getActivity(), Alarm.class);
        reminderIntent.putExtra("title", title);
        reminderIntent.putExtra("content", content);

        // reminderIntent'i saran bir PendingIntent oluştur
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, reminderIntent, PendingIntent.FLAG_IMMUTABLE);

        // AlarmManager'ı al
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);

        // Alarmı zamanla
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, currentTimeMillis + timeDifference, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTimeMillis + timeDifference, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, currentTimeMillis + timeDifference, pendingIntent);
        }
    }

    private void navigateToHomeFragment() {
        // HomeFragment'e geri dönmek için yönlendirme mantığını uygula
        // Örnek: Mevcut fragmenti HomeFragment ile değiştirmek için bir FragmentManager kullan
        // HomeFragment'in bir örneğini oluştur
        HomeFragment homeFragment = new HomeFragment();

        // FragmentManager'ı al
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        // Mevcut fragmenti HomeFragment ile değiştir
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();
    }
}
