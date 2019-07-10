package com.elrancho.pwi.pwi_app.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utils {

    private static Utils mInstance;

    private Utils() {
    }

    public static synchronized Utils getInstance() {
        if (mInstance == null)
            mInstance = new Utils();
        return mInstance;
    }

    public String getCurrentWeekEndDate() throws ParseException {

        // Current time from midnight of the current day
        // 7:00 am = 46800000 in milliseconds -- always ad 6 hours of milliseconds to get to the time you need
        long currentTime = getCurrentTimeInMilliseconds();

        Calendar calendar = GregorianCalendar.getInstance(Locale.US);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

//        add this condition to allow the managers to do inventory on Sunday as well. this condition should dbe removed if they decided to close inventory by Saturday midnight.
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && currentTime<64800000 ) {
            calendar.add(calendar.DATE, -1);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.add(calendar.DATE, 6);

        }

        String currentWeekDate = df.format(calendar.getTime());

        return currentWeekDate;
    }

    public long getCurrentTimeInMilliseconds() throws ParseException {


        long millis = System.currentTimeMillis() % 86400000;
        return millis;
    }

    public String convertToDepartmentName(String department) {


        String returnValue;
        switch (department) {
            case "202":
                returnValue = "Cremeria";
                break;
            case "200":
                returnValue = "Meat";
                break;
            case "300":
                returnValue = "Seafood";
                break;
            case "400":
                returnValue = "Produce";
                break;
            case "500":
                returnValue = "Bakery";
                break;
            case "501":
                returnValue = "Cake";
                break;
            case "600":
                returnValue = "Taqueria";
                break;
            case "601":
                returnValue = "Polleria";
                break;
            case "602":
                returnValue = "Fritura";
                break;
            case "605":
                returnValue = "Palapa";
                break;
            case "700":
                returnValue = "Tortilleria";
                break;
            default:
                returnValue = "null";
                break;
        }

        return returnValue;
    }
}
