package fr.gouret.music_player_android.asynctask;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

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

    public DownloadSongAsyncTask(Context context) {
        super(context);
    }
    @Override
    public ArrayList<Song> loadInBackground() {
        return SongList.getInstance().scanSongs(getContext(), "external");
    }

}
