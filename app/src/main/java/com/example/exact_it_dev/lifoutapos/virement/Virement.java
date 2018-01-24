package com.example.exact_it_dev.lifoutapos.virement;

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

public class Virement extends AppCompatActivity implements SwipeListener{
    Button btnvirement;
    EditText etrecipientaccount,ettransfertamount,etsenderpin;
    ProgressBar virementProgressbar;
    Playsound playsound;
    NetworkConnection networkConnection;
    MposHandler _handler;
    Settings _setting;
    private boolean isconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virement);
        setTitle("Virement");

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

        //Récupération des composants
        etrecipientaccount = (EditText)findViewById(R.id.etSenderAcc);
        ettransfertamount = (EditText)findViewById(R.id.etTransfertAmount);
        etsenderpin = (EditText)findViewById(R.id.etSenderPin);
        btnvirement = (Button)findViewById(R.id.btnvirement);
        virementProgressbar = (ProgressBar)findViewById(R.id.TransfertProgressBar);

        //Instanciation des objets internes
        playsound = new Playsound(this);
        networkConnection = new NetworkConnection(this);

        final String CurrentUrl = networkConnection.StoredUrl();
        final String SenderAccountnumber = networkConnection.StoredProfile("profnumcompte");

        btnvirement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap VirementData = new HashMap();
                VirementData.put("expediteur",SenderAccountnumber);
                VirementData.put("beneficiaire",etrecipientaccount.getText().toString());
                VirementData.put("montant",ettransfertamount.getText().toString());
                VirementData.put("senderpin",etsenderpin.getText().toString());
                if(etrecipientaccount.getText().toString().isEmpty() || ettransfertamount.getText().toString().isEmpty() || etsenderpin.getText().toString().isEmpty()){
                    playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    btnvirement.setEnabled(true);
                    virementProgressbar.setVisibility(View.GONE);
                    btnvirement.setText("Valider");
                }else{
                    btnvirement.setEnabled(false);
                    virementProgressbar.setVisibility(View.VISIBLE);
                    btnvirement.setText("Chargement...");
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tachevirement = new PostResponseAsyncTask(getApplicationContext(), VirementData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "180":
                                            playsound.jouer(R.raw.beneficiaireinexistant,"Le compte bénéficiaire n'existe pas");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;
                                        case "181":
                                            playsound.jouer(R.raw.beneficiairedesactive,"Le compte bénéficiaire est désactivé");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;
                                        case "182":
                                            playsound.jouer(R.raw.expediteurinexistant,"Le compte expéditeur n'existe pas");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;
                                        case "183":
                                            playsound.jouer(R.raw.expediteurdesactive,"Compte expéditeur désactivé");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;

                                        case "184":
                                            playsound.jouer(R.raw.soldeexpediteurinsuffisant,"Solde expéditeur insuffisant");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;

                                        case "185":
                                            playsound.jouer(R.raw.pinexpediteurincorrecte,"Le PIN expéditeur est incorrecte");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;

                                        case "":
                                            playsound.jouer(R.raw.noresponse,"Aucune reponse du serveur");
                                            btnvirement.setEnabled(true);
                                            virementProgressbar.setVisibility(View.GONE);
                                            btnvirement.setText("Valider");
                                            break;

                                        default:
                                            Log.i("retour",s);
                                            playsound.jouer(R.raw.virementreussi,"Virement effectué avec succès");
                                            if(isconnect){
                                                printtest(s);
                                                Toast.makeText(getApplicationContext(),"Dépot effectué avec succès",Toast.LENGTH_LONG).show();
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
                            tachevirement.execute(CurrentUrl+"/lifoutacourant/APIS/virement.php");

                        }catch (Exception e){
                            playsound.jouer(R.raw.erreurserveur,"Erreur serveur");
                            btnvirement.setEnabled(true);
                            virementProgressbar.setVisibility(View.GONE);
                            btnvirement.setText("Valider");
                        }
                    }else{
                        playsound.jouer(R.raw.erreurconnectioninternet,"Erreur connexion internet");
                        btnvirement.setEnabled(true);
                        virementProgressbar.setVisibility(View.GONE);
                        btnvirement.setText("Valider");
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

                //String agfees = jsonObject1.getString("agentfees");
                final String sysfees = jsonObject1.getString("systemfees");
                //final double transfees = Double.parseDouble(sysfees) + Double.parseDouble(agfees);

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
                            _setting.mPosPrnStr("Frais transaction : " + sysfees +" cfa");
                            _setting.mPosPrnStr("Date et heure : "+jsonObject1.getString("datetimetrans") );
                            _setting.mPosPrnStr("Solde : " +jsonObject1.getString("solde") +" CFA");
                            _setting.mPosPrnStr("Compte Beneficiaire : " +jsonObject1.getString("recipientaccount"));
                            //_setting.mPosPrnStr("Expediteur : "+jsonObject1.getString("nomcli") + " " +jsonObject1.getString("prenomcli"));//34 stars
                            _setting.mPosPrnStr("Expediteur : " +networkConnection.StoredProfile("proflname") +" "+networkConnection.StoredProfile("proffname"));
                            _setting.mPosPrnStr("Compte Expediteur : "+networkConnection.StoredProfile("profnumcompte"));
                            _setting.mPosPrnStr("portable Expediteur : "+networkConnection.StoredProfile("profphone"));
                            _setting.mPosPrnStr("Adresse Expediteur : " +networkConnection.StoredProfile("profadresse"));
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
