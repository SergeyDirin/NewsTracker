package com.sdirin.java.newstracker.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by User on 12.02.2018.
 */

public class DateFormater {

    public static String getNetworkString(Date date){
        String result = getFullString(date);
        return result.replace(' ','T')+"Z";//"2009-10-10T12:12:12Z";
    }

    public static Date parse(String date) throws ParseException {
        String prepare = date.replace('T',' ').replace("Z","");//"2009-10-10T12:12:12Z";
        SimpleDateFormat sdf;
        //check if already formated
        if (date.length() < 10){
            sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
        } else {
            sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }
        return sdf.parse(prepare);
    }

    public static String getFullString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static String getShortString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }
}
