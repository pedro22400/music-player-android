package fr.gouret.music_player_android.asynctask;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 12/02/15.
 */
public class AlbumAsyncTask extends AsyncTaskLoader<ArrayList<String>> {

    public AlbumAsyncTask(Context context) {
        super(context);
    }



    @Override
    public ArrayList<String> loadInBackground() {
        while ( SongList.getInstance().isScanning());
        return SongList.getInstance().getAlbums();
    }

}
