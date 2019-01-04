package com.elrancho.pwi.pwi_app.shared;

import com.elrancho.pwi.pwi_app.api.InventoryCountDetailsRetrofit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utils {

    private static Utils mInstance;

    private Utils() {
    }

    public static synchronized Utils getInstance(){
        if(mInstance==null)
            mInstance = new Utils();
        return mInstance;
    }

    public String getCurrentWeekEndDate(){

        Calendar calendar = GregorianCalendar.getInstance(Locale.US);

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

//        add this condition to allow the managers to do inventory on Sunday as well. this condition should dbe removed if the they decided to close inventory by Saturday midnight.
        if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY){
            calendar.add(calendar.DATE, -1);
        }
        else{
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            calendar.add(calendar.DATE, 6);

        }

        String currentWeekDate = df.format(calendar.getTime());

        return currentWeekDate;
    }
}
