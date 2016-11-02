package br.com.app.applica;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.io.FileInputStream;

import br.com.app.applica.entitity.Aplicacao;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * Created by felipe on 02/11/16.
 */
public class AplicacaoPersistActivity extends AppCompatActivity {
    private static Aplicacao aplicacao;
    public static String CURRENT_CARD = "";
    public static String AUTHENTICATION = "";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aplicacao_form);

        Intent intent = getIntent();
        CURRENT_CARD = intent.getStringExtra(AplicacaoPersistActivity.CURRENT_CARD);
        AUTHENTICATION = intent.getStringExtra(AplicacaoPersistActivity.AUTHENTICATION);
        System.out.println(AUTHENTICATION);

        aplicacao = new Aplicacao();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setTitle("Nova aplicação");

        setSupportActionBar(toolbar);

        setVacinaSpinner();
        setDoseSpinner();
        setAplicacaoEfetivadaListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(id){
            case 16908332:
                finish();
        }
        return true;
    }

    private void setAplicacaoEfetivadaListener(){
        final CheckBox efetivada_checkbox = (CheckBox) findViewById(R.id.aplicacao_efetivada);

        efetivada_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicacao.setEfetivada(efetivada_checkbox.isChecked());
            }
        });
    }

    private void setDoseSpinner(){
        Spinner dose_spinner = (Spinner) findViewById(R.id.aplicacao_dose);

        dose_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicacao.setDose((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.dose_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dose_spinner.setAdapter(adapter);
    }

    private void setVacinaSpinner(){
        Spinner vacina_spinner = (Spinner) findViewById(R.id.aplicacao_vacina);

        vacina_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicacao.setVacina((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.vacina_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vacina_spinner.setAdapter(adapter);
    }

    private class AplicacaoPersistTask extends AsyncTask<Object, Void, Aplicacao > {

        @Override
        protected Aplicacao doInBackground(Object... params) {
            Cardeneta cardeneta = new Cardeneta();
            cardeneta.set_id(CURRENT_CARD);
            //cardeneta.setAuthToken(AUTHENTICATION);
            try {
                User user = new User();
                if (user.getAuthToken() == null) {

                    FileInputStream fis = getApplicationContext().openFileInput("userData");

                    user.readUserDataLocally(fis);
                }



            }catch(Exception e){

            }
            return null;
        }
    }
}
