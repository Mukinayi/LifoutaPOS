package com.example.exact_it_dev.lifoutapos.network;

import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by EXACT-IT-DEV on 1/10/2018.
 */

public class NetworkConnection {
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static String MEMORY = "MEMOIRE";

    public NetworkConnection(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(MEMORY,Context.MODE_PRIVATE);
    }

    public boolean isConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null){
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null){
                if(networkInfo.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }

    public String StoredUrl(){

        String URL = sharedPreferences.getString("storedurl","https://lifouta.com");
        return URL;
    }

    public void StoreData(String profnom,String profprenom,String profnumcompte,String profagentid,String profphone, String profadresse){
        editor  = context.getSharedPreferences(MEMORY,Context.MODE_PRIVATE).edit();
        editor.putString("proflname",profnom);
        editor.putString("proffname",profprenom);
        editor.putString("profnumcompte",profnumcompte);
        editor.putString("profagentid",profagentid);
        editor.putString("profphone",profphone);
        editor.putString("profadresse",profadresse);
        editor.commit();
    }

    public String StoredProfile(String input){
        return sharedPreferences.getString(input,null);
    }

}
