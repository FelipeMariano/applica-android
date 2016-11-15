package br.com.app.applica.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {
    MapView mMapView;
    private GoogleMap myGoogleMap;
    private MainNavActivity navActivity;

    public MapsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mapsView = inflater.inflate(R.layout.fragment_maps_view, container, false);

        mMapView = (MapView) mapsView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); //needed to get the map to display immediately;
        navActivity = (MainNavActivity) getActivity();

        setHasOptionsMenu(true);

        try {
            MapsInitializer.initialize(navActivity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                myGoogleMap = googleMap;

                //For showing a move to my location button;
                if (ActivityCompat.checkSelfPermission(navActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(navActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("SET LOCATION!");
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    myGoogleMap.setMyLocationEnabled(true);

                    LatLng sydney = new LatLng(-34, 151);
                    googleMap.addMarker(new MarkerOptions().position(sydney).title("My location").snippet("Mark description!"));


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    return;
                }

            }
        });

        return mapsView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
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
                return true;
            case(R.id.action_list_unidades):
                Toast.makeText(navActivity, "LIST SHOWS!", Toast.LENGTH_SHORT).show();
                Fragment listUnidadesFragment = new UnidadesFragment();
                FragmentManager fragmentManager = navActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout, listUnidadesFragment);
                fragmentTransaction.commit();
                return true;
            default:
                return false;
        }
    }

}
