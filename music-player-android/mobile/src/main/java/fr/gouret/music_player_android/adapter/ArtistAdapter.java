package fr.gouret.music_player_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 16/02/15.
 */
public class ArtistAdapter extends BaseAdapter
{

    //song list and layout
    private ArrayList<String> albumList;
    private LayoutInflater songInf;
    private Context c;

    //constructor
    public ArtistAdapter(Context c, ArrayList<String> albumListe){
        albumList=albumListe;
        songInf=LayoutInflater.from(c);
        this.c= c;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public String getItem(int arg0) {
        return albumList.get(arg0)  ;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout)convertView;
        if (songLay == null){
            songLay = (LinearLayout)songInf.inflate(R.layout.album_adapter, parent, false);
        }
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        ImageView imageView = (ImageView)songLay.findViewById(R.id.art_album);
        if (albumList.size() != 0) {
            //get song using position
            String currAlbum = albumList.get(position);
            Song currSong = SongList.getInstance().getSongsByArtist(currAlbum).get(0);

            //get title and artist strings
            songView.setText(currAlbum);
            artistView.setText(currSong.getAlbum());
            imageView.setImageBitmap(currSong.getImage(this.c));
            //set position as tag
            songLay.setTag(position);
        }
        return songLay;
    }
}
