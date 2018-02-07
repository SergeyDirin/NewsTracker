package com.sdirin.java.newstracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.sdirin.java.newstracker.presenters.MainPresenter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by SDirin on 02-Jan-18.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    List<String> itemsPendingRemoval;
    private MainPresenter presenter;
    private Context context;
    private int width;
    boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu

    RecyclerView mRecyclerView;

    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<String, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    public MainAdapter(MainPresenter presenter){
        this.presenter = presenter;
        itemsPendingRemoval = new ArrayList<>();
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
        final Article article = presenter.newsResponse.getArticles().get(position);

        if (itemsPendingRemoval.contains(article.getID())) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_material_dark));
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // user wants to undo the removal, let's cancel the pending task
                    Runnable pendingRemovalRunnable = pendingRunnables.get(article.getID());
                    pendingRunnables.remove(article.getID());
                    if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
                    itemsPendingRemoval.remove(itemsPendingRemoval.indexOf(article.getID()));
                    // this will rebind the row in "normal" state
                    notifyItemChanged(presenter.newsResponse.getPosition(article));
                }
            });
            holder.title.setText(article.getTitle());
            holder.mainImage.setVisibility(View.INVISIBLE);
            holder.title.setVisibility(View.INVISIBLE);
            holder.author.setVisibility(View.INVISIBLE);
            holder.date.setVisibility(View.INVISIBLE);
            holder.description.setVisibility(View.INVISIBLE);
            holder.by.setVisibility(View.INVISIBLE);
            holder.tvNew.setVisibility(View.INVISIBLE);
            holder.itemView.setOnClickListener(null);
        } else {
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
            holder.author.setText(article.getAuthor()+" ("+article.getSource().getName()+")");
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
    }

    private void startDetailView(Article article) {
        article.setRead(true);
        presenter.setArticleRead(article);
        Intent intent = new Intent(context, DetailActivity.class);
        if (article == null || article.getUrl() == null) {
            Toast.makeText(context, "No link", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("NewsApp",article.getUrl());
        intent.putExtra("EXTRA_URL", article.getUrl());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return presenter.newsResponse.getArticles().size();
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(final int position) {
        final Article article = presenter.newsResponse.getArticles().get(position);
        if (!itemsPendingRemoval.contains(article.getID())) {
            itemsPendingRemoval.add(article.getID());
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(presenter.newsResponse.getPosition(article));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(article.getID(), pendingRemovalRunnable);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(ContextCompat.getColor(context, R.color.primary_material_dark));
                xMark = ContextCompat.getDrawable(context, R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                MainAdapter testAdapter = (MainAdapter)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                boolean undoOn = isUndoOn();
                if (undoOn) {
                    pendingRemoval(swipedPosition);
                } else {
                    remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                //swiping left
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                if (dX > 0) {
                    //swiping right
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
                }
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                if (dX>0){
                    xMarkLeft = itemView.getLeft() + xMarkMargin;
                    xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                }
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                if (context != null) {
                    background = new ColorDrawable(ContextCompat.getColor(context, R.color.primary_material_dark));
                } else {
                    background = new ColorDrawable(Color.BLUE);
                }
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY() -
                                (int) context.getResources().getDimension(R.dimen.list_item_big_bottom_margin);
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom() -
                                (int) context.getResources().getDimension(R.dimen.list_item_big_bottom_margin);
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY() -
                                (int) context.getResources().getDimension(R.dimen.list_item_big_bottom_margin);
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    public void remove(int position) {
        final Article article = presenter.newsResponse.getArticles().get(position);
        if (itemsPendingRemoval.contains(article.getID())) {
            itemsPendingRemoval.remove(article.getID());
        }
        if (presenter.newsResponse.getArticles().contains(article)) {
            presenter.removeArticle(article);
            presenter.newsResponse.remove(article);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        final Article article = presenter.newsResponse.getArticles().get(position);
        return itemsPendingRemoval.contains(article.getID());
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{

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
}
