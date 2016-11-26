package br.com.app.applica.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.com.app.applica.R;

public class NotificationActivity extends AppCompatActivity {

    private String vacina;
    private String data;
    private String dose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent intent = getIntent();
        vacina = intent.getStringExtra("vacina");
        data = intent.getStringExtra("data");
        dose = intent.getStringExtra("dose");

        setValues();

        Button btnClose = (Button) findViewById(R.id.btn_close);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setValues(){
        TextView vacina = (TextView) findViewById(R.id.vacina);
        TextView data = (TextView) findViewById(R.id.data);
        TextView dose = (TextView) findViewById(R.id.dose);

        vacina.setText(this.vacina);
        data.setText(this.data);
        dose.setText(this.dose);
    }
}
