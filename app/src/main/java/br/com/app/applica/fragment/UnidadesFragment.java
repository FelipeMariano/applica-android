package br.com.app.applica.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import br.com.app.applica.adapter.UnidadeAdapter;
import br.com.app.applica.entitity.Unidade;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnidadesFragment extends Fragment {
    public static MainNavActivity navActivity;
    private static String AUTH_TOKEN;
    private static String CURRENT_UNIDADE_ID;
    private List<Unidade> unidades;
    private RecyclerView.Adapter mAdapter;

    public UnidadesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navActivity = (MainNavActivity) getActivity();
        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();

        navActivity.toggleFab("HIDE", null);

        ///

        System.out.println(navActivity.CURRENT_LOCATION);

        View unidadesView = inflater.inflate(R.layout.fragment_unidades, container, false);
        final RecyclerView mRecyclerView = (RecyclerView) unidadesView.findViewById(R.id.unidades_recycler);

        List<Unidade> dummy_unidades = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            Unidade un = new Unidade();
            un.set_id("12a" + i);
            un.setNome("Unidade" + (i + 1));
            dummy_unidades.add(un);
        }
        unidades = navActivity.UNIDADES_NEAR; //loadUnidades();

        setRecyclerLayout(mRecyclerView, unidades);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment

        return unidadesView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        navActivity.getMenuInflater().inflate(R.menu.locations_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case(R.id.action_map_unidades):
                Toast.makeText(navActivity, "MAP SHOWS!", Toast.LENGTH_SHORT).show();
                Fragment mapsFragment = new MapsFragment();
                MapsFragment.unidades = unidades;
                FragmentManager fragmentManager = navActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, mapsFragment);
                fragmentTransaction.commit();
                return true;
            case(R.id.action_list_unidades):
                Toast.makeText(navActivity, "LIST SHOWS!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }
    }

    private void setRecyclerLayout(RecyclerView recyclerView, List<Unidade> unidades){
        RecyclerView.LayoutManager layout;
        mAdapter = new UnidadeAdapter(unidades);

        recyclerView.setAdapter(mAdapter);

        layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);

        ((UnidadeAdapter) mAdapter).setOnItemTouch(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UnidadeAdapter.UnidadeViewHolder vHolder = (UnidadeAdapter.UnidadeViewHolder) v.getTag();
                CURRENT_UNIDADE_ID = vHolder.getId();

                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){

        public boolean onSingleTapUp(MotionEvent e ){

            UnidadeFragment unidadeFragment = new UnidadeFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            Bundle bundle = new Bundle();
            bundle.putString("unidade_id", CURRENT_UNIDADE_ID);

            unidadeFragment.setArguments(bundle);

            transaction.replace(R.id.fragment_layout, unidadeFragment);
            transaction.addToBackStack(null);

            transaction.commit();

            return true;
        }
    });

    private List<Unidade> loadUnidades(){
        List<Unidade> loadedUnidades = new ArrayList<>();
        UnidadesTask loadUnidadesTask = new UnidadesTask();

        try{
            loadUnidadesTask.execute();
            loadedUnidades = loadUnidadesTask.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            System.out.println("ERRO AO RETORNAR UNIDADES: " + e);
            return null;
        }


        return loadedUnidades;
    }

    private class UnidadesTask extends AsyncTask<Void, Void, List<Unidade>>{

        @Override
        protected List<Unidade> doInBackground(Void... params) {
            List<Unidade> loadedUnidades = new ArrayList<>();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            String url = navActivity.BASE_URL + "/api/locais/nearlocation/";
            url += navActivity.CURRENT_USER.getId();

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

            ResponseEntity<List<Unidade>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Unidade>>() {
            });

           loadedUnidades = result.getBody();
            return loadedUnidades;
        }
    }

}
