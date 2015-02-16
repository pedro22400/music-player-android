package fr.gouret.music_player_android.adapter;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.gouret.music_player_android.activity.AlbumFragment;
import fr.gouret.music_player_android.activity.ArtisteFragment;
import fr.gouret.music_player_android.activity.EmptyFragment;
import fr.gouret.music_player_android.activity.GenreFragment;
import fr.gouret.music_player_android.activity.ListMusiqueFragment;

/**
 * Created by pierregouret on 16/02/15.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter{

    private final String[] TITLES = { "Chansons", "Album", "Artistes", "Genres" };
    
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position){
            case 0:
                return new ListMusiqueFragment();
            case 1: 
                return new AlbumFragment();
            case 2:
                return new ArtisteFragment();
            case 3:
                return new GenreFragment();
            default:
                return new EmptyFragment();
        }
            
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

}

