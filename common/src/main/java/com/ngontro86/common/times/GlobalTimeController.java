package com.ngontro86.common.times;

import java.util.Date;

public class GlobalTimeController {

    private static TimeController timeController;

    public static Date newDate() {
        return new Date(getCurrentTimeMillis());
    }

    public static long getCurrentTimeMillis() {
        return timeController == null ? System.currentTimeMillis() : timeController.getCurrentTimeMillis();
    }

    public static void setTimeController(TimeController controller) {
        timeController = controller;
    }

}
