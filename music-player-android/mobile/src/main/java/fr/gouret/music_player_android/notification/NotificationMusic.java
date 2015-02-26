package fr.gouret.music_player_android.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import fr.gouret.music_player_android.R;
import fr.gouret.music_player_android.activity.MainActivity;
import fr.gouret.music_player_android.activity.PlayNowActivity;
import fr.gouret.music_player_android.helper.ServiceHelper;
import fr.gouret.music_player_android.model.Song;

/**
 * Created by pierregouret on 13/02/15.
 */
public class NotificationMusic extends Notification {
    /**
     * Reference to the context that notified.
     */
    Context context = null;

    /**
     * Reference to the service we're attached to.
     */
    Service service = null;

    /**
     * Used to create and update the same notification.
     */
    Builder notificationBuilder = null;

    /**
     * Custom appearance of the notification, also updated.
     */
    RemoteViews notificationView = null;

    /**
     * Used to actually broadcast the notification.
     * Depends on the Activity that originally called
     * the nofitication.
     */
    NotificationManager notificationManager = null;

    /**
     * Sends a system notification with a song's information.
     *
     * If the user clicks the notification, will be redirected
     * to the "Now Playing" Activity.
     *
     * If the user clicks on any of the buttons inside it,
     * custom actions will be executed on the
     * `NotificationButtonHandler` class.
     *
     * @param context Activity that calls this function.
     * @param service Service that calls this function.
     *                Required so the Notification can
     *                run on the background.
     * @param song    Song which we'll display information.
     *
     * @note By calling this function multiple times, it'll
     *       update the old notification.
     */
    public void notifySong(Context context, Service service, Song song, boolean isPause) {

        if (this.context == null)
            this.context = context;
        if (this.service == null)
            this.service = service;



        // Intent that launches the "Now Playing" Activity
        Intent notifyIntent = new Intent(context, PlayNowActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Letting the Intent be executed later by other application.
        PendingIntent pendingIntent = PendingIntent.getActivity
                (context,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder = new Notification.Builder(context);

        notificationBuilder.setContentIntent(pendingIntent)        ;
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher)        ;
        notificationBuilder.setTicker("Playing '" + song.getTitle() + "' from '" + song.getArtist() + "'")        ;
        notificationBuilder.setOngoing(true)        ;
        notificationBuilder.setContentTitle(song.getTitle())        ;
        notificationBuilder.setContentText(song.getArtist())        ;
        notificationBuilder.setPriority(1);
        //Prev intent
     
        Intent buttonPrevIntent = new Intent(context, NotificationPrevButtonHandler.class);
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(context, 1, buttonPrevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.prev, "Prev", pendingIntentPrev);

        //Pause intent
        if (isPause){
            Intent buttonPlayIntent = new Intent(context, NotificationPlayButtonHandler.class);
            PendingIntent pendingIntentplay = PendingIntent.getBroadcast(context, 3, buttonPlayIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.addAction(R.drawable.play, "Play", pendingIntentplay);
        } else {
            Intent buttonPauseIntent = new Intent(context, NotificationPauseButtonHandler.class);
            PendingIntent pendingIntentpause = PendingIntent.getBroadcast(context, 3, buttonPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.addAction(R.drawable.pause, "Pause", pendingIntentpause);
        }

        //Next intent
        Intent buttonSkipIntent = new Intent(context, NotificationNextButtonHandler.class);
        PendingIntent pendingIntentnext = PendingIntent.getBroadcast(context, 5, buttonSkipIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.addAction(R.drawable.next, "Next", pendingIntentnext);
//        notificationBuilder.setLocalOnly(true);
        // create notification
        Notification notif =   notificationBuilder.build();
        notif.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
//        notif.defaults |= Notification.DEFAULT_LIGHTS; // LED
//        notif.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
//        notif.defaults |= Notification.DEFAULT_SOUND; // Sound
//        // Cancel the notification after its selected

        // (why not the former commented line?)
        service.startForeground(3, notif);
    }

    /**
     * Called when user clicks the "play/pause" button on the on-going system Notification.
     */
    public static class NotificationPauseButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceHelper.getInstance().getMusicService().pausePlayer();
        }
    }

    public static class NotificationPlayButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceHelper.getInstance().getMusicService().playSong();
        }
    }

    /**
     * Called when user clicks the "play/pause" button on the on-going system Notification.
     */
    public static class NotificationNextButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceHelper.getInstance().getMusicService().playNext();
        }
    }


    public static class NotificationPrevButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceHelper.getInstance().getMusicService().playPrev();
        }
    }


    public static class NotificationSeekToButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int curr = ServiceHelper.getInstance().getMusicService().getPosn();
            ServiceHelper.getInstance().getMusicService().seek(curr+15);
        }
    }

    public static class NotificationSeekFromButtonHandler extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ServiceHelper.getInstance().getMusicService().playPrev();
            int curr = ServiceHelper.getInstance().getMusicService().getPosn();
            ServiceHelper.getInstance().getMusicService().seek(curr-15);
        }
    }
    /**
     * Cancels this notification.
     */
    public void cancel() {
        service.stopForeground(true);
//
//        notificationManager.cancel(NOTIFICATION_ID);
    }

    /**
     * Cancels all sent notifications.
     */
    public static void cancelAll(Context c) {
        NotificationManager manager = (NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }
    
}
