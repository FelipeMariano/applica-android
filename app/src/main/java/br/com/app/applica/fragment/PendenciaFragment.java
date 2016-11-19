package br.com.app.applica.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.springframework.core.ParameterizedTypeReference;
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
import br.com.app.applica.adapter.PendingAdapter;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.Pendencia;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendenciaFragment extends Fragment {
    private RecyclerView.Adapter mAdapter;
    private static String AUTH_TOKEN;
    private static String CURRENT_USER_ID;
    private List<Pendencia> pendencias;


    MainNavActivity navActivity;

    public PendenciaFragment() {
        // Required empty public constructor
    }


    private void setRecyclerLayout(RecyclerView recyclerView, List<Pendencia> pendings){
        RecyclerView.LayoutManager layout;

        mAdapter = new PendingAdapter(pendings);

        ((PendingAdapter) mAdapter).setAcceptClickListener(new PendingAdapter.AcceptClickListener() {
            @Override
            public void onAcceptClick(int position, View v) {
                PendingAdapter.PendingViewHolder vHolder = (PendingAdapter.PendingViewHolder) v.getTag();
                accept(vHolder.getId());
            }
        });

        ((PendingAdapter) mAdapter).setRejectClickListener(new PendingAdapter.RejectClickListener() {
            @Override
            public void onRejectClick(int position, View v) {
                PendingAdapter.PendingViewHolder vHolder = (PendingAdapter.PendingViewHolder) v.getTag();

                Toast.makeText(navActivity, "REJECTED: " + vHolder.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(mAdapter);
        layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);
    }

    public List<Pendencia> loadPendencias(){
        PendingsLoadTask pendingsLoadTask = new PendingsLoadTask();
        List<Pendencia> pendings;

        try{
            pendingsLoadTask.execute();
            pendings = pendingsLoadTask.get(5000, TimeUnit.MILLISECONDS);

        }catch (Exception e){
            System.out.println("ERRO AO CARREGAR PENDENCIAS: " + e);
            pendings = new ArrayList<>();
        }


        return pendings;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View pendingsView =  inflater.inflate(R.layout.fragment_pendencia, container, false);

        navActivity = (MainNavActivity) getActivity();
        navActivity.toggleFab("HIDE", null);

        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();
        CURRENT_USER_ID = navActivity.CURRENT_USER.getId();

        final RecyclerView mRecyclerView = (RecyclerView) pendingsView.findViewById(R.id.pendencias_recycler);

        List<Pendencia> dummies_pendings = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            Pendencia pend = new Pendencia();
            pend.set_id("123");
            pend.setCardeneta(new Cardeneta());
            pend.setUser_dest(new User());
            User or = new User();
            or.setEmail("Origem@gmail.com");
            pend.setUser_origin(or);
            dummies_pendings.add(pend);
        }

        setRecyclerLayout(mRecyclerView, loadPendencias());
        //loadPendencias();

        return pendingsView;
    }

    private Cardeneta accept(String pending){
        PendingAcceptTask pendingAcceptTask = new PendingAcceptTask();

        try{
            pendingAcceptTask.execute(pending);
            pendingAcceptTask.get(5000, TimeUnit.MILLISECONDS);

            Toast.makeText(navActivity, "Cardeneta aceita com sucesso!", Toast.LENGTH_SHORT).show();
            navActivity.getSupportFragmentManager().popBackStack();
           navActivity.CURRENT_USER.getPendings().remove(pending);
        }catch(Exception e){
            System.out.println("ERRO AO ACEITAR PENDÊNCIA" + e);
        }

        return null;
    }


    private static class PendingsLoadTask extends AsyncTask<Void, Void, List<Pendencia>> {

        @Override
        protected List<Pendencia> doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            List<Pendencia> pendings;

            try {

                String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + CURRENT_USER_ID;
                url += "/pendings";

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

                ResponseEntity<List<Pendencia>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Pendencia>>() {
                });
                System.out.println(result.getBody());
                pendings = result.getBody();
            }catch(Exception e){
                System.out.println("ERRO AO DELETAR CARDENETA: " + e);
                pendings = new ArrayList<>();
            }

            return pendings;
        }
    }


    private static class PendingAcceptTask extends AsyncTask<String, Void, Cardeneta>{

        @Override
        protected Cardeneta doInBackground(String... params) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            String PENDING_ID = params[0];

            try {

                String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/share/" + PENDING_ID;
                url += "/accept";

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Object.class);
                System.out.println(result.getBody());

            }catch(Exception e){
                System.out.println("ERRO AO DELETAR CARDENETA: " + e);

            }

            return null;
        }
    }
}
