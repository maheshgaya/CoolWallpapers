package com.maheshgaya.android.coolwallpapers.ui.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.maheshgaya.android.coolwallpapers.R;

import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/26/17.
 */

public class ProfileEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        ButterKnife.bind(this);
    }
}
