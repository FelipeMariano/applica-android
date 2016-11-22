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
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
        View rootView = inflater.inflate(R.layout.fragment_cardeneta_form, container, false);

        navActivity = (MainNavActivity) getActivity();

        navActivity.toggleFab("HIDE", null);
        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();


        Bundle bundle = this.getArguments();

        if(bundle != null) {
            CURRENT_CARD_ID = bundle.getString("card_id");
            System.out.println("I WILL EDIT THIS: " + CURRENT_CARD_ID);
        }

        showDatePickerDialog(rootView);
        setSexoSpinner(rootView);
        setSaveButtonAction(rootView);


        if(CURRENT_CARD_ID == null)
            CURRENT_CARD = new Cardeneta();
        else {
            CURRENT_CARD = loadDados(rootView);
        }

        return rootView;
    }

    private Cardeneta loadDados(View view){

        CardenetaLoadTask loadCardDados = new CardenetaLoadTask();
        Cardeneta loadedCardeneta;
        try{
            loadCardDados.execute();
            loadedCardeneta = loadCardDados.get(5000, TimeUnit.MILLISECONDS);
            setLoadedCardeneta(loadedCardeneta, view);
            return loadedCardeneta;
        }catch(Exception e){
            System.out.println("ERRO AO CHAMAR TASK PARA LOAD CARD: " + e);
        }

        return new Cardeneta();
    }

    private void setLoadedCardeneta(Cardeneta cardeneta, View view){
        if(!cardeneta.getNome().equals(null) && !cardeneta.getSobrenome().equals(null)) {
            String title_name = cardeneta.getNome() + " " + cardeneta.getSobrenome();
            navActivity.getSupportActionBar().setTitle(title_name);

            TextView nome = (TextView) view.findViewById(R.id.txt_cardeneta_nome);
            TextView sobrenome = (TextView) view.findViewById(R.id.txt_cardeneta_sobrenome);

            nome.setText(cardeneta.getNome());
            sobrenome.setText(cardeneta.getSobrenome());
        }

        if(!cardeneta.getSexo().equals(null)) {
            Spinner sexoSpinner = (Spinner) view.findViewById(R.id.cardeneta_sexo);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.sexo_arrays,
                    android.R.layout.simple_spinner_item);


            int itemPosition = adapter.getPosition(cardeneta.getSexo());
            sexoSpinner.setSelection(itemPosition);
        }

        if(!(cardeneta.getDt_nasc() == null)){
            String data = cardeneta.getDt_nasc();
            Button dataButton = (Button) view.findViewById(R.id.cardeneta_data_nasc);
            System.out.println(data);
            day_x = Integer.parseInt(data.substring(8, 10));
            month_x = Integer.parseInt(data.substring(5, 7));
            year_x = Integer.parseInt(data.substring(0, 4));
            dataButton.setText(day_x + "/" + month_x + "/" + year_x);
        }
    }

    private void saveCardeneta(){
        CardenetaSaveTask saveCard = new CardenetaSaveTask();

        //if (CURRENT_CARD_ID == null){
           try{
               saveCard.execute();
               saveCard.get(5000, TimeUnit.MILLISECONDS);
              // CURRENT_CARD_ID = CURRENT_CARD.get_id();
               System.out.println("SUCCESSFULLY SAVED: " + CURRENT_CARD_ID);
            }catch(Exception e){

            }
        //}

    }

    private boolean isValidNasc(){
        Boolean isValid = true;
        TextView errorDtNasc = (TextView) navActivity.findViewById(R.id.error_cardeneta_data_nasc);
        String strDate = day_x + "/" + month_x + "/" + year_x;
        SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy");
        Date selectedDate;

        try {
            selectedDate = format.parse(strDate);
        }catch(Exception e){
            System.out.println("ERRO AO FORMATAR DATA");
            return false;
        }

        if(year_x == 0 || month_x == 0 || day_x == 0 || (new Date().before(selectedDate))){
            isValid = false;
            errorDtNasc.setText("*Data inválida");
        }

        return isValid;
    }

    private boolean isValidSobrenome(String sobrenome){
        Boolean isValid = true;
        TextView errorSobrenome = (TextView) navActivity.findViewById(R.id.error_cardeneta_sobrenome);

        if ((sobrenome == null) || (sobrenome.length() < 1)){
            isValid = false;
            errorSobrenome.setText("*Sobrenome inválido");
        }

        return isValid;
    }

    private boolean isValidNome(String nome){
        Boolean isValid = true;
        TextView errorNome = (TextView) navActivity.findViewById(R.id.error_cardeneta_nome);

        if((nome == null) || (nome.length() < 1)){
            isValid = false;
            errorNome.setText("*Nome inválido");
        }

        return isValid;
    }

    private void setDadosCardeneta(View view){
        TextView nome = (TextView) view.findViewById(R.id.txt_cardeneta_nome);
        TextView sobrenome = (TextView) view.findViewById(R.id.txt_cardeneta_sobrenome);
        Spinner sexoSpinner = (Spinner) view.findViewById(R.id.cardeneta_sexo);
        String data = year_x + "-" + month_x + "-" + day_x;

        CURRENT_CARD.setNome(nome.getText().toString());
        CURRENT_CARD.setSobrenome(sobrenome.getText().toString());
        CURRENT_CARD.setSexo((String) sexoSpinner.getSelectedItem());
        CURRENT_CARD.setDt_nasc(data);
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

                Boolean isValid = true;

                if(!isValidNasc()) isValid = false;

                if(!isValidSobrenome(CURRENT_CARD.getSobrenome())) isValid = false;

                if(!isValidNome(CURRENT_CARD.getNome())) isValid = false;

                if(!isValid){
                    Toast.makeText(navActivity, "Dados inválidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveCardeneta();
                navActivity.getSupportFragmentManager().popBackStack();
            }
        });
    }

    private class CardenetaLoadTask extends AsyncTask<Void, Void, Cardeneta> {
        @Override
        protected Cardeneta doInBackground(Void... params) {
            String url = navActivity.BASE_URL + "/api/cardenetas/" + CURRENT_CARD_ID;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
            ResponseEntity<Cardeneta> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Cardeneta.class);

            CURRENT_CARD = result.getBody();

            return CURRENT_CARD;
        }
    }

    private class CardenetaSaveTask extends AsyncTask<Void, Void, Cardeneta> {

        @Override
        protected Cardeneta doInBackground(Void... params) {
            String url = navActivity.BASE_URL + "/api/users/" + navActivity.CURRENT_USER.getId();
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
            ResponseEntity<Cardeneta> result;
            if(CURRENT_CARD_ID != null) {
                System.out.println("THIS MUST EDIT!");
                url = navActivity.BASE_URL + "/api/cardenetas/" + CURRENT_CARD_ID;
                result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Cardeneta.class);
            }else{
                result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Cardeneta.class);
            }

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
