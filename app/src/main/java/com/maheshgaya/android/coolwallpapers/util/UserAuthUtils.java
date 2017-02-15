package com.maheshgaya.android.coolwallpapers.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maheshgaya.android.coolwallpapers.BuildConfig;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.main.MainActivity;

import java.util.Arrays;

/**
 * Created by Mahesh Gaya on 2/14/17.
 */

public class UserAuthUtils {
    public static FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void signOut(final Activity activity){
        if (activity instanceof MainActivity) {
            AuthUI.getInstance()
                    .signOut(activity)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Toast.makeText(activity.getApplicationContext(), activity.getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show();
                            requireLogin(activity);
                        }
                    });
        }
    }

    public static void requireLogin(Context context) {
        if (context instanceof MainActivity) {
            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
            if (status == ConnectionResult.API_UNAVAILABLE || status != ConnectionResult.SUCCESS) {
                //TODO replace small icon
                //If Google Play Services is not available, app should finish
                //Otherwise, there will be memory leaks
                //However, show the user that the app failed as a notification
                NotificationCompat.Builder notificationBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_mail_white_24dp) //icon for app
                                .setContentTitle(context.getString(R.string.google_play_services)) //subject
                                .setContentText(context.getString(R.string.no_google_play_services_available)) //text, one line view
                                .setDefaults(Notification.DEFAULT_ALL) //Allows vibrate
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(context.getString(R.string.no_google_play_services_available))) //text, multi-line view
                                .setAutoCancel(true); //cancels if user opens app

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(MainActivity.GOOGLE_API_NOTIFICATION_ID, notificationBuilder.build());

                if (Build.VERSION.SDK_INT >= 21) {
                    ((MainActivity) context).finishAndRemoveTask();
                } else {
                    ((MainActivity) context).finish();
                }
            }
            ((MainActivity) context).startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder()
                            .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .setTheme(R.style.AuthTheme)
                            .build(),
                    MainActivity.RC_SIGN_IN);
        }
    }

}
