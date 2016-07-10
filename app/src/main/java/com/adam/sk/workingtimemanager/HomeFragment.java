package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.dager.property.Util;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import at.markushi.ui.CircleButton;
import butterknife.BindView;
import butterknife.ButterKnife;


public class HomeFragment extends Fragment {
    public static final String DEFAULT_TIME_TEXT_VIEW = "00:00";
    Context thisContext;

    @BindView(R.id.label)
    TextView goHome;

    @BindView(R.id.textView3)
    TextView overTime;

    @BindView(R.id.textView4)
    TextView goHomeOv;

    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.stopLogo)
    ImageView logoStop;

    @Inject
    public TimeController timeController;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DateTime today = new DateTime();
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        thisContext = container.getContext();
        ButterKnife.bind(this, rootView);

        ((Main) thisContext.getApplicationContext()).getComponent().inject(this);

        final Animation animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_rotate);
        CircleButton btnScale = (CircleButton) rootView.findViewById(R.id.button2);

        List<WorkTimeRecord> yesterdayFoCorection = timeController.getYesterdayFoCorection(today);

        if(!(yesterdayFoCorection.isEmpty())){
            ((MainActivity)getActivity()).displayView(1);
        }

        timeLogicShow(today);

        btnScale.setOnClickListener(v -> {
            v.startAnimation(animRotate);
            v.getResources();
            logo.setImageResource(R.drawable.ic_access_time_white_48dp);
            timeController.saveWorkTime(today);
            timeController.calculateTime(today);
            timeLogicShow(today);
        });

        String workTimePeriodString = null;
        try {
            workTimePeriodString = Util.getProperty();
        } catch (IOException e) {
            e.printStackTrace();
        }
        timeController.WORK_PERIOD = Long.valueOf(workTimePeriodString);
        // Inflate the layout for this fragment
        return rootView;
    }

    private void timeLogicShow(DateTime today) {
        WorkTimeRecord workTimeRecord = timeController.findWorkTimeForThisDay();

        if (!(workTimeRecord == null)) {
            Toast.makeText(thisContext.getApplicationContext(), "You are in work", Toast.LENGTH_LONG).show();
            logo.setImageResource(R.drawable.ic_date_range_white_48px);
            logoStop.setVisibility(View.VISIBLE);
            timeController.calculateTime(today);
            updateTimeUI();
        } else {
            logoStop.setVisibility(View.INVISIBLE);
            WorkTimeRecord lastWorkTimeRecordNull = timeController.getLastWorkTimeRecordNull(today.getMillis());
            if (lastWorkTimeRecordNull == null) {
                Toast.makeText(thisContext.getApplicationContext(), "Welcome new day", Toast.LENGTH_LONG).show();
                goHome.setText(DEFAULT_TIME_TEXT_VIEW);
                goHomeOv.setText(DEFAULT_TIME_TEXT_VIEW);
                overTime.setText(DEFAULT_TIME_TEXT_VIEW);
            } else {
                timeController.calculateTime(today);
                updateTimeUI();
            }
        }
    }

    private void updateTimeUI() {
        goHome.setText(timeController.getGoHomeTime());
        goHomeOv.setText(timeController.getGoHomeTimeOv());
        overTime.setText(timeController.getOverTime());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
