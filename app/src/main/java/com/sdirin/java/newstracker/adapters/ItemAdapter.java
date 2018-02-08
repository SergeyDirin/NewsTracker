package com.sdirin.java.newstracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdirin.java.newstracker.R;
import com.sdirin.java.newstracker.activities.DetailActivity;
import com.sdirin.java.newstracker.data.model.Article;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 08.02.2018.
 */

public class ItemAdapter extends RecyclerViewCursorAdapter<MainViewHolder> {

    Context context;
    private int width;

    public ItemAdapter() {
        super(null);
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        width = parent.getWidth();
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_big, parent, false);
        return new MainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, Cursor cursor) {
        //In my case, I have a MyItem class that models the data in the cursor,
        //so I create a new instance of MyItem, and pass that on
        //to the ViewHolder to be bound and displayed.
        final Article article = Article.getFromCursor(cursor);

        holder.undoButton.setVisibility(View.INVISIBLE);
        holder.undoButton.setOnClickListener(null);
        holder.mainImage.setVisibility(View.VISIBLE);
        holder.title.setVisibility(View.VISIBLE);
        holder.author.setVisibility(View.VISIBLE);
        holder.date.setVisibility(View.VISIBLE);
        holder.description.setVisibility(View.VISIBLE);
        holder.by.setVisibility(View.VISIBLE);
        Picasso.with(context)
                .load(article.getUrlToImage())
                .resize(width, 0)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_placeholder)
                .into(holder.mainImage);

        holder.title.setText(article.getTitle());
        String author = article.getAuthor()+" ("+article.getSource().getName()+")";
        holder.author.setText(author);
        holder.date.setText(article.getPublishedAtString());
        if (article.isRead()) {
            holder.tvNew.setVisibility(View.GONE);
        } else {
            holder.tvNew.setVisibility(View.VISIBLE);
        }
        holder.description.setText(article.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDetailView(article);
            }
        });
    }

    private void startDetailView(Article article) {
        article.setRead(true);
        //presenter.setArticleRead(article);
        Intent intent = new Intent(context, DetailActivity.class);
        if (article.getUrl() == null) {
            Toast.makeText(context, "No link", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("NewsApp",article.getUrl());
        intent.putExtra("EXTRA_URL", article.getUrl());
        context.startActivity(intent);
    }

    @Override
    public void swapCursor(Cursor newCursor) {
        super.swapCursor(newCursor);
    }
}


class MainViewHolder extends RecyclerView.ViewHolder{

    public ImageView mainImage;
    public TextView title;
    public TextView author;
    public TextView date;
    public TextView description;
    public TextView by;
    public TextView tvNew;
    Button undoButton;

    public MainViewHolder(View itemView) {
        super(itemView);
        mainImage = itemView.findViewById(R.id.iv_main);
        title = itemView.findViewById(R.id.tv_name);
        author = itemView.findViewById(R.id.tv_author);
        date = itemView.findViewById(R.id.tv_date);
        description = itemView.findViewById(R.id.tv_description);
        by = itemView.findViewById(R.id.tv_by);
        tvNew = itemView.findViewById(R.id.tvNew);
        undoButton = itemView.findViewById(R.id.undoButton);
    }
}
