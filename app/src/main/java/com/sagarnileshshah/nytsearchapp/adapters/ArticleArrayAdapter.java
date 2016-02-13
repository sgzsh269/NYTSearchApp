package com.sagarnileshshah.nytsearchapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sagarnileshshah.nytsearchapp.R;
import com.sagarnileshshah.nytsearchapp.models.Article;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sshah on 2/10/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> objects) {
        super(context, 0, objects);
    }

    public static class ViewHolder {

        @Bind(R.id.ivThumbnail)
        ImageView ivThumbnail;

        @Bind(R.id.tvHeadline)
        TextView tvHeadline;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Article article = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.item_article, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvHeadline.setText(article.getHeadline());

        viewHolder.ivThumbnail.setImageResource(0);

        Glide.with(getContext()).load(article.getThumbNailUrl()).error(R.drawable.ic_placeholder).placeholder(R.drawable.ic_placeholder).into(viewHolder.ivThumbnail);

        return convertView;
    }
}
