package com.ne0nx3r0.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {
    public static boolean inLast24Hours(Date startDate){
        long duration = new Date().getTime() - startDate.getTime();

        long diffHours = TimeUnit.MILLISECONDS.toHours(duration);

        return diffHours < 24;
    }
    
    public static String getTimeSinceString(Date then) {
        return DateTimeUtil.getTimeSinceCPString(then, new Date());
    }
    
    // source:
    // http://www.mkyong.com/java/java-time-elapsed-in-days-hours-minutes-seconds/
    public static String getTimeSinceCPString(Date then, Date now) {
        //milliseconds
        long different = now.getTime() - then.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli = daysInMilli * 30;
        long yearsInMilli = daysInMilli * 365;

        long elapsedYears = different / yearsInMilli;
        different = different % yearsInMilli;
        
        if(elapsedYears > 0) {
            return elapsedYears+" years ago!";
        }

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;
        
        if(elapsedMonths > 0) {
            return elapsedMonths+" months ago";
        }
        
        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        if(elapsedDays > 0) {
            return elapsedDays+" days ago";
        }

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        if(elapsedHours > 0) {
            return elapsedHours+" hours ago";
        }

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        if(elapsedMinutes > 0) {
            return elapsedMinutes+" minutes ago";
        }

        long elapsedSeconds = different / secondsInMilli;

        if(elapsedSeconds > 0) {
            return elapsedSeconds+" seconds ago";
        }
        
        return different+"ms ago - wow you checked this quickly.";
    }
}
