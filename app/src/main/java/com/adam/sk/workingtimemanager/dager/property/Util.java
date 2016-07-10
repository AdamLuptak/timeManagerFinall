package com.adam.sk.workingtimemanager.dager.property;

import com.adam.sk.workingtimemanager.entity.Property;
import com.orm.query.Select;

import java.io.IOException;

public class Util {

    public static final String DEFAULT_WORK_PERIOD = "30600000";
    private static Property property;

    public static String getProperty() throws IOException {
        String propertyString = DEFAULT_WORK_PERIOD;
        Property property = Select.from(Property.class)
                .first();
        if (!(property == null)) {
            propertyString = String.valueOf(property.getWorkTimePeriod());
        }
        return propertyString;
    }

    public static void setProperty(Long workTimeLong) throws IOException {
        Property propertyNull = Select.from(Property.class)
                .first();
        if (propertyNull == null) {
            workTimeLong = (workTimeLong == null) ? 30600000l : workTimeLong;
            property = new Property(workTimeLong);
            property.save();
        } else {
            property = new Property(workTimeLong);
            property.update();
        }
    }
}