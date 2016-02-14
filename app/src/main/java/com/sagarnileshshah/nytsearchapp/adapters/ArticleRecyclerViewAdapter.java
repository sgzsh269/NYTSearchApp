package com.sagarnileshshah.nytsearchapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sagarnileshshah.nytsearchapp.R;
import com.sagarnileshshah.nytsearchapp.activities.ArticleActivity;
import com.sagarnileshshah.nytsearchapp.models.Article;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sshah on 2/13/16.
 */
public class ArticleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Article> mArticleList;
    private Context mContext;

    private final int WITH_THUMBNAIL = 0, WITHOUT_THUMBNAIL = 1;


    public static class ViewHolderWithThumbnail extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        private List<Article> mArticleList;
        private Context mContext;

        ViewHolderWithThumbnail(View itemView, Context context, List<Article> articleList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            mArticleList = articleList;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getLayoutPosition();
            Article article = mArticleList.get(position);
            Intent i = new Intent(mContext, ArticleActivity.class);
            i.putExtra("article", Parcels.wrap(article));
            mContext.startActivity(i);
        }
    }

    public static class ViewHolderWithOutThumbnail extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        private List<Article> mArticleList;
        private Context mContext;

        ViewHolderWithOutThumbnail(View itemView, Context context, List<Article> articleList) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            mArticleList = articleList;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            int position = getLayoutPosition();
            Article article = mArticleList.get(position);
            Intent i = new Intent(mContext, ArticleActivity.class);
            i.putExtra("article", Parcels.wrap(article));
            mContext.startActivity(i);
        }
    }

    public ArticleRecyclerViewAdapter(Context context, List<Article> articleList) {
        mArticleList = articleList;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case WITH_THUMBNAIL:
                View articleViewWithThumbnail = inflater.inflate(R.layout.item_article_with_thumbnail, parent, false);
                viewHolder = new ViewHolderWithThumbnail(articleViewWithThumbnail, mContext, mArticleList);
                break;
            case WITHOUT_THUMBNAIL:
                View articleViewWithOutThumbnail = inflater.inflate(R.layout.item_article_without_thumbnail, parent, false);
                viewHolder = new ViewHolderWithOutThumbnail(articleViewWithOutThumbnail, mContext, mArticleList);
                break;
            default:
                View articleView = inflater.inflate(R.layout.item_article_without_thumbnail, parent, false);
                viewHolder = new ViewHolderWithOutThumbnail(articleView, mContext, mArticleList);
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        Article article = mArticleList.get(position);

        switch (viewHolder.getItemViewType()) {
            case WITH_THUMBNAIL:
                configureViewHolderWithThumbnail((ViewHolderWithThumbnail) viewHolder, article);
                break;

            case WITHOUT_THUMBNAIL:
                configureViewHolderWithoutThumbnail((ViewHolderWithOutThumbnail) viewHolder, article);
                break;

            default:
                configureViewHolderWithoutThumbnail((ViewHolderWithOutThumbnail) viewHolder, article);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return mArticleList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Article article = mArticleList.get(position);
        if (article.getThumbNailUrl() != null & !article.getThumbNailUrl().isEmpty()){
            return WITH_THUMBNAIL;
        } else {
            return WITHOUT_THUMBNAIL;
        }
    }

    private void configureViewHolderWithoutThumbnail(ViewHolderWithOutThumbnail viewHolder, Article article){
        viewHolder.tvHeadline.setText(article.getHeadline());
    }

    private void configureViewHolderWithThumbnail(ViewHolderWithThumbnail viewHolder, Article article) {
        viewHolder.tvHeadline.setText(article.getHeadline());
        viewHolder.ivThumbnail.setImageResource(0);
        Glide.with(mContext).load(article.getThumbNailUrl()).error(R.drawable.ic_placeholder_image).placeholder(R.drawable.ic_placeholder_image).into(viewHolder.ivThumbnail);
    }
}
