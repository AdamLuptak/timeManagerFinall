package com.adam.sk.workingtimemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.adam.sk.workingtimemanager.entity.WorkTimeRecord;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by adam on 19.3.2016.
 */
public class WorkTimeRecordListAdapter extends BaseAdapter {
    private List _productList;
    private Context _context;

    public WorkTimeRecordListAdapter(Context context, List products) {
        _context = context;
        _productList = products;
    }

    static class ViewHolder {
        protected TextView arrivalTime, leaveTime,
                dayOfWeek, workTime;
    }

    @Override
    public int getCount() {
        return _productList.size();
    }

    @Override
    public Object getItem(int position) {
        return _productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(_context);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.work_time_record_list_row, null);
            holder = new ViewHolder();
            holder.arrivalTime = (TextView) convertView
                    .findViewById(R.id.arrivalTime);
            holder.leaveTime = (TextView) convertView
                    .findViewById(R.id.leaveTime);
            holder.dayOfWeek = (TextView) convertView
                    .findViewById(R.id.dayOfWeek);
            holder.workTime = (TextView) convertView
                    .findViewById(R.id.workTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WorkTimeRecord workTimeRecord = (WorkTimeRecord) _productList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm.ss");
        SimpleDateFormat sdfday = new SimpleDateFormat("dd/MM");

        if (workTimeRecord != null) {
            try {
                holder.arrivalTime.setText(String.format(_context
                        .getString(R.string.list_product_code_format,
                                sdf.format(workTimeRecord.getArrivalDate()))));
                Calendar cal = Calendar.getInstance();
                cal.setTime(workTimeRecord.getArrivalDate());
                String dayString = this.getNameOfDayint(cal.get(Calendar.DAY_OF_WEEK));
                holder.dayOfWeek.setText(dayString + " " + sdfday.format(workTimeRecord.getArrivalDate()));
                if (workTimeRecord.getLeaveDate() != null) {
                    holder.leaveTime.setText(String
                            .format(_context.getString(
                                    R.string.list_product_description_format,
                                    sdf.format(workTimeRecord.getLeaveDate()))));
                    Long workedHours = workTimeRecord.getLeaveDate().getTime() - workTimeRecord.getArrivalDate().getTime();
                    holder.workTime.setText(sdf.format(new Date(workedHours - 3600000l)).toString());
                } else {
                    holder.workTime.setText("In work");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }

    private String getNameOfDayint(int dow) {
        switch (dow) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
        }
        return "##:##";
    }
}
