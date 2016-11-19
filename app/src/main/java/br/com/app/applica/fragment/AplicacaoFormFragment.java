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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

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
import br.com.app.applica.entitity.Aplicacao;

/**
 * A simple {@link Fragment} subclass.
 */
public class AplicacaoFormFragment extends Fragment {
    public static MainNavActivity navActivity;
    public static String CURRENT_CARD_ID = null;
    public static String CURRENT_APLICACAO_ID = null;
    public static Aplicacao CURRENT_APLICACAO;
    public String AUTH_TOKEN;
    int year_x, month_x, day_x;


    public AplicacaoFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View formAplicacaoView = inflater.inflate(R.layout.fragment_aplicacao_form, container, false);

        navActivity = (MainNavActivity) getActivity();
        final Calendar calendar = Calendar.getInstance();

        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        Bundle bundle = this.getArguments();

        if(bundle != null) {
            CURRENT_APLICACAO_ID = bundle.getString("aplicacao_id");
            CURRENT_CARD_ID = bundle.getString("card_id");
        }

        System.out.println("---> " + CURRENT_APLICACAO_ID);
        System.out.println("---> " + CURRENT_CARD_ID);


        navActivity.toggleFab("HIDE", null);
        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();

        showDatePickerDialog(formAplicacaoView);
        setDoseSpinner(formAplicacaoView);
        setVacinaSpinner(formAplicacaoView);
        setSaveButtonAction(formAplicacaoView);

        if(CURRENT_APLICACAO_ID != null)
            CURRENT_APLICACAO = loadAplicacao(formAplicacaoView);
        else
            CURRENT_APLICACAO = new Aplicacao();

        return formAplicacaoView;
    }

    private Aplicacao loadAplicacao(View view){

        AplicacaoLoadTask aplicacaoLoad = new AplicacaoLoadTask();
        Aplicacao loadedAplicacao = new Aplicacao();
        try{
            aplicacaoLoad.execute();
            loadedAplicacao = aplicacaoLoad.get(5000, TimeUnit.MILLISECONDS);
            setLoadedAplicacao(loadedAplicacao, view);
            return loadedAplicacao;
        }catch(Exception e){
            System.out.println("ERRO AO CARREGAR APLICACAO PARA EDIÇÃO: " + e);
        }

        System.out.println(loadedAplicacao.getData());

        return new Aplicacao();
    }

    private void setLoadedAplicacao(Aplicacao aplicacao, View view){

        if(!aplicacao.getData().equals(null)){
           String data = aplicacao.getFormattedData();
           Button dataButton = (Button) view.findViewById(R.id.aplicacao_data);
           dataButton.setText(data);
           day_x = Integer.parseInt(data.substring(0,2));
            month_x = Integer.parseInt(data.substring(3,5));
            year_x = Integer.parseInt(data.substring(6, 10));

        }

        if(!aplicacao.getDose().equals(null)){
            Spinner dose_spinner = (Spinner) view.findViewById(R.id.aplicacao_dose);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.dose_arrays,
                    android.R.layout.simple_spinner_item);
            //int itemPosition = adapter.getPosition(aplicacao.getDose().toString());

            dose_spinner.setSelection(aplicacao.getDose());
        }

        if(!aplicacao.getVacina().equals(null)){
            Spinner vacina_spinner = (Spinner) view.findViewById(R.id.aplicacao_vacina);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.vacina_arrays,
                    android.R.layout.simple_spinner_item);
            int itemPosition = adapter.getPosition(aplicacao.getVacina());

            vacina_spinner.setSelection(itemPosition);
        }

        if(aplicacao.getEfetivada() != null && aplicacao.getEfetivada()){
            CheckBox isEfetivada = (CheckBox) view.findViewById(R.id.aplicacao_efetivada);
            isEfetivada.setChecked(true);
        }
    }

    private void setDoseSpinner(View view){
        Spinner dose_spinner = (Spinner) view.findViewById(R.id.aplicacao_dose);

        dose_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CURRENT_APLICACAO.setDose(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.dose_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dose_spinner.setAdapter(adapter);
    }

    private void setVacinaSpinner(View view) {
        Spinner vacina_spinner = (Spinner) view.findViewById(R.id.aplicacao_vacina);

        vacina_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CURRENT_APLICACAO.setVacina((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.vacina_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vacina_spinner.setAdapter(adapter);
    }

    private void setSaveButtonAction(final View view){
        Button saveButton = (Button) view.findViewById(R.id.btn_save_aplicacao);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDadosAplicacao(view);
                saveAplicacao();
                navActivity.getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setDadosAplicacao(View view){
        Spinner dose = (Spinner) view.findViewById(R.id.aplicacao_dose);
        Spinner vacina = (Spinner) view.findViewById(R.id.aplicacao_vacina);
        CheckBox isEfetivada = (CheckBox) view.findViewById(R.id.aplicacao_efetivada);
        String data = year_x + "-" + month_x + "-" + day_x;

        CURRENT_APLICACAO.setDose(dose.getSelectedItemPosition());
        CURRENT_APLICACAO.setVacina(vacina.getSelectedItem().toString());
        CURRENT_APLICACAO.setEfetivada(isEfetivada.isChecked());
        CURRENT_APLICACAO.setData(data);

    }

    private void saveAplicacao(){
        AplicacaoSaveTask saveAplicacao = new AplicacaoSaveTask();
        //if (CURRENT_APLICACAO_ID == null)
            try{
                saveAplicacao.execute();
                saveAplicacao.get(5000, TimeUnit.MILLISECONDS);
                CURRENT_APLICACAO_ID = CURRENT_APLICACAO.get_id();
                System.out.println("APLICACAO SAVED SUCCESSFULLY: "+ CURRENT_APLICACAO_ID);
            }catch(Exception e){

            }
    }

    private void showDatePickerDialog(final View view){

        final Calendar calendar = Calendar.getInstance();


        Button btnDataAplic = (Button) view.findViewById(R.id.aplicacao_data);

        btnDataAplic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogFragment newFragment = new SelectedDateFragment();
                newFragment.show(navActivity.getFragmentManager(), "DatePicker");
            }
        });
    }

    private class AplicacaoLoadTask extends AsyncTask<Void, Void, Aplicacao>{

        @Override
        protected Aplicacao doInBackground(Void... params) {
            String url = navActivity.BASE_URL + "/api/aplicacoes/" + CURRENT_APLICACAO_ID;

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
            ResponseEntity<Aplicacao> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Aplicacao.class);

            CURRENT_APLICACAO = result.getBody();

            return CURRENT_APLICACAO;
        }
    }

    private class AplicacaoSaveTask extends AsyncTask<Void, Void, Aplicacao> {

        @Override
        protected Aplicacao doInBackground(Void... params) {

            String url = navActivity.BASE_URL + "/api/cardenetas/" + CURRENT_CARD_ID;
            url += "/aplicacoes";


            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);



            StringWriter _writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();


            try {

                LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
                _map.put("data", CURRENT_APLICACAO.getData());
                _map.put("vacina", CURRENT_APLICACAO.getVacina());
                _map.put("dose", CURRENT_APLICACAO.getDose());
                _map.put("efetivada", CURRENT_APLICACAO.getEfetivada());
                _map.put("local", "local");

                mapper.writeValue(_writer, _map);
            }catch(Exception e){
                System.out.println("ERRO AO MAPEAR PERSIST APLICAÇÃO" + e);
            }


            HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);
            ResponseEntity<Aplicacao> result;
            if(CURRENT_APLICACAO_ID != null){
                System.out.println("THIS MUST EDIT!");
                url = navActivity.BASE_URL + "/api/aplicacoes/" + CURRENT_APLICACAO_ID;
                result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Aplicacao.class);
            }else {
                result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Aplicacao.class);
            }

            CURRENT_APLICACAO = result.getBody();

            return CURRENT_APLICACAO;
        }
    }

    public class SelectedDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState){


            return new DatePickerDialog(getActivity(), this, year_x, month_x, day_x);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Button btn = (Button) navActivity.findViewById(R.id.aplicacao_data);

            day_x = dayOfMonth;
            month_x = month + 1;
            year_x = year;

            btn.setText(day_x + "/" + month_x + "/" + year_x);

            String bd_format_date = year_x + "-" + month_x + "-" + day_x;

            CURRENT_APLICACAO.setData(bd_format_date);
        }
    }

}
