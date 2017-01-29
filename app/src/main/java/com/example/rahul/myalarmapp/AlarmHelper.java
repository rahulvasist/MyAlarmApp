package com.example.rahul.myalarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by rahul on 1/2/17.
 */

class Alarm {
    long finalAlarmTimeMillis;
    int beforeMin;
    int intervalMin;

    public Alarm(long _finalAlarm, int _beforeMin, int _intervalMin) {
        this.finalAlarmTimeMillis = _finalAlarm;
        this.beforeMin = _beforeMin;
        this.intervalMin = _intervalMin;
    }
}

public class AlarmHelper {

    private static String getTimeString(long timeInMillis) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        long diffSecs = (timeInMillis - currentTime) / 1000;
        long diffMins = diffSecs / 60;

        return ((int)(diffMins/60) + "hrs " + diffMins%60 + "mins " + (diffSecs%60) + "secs");

    }

    private static void setAlarm(Context context, long timeInMillis) {
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Log.d("MainActivity", "Set alarm in future at " + getTimeString(timeInMillis));
        Log.d("MainActivity", "Set alarm at " + timeInMillis);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, timeInMillis, pendingIntent);
    }


    public static void addAlarm(Context context, int final_hour, int final_min, int before_min, int interval_min) {
        Calendar calendar = Calendar.getInstance();

        /* Check if the final alarm time is in the past, if so add a day */
        if ((calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)) >
                (final_hour * 60 + final_min)) {
            calendar.add(Calendar.DATE, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, final_hour);
        calendar.set(Calendar.MINUTE, final_min);
        calendar.set(Calendar.SECOND, 0);

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.alarm_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("FinalAlarm", calendar.getTimeInMillis());
        editor.putInt("BeforeMin", before_min);
        editor.putInt("IntervalMin", interval_min);
        editor.commit();

        long finalTime = calendar.getTimeInMillis();
        Log.d("MainActivity", "Added Alarm at " + getTimeString(finalTime));
    }

    public static void cancelAlarm(Context context) {
        Log.d("MainActivity", "Cancelling Alarm");
        Intent myIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void deleteAlarmData(Context context) {
        Log.d("MainActivity", "Deleting alarm data");
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.alarm_file),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }


    public static void setNextAlarmEvent(Context context) {
        Alarm setAlarm = getPrevSetAlarm(context);

        if ((setAlarm.finalAlarmTimeMillis == -1) || (setAlarm.beforeMin == -1) || (setAlarm.intervalMin == -1)) {
            return;
        }

        long curTimeMillis = Calendar.getInstance().getTimeInMillis();
        long firstAlarmTimeMillis = setAlarm.finalAlarmTimeMillis - (setAlarm.beforeMin * 60 * 1000);
        long intervalTimeMillis = setAlarm.intervalMin * 60 * 1000;

        if (curTimeMillis < firstAlarmTimeMillis) {
            setAlarm(context, firstAlarmTimeMillis);
        } else if ((curTimeMillis + intervalTimeMillis) >= setAlarm.finalAlarmTimeMillis) {
            setAlarm(context, setAlarm.finalAlarmTimeMillis);
        } else {
            setAlarm(context, curTimeMillis + intervalTimeMillis);
        }
    }

    public static Alarm getPrevSetAlarm(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.alarm_file),
                Context.MODE_PRIVATE);
        long finalAlarmTimeMillis = sharedPref.getLong("FinalAlarm", -1);
        int before_min = sharedPref.getInt("BeforeMin", -1);
        int interval_min = sharedPref.getInt("IntervalMin", -1);

        return (new Alarm(finalAlarmTimeMillis, before_min, interval_min));
    }

    public static boolean isFinalAlarm(Context context) {
        Alarm setAlarm = getPrevSetAlarm(context);

        if ((setAlarm.finalAlarmTimeMillis == -1) || (setAlarm.beforeMin == -1) || (setAlarm.intervalMin == -1)) {
            return (false);
        }

        long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
        /* If currentTime is greater than final time minus a minute */
        if (currentTimeMillis >= (setAlarm.finalAlarmTimeMillis - (1 * 60 * 1000))) {
            return true;
        } else {
            return (false);
        }
    }
}
