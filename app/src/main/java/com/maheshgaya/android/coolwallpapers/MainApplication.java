package com.maheshgaya.android.coolwallpapers;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mahesh Gaya on 2/15/17.
 */

public class MainApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

}
