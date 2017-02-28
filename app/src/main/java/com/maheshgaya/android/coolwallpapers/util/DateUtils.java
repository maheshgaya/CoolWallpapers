package com.maheshgaya.android.coolwallpapers.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class DateUtils {

    private static final String TAG = DateUtils.class.getSimpleName();

    /**
     * Get the date in Feb 08, 2017
     * @param date
     * @return
     */
    public static String getFriendlyDate(String date){
        DateFormat originalFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            return dateFormat.format(originalFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date;
        }

    }

    /**
     * Get the current date in 02/08/2017
     * @return
     */
    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        return dateFormat.format(Calendar.getInstance().getTime());
    }


}
