package com.atul.musicplayerlite.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.atul.musicplayerlite.MPConstants;
import com.atul.musicplayerlite.MainActivity;
import com.atul.musicplayerlite.R;


public class NotificationHelper extends ContextWrapper {
    private NotificationManager notificationManager;
    public Context playerService;

    public NotificationHelper(Context base) {
        super(base);
        this.playerService = base;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            createChannels();
    }

    public void createNotification() {
        Intent openPlayerIntent = new Intent(playerService, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                playerService, MPConstants.NOTIFICATION_INTENT_REQ_CODE,
                openPlayerIntent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Notification.Builder notificationBuilder = getNotification(contentIntent);
            notify(10, notificationBuilder);

        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "com.amigo.vendor.ALL")
                    .setContentTitle("title")
                    .setContentText("body")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            NotificationManagerCompat.from(getApplicationContext()).notify(10, builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {

        NotificationChannel notificationChannel = new NotificationChannel(MPConstants.NOTIFICATION_CHANNEL_ID,
                MPConstants.NOTIFICATION_CHANNEL_TITLE, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setShowBadge(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(notificationChannel);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification.Builder getNotification(PendingIntent myIntent) {
        return new Notification.Builder(getApplicationContext(), MPConstants.NOTIFICATION_CHANNEL_ID)
                .setContentTitle("title")
                .setContentText("body")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(myIntent)
                .setPriority(Notification.PRIORITY_LOW)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(true);
    }

    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }
}