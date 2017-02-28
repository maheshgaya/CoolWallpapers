package com.maheshgaya.android.coolwallpapers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenActivity;
import com.maheshgaya.android.coolwallpapers.ui.image.FullScreenFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/16/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Object>  mImageUriList;
    private int mPosition = GridView.INVALID_POSITION;

    public ImageAdapter(Context context, ArrayList<Object> imageUriList){
        this.mContext = context;
        this.mImageUriList = imageUriList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_image, parent, false);
        return new ViewHolder(itemView);
    }

    public int getPosition(){
        return mPosition;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mImageUriList.get(position) instanceof  Post) {
            final Post post = (Post)mImageUriList.get(position);
            Glide.with(mContext)
                    .load(post.getImageUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.ic_image)
                    .into(holder.thumbnailImageView);
            holder.thumbnailTextView.setText(post.getTitle());
            holder.thumbnailCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = position;
                    Activity activity = (Activity)mContext;
                    Intent fullScreenIntent = new Intent(activity, FullScreenActivity.class);
                    //For Full Screen, intent for post
                    fullScreenIntent.putExtra(FullScreenFragment.POST_EXTRA, post);
                    ((Activity) mContext).startActivityForResult(fullScreenIntent, FullScreenFragment.FULL_IMAGE_CODE);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if (mImageUriList == null){
            return 0;
        }
        return mImageUriList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.thumbnail_image_view)ImageView thumbnailImageView;
        @BindView(R.id.thumbnail_title)TextView thumbnailTextView;
        @BindView(R.id.item_image_card_view)CardView thumbnailCardView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
