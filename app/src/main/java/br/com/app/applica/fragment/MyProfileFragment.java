package br.com.app.applica.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
    private MainNavActivity navActivity;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        navActivity = (MainNavActivity) getActivity();

        View profileView = (View) inflater.inflate(R.layout.fragment_my_profile, container, false);

        loadUser();

        return profileView;
    }

    private void loadUser(){
        User user = new User();
        user = navActivity.CURRENT_USER;

        System.out.println(user);
    }
}
