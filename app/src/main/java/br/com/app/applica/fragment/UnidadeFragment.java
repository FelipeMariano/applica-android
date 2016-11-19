package br.com.app.applica.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Unidade;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnidadeFragment extends Fragment {
    public static MainNavActivity navActivity;
    private Unidade CURRENT_UNIDADE;
    private String CURRENT_UNIDADE_ID;
    private static String AUTH_TOKEN;

    public UnidadeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        navActivity = (MainNavActivity) getActivity();
        View unidadeView = inflater.inflate(R.layout.fragment_unidade, container, false);

        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();

        Bundle bundle = this.getArguments();

        CURRENT_UNIDADE_ID = bundle.getString("unidade_id");

        setUnidade(unidadeView, loadUnidade());

        return unidadeView;
    }

    private void setUnidade(View view, Unidade unidade){
        TextView nome = (TextView) view.findViewById(R.id.unidade_nome);
        TextView endereco = (TextView) view.findViewById(R.id.unidade_endereco);

        nome.setText(unidade.getNome());
        endereco.setText(unidade.getLocation().getAddress());

        List<String> vacinas = new ArrayList<>();
        vacinas.add("Hepatite B");
        vacinas.add("Gripe");

        unidade.setVacinas(vacinas);

        ArrayAdapter adapter = new ArrayAdapter<String>(navActivity, R.layout.vacina_unidade_item, unidade.getVacinas());

        ListView listView = (ListView) view.findViewById(R.id.vacinas_unidade);
        listView.setAdapter(adapter);

    }

    private Unidade loadUnidade(){

        UnidadeLoadTask unidadeLoadTask = new UnidadeLoadTask();

        try{
            unidadeLoadTask.execute();
            CURRENT_UNIDADE = unidadeLoadTask.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            System.out.println("ERRO AO CARREGAR UNIDADE: " + e);
        }

        return CURRENT_UNIDADE;
    }

    private class UnidadeLoadTask extends AsyncTask<Void, Void, Unidade>{

        @Override
        protected Unidade doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            Unidade loadedUnidade;

            try {

                String url = navActivity.BASE_URL + "/api/locais/";
                url += CURRENT_UNIDADE_ID;

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

                ResponseEntity<Unidade> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Unidade.class);
                System.out.println(result.getBody());

                loadedUnidade = result.getBody();

            }catch(Exception e){
                System.out.println("ERRO AO BUSCAR UNIDADE: " + e);
                loadedUnidade = new Unidade();
            }


            return loadedUnidade;
        }
    }

}
