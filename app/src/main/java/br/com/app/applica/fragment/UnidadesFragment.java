package br.com.app.applica.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.adapter.UnidadeAdapter;
import br.com.app.applica.entitity.Unidade;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnidadesFragment extends Fragment {
    MainNavActivity navActivity;
    private static String AUTH_TOKEN;
    private RecyclerView.Adapter mAdapter;

    public UnidadesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        navActivity = (MainNavActivity) getActivity();
        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();

        ///

        View unidadesView = inflater.inflate(R.layout.fragment_unidades, container, false);
        final RecyclerView mRecyclerView = (RecyclerView) unidadesView.findViewById(R.id.unidades_recycler);

        List<Unidade> dummy_unidades = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            Unidade un = new Unidade();
            un.set_id("12a" + i);
            un.setNome("Unidade" + (i + 1));
            dummy_unidades.add(un);
        }

        setRecyclerLayout(mRecyclerView, dummy_unidades);
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


    }



}
