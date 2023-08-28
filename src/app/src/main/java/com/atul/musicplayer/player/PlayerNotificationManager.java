package com.atul.musicplayer.player;

import static com.atul.musicplayer.MPConstants.CHANNEL_ID;
import static com.atul.musicplayer.MPConstants.NEXT_ACTION;
import static com.atul.musicplayer.MPConstants.NOTIFICATION_ID;
import static com.atul.musicplayer.MPConstants.PLAY_PAUSE_ACTION;
import static com.atul.musicplayer.MPConstants.PREV_ACTION;
import static com.atul.musicplayer.MPConstants.REQUEST_CODE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.atul.musicplayer.MainActivity;
import com.atul.musicplayer.R;
import com.atul.musicplayer.helper.MusicLibraryHelper;
import com.atul.musicplayer.model.Music;

public class PlayerNotificationManager {

    private final NotificationManager notificationManager;
    private final PlayerService playerService;
    private NotificationCompat.Builder notificationBuilder;
    private final androidx.media.app.NotificationCompat.MediaStyle notificationStyle;

    PlayerNotificationManager(@NonNull final PlayerService playerService) {
        this.playerService = playerService;
        notificationStyle = new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2);
        notificationManager = (NotificationManager) playerService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public final NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private PendingIntent playerAction(@NonNull final String action) {
        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(playerService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_IMMUTABLE);
    }

    public Notification createNotification() {
        final Music song = playerService.getPlayerManager().getCurrentMusic();


        final Intent openPlayerIntent = new Intent(playerService, MainActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(playerService, REQUEST_CODE,
                openPlayerIntent, PendingIntent.FLAG_IMMUTABLE);

        if (notificationBuilder == null) {
            notificationBuilder = new NotificationCompat.Builder(playerService, CHANNEL_ID);
            notificationBuilder
                    .setShowWhen(false)
                    .setSmallIcon(R.drawable.ic_notif_music_note)
                    .setColorized(true)
                    .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(), song.albumArt);

        notificationBuilder
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setColor(MusicLibraryHelper.getDominantColorFromThumbnail(albumArt))
                .setLargeIcon(albumArt)
                .setStyle(notificationStyle);

        notificationBuilder.clearActions();
        notificationBuilder
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION));

        return notificationBuilder.build();
    }

    public void updateNotification() {
        if (notificationBuilder == null)
            return;

        notificationBuilder.setOngoing(playerService.getPlayerManager().isPlaying());
        PlayerManager playerManager = playerService.getPlayerManager();
        Music song = playerManager.getCurrentMusic();
        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(),
                song.albumArt);

        notificationBuilder.clearActions();
        notificationBuilder
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION));

        notificationBuilder
                .setLargeIcon(albumArt)
                .setColor(MusicLibraryHelper.getDominantColorFromThumbnail(albumArt))
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setColorized(true)
                .setAutoCancel(true);

        NotificationManagerCompat.from(playerService).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @NonNull
    private NotificationCompat.Action notificationAction(@NonNull final String action) {
        int icon = -1;
        if (action.equals(PREV_ACTION)) icon = R.drawable.ic_controls_prev;
        else if (action.equals(NEXT_ACTION)) icon = R.drawable.ic_controls_next;
        else if (action.equals(PLAY_PAUSE_ACTION)) icon =
                playerService.getPlayerManager().isPlaying()
                        ? R.drawable.ic_controls_pause
                        : R.drawable.ic_controls_play;
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    @RequiresApi(26)
    private void createNotificationChannel() {

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            final NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            playerService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(playerService.getString(R.string.app_name));
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
