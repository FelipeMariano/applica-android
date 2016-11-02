package br.com.app.applica;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;

/**
 * Created by felipe on 30/10/16.
 */
public class AplicacaoActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;
    private static String LOG_TAG = "AplicacaoActivity";

    @Override
    public void onCreate(Bundle savedInstanceStante){
        super.onCreate(savedInstanceStante);
        setContentView(R.layout.cardeneta_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
