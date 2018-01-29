package com.example.exact_it_dev.lifoutapos.moncompte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class MonCompte extends AppCompatActivity {
    Button btnpinchange,btnsolde,btntransactions;
    ProgressDialog pDialog;
    NetworkConnection networkConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_compte);

        btnpinchange = (Button)findViewById(R.id.btnpinchange);
        btnsolde = (Button)findViewById(R.id.btnsolde);
        btntransactions = (Button)findViewById(R.id.btntransactions);
        networkConnection = new NetworkConnection(this);
        final String CurrentURL = networkConnection.StoredUrl();
        final String myaccount = networkConnection.StoredProfile("profnumcompte");

        btnpinchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changepin = new Intent(getApplicationContext(),ChangementPin.class);
                startActivity(changepin);
            }
        });


        btnsolde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap dataSolde = new HashMap();
                dataSolde.put("moncompte",myaccount);
                pDialog = new ProgressDialog(MonCompte.this);
                pDialog.setCancelable(false);
                pDialog.setIndeterminate(true);
                pDialog.setMessage("Chargement ...");
                pDialog.setProgressStyle(R.style.AppTheme);
                pDialog.show();
                if(networkConnection.isConnected()){
                    pDialog.setMessage("Recupération du solde");
                    try {
                        PostResponseAsyncTask solde = new PostResponseAsyncTask(getApplicationContext(), dataSolde, false, new AsyncResponse() {
                            @Override
                            public void processFinish(String s) {
                                switch(s){
                                    case "":
                                        pDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Aucune reponse",Toast.LENGTH_SHORT).show();
                                        break;
                                    case "180":
                                        pDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"numéro du compte inconnu",Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Intent soldeintent = new Intent(MonCompte.this,MonSolde.class);
                                        soldeintent.putExtra("solde",s);
                                        startActivity(soldeintent);
                                        finish();
                                        break;
                                }
                            }
                        });
                        solde.execute(CurrentURL+"/lifoutacourant/APIS/solde.php");
                    }catch (Exception e){
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Erreur du serveur",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btntransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TransactionsGenerales.class);
                startActivity(intent);
            }
        });

    }
}
