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
import fr.gouret.music_player_android.adapter.GenreAdapter;
import fr.gouret.music_player_android.model.SongList;

/**
 * Created by pierregouret on 16/02/15.
 */
public class GenreFragment extends Fragment {
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
        GenreAdapter songAdt = new GenreAdapter(this.getActivity().getApplicationContext(), SongList.getInstance().getGenres(this.getActivity()));
        albumView.setAdapter(songAdt);
        albumView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        albumView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ListMusiqueActivity.class);
                intent.putExtra("position",3);
                intent.putExtra("desiredString",SongList.getInstance().getGenres(getActivity()).get(i));
                GenreFragment.this.getActivity().startActivity(intent);

            }
        });
    }
}

