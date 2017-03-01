package com.maheshgaya.android.coolwallpapers.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Mahesh Gaya on 2/27/17.
 */
@Deprecated
public class ImageAppWidgetService extends RemoteViewsService{
    private static final String TAG = ImageAppWidgetService.class.getSimpleName();
    private ArrayList<Post> mPostList;
    private int mAppWidgetId;
    private AppWidgetManager mAppWidgetManager;
    private HashMap<Post, Bitmap> mBitmaps;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        //refresh content
        mPostList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Post.TABLE_NAME);
        databaseReference.keepSynced(true);
        Query postQuery = databaseReference.orderByKey().limitToLast(6);
        postQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mPostList != null && mPostList.size() != 0) {
                    mPostList.clear();
                }

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    mPostList.add(new Post(
                            postSnapshot.child(Post.COLUMN_UID).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_TITLE).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_IMAGE_URL).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_DATE).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_DESCRIPTION).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_CATEGORY).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_TAGS).getValue().toString(),
                            postSnapshot.child(Post.COLUMN_LOCATION).getValue().toString()));
                }
                //reverse chronological order, Firebase does not provide reverse order query
                Collections.reverse(mPostList);
                AsyncTask loadBitmaps = new LoadBitmapAsyncTask();
                loadBitmaps.execute();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return(new ImageAppWidgetViewFactory(this.getApplicationContext(),
                intent));
    }

    class ImageAppWidgetViewFactory implements RemoteViewsService.RemoteViewsFactory{
        private Context mContext;
        public ImageAppWidgetViewFactory(Context context, Intent intent) {
            mContext = context;
            ComponentName appWidget = new ComponentName(context.getPackageName(), ImageAppWidgetProvider.class.getName());
            mAppWidgetManager = AppWidgetManager.getInstance(context);
            mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            onDataSetChanged();
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            //get count
            if (mPostList != null) {
                Log.d(TAG, "getCount: " + mPostList.size());
                return mPostList.size();
            }
            Log.d(TAG, "getCount: " + 0);
            return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.d(TAG, "getViewAt: " + position);
            //implement views
            RemoteViews itemRemoteViews = new RemoteViews(mContext.getPackageName(), R.layout.item_appwidget);
            try {
                Post post = mPostList.get(position);
                if (mBitmaps != null) {
                    //TODO: Fix images is lost after a certain time
                    Log.d(TAG, "getViewAt: " + post.getImageUrl());
                    itemRemoteViews.setImageViewBitmap(R.id.thumbnail_image_view, mBitmaps.get(post));

                }
                Bundle extras = new Bundle();
                extras.putString(ImageAppWidgetProvider.EXTRA_ITEM, post.getImageUrl());
                Intent fillInIntent = new Intent();
                fillInIntent.putExtras(extras);
                itemRemoteViews.setOnClickFillInIntent(R.id.app_widget_item, fillInIntent);
            } catch (java.lang.IndexOutOfBoundsException e){
                e.printStackTrace();
            }
            return itemRemoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    class LoadBitmapAsyncTask extends AsyncTask<Object, Void, Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId, R.id.widget_gridview);
        }

        @Override
        protected Void doInBackground(Object... params) {
            mBitmaps = new HashMap<>();
            if (mPostList == null){
                Log.d(TAG, "doInBackground: mPostList is null");
                return null;
            }
            Log.d(TAG, "doInBackground: " + mPostList.size());
            for (Post post: mPostList) {
                Bitmap bitmap;
                InputStream inputStream;
                BufferedInputStream bufferedInputStream;
                try {
                    URL aURL = new URL(post.getImageUrl());
                    URLConnection connection = aURL.openConnection();
                    connection.setUseCaches(true);
                    connection.connect();
                    inputStream = connection.getInputStream();
                    bufferedInputStream = new BufferedInputStream(inputStream);
                    bitmap = BitmapFactory.decodeStream(bufferedInputStream);
                    mBitmaps.put(post, bitmap);
                    //release resources
                    bitmap.recycle();
                    bufferedInputStream.close();
                    inputStream.close();

                } catch (IOException e) {
                    Log.e(TAG, "Error getting bitmap", e);
                } catch (java.lang.OutOfMemoryError e){
                    Log.e(TAG, "Out of Memory: ", e);
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

    }
}
