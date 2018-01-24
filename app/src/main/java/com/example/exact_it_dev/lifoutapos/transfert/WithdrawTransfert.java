package com.example.exact_it_dev.lifoutapos.transfert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class WithdrawTransfert extends AppCompatActivity {
    NetworkConnection networkConnection;
    Playsound playsound;
    EditText etwithdrawmontant,ettransfertcode,ettransfertdate;
    Button btntranswithdraw;
    ProgressBar progresstranswithdraw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_transfert);
        setTitle("Rétirer un transfert");

        networkConnection = new NetworkConnection(this);
        playsound = new Playsound(this);

        final String CurrentURL = networkConnection.StoredUrl();


        btntranswithdraw = (Button)findViewById(R.id.btntranswithdraw);
        etwithdrawmontant = (EditText)findViewById(R.id.etwithdrawmontant);
        ettransfertcode = (EditText)findViewById(R.id.ettransfertcode);
        ettransfertdate = (EditText)findViewById(R.id.ettransfertdate);
        progresstranswithdraw = (ProgressBar)findViewById(R.id.progresstranswithdraw);

        btntranswithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap RetraitData = new HashMap();
                RetraitData.put("amount",etwithdrawmontant.getText().toString());
                RetraitData.put("transdate",ettransfertdate.getText().toString());
                RetraitData.put("transcode",ettransfertcode.getText().toString());

                if(ettransfertcode.getText().toString().isEmpty() || ettransfertdate.getText().toString().isEmpty() || etwithdrawmontant.getText().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btntranswithdraw.setText("Vérifier");
                    btntranswithdraw.setEnabled(true);
                    progresstranswithdraw.setVisibility(View.GONE);
                }else{
                    btntranswithdraw.setText("Chargement...");
                    btntranswithdraw.setEnabled(false);
                    progresstranswithdraw.setVisibility(View.VISIBLE);
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), RetraitData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "":
                                            Toast.makeText(getApplicationContext(),"Aucune réponse du serveur",Toast.LENGTH_SHORT).show();
                                            btntranswithdraw.setText("Vérifier");
                                            btntranswithdraw.setEnabled(true);
                                            progresstranswithdraw.setVisibility(View.GONE);
                                            break;
                                        case "180":
                                            Toast.makeText(getApplicationContext(),"Transfert non reconnu",Toast.LENGTH_SHORT).show();
                                            btntranswithdraw.setText("Vérifier");
                                            btntranswithdraw.setEnabled(true);
                                            progresstranswithdraw.setVisibility(View.GONE);
                                            break;
                                            default:
                                                try {
                                                    JSONArray jsonArray = new JSONArray(s);
                                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                    Intent transres = new Intent(getApplicationContext(),ResultTransfertVerification.class);
                                                    transres.putExtra("recilname",jsonObject.getString("recipienttransfertlname"));
                                                    transres.putExtra("recifname",jsonObject.getString("recipienttransfertfname"));
                                                    transres.putExtra("transfertstate",jsonObject.getString("etattransfert"));
                                                    transres.putExtra("reciphone",jsonObject.getString("recipienttransfertphone"));
                                                    transres.putExtra("transamount",jsonObject.getString("amount"));
                                                    transres.putExtra("withdrawfees",jsonObject.getString("agwithtransfees"));
                                                    transres.putExtra("idtrans",jsonObject.getString("idtrans"));
                                                    transres.putExtra("transcode",jsonObject.getString("tranfertcode"));
                                                    startActivity(transres);

                                                }catch (JSONException e){
                                                    Toast.makeText(getApplicationContext(),"Erreur JSON",Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                    }
                                }
                            });
                            tache.execute(CurrentURL+"/lifoutacourant/APIS/transfertwithdrawal.php");
                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur serveur");
                            btntranswithdraw.setText("Vérifier");
                            btntranswithdraw.setEnabled(true);
                            progresstranswithdraw.setVisibility(View.GONE);
                        }
                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        btntranswithdraw.setText("Vérifier");
                        btntranswithdraw.setEnabled(true);
                        progresstranswithdraw.setVisibility(View.GONE);
                    }

                }
            }
        });


    }
}
