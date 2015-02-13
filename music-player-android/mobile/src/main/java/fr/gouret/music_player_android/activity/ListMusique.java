package fr.gouret.music_player_android.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.SongAdapter;
import fr.gouret.music_player_android.asynctask.DownloadSongAsyncTask;
import fr.gouret.music_player_android.model.MusicController;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.service.ServicePlayMusic;
import quickScroll.QuickScroll;


public class ListMusique extends Activity implements LoaderManager.LoaderCallbacks<ArrayList<Song>>, MediaController.MediaPlayerControl {

    //song list variables
    private ListView songView;
    private ServicePlayMusic musicService= null ;
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<Song> songs;
    private MusicController controller;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieve list view
        songView = (ListView)findViewById(R.id.song_list);



       
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicePlayMusic.MusicBinder binder = (ServicePlayMusic.MusicBinder)service;
            //get service
            musicService = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getApplicationContext(), ServicePlayMusic.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }        //create and set adapter
        getLoaderManager().initLoader(0, null, this).forceLoad();
        initController();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(musicConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public Loader<ArrayList<Song>> onCreateLoader(int i, Bundle bundle) {
        return new DownloadSongAsyncTask(this.getBaseContext());

    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Song>> arrayListLoader, ArrayList<Song> songs) {
        final SongAdapter songAdt = new SongAdapter(this.getApplicationContext(),songs);

        final QuickScroll quickscroll = (QuickScroll) findViewById(R.id.quickscroll);
        quickscroll.init(QuickScroll.TYPE_POPUP, songView, songAdt, QuickScroll.STYLE_HOLO);
        quickscroll.setFixedSize(1);
        quickscroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);

        songView.setAdapter(songAdt);
        songView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        musicService.setList(songs);

        

        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                musicService.setSong(i);
                musicService.playSong();
                controller.show(0);
            }
        });


    }


    @Override
    public void onLoaderReset(Loader<ArrayList<Song>> arrayListLoader) {

    }


    private void initController(){
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playNext();
                controller.show(0);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPrev();
                controller.show(0);
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(musicService!=null && musicBound && musicService.isPng())
        return musicService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService.isPng()){
            return musicService.getPosn(); 
        } 
        return 0;
    }

    @Override
    public void seekTo(int i) {
           musicService.seek(i);
    }

    @Override
    public boolean isPlaying() {
        return musicService.isPng();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
