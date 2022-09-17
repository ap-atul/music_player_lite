package com.atul.musicplayer.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

public class PlayerBuilder {
    private final Context context;
    private final PlayerListener playerListener;
    private PlayerService playerService;
    private PlayerManager playerManager;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(@NonNull final ComponentName componentName, @NonNull final IBinder iBinder) {
            playerService = ((PlayerService.LocalBinder) iBinder).getInstance();
            playerManager = playerService.getPlayerManager();

            if (playerListener != null) {
                playerManager.setPlayerListener(playerListener);
            }
        }

        @Override
        public void onServiceDisconnected(@NonNull final ComponentName componentName) {
            playerService = null;
        }
    };

    public PlayerBuilder(Context context, PlayerListener listener) {
        this.context = context;
        this.playerListener = listener;

        bindService();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void bindService() {
        context.bindService(new Intent(context, PlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        context.startService(new Intent(context, PlayerService.class));
    }

    public void unBindService() {
        if (playerManager != null) {
            playerManager.detachService();
            context.unbindService(serviceConnection);
        }
    }
}
