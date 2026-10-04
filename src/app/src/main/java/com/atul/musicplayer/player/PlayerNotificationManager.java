package com.atul.musicplayer.player;

import static com.atul.musicplayer.MPConstants.CHANNEL_ID;
import static com.atul.musicplayer.MPConstants.NEXT_ACTION;
import static com.atul.musicplayer.MPConstants.NOTIFICATION_ID;
import static com.atul.musicplayer.MPConstants.PLAY_PAUSE_ACTION;
import static com.atul.musicplayer.MPConstants.PREV_ACTION;
import static com.atul.musicplayer.MPConstants.REQUEST_CODE;
import static com.atul.musicplayer.MPConstants.STOP_ACTION;

import android.annotation.SuppressLint;
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
    private final androidx.media.app.NotificationCompat.MediaStyle notificationStyle;
    private NotificationCompat.Builder notificationBuilder;

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
        if (song == null) {
            return notificationBuilder != null ? notificationBuilder.build() : null;
        }

        final Intent openPlayerIntent = new Intent(playerService, MainActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(playerService, REQUEST_CODE,
                openPlayerIntent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        if (notificationBuilder == null) {
            notificationBuilder = new NotificationCompat.Builder(playerService, CHANNEL_ID);
            notificationBuilder
                    .setShowWhen(false)
                    .setSmallIcon(R.drawable.ic_notif_music_note)
                    .setColorized(true)
                    .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                    .setContentIntent(contentIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        }

        boolean playing = playerService.getPlayerManager().isPlaying();
        PendingIntent deleteIntent = PendingIntent.getBroadcast(
                playerService, REQUEST_CODE + 1,
                new Intent(STOP_ACTION), PendingIntent.FLAG_IMMUTABLE);

        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(), song.albumArt);

        notificationBuilder
                .setOngoing(playing)
                .setDeleteIntent(deleteIntent)
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

    @SuppressLint("MissingPermission")
    public void updateNotification() {
        if (notificationBuilder == null)
            return;

        PlayerManager playerManager = playerService.getPlayerManager();
        Music song = playerManager.getCurrentMusic();
        if (song == null) return;

        boolean playing = playerManager.isPlaying();
        PendingIntent deleteIntent = PendingIntent.getBroadcast(
                playerService, REQUEST_CODE + 1,
                new Intent(STOP_ACTION), PendingIntent.FLAG_IMMUTABLE);

        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(),
                song.albumArt);

        notificationBuilder.clearActions();
        notificationBuilder
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION));

        notificationBuilder
                .setOngoing(playing)
                .setDeleteIntent(deleteIntent)
                .setLargeIcon(albumArt)
                .setColor(MusicLibraryHelper.getDominantColorFromThumbnail(albumArt))
                .setContentTitle(song.title)
                .setContentText(song.artist)
                .setColorized(true);

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
