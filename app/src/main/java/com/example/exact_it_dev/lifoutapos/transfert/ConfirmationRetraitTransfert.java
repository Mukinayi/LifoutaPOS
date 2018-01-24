package com.example.exact_it_dev.lifoutapos.transfert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfirmationRetraitTransfert extends AppCompatActivity {
    NetworkConnection networkConnection;
    Playsound playsound;
    EditText idnum,adress;
    Spinner idtype;
    Button btnbenef;
    ProgressBar progressBar;
    ArrayList<String> spinnerdatas;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_retrait_transfert);
        setTitle("Détails du bénéficiaire");

        //get intent data
        Intent me = getIntent();
        final String idtrans = me.getStringExtra("idtrans");
        final String transcode = me.getStringExtra("transcode");

        networkConnection = new NetworkConnection(this);
        playsound = new Playsound(this);

        final String CurrentURL = networkConnection.StoredUrl();
        final String receiveraccount = networkConnection.StoredProfile("profnumcompte");
        idnum = (EditText)findViewById(R.id.etidnum);
        adress = (EditText)findViewById(R.id.etadressbene);
        idtype = (Spinner)findViewById(R.id.spinnertypeidbene);
        btnbenef = (Button)findViewById(R.id.btnbenef);
        progressBar = (ProgressBar)findViewById(R.id.pgbenefi);


        spinnerdatas = new ArrayList<>();
        spinnerdatas.add("PASSEPORT");
        spinnerdatas.add("PERMIT DE CONDUIRE");
        spinnerdatas.add("CARTE INDENTITAIRE NATIONALE");
        spinnerdatas.add("CARTE DE RESIDENCE");

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,spinnerdatas);
        idtype.setAdapter(arrayAdapter);


        btnbenef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap datas = new HashMap();
                datas.put("idtrans",idtrans);
                datas.put("receiveraccount",receiveraccount);
                datas.put("idtype",idtype.getSelectedItem().toString());
                datas.put("idnum",idnum.getText().toString());
                datas.put("adresse",adress.getText().toString());

                if(adress.getText().toString().isEmpty() || idtype.getSelectedItem().toString().isEmpty() || idnum.getText().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    progressBar.setVisibility(View.GONE);
                    btnbenef.setEnabled(true);
                    btnbenef.setText("Valider");
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    btnbenef.setEnabled(false);
                    btnbenef.setText("Chargement");

                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), datas, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune reponse du serveur");
                                            progressBar.setVisibility(View.GONE);
                                            btnbenef.setEnabled(true);
                                            btnbenef.setText("Valider");
                                            break;
                                        case "201":
                                            playsound.jouer(R.raw.erreurserveur,"Echec opération");
                                            progressBar.setVisibility(View.GONE);
                                            btnbenef.setEnabled(true);
                                            btnbenef.setText("Valider");
                                            break;
                                            default:
                                                   try {
                                                       JSONArray jsonArray = new JSONArray(s);
                                                       JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                       Intent confirm = new Intent(getApplicationContext(),ConfirmationOTPTransfert.class);
                                                       confirm.putExtra("transcode",transcode);
                                                       confirm.putExtra("idtrans",idtrans);
                                                       confirm.putExtra("otpclient",jsonObject.getString("optclient"));
                                                       confirm.putExtra("transtype",jsonObject.getString("transtype"));
                                                       startActivity(confirm);
                                                       finish();

                                                   }catch (JSONException e){
                                                       Toast.makeText(getApplicationContext(),"Erreur JSON",Toast.LENGTH_SHORT).show();
                                                   }
                                                break;
                                    }
                                }
                            });
                            tache.execute(CurrentURL+"/lifoutacourant/APIS/recuperer.php");
                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur du serveur");
                            progressBar.setVisibility(View.GONE);
                            btnbenef.setEnabled(true);
                            btnbenef.setText("Valider");
                        }

                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        progressBar.setVisibility(View.GONE);
                        btnbenef.setEnabled(true);
                        btnbenef.setText("Valider");
                    }
                }


            }
        });




    }
}
