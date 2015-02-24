package fr.gouret.music_player_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.AlbumAdpater;
import fr.gouret.music_player_android.asynctask.AlbumAsyncTask;
import fr.gouret.music_player_android.model.Song;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 16/02/15.
 */
public class AlbumFragment extends ListFragment implements LoaderManager.LoaderCallbacks<ArrayList<String>>{


    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("Pas d'album");
//        return SongList.getInstance().getAlbums();
        this.getListView().setBackgroundColor(getResources().getColor(R.color.white));
        this.getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        AlbumAdpater songAdt = new AlbumAdpater(this.getActivity().getApplicationContext(), SongList.getInstance().getAlbums());
        this.setListAdapter(songAdt);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(getActivity(), ListMusiqueActivity.class);
        intent.putExtra("position",1);
        intent.putExtra("desiredString",SongList.getInstance().getAlbums().get(position));
        AlbumFragment.this.getActivity().startActivity(intent);
        this.getActivity().overridePendingTransition(R.anim.anim_left, R.anim.anim_right);
    }

    @Override
    public void onResume() {
        super.onResume();
//        getLoaderManager().initLoader(0, null, this).forceLoad();
    }


    @Override
    public android.support.v4.content.Loader<ArrayList<String>> onCreateLoader(int i, Bundle bundle) {
        return new AlbumAsyncTask(this.getActivity().getBaseContext());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<String>> loader, ArrayList<String> data) {
      
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<String>> loader) {

    }

}
