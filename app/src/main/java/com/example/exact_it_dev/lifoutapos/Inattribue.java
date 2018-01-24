package com.example.exact_it_dev.lifoutapos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.settings.Settings;

public class Inattribue extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inattribue);
        Intent i = getIntent();
        String mes = i.getStringExtra("message");
        setTitle(mes);
        textView = (TextView)findViewById(R.id.textView7);
        textView.setText(mes);
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
