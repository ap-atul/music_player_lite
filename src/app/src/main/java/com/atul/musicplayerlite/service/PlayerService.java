package com.atul.musicplayerlite.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.atul.musicplayerlite.MPConstants;

public class PlayerService extends Service {

    private final IBinder iBinder = new LocalBinder();
    private PlayerManager playerManager;
    private PlayerNotificationManager notificationManager;

    public PlayerService() {
    }

    public PlayerManager getPlayerManager () {
        return playerManager;
    }

    public PlayerNotificationManager getNotificationManager (){
        return notificationManager;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if(playerManager == null){
            playerManager = new PlayerManager(this);
            notificationManager = new PlayerNotificationManager(this);
            playerManager.registerActionsReceiver();
        }

        if(playerManager != null)
            Log.d(MPConstants.DEBUG_TAG, "player manager is not null");
        Log.d(MPConstants.DEBUG_TAG, "Service binded");
        return iBinder;
    }

    @Override
    public void onDestroy() {
        playerManager.unregisterActionsReceiver();
        playerManager.release();
        notificationManager = null;
        super.onDestroy();
    }

    class LocalBinder extends Binder {
        public PlayerService getInstance (){
            return PlayerService.this;
        }
    }
}