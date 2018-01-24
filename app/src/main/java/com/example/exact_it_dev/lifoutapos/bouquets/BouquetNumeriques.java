package com.example.exact_it_dev.lifoutapos.bouquets;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.bouquets.canalplus.Canalplus;
import com.example.exact_it_dev.lifoutapos.bouquets.easytv.EasyTV;

public class BouquetNumeriques extends AppCompatActivity {
    Button canal,easy,startimes,dstv;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouquet_numeriques);
        setTitle("Les bouquet numériques");

        //play the sound
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.bouquets);
        mediaPlayer.start();

        //Bouton canal
        canal = (Button)findViewById(R.id.canalBT);
        canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CanalIntent = new Intent(getApplicationContext(),Canalplus.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Canal Plus",Toast.LENGTH_SHORT).show();
                startActivity(CanalIntent);
            }
        });

        //Bouton Easy
        easy = (Button)findViewById(R.id.easyBT);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent EasyIntent = new Intent(getApplicationContext(),EasyTV.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Canal Easy TV",Toast.LENGTH_SHORT).show();
                startActivity(EasyIntent);
            }
        });

        //Bouton Startime
        startimes = (Button)findViewById(R.id.starttimeBT);
        startimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartIntent = new Intent(getApplicationContext(),Canalplus.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Canal Startime",Toast.LENGTH_SHORT).show();
                startActivity(StartIntent);
            }
        });

        //Bouton dstv
        dstv = (Button)findViewById(R.id.dstvBT);
        dstv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent DstvIntent = new Intent(getApplicationContext(),Canalplus.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur DSTV",Toast.LENGTH_SHORT).show();
                startActivity(DstvIntent);
            }
        });
    }
}
