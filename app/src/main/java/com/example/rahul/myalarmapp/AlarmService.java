package com.example.rahul.myalarmapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by rahul on 12/31/16.
 */
public class AlarmService extends Service {
    PowerManager.WakeLock partialWakeLock, screenDimWakeLock;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("AlarmService", "Started");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        partialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Alarm Service");
        screenDimWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "My Alarm Service");
        Log.d("AlarmService", "Acquired wake locks");
        partialWakeLock.acquire();
        screenDimWakeLock.acquire();
        AlarmReceiver.completeWakefulIntent(intent);
        Log.d("AlarmService", "Completed wakeful Intent");

        Intent myintent = new Intent(this, WakeUpActivity.class);
        myintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("AlarmService", "Starting WakeUpActivity");
        startActivity(myintent);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("AlarmService", "Releasing wake locks");
        screenDimWakeLock.release();
        partialWakeLock.release();
    }
}
