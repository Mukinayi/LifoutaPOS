package com.example.exact_it_dev.lifoutapos.transfert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.exact_it_dev.lifoutapos.ConfirmationTrasaction;
import com.example.exact_it_dev.lifoutapos.MainActivity;
import com.example.exact_it_dev.lifoutapos.R;

public class ResultTransfertVerification extends AppCompatActivity {
        TextView lblnom,lblprenom,lblmontant,lblfees,lblphone,lblstate;
        Button btncontinue,btnannuler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_transfert_verification);

        Intent me = getIntent();
        String state = me.getStringExtra("transfertstate");
        String montant = me.getStringExtra("transamount");
        String nom = me.getStringExtra("recilname");
        String prenom = me.getStringExtra("recifname");
        String phone = me.getStringExtra("reciphone");
        String fees = me.getStringExtra("withdrawfees");
        final String idtrans = me.getStringExtra("idtrans");
        final String transcode = me.getStringExtra("transcode");

        lblnom = (TextView)findViewById(R.id.LabelNom);
        lblprenom = (TextView)findViewById(R.id.labelPenom);
        lblmontant = (TextView)findViewById(R.id.labelMontant);
        lblfees = (TextView)findViewById(R.id.labelFees);
        lblphone = (TextView)findViewById(R.id.labelPortable);
        lblstate = (TextView)findViewById(R.id.labelEtat);
        btncontinue = (Button)findViewById(R.id.btncontinuer);
        btnannuler = (Button)findViewById(R.id.buttonAnnuler);

        lblnom.setText(": "+nom);
        lblprenom.setText(": "+prenom);
        lblstate.setText(": "+state);
        lblmontant.setText(": "+montant+" cfa");
        lblphone.setText(": "+phone);
        lblfees.setText(": "+fees+" cfa");


        if(state.equals("RETIRE")){
            btncontinue.setEnabled(false);
            btncontinue.setVisibility(View.GONE);
        }else{
            btncontinue.setEnabled(true);
            btncontinue.setVisibility(View.VISIBLE);
        }

        btncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DetailsIntent = new Intent(getApplicationContext(), ConfirmationRetraitTransfert.class);
                DetailsIntent.putExtra("idtrans",idtrans);
                DetailsIntent.putExtra("transcode",transcode);
                startActivity(DetailsIntent);
            }
        });

        btnannuler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
                finish();
            }
        });
    }
}
