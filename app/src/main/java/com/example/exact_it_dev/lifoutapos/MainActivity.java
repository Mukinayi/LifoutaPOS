package com.example.exact_it_dev.lifoutapos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.exact_it_dev.lifoutapos.airtimes.Airtimes;
import com.example.exact_it_dev.lifoutapos.bouquets.BouquetNumeriques;
import com.example.exact_it_dev.lifoutapos.cashin.Cashin;
import com.example.exact_it_dev.lifoutapos.cashout.Cashout;
import com.example.exact_it_dev.lifoutapos.enrolement.GestionComptes;
import com.example.exact_it_dev.lifoutapos.payer.Payer;
import com.example.exact_it_dev.lifoutapos.servicespublics.snde.PaiementSNDE;
import com.example.exact_it_dev.lifoutapos.servicespublics.sne.PaiementSNE;
import com.example.exact_it_dev.lifoutapos.settings.Playsound;
import com.example.exact_it_dev.lifoutapos.settings.Settings;
import com.example.exact_it_dev.lifoutapos.transfert.TransfertBoard;
import com.example.exact_it_dev.lifoutapos.virement.Virement;

public class MainActivity extends AppCompatActivity {
    Button enrolement;
    Button depot;
    Button retrait;
    Button virement;
    Button transfert;
    Button sne;
    Button snde;
    Button airtime;
    Button bouquets;
    Button payer;
    Playsound playsound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playsound = new Playsound(this);


        //Bouton pour création des comptes
        enrolement = (Button)findViewById(R.id.enrolement);
        enrolement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playsound.jouer(R.raw.enrolement,"Enrolement");
                Intent comptes = new Intent(getApplicationContext(), GestionComptes.class);
                startActivity(comptes);
            }
        });

        //Bouton pour effectuer un dépôt
        depot = (Button)findViewById(R.id.cashin);
        depot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playsound.jouer(R.raw.cashin,"Vous avez cliqué sur dépôt");
                Intent CashinIntent = new Intent(getApplicationContext(),Cashin.class);
                startActivity(CashinIntent);
            }
        });

        //Bouton pour effectuer un retrait
        retrait = (Button)findViewById(R.id.cashout);
        retrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent CashoutIntent = new Intent(getApplicationContext(),Cashout.class);
                startActivity(CashoutIntent);
            }
        });

        //Bouton pour effectuer un virement
        virement = (Button)findViewById(R.id.virement);
        virement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent VirementIntent = new Intent(getApplicationContext(),Virement.class);
                startActivity(VirementIntent);
            }
        });

        //Bouton pour effectuer un transfert
        transfert = (Button)findViewById(R.id.transfert);
        transfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent TransfertIntent = new Intent(getApplicationContext(),TransfertBoard.class);
                startActivity(TransfertIntent);
            }
        });

        //Bouton pour effectuer un paiement sne
        sne = (Button)findViewById(R.id.sne);
        sne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Paiement SNE",Toast.LENGTH_SHORT).show();
                Intent SneIntent = new Intent(getApplicationContext(),PaiementSNE.class);
                startActivity(SneIntent);
            }
        });

        //Bouton pour effectuer un paiement snde
        snde = (Button)findViewById(R.id.snde);
        snde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Paiement SNDE",Toast.LENGTH_SHORT).show();
                Intent SndeIntent = new Intent(getApplicationContext(),PaiementSNDE.class);
                startActivity(SndeIntent);
            }
        });

        //Bouton pour effectuer un paiement airtime
        airtime = (Button)findViewById(R.id.airtime);
        airtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Paiement Crédits",Toast.LENGTH_SHORT).show();
                Intent AirtimeIntent = new Intent(getApplicationContext(),Airtimes.class);
                startActivity(AirtimeIntent);
            }
        });

        //Bouton pour effectuer un paiement bouquets
        bouquets = (Button)findViewById(R.id.bouquets);
        bouquets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Paiement Bouquets",Toast.LENGTH_SHORT).show();
                Intent BouquetsIntent = new Intent(getApplicationContext(),BouquetNumeriques.class);
                startActivity(BouquetsIntent);
            }
        });

        //Bouton pour effectuer un paiement payer
        payer = (Button)findViewById(R.id.payer);
        payer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Vous avez cliqué sur Payer",Toast.LENGTH_SHORT).show();
                Intent PayerIntent = new Intent(getApplicationContext(),Payer.class);
                startActivity(PayerIntent);
            }
        });






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
