package com.maheshgaya.android.coolwallpapers.ui.post;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.maheshgaya.android.coolwallpapers.R;

import butterknife.ButterKnife;


/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class PostActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ButterKnife.bind(this);
    }
}
