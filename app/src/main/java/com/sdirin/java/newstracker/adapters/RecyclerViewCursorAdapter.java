package com.sdirin.java.newstracker.adapters;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.sdirin.java.newstracker.data.database.DatabaseHandler.KEY_ID;

/**
 * Created by User on 08.02.2018.
 */

public abstract class RecyclerViewCursorAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    List<Integer> itemsPendingRemoval;
    protected Cursor mCursor;
    private boolean mDataValid;
    private int mRowIDColumn;

    RecyclerView mRecyclerView;

    boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu

    boolean canDelete = true;

    protected Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<Integer, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be

    Drawable background;
    Drawable xMark;
    int xMarkMargin;


    /// to change background use this:
//    background = new ColorDrawable(ContextCompat.getColor(, R.color.primary_material_dark));
//    xMark = ContextCompat.getDrawable(context, R.drawable.ic_clear_24dp);
//                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
//    xMarkMargin = (int) context.getResources().getDimension(R.dimen.ic_clear_margin);


    public RecyclerViewCursorAdapter(Cursor c) {
        setHasStableIds(true);
        swapCursor(c);
        background = new ColorDrawable(Color.RED);
        xMarkMargin = 14;

        itemsPendingRemoval = new ArrayList<>();
    }

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    abstract void onBindRemoveViewHolder(VH holder, Cursor mCursor);

    void onUndoPressed(Cursor cursor){
        // user wants to undo the removal, let's cancel the pending task
        int id = (int) getItemId(cursor.getPosition());
        Runnable pendingRemovalRunnable = pendingRunnables.get(id);
        pendingRunnables.remove(id);
        if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
        itemsPendingRemoval.removeAll(Arrays.asList(id));
        // this will rebind the row in "normal" state
        notifyItemChanged(cursor.getPosition());
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {

        if (!mDataValid) {
            throw new IllegalStateException("Cannot bind viewholder when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to bind viewholder");
        }

        if (itemsPendingRemoval.contains((int)getItemId(position))) {
            onBindRemoveViewHolder(holder, mCursor);
        } else {
            onBindViewHolder(holder, mCursor);
        }
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(final int position) {
        final int id = (int)getItemId(position);
        if (!itemsPendingRemoval.contains(id)) {
            itemsPendingRemoval.add(id);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    removePending(id);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(id, pendingRemovalRunnable);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        if (canDelete) {
            setUpItemTouchHelper();
            setUpAnimationDecoratorHelper();
        }
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            boolean initiated;

            private void init() {
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
                if (position < 0) {
                    return super.getSwipeDirs(recyclerView, viewHolder);
                }
                RecyclerViewCursorAdapter testAdapter = (RecyclerViewCursorAdapter)recyclerView.getAdapter();
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
                    remove((int)getItemId(swipedPosition));
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
                if (xMark != null) {
                    int itemHeight = itemView.getBottom() - itemView.getTop();
                    int intrinsicWidth = xMark.getIntrinsicWidth();
                    int intrinsicHeight = xMark.getIntrinsicWidth();

                    int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                    int xMarkRight = itemView.getRight() - xMarkMargin;
                    if (dX > 0) {
                        xMarkLeft = itemView.getLeft() + xMarkMargin;
                        xMarkRight = itemView.getLeft() + xMarkMargin + intrinsicWidth;
                    }
                    int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                    int xMarkBottom = xMarkTop + intrinsicHeight;
                    xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                    xMark.draw(c);
                }

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

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

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
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY() - xMarkMargin;
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom() - xMarkMargin;
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY() - xMarkMargin;
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

    void removePending(int dbId){
        if (itemsPendingRemoval.contains(dbId)) {
            itemsPendingRemoval.removeAll(Arrays.asList(dbId));
        }
        remove(dbId);
    }
    abstract void remove(int dbId);

    public boolean isPendingRemoval(int position) {
        return itemsPendingRemoval.contains(getItemId(position));
    }

    @Override
    public int getItemCount() {
        if (mDataValid) {
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (!mDataValid) {
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to get an item id");
        }

        return mCursor.getLong(mRowIDColumn);
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return;
        }

        if (newCursor != null) {
            mCursor = newCursor;
            mRowIDColumn = mCursor.getColumnIndexOrThrow(KEY_ID);
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
            mRowIDColumn = -1;
            mDataValid = false;
        }
    }
}
