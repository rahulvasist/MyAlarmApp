package com.example.rahul.myalarmapp;

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
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;

public class WakeUpActivity extends AppCompatActivity {
    private MediaPlayer mPlayer;
    private CountDownTimer cntDwnTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int     timeout;
        float   volume;
        Uri     alarmUri;

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_wake_up);

        if (AlarmHelper.isFinalAlarm(this)) {
            AlarmHelper.deleteAlarmData(this);
            timeout = 2 * 60 * 1000;
            volume = 1.0f;
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        } else {
            timeout = 5 * 1000;
            volume = 0.05f;
            alarmUri = Uri.parse("android.resource://"+getPackageName()+"/raw/ocean_raw");
        }

        cntDwnTimer = new CountDownTimer(timeout, 1000) {
            public void onTick(long millisUntilFinished) {
                Log.d("MainActivity", "Tick");
            }

            public void onFinish() {
                Log.d("MainActivity", "Finish");
                stopAlarmAndCleanup();
            }
        };

        try {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setDataSource(this, alarmUri);
            mPlayer.setVolume(volume, volume);
            mPlayer.prepare();
            mPlayer.start();

            cntDwnTimer.start();
        } catch (IOException e) {
            Log.d("MainActivity", "Exception");
        }
    }

    public void stopAlarmService() {
        Intent intent = new Intent(this, AlarmService.class);
        stopService(intent);
    }

    public void stopAlarmAndCleanup() {
        mPlayer.stop();
        AlarmHelper.setNextAlarmEvent(WakeUpActivity.this);
        stopAlarmService();
        finish();
    }

    public void cancelAlarmBtnClicked(View view) {
        cntDwnTimer.cancel();
        stopAlarmAndCleanup();
    }
}
