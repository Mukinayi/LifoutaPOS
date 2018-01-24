package com.example.exact_it_dev.lifoutapos.enrolement;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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


public class Enrolement extends AppCompatActivity implements SwipeListener {
    Spinner spinnerSexe;
    Spinner accounttype;
    EditText dn,ln,nom,prenom,adress,phone;
    Button btnval;
    ProgressBar pbar;
    Playsound playsound;
    NetworkConnection networkConnection;
    MposHandler _handler;
    Settings _setting;
    private boolean isconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolement);
        setTitle("Création du compte Lifouta");

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



        spinnerSexe = (Spinner) findViewById(R.id.SpinnerSexe);
        ArrayList<String> element = new ArrayList<String>();
        element.add("M");
        element.add("F");
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, element);
        spinnerSexe.setAdapter(arrayAdapter);

        accounttype = (Spinner)findViewById(R.id.AccountType);
        ArrayList<String> compte = new ArrayList<String>();
        compte.add("Courant");
        compte.add("Epargne");

        ArrayAdapter compteAdapter = new ArrayAdapter(this,android.R.layout.select_dialog_item,compte);
        accounttype.setAdapter(compteAdapter);

        //recupération des données du formulaire
        dn = (EditText)findViewById(R.id.etDnaiss);
        ln = (EditText)findViewById(R.id.etLieunais);
        nom = (EditText)findViewById(R.id.etNom);
        prenom = (EditText)findViewById(R.id.etPrenom);
        adress = (EditText)findViewById(R.id.etAdress);
        phone = (EditText)findViewById(R.id.etphone);
        btnval = (Button)findViewById(R.id.btnenrol);
        pbar = (ProgressBar)findViewById(R.id.pbarenrol);

        //Network
        networkConnection = new NetworkConnection(this);



        btnval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int typecompte;
                if(accounttype.getSelectedItem().toString().equals("Courant")){
                    typecompte = 1;
                }else{
                    typecompte = 2;
                }
                HashMap PostData = new HashMap();
                PostData.put("prenom",prenom.getText().toString());
                PostData.put("nom",nom.getText().toString());
                PostData.put("sexe",spinnerSexe.getSelectedItem().toString());
                PostData.put("dn",dn.getText().toString());
                PostData.put("ln",ln.getText().toString());
                PostData.put("adresse",adress.getText().toString());
                PostData.put("phone",phone.getText().toString());
                PostData.put("agentid",networkConnection.StoredProfile("profagentid"));
                PostData.put("typeaccountid",String.valueOf(typecompte));
                if(dn.getText().toString().isEmpty() || ln.getText().toString().isEmpty() || nom.getText().toString().isEmpty() || prenom.getText().toString().isEmpty() || adress.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || spinnerSexe.getSelectedItem().toString().isEmpty() || accounttype.getSelectedItem().toString().isEmpty()){
                   playsound.jouer(R.raw.remplirtousleschamps,"Veuillez remplir tous les champs");
                    pbar.setVisibility(View.GONE);
                    pbar.animate().alpha(0f).setDuration(2000);
                    btnval.setEnabled(true);
                    btnval.setText("Valider");
                }else{
                    pbar.setVisibility(View.VISIBLE);
                    pbar.animate().alpha(1f).setDuration(1000);
                    btnval.setEnabled(false);
                    btnval.setText("Chargement...");

                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tacheone = new PostResponseAsyncTask(getApplicationContext(), PostData, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch(s){
                                        case "199":
                                            pbar.setVisibility(View.GONE);
                                            pbar.animate().alpha(0f).setDuration(2000);
                                            btnval.setEnabled(true);
                                            btnval.setText("Valider");
                                                playsound.jouer(R.raw.clientexiste,"Ce client existe déjà");
                                            break;
                                        case "201":
                                            pbar.setVisibility(View.GONE);
                                            pbar.animate().alpha(0f).setDuration(2000);
                                            btnval.setEnabled(true);
                                            btnval.setText("Valider");
                                               playsound.jouer(R.raw.echeccreationclient,"Echec enregistrement client");
                                            break;

                                        case "202":
                                            pbar.setVisibility(View.GONE);
                                            pbar.animate().alpha(0f).setDuration(2000);
                                            btnval.setEnabled(true);
                                            btnval.setText("Valider");
                                                playsound.jouer(R.raw.maxcompteatteint,"Nombre maximal de compte atteint pour ce client");
                                            break;
                                        case "203":
                                            pbar.setVisibility(View.GONE);
                                            pbar.animate().alpha(0f).setDuration(2000);
                                            btnval.setEnabled(true);
                                            btnval.setText("Valider");
                                                playsound.jouer(R.raw.echeccreationclient,"Echec création compte");
                                            break;

                                        case "":
                                            playsound.jouer(R.raw.erreurserveur,"Aucune réponse du serveur");
                                            pbar.setVisibility(View.GONE);
                                            pbar.animate().alpha(0f).setDuration(2000);
                                            btnval.setEnabled(true);
                                            btnval.setText("Valider");
                                            break;
                                            default:
                                                if(isconnect){
                                                    playsound.jouer(R.raw.impressionencours,"Impression en cours");
                                                    printtest(s);
                                                    Toast.makeText(getApplicationContext(),"Compte créé avec succès",Toast.LENGTH_LONG).show();
                                                    Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                                    startActivity(main);
                                                    finish();
                                                }else{
                                                    try {
                                                        Thread.sleep(2000);
                                                        Toast.makeText(getApplicationContext(),"Compte créé avec succès",Toast.LENGTH_LONG).show();
                                                        Intent main = new Intent(getApplicationContext(),MainActivity.class);
                                                        startActivity(main);
                                                        finish();
                                                        Toast.makeText(getApplicationContext(),"Erreur impression",Toast.LENGTH_SHORT).show();
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                                break;

                                    }



                                }
                            });
                            tacheone.execute(networkConnection.StoredUrl()+"/lifoutacourant/APIS/enrolement.php");
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(),"Erreur du web service",Toast.LENGTH_SHORT).show();
                            pbar.setVisibility(View.GONE);
                            pbar.animate().alpha(0f).setDuration(2000);
                            btnval.setEnabled(true);
                            btnval.setText("Valider");
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                        pbar.setVisibility(View.GONE);
                        pbar.animate().alpha(0f).setDuration(2000);
                        btnval.setEnabled(true);
                        btnval.setText("Valider");
                    }



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
                            _setting.mPosPrnStr("MICRO-FINANCE LIFOUTA ");
                            _setting.mPosPrintLn();
                            _setting.mPosPrintTextSize(com.imagpay.Settings.MPOS_PRINT_TEXT_NORMAL);
                            _setting.mPosPrintAlign(com.imagpay.Settings.MPOS_PRINT_ALIGN_LEFT);
                            _setting.mPosPrnStr("Numéro du compte : "+ jsonObject1.getString("numcompte"));
                            _setting.mPosPrnStr("Type du compte : " +jsonObject1.getString("typeaccount"));
                            _setting.mPosPrnStr("Date de création : " +jsonObject1.getString("creationdate"));
                            _setting.mPosPrnStr("Solde : " +jsonObject1.getString("solde") +", Monnaie :" +jsonObject1.getString("currency"));
                            _setting.mPosPrnStr("------------------------------");//34 stars
                            _setting.mPosPrnStr("Nom : "+jsonObject1.getString("nomcli"));
                            _setting.mPosPrnStr("Prenom : "+jsonObject1.getString("prenomcli"));
                            _setting.mPosPrnStr("Sexe : "+jsonObject1.getString("sexecli"));
                            _setting.mPosPrnStr("Portable : " +jsonObject1.getString("portablecli"));
                            _setting.mPosPrnStr("Adresse : " +jsonObject1.getString("adressecli"));
                            _setting.mPosPrnStr("Type client :" +jsonObject1.getString("clienttype"));
                            _setting.mPosPrnStr("------------------------------");

                            _setting.mPosPrnStr("\n");

                            _setting.mPosPrnStr("Merci pour votre confiance");


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
