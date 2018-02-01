package com.example.exact_it_dev.lifoutapos.bouquets.easytv;


import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.bouquets.canalplus.Canalplus;

public class EasyTV extends AppCompatActivity {
    Button btneasycheck;
    EditText etcardnumeasy;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_tv);
        setTitle("Renouvellement Easy Tv");

        btneasycheck = (Button)findViewById(R.id.btneasycheck);
        etcardnumeasy = (EditText)findViewById(R.id.etcardnumeasy);

        pDialog = new ProgressDialog(EasyTV.this);
        pDialog.setMessage("Chargement...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);


        btneasycheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etcardnumeasy.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez renseigner le numéro de la carte",Toast.LENGTH_SHORT).show();
                }else{
                    pDialog.show();
                }
            }
        });


    }
}
