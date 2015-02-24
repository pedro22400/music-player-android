package fr.gouret.music_player_android.asynctask;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 12/02/15.
 */
public class DownloadSongAsyncTask extends AsyncTaskLoader<ArrayList<Song>> {
    SongList songList;
    int position = -1 ;
    String desiredString = "";

    public DownloadSongAsyncTask(Context context, int position, String desiredString) {
        super(context);
        this.position = position; 
        this.desiredString = desiredString; 
    }



    @Override
    public ArrayList<Song> loadInBackground() {
        if (position != -1) {
            switch (position){
                case 0:
                    return SongList.getInstance().scanSongs(getContext(), "external");
                case 1 :
                    return SongList.getInstance().getSongsByAlbum(desiredString);
                case 2 :
                    return SongList.getInstance().getSongsByArtist(desiredString);
                case 3 :
                    return SongList.getInstance().getSongsByGenre(desiredString);


            }
        }
        return SongList.getInstance().scanSongs(getContext(), "external");



    }

}
