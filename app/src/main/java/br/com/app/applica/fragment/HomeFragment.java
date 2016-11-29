package br.com.app.applica.fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
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
import br.com.app.applica.adapter.CardenetaAdapter;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment{
    private User CURRENT_USER;
    private Boolean OPTIONS_SHOW = false;
    public static List<Cardeneta> cardenetas;
    MainNavActivity navActivity;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private String CURRENT_CARD_ID;
    private Menu mMenu;

    private void setRecyclerLayout(final RecyclerView recyclerView){

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

        ((CardenetaAdapter) mAdapter).setOnItemTouch(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                CardenetaAdapter.CardenetaViewHolder vHolder = (CardenetaAdapter.CardenetaViewHolder) v.getTag();
                CURRENT_CARD_ID = vHolder.getId();

                return gestureDetector.onTouchEvent(event);
            }
        });

        //setOnClickListener();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    private void setCurrentUser(){
        CURRENT_USER = navActivity.CURRENT_USER;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navActivity = (MainNavActivity) getActivity();

        setCurrentUser();

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardeneta_recycler);
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

    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
       public void onLongPress(MotionEvent e){
            mMenu.clear();
            navActivity.getMenuInflater().inflate(R.menu.main_nav, mMenu);
           Vibrator vibe = (Vibrator) navActivity.getSystemService(Context.VIBRATOR_SERVICE);
           vibe.vibrate(100);
       }

        public boolean onSingleTapUp(MotionEvent e ){


            final ProgressDialog progress = ProgressDialog.show(navActivity, "", "Carregando...", true);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Fragment cardeneta = new CardenetaFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("card_id", CURRENT_CARD_ID);
                    cardeneta.setArguments(bundle);

                    FragmentTransaction transaction= getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_layout, cardeneta);
                    transaction.addToBackStack(null);

                    transaction.commit();

                    navActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                        }
                    });
                }
            }).start();

            return true;
        }
    });

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        setPendingsAlert();

        inflateMainMenu();
    }

    private void inflateMainMenu(){
        navActivity.getMenuInflater().inflate(R.menu.home_menu, mMenu);
    }

    @Override
    public void onResume(){
        super.onResume();

        navActivity.getSupportActionBar().setTitle("Minhas cardenetas");

        if(mMenu != null)
            setPendingsAlert();
    }

    private void setPendingsAlert() {
        mMenu.clear();
        setCurrentUser();

        System.out.println("QTD PENDINGS: " + CURRENT_USER.getPendings().size());

        if (CURRENT_USER.getPendings() != null && CURRENT_USER.getPendings().size() > 0) {
            navActivity.getMenuInflater().inflate(R.menu.pending_menu, mMenu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_sync:
                try{
                    navActivity.loadUser(new Bundle());
                    setRecyclerLayout(mRecyclerView);
                    Toast.makeText(navActivity, "Dados atualizados", Toast.LENGTH_SHORT).show();
                    setPendingsAlert();
                    inflateMainMenu();
                }catch(Exception e){
                    Toast.makeText(navActivity, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.pending:
                try{
                    PendenciaFragment fragment = new PendenciaFragment();

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_layout, fragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }catch(Exception e){
                    System.out.println("NAO FOI POSSIVEL CARREGAR AS NOTIFICAÇÕES: " + e);
                }
                return true;
            case R.id.action_edit:
                OPTIONS_SHOW = false;
                CardenetaFragment.setToEdit(CURRENT_CARD_ID, getFragmentManager());
                return true;
            case R.id.action_delete:
                CardenetaFragment.delete(CURRENT_USER.getAuthToken(), CURRENT_CARD_ID, navActivity);
                setRecyclerLayout(mRecyclerView);
                return true;
            case R.id.action_share:
                CardenetaFragment.share(CURRENT_USER.getAuthToken(), CURRENT_CARD_ID, navActivity);
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

            String url = navActivity.BASE_URL + "/api/users/" + CURRENT_USER.getId();
            url += "/cardenetas";

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

            ResponseEntity<List<Cardeneta>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Cardeneta>>() {
            });

            CURRENT_USER.setListaCardenetas(result.getBody());

            return CURRENT_USER;
        }

        @Override
        protected void onPostExecute(User user){

        }
    }

}
