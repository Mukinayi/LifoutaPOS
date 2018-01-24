package com.example.exact_it_dev.lifoutapos.moncompte;

import android.app.ProgressDialog;
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
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.util.HashMap;

public class ChangementPin extends AppCompatActivity {
    NetworkConnection networkConnection;
    EditText etoldpin,etnewpin,etconfirmpin;
    Button btnpin;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changement_pin);
        setTitle("Changement de PIN");

        etoldpin = (EditText)findViewById(R.id.etoldpin);
        etconfirmpin = (EditText)findViewById(R.id.etconfirmpin);
        etnewpin = (EditText)findViewById(R.id.etnewpin);
        btnpin = (Button)findViewById(R.id.btnpin);


        networkConnection = new NetworkConnection(this);

        final String CurrentURL = networkConnection.StoredUrl();
        final String myaccount = networkConnection.StoredProfile("profnumcompte");

        btnpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap PinData = new HashMap();
                PinData.put("oldpin",etoldpin.getText().toString());
                PinData.put("newpin",etnewpin.getText().toString());
                PinData.put("myaccount",myaccount);

                pDialog = new ProgressDialog(ChangementPin.this);
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(false);



                if(etoldpin.getText().toString().isEmpty() || etnewpin.getText().toString().isEmpty() || etconfirmpin.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez remplir tous les champs",Toast.LENGTH_SHORT).show();
                    btnpin.setEnabled(true);
                    btnpin.setText("Changer pin");
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                }else{
                    pDialog.setMessage("Chargement...");
                    btnpin.setEnabled(false);
                    btnpin.setText("Chargement...");
                    pDialog.show();

                    if(!etconfirmpin.getText().toString().equals(etnewpin.getText().toString())){
                        Toast.makeText(getApplicationContext(),"Les nouveaux mots de passe ne sont pad identiques",Toast.LENGTH_SHORT).show();
                        btnpin.setEnabled(true);
                        btnpin.setText("Changer pin");
                        if(pDialog.isShowing()){
                            pDialog.dismiss();
                        }
                    }else{
                        if(etnewpin.getText().toString().length() != 4){
                            Toast.makeText(getApplicationContext(),"Les nouveaux mots doivent avoir 4 caractères",Toast.LENGTH_SHORT).show();
                            btnpin.setEnabled(true);
                            btnpin.setText("Changer pin");
                            if(pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                        }else{
                            if(networkConnection.isConnected()){
                                try {
                                    PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), PinData, false, new AsyncResponse() {
                                        @Override
                                        public void processFinish(String s) {
                                            switch (s){
                                                case "180":
                                                    Toast.makeText(getApplicationContext(),"Compte Lifouta inexistant",Toast.LENGTH_SHORT).show();
                                                    if(pDialog.isShowing()){
                                                        pDialog.dismiss();
                                                    }
                                                    btnpin.setEnabled(true);
                                                    btnpin.setText("Changer pin");
                                                    break;
                                                case "181":
                                                    Toast.makeText(getApplicationContext(),"Ancien mot de passe incorrecte",Toast.LENGTH_SHORT).show();
                                                    if(pDialog.isShowing()){
                                                        pDialog.dismiss();
                                                    }
                                                    btnpin.setEnabled(true);
                                                    btnpin.setText("Changer pin");
                                                    break;
                                                case "201":
                                                    Toast.makeText(getApplicationContext(),"Echec modification PIN",Toast.LENGTH_SHORT).show();
                                                    if(pDialog.isShowing()){
                                                        pDialog.dismiss();
                                                    }
                                                    btnpin.setEnabled(true);
                                                    btnpin.setText("Changer pin");
                                                    break;

                                                default:
                                                    Toast.makeText(getApplicationContext(),"PIN modifié avec succès",Toast.LENGTH_LONG).show();
                                                    Intent home = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(home);
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                                    tache.execute(CurrentURL+"/lifoutacourant/APIS/changementpin.php");
                                }catch (Exception e){
                                    Toast.makeText(getApplicationContext(),"Erreur serveur",Toast.LENGTH_SHORT).show();
                                    btnpin.setEnabled(true);
                                    btnpin.setText("Changer pin");
                                    pDialog.dismiss();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                                btnpin.setEnabled(true);
                                btnpin.setText("Changer pin");
                                pDialog.dismiss();
                            }
                        }

                    }
                }
            }
        });

    }
}
