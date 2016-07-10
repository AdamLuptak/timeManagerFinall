package com.adam.sk.workingtimemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adam.sk.workingtimemanager.controller.LocationController;
import com.adam.sk.workingtimemanager.controller.TimeController;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;


    @Inject
    LocationController locationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        ((Main) this.getApplicationContext()).getComponent().inject(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        Intent intentEdit = workTimeCompletionProtection();

        saveLocationFromAnotherApp(intentEdit);
    }

    @NonNull
    private Intent workTimeCompletionProtection() {
        Intent intentEdit = this.getIntent();
        String inWorkBefore = intentEdit.getStringExtra("showRecords");
        if (inWorkBefore == null) {
            displayView(0);
        } else {
            displayView(Integer.valueOf(inWorkBefore));
        }
        return intentEdit;
    }

    private void saveLocationFromAnotherApp(Intent intentEdit) {
        String action = intentEdit.getAction();
        String type = intentEdit.getType();
        String text = "";

        if (Intent.ACTION_SEND.equals(action) && type != null && "text/plain".equals(type)) {
            text = intentEdit.getStringExtra(Intent.EXTRA_TEXT);
            if (text.indexOf("\n") >= 0) {
                String lat = text.substring(0, text.indexOf(","));
                String lon = text.substring(text.indexOf(",") + 1, text.indexOf("\n"));
                locationController.saveLocation(lon, lat);
                Log.e(TAG, "Save " + String.valueOf(lon) + " " + String.valueOf(lat));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            this.displayView(2);
            return true;
        }

        if (id == R.id.action_search) {
            Toast.makeText(getApplicationContext(), "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new WorkTimeRecordsFragment();
                title = getString(R.string.title_work_times);
                break;
            case 2:
                fragment = new Setup();
                title = getString(R.string.title_setup);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

}
