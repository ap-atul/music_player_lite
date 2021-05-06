package com.atul.musicplayerlite.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.atul.musicplayerlite.MPConstants;

public class PlayerBuilder {
    private final Context context;
    private PlayerService playerService;
    private PlayerManager playerManager;
    private PlayerNotificationManager notificationManager;
    private final PlayerListener playerListener;

    private boolean serviceBound = false;

    public PlayerBuilder(Context context, PlayerListener listener){
        this.context = context;
        this.playerListener = listener;

        if (!serviceBound)
            bindService();
    }

    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    public PlayerNotificationManager getNotificationManager(){
        return notificationManager;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(@NonNull final ComponentName componentName, @NonNull final IBinder iBinder) {
            playerService = ((PlayerService.LocalBinder) iBinder).getInstance();
            playerManager = playerService.getPlayerManager();
            notificationManager = playerService.getNotificationManager();
            serviceBound = true;

            if (playerListener != null) {
                playerManager.setPlayerListener(playerListener);
            }
        }

        @Override
        public void onServiceDisconnected(@NonNull final ComponentName componentName) {
            playerService = null;
            serviceBound = false;
        }
    };

    private void bindService() {
        context.bindService(new Intent(context, PlayerService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        context.startService(new Intent(context, PlayerService.class));
    }

    private void unBindService (){
        if (serviceBound){
            context.unbindService(serviceConnection);
            serviceBound = false;
        }
    }
}
