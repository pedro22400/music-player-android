package fr.gouret.music_player_android.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.helper.MusicControllerHelper;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.model.Song;


public class PlayNowActivity extends ActionBarActivity {
    Song currentSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_now);
        MusicControllerHelper.getInstance().initMusicController(this);
        MusicControllerHelper.getInstance().setAct(this);
        setTitle("En ce moment");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initActivity();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        ServiceHelper.getInstance().getMusicService().playSong();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                MusicControllerHelper.getInstance().getMusicController().setAnchorView(findViewById(R.id.mainLay));
                MusicControllerHelper.getInstance().getMusicController().show(0);
            }
        }, 100);

    }

    @Override
    public void finish() {
        super.finish();

    }
 
    public void initActivity(){
        currentSong = ServiceHelper.getInstance().getMusicService().getCurrentSong();

        TextView songView = (TextView)findViewById(R.id.title);
        TextView artistView = (TextView)findViewById(R.id.artiste);
        TextView albumView = (TextView)findViewById(R.id.album);

        ImageView imageView = (ImageView)findViewById(R.id.art_album);

        //get song using position
        songView.setText(currentSong.getTitle());
        artistView.setText(currentSong.getArtist());
        albumView.setText(currentSong.getAlbum());
        imageView.setImageBitmap(currentSong.getImage(this));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("TAG", "OnBackPressed");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_play_now, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_rand:
                 ServiceHelper.getInstance().getMusicService().setShuffle();
                if (ServiceHelper.getInstance().getMusicService().isShuffle()){
                    item.setIcon(R.drawable.rand_selected);
                } else {
                    item.setIcon(R.drawable.rand);
                }
                    return true;
            case R.id.repeat:
                ServiceHelper.getInstance().getMusicService().setLooping();
                if (ServiceHelper.getInstance().getMusicService().isLooping()){
                    item.setIcon(R.drawable.repeat_selected);
                } else {
                    item.setIcon(R.drawable.repeat);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
