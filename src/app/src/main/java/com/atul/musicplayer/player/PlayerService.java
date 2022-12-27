package com.atul.musicplayer.player;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;

import com.atul.musicplayer.MPConstants;

public class PlayerService extends Service {

    private final IBinder iBinder = new LocalBinder();
    private PlayerManager playerManager;
    private PlayerNotificationManager notificationManager;
    private MediaSessionCompat mediaSessionCompat;
    private final MediaSessionCompat.Callback mediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public void onPlay() {
            playerManager.playPause();
        }

        @Override
        public void onPause() {
            playerManager.playPause();
        }

        @Override
        public void onSkipToNext() {
            playerManager.playNext();
        }

        @Override
        public void onSkipToPrevious() {
            playerManager.playPrev();
        }

        @Override
        public void onFastForward() {
            super.onFastForward();
        }

        @Override
        public void onRewind() {
            playerManager.seekTo(0);
        }

        @Override
        public void onStop() {
            playerManager.playPause();
        }

        @Override
        public void onSeekTo(long pos) {
            playerManager.seekTo((int) pos);
        }

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            return handleMediaButtonEvent(mediaButtonEvent);
        }
    };
    private PowerManager.WakeLock wakeLock;

    public PlayerService() {
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public PlayerNotificationManager getNotificationManager() {
        return notificationManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // prevents the service from closing, when app started from
        // notification click, this will make sure that a foreground
        // service exists too.
        if (playerManager != null && playerManager.isPlaying())
            playerManager.attachService();

        return START_NOT_STICKY;
    }

    private void configureMediaSession() {
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        ComponentName mediaButtonReceiverComponentName = new ComponentName(this, PlayerManager.NotificationReceiver.class);
        PendingIntent mediaButtonReceiverPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE);

        mediaSessionCompat = new MediaSessionCompat(this, MPConstants.MEDIA_SESSION_TAG, mediaButtonReceiverComponentName, mediaButtonReceiverPendingIntent);
        mediaSessionCompat.setActive(true);
        mediaSessionCompat.setCallback(mediaSessionCallback);
        mediaSessionCompat.setMediaButtonReceiver(mediaButtonReceiverPendingIntent);
    }

    private boolean handleMediaButtonEvent(Intent mediaButtonEvent) {
        boolean isSuccess = false;
        if (mediaButtonEvent == null) {
            return false;
        }

        KeyEvent keyEvent = mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);

        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_HEADSETHOOK:
                    playerManager.playPause();
                    isSuccess = true;
                    break;

                case KeyEvent.KEYCODE_MEDIA_CLOSE:
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    playerManager.pauseMediaPlayer();
                    isSuccess = true;
                    break;

                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    playerManager.playPrev();
                    isSuccess = true;
                    break;

                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    playerManager.playNext();
                    isSuccess = true;
                    break;

                case KeyEvent.KEYCODE_MEDIA_REWIND:
                    playerManager.seekTo(0);
                    isSuccess = true;
                    break;
            }
        }

        return isSuccess;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (playerManager == null) {
            playerManager = new PlayerManager(this);
            notificationManager = new PlayerNotificationManager(this);
            playerManager.registerActionsReceiver();
        }

        return iBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            wakeLock.setReferenceCounted(false);
        }

        configureMediaSession();
    }

    @Override
    public void onDestroy() {
        if (playerManager != null) {
            playerManager.unregisterActionsReceiver();
            playerManager.release();
        }
        notificationManager = null;
        playerManager = null;

        super.onDestroy();
    }

    public MediaSessionCompat getMediaSessionCompat() {
        return mediaSessionCompat;
    }

    class LocalBinder extends Binder {
        public PlayerService getInstance() {
            return PlayerService.this;
        }
    }
}