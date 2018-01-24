package com.example.exact_it_dev.lifoutapos.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.R;
import com.example.exact_it_dev.lifoutapos.network.NetworkConnection;

public class Settings extends AppCompatActivity {
    SharedPreferences.Editor datas;
    public final String MEMORY = "MEMOIRE";
    Switch swich;
    SharedPreferences stored;
    LinearLayout linearLayout;
    Button seturl;
    EditText eturl;
    NetworkConnection networkConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Parametres de l'application");

        datas = getSharedPreferences(MEMORY, Context.MODE_PRIVATE).edit();
        stored = getSharedPreferences(MEMORY,Context.MODE_PRIVATE);

        linearLayout = (LinearLayout) findViewById(R.id.webserviceLayout);
        seturl = (Button)findViewById(R.id.btnseturl);
        eturl = (EditText)findViewById(R.id.etURL);

        networkConnection = new NetworkConnection(this);
        String URL = networkConnection.StoredUrl();
        //Toast.makeText(getApplicationContext(),URL,Toast.LENGTH_SHORT).show();

        seturl.setEnabled(true);
        seturl.setText("Valider");

        seturl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eturl.getText().toString().isEmpty() || !URLUtil.isValidUrl(eturl.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Veuillez renseigner une URL valide",Toast.LENGTH_SHORT).show();
                }else{
                    seturl.setEnabled(false);
                    seturl.setText("Chargement...");
                    datas.putString("storedurl",eturl.getText().toString());
                    datas.commit();
                    linearLayout.animate().alpha(0f).setDuration(1000);
                    linearLayout.setVisibility(View.GONE);

                }
            }
        });






        swich = (Switch)findViewById(R.id.switch1);

        swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    datas.putBoolean("voice",true);
                    datas.commit();
                    Toast.makeText(getApplicationContext(),"Voix activée",Toast.LENGTH_SHORT).show();
                }else{
                    datas.putBoolean("voice",false);
                    datas.commit();
                    Toast.makeText(getApplicationContext(),"Voix désactivée",Toast.LENGTH_SHORT).show();
                }
            }
        });

        boolean voice = stored.getBoolean("voice",false);

        if(voice==true){
            swich.setChecked(true);
        }else{
            swich.setChecked(false);
        }

        final Context context = this;
        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            int count = 0;
            int total = 4;
            int currentInt;
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                total--;
                if(total !=0) {
                    Toast.makeText(context, "Encore " + String.valueOf(total) + " fois", Toast.LENGTH_SHORT).show();
                }


                if(count==total){
                    Toast.makeText(getApplicationContext(),"Mode modification URL Web Service",Toast.LENGTH_SHORT).show();
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout.animate().alpha(1f).setDuration(1000);
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Toast.makeText(context, "onLongPress", Toast.LENGTH_SHORT).show();
            }

        };

        final GestureDetector detector = new GestureDetector(listener);

        detector.setOnDoubleTapListener(listener);
        detector.setIsLongpressEnabled(true);

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

    }
}
