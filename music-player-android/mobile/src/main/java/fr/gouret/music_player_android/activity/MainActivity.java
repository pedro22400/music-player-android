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
public class MainActivity extends FragmentActivity implements
        ActionBar.TabListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Initialize the ViewPager and set an adapter
        findViewById(R.id.lecture_aleatoire).setVisibility(View.VISIBLE);
        findViewById(R.id.lecture_aleatoire).setBackgroundColor(getResources().getColor(R.color.white));


        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0){
                    findViewById(R.id.lecture_aleatoire).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.lecture_aleatoire).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0){
                    findViewById(R.id.lecture_aleatoire).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.lecture_aleatoire).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

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
