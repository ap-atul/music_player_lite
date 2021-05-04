package com.atul.musicplayerlite.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.atul.musicplayerlite.model.Music;

import java.util.List;

public class PlayerManager {
    private Context context;
    private MusicService mMusicService;
    private PlayerAdapter mPlayerAdapter;
    private MusicNotificationManager mMusicNotificationManager;
    private PlaybackListener mPlaybackListener;

    private boolean mUserIsSeeking = false;
    private boolean sBound = false;

    public PlayerManager(Context context) {
        this.context = context;

        doBindService();
    }

    private void doBindService() {
        context.bindService(new Intent(context,
                MusicService.class), mConnection, Context.BIND_AUTO_CREATE);
        sBound = true;

        final Intent startNotStickyIntent = new Intent(context, MusicService.class);
        context.startService(startNotStickyIntent);
    }

    private void doUnbindService() {
        if (sBound) {
            // Detach our existing connection.
            context.unbindService(mConnection);
            sBound = false;
        }
    }

    public void setSong(List<Music> musicList){
        mPlayerAdapter.setCurrentSong(musicList.get(0), musicList);
        mPlayerAdapter.initMediaPlayer(musicList.get(0));
    }

    public Music getCurrentSong() {
        return mPlayerAdapter.getCurrentSong();
    }

    public boolean isStarted(){
        return sBound;
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(@NonNull final ComponentName componentName, @NonNull final IBinder iBinder) {
            mMusicService = ((MusicService.LocalBinder) iBinder).getInstance();
            mPlayerAdapter = mMusicService.getMediaPlayerHolder();
            mMusicNotificationManager = mMusicService.getMusicNotificationManager();

            Log.i("SERVICE", "service connected");

            if (mPlaybackListener == null) {
                mPlaybackListener = new PlaybackListener();
                mPlayerAdapter.setPlaybackInfoListener(mPlaybackListener);
            }

        }

        @Override
        public void onServiceDisconnected(@NonNull final ComponentName componentName) {
            mMusicService = null;
        }
    };

    class PlaybackListener extends PlaybackInfoListener {

        @Override
        public void onPositionChanged(int position) {

        }

        @Override
        public void onStateChanged(@State int state) {
        }

        @Override
        public void onPlaybackCompleted(){ }
    }

}
