package de.taop.hskl.dynamicStackAdapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import de.taop.hskl.dynamicStackAdapter.helpers.ItemTouchHelperViewHolder;

/**
 * This ViewHolder functions as a normal ViewHolder and adds functionality
 * for resizing views by dragging a specific view around. You need
 * to implement your Version of this and pass it to the {@link DynamicStackBuilder}!
 * <b>Note:</b> Keep in mind that you still need to implement your custom {@link DynamicStackViewHolder}
 * like any other ViewHolder!
 *
 * @author Adrian Bernhart
 */
public abstract class DynamicStackViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    protected final DynamicStackAdapter adapter;

    public int originalWidth;
    public int originalHeight;
    public boolean isExpanded = false;
    boolean allowUserResize;

    private View dynamicItemResize;

    protected DynamicStackViewHolder(final View itemView, final DynamicStackAdapter adapter) {
        super(itemView);

        this.adapter = adapter;

        findCustomViews(itemView);
        dynamicItemResize = itemView.findViewById(adapter.resizeViewResourceID);
        allowUserResize = adapter.allowUserResize;

        //a resize area was specified
        if (dynamicItemResize != null) {

            dynamicItemResize.setOnTouchListener(new View.OnTouchListener() {
                float origY;
                float accumulatedHeight;

                @Override
                public boolean onTouch(View view, MotionEvent me) {
                    if (!allowUserResize)
                        return true;
                    if (me.getAction() == MotionEvent.ACTION_DOWN) {
                        origY = me.getY();

                        accumulatedHeight = 0f;
                        for (int i = 0; i < adapter.getItemCount(); i++) {
                            if (i != getAdapterPosition()) {
                                View item =  adapter.container.findViewHolderForAdapterPosition(i).itemView;
                                accumulatedHeight += item.getHeight();
                            }
                        }

                        adapter.callback.allowDrag = false;
                        adapter.callback.allowSwipe = false;

                    } else if (me.getAction() == MotionEvent.ACTION_UP) {
                        //dynamicItemResize.setX(originalButtonX);
                        adapter.callback.allowDrag = true;
                        adapter.callback.allowSwipe = true;
                    } else if (me.getAction() == MotionEvent.ACTION_MOVE) {

                        float heightDiff = origY - me.getY();

                        if ((itemView.getLayoutParams().height + heightDiff) > adapter.minHeightPX) {
                            if ((accumulatedHeight + itemView.getHeight() + heightDiff) <= adapter.container.getHeight()) {

                                itemView.getLayoutParams().height += heightDiff;
                                if (adapter.reverseStack) {
                                    //itemView.setY(itemView.getY() - heightDiff);

                                }
                                itemView.requestLayout();
                            } else {
                                itemView.getLayoutParams().height = (int) (adapter.container.getHeight() - accumulatedHeight);
                                itemView.requestLayout();
                            }

                            isExpanded = true;
                        } else {
                            itemView.getLayoutParams().height = adapter.minHeightPX;
                            isExpanded = false;
                        }

                        updateOnResize(DynamicStackViewHolder.this.getAdapterPosition(),
                                adapter.getItem(DynamicStackViewHolder.this.getAdapterPosition()),
                                calculateHeightPercentage());

                    }

                    return true;
                }


            });
        }
    }

    float calculateHeightPercentage() {
        return itemView.getLayoutParams().height / (float) adapter.container.getHeight();
    }

    /**
     * You need to assign all your views which you want to use from the specified item layout here!
     * E.g.: customView = itemView.findViewByID(R.id.myView);
     *
     * @param itemView the item view of the ViewHolder
     * @see DynamicStackBuilder#withItemLayoutID(int)
     */
    protected abstract void findCustomViews(View itemView);

    /**
     * You need to assign all your views which you want to use from the specified item layout here!
     * E.g.: customView = itemView.findViewByID(R.id.myView);
     *
     * @param position         the position of the ViewHolder
     * @param object           the object at the position
     * @param sizeChangeAmount the percentage [0.0,1.0] of change in size since the resizing started
     */
    protected abstract void updateOnResize(int position, Object object, float sizeChangeAmount);

    @Override
    public void onItemSelected() {
        //itemView.getBackground().setTint();
    }

    @Override
    public void onItemClear() {
        //itemView.setBackgroundColor(0);
    }
}