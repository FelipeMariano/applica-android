package br.com.app.applica;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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
        new LoginRequestTask().execute();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

            }

    @Override
    public void onStart(){
    super.onStart();
        File file = new File(getFilesDir(), "userData");
        if(file.exists())
            file.delete();


        try {
            FileInputStream fis = getApplicationContext().openFileInput("userData");
            User user = new User();
            user.readUserDataLocally(fis);
            if(user.getId() != null) {
                Intent intent = new Intent(this, CardenetaActivity.class);

                startActivity(intent);
            }
        } catch (FileNotFoundException e) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void saveLocally(User user){
        SQLiteDatabase myDB = null;
        String tableName = "user";

        try{
            myDB = this.openOrCreateDatabase("applica", MODE_PRIVATE, null);
            System.out.println("CREATING USER TABLE");
            myDB.execSQL("CREATE TABLE IF NOT EXISTS " + tableName +
                    "_id VARCHAR, email VARCHAR, password VARCHAR, token VARCHAR");
            System.out.println("SAVING USER");

            myDB.execSQL("INSERT INTO " + tableName +
            "VALEUS (" + user.getId() + ", " +
            user.getEmail() + ", " + user.getPassword() +
            ", " + user.getAuthToken() + ")");


            Cursor cursor = myDB.rawQuery("SELECT * FROM " + tableName, null);

            cursor.moveToFirst();

            if(cursor != null){
                System.out.println("USER SAVED LOCALLY");
            }

        }catch(Exception e){
            System.out.println("ERROR TO SAVE");
        }

    }

    private class LoginRequestTask extends AsyncTask<Void, Void, User> {

        private User user;

        private void loadUserInfos(){
            TextView email = (TextView) findViewById(R.id.txt_email);
            TextView password = (TextView) findViewById(R.id.txt_pass);

            String email_str = email.getText().toString();
            email_str = email_str.toLowerCase().trim();

            String pass_str = password.getText().toString();
            pass_str = pass_str.toLowerCase().trim();

            user = new User(email_str, pass_str);
            //user = new User("felipe@gmail.com", "223333330");
        }

        @Override
        protected User doInBackground(Void... params){
            loadUserInfos();

            System.out.println(user.getEmail());
            System.out.println(user.getPassword());

            this.user.authenticateAndGetToken();
            this.user.load();

            this.user.loadCardenetas();
            try{

                FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "userData.xml"));
                FileOutputStream fileos = openFileOutput("userData", Context.MODE_PRIVATE);

                this.user.writeUserDataLocally(fos, fileos);
                //saveLocally(this.user);
                FileInputStream fis = getApplicationContext().openFileInput("userData");

                this.user.readUserDataLocally(fis);

                ///
                return user;
            }catch(Exception e){
                System.out.println("Error ao autenticar usuário: " + e);
                user = new User();
                try {
                    FileOutputStream fos = new FileOutputStream(new File(getFilesDir(), "userData.xml"));
                    FileOutputStream fileos = openFileOutput("userData", Context.MODE_PRIVATE);
                    user.writeUserDataLocally(fos, fileos);
                }catch(Exception error){
                    System.out.println("Erro ao sobreescrever userData: " + error);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user){
            Intent intent = new Intent(MainActivity.this, CardenetaActivity.class);
            startActivity(intent);
        }
    }
}
