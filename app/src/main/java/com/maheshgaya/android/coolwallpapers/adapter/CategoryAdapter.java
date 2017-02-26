package com.maheshgaya.android.coolwallpapers.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maheshgaya.android.coolwallpapers.R;
import com.maheshgaya.android.coolwallpapers.data.Category;
import com.maheshgaya.android.coolwallpapers.loader.CategoryAsyncTaskLoader;
import com.maheshgaya.android.coolwallpapers.ui.search.ExpandedSearchActivity;
import com.maheshgaya.android.coolwallpapers.ui.search.ExpandedSearchFragment;

import java.util.ArrayList;

/**
 * Created by Mahesh Gaya on 2/19/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>  {
    private static final int HEADER = 0;
    private static final int CONTENT = 1;

    private ArrayList<Category> mCategoryList;
    private Context mContext;

    public CategoryAdapter(Context context, ArrayList<Category> categoryList){
        this.mContext = context;
        this.mCategoryList = categoryList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutInt = 0;
        switch (viewType){
            case HEADER:
                layoutInt = R.layout.general_text_view;
                break;
            case CONTENT:
                layoutInt = R.layout.item_category;
                break;

        }
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(layoutInt, parent, false);
        return new CategoryAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Category category = mCategoryList.get(position);
        if (category.getDrawableInt() != CategoryAsyncTaskLoader.HEADER_TITLE){
            final String name = category.getName();
            holder.nameTextView.setText(category.getName());
            holder.backgroundImageView.setImageResource(category.getDrawableInt());
            holder.categoryCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent expandedSearchIntent = new Intent(mContext, ExpandedSearchActivity.class);
                    expandedSearchIntent.putExtra(ExpandedSearchFragment.CATEGORY_KEY, name);
                    mContext.startActivity(expandedSearchIntent);
                }
            });
        } else {
            holder.titleTextView.setText(category.getName());
        }
    }

    @Override
    public int getItemCount() {
        if (mCategoryList == null){
            return 0;
        }
        return mCategoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch(position) {
            case 0:
                return HEADER;
            default:
                return CONTENT;
        }
    }

    public void setList(ArrayList<Category> list){
        this.mCategoryList = list;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameTextView;
        TextView titleTextView;
        ImageView backgroundImageView;
        CardView categoryCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView)itemView.findViewById(R.id.category_title);
            titleTextView = (TextView)itemView.findViewById(R.id.general_title_text_view);
            backgroundImageView = (ImageView)itemView.findViewById(R.id.category_image_view);
            categoryCardView = (CardView)itemView.findViewById(R.id.item_category_card_view);
        }
    }
}
