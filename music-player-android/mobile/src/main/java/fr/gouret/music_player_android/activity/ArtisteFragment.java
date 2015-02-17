package fr.gouret.music_player_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.adapter.AlbumAdpater;
import fr.gouret.music_player_android.adapter.ArtistAdapter;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 16/02/15.
 */
public class ArtisteFragment extends Fragment {
    ListView albumView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_musique, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        albumView = (ListView) getView().findViewById(R.id.song_list);
        ArtistAdapter songAdt = new ArtistAdapter(this.getActivity().getApplicationContext(), SongList.getInstance().getArtists());
        albumView.setAdapter(songAdt);
        albumView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ListMusiqueActivity.class);
                intent.putExtra("position",2);
                intent.putExtra("desiredString",SongList.getInstance().getArtists().get(i));
                ArtisteFragment.this.getActivity().startActivity(intent);

            }
        });
    }
}
