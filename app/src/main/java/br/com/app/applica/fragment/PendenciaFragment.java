package br.com.app.applica.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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


    MainNavActivity navActivity;

    public PendenciaFragment() {
        // Required empty public constructor
    }


    private void setRecyclerLayout(RecyclerView recyclerView, List<Pendencia> pendings){
        RecyclerView.LayoutManager layout;

        mAdapter = new PendingAdapter(pendings);

        recyclerView.setAdapter(mAdapter);
        layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View pendingsView =  inflater.inflate(R.layout.fragment_pendencia, container, false);

        navActivity = (MainNavActivity) getActivity();
        navActivity.toggleFab("HIDE", null);

        final RecyclerView mRecyclerView = (RecyclerView) pendingsView.findViewById(R.id.pendencias_recycler);

        List<Pendencia> dummies_pendings = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            Pendencia pend = new Pendencia();
            pend.set_id("123");
            pend.setCardeneta(new Cardeneta());
            pend.setDestino(new User());
            User or = new User();
            or.setEmail("Origem@gmail.com");
            pend.setOrigem(or);
            dummies_pendings.add(pend);
        }

        setRecyclerLayout(mRecyclerView, dummies_pendings);


        return pendingsView;
    }

}
