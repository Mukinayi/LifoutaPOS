package com.example.exact_it_dev.lifoutapos.bouquets.canalplus;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;

public class Canalplus extends AppCompatActivity {
    Button btncanalcheck;
    EditText etcardnum;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canalplus);
        setTitle("Renouvellement canalplus");

        btncanalcheck = (Button)findViewById(R.id.btncanalcheck);
        etcardnum = (EditText)findViewById(R.id.etcardnum);

        pDialog = new ProgressDialog(Canalplus.this);
        pDialog.setMessage("Chargement...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);


        btncanalcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etcardnum.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez renseigner le numéro de la carte",Toast.LENGTH_SHORT).show();
                }else{
                    pDialog.show();
                }
            }
        });
    }
}
