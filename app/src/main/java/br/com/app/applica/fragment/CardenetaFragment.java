package br.com.app.applica.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.adapter.AplicacaoAdapter;
import br.com.app.applica.entitity.Aplicacao;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardenetaFragment extends Fragment {
    private Cardeneta CURRENT_CARD;
    private String CURRENT_CARD_ID;
    private String AUTH_TOKEN;
    private List<Aplicacao> aplicacoes;
    private RecyclerView.Adapter mAdapter;

    MainNavActivity navActivity;

    public CardenetaFragment() {
        // Required empty public constructor
    }

    private void setRecyclerLayout(RecyclerView recyclerView){

        //Chamada para get

        RecyclerView.LayoutManager layout;
        mAdapter = new AplicacaoAdapter(aplicacoes);
        recyclerView.setAdapter(mAdapter);
        layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);


        ((AplicacaoAdapter) mAdapter).setOnItemClickListener(new AplicacaoAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                System.out.println("I WAS CLICKED!");
            }
        });
    }

    private void loadDadadosCardeneta(){

        CardenetaDadosTask loadCardDados = new CardenetaDadosTask();

        try{
            loadCardDados.execute();
            loadCardDados.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            System.out.println("ERRO AO CHAMAR TASK PARA LOAD CARD: " + e);
        }

        String title_name = CURRENT_CARD.getNome() + " " + CURRENT_CARD.getSobrenome();
        navActivity.getSupportActionBar().setTitle(title_name);
        aplicacoes = CURRENT_CARD.getListaAplicacoes();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        navActivity = (MainNavActivity) getActivity();

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            CURRENT_CARD_ID = bundle.getString("card_id");

            User user = navActivity.CURRENT_USER;
            navActivity.toggleFab(MainNavActivity.TAG_APLICACAO, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AplicacaoFormFragment fragment = new AplicacaoFormFragment();
                    fragment.CURRENT_APLICACAO_ID = null;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_layout, fragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            });
            AUTH_TOKEN = user.getAuthToken();

        }
        //Load cardeneta data by id acima /\


        loadDadadosCardeneta();

        View cardenetaView = inflater.inflate(R.layout.fragment_cardeneta, container, false);
        RecyclerView mRecyclerView = (RecyclerView) cardenetaView.findViewById(R.id.aplicacoes_recycler);

        setRecyclerLayout(mRecyclerView);

        return cardenetaView;
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

        private void loadAplicacoes(HttpHeaders requestHeaders, RestTemplate restTemplate){
            String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/cardenetas/" + CURRENT_CARD_ID;
            url += "/aplicacoes";

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

            ResponseEntity<List<Aplicacao>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Aplicacao>>() {
            });

            loadedCardeneta.setListaAplicacoes(result.getBody());
            System.out.println(loadedCardeneta.getListaAplicacoes());
        }

        @Override
        protected Cardeneta doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            loadData(requestHeaders, restTemplate);

            if(loadedCardeneta.get_id() != null)
                loadAplicacoes(requestHeaders, restTemplate);

            //ResponseEntity<List<Cardeneta>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Cardeneta>>() {
            //});
            CURRENT_CARD = loadedCardeneta;
            return CURRENT_CARD;
        }
    }

}
