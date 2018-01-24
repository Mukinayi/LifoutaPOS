package com.example.exact_it_dev.lifoutapos.enrolement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;

public class GestionComptes extends AppCompatActivity {
    Button btnnacc,btnchckacc,btnreprint,btnnfc;
    NetworkConnection networkConnection;
    Playsound playsound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_comptes);
        setTitle("Gestion du compte");
        playsound = new Playsound(this);


        //Instanciation des bouton

        btnnacc = (Button)findViewById(R.id.btnNewAccount);
        btnchckacc = (Button)findViewById(R.id.btnCheckAccount);
        networkConnection = new NetworkConnection(this);

        btnnacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playsound.jouer(R.raw.creationcompte,"création du compte");
                Intent enrolement = new Intent(getApplicationContext(),Enrolement.class);
                startActivity(enrolement);
            }
        });

        btnchckacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),VerificationCompte.class);
                startActivity(intent);
            }
        });




    }
}
