package fr.gouret.music_player_android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.TabsPagerAdapter;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.service.ServicePlayMusic;

/**
 * Created by pierregouret on 16/02/15.
 */
public class MainActivity extends FragmentActivity {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Initialize the ViewPager and set an adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);

    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setCurrentItem(ServiceHelper.getInstance().getSelectedItem());
        
    }

    @Override
    protected void onPause() {
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        ServiceHelper.getInstance().setSelectedItem(pager.getCurrentItem());
        super.onPause();

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
