package com.adam.sk.workingtimemanager.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

public class LocationController {

    public static final String SHARED_PREFERENCES_NAME = "com.osmand.settings";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String THRESHOLD = "threshold";
    public static final int DEF_VALUE_THRESHOLD = 20;
    public static final String PROVIDER = "dummyprovider";
    public static final String DEF_VALUE_LAT = "0.0";
    public static final String DEF_VALUE_LON = "0.0";

    //defualt threshol 20 m
    private int thresholdDistance = 20;
    private SharedPreferences prefs;

    public LocationController(Context thisContext) {
        prefs = thisContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
    }

    public void saveLocation(String lon, String lat) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LATITUDE, lat);
        editor.putString(LONGITUDE, lon);
        editor.commit();
    }

    public int getThresholdDistance() {
        return prefs.getInt(THRESHOLD, DEF_VALUE_THRESHOLD);
    }

    public void setThresholdDistance(int thresholdDistance) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(THRESHOLD, thresholdDistance);
        editor.commit();
        this.thresholdDistance = thresholdDistance;
    }

    public Location loadLocation() {
        double lat = Double.valueOf(prefs.getString(LATITUDE, DEF_VALUE_LAT));
        double lon = Double.valueOf(prefs.getString(LONGITUDE, DEF_VALUE_LON));

        Location location = new Location(PROVIDER);
        location.setLatitude(lat);
        location.setLongitude(lon);

        return location;
    }
}
