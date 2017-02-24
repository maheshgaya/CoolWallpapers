package com.maheshgaya.android.coolwallpapers.ui.post;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.maheshgaya.android.coolwallpapers.Constants;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenActivity;
import com.maheshgaya.android.coolwallpapers.ui.main.MainActivity;
import com.maheshgaya.android.coolwallpapers.util.DateUtils;
import com.maheshgaya.android.coolwallpapers.util.DisplayUtils;
import com.maheshgaya.android.coolwallpapers.util.FragmentUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Mahesh Gaya on 2/8/17.
 */

public class PostFragment extends Fragment{
    /** logging purposes */
    private static final String TAG = PostFragment.class.getSimpleName();

    /** Request Codes */
    private static final int RC_PHOTO_PICKER = 500;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    /** For Firebase */
    private Uri mSelectedImageUri = null;
    private FirebaseUser mUser;
    private FirebaseDatabase mPostDatabase;
    private FirebaseStorage mStorage;

    /**
     * Views
     */
    @BindView(R.id.layout_post)CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.title_edittext) EditText mTitleEditText;
    @BindView(R.id.post_location_edittext)EditText mLocationEditText;
    @BindView(R.id.post_toolbar) Toolbar mToolbar;
    @BindView(R.id.post_add_image_fab) FloatingActionButton mAddImageFab;
    @BindView(R.id.post_imageview)ImageView mPhotoImageView;
    @BindView(R.id.post_description_edittext)EditText mDescriptionEditText;
    @BindView(R.id.post_category_spinner)Spinner mCategorySpinner;
    @BindView(R.id.post_tags_edittext)EditText mTagsEditText;

    private MenuItem mPostMenuItem;
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
        mToolbar = FragmentUtils.getToolbar(getContext(), mToolbar, false);

        mAddImageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedImageUri != null) {
                    Intent fullScreenIntent = new Intent(getActivity(), FullScreenActivity.class);
                    //For Full Screen
                    if (mSelectedImageUri != null) {
                        fullScreenIntent.setData(mSelectedImageUri);
                    }
                    startActivity(fullScreenIntent);
                }
            }
        });

        //Setup Autocomplete for location
        mLocationEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(getActivity());
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // Handle the error silently
                    Log.e(TAG, "onClick: ", e);
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Handle the error silently
                    Log.e(TAG, "onClick: ", e);
                }
            }
        });

        //add listener to enable or disable post button
        addListenerToInput();

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //get the references
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mPostDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

    }

    /**
     * Opens Image directory to get image
     */
    public void addImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
    }

    /**
     * Check if the user has entered  values in the required fields
     * @return
     */
    private boolean isInputEmpty(){
        return (mTitleEditText.getText().toString().equals("") ||
        mSelectedImageUri == null ||
        mCategorySpinner.getSelectedItemPosition() == 0);

    }

    /**
     * Listens for changes in EditText and ImageView
     * And enables or disables post button accordingly
     */
    private void addListenerToInput(){
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enablePostButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                enablePostButton();
            }
        });

        mTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enablePostButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                enablePostButton();
            }
        });

    }


    /**
     * creates the menu for Toolbar
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_menu, menu);
        mPostMenuItem = menu.findItem(R.id.action_post);
        mPostMenuItem.setEnabled(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_post:{
                //all the input fields have not been filled out, so just disable the button
                //save image and then save data
                final DatabaseReference postDBReference = mPostDatabase.getReference().child(Post.TABLE_NAME);
                if (mUser != null) {
                    //save image
                    final StorageReference storageReference = mStorage.getReference().child(mUser.getUid() + "_" +
                            DateUtils.getCurrentDate().replaceAll("\\s+|/+", ""));
                    storageReference.putFile(mSelectedImageUri)
                            .addOnSuccessListener(getActivity(), new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    //save data to database
                                    String imageUrl = (taskSnapshot.getDownloadUrl() != null) ?
                                            taskSnapshot.getDownloadUrl().toString() : "";

                                    postDBReference.push().setValue(new Post(mUser.getUid(),
                                            mTitleEditText.getText().toString(),
                                            imageUrl,
                                            DateUtils.getCurrentDate(), //todo save locale
                                            mDescriptionEditText.getText().toString(),
                                            mCategorySpinner.getSelectedItem().toString(),
                                            mTagsEditText.getText().toString(),
                                            mLocationEditText.getText().toString()));
                                    //finish activity
                                    getActivity().finish();

                                }
                            })
                            .addOnFailureListener(getActivity(), new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //show error SnackBar
                                    DisplayUtils.showSnackBar(mCoordinatorLayout,
                                            getString(R.string.error_could_not_post)).show();

                                }
                            });
                }

                return true;
            }
            case android.R.id.home:{
                //// TODO: test
                if (!isInputEmpty()) {
                    Log.d(TAG, "onOptionsItemSelected: " + isInputEmpty());
                    showAlertDialog();
                } else if (isInputEmpty()){
                    Log.d(TAG, "onOptionsItemSelected: " + isInputEmpty());
                    getActivity().finish();
                }
            }
            default:
                 return super.onOptionsItemSelected(item);
        }


    }

    private void showAlertDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(getString(R.string.discard_message));
        alertDialog.setMessage(getString(R.string.confirmation_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.no),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
        alertDialog.show();
        alertDialog.getWindow().setLayout(800, 500);
    }

    /**
     * enable post button if every required input is filled out
     */
    private void enablePostButton(){
        if (mPostMenuItem != null) {
            if (isInputEmpty()) {
                mPostMenuItem.setEnabled(false);
            } else if (!isInputEmpty()) {
                mPostMenuItem.setEnabled(true);
            }
        }
    }

    /**
     * Handles results from other activities
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            //Image Picker Activity
            mSelectedImageUri = data.getData();
            //update image view
            Glide.with(getContext())
                    .load(mSelectedImageUri)
                    .error(R.drawable.ic_image)
                    .into(mPhotoImageView);
            //enable post button if possible
            enablePostButton();

        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE){
            //Places API Autocomplete Activity
            if (resultCode == RESULT_OK) {
                //result is ok
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                try {
                    //show name, city, state and country only. No need to show full address
                    List<Address> address = geocoder.getFromLocationName(place.getName().toString(), 1);
                    String location = place.getName() + "\n" + address.get(0).getLocality()
                            + ", " + address.get(0).getAdminArea() + ", " + address.get(0).getCountryName();
                    mLocationEditText.setText(location);

                } catch (IOException e) {
                    //if error occurred with Geocoder, just show the place name
                    mLocationEditText.setText(place.getName());
                    e.printStackTrace();
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                //an error occurred
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // reset location
                mLocationEditText.setText("");
                Log.d(TAG, "onActivityResult: " + status.getStatus());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                mLocationEditText.setText("");
            }
        }
    }
}
