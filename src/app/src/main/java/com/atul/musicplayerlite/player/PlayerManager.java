package com.atul.musicplayerlite.player;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.atul.musicplayerlite.model.Music;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.atul.musicplayerlite.MPConstants.AUDIO_FOCUSED;
import static com.atul.musicplayerlite.MPConstants.AUDIO_NO_FOCUS_CAN_DUCK;
import static com.atul.musicplayerlite.MPConstants.AUDIO_NO_FOCUS_NO_DUCK;
import static com.atul.musicplayerlite.MPConstants.DEBUG_TAG;
import static com.atul.musicplayerlite.MPConstants.NEXT_ACTION;
import static com.atul.musicplayerlite.MPConstants.NOTIFICATION_ID;
import static com.atul.musicplayerlite.MPConstants.PLAY_PAUSE_ACTION;
import static com.atul.musicplayerlite.MPConstants.PREV_ACTION;
import static com.atul.musicplayerlite.MPConstants.VOLUME_DUCK;
import static com.atul.musicplayerlite.MPConstants.VOLUME_NORMAL;

public class PlayerManager implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private final Context context;
    private final PlayerService playerService;
    private final AudioManager audioManager;
    private PlayerListener playerListener;
    private int playerState;
    private Music currentSong;
    private List<Music> musicList;
    private MediaPlayer mediaPlayer;
    private NotificationReceiver notificationReceiver;
    private PlayerNotificationManager notificationManager;
    private int currentAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
    private boolean playOnFocusGain;
    private MediaObserver mediaObserver;

    private final AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {

                @Override
                public void onAudioFocusChange(final int focusChange) {

                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_GAIN:
                            currentAudioFocus = AUDIO_FOCUSED;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            // Audio focus was lost, but it's possible to duck (i.e.: play quietly)
                            currentAudioFocus = AUDIO_NO_FOCUS_CAN_DUCK;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            // Lost audio focus, but will gain it back (shortly), so note whether
                            // playback should resume
                            currentAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
                            playOnFocusGain = isMediaPlayer() && playerState == PlaybackStateCompat.STATE_PLAYING || playerState == PlaybackStateCompat.STATE_NONE;
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            // Lost audio focus, probably "permanently"
                            currentAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
                            break;
                    }

                    if (mediaPlayer != null) {
                        // Update the player state based on the change
                        configurePlayerState();
                    }

                }
            };

    public PlayerManager(@NonNull PlayerService playerService) {
        this.playerService = playerService;
        this.context = playerService.getApplicationContext();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void registerActionsReceiver() {
        notificationReceiver = new PlayerManager.NotificationReceiver();
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(PREV_ACTION);
        intentFilter.addAction(PLAY_PAUSE_ACTION);
        intentFilter.addAction(NEXT_ACTION);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);

        playerService.registerReceiver(notificationReceiver, intentFilter);
    }

    public void unregisterActionsReceiver() {
        if (playerService != null && notificationReceiver != null) {
            try {
                playerService.unregisterReceiver(notificationReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public int getPlayerState() {
        return playerState;
    }

    private void setPlayerState(@PlayerListener.State int state) {
        playerState = state;
        playerListener.onStateChanged(state);
        playerService.getNotificationManager().updateNotification();

        int playbackState = isPlaying() ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED;

        playerService.getMediaSessionCompat().setPlaybackState(
                new PlaybackStateCompat.Builder()
                        .setActions(
                                PlaybackStateCompat.ACTION_PLAY |
                                        PlaybackStateCompat.ACTION_PAUSE |
                                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT |
                                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                        PlaybackStateCompat.ACTION_SEEK_TO |
                                        PlaybackStateCompat.ACTION_PLAY_PAUSE)
                        .setState(playbackState, mediaPlayer.getCurrentPosition(), 0)
                        .build());
    }

    public Music getCurrentSong() {
        return currentSong;
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public void setPlayerListener(PlayerListener listener) {
        playerListener = listener;
        playerListener.onPrepared();
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
        this.currentSong = musicList.get(0);

        initMediaPlayer();
    }

    public void detachService(){
        playerService.stopForeground(false);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playerListener.onPlaybackCompleted();
        playNext();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playerListener.onMusicSet(currentSong);

        if (mediaObserver == null){
            mediaObserver = new MediaObserver();
            new Thread(mediaObserver).start();
        }
    }

    public boolean isPlaying() {
        return isMediaPlayer() && mediaPlayer.isPlaying();
    }

    public boolean isMediaPlayer() {
        return mediaPlayer != null;
    }

    public void pauseMediaPlayer() {
        setPlayerState(PlayerListener.State.PAUSED);
        mediaPlayer.pause();

        playerService.stopForeground(false);
        notificationManager.getNotificationManager().notify(NOTIFICATION_ID, notificationManager.createNotification());
    }

    public void resumeMediaPlayer() {
        if (!isPlaying()) {
            mediaPlayer.start();
            setPlayerState(PlayerListener.State.RESUMED);
            playerService.startForeground(NOTIFICATION_ID, notificationManager.createNotification());

            if (notificationManager != null) {
                Log.d(DEBUG_TAG, "Notification updated");
                notificationManager.updateNotification();
            }
        }
    }

    public void playPrev() {
        int currentIndex = musicList.indexOf(currentSong) - 1;
        if (currentIndex <= -1)
            currentSong = musicList.get(0);

        currentSong = musicList.get(currentIndex);
        initMediaPlayer();
        playerService.startForeground(NOTIFICATION_ID, notificationManager.createNotification());
    }

    public void playNext() {
        int currentIndex = musicList.indexOf(currentSong) + 1;
        if (currentIndex > musicList.size())
            currentSong = musicList.get(0);

        currentSong = musicList.get(currentIndex);
        initMediaPlayer();
    }

    public void playPause() {
        if (isPlaying()) {
            mediaPlayer.pause();
            setPlayerState(PlayerListener.State.PAUSED);
        } else {
            mediaPlayer.start();
            setPlayerState(PlayerListener.State.PLAYING);
        }
    }

    public void release() {
        mediaPlayer.release();
        playerListener.onRelease();
        mediaObserver.stop();

        Log.d(DEBUG_TAG, "Released");
    }

    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    private void configurePlayerState() {

        if (currentAudioFocus == AUDIO_NO_FOCUS_NO_DUCK) {
            // We don't have audio focus and can't duck, so we have to pause
            pauseMediaPlayer();
        } else {

            if (currentAudioFocus == AUDIO_NO_FOCUS_CAN_DUCK) {
                // We're permitted to play, but only if we 'duck', ie: play softly
                mediaPlayer.setVolume(VOLUME_DUCK, VOLUME_DUCK);
            } else {
                mediaPlayer.setVolume(VOLUME_NORMAL, VOLUME_NORMAL);
            }

            // If we were playing when we lost focus, we need to resume playing.
            if (playOnFocusGain) {
                resumeMediaPlayer();
                playOnFocusGain = false;
            }
        }
    }

    private void tryToGetAudioFocus() {

        final int result = audioManager.requestAudioFocus(
                mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            currentAudioFocus = AUDIO_FOCUSED;
        } else {
            currentAudioFocus = AUDIO_NO_FOCUS_NO_DUCK;
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        } else {
            mediaPlayer = new MediaPlayer();
//            EqualizerUtils.openAudioEffectSession(mContext, mediaPlayer.getAudioSessionId());

            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            notificationManager = playerService.getNotificationManager();
        }

        tryToGetAudioFocus();

        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currentSong.id);

        try {
            mediaPlayer.setDataSource(context, trackUri);
            mediaPlayer.prepare();
            mediaPlayer.start();

            playerService.startForeground(NOTIFICATION_ID, notificationManager.createNotification());

            setPlayerState(PlayerListener.State.PLAYING);
        } catch (Exception e) {
            Log.d(DEBUG_TAG, "exception in media player set : initMediaPlayer");
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        playerListener.onPositionChanged(percent);
    }

    public class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(@NonNull final Context context, @NonNull final Intent intent) {
            final String action = intent.getAction();

            if (action != null) {

                switch (action) {
                    case PREV_ACTION:
                        Log.i(DEBUG_TAG, "Previous song action");
                        playPrev();
                        break;

                    case PLAY_PAUSE_ACTION:
                        Log.i(DEBUG_TAG, "Pause play song action");
                        playPause();
                        break;

                    case NEXT_ACTION:
                        Log.i(DEBUG_TAG, "Next song action");
                        playNext();
                        break;

                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        if (currentSong != null) {
                            pauseMediaPlayer();
                        }
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        if (currentSong != null && !isPlaying()) {
                            resumeMediaPlayer();
                        }
                        break;

                    case Intent.ACTION_HEADSET_PLUG:
                        if (currentSong != null) {
                            switch (intent.getIntExtra("state", -1)) {
                                //0 means disconnected
                                case 0:
                                    pauseMediaPlayer();
                                    break;
                                //1 means connected
                                case 1:
                                    if (!isPlaying()) {
                                        resumeMediaPlayer();
                                    }
                                    break;
                            }
                        }
                        break;

                    case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                        if (isPlaying()) {
                            pauseMediaPlayer();
                        }
                        break;
                }
            }
        }
    }

    private class MediaObserver implements Runnable{
        private AtomicBoolean stop = new AtomicBoolean(false);

        public void stop() {
            stop.set(true);
        }

        @Override
        public void run() {
            while(!stop.get()){
                try{

                    if(isPlaying()){
                        int percent = mediaPlayer.getCurrentPosition() * 100 / mediaPlayer.getDuration();
                        playerListener.onPositionChanged(percent);
                    }

                    Thread.sleep(100);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}