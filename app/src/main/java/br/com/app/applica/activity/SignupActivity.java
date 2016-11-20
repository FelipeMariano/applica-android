package br.com.app.applica.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Auth;
import br.com.app.applica.entitity.User;

public class SignupActivity extends AppCompatActivity {
    private User USER;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        USER = new User();
        setSexoSpinner();

        baseUrl = getResources().getString(R.string.base_url);

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
        if(setAndValidateFormInfos()) {
            SignupTask signupTask = new SignupTask();

            try {
                signupTask.execute();
                signupTask.get(5000, TimeUnit.MILLISECONDS);
                Toast.makeText(this, "Usuário criado", Toast.LENGTH_SHORT).show();

                FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "userData.xml"));
                FileOutputStream fileos = openFileOutput("userData", Context.MODE_PRIVATE);

                LoginActivity.storageUserData(fos, fileos, USER);

                Intent intent = new Intent(SignupActivity.this, MainNavActivity.class);
                intent.putExtra("id", USER.getId());
                intent.putExtra("x-access-token", USER.getAuthToken());
                startActivity(intent);
                finish();
            }catch (Exception e){
                System.out.println("Erro ao cadastrar usuário: " + e);
            }
        }else
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

    private class SignupTask extends AsyncTask<Void, Void, User>{

        @Override
        protected User doInBackground(Void... params) {
            String url = baseUrl + "/api/signUp";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);

            LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
            _map.put("nome", USER.getNome());
            _map.put("sobrenome", USER.getSobrenome());
            _map.put("sexo", USER.getSexo());
            _map.put("dt_nasc", USER.getDt_nasc());
            _map.put("email", USER.getEmail());
            _map.put("password", USER.getPassword());

            StringWriter _writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();

            try {
                mapper.writeValue(_writer, _map);
            }catch(Exception e){

            }

            try {

                HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);

                if (result.getStatusCode().toString().equals("307")){
                    url = baseUrl + "/authenticate";
                    ResponseEntity<Auth> authResult = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);

                    Auth user = authResult.getBody();
                    USER.setId(user.getUser_id());
                    USER.setAuthToken(user.getToken());
                }

            }catch(Exception e){
                System.out.println("FAILS TO VALIDATE USER: " + e);
                USER = new User();
            }finally{
                return USER;
            }
        }
    }
}
