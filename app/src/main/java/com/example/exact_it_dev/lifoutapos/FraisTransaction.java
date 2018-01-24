package com.example.exact_it_dev.lifoutapos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

public class FraisTransaction extends AppCompatActivity {
    NetworkConnection networkConnection;
    Playsound playsound;
    Spinner transtype;
    EditText etamount;
    Button btngo;
    TextView textfees;
    ProgressBar pgtrans;
    ArrayList<String> liste;
    ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frais_transaction);

        networkConnection = new NetworkConnection(this);
        playsound = new Playsound(this);

        final String CurrentURL = networkConnection.StoredUrl();

        transtype = (Spinner)findViewById(R.id.spinnertransfees);
        etamount = (EditText)findViewById(R.id.etamount);
        btngo = (Button)findViewById(R.id.btngo);
        textfees = (TextView)findViewById(R.id.transamounttext);
        pgtrans = (ProgressBar)findViewById(R.id.progressfaistransaction);

        liste = new ArrayList<>();
        liste.add("CASHIN");
        liste.add("CASHOUT");
        liste.add("PAYMENT");
        liste.add("TRANSFERTCOMPTETOCASH");
        liste.add("TRANSFERTCASHTOCASH");
        liste.add("VIREMENT");

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,liste);
        transtype.setAdapter(arrayAdapter);

        btngo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap datas = new HashMap();
                datas.put("amount",etamount.getText().toString());
                datas.put("transtype",transtype.getSelectedItem().toString());

                if(etamount.getText().toString().isEmpty() || transtype.getSelectedItem().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btngo.setEnabled(true);
                    btngo.setText("Valider");
                    pgtrans.setVisibility(View.GONE);
                    textfees.setText("0.0 CFA");
                }else{
                    btngo.setEnabled(false);
                    btngo.setText("Chargement");
                    pgtrans.setVisibility(View.VISIBLE);

                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), datas, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune reponse");
                                            btngo.setEnabled(true);
                                            btngo.setText("Valider");
                                            pgtrans.setVisibility(View.GONE);
                                            textfees.setText("0.0 CFA");
                                            break;

                                            default:
                                                Log.i("result",s);
                                                btngo.setEnabled(true);
                                                btngo.setText("Valider");
                                                pgtrans.setVisibility(View.GONE);
                                                textfees.setText(s+" CFA");
                                                break;
                                    }
                                }
                            });
                            tache.execute(CurrentURL+"/lifoutacourant/APIS/fraistransfert.php");
                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur serveur");
                            btngo.setEnabled(true);
                            btngo.setText("Valider");
                            pgtrans.setVisibility(View.GONE);
                            textfees.setText("0.0 CFA");
                        }

                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"erreur connxion internet");
                        btngo.setEnabled(true);
                        btngo.setText("Valider");
                        pgtrans.setVisibility(View.GONE);
                        textfees.setText("0.0 CFA");

                    }
                }
            }
        });


    }
}
