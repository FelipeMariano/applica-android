package br.com.app.applica;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.FileInputStream;

import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * Created by felipe on 30/10/16.
 */
public class CardenetaItemActivity extends AppCompatActivity{

    public static String CURRENT_CARD = "";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardeneta_view);

        Intent intent = getIntent();
        CURRENT_CARD = intent.getStringExtra(CardenetaItemActivity.CURRENT_CARD);


    }

    @Override
    public void onStart(){
        super.onStart();
        new CardenetaRequestTask().execute();
    }

    private class CardenetaRequestTask extends AsyncTask<Void, Void, Cardeneta> {
        private Cardeneta cardeneta =  new Cardeneta();

        @Override
        protected Cardeneta doInBackground(Void... params){
            try{
                User user = new User();
                if(user.getAuthToken() == null) {

                    FileInputStream fis = getApplicationContext().openFileInput("userData");

                    user.readUserDataLocally(fis);
                }

                this.cardeneta = Cardeneta.load(user.getRequestHeaders(), CURRENT_CARD);

                ///
                return this.cardeneta;
            }catch(Exception e){
                System.out.println("Error: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cardeneta cardeneta){
            TextView nome = (TextView) findViewById(R.id.cardeneta_view_nome);
            nome.setText(this.cardeneta.getNome());

            TextView sobrenome = (TextView) findViewById(R.id.cardeneta_view_sobrenome);
            sobrenome.setText(this.cardeneta.get_id());
            System.out.println();
            TextView sexo = (TextView) findViewById(R.id.cardeneta_view_sexo);
            sexo.setText(this.cardeneta.getSexo());
        }
    }
}
