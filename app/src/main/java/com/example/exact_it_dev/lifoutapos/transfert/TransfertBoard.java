package com.example.exact_it_dev.lifoutapos.transfert;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.example.exact_it_dev.lifoutapos.transfert.TransfertCashToCash;
import com.example.exact_it_dev.lifoutapos.transfert.TransfertCompteToCash;

public class TransfertBoard extends AppCompatActivity {
    Button btncashtocash,btncomptetocash,btnwithdraw,btncheck;
    Playsound playsound;
    NetworkConnection networkConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert_board);
        setTitle("Transfert");

        //Récupération des composants
        btncashtocash = (Button)findViewById(R.id.btntransfertcashtocash);
        btncomptetocash = (Button)findViewById(R.id.btntransfertcomptetocash);
        btnwithdraw = (Button)findViewById(R.id.btntransfertwithdrawal);
        //btncheck = (Button)findViewById(R.id.btntransfertcheck);


        //Cash to Cash
        btncashtocash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tcashtocah = new Intent(getApplicationContext(), TransfertCashToCash.class);
                startActivity(tcashtocah);
            }
        });

        //Compte to Cash

        btncomptetocash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tcomptetocash = new Intent(getApplicationContext(),TransfertCompteToCash.class);
                startActivity(tcomptetocash);
            }
        });

        btnwithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent withdraw = new Intent(getApplicationContext(),WithdrawTransfert.class);
                startActivity(withdraw);
            }
        });

    }
}
