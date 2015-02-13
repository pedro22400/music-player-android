package fr.gouret.music_player_android.activity;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.SongAdapter;
import fr.gouret.music_player_android.asynctask.DownloadSongAsyncTask;
import fr.gouret.music_player_android.model.Song;


public class ListMusique extends Activity implements LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    //song list variables
    private ListView songView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //retrieve list view
        songView = (ListView)findViewById(R.id.song_list);

        //create and set adapter

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public Loader<ArrayList<Song>> onCreateLoader(int i, Bundle bundle) {
        return new DownloadSongAsyncTask(getApplicationContext());
        
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Song>> arrayListLoader, ArrayList<Song> songs) {
        SongAdapter songAdt = new SongAdapter(this.getApplicationContext(),songs);
        songView.setAdapter(songAdt);
        songView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        songView.setOnItemClickListener(songAdt);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Song>> arrayListLoader) {

    }


}
