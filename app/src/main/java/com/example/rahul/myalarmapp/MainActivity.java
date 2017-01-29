package com.example.rahul.myalarmapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int SET_ALARM = 1;
    private TextView finalAlarmTxtView;
    private TextView beforeMinTxtView;
    private TextView intervalMinTxtView;
    private AlarmHelper alarmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        finalAlarmTxtView = (TextView) findViewById(R.id.finalAlarmTxtView);
        beforeMinTxtView = (TextView) findViewById(R.id.beforeMinTxtView);
        intervalMinTxtView = (TextView) findViewById(R.id.intervalMinTxtView);

        displaySetAlarm();
    }

    public void onStart() {
        super.onStart();
    }

    private void displaySetAlarm() {
        Alarm setAlarm = alarmHelper.getPrevSetAlarm(this);

        if ((setAlarm.finalAlarmTimeMillis == -1) || (setAlarm.beforeMin == -1) || (setAlarm.intervalMin == -1)) {
            finalAlarmTxtView.setText("Not set");
            beforeMinTxtView.setText("Not set");
            intervalMinTxtView.setText("Not set");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(setAlarm.finalAlarmTimeMillis);

            finalAlarmTxtView.setText(String.format("%d:%02d", calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)));

            beforeMinTxtView.setText(setAlarm.beforeMin + " mins");
            intervalMinTxtView.setText(setAlarm.intervalMin + " mins");
        }
    }

    public void setAlarmClicked(View view) {
        Intent intent = new Intent(this, SetAlarmActivity.class);
        startActivityForResult(intent, SET_ALARM);
    }

    public void cancelAlarmClicked(View view) {
        alarmHelper.cancelAlarm(this);
        alarmHelper.deleteAlarmData(this);
        displaySetAlarm();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SET_ALARM) {
            int final_hour = data.getExtras().getInt("FinalHour", -1);
            int final_min = data.getExtras().getInt("FinalMin", -1);
            int before_min = data.getExtras().getInt("BeforeMin", -1);
            int interval_min = data.getExtras().getInt("IntervalMin", -1);

            alarmHelper.addAlarm(this, final_hour, final_min, before_min, interval_min);
            alarmHelper.setNextAlarmEvent(this);
            displaySetAlarm();
        }
    }
}