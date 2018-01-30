package com.example.exact_it_dev.lifoutapos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.example.exact_it_dev.lifoutapos.settings.Settings;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Welcome extends AppCompatActivity {
    NetworkConnection networkConnection;
    TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        networkConnection = new NetworkConnection(this);
        String storedurl = networkConnection.StoredUrl();
        String imei ="";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            imei = "00000000000000000";
            //imei = "362523432421083";
        }else{

            imei = tm.getDeviceId();
        }
        Log.i("imei",imei);
        final String unident = "Terminal non identifié";
        final String unass = "Terminal non assigné";
        final String connex = "Erreur Connexion internet";
        final String webserv = "Erreur Webservice";

        HashMap PostData = new HashMap();
        PostData.put("imei",imei);
        if(networkConnection.isConnected()){
            try {
                PostResponseAsyncTask task = new PostResponseAsyncTask(getApplicationContext(), PostData, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        switch (s){
                            case "180":
                                Toast.makeText(getApplicationContext(),"Terminal non reconnu",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Welcome.this,Inattribue.class);
                                intent.putExtra("message",unident);
                                startActivity(intent);
                                finish();
                            break;
                            case "201":
                                Toast.makeText(getApplicationContext(),"Terminal non attribué",Toast.LENGTH_SHORT).show();
                                Intent unassign = new Intent(getApplicationContext(),Inattribue.class);
                                unassign.putExtra("message",unass);
                                startActivity(unassign);
                                finish();

                                break;
                            case "":
                                Intent erserve = new Intent(getApplicationContext(),Inattribue.class);
                                erserve.putExtra("message",webserv);
                                startActivity(erserve);
                                finish();
                                break;
                            default:
                                try{
                                    JSONArray jsonArray = new JSONArray(s);
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    networkConnection.StoreData(jsonObject.getString("nomcli"),jsonObject.getString("prenomcli"),jsonObject.getString("numcompte"),jsonObject.getString("agentid"),jsonObject.getString("portablecli"),jsonObject.getString("adressecli"));
                                }catch (JSONException e){
                                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                                Toast.makeText(getApplicationContext(),"Vérification réussie",Toast.LENGTH_SHORT).show();
                                Intent home = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(home);
                                finish();
                                break;
                        }
                        Log.i("resul",s);

                    }
                });
                task.execute(storedurl+"/lifoutacourant/APIS/checkterminal.php");
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),"Erreur du web service",Toast.LENGTH_SHORT).show();
                Intent erserv = new Intent(getApplicationContext(),Inattribue.class);
                erserv.putExtra("message",webserv);
                startActivity(erserv);
                finish();
            }
        }else{
            Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
            Intent con = new Intent(getApplicationContext(),Inattribue.class);
            con.putExtra("message",connex);
            startActivity(con);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.side_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.parametre:
                Intent settings = new Intent(getApplicationContext(), Settings.class);
                startActivity(settings);
                break;
            case R.id.transactionfees:
                Intent transfees = new Intent(getApplicationContext(),FraisTransaction.class);
                startActivity(transfees);
                break;
            default:
                return false;
        }
        return true;
    }
}
