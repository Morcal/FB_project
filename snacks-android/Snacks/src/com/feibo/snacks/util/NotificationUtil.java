package com.feibo.snacks.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;

public class NotificationUtil {
    public static void notifyNormal(Context context, int id, int smallIcon, Bitmap largeIcon,
            String ticker, String title, String text, PendingIntent pi) {
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setTicker(ticker)
                        //.setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(pi)
                .build();
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }

    public static void notifyProgress(Context context, int id, int smallIcon, Bitmap largeIcon,
            String ticker, String title, String text, int progress) {
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(smallIcon)
                .setTicker(ticker)
                        //.setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setWhen(System.currentTimeMillis())
                .setOngoing(false)
                .setProgress(100, progress, false)
                .setDefaults(Notification.DEFAULT_SOUND)
                .build();
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }
}
