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
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            return dateFormat.parse(date).toString();
        } catch (ParseException e) {
            Log.e(TAG, "getFriendlyDate: ", e);
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get the current date in 02/08/2017
     * @return
     */
    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

}
