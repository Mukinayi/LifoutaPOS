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
    Button canal,easy,startimes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bouquet_numeriques);
        setTitle("Les bouquet numériques");

        //Bouton canal
        canal = (Button)findViewById(R.id.btncanaplus);
        canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CanalIntent = new Intent(getApplicationContext(),Canalplus.class);
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Canal Plus",Toast.LENGTH_SHORT).show();
                startActivity(CanalIntent);
            }
        });

        //Bouton Easy
        easy = (Button)findViewById(R.id.btneasy);
        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent EasyIntent = new Intent(getApplicationContext(),EasyTV.class);
                startActivity(EasyIntent);
            }
        });

        //Bouton Startime
        startimes = (Button)findViewById(R.id.btnstartime);
        startimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent StartIntent = new Intent(getApplicationContext(),Canalplus.class);
                startActivity(StartIntent);
            }
        });


    }
}
