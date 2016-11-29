package br.com.app.applica.util;

import android.widget.Toast;

import org.joda.time.LocalDateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.fragment.FragmentFactory;

/**
 * Created by felipe on 27/11/16.
 */
public class DatePickerConstructor {

    public static void showDateTimePicker(final MainNavActivity navActivity, final Map dados, final int sortId){
        final LocalDateTime[] localDate = new LocalDateTime[1];
        Date dateToUse = new LocalDateTime().toDate();
        DateTimePickerFragment dateTimePickerFragment =
                FragmentFactory.createDatePickerFragment(dateToUse, "A", DateTimePickerFragment.BOTH,
                        new DateTimePickerFragment.ResultHandler(){

                            @Override
                            public void setDate(Date result) {
                                LocalDateTime locDate = new LocalDateTime(result.getTime());
                                int day_x = locDate.getDayOfMonth();
                                int month_x = locDate.getMonthOfYear();
                                int year_x = locDate.getYear();
                                int hour_x = locDate.getHourOfDay();
                                int minute_x = locDate.getMinuteOfHour();
                                Map<String, Integer> alarmDate = new HashMap<String, Integer>();
                                alarmDate.put("day", day_x);
                                alarmDate.put("month", month_x);
                                alarmDate.put("year", year_x);
                                alarmDate.put("hour", hour_x);
                                alarmDate.put("minutes", minute_x);

                                Toast.makeText(navActivity, "Lembrete adicionado com sucesso.", Toast.LENGTH_SHORT).show();
                                AlarmInputer.scheduleAlarm(navActivity, alarmDate, sortId, dados);
                            }
                        });

        dateTimePickerFragment.show( navActivity.getFragmentManager(), DateTimePickerFragment.DIALOG_TAG);
    }

}
