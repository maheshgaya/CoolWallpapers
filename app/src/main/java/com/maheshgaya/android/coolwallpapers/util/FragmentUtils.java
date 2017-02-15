package com.maheshgaya.android.coolwallpapers.util;

import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.main.HomeFragment;
import com.maheshgaya.android.coolwallpapers.ui.main.ProfileFragment;
import com.maheshgaya.android.coolwallpapers.ui.main.SearchFragment;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class FragmentUtils {
    public static int getBottomNavigationItemId(int currentFragmentId){
        switch (currentFragmentId){
            case R.id.menu_search:
                return 1;
            case R.id.menu_profile:
                return 2;
            default:
                return 0; //if nothing is selected, home is returned
        }
    }

    /**
     * return a fragment instance based on the MenuItemId passed
     * @param itemId
     * @return
     */
    public static Fragment selectFragment(int itemId) {
        switch (itemId){
            case R.id.menu_home:
                return new HomeFragment();
            case R.id.menu_search:
                return new SearchFragment();
            case R.id.menu_profile:
                return new ProfileFragment();
            default:
                return null;
        }
    }

}
