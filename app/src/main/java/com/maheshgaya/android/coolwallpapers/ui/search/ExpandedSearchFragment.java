package com.maheshgaya.android.coolwallpapers.ui.search;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.adapter.ImageAdapter;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.main.SearchFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/25/17.
 */

public class ExpandedSearchFragment extends Fragment {
    private static final String TAG = ExpandedSearchFragment.class.getSimpleName();
    public static final String CATEGORY_KEY = "category";
    @BindView(R.id.expanded_search_back_image_view)ImageView mExpandedSearchBackImageView;
    @BindView(R.id.expanded_search_cancel_button)ImageButton mExpandedSearchCancelImageButton;
    @BindView(R.id.expanded_search_edit_text)EditText mExpandedSearchEditText;
    @BindView(R.id.expanded_search_recycleview)RecyclerView mImageRecycleView;
    @BindView(R.id.expanded_search_content)FrameLayout mSearchContentFrameLayout;
    @BindView(R.id.empty_linearlayout)LinearLayout mEmptyLayout;
    @BindView(R.id.empty_image_view)ImageView mEmptyImageView;
    @BindView(R.id.empty_text_view)TextView mEmptyTextView;
    @BindView(R.id.empty_action_text_view)TextView mEmptyActionTextView;
    private ImageAdapter mImageAdapter;
    private ArrayList<Object> mImageList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expanded_search, container, false);
        ButterKnife.bind(this, rootView);

        if (getActivity().getIntent().getStringExtra(CATEGORY_KEY) != null){
            String category = getActivity().getIntent().getStringExtra(CATEGORY_KEY);
            showContent(true);
            mExpandedSearchEditText.setText(category);
            mExpandedSearchEditText.setSelection(mExpandedSearchEditText.getText().length());
            queryDatabase(new String[]{category, CATEGORY_KEY});
        }

        mExpandedSearchBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mExpandedSearchCancelImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedSearchEditText.setText("");
                mEmptyTextView.setText(getString(R.string.empty_search));
            }
        });

        mImageList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(getContext(), mImageList);
        mImageRecycleView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.grid_item_columns));
        mImageRecycleView.setLayoutManager(gridLayoutManager);
        mImageRecycleView.setAdapter(mImageAdapter);
        mImageRecycleView.setItemViewCacheSize(20);
        mImageRecycleView.setDrawingCacheEnabled(true);
        mImageRecycleView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mImageRecycleView.setPersistentDrawingCache(ViewGroup.PERSISTENT_SCROLLING_CACHE);

        //search is empty, so show empty view
        showContent(false);

        mExpandedSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                if (text.length() > 0){
                    showContent(true);
                    queryDatabase(new String[]{text.toString()});
                } else {
                    mImageList.clear();
                    mImageAdapter.notifyDataSetChanged();
                    showContent(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEmptyImageView.setImageResource(R.drawable.ic_big_search_dark);
        mEmptyTextView.setText(getString(R.string.empty_search));

        return rootView;
    }

    private void queryDatabase(String[] query){
        QueryAsyncTask queryAsyncTask = new QueryAsyncTask();
        queryAsyncTask.execute(query);
    }
    private void showContent(boolean value){
        if (value){
            mExpandedSearchCancelImageButton.setVisibility(View.VISIBLE);
            mEmptyLayout.setVisibility(View.GONE);
            mSearchContentFrameLayout.setVisibility(View.VISIBLE);
        } else {
            mExpandedSearchCancelImageButton.setVisibility(View.INVISIBLE);
            mSearchContentFrameLayout.setVisibility(View.GONE);
            mEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    public class QueryAsyncTask extends AsyncTask<String, Void, Void> {
        DatabaseReference mDatabaseReference;
        private ValueEventListener mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mImageList.size() != 0) {
                    mImageList.clear();
                }

                if (dataSnapshot.getChildrenCount() > 0){
                    showContent(true);
                } else {
                    showContent(false);
                    mEmptyTextView.setText(getString(R.string.empty_results));
                }
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    mImageList.add(new Post(
                            postSnapshot.child(Post.COLUMN_UID).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_TITLE).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_IMAGE_URL).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_DATE).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_DESCRIPTION).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_CATEGORY).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_TAGS).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_LOCATION).getValue().toString()));
                }
                mImageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        @Override
        protected void onCancelled() {
            mImageList.clear();
            mImageAdapter.notifyDataSetChanged();
            super.onCancelled();
        }


        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "doInBackground: " + params[0]);
            mDatabaseReference = FirebaseDatabase.getInstance().getReference(Post.TABLE_NAME);
            if (params.length > 1){
                Log.d(TAG, "doInBackground: " + params[1]);
                Query query = mDatabaseReference.orderByChild(Post.COLUMN_CATEGORY)
                        .equalTo(params[0]);
                query.addValueEventListener(mValueEventListener);
            } else {
                Query query = mDatabaseReference.orderByChild(Post.COLUMN_TITLE)
                        .endAt(params[0] + "\\uF8FF");
                query.addValueEventListener(mValueEventListener);
            }

            return null;
        }
    }
}
