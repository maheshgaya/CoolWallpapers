package com.maheshgaya.android.coolwallpapers.service;

import android.app.IntentService;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Mahesh Gaya on 2/21/17.
 */

public class SetWallpaperIntentService extends IntentService {
    public static final String WALLPAPER_EXTRA = "wallpaper_extra";

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
        if (intent.getStringExtra(WALLPAPER_EXTRA) != null) {
            try {
                URL url = new URL(intent.getStringExtra(WALLPAPER_EXTRA));
                //reads the assets from the input stream
                try {
                    InputStream assetInputStream = url.openConnection().getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(assetInputStream);
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    try {
                        wallpaperManager.setBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }
}
