package com.adam.sk.workingtimemanager.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.adam.sk.workingtimemanager.MainActivity;
import com.adam.sk.workingtimemanager.R;
import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

public class UpdaterService extends IntentService {
    public static final String ACTION_FOO = "com.example.aluptak.androidrobo.services.action.FOO";
    public static final String ACTION_BAZ = "com.example.aluptak.androidrobo.services.action.BAZ";
    public static final String ACTION_ALARM_RECEIVER = "updateService";
    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.example.aluptak.androidrobo.services.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.example.aluptak.androidrobo.services.extra.PARAM2";
    private static final String TAG = "UpdaterService";

    private boolean rob = false;
    private Handler mHandler;

    @Inject
    TimeController timeController;

    public UpdaterService() {
        super("UpdaterService");
        Log.d(TAG, "vytvoreny service");
    }

    @Override
    public void onCreate() {
        mHandler = new Handler();
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mHandler.post(() -> {

            Log.d(TAG, "idem zo servicu mal by som ist kazdych 15 sekund " + rob);
            DateTime timeNow = DateTime.now();
            timeController = new TimeController();
            timeController.calculateTime(timeNow);

            if (timeController.getGoHomeMillis() <= timeNow.getMillis()) {
                notification("Go home time", "Go home time");
            } else if (timeController.getGoHomeOvMillis() <= timeNow.getMillis()) {
                notification("Go home timeOv", "Go home time");
            }
        });
    }

    private void notification(String title, String text) {
        Intent intent = new Intent(this,
                MainActivity.class);
        PendingIntent intent2 = PendingIntent.getActivity(this, 0,
                intent, 0);
        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(UpdaterService.this)
                        .setSmallIcon(R.drawable.ic_query_builder_white_24px)
                        .setContentTitle(title)
                        .setContentIntent(intent2)
                        .setContentText(text);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification.build());
    }
}
