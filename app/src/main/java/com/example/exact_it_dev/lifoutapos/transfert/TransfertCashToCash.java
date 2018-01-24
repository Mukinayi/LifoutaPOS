package com.example.exact_it_dev.lifoutapos.transfert;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

import com.example.exact_it_dev.lifoutapos.MainActivity;
import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.mpos.MposHandler;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TransfertCashToCash extends AppCompatActivity implements SwipeListener{
        EditText etprenomexp,etnomexp,etphoneexp,etidnumberexp,ettransfertmontant,etprenombenef,etnombenef,etphonebenef,etpinexped;
        Spinner spinnertypeidentiteexp;
        ProgressBar progressBar;
        Button btntranscashtocash;
        NetworkConnection networkConnection;
        Playsound playsound;
        ArrayList<String> typeidentite;
        ArrayAdapter arrayAdapter;
        MposHandler _handler;
        Settings _setting;
        private boolean isconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfertcashtocash);
        setTitle("Transfert Cash To Cash");

        _handler = new MposHandler(this);
        _setting = new Settings(_handler);
        _handler.setParameters("/dev/ttyS2", 115200);
        _handler.addSwipeListener(this);
        _setting.mPosPowerOn();
        try {
            Thread.sleep(1500);
            if (!_handler.isConnected()) {
                isconnect= _handler.connect();
            } else {
                _handler.close();
                isconnect= _handler.connect();
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        playsound = new Playsound(this);
        networkConnection = new NetworkConnection(this);

        final String CurrentURL = networkConnection.StoredUrl();
        final String SenderAccount = networkConnection.StoredProfile("profnumcompte");

        etprenomexp = (EditText)findViewById(R.id.etprenomexp);
        etnomexp = (EditText)findViewById(R.id.etnomexp);
        etphoneexp = (EditText)findViewById(R.id.etphoneexp);
        etidnumberexp = (EditText)findViewById(R.id.etidnumberexp);
        ettransfertmontant = (EditText)findViewById(R.id.ettransfertmontant);
        etprenombenef = (EditText)findViewById(R.id.etprenombenef);
        etnombenef = (EditText)findViewById(R.id.etnombenef);
        etphonebenef = (EditText)findViewById(R.id.etphonebenef);
        etpinexped = (EditText)findViewById(R.id.etpinexped);
        progressBar = (ProgressBar)findViewById(R.id.progresscashtocash);
        spinnertypeidentiteexp = (Spinner)findViewById(R.id.spinnertypeidentiteexp);
        btntranscashtocash = (Button)findViewById(R.id.btntranscashtocash);

        typeidentite = new ArrayList<>();
        typeidentite.add("PASSEPORT");
        typeidentite.add("PERMIS DE CONDUIRE");
        typeidentite.add("CARTE IDENTITAIRE NATIONALE");
        typeidentite.add("CARTE RESIDENTIELLE");
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,typeidentite);
        spinnertypeidentiteexp.setAdapter(arrayAdapter);

        //débuter la transaction
        btntranscashtocash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etprenomexp.getText().toString().isEmpty() || etnomexp.getText().toString().isEmpty() || etphoneexp.getText().toString().isEmpty() || etidnumberexp.getText().toString().isEmpty()
                        || ettransfertmontant.getText().toString().isEmpty() || etprenombenef.getText().toString().isEmpty() || etnombenef.getText().toString().isEmpty()
                         || etphonebenef.getText().toString().isEmpty() || etpinexped.getText().toString().isEmpty() || spinnertypeidentiteexp.getSelectedItem().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btntranscashtocash.setEnabled(true);
                    btntranscashtocash.setText("Valider");
                    progressBar.setVisibility(View.GONE);
                }else{
                    btntranscashtocash.setEnabled(false);
                    btntranscashtocash.setText("Chargement...");
                    progressBar.setVisibility(View.VISIBLE);

                    HashMap transfertData = new HashMap();
                    transfertData.put("senderaccount",SenderAccount);
                    transfertData.put("amount",ettransfertmontant.getText().toString());
                    transfertData.put("explname",etnomexp.getText().toString());
                    transfertData.put("expfname",etprenomexp.getText().toString());
                    transfertData.put("phoneexp",etphoneexp.getText().toString());
                    transfertData.put("typeidentite",spinnertypeidentiteexp.getSelectedItem().toString());
                    transfertData.put("idnumber",etidnumberexp.getText().toString());
                    transfertData.put("recilname",etnombenef.getText().toString());
                    transfertData.put("recifname",etprenombenef.getText().toString());
                    transfertData.put("reciphone",etphonebenef.getText().toString());
                    transfertData.put("pinexpeditor",etpinexped.getText().toString());

                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tachetransfert = new PostResponseAsyncTask(getApplicationContext(), transfertData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            playsound.jouer(R.raw.expediteurinexistant,"Compte Expéditeur inexistant");
                                            btntranscashtocash.setEnabled(true);
                                            btntranscashtocash.setText("Valider");
                                            progressBar.setVisibility(View.GONE);
                                            break;
                                        case "181":
                                            playsound.jouer(R.raw.expediteurdesactive,"Compte expéditeur désactivé");
                                            btntranscashtocash.setEnabled(true);
                                            btntranscashtocash.setText("Valider");
                                            progressBar.setVisibility(View.GONE);
                                            break;
                                        case "182":
                                            playsound.jouer(R.raw.pinexpediteurincorrecte,"PIN expéditeur incorrecte");
                                            btntranscashtocash.setEnabled(true);
                                            btntranscashtocash.setText("Valider");
                                            progressBar.setVisibility(View.GONE);
                                            break;
                                        case "183":
                                            playsound.jouer(R.raw.soldeexpediteurinsuffisant,"Solde expéditeur insuffisant pour effectuer ce transfert");
                                            btntranscashtocash.setEnabled(true);
                                            btntranscashtocash.setText("Valider");
                                            progressBar.setVisibility(View.GONE);
                                            break;
                                        case "201":
                                            playsound.jouer(R.raw.noresponse,"Echec transfert");
                                            btntranscashtocash.setEnabled(true);
                                            btntranscashtocash.setText("Valider");
                                            progressBar.setVisibility(View.GONE);
                                            break;
                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune reponse du serveur");
                                            btntranscashtocash.setEnabled(true);
                                            btntranscashtocash.setText("Valider");
                                            progressBar.setVisibility(View.GONE);
                                            break;
                                            default:
                                                if(isconnect){
                                                    printtest(s);
                                                    playsound.jouer(R.raw.impressionencours,"Opération réussie");
                                                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                                    startActivity(main);
                                                    finish();
                                                }else{
                                                    Log.i("reponse",s);
                                                    Toast.makeText(getApplicationContext(),"Erreur impression",Toast.LENGTH_SHORT).show();
                                                    Intent main2 = new Intent(getApplicationContext(),MainActivity.class);
                                                    startActivity(main2);
                                                    finish();
                                                }
                                                break;
                                    }
                                }
                            });
                            tachetransfert.execute(CurrentURL+"/lifoutacourant/APIS/transfertcashtocash.php");
                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur web service");
                            btntranscashtocash.setEnabled(true);
                            btntranscashtocash.setText("Valider");
                            progressBar.setVisibility(View.GONE);
                        }
                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        btntranscashtocash.setEnabled(true);
                        btntranscashtocash.setText("Valider");
                        progressBar.setVisibility(View.GONE);
                    }

                }
            }
        });

    }

    private void printtest(String s) {
        //final String MEMORY="MEMOIRE";
        //final SharedPreferences profile = getSharedPreferences(MEMORY,MODE_PRIVATE);

        try {
            final JSONArray jsonArray = new JSONArray(s);
            if(jsonArray.length() <=0){
                Toast.makeText(getApplicationContext(),"Données non recupérées",Toast.LENGTH_SHORT).show();
            }else{
                //Pour impression
                final JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                String agfees = jsonObject1.getString("agentfees");
                String sysfees = jsonObject1.getString("systemfees");
                String agwithtransfees = jsonObject1.getString("agwithtransfees");
                final double transfees = Double.parseDouble(sysfees) + Double.parseDouble(agfees) + Double.parseDouble(agwithtransfees);

                //Pour impression
                new Thread(new Runnable() {

                    public void run() {
                        try{
                            _setting.mPosEnterPrint();
                            _setting.mPosPrintFontSwitch(com.imagpay.Settings.MPOS_PRINT_FONT_NEW);
                            _setting.mPosPrintAlign(com.imagpay.Settings.MPOS_PRINT_ALIGN_CENTER);
                            _setting.mPosPrintTextSize(com.imagpay.Settings.MPOS_PRINT_TEXT_DOUBLE_HEIGHT);
                            _setting.mPosPrnStr("MICRO-FINANCE LIFOUTA");
                            _setting.mPosPrintLn();
                            _setting.mPosPrintTextSize(com.imagpay.Settings.MPOS_PRINT_TEXT_NORMAL);
                            _setting.mPosPrintAlign(com.imagpay.Settings.MPOS_PRINT_ALIGN_LEFT);
                            _setting.mPosPrnStr("Code transfert :" +jsonObject1.getString("tranfertcode"));
                            _setting.mPosPrnStr("Transaction : "+ jsonObject1.getString("transtype"));
                            _setting.mPosPrnStr("Montant transfert : " +jsonObject1.getString("amount") +" cfa");
                            _setting.mPosPrnStr("Frais transfert : " + String.valueOf(transfees) +" cfa");
                            _setting.mPosPrnStr("Agent : " +networkConnection.StoredProfile("proflname") +" "+networkConnection.StoredProfile("proffname"));
                            _setting.mPosPrnStr("Compte Agent : "+networkConnection.StoredProfile("profnumcompte"));
                            _setting.mPosPrnStr("portable Agent : "+networkConnection.StoredProfile("profphone"));
                            _setting.mPosPrnStr("Adresse Agent : " +networkConnection.StoredProfile("profadresse"));
                            _setting.mPosPrnStr("Nom beneficiaire :" +jsonObject1.getString("recipienttransfertlname"));
                            _setting.mPosPrnStr("Prenom beneficiaire :" +jsonObject1.getString("recipienttransfertfname"));
                            _setting.mPosPrnStr("Portable beneficiaire : " +jsonObject1.getString("recipienttransfertphone"));
                            _setting.mPosPrnStr("------------------------------");
                            _setting.mPosPrnStr("Merci pour votre confiance");
                            _setting.mPosPrnStr("\n\n\n");


                        }catch(Exception e){
                            Toast.makeText(getApplicationContext(),"Impossible",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        }catch (JSONException e){
            Toast.makeText(getApplicationContext(),"Erreur JSON",Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDisconnected(SwipeEvent swipeEvent) {

    }

    @Override
    public void onConnected(SwipeEvent swipeEvent) {

    }

    @Override
    public void onStarted(SwipeEvent swipeEvent) {

    }

    @Override
    public void onStopped(SwipeEvent swipeEvent) {

    }

    @Override
    public void onReadData(SwipeEvent swipeEvent) {

    }

    @Override
    public void onParseData(SwipeEvent swipeEvent) {

    }

    @Override
    public void onPermission(SwipeEvent swipeEvent) {

    }

    @Override
    public void onCardDetect(CardDetected cardDetected) {

    }

    @Override
    public void onPrintStatus(PrintStatus printStatus) {

    }

    @Override
    public void onEmvStatus(EmvStatus emvStatus) {

    }
}
