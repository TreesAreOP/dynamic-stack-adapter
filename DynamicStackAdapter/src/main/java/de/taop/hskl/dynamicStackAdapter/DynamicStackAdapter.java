package de.taop.hskl.dynamicStackAdapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.taop.hskl.dynamicStackAdapter.helpers.ItemTouchHelperAdapter;
import de.taop.hskl.dynamicStackAdapter.helpers.SimpleItemTouchHelperCallback;


/**
 * This adapter functions as a normal Adapter and adds functionality
 * for arranging all view in a stack and resizing them. You need
 * to implement your Version of this and pass it to the {@link DynamicStackBuilder}!
 *
 * @param <T>  defines the item type of the adapter
 * @param <VH> defines the ViewHolder type and represents your custom implemented {@link DynamicStackViewHolder}
 * @author Adrian Bernhart
 */
public abstract class DynamicStackAdapter<T, VH extends DynamicStackViewHolder> extends RecyclerView.Adapter<VH>
        implements ItemTouchHelperAdapter {

    RecyclerView container;
    SimpleItemTouchHelperCallback callback;
    int minHeightPX;
    int marginPixels;
    int maxItems;
    boolean autoResizeItems;
    int resizeViewResourceID;
    boolean allowUserResize;
    int itemLayout;
    boolean reverseStack;
    private ArrayList<T> dataSet;
    private double heightOfAllItems;
    private ArrayList<VH> expandedNotSuitableItems;
    private Class<? extends DynamicStackViewHolder> holderClass;
    private VH vh;

    public int getParentHeight() {
        return parentHeight;
    }

    public int getMinHeightPX() {
        return minHeightPX;
    }

    private int parentHeight;

    protected DynamicStackAdapter(final RecyclerView container, Class<VH> holderClass) {
        dataSet = new ArrayList<>();
        this.container = container;
        this.holderClass = holderClass;

        callback = new SimpleItemTouchHelperCallback();
        callback.setAdapter(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(container);

        expandedNotSuitableItems = new ArrayList<>();

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                parentHeight = (container.getHeight() - marginPixels);
                minHeightPX = (int) (parentHeight / (float) maxItems);
            }
        });
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);

        try {
            vh = (VH) holderClass.getConstructor(View.class, DynamicStackAdapter.class).newInstance(view, this);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        vh.itemView.getLayoutParams().height = minHeightPX;
        vh.itemView.setMinimumHeight(minHeightPX);

        withCreateViewHolder(vh);

        vh.setIsRecyclable(false);

        return vh;
    }

    /**
     * This Method will be called when creating a ViewHolder.
     *
     * @param vh the newly created ViewHolder
     */
    public abstract void withCreateViewHolder(final VH vh);

    /**
     * This Method will be called when binding a ViewHolder.
     *
     * @param vh               the ViewHolder to bind
     * @param position         the position of the item in the adapter
     * @param objectAtPosition the data object at the given position
     */
    public abstract void withBindViewHolder(final VH vh, final int position, final T objectAtPosition);

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        final T object = dataSet.get(position);

        final boolean wasSaved = DynamicStackSaveManager.positionWasSavedBefore(position);

        holder.itemView.post(new Runnable() {
            @Override
            public void run() {
                if (wasSaved) {
                    holder.updateOnResizeInternalWithPercentage(holder.getAdapterPosition(), object, DynamicStackSaveManager.getPercentageOfPosition(position).floatValue());
                } else {
                    holder.updateOnResizeInternal(holder.getAdapterPosition(), object);
                }
            }
        });

        withBindViewHolder(holder, position, object);

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    /**
     * Used to get the Item at a given Position.
     *
     * @param position the position of the item
     * @return the item at the given position
     */
    T getItem(int position) {
        return dataSet.get(position);
    }

    /**
     * Inserts an item to the Adapter.
     * <b>Note:</b> Always use this Method or {@link #addItem(Object)} to add items!
     *
     * @param item     the item to add
     * @param position the position at which the item should be inserted
     */
    public void insertItem(final T item, final int position) {
        container.post(new Runnable() {
            @Override
            public void run() {
                if ((autoResizeItems && fitNewItemAndAdjustHeight())
                        || (!autoResizeItems && fitNewItem())) {
                    DynamicStackSaveManager.removeSavedPosition(position);
                    dataSet.add(position, item);
                    notifyItemInserted(position);
                    notifyItemRangeChanged(position, getItemCount());
                }
            }
        });

    }

    /**
     * Adds an item to the Adapter.
     * <b>Note:</b> Always use this Method or {@link #insertItem(Object, int)} to add items!
     *
     * @param item the item to add
     */
    public void addItem(final T item) {

        container.post(new Runnable() {
            @Override
            public void run() {
                if ((autoResizeItems && fitNewItemAndAdjustHeight())
                        || (!autoResizeItems && fitNewItem())) {
                    DynamicStackSaveManager.removeSavedPosition(dataSet.size());
                    dataSet.add(item);
                    notifyItemInserted(dataSet.size() - 1);
                }
            }
        });
    }

    /**
     * Removes an item from the Adapter.
     * <b>Note:</b> Always use this Method or {@link #removeItemRange(int, int)} to remove items!
     *
     * @param adapterPosition the item to remove
     */
    public void removeItem(int adapterPosition) {
        dataSet.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
        DynamicStackSaveManager.removeSavedPosition(adapterPosition);
    }

    /**
     * Removes an item from the Adapter.
     * <b>Note:</b> Always use this Method or {@link #removeItem(int)} to remove items!
     *
     * @param startPosition the start position (inclusive)
     * @param endPosition   the end position (exclusive)
     */
    public void removeItemRange(int startPosition, int endPosition) {
        for (int i = startPosition; i < endPosition; i++) {
            removeItem(i);
            DynamicStackSaveManager.removeSavedPosition(i);
        }
    }

    /**
     * Gets the Dataset of this Adapter. Keep in mind
     * that a reference to the list is returned!
     *
     * @return this adapters dataset
     */
    public ArrayList<T> getDataSet() {
        return dataSet;
    }

    /**
     * Sets the Dataset for this Adapter. Keep in mind
     * that a reference to the list is used!
     *
     * @param dataSet a list containing items
     */
    public void setDataSet(List dataSet) {
        this.dataSet = (ArrayList<T>) dataSet;
        notifyDataSetChanged();
    }

    private boolean fitNewItem() {
        heightOfAllItems = 0d;
        for (int i = 0; i < getItemCount(); i++) {
            if (container.findViewHolderForAdapterPosition(i) != null) {
                View itemView = container.findViewHolderForAdapterPosition(i).itemView;
                heightOfAllItems += itemView.getHeight();
            }
        }


        return (heightOfAllItems + minHeightPX) <= (parentHeight);
    }

    private boolean fitNewItemAndAdjustHeight() {
        float accumulatedHeight = 0f;

        if (!fitNewItem()) {
            double extraHeight = heightOfAllItems + minHeightPX - (parentHeight);
            expandedNotSuitableItems.clear();
            for (int i = 0; i < getItemCount(); i++) {
                VH itemHolder = (VH) container.findViewHolderForAdapterPosition(i);
                if (itemHolder.isExpanded) {
                    if ((itemHolder.itemView.getHeight() - extraHeight) >= minHeightPX) {

                        itemHolder.itemView.getLayoutParams().height -= extraHeight;

                        itemHolder.isExpanded = itemHolder.itemView.getLayoutParams().height > minHeightPX;

                        itemHolder.updateOnResizeInternal(i, getItem(i));

                        return true;
                    } else {
                        accumulatedHeight += itemHolder.itemView.getHeight() - extraHeight;
                        expandedNotSuitableItems.add(itemHolder);


                    }
                }
            }

            if (accumulatedHeight >= minHeightPX) {

                double deletedHeight = 0.0d;
                boolean fittedItem = false;

                for (int j = 0; j < expandedNotSuitableItems.size() && !fittedItem; j++) {
                    DynamicStackViewHolder vh = expandedNotSuitableItems.get(j);
                    deletedHeight += vh.itemView.getHeight() - minHeightPX;
                    vh.itemView.getLayoutParams().height = minHeightPX;
                    vh.itemView.requestLayout();
                    vh.isExpanded = false;

                    vh.updateOnResizeInternal(vh.getAdapterPosition(), getItem(vh.getAdapterPosition()));

                    fittedItem = (deletedHeight >= minHeightPX);
                }
                expandedNotSuitableItems.clear();
                return true;
            }

        } else {
            return true;
        }

        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        removeItem(position);
        itemWasDismissed(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        //T prev = dataSet.remove(fromPosition);
        //dataSet.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);

        Collections.swap(dataSet, fromPosition, toPosition);
        DynamicStackSaveManager.swapSavedPosition(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);

        itemWasMoved(fromPosition, toPosition);
    }

    public abstract void itemWasDismissed(int position);
    public abstract void itemWasMoved(int fromPosition, int toPosition);

}
