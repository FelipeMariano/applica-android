package br.com.app.applica;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardeneta_view);

        Intent intent = getIntent();
        CURRENT_CARD = intent.getStringExtra(CardenetaItemActivity.CURRENT_CARD);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_back);

        setSupportActionBar(toolbar);

        FloatingActionButton fab_aplicacao = (FloatingActionButton) findViewById(R.id.fab_new_aplicacao);
        fab_aplicacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AplicacaoPersistActivity.class);
                intent.putExtra(AplicacaoPersistActivity.CURRENT_CARD, cardeneta.get_id());
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        setRecyclerView(recyclerView);

        ((AplicacaoAdapter) mAdapter).setOnItemClickListener(new AplicacaoAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                System.out.println("Click disabled!");
            }
        });

        getSupportActionBar().setTitle(cardeneta.getNome() + " " + cardeneta.getSobrenome());

    }

    public void setRecyclerView(RecyclerView recyclerView){
        mAdapter = new AplicacaoAdapter(cardeneta.getListaAplicacoes());
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
    }

    private class CardenetaRequestTask extends AsyncTask<Void, Void, Cardeneta> {

        @Override
        protected Cardeneta doInBackground(Void... params){
            try{
                user = new User();
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
            nome.setText("");

            TextView sobrenome = (TextView) findViewById(R.id.cardeneta_view_sobrenome);
            sobrenome.setText("");

            TextView sexo = (TextView) findViewById(R.id.cardeneta_view_sexo);
            sexo.setText("");

        }
    }
}
