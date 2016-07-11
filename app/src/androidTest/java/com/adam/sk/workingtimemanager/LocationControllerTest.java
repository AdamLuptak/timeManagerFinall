package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.support.test.internal.util.AndroidRunnerParams;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityTestCase;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.LargeTest;

import com.adam.sk.workingtimemanager.controller.LocationController;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class LocationControllerTest extends ActivityTestCase {

    public static final double LATITUDE = 40.225000;
    public static final double LONGITUDE = 50.456400;

    Context context;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        context = getInstrumentation().getContext();

        assertNotNull(context);

    }

    @Test
    public void testSetLocation() {
        Location expLocation = new Location("dummyprovider");
        expLocation.setLatitude(LATITUDE);
        expLocation.setLongitude(LONGITUDE);

        LocationController locationController = new LocationController(context);

        locationController.saveLocation(String.valueOf(expLocation.getLongitude()),String.valueOf(expLocation.getLatitude()));

        Assert.assertEquals(expLocation.getLongitude(),locationController.loadLocation().getLongitude());
        Assert.assertEquals(expLocation.getLatitude(),locationController.loadLocation().getLatitude());
    }
}
