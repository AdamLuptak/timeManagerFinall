package com.adam.sk.workingtimemanager.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.adam.sk.workingtimemanager.Main;
import com.adam.sk.workingtimemanager.controller.LocationController;
import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

public class LocationService extends Service implements LocationListener {

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 0;
    public static final String ACTION_ALARM_RECEIVER = "locationService";

    @Inject
    LocationController locationController;

    @Inject
    TimeController timeController;

    @Inject
    LocationManager locationManager;


    @Inject
    List<Location> allLocations;

    private boolean inWork = false;
    Location mLastLocation;


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        List<String> providers = locationManager.getAllProviders();
        boolean atLeastOneProviderEnabled = false;
        for (String provider : providers) {
            if (locationManager.isProviderEnabled(provider)) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return 0;
                    }
                    mLastLocation = new Location(provider);
                    locationManager.requestLocationUpdates(provider, LOCATION_INTERVAL, LOCATION_DISTANCE, this);
                    atLeastOneProviderEnabled = true;
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
        if (!atLeastOneProviderEnabled) {
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void locationUpdated(Location location) {
        allLocations.add(location);

        Log.e(TAG, "onLocationChanged: " + location);

        Location workLocation = locationController.loadLocation();

        double distance = workLocation.distanceTo(location);

        Log.e(TAG, String.valueOf(distance));

        double threshold = Double.valueOf(locationController.getThresholdDistance());

        if (distance < threshold) {
            if (!(inWork)) {
                timeController.saveWorkTime(new DateTime());
                Log.e(TAG, "Came to work");
                inWork = true;
            } else {
                Log.e(TAG, "You are in work");
            }
        } else {
            Log.e(TAG, "Leave work");
            if (inWork) {
                timeController.saveWorkTime(new DateTime());
            }
            inWork = false;
        }
        mLastLocation.set(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        locationUpdated(location);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        // initializeLocationManager();
        ((Main) getApplication()).getComponent().inject(this);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.removeUpdates(this);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

    }
}

