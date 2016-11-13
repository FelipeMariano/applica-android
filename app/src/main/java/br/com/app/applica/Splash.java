package br.com.app.applica;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import br.com.app.applica.activity.LoginActivity;
import br.com.app.applica.entitity.Auth;
import br.com.app.applica.entitity.User;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Splash extends AppCompatActivity {
    private User user;
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        user = new User();


        final Thread launcherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3000);
                    //File file = new File(getFilesDir(), "userData");
                    //if(file.exists())
                    //    file.delete();

                    user = getUserLocally();

                    System.out.println(user.getEmail());
                    if (user.getEmail() != null)
                        System.out.println("Is Logged!");
                    else{
                        System.out.println("Login online!");
                        Intent intent = new Intent(Splash.this, LoginActivity.class);
                        startActivity(intent);
                        return;
                    }

                    Intent intent = new Intent(Splash.this, MainNavActivity.class);
                    intent.putExtra("id", user.getId());
                    intent.putExtra("x-access-token", user.getAuthToken());
                    startActivity(intent);

                }catch(Exception e){
                    System.out.println("SPLASH PAGE - ERRO: " + e);
                }finally{

                    finish();
                }
            }
        });

        launcherThread.start();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private User getUserLocally(){
        User localUser = new User();



        ArrayList<String> userData = new ArrayList<String>();
        String data = null;
        FileInputStream fis;

        try{
            fis = getApplicationContext().openFileInput("userData");
            InputStreamReader isr = new InputStreamReader(fis);

            char[] inputBuffer = new char[fis.available()];

            isr.read(inputBuffer);
            data = new String(inputBuffer);
            isr.close();
            fis.close();

        }catch(Exception e){
            System.out.println("ERROR TRYING TO READ USER: " + e);
            return user;
        }


        XmlPullParserFactory factory = null;

        try {
            factory = XmlPullParserFactory.newInstance();
        }catch(Exception e ){

        }

        factory.setNamespaceAware(true);
        XmlPullParser xpp = null;
        int eventType = 0;
        try{
            xpp = factory.newPullParser();
            xpp.setInput(new StringReader(data));
            eventType = xpp.getEventType();
        }catch(Exception e){

        }


        while(eventType != XmlPullParser.END_DOCUMENT){
            String dataPointer = "";
            switch(eventType){
                case XmlPullParser.START_DOCUMENT:
                    System.out.println("Start document");
                    break;
                case XmlPullParser.START_TAG:
                    dataPointer = xpp.getName();
                    System.out.println("Start tag " + xpp.getName());
                    break;
                case XmlPullParser.END_TAG:
                    System.out.println("End tag " + xpp.getName());
                    break;
                case XmlPullParser.TEXT:
                    userData.add(xpp.getText());
                    break;
            }

            try{
                eventType = xpp.next();
            }catch(Exception e){
                System.out.println("ERROR ON NEXT:" + e );
                break;
            }

        }

        try{
            user.setId(userData.get(0));
            user.setEmail(userData.get(1));
            user.setPassword(userData.get(2));
            user.setAuthToken(userData.get(3));
        }catch(Exception e){

        }finally {
            return user;
        }

    }
    private class UserLoginTask extends AsyncTask<Void, Void, br.com.app.applica.entitity.User> {
        //FAVOR MOVER ISTO PARA ACTIVITY DE LOGIN!!!!
        @Override
        protected User doInBackground(Void... params){
            try{
                User user = new User();
                String url = "http://applica-ihc.44fs.preview.openshiftapps.com/authenticate";

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders requestHeaders = new HttpHeaders();

                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                //requestHeaders.add("x-access-token", getAuthToken());

                user.setEmail("teste@gmail.com");
                user.setPassword("felipe123");

                LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
                _map.put("email", user.getEmail());
                _map.put("password", user.getPassword());

                StringWriter _writer = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();

                try {
                    mapper.writeValue(_writer, _map);
                }catch(Exception e){

                }

                try {

                    HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                    ResponseEntity<Auth> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);

                    Auth body = result.getBody();

                    user.setId(body.getUser_id());
                    user.setAuthToken(body.getToken());
                }catch(Exception e){
                    System.out.println("FAILS TO VALIDATE USER: " + e);
                    return new User();
                }finally{
                    return user;
                }

            }catch(Exception e){
                System.out.println("Error: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user){

            System.out.println("USER LOADED");
        }
    }

}
