package com.example.exact_it_dev.lifoutapos.payer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.ConfirmationTrasaction;
import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Payer extends AppCompatActivity {
        NetworkConnection networkConnection;
        Playsound playsound;
        EditText etclientnumber,etclientpin,etinvoicenumber,etpayamount,etpayreason;
        Button btnpayer;
        ProgressBar progresspay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payer);
        setTitle("Paiement divers");

        networkConnection = new NetworkConnection(this);
        playsound = new Playsound(this);

        final String receiveraccount = networkConnection.StoredProfile("profnumcompte");
        final String CurrentURL = networkConnection.StoredUrl();

        etclientnumber = (EditText)findViewById(R.id.etnumcomptepayer);
        etclientpin = (EditText)findViewById(R.id.etpaypin);
        etinvoicenumber = (EditText)findViewById(R.id.etinvoicenumber);
        etpayamount = (EditText)findViewById(R.id.etpayamount);
        etpayreason = (EditText)findViewById(R.id.etpaydescription);
        btnpayer = (Button)findViewById(R.id.buttonpay);
        progresspay = (ProgressBar)findViewById(R.id.progresspayer);

        btnpayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap payData = new HashMap();
                payData.put("senderaccount",etclientnumber.getText().toString());
                payData.put("amount",etpayamount.getText().toString());
                payData.put("senderpin",etclientpin.getText().toString());
                payData.put("description",etpayreason.getText().toString());
                payData.put("invoicenumber",etinvoicenumber.getText().toString());
                payData.put("receiveraccount",receiveraccount);

                if(etclientnumber.getText().toString().isEmpty() || etclientpin.getText().toString().isEmpty() || etpayamount.getText().toString().isEmpty()
                         || etpayreason.getText().toString().isEmpty() || etinvoicenumber.getText().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btnpayer.setEnabled(true);
                    btnpayer.setText("Valider");
                    progresspay.setVisibility(View.GONE);

                }else{
                    btnpayer.setEnabled(false);
                    btnpayer.setText("Chargement...");
                    progresspay.setVisibility(View.VISIBLE);
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), payData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune reponse du serveur");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "180":
                                            playsound.jouer(R.raw.compteclientinexistant,"Compte client inexistant");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "181":
                                            playsound.jouer(R.raw.compteclientinactif,"Compte client inactif");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "182":
                                            playsound.jouer(R.raw.pinclientincorrecte,"Pin client incorrecte");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "183":
                                            playsound.jouer(R.raw.compteagentinexistant,"Compte Agent non reconnu");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "184":
                                            playsound.jouer(R.raw.compteagentinactif,"Compte agent inactif");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "185":
                                            playsound.jouer(R.raw.soldeclientinsuffisant,"Solde client insuffisant");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                        case "201":
                                            playsound.jouer(R.raw.erreurserveur,"Erreur serveur");
                                            btnpayer.setEnabled(true);
                                            btnpayer.setText("Valider");
                                            progresspay.setVisibility(View.GONE);
                                            break;
                                            default:
                                                try {
                                                    JSONArray jsonArray = new JSONArray(s);
                                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                    Intent confirm = new Intent(getApplicationContext(), ConfirmationTrasaction.class);
                                                    confirm.putExtra("titre","Confirmation du paiement");
                                                    confirm.putExtra("otp",jsonObject.getString("optclient"));
                                                    confirm.putExtra("transtype",jsonObject.getString("transtype"));
                                                    confirm.putExtra("sender",jsonObject.getString("senderaccount"));
                                                    confirm.putExtra("receiver",jsonObject.getString("recipientaccount"));
                                                    confirm.putExtra("idtrans",jsonObject.getString("idtrans"));
                                                    startActivity(confirm);
                                                    finish();
                                                }catch (JSONException e){
                                                    Toast.makeText(getApplicationContext(),"Erreur données JSON",Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                    }
                                }
                            });
                            tache.execute(CurrentURL+"/lifoutacourant/APIS/payer.php");

                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur serveur");
                            btnpayer.setEnabled(true);
                            btnpayer.setText("Valider");
                            progresspay.setVisibility(View.GONE);
                        }
                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        btnpayer.setEnabled(true);
                        btnpayer.setText("Valider");
                        progresspay.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
}
