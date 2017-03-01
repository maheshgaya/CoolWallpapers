package com.maheshgaya.android.coolwallpapers.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
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

//            Intent remoteViewsIntent = new Intent(getApplicationContext(), ImageAppWidgetService.class);

            // Add the app widget ID to the intent extras.
//            remoteViewsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            remoteViewsIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews remoteViews = new RemoteViews(getPackageName(), layoutId);
            // Add the adapter to the RemoteViews
//            remoteViews.setRemoteAdapter(R.id.widget_gridview, remoteViewsIntent); //for gridlayout
            //empty list
//            remoteViews.setEmptyView(R.id.widget_gridview, R.layout.empty_widget);


            //set individual clicks
//            Intent fullScreenIntent = new Intent(getApplicationContext(), ImageAppWidgetProvider.class);
            // Set the action for the intent.
//            fullScreenIntent.setAction(ImageAppWidgetProvider.CLICK_ACTION);
//            fullScreenIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
//            PendingIntent fullScreenPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, fullScreenIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setPendingIntentTemplate(R.id.widget_gridview, fullScreenPendingIntent);


            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingLaunchIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_appname_text_view, pendingLaunchIntent);

            //Create an Intent to launch Add New Post
            Intent postIntent = new Intent(this, PostActivity.class);
            PendingIntent pendingPostIntent = PendingIntent.getActivity(this, 0, postIntent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_add_imagebutton, pendingPostIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }
}
