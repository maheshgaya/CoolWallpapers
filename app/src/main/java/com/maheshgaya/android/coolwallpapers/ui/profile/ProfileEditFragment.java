package com.maheshgaya.android.coolwallpapers.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.User;
import com.maheshgaya.android.coolwallpapers.util.DateUtils;
import com.maheshgaya.android.coolwallpapers.util.FragmentUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mahesh Gaya on 2/26/17.
 */

public class ProfileEditFragment extends Fragment {
    private static final String TAG = ProfileEditFragment.class.getSimpleName();
    public static final String EDIT_EXTRA = "profile_edit";
    private static final int RC_PHOTO_PICKER = 1920;
    @BindView(R.id.toolbar)Toolbar mToolbar;
    @BindView(R.id.profile_edit_image_view)ImageView mImageView;
    @BindView(R.id.name_edittext)EditText mNameEditText;
    @BindView(R.id.profile_edit_coordinator_layout)CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.profile_edit_upload_photo_button)Button mUploadButton;
    private User mUser;
    private Uri mSelectedImageUri;
    Map<String, Object> userUpdate;

    public ProfileEditFragment(){
        setHasOptionsMenu(true);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        ButterKnife.bind(this, rootView);
        if (getActivity().getIntent().getParcelableExtra(EDIT_EXTRA) != null){
            mUser = getActivity().getIntent().getParcelableExtra(EDIT_EXTRA);
            FragmentUtils.getToolbar(getContext(), mToolbar, false);
            Glide.with(getContext())
                    .load(mUser.getImageUrl())
                    .error(R.drawable.ic_user_profile)
                    .into(mImageView);
            userUpdate = new HashMap<>();
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addImage();
                }
            });
            mNameEditText.setText(mUser.getName());
            mNameEditText.setSelection(mNameEditText.getText().length());
            mUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addImage();
                }
            });
        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_edit_menu, menu);
    }

    public void addImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:{
                String name = mNameEditText.getText().toString();
                if (name.equals("")){
                    Toast.makeText(getContext(), getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (name.replaceAll("\\s+", "").equals("")){
                    Toast.makeText(getContext(), getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
                    return false;
                } else if (name.length() > 0){
                    mUser.setName(name);
                    userUpdate.put(User.COLUMN_NAME, name);
                    //only name is changed
                    if (mSelectedImageUri == null){
                        updateUserProfile();
                        return true;
                    }

                    //save new image
                    final StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference().child(mUser.getUid() + "_" +
                            getString(R.string.profile_pic_url)); //profile will always be the same url so that it can be overridden
                    storageReference.putFile(mSelectedImageUri)
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String imageUrl = (taskSnapshot.getDownloadUrl() != null) ?
                                            taskSnapshot.getDownloadUrl().toString() : "";
                                    userUpdate.put(User.COLUMN_IMAGE_URL, imageUrl);
                                    updateUserProfile();
                                }
                            })
                            .addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), getString(R.string.profile_edit_failed), Toast.LENGTH_SHORT).show();
                                }
                            });


                }
                return true;
            }
            case android.R.id.home:{
                getActivity().finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUserProfile(){
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference(User.TABLE_NAME + "/" + mUser.getUid());

        userRef.updateChildren(userUpdate);
        Toast.makeText(getContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            //Image Picker Activity
            mSelectedImageUri = data.getData();
            //update image view
            Glide.with(getContext())
                    .load(mSelectedImageUri)
                    .error(R.drawable.ic_image)
                    .into(mImageView);
        }
    }
}
