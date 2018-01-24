package com.example.exact_it_dev.lifoutapos.cashin;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class Cashin extends AppCompatActivity implements SwipeListener {
    ProgressBar cashinProgressBar;
    NetworkConnection networkConnection;
    EditText cashinam;
    EditText accnum;
    EditText agentpin;
    Button btncashin;

    Playsound playsound;
    MposHandler _handler;
    Settings _setting;
    private boolean isconnect;
    PostResponseAsyncTask requette;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashin);
        setTitle("Effectuer un dépôt");
        playsound = new Playsound(this);

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


        //Réinitialisation des composants
        cashinProgressBar =  (ProgressBar) findViewById(R.id.cashinProgressBar);
        networkConnection = new NetworkConnection(this);
        cashinam = (EditText)findViewById(R.id.etCasham);
        accnum = (EditText) findViewById(R.id.etAccnum);
        agentpin = (EditText) findViewById(R.id.etAgentPin);
        btncashin = (Button)findViewById(R.id.btncashin);


        final String storedUrl = networkConnection.StoredUrl();
        final String numcompte = networkConnection.StoredProfile("profnumcompte");



        btncashin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap CashinData = new HashMap();
                CashinData.put("receiveraccount",accnum.getText().toString());
                CashinData.put("cashinamount",cashinam.getText().toString());
                CashinData.put("agentpin",agentpin.getText().toString());
                CashinData.put("senderaccount",numcompte);
                if(accnum.getText().toString().isEmpty() || cashinam.getText().toString().isEmpty() || agentpin.getText().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btncashin.setEnabled(true);
                    cashinProgressBar.setVisibility(View.GONE);
                    btncashin.setText("Valider");
                }else{
                    btncashin.setEnabled(false);
                    cashinProgressBar.setVisibility(View.VISIBLE);
                    btncashin.setText("Chargement...");


                    if(networkConnection.isConnected()){
                        try {
                            requette = new PostResponseAsyncTask(getApplicationContext(), CashinData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            playsound.jouer(R.raw.compteclientinexistant,"Ce compte Client n'existe pas");
                                            break;
                                        case "181":
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            playsound.jouer(R.raw.compteclientinactif,"Le compte du client est désactivé");
                                            break;
                                        case "182":
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            playsound.jouer(R.raw.compteagentinexistant,"Compte agent inexistant");
                                            break;
                                        case "183":
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            playsound.jouer(R.raw.compteagentinactif,"Compte Agent désactivé");
                                            break;
                                        case "184":
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            playsound.jouer(R.raw.soldeagentinsuffisant,"Solde Agent insuffisant");
                                            break;
                                        case "185":
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            playsound.jouer(R.raw.pinagentincorrecte,"PIN Agent incorrect");
                                            break;

                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune réponse du serveur");
                                            btncashin.setText("Valider");
                                            btncashin.setEnabled(true);
                                            cashinProgressBar.setVisibility(View.GONE);
                                            break;
                                        default:
                                            if(isconnect){
                                                printtest(s);
                                                playsound.jouer(R.raw.impressionencours,"Opération effectuée avec succès");
                                                Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(main);
                                                finish();
                                            }else{

                                                Toast.makeText(getApplicationContext(),"Erreur impression",Toast.LENGTH_SHORT).show();
                                                Intent main2 = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(main2);
                                                finish();
                                            }
                                            break;

                                    }
                                }
                            });
                            requette.execute(storedUrl+"/lifoutacourant/APIS/depot.php");
                        }catch (Exception e){
                            playsound.jouer(R.raw.noresponse,"Echec du serveur");
                            btncashin.setEnabled(true);
                            cashinProgressBar.setVisibility(View.GONE);
                            btncashin.setText("Valider");
                        }
                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        btncashin.setEnabled(true);
                        cashinProgressBar.setVisibility(View.GONE);
                        btncashin.setText("Valider");
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
                            _setting.mPosPrnStr("Id transaction : " +jsonObject1.getString("idtrans"));
                            _setting.mPosPrnStr("Transaction : "+ jsonObject1.getString("transtype"));
                            _setting.mPosPrnStr("Montant : " +jsonObject1.getString("amount"));
                            _setting.mPosPrnStr("Solde : " +jsonObject1.getString("solde") +" CFA");
                            _setting.mPosPrnStr("Compte client : " +jsonObject1.getString("recipientaccount"));
                            _setting.mPosPrnStr("Titulaire du compte : "+jsonObject1.getString("nomcli") + " " +jsonObject1.getString("prenomcli"));//34 stars
                            _setting.mPosPrnStr("Agent : " +networkConnection.StoredProfile("proflname") +" "+networkConnection.StoredProfile("proffname"));
                            _setting.mPosPrnStr("Compte Agent : "+networkConnection.StoredProfile("profnumcompte"));
                            _setting.mPosPrnStr("portable Agent : "+networkConnection.StoredProfile("profphone"));
                            _setting.mPosPrnStr("Adresse Agent : " +networkConnection.StoredProfile("profadresse"));
                            _setting.mPosPrnStr("------------------------------");
                            _setting.mPosPrnStr("Merci pour votre confiance");
                            _setting.mPosPrnStr("\n");


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
