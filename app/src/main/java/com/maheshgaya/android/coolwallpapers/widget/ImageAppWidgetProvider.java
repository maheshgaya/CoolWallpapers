package com.maheshgaya.android.coolwallpapers.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenActivity;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenFragment;

/**
 * Created by Mahesh Gaya on 2/27/17.
 */

public class ImageAppWidgetProvider extends AppWidgetProvider {
    private static final String TAG = ImageAppWidgetProvider.class.getSimpleName();
    public static final String EXTRA_ITEM = "com.maheshgaya.android.coolwallpapers.widget.EXTRA_ITEM";
    public static final String CLICK_ACTION = "com.maheshgaya.android.coolwallpapers.widget.CLICK";
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, ImageAppWidgetIntentService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(CLICK_ACTION)){
            //opens Full Screen Activity
            final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent fullScreenIntent = new Intent(context, FullScreenActivity.class);
            fullScreenIntent.setData(Uri.parse(intent.getStringExtra(EXTRA_ITEM)));
            fullScreenIntent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fullScreenIntent);

        } else {
            context.startService(new Intent(context, ImageAppWidgetService.class));
        }
        context.startService(new Intent(context, ImageAppWidgetIntentService.class));
        super.onReceive(context, intent);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, ImageAppWidgetIntentService.class));
    }
}
