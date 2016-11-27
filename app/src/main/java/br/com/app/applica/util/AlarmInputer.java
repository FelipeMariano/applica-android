package br.com.app.applica.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Map;

import br.com.app.applica.R;
import br.com.app.applica.activity.NotificationActivity;

/**
 * Created by felipe on 27/11/16.
 */
public class AlarmInputer {

    public static void scheduleAlarm(Context context, Map<String, Integer> alarmDate, int notificationId, Map<String, String> dados){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_content))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification_on)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_vaccine)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putExtra("data", dados.get("data"));
        intent.putExtra("vacina", dados.get("vacina"));
        intent.putExtra("dose", dados.get("dose"));
        intent.putExtra("detalhes", dados.get("detalhes"));
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.roll(Calendar.DAY_OF_MONTH, -1);
        cal.clear();

        int year = alarmDate.get("year");
        int month = alarmDate.get("month") - 1;

        if(month < 0)
            month = 11;

        int day = alarmDate.get("day");
        int hour = alarmDate.get("hour");
        int minutes = alarmDate.get("minutes");

        cal.set(year, month, day, hour, minutes);
        long alarmTime = cal.getTimeInMillis();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

    }
}
