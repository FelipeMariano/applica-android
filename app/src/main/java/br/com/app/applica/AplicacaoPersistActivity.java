package br.com.app.applica;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.entitity.Aplicacao;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * Created by felipe on 02/11/16.
 */
public class AplicacaoPersistActivity extends AppCompatActivity {
    private static Aplicacao aplicacao;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    public static String CURRENT_CARD = "";

    public void createAplicacao(Aplicacao aplicacao){
        try {
            FileInputStream fis = getApplicationContext().openFileInput("userData");
            User user = new User();
            user.readUserDataLocally(fis);

            AplicacaoPersistTask aplicacaoPersist = new AplicacaoPersistTask();

            aplicacaoPersist.execute(aplicacao, user);
            aplicacaoPersist.get(5000, TimeUnit.MILLISECONDS);

        }catch(Exception e){

        }

        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aplicacao_form);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_back);
        toolbar.setTitle("Nova aplicação");

        setSupportActionBar(toolbar);

        setVacinaSpinner();
        setDoseSpinner();
        setAplicacaoEfetivadaListener();
        setDatePickerDialog();
        setSaveButton();
    }

    @Override
    public void onResume(){
        super.onResume();

        Intent intent = getIntent();
        CURRENT_CARD = intent.getStringExtra(AplicacaoPersistActivity.CURRENT_CARD);
        System.out.println(CURRENT_CARD);
        aplicacao = new Aplicacao();
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

    private void setSaveButton(){
        Button btnSave = (Button) findViewById(R.id.btn_add_aplicacao);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aplicacao = new Aplicacao();

                CheckBox efetivada_checkbox = (CheckBox) findViewById(R.id.aplicacao_efetivada);
                aplicacao.setEfetivada(efetivada_checkbox.isChecked());

                Spinner dose_spinner = (Spinner) findViewById(R.id.aplicacao_dose);
                aplicacao.setDose((String) dose_spinner.getSelectedItem());

                Spinner vacina_spinner = (Spinner) findViewById(R.id.aplicacao_vacina);
                aplicacao.setVacina((String) vacina_spinner.getSelectedItem());

                aplicacao.setData(year_x + "-" + month_x + "-" + day_x);

                createAplicacao(aplicacao);
            }
        });
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

    public void setDatePickerDialog(){
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        Button btn = (Button) findViewById(R.id.aplicacao_data);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID){
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);

        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

            Button btn = (Button) findViewById(R.id.aplicacao_data);
            btn.setText(day_x + "/" + month_x + "/" + year_x);

            String bd_format_date = year_x + "-" + month_x + "-" + day_x;

            aplicacao.setData(bd_format_date);

            Toast.makeText(AplicacaoPersistActivity.this, day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_SHORT).show();
        }
    };

    private class AplicacaoPersistTask extends AsyncTask<Object, Void, Aplicacao > {

        @Override
        protected Aplicacao doInBackground(Object... params) {
            Cardeneta cardeneta = new Cardeneta();

            Aplicacao aplicacao = (Aplicacao) params[0];
            User user = (User) params[1];

            cardeneta.set_id(CURRENT_CARD);
            try {

                cardeneta.createAplicacao(user.getRequestHeaders(), aplicacao);

            }catch(Exception e){
                System.out.println("ERRO AO SALVAR APLICAÇÃO: " + e);
            }
            return null;
        }
    }
}
