package fr.gouret.music_player_android.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.model.Song;
import quickScroll.QuickScroll;
import quickScroll.Scrollable;


public class SongAdapter extends BaseAdapter implements Scrollable{
	
	//song list and layout
	private ArrayList<Song> songs;
	private LayoutInflater songInf;
    private Context c;

	//constructor
	public SongAdapter(Context c, ArrayList<Song> theSongs){
		songs=theSongs;
		songInf=LayoutInflater.from(c);
        this.c= c;
	}

   	@Override
	public int getCount() {
		return songs.size();
	}

	@Override
	public Song getItem(int arg0) {
		return songs.get(arg0)  ;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout)convertView;
        if (songLay == null){
            songLay = (LinearLayout)songInf.inflate(R.layout.song, parent, false);
        }
        		//get title and artist views
		TextView songView = (TextView)songLay.findViewById(R.id.song_title);
		TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        TextView albumView = (TextView)songLay.findViewById(R.id.song_album);

        ImageView imageView = (ImageView)songLay.findViewById(R.id.art_album); 
        
		//get song using position
		Song currSong = songs.get(position);
		//get title and artist strings
		songView.setText(currSong.getTitle());
		artistView.setText(currSong.getArtist());
        albumView.setText(currSong.getAlbum());
        imageView.setImageBitmap(currSong.getImage(this.c));
        
		//set position as tag
		songLay.setTag(position);


        return songLay;
	}

    @Override
    public String getIndicatorForPosition(int childposition, int groupposition) {
        Song song = songs.get(childposition);
        return Character.toString(song.getTitle().charAt(0));
    }

    @Override
    public int getScrollPosition(int childposition, int groupposition) {
        return childposition;
    }
}
