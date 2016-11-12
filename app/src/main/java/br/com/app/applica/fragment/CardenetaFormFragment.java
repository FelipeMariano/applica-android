package br.com.app.applica.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Cardeneta;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardenetaFormFragment extends Fragment {
    MainNavActivity navActivity;
    public static String CURRENT_CARD_ID = null;
    public static Cardeneta CURRENT_CARD;
    public String AUTH_TOKEN;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    public CardenetaFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        navActivity = (MainNavActivity) getActivity();

        navActivity.toggleFab("HIDE", null);
        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();


        if(CURRENT_CARD_ID == null)
            CURRENT_CARD = new Cardeneta();
        else
            loadDadosCardeneta();

        View rootView = inflater.inflate(R.layout.fragment_cardeneta_form, container, false);

        showDatePickerDialog(rootView);
        setSexoSpinner(rootView);
        setSaveButtonAction(rootView);



        return rootView;
    }

    private void loadDadosCardeneta(){

        CardenetaDadosTask loadCardDados = new CardenetaDadosTask();

        try{
            loadCardDados.execute();
            loadCardDados.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            System.out.println("ERRO AO CHAMAR TASK PARA LOAD CARD: " + e);
        }

        String title_name = CURRENT_CARD.getNome() + " " + CURRENT_CARD.getSobrenome();
        navActivity.getSupportActionBar().setTitle(title_name);
    }

    private void saveCardeneta(){
        CardenetaSaveTask saveCard = new CardenetaSaveTask();
        if (CURRENT_CARD_ID == null){
           try{
               saveCard.execute();
               saveCard.get(5000, TimeUnit.MILLISECONDS);
               CURRENT_CARD_ID = CURRENT_CARD.get_id();
               System.out.println("SUCCESSFULLY SAVED: " + CURRENT_CARD_ID);
            }catch(Exception e){

            }
        }

    }

    private void setDadosCardeneta(View view){
        TextView nome = (TextView) view.findViewById(R.id.txt_cardeneta_nome);
        TextView sobrenome = (TextView) view.findViewById(R.id.txt_cardeneta_sobrenome);

        CURRENT_CARD.setNome(nome.getText().toString());
        CURRENT_CARD.setSobrenome(sobrenome.getText().toString());
    }

    private void setSexoSpinner(View view){
        Spinner sexoSpinner = (Spinner) view.findViewById(R.id.cardeneta_sexo);
        sexoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CURRENT_CARD.setSexo((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.sexo_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(adapter);
    }

    private void showDatePickerDialog(final View view){

        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        Button btnDataNasc = (Button) view.findViewById(R.id.cardeneta_data_nasc);

        btnDataNasc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogFragment newFragment = new SelectedDateFragment();
                newFragment.show(navActivity.getFragmentManager(), "DatePicker");
            }
        });
    }

    private void setSaveButtonAction(final View view){
        Button saveButton = (Button) view.findViewById(R.id.btn_save_cardeneta);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDadosCardeneta(view);
                saveCardeneta();
                navActivity.getSupportFragmentManager().popBackStack();
            }
        });
    }

    private class CardenetaDadosTask extends AsyncTask<Void, Void, Cardeneta> {
        private Cardeneta loadedCardeneta;

        private void loadData(HttpHeaders requestHeaders, RestTemplate restTemplate){
            try {

                String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/cardenetas/" + CURRENT_CARD_ID;

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Cardeneta.class);
                loadedCardeneta = (Cardeneta) result.getBody();
            }catch(Exception e){
                System.out.println("ERRO AO LOAD CARDENETA: " + e);
                loadedCardeneta = new Cardeneta();
            }
        }

        @Override
        protected Cardeneta doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            loadData(requestHeaders, restTemplate);

            //ResponseEntity<List<Cardeneta>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Cardeneta>>() {
            //});
            CURRENT_CARD = loadedCardeneta;
            return CURRENT_CARD;
        }
    }

    private class CardenetaSaveTask extends AsyncTask<Void, Void, Cardeneta> {

        @Override
        protected Cardeneta doInBackground(Void... params) {
            String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + navActivity.CURRENT_USER.getId();
            url += "/cardenetas";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
            _map.put("nome", CURRENT_CARD.getNome());
            _map.put("sobrenome", CURRENT_CARD.getSobrenome());
            _map.put("sexo", CURRENT_CARD.getSexo());
            _map.put("dt_nasc", CURRENT_CARD.getDt_nasc());

            StringWriter _writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();

            try {
                mapper.writeValue(_writer, _map);
            }catch(Exception e){

            }


            HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);
            ResponseEntity<Cardeneta> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Cardeneta.class);

            CURRENT_CARD = result.getBody();

            return CURRENT_CARD;
        }
    }


    public class SelectedDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){
            final Calendar calendar = Calendar.getInstance();

            year_x = calendar.get(Calendar.YEAR);
            month_x = calendar.get(Calendar.MONTH);
            day_x = calendar.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year_x, month_x, day_x);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Button btn = (Button) navActivity.findViewById(R.id.cardeneta_data_nasc);

            day_x = dayOfMonth;
            month_x = month + 1;
            year_x = year;

            btn.setText(day_x + "/" + month_x + "/" + year_x);

            String bd_format_date = year_x + "-" + month_x + "-" + day_x;

            CURRENT_CARD.setDt_nasc(bd_format_date);
        }
    }

}