package br.com.app.applica.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.app.applica.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardenetaFragment extends Fragment {


    public CardenetaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Bundle bundle = this.getArguments();
        if(bundle != null)
            System.out.println(bundle.getString("card_id"));

        //Load cardeneta data by id acima /\ 

        return inflater.inflate(R.layout.fragment_cardeneta, container, false);
    }

}
