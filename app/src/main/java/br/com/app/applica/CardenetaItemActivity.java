package br.com.app.applica;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.adapter.AplicacaoAdapter;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * Created by felipe on 30/10/16.
 */
public class CardenetaItemActivity extends AppCompatActivity{

    public static String CURRENT_CARD = "";
    private Cardeneta cardeneta;
    private RecyclerView.Adapter mAdapter;

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

        CardenetaRequestTask task = new CardenetaRequestTask();
        try {
            task.execute();
            task.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){

        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.aplicacao_recycler);

        mAdapter = new AplicacaoAdapter(cardeneta.getAplicacoes());
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
    }

    private class CardenetaRequestTask extends AsyncTask<Void, Void, Cardeneta> {

        @Override
        protected Cardeneta doInBackground(Void... params){
            try{
                User user = new User();
                if(user.getAuthToken() == null) {

                    FileInputStream fis = getApplicationContext().openFileInput("userData");

                    user.readUserDataLocally(fis);
                }

                cardeneta = Cardeneta.load(user.getRequestHeaders(), CURRENT_CARD);
                cardeneta.loadAplicacoes(user.getRequestHeaders());
                ///
                return cardeneta;
            }catch(Exception e){
                System.out.println("Error: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Cardeneta cardeneta){
            TextView nome = (TextView) findViewById(R.id.cardeneta_view_nome);
            nome.setText(cardeneta.getNome());

            TextView sobrenome = (TextView) findViewById(R.id.cardeneta_view_sobrenome);
            sobrenome.setText(cardeneta.get_id());

            TextView sexo = (TextView) findViewById(R.id.cardeneta_view_sexo);
            sexo.setText(cardeneta.getSexo());

        }
    }
}
