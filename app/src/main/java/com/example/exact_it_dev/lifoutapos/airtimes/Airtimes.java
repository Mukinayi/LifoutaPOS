package com.example.exact_it_dev.lifoutapos.airtimes;

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
import com.example.exact_it_dev.lifoutapos.airtimes.airtel.AirtelAirtime;
import com.example.exact_it_dev.lifoutapos.airtimes.azure.AzureAirtime;
import com.example.exact_it_dev.lifoutapos.airtimes.congotelecom.CongotelecomAirtime;
import com.example.exact_it_dev.lifoutapos.airtimes.mtn.MTNAirtime;

public class Airtimes extends AppCompatActivity {

    Button airtel,mtn,congotel,azure;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airtimes);
        setTitle("Vente crédit téléphonique");

        //play the sound
        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.airtimes);
        mediaPlayer.start();

        //Buton Airtel
        airtel = (Button)findViewById(R.id.easyBT);
        airtel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent AirtelIntent = new Intent(getApplicationContext(),AirtelAirtime.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Airtel",Toast.LENGTH_SHORT).show();
                startActivity(AirtelIntent);
            }
        });

        //Buton MTN
        mtn = (Button)findViewById(R.id.canalBT);
        mtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent MtnIntent = new Intent(getApplicationContext(),MTNAirtime.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur MTN",Toast.LENGTH_SHORT).show();
                startActivity(MtnIntent);
            }
        });

        //Buton Congotel
        congotel = (Button)findViewById(R.id.starttimeBT);
        congotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent CongotelIntent = new Intent(getApplicationContext(),CongotelecomAirtime.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Congo Télécom",Toast.LENGTH_SHORT).show();
                startActivity(CongotelIntent);
            }
        });
        //Buton Azure
        azure = (Button)findViewById(R.id.azureBT);
        azure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent AzureIntent = new Intent(getApplicationContext(),AzureAirtime.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Azures",Toast.LENGTH_SHORT).show();
                startActivity(AzureIntent);
            }
        });
    }
}
