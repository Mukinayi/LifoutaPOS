package com.example.exact_it_dev.lifoutapos.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;


/**
 * Created by EXACT-IT-DEV on 1/9/2018.
 */

public class Playsound{
    public final String MEMORY = "MEMOIRE";
    MediaPlayer mediaPlayer;
    SharedPreferences sharedPreferences;
    Context context;

    public Playsound(Context context){
        this.context = context;
    }

    public void jouer(int song,String textcontent){
        try {
            this.sharedPreferences = this.context.getSharedPreferences(this.MEMORY,Context.MODE_PRIVATE);
            Boolean voice = this.sharedPreferences.getBoolean("voice",false);
            if(voice==true){
                //Toast.makeText(this.context,"Sons joué avec succès",Toast.LENGTH_SHORT).show();
                AudioManager am = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
                am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                mediaPlayer = MediaPlayer.create(this.context,song);
                mediaPlayer.start();
            }else{
                Toast.makeText(this.context,textcontent,Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
