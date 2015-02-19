package fr.gouret.music_player_android.helper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import fr.gouret.music_player_android.service.ServicePlayMusic;

/**
 * Created by pierregouret on 17/02/15.
 */
public class ServiceHelper {
    private ServicePlayMusic musicService = null;

    private boolean musicBound = false;

    private Intent playIntent;
    
    private int selectedItem = 0;


    private static ServiceHelper ourInstance = new ServiceHelper();

    public static ServiceHelper getInstance() {
        return ourInstance;
    }

    private ServiceHelper() {
    }

    public ServicePlayMusic getMusicService() {
        return musicService;
    }


    public boolean isMusicBound() {
        return musicBound;
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("ListMusique", "service start");

            ServicePlayMusic.MusicBinder binder = (ServicePlayMusic.MusicBinder) service;
            //get service
            musicService = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("ListMusique","service stop");
            musicBound = false;
        }
    };

    public Intent getPlayIntent() {
        return playIntent;
    }
    
    public void startMusiqueService(Activity act){
        if (playIntent == null) {
            playIntent = new Intent(act.getApplicationContext(), ServicePlayMusic.class);
            act.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            act.startService(playIntent);
        }
        
    }
    
    public void stopMusiqueService(Activity act){
        if (musicBound){
            try {
                act.unbindService(musicConnection);
                act.stopService(playIntent);
                playIntent = null;
                musicBound = false;
            } catch (IllegalArgumentException e){
                
            }


        }
        
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }
}
