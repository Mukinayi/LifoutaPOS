package com.example.exact_it_dev.lifoutapos.bouquets.dstv;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.exact_it_dev.lifoutapos.R;

public class DSTV extends AppCompatActivity {
        MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dstv);
        setTitle("Renouvellement DSTV");

        //play the sound
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.dstv);
        mediaPlayer.start();
    }
}
