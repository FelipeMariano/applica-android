package br.com.app.applica;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

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
}
