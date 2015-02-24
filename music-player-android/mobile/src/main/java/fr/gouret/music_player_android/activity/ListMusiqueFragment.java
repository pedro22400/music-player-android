package fr.gouret.music_player_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.zip.Inflater;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.adapter.SongAdapter;
import fr.gouret.music_player_android.asynctask.DownloadSongAsyncTask;
import fr.gouret.music_player_android.model.MusicController;
import fr.gouret.music_player_android.model.Song;


public class ListMusiqueFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<Song>> {

    //song list variables
    private View songHeaderView;

    int postion;
    String desiredString;


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        Log.d("ListMusique", "onCreateView");
//        View rootView = inflater.inflate(R.layout.list_musique, container, false);
//
//        return rootView;
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ListMusique", "Oncreated");
       


    }


    @Override
    public void onStart(){
        super.onStart();
        Log.d("ListMusique","onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("ListMusique", "onResume");
        getLoaderManager().initLoader(0, null, this).forceLoad();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        Bundle bundle = this.getArguments();
        if (bundle != null){
            postion= bundle.getInt("position", -1);
            desiredString = bundle.getString("desiredString");
        }
        setEmptyText("Pas de musique disponible");
        getListView().setBackgroundColor(getResources().getColor(R.color.white));
        ServiceHelper.getInstance().startMusiqueService(this.getActivity());

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
        setListAdapter(songAdt);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        if (ServiceHelper.getInstance().getMusicService() != null){
            ServiceHelper.getInstance().getMusicService().setList(data);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Song>> loader) {

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ServiceHelper.getInstance().getMusicService().setSong(position);
        Intent intent = new Intent(this.getActivity(), PlayNowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.anim_up, R.anim.anim_down);
    }
}
