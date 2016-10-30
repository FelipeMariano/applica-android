package br.com.app.applica;

import android.content.Context;
import android.content.Intent;

/**
 * Created by felipe on 30/10/16.
 */
public class Activity {
    public static void startMainActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void startCardenetaActivity(Context context){
        Intent intent = new Intent(context, CardenetaActivity.class);
        context.startActivity(intent);
    }
}
