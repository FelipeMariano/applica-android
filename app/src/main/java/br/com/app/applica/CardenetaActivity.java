package br.com.app.applica;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.io.FileInputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.adapter.CardenetaAdapter;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * Created by felipe on 30/10/16.
 */
public class CardenetaActivity extends AppCompatActivity {

    private RecyclerView.Adapter mAdapter;
    private static String LOG_TAG = "CardenetaActivity";

    private User user;
    List<Cardeneta> cardenetas;

    private void setRecyclerLayout(RecyclerView recyclerView){
        mAdapter = new CardenetaAdapter(user.getListaCardenetas());
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardeneta_recycler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_crop);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CardenetaPersistActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume(){
        super.onResume();


        UserRequestTask task = new UserRequestTask();
        try {
            task.execute();
            task.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){

        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardeneta_recycler);
        setRecyclerLayout(recyclerView);

        ((CardenetaAdapter) mAdapter).setOnItemClickListener(new CardenetaAdapter.MyClickListener(){
            @Override
            public void onItemClick(int position, View v){
                Log.i(LOG_TAG, " clicked on Item " + position);
                CardenetaAdapter.CardenetaViewHolder vHolder = (CardenetaAdapter.CardenetaViewHolder) v.getTag();
                showCardeneta(vHolder.getId());
            }
        });


    }

    @Override
    public void onStart(){
        super.onStart();
        getSupportActionBar().setTitle("Minhas cardenetas");

    }

    private void showCardeneta(String card_id){
        Intent intent = new Intent(this, CardenetaItemActivity.class);

        intent.putExtra(br.com.app.applica.CardenetaItemActivity.CURRENT_CARD, card_id);

        startActivity(intent);

    }


    private class UserRequestTask extends AsyncTask<Void, Void, User> {
        private void loadUserInfos(){
            user = new User();
            try {
                FileInputStream fis = getApplicationContext().openFileInput("userData");

                user.readUserDataLocally(fis);
            }catch(Exception e){
                System.out.println("ERRO DE LEITURA: " + e);
            }
        }

        @Override
        protected User doInBackground(Void... params){
            try{
                loadUserInfos();

                user.loadCardenetas();
                System.out.println("Cardenetas: " + user.getListaCardenetas());
                ///
                return user;
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
