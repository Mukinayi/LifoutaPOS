package com.example.exact_it_dev.lifoutapos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.imagpay.Settings;
import com.imagpay.SwipeEvent;
import com.imagpay.SwipeListener;
import com.imagpay.enums.CardDetected;
import com.imagpay.enums.EmvStatus;
import com.imagpay.enums.PrintStatus;
import com.imagpay.mpos.MposHandler;

public class DetailsCompte extends AppCompatActivity implements SwipeListener{
    TextView txtnom,txtprenom,txtsexe,txtphone,txtnumcompte,txtetatcompte,txttypeclient,txtsolde;
    Button btnprint,btncancel;
    MposHandler _handler;
    Settings _setting;
    private boolean isconnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_compte);
        setTitle("Détail du compte");

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

        txtnom = (TextView)findViewById(R.id.txtnom);
        txtprenom = (TextView)findViewById(R.id.txtprenom);
        txtsexe = (TextView)findViewById(R.id.txtsexe);
        txtphone = (TextView)findViewById(R.id.txtphone);
        txtnumcompte = (TextView)findViewById(R.id.txtnumcompte);
        txttypeclient = (TextView)findViewById(R.id.txtclienttype);
        txtsolde = (TextView)findViewById(R.id.txtsolde);
        txtetatcompte = (TextView)findViewById(R.id.txtetatcompte);



        Intent me = getIntent();
        String etatcompte = "";
            if(me.getStringExtra("comptestatus").equalsIgnoreCase("0")){
                etatcompte = "Inactif";
            }else{
                etatcompte = "Actif";
            }
        txtnom.setText(": "+me.getStringExtra("nom"));
        txtprenom.setText(": "+me.getStringExtra("prenom"));
        txtsexe.setText(": "+me.getStringExtra("sexe"));
        txtphone.setText(": "+me.getStringExtra("phone"));
        txtnumcompte.setText(": "+me.getStringExtra("numcompte"));
        txtsolde.setText(": "+me.getStringExtra("solde"));
        txtetatcompte.setText(": "+etatcompte);
        txttypeclient.setText(": "+me.getStringExtra("clienttype"));

        btnprint = (Button)findViewById(R.id.btnprint);
        btncancel =(Button)findViewById(R.id.btncancel);

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(home);
                finish();
            }
        });

        btnprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isconnect){
                    printform();
                }else{
                    Toast.makeText(getApplicationContext(),"Erreur impression",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void printform(){
        final Intent me = getIntent();

        //Pour impression
        new Thread(new Runnable() {

            public void run() {
                String etatcompte = "";
                if(me.getStringExtra("comptestatus").equalsIgnoreCase("0")){
                    etatcompte = "Inactif";
                }else{
                    etatcompte = "Actif";
                }
                try{
                    _setting.mPosEnterPrint();
                    _setting.mPosPrintFontSwitch(com.imagpay.Settings.MPOS_PRINT_FONT_NEW);
                    _setting.mPosPrintAlign(com.imagpay.Settings.MPOS_PRINT_ALIGN_CENTER);
                    _setting.mPosPrintTextSize(com.imagpay.Settings.MPOS_PRINT_TEXT_DOUBLE_HEIGHT);
                    _setting.mPosPrnStr("MICRO-FINANCE LIFOUTA ");
                    _setting.mPosPrintLn();
                    _setting.mPosPrintTextSize(com.imagpay.Settings.MPOS_PRINT_TEXT_NORMAL);
                    _setting.mPosPrintAlign(com.imagpay.Settings.MPOS_PRINT_ALIGN_LEFT);
                    _setting.mPosPrnStr("Numéro du compte : "+ me.getStringExtra("numcompte"));
                    _setting.mPosPrnStr("Solde : " +me.getStringExtra("solde"));
                    _setting.mPosPrnStr("Etat du compte : "+etatcompte);
                    _setting.mPosPrnStr("------------------------------");//34 stars
                    _setting.mPosPrnStr("Nom : "+me.getStringExtra("nom"));
                    _setting.mPosPrnStr("Prenom : "+me.getStringExtra("prenom"));
                    _setting.mPosPrnStr("Sexe : "+me.getStringExtra("sexe"));
                    _setting.mPosPrnStr("Portable : " +me.getStringExtra("phone"));
                    _setting.mPosPrnStr("Type client :" +me.getStringExtra("clienttype"));
                    _setting.mPosPrnStr("------------------------------");

                    _setting.mPosPrnStr("\n");

                    _setting.mPosPrnStr("Merci pour votre confiance");


                }catch(Exception e){
                    Toast.makeText(getApplicationContext(),"Impossible",Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
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
