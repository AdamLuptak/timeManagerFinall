package com.adam.sk.workingtimemanager;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.adam.sk.workingtimemanager.controller.TimeController;
import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;
import com.annimon.stream.Stream;
import com.orm.query.Condition;
import com.orm.query.Select;

import junit.framework.Assert;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TimeControllerTest {

    public static final long ARRIVAL_DATE_MILLIS = 1451646000000l;
    public static final long LEAVE_DATE_MILLIS = 1451660400000l;
    public static final long WORK_PERIOD = 30600000l;
    public static final long FRIDAY_COME = 1452232800000l;
    private Date arrivalDate;
    private Date leaveDate;

    @Before
    public void mockDb() {

        /**
         * arrivalTimeMS = 1451890800000l , Mon Jan 04 2016 08:00:00
         * leaveTimeMs = 1451898000000l, Mon Jan 04 2016 10:00:00
         */
        Date arrvialDateMonday = new Date(1451890800000l);
        Date leaveDateMonday = new Date(1451898000000l);
        WorkTimeRecord monday = new WorkTimeRecord(arrvialDateMonday, leaveDateMonday);
        monday.save();


        /**
         * arrivalTimeMS = 1451898300000l , Mon Jan 04 2016 10:05:00
         * leaveTimeMs = 1451916000000l, Mon Jan 04 2016 15:00:00
         */
        Date arrvialDateMonday1 = new Date(1451898300000l);
        Date leaveDateMonday1 = new Date(1451916000000l);
        WorkTimeRecord monday1 = new WorkTimeRecord(arrvialDateMonday1, leaveDateMonday1);
        monday1.save();

        /**
         * arrivalTimeMS = 1451973600000l , Tue Jan 05 2016 07:00:00
         * leaveTimeMs = 1452002400000l, Tue Jan 05 2016 15:00:00
         */
        Date arrvialDateTuesday = new Date(1451973600000l);
        Date leaveDateTuesday = new Date(1452002400000l);
        WorkTimeRecord tuesday = new WorkTimeRecord(arrvialDateTuesday, leaveDateTuesday);
        tuesday.save();

        /**
         * arrivalTimeMS = 1452067200000l , Wed Jan 06 2016 09:00:00
         * leaveTimeMs = 1452103200000l, Wed Jan 06 2016 19:00:00
         */
        Date arrvialDateWednesday = new Date(1452067200000l);
        Date leaveDateWednesday = new Date(1452103200000l);
        WorkTimeRecord wednesday = new WorkTimeRecord(arrvialDateWednesday, leaveDateWednesday);
        wednesday.save();

        /**
         * arrivalTimeMS = 1452150000000l , Thu Jan 07 2016 08:00:00
         * leaveTimeMs = 1452178800000l, Thu Jan 07 2016 08:00:00
         */
        Date arrvialDateThursday = new Date(1452150000000l);
        Date leaveDateThursday = new Date(1452178800000l);
        WorkTimeRecord thursday = new WorkTimeRecord(arrvialDateThursday, leaveDateThursday);
        thursday.save();

        saveFriday();

    }

    private void saveFriday() {
        /**
         * arrivalTimeMS = 1452232800000l , Fri Jan 08 2016 07:00:00
         * leaveTimeMs = 1452178800000l, Thu Jan 07 2016 16:00:00
         */
        Date arrivalDateFriday = new Date(1452232800000l);
        //Date leaveDateFriday = new Date(1452178800000l);
        WorkTimeRecord friday = new WorkTimeRecord(arrivalDateFriday);
        friday.save();
    }

    @Test
    public void countOvertime() {
        //  - 00.30.00

        /**
         * Inserting fake Day
         */
        Date arrivalDateFriday = new Date(1454598000000l);
        Date leaveDateFriday = new Date(1454598000000l);
        WorkTimeRecord friday = new WorkTimeRecord(arrivalDateFriday, leaveDateFriday);
        //  friday.save();

        Long expectedOverTime = -3900000l;

        TimeController timeController = new TimeController();
        DateTime today = new DateTime(1452146400000l); //Thu Jan 07 2016 07:00:00
        Long actualWeekOverTime = WeekOverTime(today);

        timeController.calculateTime(new DateTime(FRIDAY_COME));
        String goHomeTime1 = timeController.getGoHomeTime();
        String goHomeTimeOv = timeController.getGoHomeTimeOv();
        String overTime = timeController.getOverTime();

        Assert.assertEquals("15:30", goHomeTime1);
        Assert.assertEquals("16:35", goHomeTimeOv);
        Assert.assertEquals("-01:05", overTime);

        Long actulaWeekOverTimeReal = timeController.getWeekOverTime(today);

        Assert.assertEquals(expectedOverTime, actulaWeekOverTimeReal);
        Assert.assertEquals(expectedOverTime, actualWeekOverTime);

        WorkTimeRecord fridayComToWork = getLastWorkTimeRecordNull(FRIDAY_COME);

        long goHomeTime = fridayComToWork.getArrivalDate().getTime() + WORK_PERIOD;
        long goHomeOv = goHomeTime - actualWeekOverTime;

        Assert.assertEquals(goHomeTime, 1452263400000l); //Fri Jan 08 2016 15:30:00
        Assert.assertEquals(goHomeOv, 1452267300000l); //Fri Jan 08 2016 16:35:00
    }

    private WorkTimeRecord getLastWorkTimeRecordNull(long fridayCome) {
        DateTime fridayComeDate = new DateTime(fridayCome).withHourOfDay(0).withSecondOfMinute(0);
        return Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").gt(fridayComeDate.toDate().getTime()), Condition.prop("leave_date").isNull()).groupBy("arrival_date").first();
    }

    long workTimePart = 0;

    private Long WeekOverTime(DateTime today) {
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
        return Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").gt(startOfWeek.toDate().getTime()), Condition.prop("arrival_date").lt(endOfWeek.toDate().getTime()), Condition.prop("leave_date").isNotNull()).groupBy("arrival_date").list();
    }

    @Test
    public void testSaveWorkTime() throws Exception {
        this.cleanDb();

        TimeController timeController = new TimeController();

        arrivalDate = new Date(ARRIVAL_DATE_MILLIS);
        WorkTimeRecord expWorkTimeRecord = new WorkTimeRecord(arrivalDate);
        timeController.saveWorkTime(new DateTime(ARRIVAL_DATE_MILLIS));

        WorkTimeRecord actualWorkTimeRecord = Select.from(WorkTimeRecord.class).where(Condition.prop("arrival_date").eq(arrivalDate.getTime())).first();

        Assert.assertEquals(expWorkTimeRecord, actualWorkTimeRecord);

        leaveDate = new Date(LEAVE_DATE_MILLIS);
        timeController.saveWorkTime(new DateTime(leaveDate));
        expWorkTimeRecord.setLeaveDate(leaveDate);

        WorkTimeRecord actualWorkTimeRecordLeaveTime = Select.from(WorkTimeRecord.class).where(Condition.prop("leave_date").eq(leaveDate.getTime())).first();

        Assert.assertEquals(expWorkTimeRecord, actualWorkTimeRecordLeaveTime);
    }

    @After
    public void cleanDb() {
        List<WorkTimeRecord> books = WorkTimeRecord.listAll(WorkTimeRecord.class);

        WorkTimeRecord.deleteAll(WorkTimeRecord.class);
    }
}