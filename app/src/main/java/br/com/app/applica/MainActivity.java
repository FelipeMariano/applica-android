package br.com.app.applica;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import br.com.app.applica.entitity.User;

public class MainActivity extends AppCompatActivity {

    public final static String MESSAGE = "I AM HERE FIRST!";

    public void userLogin(View view){
        System.out.println("BUTTON PRESSED!");
        //new LoginRequestTask().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onStart(){
    super.onStart();

        try {
            FileInputStream fis = getApplicationContext().openFileInput("userData");
            User user = new User();
            user.readUserDataLocally(fis);;
            if(user.getId() != null) {
                Intent intent = new Intent(this, CardenetaActivity.class);

                startActivity(intent);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LoginRequestTask extends AsyncTask<Void, Void, User> {

        private User user;

        private void loadUserInfos(){
            TextView email = (TextView) findViewById(R.id.txt_email);
            TextView password = (TextView) findViewById(R.id.txt_pass);

            //user = new User(email.getText().toString().trim(), password.getText().toString().trim());
            user = new User("felipe@gmail.com", "223333330");
        }

        @Override
        protected User doInBackground(Void... params){
            try{
                loadUserInfos();

                System.out.println(user.getEmail());
                System.out.println(user.getPassword());

                this.user.authenticateAndGetToken();
                this.user.load();

                this.user.loadCardenetas();

                FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "userData.xml"));
                FileOutputStream fileos = openFileOutput("userData", Context.MODE_PRIVATE);

                this.user.writeUserDataLocally(fos, fileos);


                FileInputStream fis = getApplicationContext().openFileInput("userData");

                this.user.readUserDataLocally(fis);

                ///
                return user;
            }catch(Exception e){
                System.out.println("Error: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user){
            TextView id = (TextView) findViewById(R.id.id_value);
            TextView content = (TextView) findViewById(R.id.content_value);
        //    id.setText(user.getId());
        //    content.setText(user.getEmail());
        //    EditText txt_email = (EditText) findViewById(R.id.txt_email);
        //    txt_email.setText(user.getEmail());
        }
    }
}
