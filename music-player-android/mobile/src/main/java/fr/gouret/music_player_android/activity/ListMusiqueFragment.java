package fr.gouret.music_player_android.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MediaController;

import java.util.ArrayList;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.SongAdapter;
import fr.gouret.music_player_android.asynctask.DownloadSongAsyncTask;
import fr.gouret.music_player_android.model.MusicController;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.service.ServicePlayMusic;
import quickScroll.QuickScroll;


public class ListMusiqueFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Song>>, MediaController.MediaPlayerControl {

    //song list variables
    private ListView songView;
    private ServicePlayMusic musicService = null;
    private Intent playIntent;
    private boolean musicBound = false;
    private MusicController controller;
    int postion;
    String desiredString;

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_musique, container, false);
        songView = (ListView) rootView.findViewById(R.id.song_list);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            postion= bundle.getInt("position", -1);
            desiredString = bundle.getString("desiredString");
        }
        return rootView;
    }



    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ServicePlayMusic.MusicBinder binder = (ServicePlayMusic.MusicBinder) service;
            //get service
            musicService = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };



    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this.getActivity().getApplicationContext(), ServicePlayMusic.class);
            this.getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            this.getActivity().startService(playIntent);
        }        //create and set adapter
        getLoaderManager().initLoader(0, null, this).forceLoad();
        initController();
    }

    @Override
    public void onStop() {

        if (musicBound){
            this.getActivity().unbindService(musicConnection);
        }

        super.onStop();
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Song>> onCreateLoader(int i, Bundle bundle) {
        return new DownloadSongAsyncTask(this.getActivity().getBaseContext(), postion, desiredString);

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Song>> loader, ArrayList<Song> data) {
        final SongAdapter songAdt = new SongAdapter(this.getActivity().getApplicationContext(), data);


        songView.setAdapter(songAdt);
        songView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        if (musicService != null){
            musicService.setList(data);
        }


        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                musicService.setSong(i);
                musicService.playSong();
                controller.show(0);

            }
        });

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Song>> loader) {

    }

    private void initController() {
        controller = new MusicController(this.getActivity().getBaseContext());
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playNext();
                controller.show(0);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.playPrev();
                controller.show(0);
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(this.getActivity().findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    public void pause() {
        musicService.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicBound && musicService.isPng())
            return musicService.getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService.isPng()) {
            return musicService.getPosn();
        }
        return 0;
    }

    @Override
    public void seekTo(int i) {
        musicService.seek(i);
    }

    @Override
    public boolean isPlaying() {
        return musicService.isPng();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
