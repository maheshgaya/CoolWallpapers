package com.maheshgaya.android.coolwallpapers.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.maheshgaya.android.coolwallpapers.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mahesh Gaya on 2/21/17.
 */

public class SetWallpaperIntentService extends IntentService {
    public static final String WALLPAPER_EXTRA = "wallpaper_extra";
    private static final int NOTIFICATION_ID = 102;
    private static final String TITLE_KEY = "title";
    private static final String TEXT_KEY = "text";


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
        if (intent.getStringExtra(WALLPAPER_EXTRA) != null) {
            try {
                URL url = new URL(intent.getStringExtra(WALLPAPER_EXTRA));
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

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(message.get(TITLE_KEY))
                        .setContentText(message.get(TEXT_KEY))
                        .setVibrate(new long[]{200});
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
