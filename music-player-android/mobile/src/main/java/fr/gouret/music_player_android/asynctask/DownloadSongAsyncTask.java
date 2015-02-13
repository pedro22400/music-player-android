package fr.gouret.music_player_android.asynctask;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.activity.ListMusique;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 12/02/15.
 */
public class DownloadSongAsyncTask extends AsyncTaskLoader<ArrayList<Song>> {
    SongList songList;
    Context context;
    
    
   public DownloadSongAsyncTask(Context c) {
       super(c);
       context=c;
   }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        songList = SongList.getInstance();
    }

    @Override
    public ArrayList<Song> loadInBackground() {
        songList.scanSongs(context, "external");
        return songList.getSongs();
    }
}
