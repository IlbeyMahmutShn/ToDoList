package com.example.ToDoList;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class Alarm extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Intent'ten görev detaylarını al
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        // Bildirim kanalı oluştur (Android Oreo ve üstü için gereklidir)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    context.getString(R.string.default_notification_channel_id),
                    context.getString(R.string.default_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Bir bildirim oluştur
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.task_time)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                .setVibrate(new long[]{0, 100, 200, 300});

        // Bildirimi göstermek için gerekli iznin olup olmadığını kontrol et
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {
            // İzin verilmediği durumda durumu ele al
            // Kullanıcıdan izni isteyebilir veya başka uygun bir işlem yapabilirsin
            return;
        }

        // Bildirimi göster
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(0, builder.build());
        } catch (SecurityException e) {
            // SecurityException'ı ele al
            // Bu istisna, uygulamanın gerekli izne sahip olmadığı durumlarda meydana gelebilir
            e.printStackTrace();
        }
    }
}
