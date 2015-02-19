package fr.gouret.music_player_android.model;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;

import fr.gouret.music_player_android.helper.MusicControllerHelper;

/**
 * Created by pierregouret on 13/02/15.
 */
public class MusicController extends MediaController {

    public MusicController(Context c) {
        super(c);
    }

    public void hide() {
        super.hide();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.hide();
            MusicControllerHelper.getInstance().getAct().finish();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
    

}
