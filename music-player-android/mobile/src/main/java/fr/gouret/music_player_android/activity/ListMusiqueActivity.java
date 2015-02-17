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
import fr.gouret.music_player_android.helper.MusicControllerHelper;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.model.MusicController;

public class ListMusiqueActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_musique);
        Bundle extras = getIntent().getExtras();
        int position = extras.getInt("position");
        String desired = extras.getString("desiredString");

        Bundle arguments = new Bundle();
            arguments.putString("desiredString",extras.getString("desiredString"));
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
}
