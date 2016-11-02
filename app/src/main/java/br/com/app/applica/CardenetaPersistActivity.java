package br.com.app.applica;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * Created by felipe on 30/10/16.
 */
public class CardenetaPersistActivity extends AppCompatActivity {
    public static Cardeneta cardeneta;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;
    public static String CURRENT_CARDENETA = "";

    public void createCardeneta(Cardeneta cardeneta) {
        Intent intent = getIntent();
        try {
            FileInputStream fis = getApplicationContext().openFileInput("userData");
            User user = new User();
            user.readUserDataLocally(fis);

            CardenetaPersistTask persistTask = new CardenetaPersistTask();

            persistTask.execute(cardeneta, user);
            persistTask.get(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {

        }
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        cardeneta  = new Cardeneta();

        setContentView(R.layout.cardeneta_new);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_back);
        toolbar.setTitle("Nova cardeneta");

        setSupportActionBar(toolbar);

        setSaveButton();
        showDatePickerDialog();
        setSexoSpinner();

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

    public void setSaveButton(){

        Button btnSave = (Button) findViewById(R.id.btn_add_cardeneta);
        btnSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                TextView nome = (TextView) findViewById(R.id.txt_cardeneta_nome);
                TextView sobrenome = (TextView) findViewById(R.id.txt_cardeneta_sobrenome);


                cardeneta.setNome(nome.getText().toString());
                cardeneta.setSobrenome(sobrenome.getText().toString());

                createCardeneta(cardeneta);
            }
        });
    }

    /////////////

    public void setSexoSpinner(){
        Spinner sexo_spinner = (Spinner) findViewById(R.id.cardeneta_sexo);

        sexo_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cardeneta.setSexo((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexo_spinner.setAdapter(adapter);
    }

    /////////////

    public void showDatePickerDialog(){

        //Sets todays as default day!
        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        Button btn = (Button) findViewById(R.id.cardeneta_data_nasc);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_ID);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id == DIALOG_ID)
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);

        return null;
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;

            Button btn = (Button) findViewById(R.id.cardeneta_data_nasc);
            btn.setText(day_x + "/" + month_x + "/" + year_x);

            String bd_format_date = year_x + "-" + month_x + "-" + day_x;

            cardeneta.setDt_nasc(bd_format_date);

            Toast.makeText(CardenetaPersistActivity.this, day_x + "/" + month_x + "/" + year_x, Toast.LENGTH_SHORT).show();
        }
    };
    /////////////

    private class CardenetaPersistTask extends AsyncTask<Object, Void, Cardeneta> {

        @Override
        protected Cardeneta doInBackground(Object... params) {
            Cardeneta card = (Cardeneta) params[0];
            User user = (User) params[1];
            System.out.println(user.getAuthToken());
            user.createCardeneta(card);
            return null;
        }

        @Override
        protected void onPostExecute(Cardeneta cardeneta){

            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.cardeneta_recycler);

        }
    }
}
