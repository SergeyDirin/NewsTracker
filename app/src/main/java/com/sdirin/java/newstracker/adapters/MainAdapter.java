package com.sdirin.java.newstracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.activities.DetailActivity;
import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.squareup.picasso.Picasso;

/**
 * Created by SDirin on 02-Jan-18.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private NewsResponse response;
    private Context context;
    private int width;

    public MainAdapter(NewsResponse response){
        this.response = response;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        width = parent.getWidth();
        View item = LayoutInflater.from(context).inflate(R.layout.list_item_big,parent,false);
        Picasso.with(context).setIndicatorsEnabled(true);
        return new MainViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        final Article article = response.getArticles().get(position);



        Picasso.with(context)
                .load(article.getUrlToImage())
                .resize(width, 0)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.mainImage);

        holder.title.setText(article.getTitle());
        holder.author.setText(article.getAuthor());
        holder.date.setText(article.getPublishedAtString());
        holder.description.setText(article.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailView(article);
            }
        });
    }

    private void startDetailView(Article article) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("EXTRA_URL", article.getUrl());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return response.getArticles().size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{

        public ImageView mainImage;
        public TextView title;
        public TextView author;
        public TextView date;
        public TextView description;

        public MainViewHolder(View itemView) {
            super(itemView);
            mainImage = itemView.findViewById(R.id.iv_main);
            title = itemView.findViewById(R.id.tv_title);
            author = itemView.findViewById(R.id.tv_author);
            date = itemView.findViewById(R.id.tv_date);
            description = itemView.findViewById(R.id.tv_description);
        }
    }
}
