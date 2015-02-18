package fr.gouret.music_player_android.service;

/**
 * Created by pierregouret on 13/02/15.
 */


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.activity.ListMusiqueFragment;
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
        MediaPlayer.OnCompletionListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener, DataApi.DataListener {


    private boolean shuffle=false;
    private Random rand;
    private static final String IMAGE_PATH = "/image";
    private static final String IMAGE_KEY = "photo";
    private static final int REQUEST_RESOLVE_ERROR = 34;
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    //binder
    private final IBinder musicBind = new MusicBinder();
    private GoogleApiClient mGoogleApiClient;


    private String songTitle;
    private static final int NOTIFY_ID=1;
    
    public void onCreate(){
        //create the service
        super.onCreate();
        //initialize position
        songPosn=0;
        rand=new Random();
        //create player
        player = new MediaPlayer();
        //initialize
        initMusicPlayer();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        super.onDestroy();
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

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            mGoogleApiClient.connect();
        } else {
            Log.e("ListMusique", "Connection to Google API client has failed");
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        }
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("TAG", "onMessageReceived() A message from watch was received:" + messageEvent
                .getRequestId() + " " + messageEvent.getPath());
        if (messageEvent.getPath().equals("PLAY")){
            if (this.isPng()){
                this.pausePlayer();
            } else {
                this.playSong();
            }
        } else if (messageEvent.getPath().equals("STOP")){

        }else if (messageEvent.getPath().equals("NEXT")){
            this.playNext();
        }else if (messageEvent.getPath().equals("PREV")){
            this.playPrev();
        }else {
            Toast.makeText(this, "Action non valide", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        
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
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, "blabla", "/start-activity", new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e("ListMusique", "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        } else {
                            Log.e("ListMusique", "Success");
                        }
                    }
                }
        );
        sendInfo(playSong);
        sendPhoto(playSong.getImage(this.getBaseContext()));

    }

    public void sendInfo(Song song){
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, "envoieTitre", song.getTitle() + "/" + song.getAlbum() + "/"+song.getArtist() + "/", new byte[0]
        );

        

    }

    private void sendPhoto(Bitmap bitmap) {
        PutDataMapRequest dataMap = PutDataMapRequest.create(IMAGE_PATH);
        dataMap.getDataMap().putAsset(IMAGE_KEY,toAsset(bitmap));
        dataMap.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.d("Service", "Sending image was successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });

    }

    private static Asset toAsset(Bitmap bitmap){
        ByteArrayOutputStream byteStream = null;
        try {
            byteStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
            return Asset.createFromBytes(byteStream.toByteArray());
        } finally {
            if (null != byteStream) {
                try {
                    byteStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        
    } 

    
    //set the song
    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("ServicePlayMusic", "onCompletion");
        playNext();
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
        NotificationMusic notificationMusic = new NotificationMusic(); 
        notificationMusic.notifySong(getBaseContext(), this,songs.get(songPosn), !isPng() );


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
        NotificationMusic notificationMusic = new NotificationMusic();
        notificationMusic.notifySong(getBaseContext(), this,songs.get(songPosn), !isPng() );
        
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }
    
    public void playNext(){
        if (shuffle){
            this.songPosn = rand.nextInt(songs.size()); 
        } else {
            this.songPosn++;
            if (songPosn==songs.size() ){
                songPosn = 0;
            }
        }
        

        playSong();
        
    }

    public void playPrev(){
        if (shuffle){
            this.songPosn = rand.nextInt(songs.size());
        } else {
            this.songPosn--;
            if (songPosn < 0) {
                songPosn = songs.size() - 1;
            }
        }
        playSong();

    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }
    
    public boolean isShuffle(){
        return shuffle;
        
    }


}
