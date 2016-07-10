package com.adam.sk.workingtimemanager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.adam.sk.workingtimemanager.dager.WorkTimeComponent;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;
import com.android.datetimepicker.date.DatePickerDialog;
import com.android.datetimepicker.time.RadialPickerLayout;
import com.android.datetimepicker.time.TimePickerDialog;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.annotation.Resource;
import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkTimeRecordEditActivity extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATE_PATTERN = "MM.dd.yyyy";

    private TextView lblDate;
    private TextView lblTime;
    private Calendar calendar;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;

    @BindView(R.id.arriveOrLeave)
    TextView arriveOrLeave;

    @BindView(R.id.arrivalDate)
    TextView arriveDate;

    @BindView(R.id.arrivalTime)
    TextView arrivalTime;

    @BindView(R.id.checkBox)
    CheckBox checkBox;

    @BindView(R.id.leaveDate)
    TextView leaveDate;

    @BindView(R.id.leaveTime)
    TextView leaveTime;
    private WorkTimeRecord workTimeRecord;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_time_record_edit2);
        ButterKnife.bind(this);

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());
        timeFormat = new SimpleDateFormat(TIME_PATTERN, Locale.getDefault());

        lblTime = (TextView) findViewById(R.id.lblTime);

        Intent intentEdit = this.getIntent();
        workTimeRecord = (WorkTimeRecord) getIntent()
                .getSerializableExtra("WorkTimeRecord");

        update();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void update() {
        setDate(arriveDate, dateFormat.format(workTimeRecord.getArrivalDate()));
        setDate(arrivalTime, timeFormat.format(workTimeRecord.getArrivalDate()));
        if (workTimeRecord.getLeaveDate() == null) {
            setDate(leaveDate, DATE_PATTERN);
            setDate(leaveTime, TIME_PATTERN);
        } else {
            setDate(leaveDate, dateFormat.format(workTimeRecord.getLeaveDate()));
            setDate(leaveTime, timeFormat.format(workTimeRecord.getLeaveDate()));
        }
    }

    private void setDate(TextView view, String time) {
        view.setText(time);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDatePicker:
                DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show(getFragmentManager(), "datePicker");
                break;
            case R.id.btnTimePicker:
                TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show(getFragmentManager(), "timePicker");
                break;
            case R.id.arrivalTimeLayout:
                changeArrivalDateText();
                break;
            case R.id.leaveTimeLayout:
                changeArrivalDateText();
                break;
            case R.id.timeChange:
                changeArrivalDateText();
                break;
            case R.id.buttonCancel:
                goBack();
                break;
            case R.id.buttonOk:
                saveWorkTime();
                if (checkBox.isChecked() && workTimeRecord != null) {
                    deleteItem();
                }
                goBack();
                break;
        }
    }

    private void deleteItem() {
        WorkTimeRecord workTimeRecord = Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").eq(this.workTimeRecord.getArrivalDate().getTime())).first();
        workTimeRecord.delete();
    }

    private void changeArrivalDateText() {
        if (arriveOrLeave.getText().equals("Arrival WorkTime")) {
            arriveOrLeave.setText(R.string.leaveSet);
        } else {
            arriveOrLeave.setText(R.string.arrivalSet);
        }
    }

    private void saveWorkTime() {
        workTimeRecord.update();
    }

    public void goBack() {
        Intent intent = new Intent(this,
                MainActivity.class);
        intent.putExtra("showRecords", "1");
        startActivityForResult(intent, 0);
        finish();
    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year, monthOfYear, dayOfMonth);
        if (arriveOrLeave.getText().equals("Arrival WorkTime")) {
            workTimeRecord.setArrivalDate(calendar.getTime());
        } else {
            workTimeRecord.setLeaveDate(calendar.getTime());
        }
        update();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if (arriveOrLeave.getText().equals("Arrival WorkTime")) {
            workTimeRecord.setArrivalDate(calendar.getTime());
        } else {
            workTimeRecord.setLeaveDate(calendar.getTime());
        }
        update();
    }

}
