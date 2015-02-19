package fr.gouret.music_player_android.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.MediaController;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.activity.PlayNowActivity;
import fr.gouret.music_player_android.model.MusicController;

/**
 * Created by pierregouret on 17/02/15.
 */
public class MusicControllerHelper implements MediaController.MediaPlayerControl {

    private MusicController musicController;
    
    private PlayNowActivity act; 

    private static MusicControllerHelper ourInstance = new MusicControllerHelper();

    public static MusicControllerHelper getInstance() {
        return ourInstance;
    }

    private MusicControllerHelper() {
    }

    public void initMusicController(Context context){
        musicController = new MusicController(context);
        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceHelper.getInstance().getMusicService().playNext();
                act.initActivity();
                musicController.show(0);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceHelper.getInstance().getMusicService().playPrev();
                act.initActivity();
                musicController.show(0);
            }
        });
        musicController.setMediaPlayer(this);
        musicController.setEnabled(true);
    }


    public MusicController getMusicController() {
        return musicController;
    }

    public void setMusicController(MusicController musicController) {
        this.musicController = musicController;
    }
    
    

    @Override
    public void start() {
        ServiceHelper.getInstance().getMusicService().go();
    }

    @Override
    public void pause() {
        ServiceHelper.getInstance().getMusicService().pausePlayer();
    }

    @Override
    public int getDuration() {
        if (ServiceHelper.getInstance().getMusicService() != null && ServiceHelper.getInstance().getMusicService().isPng())
            return ServiceHelper.getInstance().getMusicService().getDur();
        else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (ServiceHelper.getInstance().getMusicService().isPng()) {
            return ServiceHelper.getInstance().getMusicService().getPosn();
        }
        return 0;
    }

    @Override
    public void seekTo(int i) {
        ServiceHelper.getInstance().getMusicService().seek(i);
    }

    @Override
    public boolean isPlaying() {
        return ServiceHelper.getInstance().getMusicService().isPng();
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

    public PlayNowActivity getAct() {
        return act;
    }

    public void setAct(PlayNowActivity act) {
        this.act = act;
    }
}
