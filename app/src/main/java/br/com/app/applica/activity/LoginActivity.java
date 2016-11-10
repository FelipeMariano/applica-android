package br.com.app.applica.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Auth;
import br.com.app.applica.entitity.User;

public class LoginActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = new User();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);

        Button buttonLogin = (Button) findViewById(R.id.btn_signin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFields();
                login();
            }
        });
    }

    private void setFields(){
        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);

        user.setEmail(email.getText().toString().toLowerCase());
        user.setPassword(password.getText().toString().toLowerCase());
    }

    private void login() {
        final UserLoginTask loginUser = new UserLoginTask();

        try {
            loginUser.execute();
            user = loginUser.get(5000, TimeUnit.MILLISECONDS);
            System.out.println("---> " + user.getAuthToken());
        } catch (Exception e) {
            System.out.println("ERRO: " + e);
        } finally {
             Intent intent = new Intent(LoginActivity.this, MainNavActivity.class);
            intent.putExtra("id", user.getId());
            intent.putExtra("x-access-token", user.getAuthToken());
            startActivity(intent);
            finish();
        }

    }

    private class UserLoginTask extends AsyncTask<Void, Void, User> {
        //FAVOR MOVER ISTO PARA ACTIVITY DE LOGIN!!!!
        @Override
        protected User doInBackground(Void... params){
            try{
                //User loggedUser = new User();
                String url = "http://applica-ihc.44fs.preview.openshiftapps.com/authenticate";

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders requestHeaders = new HttpHeaders();

                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                //requestHeaders.add("x-access-token", getAuthToken());

                LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
                _map.put("email", user.getEmail());
                _map.put("password", user.getPassword());

                StringWriter _writer = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();

                try {
                    mapper.writeValue(_writer, _map);
                }catch(Exception e){

                }

                try {

                    HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                    ResponseEntity<Auth> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);

                    Auth body = result.getBody();

                    user.setId(body.getUser_id());
                    user.setAuthToken(body.getToken());

                }catch(Exception e){
                    System.out.println("FAILS TO VALIDATE USER: " + e);
                    return new User();
                }finally{
                    return user;
                }

            }catch(Exception e){
                System.out.println("Error: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user){
            System.out.println("USER LOADED");
        }
    }
}
