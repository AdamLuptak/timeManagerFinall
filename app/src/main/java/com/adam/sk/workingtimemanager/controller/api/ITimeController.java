package com.adam.sk.workingtimemanager.controller.api;

import org.joda.time.DateTime;

public interface ITimeController {

    void saveWorkTime(DateTime date);

    String getGoHomeTimeOv();

    String getGoHomeTime();

    String getOverTime();
}
