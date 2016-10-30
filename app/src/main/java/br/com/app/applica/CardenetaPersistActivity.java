package br.com.app.applica;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.com.app.applica.entitity.Cardeneta;

/**
 * Created by felipe on 30/10/16.
 */
public class CardenetaPersistActivity extends AppCompatActivity {
    public static Cardeneta CURRENT_CARD = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cardeneta_new);
    }
}
