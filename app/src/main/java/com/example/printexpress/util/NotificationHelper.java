package com.example.printexpress.util;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.printexpress.R;
import com.example.printexpress.ui.profile.NotificationsActivity;

/** Shows Android system notifications (the alerts in the phone's status bar). */
public final class NotificationHelper {

    private static final String CHANNEL_ID = "printxpress_orders";

    private NotificationHelper() {
    }

    /** Call once when the app starts (Android 8+ needs a channel). */
    public static void createChannel(Context context) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                context.getString(R.string.channel_orders), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(context.getString(R.string.channel_orders_desc));
        context.getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    public static void show(Context context, int id, String title, String message) {
        // Android 13+ asks the user for permission; skip quietly if not given
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Intent intent = new Intent(context, NotificationsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_print)
                .setColor(ContextCompat.getColor(context, R.color.px_primary))
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
