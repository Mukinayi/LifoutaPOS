package com.example.exact_it_dev.lifoutapos.moncompte;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.exact_it_dev.lifoutapos.R;

public class MonSolde extends AppCompatActivity {
    TextView tsolde;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mon_solde);
        setTitle("Mon Solde");

        Intent intent = getIntent();
        double amount = Double.parseDouble(intent.getStringExtra("solde"));

        tsolde = (TextView)findViewById(R.id.txtsoldeamount);
        tsolde.setText(String.format("%,.2f", amount));
    }
}
