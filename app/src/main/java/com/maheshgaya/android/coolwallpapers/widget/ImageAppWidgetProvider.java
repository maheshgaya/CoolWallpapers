package com.maheshgaya.android.coolwallpapers.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Mahesh Gaya on 2/27/17.
 */

public class ImageAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = ImageAppWidgetProvider.class.getSimpleName();
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ImageAppWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, ImageAppWidgetIntentService.class));
    }
}
