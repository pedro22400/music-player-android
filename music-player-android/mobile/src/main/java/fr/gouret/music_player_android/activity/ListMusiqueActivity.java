package fr.gouret.music_player_android.activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ListView;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.model.MusicController;

public class ListMusiqueActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_musique);
        Bundle extras = getIntent().getExtras();

        Bundle arguments = new Bundle();
            arguments.putString("desiredString",extras.getString("desiredString"));
        setTitle(extras.getString("desiredString"));
        arguments.putInt("position", extras.getInt("position"));
        ListMusiqueFragment fragment = new ListMusiqueFragment();
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }
    public void random(View v){
        if (ServiceHelper.getInstance().getMusicService().isShuffle()){
            findViewById(R.id.lecture_aleatoire).setBackgroundColor(getResources().getColor(R.color.bleuLight));
        } else {
            findViewById(R.id.lecture_aleatoire).setBackgroundColor(getResources().getColor(R.color.white));
        }
        ServiceHelper.getInstance().getMusicService().setShuffle();

    }
}
