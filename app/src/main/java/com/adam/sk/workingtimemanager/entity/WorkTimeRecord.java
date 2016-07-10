package com.adam.sk.workingtimemanager.entity;

import com.orm.SugarRecord;
import com.orm.dsl.NotNull;
import com.orm.dsl.Unique;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class WorkTimeRecord extends SugarRecord implements Serializable {

    @NotNull
    @Unique
    private Date arrivalDate;

    private Date leaveDate;

    public WorkTimeRecord(){

    }

    public WorkTimeRecord(Date arrivalDate){
        this.arrivalDate = arrivalDate;
    }

    public WorkTimeRecord(Date arrivalDate, Date leaveDate) {
        this.arrivalDate = arrivalDate;
        this.leaveDate = leaveDate;
    }

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    @Override
    public String toString() {
        return "WorkTimeRecord{" +
                ", arrivalDate=" + arrivalDate +
                ", leaveDate=" + leaveDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkTimeRecord that = (WorkTimeRecord) o;

        if (arrivalDate != null ? !arrivalDate.equals(that.arrivalDate) : that.arrivalDate != null)
            return false;
        return leaveDate != null ? leaveDate.equals(that.leaveDate) : that.leaveDate == null;

    }

    @Override
    public int hashCode() {
        int result = arrivalDate != null ? arrivalDate.hashCode() : 0;
        result = 31 * result + (leaveDate != null ? leaveDate.hashCode() : 0);
        return result;
    }
}
