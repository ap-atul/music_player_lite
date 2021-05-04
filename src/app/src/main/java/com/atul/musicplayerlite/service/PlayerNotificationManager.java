package com.atul.musicplayerlite.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.atul.musicplayerlite.MainActivity;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.model.Music;

import static com.atul.musicplayerlite.MPConstants.*;

public class PlayerNotificationManager {

    private final NotificationManager notificationManager;
    private final PlayerService playerService;
    private NotificationCompat.Builder mNotificationBuilder;
    private int mAccent;

    PlayerNotificationManager(@NonNull final PlayerService playerService) {
        this.playerService = playerService;
        notificationManager = (NotificationManager) playerService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void setAccentColor(final int color) {
        mAccent = getColorFromResource(playerService, color, R.color.purple_200);
    }

    public final NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public final NotificationCompat.Builder getNotificationBuilder() {
        return mNotificationBuilder;
    }

    public static int getColorFromResource(@NonNull final Context context, final int resource, final int emergencyColor) {
        int color;
        try {
            color = ContextCompat.getColor(context, resource);
        } catch (Exception e) {
            color = ContextCompat.getColor(context, emergencyColor);
        }
        return color;
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

    public Notification createNotification() {
        final Music song = playerService.getPlayerManager().getCurrentSong();
        mNotificationBuilder = new NotificationCompat.Builder(playerService, CHANNEL_ID);

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

        @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) final Spanned spanned = buildSpanned(playerService.getString(R.string.app_name, artist, songTitle));

        mNotificationBuilder
                .setShowWhen(false)
                .setSmallIcon(R.drawable.ic_music_note)
                .setLargeIcon(getLargeIcon())
                .setColor(mAccent)
                .setContentTitle(spanned)
                .setContentText(song.album)
                .setContentIntent(contentIntent)
                .addAction(notificationAction(PREV_ACTION))
                .addAction(notificationAction(PLAY_PAUSE_ACTION))
                .addAction(notificationAction(NEXT_ACTION))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        mNotificationBuilder.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0, 1, 2));
        return mNotificationBuilder.build();
    }

    @NonNull
    private NotificationCompat.Action notificationAction(@NonNull final String action) {
        int icon;

        switch (action) {
            default:
            case PREV_ACTION:
                icon = R.drawable.ic_controls_prev;
                break;
            case PLAY_PAUSE_ACTION:
                icon = playerService.getPlayerManager().getPlayerState() != PlayerListener.State.PAUSED ? R.drawable.ic_controls_pause : R.drawable.ic_controls_play;
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

    private Bitmap getLargeIcon() {

        @SuppressLint("UseCompatLoadingForDrawables")
        final VectorDrawable vectorDrawable = (VectorDrawable) playerService.getDrawable(R.drawable.ic_music_note);

        final int largeIconSize = playerService.getResources().getDimensionPixelSize(R.dimen.text_medium);
        final Bitmap bitmap = Bitmap.createBitmap(largeIconSize, largeIconSize, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.setTint(mAccent);
            vectorDrawable.setAlpha(100);
            vectorDrawable.draw(canvas);
        }

        return bitmap;
    }
}
