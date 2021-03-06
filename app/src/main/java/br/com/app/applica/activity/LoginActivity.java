package br.com.app.applica.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Auth;
import br.com.app.applica.entitity.User;

public class LoginActivity extends AppCompatActivity {
    public static String LOGIN_ERROR;
    private User user;
    String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        baseUrl = getResources().getString(R.string.base_url);

        user = new User();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);


        TextView error = (TextView) findViewById(R.id.login_error);
        if(LOGIN_ERROR != null){
            error.setText(LOGIN_ERROR);
        }else{
            error.setText("");
        }


        Button buttonLogin = (Button) findViewById(R.id.btn_signin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFields();
                login();
            }
        });

        Button buttonSignup = (Button) findViewById(R.id.btn_signup);
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void setFields(){
        EditText email = (EditText) findViewById(R.id.login_email);
        EditText password = (EditText) findViewById(R.id.login_password);

        user.setEmail(email.getText().toString().toLowerCase());
        user.setPassword(password.getText().toString().toLowerCase());
    }

    private void signUp(){
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    private void login() {
        final UserLoginTask loginUser = new UserLoginTask();

        final ProgressDialog progress = ProgressDialog.show(this, "", "Carregando...", true);

        new Thread(new Runnable() {
            @Override
            public void run() {


                try {
                    loginUser.execute();
                    user = loginUser.get(5000, TimeUnit.MILLISECONDS);

                    FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "userData.xml"));
                    FileOutputStream fileos = openFileOutput("userData", Context.MODE_PRIVATE);

                    storageUserData(fos, fileos, user);

                } catch (Exception e) {
                    System.out.println("ERRO: " + e);
                } finally {
                    Intent intent = new Intent(LoginActivity.this, MainNavActivity.class);
                    intent.putExtra("id", user.getId());

                    intent.putExtra("password", user.getPassword());
                    intent.putExtra("x-access-token", user.getAuthToken());
                    startActivity(intent);
                    finish();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                    }
                });
            }
        }).start();


    }

    public static void storageUserData(FileOutputStream fos, FileOutputStream fileos, User user){
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "userData");

            xmlSerializer.startTag(null, "id");
            xmlSerializer.text(user.getId());
            xmlSerializer.endTag(null, "id");

            xmlSerializer.startTag(null, "email");
            xmlSerializer.text(user.getEmail());
            xmlSerializer.endTag(null, "email");

            xmlSerializer.startTag(null, "password");
            xmlSerializer.text(user.getPassword());
            xmlSerializer.endTag(null, "password");

            xmlSerializer.startTag(null, "x-access-token");
            xmlSerializer.text(user.getAuthToken());
            xmlSerializer.endTag(null, "x-access-token");

            xmlSerializer.endTag(null, "userData");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        }catch(Exception e){
            System.out.println("----> ERROR TO SAVE USER: " + e);
        }
        System.out.println("USERte SAVED: " + user.getId());

    }

    private class UserLoginTask extends AsyncTask<Void, Void, User> {
        //FAVOR MOVER ISTO PARA ACTIVITY DE LOGIN!!!!
        @Override
        protected User doInBackground(Void... params){
            try{
                //User loggedUser = new User();
                String url = baseUrl + "/authenticate";

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
                ResponseEntity<Auth> result;
                try {

                    HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                    result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);
                    System.out.println("--> " + result.getStatusCode());
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
