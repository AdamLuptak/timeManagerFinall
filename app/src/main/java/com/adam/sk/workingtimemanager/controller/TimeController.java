package com.adam.sk.workingtimemanager.controller;

import android.util.Log;

import com.adam.sk.workingtimemanager.controller.api.ITimeController;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;
import com.annimon.stream.Stream;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeController implements ITimeController {

    public static final String NEW_WORK_TIME_MESSAGE = "create new workTime";
    public static final String UPDATE_WORK_TIME_MESSAGE = "update workTime";
    public static final long DEFAULT_GO_HOME_TIME = 0l;
    public static final String LEAVE_DATE = "leave_date";
    public static final String ARRIVAL_DATE = "arrival_date";
    public static final String HH_MM_FORMATTED = "HH:mm";
    public static final String MINUS_DEFAULT_STRING = "";
    public static final int DURATION_TO_ONE = 1;
    public static final String MINUS_SYMBOL = "-";
    public static long WORK_PERIOD = 30600000;

    public static final String TAG = "TimeController";

    private String overTime;

    private Long overTimeMillis;

    private Long goHomeOvMillis;

    private Long goHomeMillis;

    @Override
    public void saveWorkTime(DateTime date) {
        WorkTimeRecord lastWorkTimeRecord = findWorkTimeForThisDay();

        if (lastWorkTimeRecord == null) {
            Log.d(TAG, NEW_WORK_TIME_MESSAGE);
            WorkTimeRecord workTimeRecord = new WorkTimeRecord(date.toDate());
            workTimeRecord.save();

            calculateTime(date);

        } else {
            Log.d(TAG, UPDATE_WORK_TIME_MESSAGE);
            lastWorkTimeRecord.setLeaveDate(date.toDate());
            lastWorkTimeRecord.save();
        }
    }

    public void calculateTime(DateTime date) {
        this.overTimeMillis = getWeekOverTime(date);

        WorkTimeRecord lastComeToWork = getLastWorkTimeRecordNull(date.getMillis());

        if (lastComeToWork == null) {
            goHomeMillis = DEFAULT_GO_HOME_TIME;
            goHomeOvMillis = DEFAULT_GO_HOME_TIME;
        } else {
            goHomeMillis = lastComeToWork.getArrivalDate().getTime() + WORK_PERIOD;
            goHomeOvMillis = goHomeMillis - this.overTimeMillis;
        }
    }

    public WorkTimeRecord findWorkTimeForThisDay() {
        return Select.from(WorkTimeRecord.class).where(Condition.prop(LEAVE_DATE).isNull()).groupBy(ARRIVAL_DATE).first();
    }

    SimpleDateFormat formatter = new SimpleDateFormat(HH_MM_FORMATTED);

    @Override
    public String getGoHomeTimeOv() {
        return formatter.format(new DateTime(goHomeOvMillis).toDate()).toString();
    }

    @Override
    public String getGoHomeTime() {
        return formatter.format(new DateTime(goHomeMillis).toDate()).toString();
    }

    @Override
    public String getOverTime() {
        String minusSymbol = MINUS_DEFAULT_STRING;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(overTimeMillis) % TimeUnit.HOURS.toMinutes(DURATION_TO_ONE);
        long hours = TimeUnit.MILLISECONDS.toHours(overTimeMillis);
        if (hours <= 0 && minutes < 0) {
            minusSymbol += MINUS_SYMBOL;
        }
        return String.format(minusSymbol + "%02d:%02d", Math.abs(hours), Math.abs(minutes));
    }

    long workTimePart = 0;

    public WorkTimeRecord getLastWorkTimeRecordNull(long fridayCome) {
        DateTime fridayComeDate = new DateTime(fridayCome).withHourOfDay(0).withSecondOfMinute(0);
        return Select.from(WorkTimeRecord.class).where(Condition.prop(ARRIVAL_DATE).gt(fridayComeDate.toDate().getTime())).groupBy(ARRIVAL_DATE).first();
    }

    public Long getWeekOverTime(DateTime today) {
        DateTime startOfWeek = today.weekOfWeekyear().roundFloorCopy();
        DateTime endOfWeek = today.weekOfWeekyear().roundCeilingCopy();
        long workTime = 0;

        List<WorkTimeRecord> workTimeRecords = getWorkTimeRecordsRange(startOfWeek, endOfWeek);

        for (int dayOFWeek = 0; dayOFWeek < 7; dayOFWeek++) {
            workTimePart = 0;
            DateTime nextDayCal = startOfWeek.plusDays(dayOFWeek);
            Stream.of(workTimeRecords).filter(workTimeLambda -> new DateTime(workTimeLambda.getArrivalDate()).getDayOfMonth() == nextDayCal.getDayOfMonth()).forEach(w -> {
                workTimePart += w.getLeaveDate().getTime() - w.getArrivalDate().getTime();
            });
            workTime += (workTimePart != 0) ? workTimePart - WORK_PERIOD : 0;
        }
        return workTime;
    }

    private List<WorkTimeRecord> getWorkTimeRecordsRange(DateTime startOfWeek, DateTime endOfWeek) {
        return Select.from(WorkTimeRecord.class).where(Condition.prop(ARRIVAL_DATE).gt(startOfWeek.toDate().getTime()), Condition.prop(ARRIVAL_DATE).lt(endOfWeek.toDate().getTime()), Condition.prop(LEAVE_DATE).isNotNull()).groupBy(ARRIVAL_DATE).list();
    }

    public Long getOverTimeMillis() {
        return overTimeMillis;
    }

    public void setOverTimeMillis(Long overTimeMillis) {
        this.overTimeMillis = overTimeMillis;
    }

    public Long getGoHomeOvMillis() {
        return goHomeOvMillis;
    }

    public void setGoHomeOvMillis(Long goHomeOvMillis) {
        this.goHomeOvMillis = goHomeOvMillis;
    }

    public Long getGoHomeMillis() {
        return goHomeMillis;
    }

    public void setGoHomeMillis(Long goHomeMillis) {
        this.goHomeMillis = goHomeMillis;
    }

    public List<WorkTimeRecord> getYesterdayFoCorection(DateTime today) {
        DateTime yesterday = today.minusDays(1).withHourOfDay(23).withSecondOfMinute(59);
        return Select.from(WorkTimeRecord.class).where(Condition.prop(ARRIVAL_DATE).lt(yesterday.toDate().getTime()), Condition.prop(ARRIVAL_DATE).isNull()).list();
    }

}
