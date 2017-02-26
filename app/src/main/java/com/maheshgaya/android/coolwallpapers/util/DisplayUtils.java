package com.maheshgaya.android.coolwallpapers.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by Mahesh Gaya on 2/12/17.
 */

public class DisplayUtils {
    private static final String TAG = DisplayUtils.class.getSimpleName();

    public static Snackbar showSnackBar(View layout, String message){
        return showSnackBar(layout, message, -1);
    }

    public static Snackbar showSnackBar(View layout, String message,  int actionTextColor){
        Snackbar snackbar = Snackbar.make(layout, message, Snackbar.LENGTH_SHORT);
        if (actionTextColor > -1) {
            snackbar.setActionTextColor(actionTextColor);
        }
        return snackbar;
    }
}
