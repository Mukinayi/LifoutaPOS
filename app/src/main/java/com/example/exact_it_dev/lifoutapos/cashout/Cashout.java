package com.example.exact_it_dev.lifoutapos.cashout;
import android.content.Intent;
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

public class Cashout extends AppCompatActivity {
    ProgressBar cashoutProgressBar;
    NetworkConnection networkConnection;
    EditText cashoutam;
    EditText cashoutnum;
    EditText clientpin;
    Button btncashout;
    Playsound playsound;
    PostResponseAsyncTask requette;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashout);
        setTitle("Effectuer un retrait");

        networkConnection = new NetworkConnection(this);

        //Réinitialisation des composants
        cashoutProgressBar =  (ProgressBar) findViewById(R.id.cashoutProgressBar);
        cashoutam = (EditText)findViewById(R.id.etCashoutam);
        cashoutnum = (EditText) findViewById(R.id.etAccountCashout);
        clientpin = (EditText) findViewById(R.id.etClientPin);
        btncashout = (Button)findViewById(R.id.btncashout);


        final String storedUrl = networkConnection.StoredUrl();
        final String receiver = networkConnection.StoredProfile("profnumcompte");

        btncashout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap Cashout = new HashMap();
                Cashout.put("senderaccount",cashoutnum.getText().toString());
                Cashout.put("receiveraccount",receiver);
                Cashout.put("senderpin",clientpin.getText().toString());
                Cashout.put("cashoutamount",cashoutam.getText().toString());
                if(cashoutnum.getText().toString().isEmpty() || clientpin.getText().toString().isEmpty() || cashoutam.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show();
                    btncashout.setText("Valider");
                    btncashout.setEnabled(true);
                    cashoutProgressBar.setVisibility(View.GONE);
                }else{
                    btncashout.setText("Chargement...");
                    btncashout.setEnabled(false);
                    cashoutProgressBar.setVisibility(View.VISIBLE);
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), Cashout, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    btncashout.setText("Valider");
                                    btncashout.setEnabled(true);
                                    cashoutProgressBar.setVisibility(View.GONE);
                                    switch (s){
                                        case "180":
                                            Toast.makeText(getApplicationContext(),"Le compte client n'existe",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;
                                        case "181":
                                            Toast.makeText(getApplicationContext(),"Le compte du client est désactivé",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;
                                        case "182":
                                        Toast.makeText(getApplicationContext(),"Compte Agent inexistant",Toast.LENGTH_SHORT).show();
                                        btncashout.setText("Valider");
                                        btncashout.setEnabled(true);
                                        cashoutProgressBar.setVisibility(View.GONE);
                                        break;
                                        case "183":
                                            Toast.makeText(getApplicationContext(),"Compte agent désactivé",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;
                                        case "184":
                                            Toast.makeText(getApplicationContext(),"Solde client insuffisant",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;
                                        case "185":
                                            Toast.makeText(getApplicationContext(),"Pin client incorrecte",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;
                                        case "186":
                                            Toast.makeText(getApplicationContext(),"Transaction échouée",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;

                                        case "":
                                            Toast.makeText(getApplicationContext(),"Aucune réponse du serveur",Toast.LENGTH_SHORT).show();
                                            btncashout.setText("Valider");
                                            btncashout.setEnabled(true);
                                            cashoutProgressBar.setVisibility(View.GONE);
                                            break;

                                            default:
                                                try {
                                                    JSONArray jsonArray = new JSONArray(s);
                                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                                    Intent confirm = new Intent(getApplicationContext(), ConfirmationTrasaction.class);
                                                    confirm.putExtra("titre","Confirmation du retrait");
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
                            tache.execute(storedUrl+"/lifoutacourant/APIS/cashout.php");
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Erreur Web servive",Toast.LENGTH_SHORT).show();
                            btncashout.setText("Valider");
                            btncashout.setEnabled(true);
                            cashoutProgressBar.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                        btncashout.setText("Valider");
                        btncashout.setEnabled(true);
                        cashoutProgressBar.setVisibility(View.GONE);
                    }
                }
            }
        });






    }
}
