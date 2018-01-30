package com.example.exact_it_dev.lifoutapos.moncompte;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;

public class TransactionsByDateFromTo extends AppCompatActivity {
    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    NetworkConnection networkConnection;
    ProgressDialog pDialog;
    EditText etstartdate,etenddate;
    Button btntransdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions_by_date_from_to);
        setTitle("Transactions par période");

        btntransdate = (Button)findViewById(R.id.btntransbydate);
        etstartdate = (EditText)findViewById(R.id.etDateStart);
        etenddate = (EditText)findViewById(R.id.etDateend);

        listView =(ListView)findViewById(R.id.listViewDates);
        networkConnection = new NetworkConnection(this);
        final String CurrentURL = networkConnection.StoredUrl();
        final String numcompte = networkConnection.StoredProfile("profnumcompte");
        HashMap datas = new HashMap();
        datas.put("numcompte",numcompte);
        dataModels= new ArrayList<>();

        btntransdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(TransactionsByDateFromTo.this);
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(true);
                pDialog.setMessage("Recherche transactions");
                pDialog.show();
                final HashMap datas = new HashMap();
                datas.put("numcompte",numcompte);
                datas.put("datebegin",etstartdate.getText().toString());
                datas.put("datefin",etenddate.getText().toString());

                if(etstartdate.getText().toString().isEmpty() || etenddate.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Veuillez remplir les dates",Toast.LENGTH_SHORT).show();
                    pDialog.dismiss();
                }else{
                    if(networkConnection.isConnected()){
                        try {
                            PostResponseAsyncTask tache = new PostResponseAsyncTask(getApplicationContext(), datas, false, new AsyncResponse() {
                                @Override
                                public void processFinish(String s) {
                                    switch (s){
                                        case "160":
                                                Toast.makeText(getApplicationContext(),"La date du début est supérieure à celle de fin",Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                            break;
                                        case "161":
                                            Toast.makeText(getApplicationContext(),"La date de fin doit être inférieure ou égale à celle d\'aujourd\'hui",Toast.LENGTH_SHORT).show();
                                            pDialog.dismiss();
                                            break;
                                        case "180":
                                            Toast.makeText(getApplicationContext(),"Pas de type de transactions disponible",Toast.LENGTH_SHORT).show();
                                            pDialog.dismiss();
                                            break;
                                        default:
                                            try {
                                                JSONArray jsonArray = new JSONArray(s);
                                                dataModels.clear();
                                                listView.setAdapter(null);
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
                                                        Toast.makeText(getApplicationContext(),dataModel.getTxtCommission(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }catch (JSONException e){
                                                Toast.makeText(getApplicationContext(),"Erreur JSON",Toast.LENGTH_SHORT).show();
                                                pDialog.dismiss();
                                            }
                                            Log.i("result",s);
                                            break;
                                    }
                                }
                            });
                            tache.execute(CurrentURL+"/lifoutacourant/APIS/transbydate.php");
                        }catch (Exception e){
                            pDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Erreur du serveur",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        pDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Erreur connexion internet",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }
}
