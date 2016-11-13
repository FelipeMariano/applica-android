package br.com.app.applica.fragment;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
    List<Cardeneta> cardenetas;
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
                //return getGestureDetector(event, v);
                return gestureDetector.onTouchEvent(event);
            }
        });

        //setOnClickListener();


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
            navActivity.getMenuInflater().inflate(R.menu.main_nav, mMenu);
       }

        public boolean onSingleTapUp(MotionEvent e ){
            Fragment cardeneta = new CardenetaFragment();

            Bundle bundle = new Bundle();
            bundle.putString("card_id", CURRENT_CARD_ID);
            cardeneta.setArguments(bundle);

            FragmentTransaction transaction= getFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_layout, cardeneta);
            transaction.addToBackStack(null);

            transaction.commit();

            return true;
        }
    });

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
            case R.id.action_edit:
                OPTIONS_SHOW = false;
                CardenetaFragment.setToEdit(CURRENT_CARD_ID, getFragmentManager());
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(navActivity).setTitle("Deletar cardeneta")
                        .setMessage("Você realmente deseja deletar a cardeneta permanentemente? Todos os dados aqui serão perdidos!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                if(CardenetaFragment.delete(CURRENT_CARD_ID))
                                    Toast.makeText(navActivity, "Cardeneta deletada!", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton(android.R.string.no, null).show();

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
