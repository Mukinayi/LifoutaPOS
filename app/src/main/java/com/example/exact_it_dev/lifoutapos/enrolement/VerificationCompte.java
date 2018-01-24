package com.example.exact_it_dev.lifoutapos.enrolement;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VerificationCompte extends AppCompatActivity {
    EditText etacc;
    Button btncheck;
    ProgressBar progressBar;
    NetworkConnection networkConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_compte);
        setTitle("Vérification du compte");

        etacc = (EditText)findViewById(R.id.etacc);
        btncheck = (Button)findViewById(R.id.btncheck);
        progressBar = (ProgressBar)findViewById(R.id.progresscheck);
        networkConnection = new NetworkConnection(this);

        final String CurrentURL = networkConnection.StoredUrl();

        btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap numcompte = new HashMap();
                numcompte.put("numcompte",etacc.getText().toString());

                if(etacc.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Vous devez renseigner le numéro du compte",Toast.LENGTH_SHORT).show();
                    btncheck.setText("Vérifier");
                    btncheck.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }else{
                    btncheck.setText("Chargement...");
                    btncheck.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);

                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), numcompte, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(s);
                                        if(jsonArray.length() <=0){
                                            Toast.makeText(getApplicationContext(),"Compte Lifouta non existant",Toast.LENGTH_SHORT).show();
                                            btncheck.setText("Vérifier");
                                            btncheck.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                        }else{
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            Toast.makeText(getApplicationContext(),"Très bien",Toast.LENGTH_SHORT).show();
                                            btncheck.setText("Vérifier");
                                            btncheck.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }catch (JSONException e){
                                        Toast.makeText(getApplicationContext(),"Erreur JSON",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            tache.execute(CurrentURL+"/lifoutacourant/APIS/checkcompte.php");
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Erreur du serveur",Toast.LENGTH_SHORT).show();
                            btncheck.setText("Vérifier");
                            btncheck.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                        btncheck.setText("Vérifier");
                        btncheck.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
