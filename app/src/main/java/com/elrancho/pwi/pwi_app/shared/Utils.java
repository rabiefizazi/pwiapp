package com.elrancho.pwi.pwi_app.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

        // Current time
        double currentTime = getCurrentTimeInMilliseconds();

        Calendar calendar = GregorianCalendar.getInstance(Locale.US);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

//        add this condition to allow the managers to do inventory on Sunday as well. this condition should dbe removed if the they decided to close inventory by Saturday midnight.
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ) {
            calendar.add(calendar.DATE, -1);
        } else {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.add(calendar.DATE, 6);

        }

        String currentWeekDate = df.format(calendar.getTime());

        return currentWeekDate;
    }

    public long getCurrentTimeInMilliseconds() throws ParseException {

        String currentDate = DateFormat.getInstance().format(new Date());

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a", Locale.US);

        Date now = formatter.parse(currentDate);

        long returnValue = now.getTime();

        return returnValue;
    }

    public String convertToDepartmentName(String department) {


        String returnValue;
        switch (department) {
            case "108":
                returnValue = "Dairy";
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
                returnValue = "Kitchen";
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
