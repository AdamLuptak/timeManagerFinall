package com.adam.sk.workingtimemanager.entity;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Property extends SugarRecord implements Serializable {

    long workTimePeriod;

    public Property(){

    }

    public Property(long workTimePeriod) {
        this.workTimePeriod = workTimePeriod;
    }

    public long getWorkTimePeriod() {
        return workTimePeriod;
    }

    public void setWorkTimePeriod(long workTimePeriod) {
        this.workTimePeriod = workTimePeriod;
    }

    @Override
    public String toString() {
        return "Property{" +
                "workTimePeriod=" + workTimePeriod +
                '}';
    }
}
