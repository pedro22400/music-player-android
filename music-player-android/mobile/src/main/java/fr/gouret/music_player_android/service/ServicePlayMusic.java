package fr.gouret.music_player_android.service;

/**
 * Created by pierregouret on 13/02/15.
 */


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RemoteControlClient;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.activity.ListMusique;
import fr.gouret.music_player_android.external.RemoteControlClientCompat;
import fr.gouret.music_player_android.external.RemoteControlHelper;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.notification.NotificationMusic;


/**
 * Service that makes the music play and notifies every action.
 *
 * Tasks:
 *
 * - Abstracts controlling the native Android MediaPlayer;
 * - Keep showing a system Notification with info on
 *   currently playing song;
 * - Starts the other service, `MusicScrobblerService`
 *   (if set on Settings) that scrobbles songs to Last.fm;
 * - LocalBroadcasts every action it takes;
 * - Keep watching for headphone/headset events with
 *   a Broadcast - and react accordingly.
 *
 * Broadcasts:
 *
 * This service makes sure to broadcast every action it
 * takes.
 *
 * It sends a LocalBroadcast of name `BROADCAST_EVENT_NAME`,
 * of which you can get it's action with the following
 * extras:
 *
 * - String BROADCAST_EXTRA_ACTION: Current action it's taking.
 *
 * - Long   BROADCAST_EXTRA_SONG_ID: ID of the Song it's taking
 *                                   action into.
 *
 * For example, see the following scenarios:
 *
 * - Starts playing Song with ID 1.
 *   + Send a LocalBroadcast with `BROADCAST_EXTRA_ACTION`
 *     of `BROADCAST_EXTRA_PLAYING` and
 *     `BROADCAST_EXTRA_SONG_ID` of 1.
 *
 * - User skips to a Song with ID 2:
 *   + Send a LocalBroadcast with `BROADCAST_EXTRA_ACTION`
 *     of `BROADCAST_EXTRA_SKIP_NEXT` and
 *     `BROADCAST_EXTRA_SONG_ID` of 1.
 *   + Send a LocalBriadcast with `BROADCAST_EXTRA_ACTION`
 *     of `BROADCAST_EXTRA_PLAYING` and
 *     `BROADCAST_EXTRA_SONG_ID` of 2.
 *
 * @note It keeps the music playing even when the
 *       device is locked.
 *       For that, we must add a special permission
 *       on the AndroidManifest.
 *
 * Thanks:
 * - Google's MediaPlayer guide - has info on AudioFocus,
 *   Services and lotsa stuff
 *   http://developer.android.com/guide/topics/media/mediaplayer.html
 */
public class ServicePlayMusic extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();

    private String songTitle;
    private static final int NOTIFY_ID=1;
    
    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    //pass song list
    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    //binder
    public class MusicBinder extends Binder {
        public ServicePlayMusic getService() {
            return ServicePlayMusic.this;
        }
    }

    //activity will bind to service
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    //release resources when unbind
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    //play a song
    public void playSong(){
        //play
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        songTitle = playSong.getTitle();
        //get id
        long currSong = playSong.getId();
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);
        //set the data source
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    //set the song
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        songTitle=songs.get(songPosn).getTitle();
        Intent notIntent = new Intent(this, ListMusique.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
        .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }
    
    public void playNext(){
        this.songPosn++;
        if (songPosn==songs.size() ){
            songPosn = 0;
        }
        playSong();
        
    }

    public void playPrev(){
        this.songPosn--;
        if (songPosn<0){
            songPosn = songs.size()-1; 
        }
        playSong();

    }



}
