package fr.gouret.music_player_android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import fr.gouret.music_player_android.helper.MusicControllerHelper;
import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.adapter.SongAdapter;
import fr.gouret.music_player_android.asynctask.DownloadSongAsyncTask;
import fr.gouret.music_player_android.model.MusicController;
import fr.gouret.music_player_android.model.Song;


public class ListMusiqueFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    //song list variables
    private ListView songView;

    int postion;
    String desiredString;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("ListMusique", "onCreateView");
        View rootView = inflater.inflate(R.layout.list_musique, container, false);
        songView = (ListView) rootView.findViewById(R.id.song_list);
        Bundle bundle = this.getArguments();
        if (bundle != null){
            postion= bundle.getInt("position", -1);
            desiredString = bundle.getString("desiredString");
        }
        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ListMusique", "Oncreated");
        ServiceHelper.getInstance().startMusiqueService(this.getActivity());


    }


    @Override
    public void onStart(){
        super.onStart();
        Log.d("ListMusique","onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ListMusique","onResume");
        getLoaderManager().initLoader(0, null, this).forceLoad();
        MusicControllerHelper.getInstance().initMusicController(this.getActivity());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("ListMusique","onPause");

    }

    @Override
    public void onStop() {
        Log.d("ListMusique","onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ListMusique","onDestroy");
        ServiceHelper.getInstance().stopMusiqueService(this.getActivity());
    }

    @Override
    public android.support.v4.content.Loader<ArrayList<Song>> onCreateLoader(int i, Bundle bundle) {
        return new DownloadSongAsyncTask(this.getActivity().getBaseContext(), postion, desiredString);

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Song>> loader, ArrayList<Song> data) {
        //create and set adapter
        final SongAdapter songAdt = new SongAdapter(this.getActivity().getApplicationContext(), data);
        songView.setAdapter(songAdt);
        songView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        if (ServiceHelper.getInstance().getMusicService() != null){
            ServiceHelper.getInstance().getMusicService().setList(data);
        }

        songView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ServiceHelper.getInstance().getMusicService().setSong(i);
                ServiceHelper.getInstance().getMusicService().playSong();
                MusicControllerHelper.getInstance().getMusicController().show();

            }
        });

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Song>> loader) {

    }
}
