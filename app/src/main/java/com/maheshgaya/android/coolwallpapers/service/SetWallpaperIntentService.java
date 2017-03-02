package com.maheshgaya.android.coolwallpapers.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenActivity;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenFragment;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mahesh Gaya on 2/21/17.
 */

public class SetWallpaperIntentService extends IntentService {
    private static final String TAG = SetWallpaperIntentService.class.getSimpleName();
    public static final String WALLPAPER_EXTRA = "wallpaper_extra";
    private static final int NOTIFICATION_ID = 102;
    private static final String TITLE_KEY = "title";
    private static final String TEXT_KEY = "text";
    private Post mPost;


    public SetWallpaperIntentService(){
        super(SetWallpaperIntentService.class.getName());
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SetWallpaperIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Map<String, String> message = new HashMap<>();
        if (intent.getParcelableExtra(WALLPAPER_EXTRA) != null) {
            mPost = intent.getParcelableExtra(WALLPAPER_EXTRA);
            try {
                URL url = new URL(mPost.getImageUrl());
                //reads the assets from the input stream
                InputStream assetInputStream = url.openConnection().getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(assetInputStream);
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                wallpaperManager.setBitmap(bitmap);
                message.put(TITLE_KEY, getString(R.string.set_wallpaper_successful_friendly));
                message.put(TEXT_KEY, getString(R.string.set_wallpaper_successful));
            } catch (Exception e) {
                e.printStackTrace();
                message.put(TITLE_KEY, getString(R.string.set_wallpaper_failed_friendly));
                message.put(TEXT_KEY, getString(R.string.set_wallpaper_failed));
            }

        }
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getApplicationContext(), FullScreenActivity.class);
        resultIntent.putExtra(FullScreenFragment.POST_EXTRA, mPost);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(FullScreenActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(message.get(TITLE_KEY))
                        .setContentText(message.get(TEXT_KEY))
                        .setVibrate(new long[]{200});
        builder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
