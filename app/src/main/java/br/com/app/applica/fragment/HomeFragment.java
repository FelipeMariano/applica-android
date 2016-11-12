package br.com.app.applica.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.adapter.CardenetaAdapter;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private User CURRENT_USER;
    List<Cardeneta> cardenetas;
    MainNavActivity navActivity;
    private RecyclerView.Adapter mAdapter;
    private Menu mMenu;

    private void setRecyclerLayout(RecyclerView recyclerView){

        cardenetas = new ArrayList<>();

        CardenetasTask loadCardenetas = new CardenetasTask();

        try{
            loadCardenetas.execute();
            loadCardenetas.get(5000, TimeUnit.MILLISECONDS);
            cardenetas = CURRENT_USER.getListaCardenetas();
        }catch(Exception e){

        }

        RecyclerView.LayoutManager layout;
            mAdapter = new CardenetaAdapter(cardenetas);

            recyclerView.setAdapter(mAdapter);
            layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);

        ((CardenetaAdapter) mAdapter).setOnItemHold(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CardenetaAdapter.CardenetaViewHolder vHolder = (CardenetaAdapter.CardenetaViewHolder) v.getTag();
                System.out.println("I WAS HOLDED: " + vHolder.getId());
                navActivity.getMenuInflater().inflate(R.menu.main_nav, mMenu);
                return false;
            }
        });


        ((CardenetaAdapter) mAdapter).setOnItemClickListener(new CardenetaAdapter.MyClickListener(){
            @Override
            public void onItemClick(int position, View v){

                CardenetaAdapter.CardenetaViewHolder vHolder = (CardenetaAdapter.CardenetaViewHolder) v.getTag();

                Fragment cardeneta = new CardenetaFragment();

                Bundle bundle = new Bundle();
                bundle.putString("card_id", vHolder.getId());

                cardeneta.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();


                transaction.replace(R.id.fragment_layout, cardeneta);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });



    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navActivity = (MainNavActivity) getActivity();
        CURRENT_USER = navActivity.CURRENT_USER;

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardeneta_recycler);

        registerForContextMenu(mRecyclerView);

        navActivity.toggleFab(MainNavActivity.TAG_CARDENETA, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardenetaFormFragment fragment = new CardenetaFormFragment();
                fragment.CURRENT_CARD_ID = null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.replace(R.id.fragment_layout, fragment);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });

        setRecyclerLayout(mRecyclerView);
        setHasOptionsMenu(true);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        navActivity.getMenuInflater().inflate(R.menu.home_menu, menu);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_sync:
                System.out.println("Sync!");
                return true;
        }
        return false;
    }


    private class CardenetasTask extends AsyncTask<Void, Void, User>{

        @Override
        protected User doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", CURRENT_USER.getAuthToken());

            String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + CURRENT_USER.getId();
            url += "/cardenetas";

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

            ResponseEntity<List<Cardeneta>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Cardeneta>>() {
            });

            CURRENT_USER.setListaCardenetas(result.getBody());

            return CURRENT_USER;
        }

        @Override
        protected void onPostExecute(User user){
            System.out.println(CURRENT_USER.getListaCardenetas().size());
        }
    }

}
