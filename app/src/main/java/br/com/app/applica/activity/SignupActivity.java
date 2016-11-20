package br.com.app.applica.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import br.com.app.applica.R;
import br.com.app.applica.entitity.User;

public class SignupActivity extends AppCompatActivity {
    private User USER;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        USER = new User();
        setSexoSpinner();
    }

    private void setSexoSpinner(){
        Spinner sexoSpinner = (Spinner) findViewById(R.id.signup_sexo);
        sexoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 String sexo = (String) parent.getItemAtPosition(position);
                USER.setSexo(sexo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sexo_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(adapter);
    }
}
