package fr.gouret.music_player_android;/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */




import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.List;

import fr.gouret.music_player_android.R;

/**
 * Shows events and photo from the Wearable APIs.
 */
public class MainActivity extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener {

    private static final String IMAGE_PATH = "/image";
    private static final String IMAGE_KEY = "photo";
    private static final String TAG = "MainActivity";
    private View relativeLayout ;
    private GoogleApiClient mGoogleApiClient;

    private Handler mHandler;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        mHandler = new Handler();
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        relativeLayout = (View)findViewById(R.id.cover);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged(): " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (final DataEvent event : events) {
            String path = event.getDataItem().getUri().getPath();
            if (IMAGE_PATH.equals(path)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset photo = dataMapItem.getDataMap()
                        .getAsset(IMAGE_KEY);
                final Bitmap bitmap =loadBitmapFromAsset(mGoogleApiClient, photo);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        relativeLayout = (View)findViewById(R.id.cover);
                        Log.d(TAG, "Setting background image..");
                        relativeLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
                    }
                });
            } else if ("/update-song".equals(path)){
                final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView txTitle = (TextView) findViewById(R.id.title);
//                        TextView txAlbum = (TextView) findViewById(R.id.album);
                        TextView txArtiste = (TextView) findViewById(R.id.artist);
                        txTitle.setText(dataMapItem.getDataMap().getString("Titre"));
//                        txAlbum.setText(dataMapItem.getDataMap().getString("Album"));
                        txArtiste.setText(dataMapItem.getDataMap().getString("Artiste"));
                    }
                });
            }
        }
    }
    @Override
    public void onMessageReceived(final MessageEvent event) {
        Log.d(TAG, "onMessageReceived " + event.getPath());
        if (event.getPath().equals("/start-activity")){

        }
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConnected");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected");
    }

    public void play(View v){
        final ImageView img = (ImageView)findViewById(R.id.play_pause);
        Wearable.MessageApi.sendMessage(mGoogleApiClient, "Action", "PlAY",new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (sendMessageResult.getStatus().isSuccess()){
                    if (img.getTag() == null || img.getTag().equals(Boolean.TRUE)){
                        img.setImageResource(R.mipmap.pause);
                        img.setTag(Boolean.FALSE);
                    } else {
                        img.setImageResource(R.mipmap.play);
                        img.setTag(Boolean.TRUE);
                    }

                }
            }
        });
    }
    public void next(View v){
        Wearable.MessageApi.sendMessage(mGoogleApiClient, "Action", "NEXT",new byte[0]);

    }
    public void prev(View v){
        Wearable.MessageApi.sendMessage(mGoogleApiClient, "Action", "PREV",new byte[0]);
    }

    /**
     * Extracts {@link android.graphics.Bitmap} data from the
     * {@link com.google.android.gms.wearable.Asset}
     */
    private Bitmap loadBitmapFromAsset(GoogleApiClient apiClient, Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                apiClient, asset).await().getInputStream();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        return BitmapFactory.decodeStream(assetInputStream);
    }
}
