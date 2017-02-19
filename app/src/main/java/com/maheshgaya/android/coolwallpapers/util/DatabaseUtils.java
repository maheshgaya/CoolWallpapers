package com.maheshgaya.android.coolwallpapers.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.User;

import java.util.Map;

/**
 * Created by Mahesh Gaya on 2/14/17.
 */

public class DatabaseUtils {
    private static final String TAG = DatabaseUtils.class.getSimpleName();

    /**
     * Add user to database if the user does not already exists
     * This allows changes to the profile
     */
    public static void addUserToDatabase(){
        //add user to database if user is not already in database
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(User.TABLE_NAME);
        Query query = userRef.orderByKey().equalTo(UserAuthUtils.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if user is not in database, add the user
                if (dataSnapshot.getChildrenCount() == 0) {
                    String imageUrl = (UserAuthUtils.getCurrentUser().getPhotoUrl() != null) ? UserAuthUtils.getCurrentUser().getPhotoUrl().toString() : "";
                    String name = UserAuthUtils.getCurrentUser().getDisplayName();
                    userRef.child(UserAuthUtils.getCurrentUser().getUid()).setValue(
                            new User(UserAuthUtils.getCurrentUser().getUid(),
                                    (name == null)? "" : name, //handles bug with Firebase:10.0.1
                                    UserAuthUtils.getCurrentUser().getEmail(),
                                    imageUrl));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
