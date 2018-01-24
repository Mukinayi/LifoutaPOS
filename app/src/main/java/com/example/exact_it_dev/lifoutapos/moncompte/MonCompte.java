package com.example.exact_it_dev.lifoutapos.moncompte;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.exact_it_dev.lifoutapos.R;

public class MonCompte extends AppCompatActivity {
    Button btnpinchange,btnsolde,btntransactions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_compte);

        btnpinchange = (Button)findViewById(R.id.btnpinchange);
        btnsolde = (Button)findViewById(R.id.btnsolde);
        btntransactions = (Button)findViewById(R.id.btntransactions);

        btnpinchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changepin = new Intent(getApplicationContext(),ChangementPin.class);
                startActivity(changepin);
            }
        });
    }
}
