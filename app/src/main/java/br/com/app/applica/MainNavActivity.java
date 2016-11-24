package br.com.app.applica;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.activity.AboutUsActivity;
import br.com.app.applica.activity.LoginActivity;
import br.com.app.applica.entitity.Unidade;
import br.com.app.applica.entitity.User;
import br.com.app.applica.fragment.HomeFragment;
import br.com.app.applica.fragment.MapsFragment;
import br.com.app.applica.fragment.MyProfileFragment;
import br.com.app.applica.fragment.UnidadesFragment;

public class MainNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView.Adapter mAdapter;
    public static String BASE_URL;
    public static Location CURRENT_LOCATION;
    public List<Unidade> UNIDADES_NEAR;

    private String[] activityTitles;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    public static int navItemIndex = 0;

    public static final String TAG_HOME = "home";
    public static final String TAG_UNIDADES = "unidades";
    public static final String TAG_MY_PROFILE = "my_profile";

    public static final String TAG_CARDENETA = "card";
    public static final String TAG_APLICACAO = "aplic";

    public static String CURRENT_TAG = TAG_HOME;


    public static Toolbar toolbar;

    public User CURRENT_USER;
    public Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("INITIALIZING MAIN NAV");

        setContentView(R.layout.activity_main_nav);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BASE_URL = getResources().getString(R.string.base_url);


        fab = (FloatingActionButton) findViewById(R.id.fab);


        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String userId = "";
        String userToken = "";
        Bundle extras = getIntent().getExtras();
        userId = extras.getString("id");
        userToken = extras.getString("x-access-token");

        CURRENT_USER = new User();
        CURRENT_USER.setId(userId);
        CURRENT_USER.setAuthToken(userToken);

        LoadUser loadUserTask = new LoadUser();
        User loadedUser = new User();
        try{
            loadUserTask.execute();
            loadedUser = loadUserTask.get(5000, TimeUnit.MILLISECONDS);
            loadedUser.setPassword(CURRENT_USER.getPassword());

            CURRENT_USER = loadedUser;
        }catch(Exception e){

        }

        if(CURRENT_USER.getId() == null){
            System.out.println("ID IS NULL!");
            LoginActivity.LOGIN_ERROR = "*Erro ao realizar login. Verifique os dados.";
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if(savedInstanceState == null && CURRENT_USER.getId() != null){
            CURRENT_TAG = TAG_HOME;
            navItemIndex = 0 ;
            loadFragment();
        }

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (CURRENT_USER.getId() != null){
        // Inflate the menu; this adds items to the action bar if it is present.
        TextView nome = (TextView) findViewById(R.id.nav_header_name);
        TextView email = (TextView) findViewById(R.id.nav_header_email);

        nome.setText(CURRENT_USER.getFullName());
        email.setText(CURRENT_USER.getEmail());
        //getMenuInflater().inflate(R.menu.main_nav, menu);
        }

       return true;
    }

    private void setToolbarTitle(){
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu(){
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Boolean selectable = true;

        if (id == R.id.nav_home) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
        } else if (id == R.id.nav_unidades) {
            navItemIndex = 1;
            CURRENT_TAG = TAG_UNIDADES;
        }else if (id == R.id.nav_my_profile) {
            navItemIndex = 2;
            CURRENT_TAG = TAG_MY_PROFILE;
        }else if (id == R.id.nav_about_us) {
            selectable = false;
            Intent intent = new Intent(this, AboutUsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            try{
                File file = new File(getFilesDir(), "userData");
                if(file.exists())
                   file.delete();

                Intent intent = new Intent(this, Splash.class);
                startActivity(intent);
            }catch(Exception e){
                System.out.println("ERROR TRYING TO LOG OUT!");
            }
        }

        if(item.isChecked())
            item.setChecked(false);
        if(selectable)
            item.setChecked(true);

        loadFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadFragment(){
        //

        if(CURRENT_TAG.equals(TAG_UNIDADES)){
           // Fragment mapsFragment = new MapsFragment();
            Fragment mapsFragment = new MapsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction =  fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.fragment_layout, mapsFragment);
            fragmentTransaction.replace(R.id.fragment_layout, mapsFragment);
            //Toast.makeText(this,"MAPS",Toast.LENGTH_SHORT).show();
            fragmentTransaction.commit();
            toggleFab("HIDE", null);
            return;
        }

        if(CURRENT_TAG.equals(TAG_MY_PROFILE)){
            Fragment myProfFragment = new MyProfileFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragment_layout, myProfFragment);

            fragmentTransaction.commit();
            toggleFab("HIDE", null);
            return;
        }

        Fragment homeFragment = getFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_layout, homeFragment).commit();

        setToolbarTitle();
        selectNavMenu();

        toggleFab(TAG_CARDENETA, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("BRAND");
            }
        });

    }

    private Fragment getFragment(){
        switch(navItemIndex){
            case 0:
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                UnidadesFragment unidadesFragment = new UnidadesFragment();
                return unidadesFragment;
            default:
                return new HomeFragment();
        }
    }

    public void toggleFab(String toAdd, View.OnClickListener listener){
        fab.show();
        switch(toAdd){
            case TAG_CARDENETA:
                setFloatActionButton(listener);
                break;
            case TAG_APLICACAO:
                setFloatActionButton(listener);
                break;
            case "HIDE":
                fab.hide();
                break;
        }
    }

    public void setFloatActionButton(View.OnClickListener listener){
            fab.setOnClickListener(listener);
    }


    private class LoadUser extends AsyncTask<Void, Void, User> {

        @Override
        protected User doInBackground(Void... params) {

            User loggedUser;

            try {

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders requestHeaders = new HttpHeaders();

                String authToken = CURRENT_USER.getAuthToken();
                String userId = CURRENT_USER.getId();

                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                requestHeaders.add("x-access-token", authToken);

                String url = BASE_URL + "/api/users/" + CURRENT_USER.getId();

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, User.class);


                loggedUser = (User) result.getBody();
                loggedUser.setAuthToken(authToken);
                loggedUser.setId(userId);
            }catch(Exception e){
                System.out.println("USER AUTHENTICATION ERROR: " + e);
                return new User();
            }
            return loggedUser;
        }
    }
}
