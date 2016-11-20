package br.com.app.applica.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

        Button btnSendInfos = (Button) findViewById(R.id.signup_btn_send);
        btnSendInfos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private boolean setAndValidateFormInfos(){
        TextView nome = (TextView) findViewById(R.id.signup_nome);
        TextView sobrenome = (TextView) findViewById(R.id.signup_sobrenome);
        Spinner sexo = (Spinner) findViewById(R.id.signup_sexo);
        TextView dtNasc = (TextView) findViewById(R.id.signup_dt_nasc);
        TextView email = (TextView) findViewById(R.id.signup_email);
        TextView password = (TextView) findViewById(R.id.signup_password);
        TextView confirmPassword = (TextView) findViewById(R.id.signup_password_repeat);

        System.out.println(password.getText().toString());
        System.out.println(confirmPassword.getText().toString());
        if(!password.getText().toString().equals(confirmPassword.getText().toString()) && (!password.getText().toString().equals("")))
            return false;

        USER.setPassword(password.getText().toString());
        USER.setEmail(email.getText().toString());
        USER.setDt_nasc(dtNasc.getText().toString());
        USER.setSexo(sexo.getSelectedItem().toString());
        USER.setNome(nome.getText().toString());
        USER.setSobrenome(sobrenome.getText().toString());

        System.out.println(USER.getPassword());
        System.out.println(USER.getEmail());
        System.out.println(USER.getDt_nasc());
        System.out.println(USER.getNome());
        System.out.println(USER.getSobrenome());
        System.out.println(USER.getSexo());

        if(USER.getNome() == null || USER.getSobrenome() == null || USER.getSexo() == null || USER.getEmail() == null || USER.getDt_nasc() == null)
            return false;

        return true;

    }

    private void signUp(){
        if(setAndValidateFormInfos())
            Toast.makeText(this, "Usuário criado", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Informações inválidas", Toast.LENGTH_SHORT).show();
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
