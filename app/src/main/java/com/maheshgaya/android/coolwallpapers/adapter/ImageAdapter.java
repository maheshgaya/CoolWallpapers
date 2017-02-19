package com.maheshgaya.android.coolwallpapers.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Post;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mahesh Gaya on 2/16/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Object>  mImageUriList;

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

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
                    Toast.makeText(mContext, post.getTitle() + " " + post.getDate(), Toast.LENGTH_SHORT).show();
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
