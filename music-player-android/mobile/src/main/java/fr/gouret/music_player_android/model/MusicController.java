package fr.gouret.music_player_android.model;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by pierregouret on 13/02/15.
 */
public class MusicController extends MediaController {

    public MusicController(Context c) {
        super(c);
    }

    public void hide() {
        this.show();
    }

}
