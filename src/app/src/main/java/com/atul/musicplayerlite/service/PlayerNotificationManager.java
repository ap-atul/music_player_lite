package com.atul.musicplayerlite.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.atul.musicplayerlite.MainActivity;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.helper.MusicLibraryHelper;
import com.atul.musicplayerlite.model.Music;

import static com.atul.musicplayerlite.MPConstants.CHANNEL_ID;
import static com.atul.musicplayerlite.MPConstants.NEXT_ACTION;
import static com.atul.musicplayerlite.MPConstants.NOTIFICATION_ID;
import static com.atul.musicplayerlite.MPConstants.PLAY_PAUSE_ACTION;
import static com.atul.musicplayerlite.MPConstants.PREV_ACTION;
import static com.atul.musicplayerlite.MPConstants.REQUEST_CODE;

public class PlayerNotificationManager {

    private final NotificationManager notificationManager;
    private final PlayerService playerService;
    private NotificationCompat.Builder notificationBuilder;

    PlayerNotificationManager(@NonNull final PlayerService playerService) {
        this.playerService = playerService;
        notificationManager = (NotificationManager) playerService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public final NotificationManager getNotificationManager() {
        return notificationManager;
    }

    private PendingIntent playerAction(@NonNull final String action) {
        final Intent pauseIntent = new Intent();
        pauseIntent.setAction(action);

        return PendingIntent.getBroadcast(playerService, REQUEST_CODE, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static Spanned buildSpanned(@NonNull final String res) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                Html.fromHtml(res, Html.FROM_HTML_MODE_LEGACY) :
                Html.fromHtml(res);
    }

    private int getDominantColor(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true);
        final int color = newBitmap.getPixel(0, 0);
        newBitmap.recycle();
        return color;
    }

    public Notification createNotification() {
        final Music song = playerService.getPlayerManager().getCurrentSong();
        notificationBuilder = new NotificationCompat.Builder(playerService, CHANNEL_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        final Intent openPlayerIntent = new Intent(playerService, MainActivity.class);
        openPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent contentIntent = PendingIntent.getActivity(playerService, REQUEST_CODE,
                openPlayerIntent, 0);

        final String artist = song.artist;
        final String songTitle = song.title;
        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(), song.albumArt);

        if(albumArt != null)
            notificationBuilder
                    .setLargeIcon(albumArt)
                    .setColor(getDominantColor(albumArt));
        else
            notificationBuilder
                    .setLargeIcon(null);

        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) final Spanned spanned = buildSpanned(playerService.getString(R.string.app_name, artist, songTitle));

        notificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_music_note)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(playerService.getMediaSessionCompat().getSessionToken())
                    .setShowActionsInCompactView(1, 2, 3))
                .setContentTitle(spanned)
                .setContentText(song.artist)
                .setSubText(song.album)
                .setColorized(true)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        notificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));
        return notificationBuilder.build();
    }

    @SuppressLint("RestrictedApi")
    public void updateNotification(){

        notificationBuilder.setOngoing(playerService.getPlayerManager().isPlaying());

        PlayerManager playerManager = playerService.getPlayerManager();
        Music song = playerManager.getCurrentSong();
        Bitmap albumArt = MusicLibraryHelper.getThumbnail(playerService.getApplicationContext(),
                song.albumArt);

        notificationBuilder.mActions.set(1, notificationAction(PLAY_PAUSE_ACTION));

        if(albumArt != null)
            notificationBuilder
                    .setLargeIcon(albumArt)
                    .setColor(getDominantColor(albumArt));

        else
            notificationBuilder
                    .setLargeIcon(null);

        notificationBuilder
                .setContentText(song.artist)
                .setColorized(true)
                .setSubText(song.album);


        NotificationManagerCompat.from(playerService).notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    @NonNull
    private NotificationCompat.Action notificationAction(@NonNull final String action) {
        int icon = R.drawable.ic_controls_pause;

        switch (action) {
            case PREV_ACTION:
                icon = R.drawable.ic_controls_prev;
                break;
            case PLAY_PAUSE_ACTION:
                icon = playerService.getPlayerManager().isPlaying() ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
                break;
            case NEXT_ACTION:
                icon = R.drawable.ic_controls_next;
                break;
        }
        return new NotificationCompat.Action.Builder(icon, action, playerAction(action)).build();
    }

    @RequiresApi(26)
    private void createNotificationChannel() {

        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            final NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID,
                            playerService.getString(R.string.app_name),
                            NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription(
                    playerService.getString(R.string.app_name));

            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
