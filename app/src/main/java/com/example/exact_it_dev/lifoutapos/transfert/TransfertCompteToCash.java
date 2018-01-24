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

import java.util.HashMap;

public class TransfertCompteToCash extends AppCompatActivity implements SwipeListener{
    EditText etprenombene,ettransmontant,etnombene,etphonebene,etpinexp;
    Button btncomptetocash;
    ProgressBar progresscomptetocash;
    NetworkConnection networkConnection;
    Playsound playsound;
    MposHandler _handler;
    Settings _setting;
    private boolean isconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert_compte_to_cash);
        setTitle("Transfert Compte à Cash");

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

        etprenombene = (EditText)findViewById(R.id.etprenombene);
        ettransmontant = (EditText)findViewById(R.id.ettransmontant);
        etnombene = (EditText)findViewById(R.id.etnombene);
        etphonebene = (EditText)findViewById(R.id.etphonebene);
        etpinexp = (EditText)findViewById(R.id.etpinexp);
        btncomptetocash = (Button)findViewById(R.id.btntranscomptetocash);
        progresscomptetocash = (ProgressBar)findViewById(R.id.progresscomptetocash);
        networkConnection = new NetworkConnection(this);
        playsound = new Playsound(this);

        final String CurrentURL = networkConnection.StoredUrl();
        final String senderaccount = networkConnection.StoredProfile("profnumcompte");

        btncomptetocash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap comptetoCash = new HashMap();
                comptetoCash.put("senderaccount",senderaccount);
                comptetoCash.put("amount",ettransmontant.getText().toString());
                comptetoCash.put("nombene",etnombene.getText().toString());
                comptetoCash.put("prenombene",etprenombene.getText().toString());
                comptetoCash.put("phoneexp",etphonebene.getText().toString());
                comptetoCash.put("pinexp",etpinexp.getText().toString());

                if(ettransmontant.getText().toString().isEmpty() || etnombene.getText().toString().isEmpty() || etprenombene.getText().toString().isEmpty()
                         || etpinexp.getText().toString().isEmpty() || etphonebene.getText().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btncomptetocash.setEnabled(true);
                    btncomptetocash.setText("Valider");
                    progresscomptetocash.setVisibility(View.GONE);

                }else{
                    btncomptetocash.setEnabled(false);
                    btncomptetocash.setText("Chargement...");
                    progresscomptetocash.setVisibility(View.VISIBLE);
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask comptetocash = new PostResponseAsyncTask(getApplicationContext(), comptetoCash, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            playsound.jouer(R.raw.expediteurinexistant,"Compte Expéditeur inexistant");
                                            btncomptetocash.setEnabled(true);
                                            btncomptetocash.setText("Valider");
                                            progresscomptetocash.setVisibility(View.GONE);
                                            break;
                                        case "181":
                                            playsound.jouer(R.raw.expediteurdesactive,"Compte expéditeur désactivé");
                                            btncomptetocash.setEnabled(true);
                                            btncomptetocash.setText("Valider");
                                            progresscomptetocash.setVisibility(View.GONE);
                                            break;
                                        case "182":
                                            playsound.jouer(R.raw.pinexpediteurincorrecte,"PIN expéditeur incorrecte");
                                            btncomptetocash.setEnabled(true);
                                            btncomptetocash.setText("Valider");
                                            progresscomptetocash.setVisibility(View.GONE);
                                            break;
                                        case "183":
                                            playsound.jouer(R.raw.soldeexpediteurinsuffisant,"Solde expéditeur insuffisant pour effectuer ce transfert");
                                            btncomptetocash.setEnabled(true);
                                            btncomptetocash.setText("Valider");
                                            progresscomptetocash.setVisibility(View.GONE);
                                            break;
                                        case "201":
                                            playsound.jouer(R.raw.noresponse,"Echec transfert");
                                            btncomptetocash.setEnabled(true);
                                            btncomptetocash.setText("Valider");
                                            progresscomptetocash.setVisibility(View.GONE);
                                            break;
                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune reponse du serveur");
                                            btncomptetocash.setEnabled(true);
                                            btncomptetocash.setText("Valider");
                                            progresscomptetocash.setVisibility(View.GONE);
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
                            comptetocash.execute(CurrentURL+"/lifoutacourant/APIS/transfertcomptetocash.php");
                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur serveur");
                            btncomptetocash.setEnabled(true);
                            btncomptetocash.setText("Valider");
                            progresscomptetocash.setVisibility(View.GONE);
                        }
                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        btncomptetocash.setEnabled(true);
                        btncomptetocash.setText("Valider");
                        progresscomptetocash.setVisibility(View.GONE);

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
