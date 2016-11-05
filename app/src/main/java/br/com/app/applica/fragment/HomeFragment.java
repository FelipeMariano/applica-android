package br.com.app.applica.fragment;


import android.os.AsyncTask;
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
import br.com.app.applica.adapter.CardenetaAdapter;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private User CURRENT_USER;
    List<Cardeneta> cardenetas;
    private RecyclerView.Adapter mAdapter;

    private void setRecyclerLayout(RecyclerView recyclerView){

        cardenetas = new ArrayList<>();

        CardenetasTask loadCardenetas = new CardenetasTask();
        RecyclerView.LayoutManager layout;
            mAdapter = new CardenetaAdapter(CURRENT_USER.getListaCardenetas());
            recyclerView.setAdapter(mAdapter);
            layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(layout);

        ((CardenetaAdapter) mAdapter).setOnItemClickListener(new CardenetaAdapter.MyClickListener(){
            @Override
            public void onItemClick(int position, View v){
                System.out.println("CLICKED!");
            }
        });



    }

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainNavActivity navActivity = (MainNavActivity) getActivity();
        CURRENT_USER = navActivity.CURRENT_USER;

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.cardeneta_recycler);

        setRecyclerLayout(mRecyclerView);

        return rootView;
    }

    private class CardenetasTask extends AsyncTask<Void, Void, User>{

        @Override
        protected User doInBackground(Void... params) {

            CURRENT_USER.loadCardenetas();

            return CURRENT_USER;
        }

        @Override
        protected void onPostExecute(User user){
            System.out.println(CURRENT_USER.getListaCardenetas().size());
        }
    }

}
