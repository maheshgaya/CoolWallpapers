package com.maheshgaya.android.coolwallpapers.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.main.MainActivity;
import com.maheshgaya.android.coolwallpapers.ui.post.PostActivity;

/**
 * Created by Mahesh Gaya on 2/27/17.
 */

public class ImageAppWidgetIntentService extends IntentService {
    public ImageAppWidgetIntentService() {
        super("ImageAppWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                ImageAppWidgetProvider.class));

        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.image_appwidget;

            RemoteViews remoteViews = new RemoteViews(getPackageName(), layoutId);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingLaunchIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_home_imagebutton, pendingLaunchIntent);
            remoteViews.setOnClickPendingIntent(R.id.widget_logo_imagebutton, pendingLaunchIntent);

            Intent searchIntent = new Intent(this, MainActivity.class);
            searchIntent.putExtra(MainActivity.PENDING_KEY, R.id.menu_search);
            PendingIntent pendingSearchIntent = PendingIntent.getActivity(this, 1, searchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_search_imagebutton, pendingSearchIntent);

            Intent userIntent = new Intent(this, MainActivity.class);
            userIntent.putExtra(MainActivity.PENDING_KEY, R.id.menu_profile);
            PendingIntent pendingUserIntent = PendingIntent.getActivity(this, 2, userIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_profile_imagebutton, pendingUserIntent);


            //Create an Intent to launch Add New Post
            Intent postIntent = new Intent(this, PostActivity.class);
            PendingIntent pendingPostIntent = PendingIntent.getActivity(this, 0, postIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_add_imagebutton, pendingPostIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }
}
