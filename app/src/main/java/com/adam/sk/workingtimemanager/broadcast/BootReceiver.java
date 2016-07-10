package com.adam.sk.workingtimemanager.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.adam.sk.workingtimemanager.service.LocationService;
import com.adam.sk.workingtimemanager.service.UpdaterService;

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";
    public static final int UPDATE_SERVICE_ID = 1001;
    public static final int LOCATION_SERVICE_ID = 1002;
    public static final int ALARM_PERIOD = 5000;

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive()");
        setAlarmService(context);
    }

    private void setAlarmService(Context context) {
        startUpdateService(context, UPDATE_SERVICE_ID, new Intent(context, UpdaterService.class), UpdaterService.ACTION_ALARM_RECEIVER, ALARM_PERIOD);
        startUpdateService(context, LOCATION_SERVICE_ID, new Intent(context, LocationService.class), LocationService.ACTION_ALARM_RECEIVER, ALARM_PERIOD);
    }

    public void startUpdateService(Context context, int serviceID, Intent intent, String actionAlarmReceiver, int alarmPeriod) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent.setAction(actionAlarmReceiver);
        PendingIntent pendingIntent = PendingIntent.getService(context, serviceID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), alarmPeriod, pendingIntent);
    }
}
