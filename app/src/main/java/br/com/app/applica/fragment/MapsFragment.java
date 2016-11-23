package br.com.app.applica.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.Unidade;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment {
    MapView mMapView;
    private GoogleMap myGoogleMap;
    private MainNavActivity navActivity;
    public static List<Unidade> unidades;
    private Boolean LOCATION_MOVE = true;
    private String AUTH_TOKEN;

    private final LatLng LOC = new LatLng(49.187500, -122.849000);

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

        AUTH_TOKEN = navActivity.CURRENT_USER.getAuthToken();

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

                //myGoogleMap.addMarker(new MarkerOptions().position(LOC).title("My loction!"));
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(LOC, 9);
                //myGoogleMap.animateCamera(update);

                myGoogleMap.setMyLocationEnabled(true);

                if (ActivityCompat.checkSelfPermission(navActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(navActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                riseMap();
            }

        });

        return mapsView;
    }

    @Override
    public void onResume(){
        super.onResume();
        mMapView.onResume();
        if(myGoogleMap != null)
            riseMap();

    }

    private void riseMap(){
        System.out.println("SETTING MAP!");
        myGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener(){

            @Override
            public void onMyLocationChange(Location location) {
                if(LOCATION_MOVE) {
                    LOCATION_MOVE = false;

                    //getUnidadesNear(location);

                    //   myGoogleMap.addMarker(
                    //          new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude()))
                    //                  .title("Minha localização")
                    //                  .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location))
                    // );

                    //addMarkers();

                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
                    myGoogleMap.animateCamera(update);
                    navActivity.CURRENT_LOCATION = location;
                    getUnidadesNear(location);
                }
            }
        });
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

    private void addMarkers(){

        for (Unidade unidade : unidades) {
            List<Float> coordinates = unidade.getLocation().getCoordinates();
            LatLng LOC = new LatLng(coordinates.get(0), coordinates.get(1));
            String desc = "Endereço: " + unidade.getLocation().getAddress();
            desc += "\nTelefone: " + unidade.getTelefone();
            desc += "\nE-mail: " + unidade.getEmail();
            desc += "\nWebsite: " + unidade.getWebsite();


            myGoogleMap.addMarker(
                    new MarkerOptions().position(LOC)
                            .title(unidade.getNome())
                            .snippet(desc)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_default_location))
            );

            myGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    LinearLayout info = new LinearLayout(navActivity);

                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(navActivity);
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(navActivity);
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);
                    return info;
                }
            });
        }
    }

    private List<Unidade> getUnidadesNear(Location loc){
        UnidadesNearTask unidadesNearTask = new UnidadesNearTask();

        try{
            unidadesNearTask.execute(loc.getLatitude(), loc.getLongitude());
            unidades = unidadesNearTask.get(5000, TimeUnit.MILLISECONDS);
            navActivity.UNIDADES_NEAR = unidades;
            System.out.println(unidades);
            addMarkers();
        }catch(Exception e){
            System.out.println("Erro ao puxar unidades perto: " + e);
        }

        return unidades;
    }

    private class UnidadesNearTask extends AsyncTask<Double, Void, List<Unidade>>{

        @Override
        protected List<Unidade> doInBackground(Double... params) {
            List<Unidade> loadedUnidades = new ArrayList<>();

            Double lat = params[0];
            Double lng = params[1];

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            String url = navActivity.BASE_URL + "/api/locais/nearlocation/";

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("latitude", lat)
                    .queryParam("longitude", lng);

            try{
                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);


                ResponseEntity<List<Unidade>> result = restTemplate.exchange(builder.build().encode().toUri()
                        , HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Unidade>>() {
                });

                loadedUnidades = result.getBody();
            }catch(Exception e){
                System.out.println("UNABLE TO LOAD LOCATIONS: " + e);
                loadedUnidades = new ArrayList<>();
            }

            return loadedUnidades;
        }
    }
}
