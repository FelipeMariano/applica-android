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

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Aplicacao;

/**
 * A simple {@link Fragment} subclass.
 */
public class AplicacaoFormFragment extends Fragment {
    MainNavActivity navActivity;
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

        navActivity.toggleFab("HIDE", null);

        if(CURRENT_APLICACAO_ID != null)
            System.out.println("VAI FAZER LOAD PRA EDITAR!");
        else
            CURRENT_APLICACAO = new Aplicacao();

        showDatePickerDialog(formAplicacaoView);
        setDoseSpinner(formAplicacaoView);
        setVacinaSpinner(formAplicacaoView);
        setSaveButtonAction(formAplicacaoView);

        return formAplicacaoView;
    }

    private void setDoseSpinner(View view){
        Spinner dose_spinner = (Spinner) view.findViewById(R.id.aplicacao_dose);

        dose_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CURRENT_APLICACAO.setDose((String) parent.getItemAtPosition(position));
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
            }
        });
    }

    private void setDadosAplicacao(View view){
        TextView data = (TextView) view.findViewById(R.id.aplicacao_data);

        CURRENT_APLICACAO.setData(data.getText().toString());

        System.out.println(CURRENT_APLICACAO.getData() + " - " + CURRENT_APLICACAO.getDose());

    }

    private void saveAplicacao(){

    }

    private void showDatePickerDialog(final View view){

        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);

        Button btnDataAplic = (Button) view.findViewById(R.id.aplicacao_data);

        btnDataAplic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                DialogFragment newFragment = new SelectedDateFragment();
                newFragment.show(navActivity.getFragmentManager(), "DatePicker");
            }
        });
    }

    private class AplicacaoSaveTask extends AsyncTask<Void, Void, Aplicacao> {

        @Override
        protected Aplicacao doInBackground(Void... params) {

            String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/cardenetas/" + navActivity.CURRENT_USER.getId();
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
                _map.put("efetivada", false);
                _map.put("local", "");

                mapper.writeValue(_writer, _map);
            }catch(Exception e){
                System.out.println("ERRO AO MAPEAR PERSIST APLICAÇÃO" + e);
            }


            HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);
            ResponseEntity<Aplicacao> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Aplicacao.class);

            CURRENT_APLICACAO = result.getBody();

            return CURRENT_APLICACAO;
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
            Button btn = (Button) navActivity.findViewById(R.id.aplicacao_data);
            btn.setText(day_x + "/" + month_x + "/" + year_x);

            String bd_format_date = year_x + "-" + month_x + "-" + day_x;

            CURRENT_APLICACAO.setData(bd_format_date);
        }
    }

}
