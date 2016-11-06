package br.com.app.applica.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardenetaFormFragment extends Fragment {
    MainNavActivity navActivity;

    public CardenetaFormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        navActivity = (MainNavActivity) getActivity();

        navActivity.toggleFab("HIDE", null);


        View rootView = inflater.inflate(R.layout.fragment_cardeneta_form, container, false);

        return rootView;
    }

}
