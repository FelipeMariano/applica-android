package br.com.app.applica;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardeneta_recycler);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cardeneta_recycler);

        UserRequestTask task = new UserRequestTask();
        try {
            task.execute();
            task.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){

        }
        mAdapter = new CardenetaAdapter(user.getCardenetas());
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


    }

    @Override
    protected void onResume(){
        super.onResume();
        ((CardenetaAdapter) mAdapter).setOnItemClickListener(new CardenetaAdapter.MyClickListener(){
            @Override
            public void onItemClick(int position, View v){
                Log.i(LOG_TAG, " clicked on Item " + position);
                CardenetaAdapter.CardenetaViewHolder vHolder = (CardenetaAdapter.CardenetaViewHolder) v.getTag();
                showCardeneta(vHolder.getId());
            }
        });
    }

    public void editCardeneta(View v){
        Intent intent = new Intent(this, CardenetaPersistActivity.class);
        Cardeneta card = new Cardeneta();


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
                System.out.println("Cardenetas: " + user.getCardenetas());
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
