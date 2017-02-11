package com.maheshgaya.android.coolwallpapers.ui.post;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.ui.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class PostFragment extends Fragment {
    private static final String TAG = PostFragment.class.getSimpleName();
    private static final int RC_PHOTO_PICKER = 500;
    private Uri mSelectedImageUri = null;

    /**
     * Views
     */
    @BindView(R.id.title_edittext) EditText mTitleEditText;
    @BindView(R.id.post_toolbar) Toolbar mToolbar;
    @BindView(R.id.post_add_image_fab) FloatingActionButton mAddImageFab;
    @BindView(R.id.post_imageview)ImageView mPhotoImageView;

    /**
     * Enable menus on toolbar
     */
    public PostFragment() {
        setHasOptionsMenu(true);
    }

    /**
     * initialize the views
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post, container, false);
        ButterKnife.bind(this, rootView);
        ((PostActivity) getActivity()).setSupportActionBar(mToolbar);
        ((PostActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((PostActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((PostActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAddImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

        return rootView;
    }


    public void addImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    /**
     * creates the menu for Toolbar
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu, menu);
        MenuItem postItem = menu.findItem(R.id.action_post);
        postItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mSelectedImageUri = data.getData();
            Glide.with(getContext())
                    .load(mSelectedImageUri)
                    .error(R.drawable.ic_image)
                    .into(mPhotoImageView);
        }
    }
}
