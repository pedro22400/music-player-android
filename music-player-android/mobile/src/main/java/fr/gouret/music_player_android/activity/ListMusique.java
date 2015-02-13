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
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.SongAdapter;
import fr.gouret.music_player_android.asynctask.DownloadSongAsyncTask;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.service.ServicePlayMusic;


public class ListMusique extends Activity implements LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    //song list variables
    private ListView songView;
    private ServicePlayMusic musicService= null ;
    private Intent playIntent;
    private boolean musicBound=false;
    private ArrayList<Song> songs;
   


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieve list view
        songView = (ListView)findViewById(R.id.song_list);

        //create and set adapter
        getLoaderManager().initLoader(0, null, this).forceLoad();
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
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        unbindService(musicConnection);
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
        this.songs = songs; 
        final SongAdapter songAdt = new SongAdapter(this.getApplicationContext(),songs);
        songView.setAdapter(songAdt);
        songView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        musicService.setList(songs);
        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                musicService.setSong(i);
                musicService.playSong();
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Song>> arrayListLoader) {

    }


}
