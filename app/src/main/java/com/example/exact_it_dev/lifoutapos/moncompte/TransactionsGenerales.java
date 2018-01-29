package com.example.exact_it_dev.lifoutapos.moncompte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.adapter.CustomAdapter;
import com.example.exact_it_dev.lifoutapos.adapter.DataModel;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TransactionsGenerales extends AppCompatActivity {
    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    NetworkConnection networkConnection;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_generales);
        setTitle("Transactions Générales");

        listView =(ListView)findViewById(R.id.listViewCustom);
        networkConnection = new NetworkConnection(this);
        final String CurrentURL = networkConnection.StoredUrl();
        final String numcompte = networkConnection.StoredProfile("profnumcompte");
        HashMap datas = new HashMap();
        datas.put("numcompte",numcompte);
        dataModels= new ArrayList<>();
        pDialog = new ProgressDialog(TransactionsGenerales.this);
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(true);
        pDialog.setMessage("Recherche transactions");
        pDialog.show();

        if(networkConnection.isConnected()){
            try {
                PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), datas, false, new AsyncResponse() {
                    @Override
                    public void processFinish(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for(int i = 0; i<jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                dataModels.add(new DataModel(jsonObject.getString("total") +" CFA",jsonObject.getString("transcount"),jsonObject.getString("myfees")+" CFA",jsonObject.getString("transtype")));
                            pDialog.dismiss();
                            }
                            pDialog.dismiss();
                            adapter= new CustomAdapter(dataModels,getApplicationContext());
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    DataModel dataModel= dataModels.get(position);

                /*Snackbar.make(view, dataModel.getTextone()+"\n"+dataModel.getTextsecond(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/
                                    Toast.makeText(getApplicationContext(),dataModel.getTxtCommission(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }catch (JSONException e){
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Erreur JSON",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TransactionsGenerales.this,MonCompte.class);
                            startActivity(intent);
                            finish();
                        }
                        Log.i("result",s);
                    }
                });
                tache.execute(CurrentURL+"/lifoutacourant/APIS/transactionsgeneral.php");
            }catch (Exception e){
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(),"Erreur Web service",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(TransactionsGenerales.this,MonCompte.class);
                startActivity(intent);
                finish();
            }
        }else{
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(),"Erreur connexion Internet",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TransactionsGenerales.this,MonCompte.class);
            startActivity(intent);
            finish();
        }



    }
}
