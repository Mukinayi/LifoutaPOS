package com.example.exact_it_dev.lifoutapos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
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

public class ConfirmationTrasaction extends AppCompatActivity implements SwipeListener{
    StringBuilder stringBuilder;
    ImageButton smsresend;
    EditText etOTP;
    Button btnconf;
    ProgressBar confirmProgress;
    NetworkConnection networkConnection;
    PostResponseAsyncTask tacheconfirm;

    MposHandler _handler;
    Settings _setting;
    private boolean isconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_trasaction);

        Intent me = getIntent();
        String titre = me.getStringExtra("titre");
        final String optclient = me.getStringExtra("otp");
        final String sender = me.getStringExtra("sender");
        final String receiver = me.getStringExtra("receiver");
        final String transtype = me.getStringExtra("transtype");
        final String idtrans = me.getStringExtra("idtrans");

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

        networkConnection = new NetworkConnection(this);
        final String storedUrl = networkConnection.StoredUrl();

        setTitle(titre);
        stringBuilder = new StringBuilder();
        stringBuilder.append("");

        //Récupértaion des composants
        smsresend = (ImageButton) findViewById(R.id.resendBtn);
        etOTP = (EditText)findViewById(R.id.etOTP);
        btnconf = (Button)findViewById(R.id.btnconfirm);
        confirmProgress = (ProgressBar)findViewById(R.id.confirmProgressBar);

        //Retrait
        btnconf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap ConfirmData = new HashMap();
                ConfirmData.put("senderaccount",sender);
                ConfirmData.put("receiveraccount",receiver);
                ConfirmData.put("transtype",transtype);
                ConfirmData.put("optclient",etOTP.getText().toString());

                if(etOTP.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez renseigner le OTP",Toast.LENGTH_SHORT).show();
                    btnconf.setText("Confirmer");
                    btnconf.setEnabled(true);
                    confirmProgress.setVisibility(View.GONE);
                }else{
                    btnconf.setText("Chargement...");
                    btnconf.setEnabled(false);
                    confirmProgress.setVisibility(View.VISIBLE);
                    if(networkConnection.isConnected()){
                        try {
                            tacheconfirm = new PostResponseAsyncTask(getApplicationContext(), ConfirmData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    Log.i("confirm",s);
                                    switch (s){
                                        case "180":
                                            Toast.makeText(getApplicationContext(),"OTP non existant",Toast.LENGTH_SHORT).show();
                                            btnconf.setText("Confirmer");
                                            btnconf.setEnabled(true);
                                            confirmProgress.setVisibility(View.GONE);
                                            break;
                                        case "181":
                                            Toast.makeText(getApplicationContext(),"OTP expiré",Toast.LENGTH_SHORT).show();
                                            try {
                                                Thread.sleep(2000);
                                                Toast.makeText(getApplicationContext(),"Transaction annulé",Toast.LENGTH_SHORT).show();
                                                Intent home = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(home);
                                                finish();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "201":
                                            Toast.makeText(getApplicationContext(),"Echec mise à jour",Toast.LENGTH_SHORT).show();
                                            try {
                                                Thread.sleep(2000);
                                                Toast.makeText(getApplicationContext(),"Transaction annulé",Toast.LENGTH_SHORT).show();
                                                Intent hom = new Intent(getApplicationContext(),MainActivity.class);
                                                startActivity(hom);
                                                finish();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            break;
                                        case "":
                                            Toast.makeText(getApplicationContext(),"Aucune réponse du serveur",Toast.LENGTH_SHORT).show();
                                            btnconf.setText("Confirmer");
                                            btnconf.setEnabled(true);
                                            confirmProgress.setVisibility(View.GONE);
                                            break;

                                            default:
                                                if(isconnect){
                                                    printtest(s);
                                                    Toast.makeText(getApplicationContext(),"Dépot effectué avec succès",Toast.LENGTH_LONG).show();
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
                            tacheconfirm.execute(storedUrl+"/lifoutacourant/APIS/confirmtransaction.php");

                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Erreur Web service",Toast.LENGTH_SHORT).show();
                            btnconf.setText("Confirmer");
                            btnconf.setEnabled(true);
                            confirmProgress.setVisibility(View.GONE);
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                        btnconf.setText("Confirmer");
                        btnconf.setEnabled(true);
                        confirmProgress.setVisibility(View.GONE);
                    }
                }
            }
        });

        smsresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Renvoi du SMS en cours...",Toast.LENGTH_SHORT).show();
                HashMap smsData = new HashMap();
                smsData.put("senderaccount",sender);
                smsData.put("optclient",optclient);
                smsData.put("transtype",transtype);

                if(networkConnection.isConnected()){
                    try {
                        tacheconfirm = new PostResponseAsyncTask(getApplicationContext(), smsData, false, new AsyncResponse() {
                            @Override
                            public void processFinish(String s) {
                                switch (s){
                                    case "180":
                                        Toast.makeText(getApplicationContext(),"Aucun OPT trouvé", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "200":
                                        Toast.makeText(getApplicationContext(),"OTP renvoyé avec succès",Toast.LENGTH_SHORT).show();
                                        break;

                                        default:
                                            Toast.makeText(getApplicationContext(),"Echec renvoi OTP",Toast.LENGTH_SHORT).show();
                                            break;
                                }
                            }
                        });
                        tacheconfirm.execute(storedUrl+"/lifoutacourant/APIS/resendsms.php");
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Erreur webservice",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                }
            }
        });




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

    private void printtest(String s) {

        try {
            final JSONArray jsonArray = new JSONArray(s);
            if(jsonArray.length() <=0){
                Toast.makeText(getApplicationContext(),"Données non recupérées",Toast.LENGTH_SHORT).show();
            }else{
                //Pour impression
                final JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                String agfees = jsonObject1.getString("agentfees");
                String sysfees = jsonObject1.getString("systemfees");
                final double transfees = Double.parseDouble(sysfees) + Double.parseDouble(agfees);

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
                            _setting.mPosPrnStr("Id transaction :" +jsonObject1.getString("idtrans"));
                            _setting.mPosPrnStr("Transaction : "+ jsonObject1.getString("transtype"));
                            _setting.mPosPrnStr("Montant : " +jsonObject1.getString("amount"));
                            _setting.mPosPrnStr("Frais transaction : " + String.valueOf(transfees) +" cfa");
                            _setting.mPosPrnStr("Solde : " +jsonObject1.getString("solde") +" CFA");
                            _setting.mPosPrnStr("Compte client : " +jsonObject1.getString("senderaccount"));
                            _setting.mPosPrnStr("Titulaire : "+jsonObject1.getString("nomcli") + " " +jsonObject1.getString("prenomcli"));//34 stars
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
}
