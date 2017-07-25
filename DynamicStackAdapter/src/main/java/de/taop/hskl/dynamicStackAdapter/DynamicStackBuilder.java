package de.taop.hskl.dynamicStackAdapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import de.taop.hskl.dynamicStackAdapter.helpers.NoScrollStaggeredGridLayoutManager;
import de.taop.hskl.dynamicStackAdapter.helpers.SimpleItemTouchHelperCallback;

/**
 * A Builder Class which creates your custom implemented {@link DynamicStackAdapter}
 * and configures the given {@link RecyclerView}.
 *
 * @author Adrian Bernhart
 */

public class DynamicStackBuilder<A extends DynamicStackAdapter, VH extends DynamicStackViewHolder> {

    private Class<A> adapterType;
    private Class<VH> viewHolderType;
    private A adapter;
    private RecyclerView rv;

    public static final int DEFAULT_MAX_ITEMS = 6;
    public static final float DEFAULT_PIXEL_PADDING = 1.5f;
    public static final boolean DEFAULT_AUTO_RESIZE_ITEMS = false;
    public static final boolean DEFAULT_USER_RESIZE = false;
    public static final boolean DEFAULT_USER_MOVE = true;
    public static final boolean DEFAULT_USER_DELETE = true;
    public static final boolean DEFAULT_REVERSE_STACK = false;

    private int maxItems;
    private float pixelPadding;
    private List dataSet;
    private boolean autoResizeItems;
    private boolean userResize;
    private int resourceID;
    private int itemLayout;
    private boolean userMove;
    private boolean userDelete;
    private boolean reverseStack;

    /**
     * Creates a new Builder.
     */
    public DynamicStackBuilder() {
        maxItems = DEFAULT_MAX_ITEMS;
        pixelPadding = DEFAULT_PIXEL_PADDING;
        dataSet = new ArrayList();
        autoResizeItems = DEFAULT_AUTO_RESIZE_ITEMS;
        userResize = DEFAULT_USER_RESIZE;
        userMove = DEFAULT_USER_MOVE;
        userDelete = DEFAULT_USER_DELETE;
        reverseStack = DEFAULT_REVERSE_STACK;
        resourceID = -1;
        itemLayout = -1;
    }

    /**
     * <b>Required!</b> Call this Method to specify your
     * recyclerview which should use a stackAdapter.
     *
     * @param rv the recyclerview to be used as stack
     * @return this Builder for chaining
     * @see DynamicStackAdapter
     */

    public DynamicStackBuilder stackRecyclerView(RecyclerView rv) {
        this.rv = rv;
        return this;
    }

    /**
     * <b>Required!</b> Call this Method to specify your custom
     * implemented Adapter type.
     *
     * @param adapterType the type of your adapter
     * @return this Builder for chaining
     * @see DynamicStackAdapter
     */
    public DynamicStackBuilder withAdapterType(Class<A> adapterType) {
        this.adapterType = adapterType;
        return this;
    }

    /**
     * <b>Required!</b> Call this Method to specify your custom
     * implemented ViewHolder type.
     *
     * @param viewHolderType the type of your viewHolder
     * @return this Builder for chaining
     * @see DynamicStackAdapter
     */
    public DynamicStackBuilder withViewHolderType(Class<VH> viewHolderType) {
        this.viewHolderType = viewHolderType;
        return this;
    }

    /**
     * <b>Required!</b> Call this Method to specify your custom
     * layout id for the items.
     *
     * @param itemLayout the layout id of your items
     * @return this Builder for chaining
     */
    public DynamicStackBuilder withItemLayoutID(int itemLayout) {
        this.itemLayout = itemLayout;
        return this;
    }

    /**
     * Defines the maximum count of items in the adapter/recyclerview. The
     * minimum height of the items is calculated with this value and the
     * height of the recyclerview. By default, the value is set to {@value DEFAULT_MAX_ITEMS}.
     *
     * @param maxItems maximum count of items in the stack
     * @return this Builder for chaining
     */
    public DynamicStackBuilder setMaxItems(int maxItems) {
        this.maxItems = maxItems;
        return this;
    }

    /**
     * Sets the initial dataset of the adapter.
     *
     * @param dataSet the initial dataset
     * @return this Builder for chaining
     */
    public DynamicStackBuilder setDataSet(List dataSet) {
        this.dataSet = (ArrayList) dataSet;
        return this;
    }

    /**
     * Sets the Padding of the recyclerview. This is used when calculating
     * the minimal allowed height of the items. A higher value means a lower
     * minimal height. Play with this value if your max item count doesn't match the
     * shown number of items in your recyclerview. Note: negative values are
     * allowed. By default, the value is set to {@value DEFAULT_PIXEL_PADDING}.
     *
     * @param pixelPadding the padding in pixel
     * @return this Builder for chaining
     * @see #setMaxItems(int)
     */
    public DynamicStackBuilder setPixelPadding(float pixelPadding) {
        this.pixelPadding = pixelPadding;
        return this;
    }

    /**
     * Whether or the not existing items can be resized by the user. If true
     * a resource ID to the "resize area" (which needs to be located in the
     * in the items layout file) needs to be provided!By default, the value
     * is set to {@value DEFAULT_USER_RESIZE}.
     *
     * @param userResize Whether or not the user can resize the items
     * @return this Builder for chaining
     */
    public DynamicStackBuilder allowUserResizingItems(boolean userResize) {
        this.userResize = userResize;
        return this;
    }

    /**
     * Whether or the not existing items can be moved by the user.
     * By default, the value is set to {@value DEFAULT_USER_MOVE}.
     *
     * @param userMove Whether or not the user can move the items
     * @return this Builder for chaining
     */
    public DynamicStackBuilder allowUserMoveItems(boolean userMove) {
        this.userMove = userMove;
        return this;
    }

    /**
     * Whether or the not existing items can be deleted by the user.
     * By default, the value is set to {@value DEFAULT_USER_DELETE}.
     *
     * @param userDelete Whether or not the user can delete the items
     * @return this Builder for chaining
     */
    public DynamicStackBuilder allowUserDeleteItems(boolean userDelete) {
        this.userDelete = userDelete;
        return this;
    }


    /**
     * Reverses the stack direction. false means the stack will be build up
     * from bottom to top. New items will be added on top. false means the
     * items stack at the top. new items will be added on the bottom and
     * then "float" to the top. By default, the value is set to
     * {@value DEFAULT_REVERSE_STACK}.
     *
     * <b>NOT WORKING AT THE MOMENT</b>
     *
     * @param reverseStack Whether or not the stack is reversed
     * @return this Builder for chaining
     */
    @Deprecated
    public DynamicStackBuilder reverseStackDirection(boolean reverseStack) {
        //this.reverseStack = reverseStack;
        return this;
    }

    /**
     * The Resource ID of the "resize area" located in the items layout file.
     * This view will be used as "Button" to change the views height. The user
     * then can touch and drag the area to change the items height.
     *
     * @param resourceID The id of the view to use as resize area
     * @return this Builder for chaining
     */
    public DynamicStackBuilder provideResizeAreaID(int resourceID) {
        this.resourceID = resourceID;
        return this;
    }

    /**
     * Whether or the not existing items should be shrunk to make room
     * for a new item. If not items won't be added until the user makes
     * room for them.
     *
     * @param autoResizeItems Whether or not items are resized
     * @return this Builder for chaining
     */
    public DynamicStackBuilder setAutoResizeItems(boolean autoResizeItems) {
        this.autoResizeItems = autoResizeItems;
        return this;
    }


    /**
     * Builds the adapter and configures the recyclerview appropriately.
     *
     * @return a new instance of your custom DynamicStackAdapter.
     */
    public A build() {
        if (adapterType == null || viewHolderType == null) {
            throw new BuilderNotReadyException("You need to specify the adapter type and viewHolder type!");
        }

        if (userResize && resourceID == -1) {
            throw new BuilderNotReadyException("You need to specify a Resource ID for the resize area!");
        }

        if (itemLayout == -1) {
            throw new BuilderNotReadyException("You need to specify a Layout ID for the item layout!");
        }
        return setUpRecyclerViewAsDynamicStack();
    }

    private A setUpRecyclerViewAsDynamicStack() {

        NoScrollStaggeredGridLayoutManager staggeredLayoutManager = new NoScrollStaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        staggeredLayoutManager.setReverseLayout(!reverseStack);
        rv.setLayoutManager(staggeredLayoutManager);
        rv.setHasFixedSize(true);

        Constructor<A> constructor = null;
        try {
            constructor = adapterType.getConstructor(RecyclerView.class, Class.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            adapter = constructor.newInstance(rv, viewHolderType);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        if (dataSet != null) {
            adapter.setDataSet(dataSet);
        }
        adapter.maxItems = maxItems;
        adapter.marginPixels = pixelPadding;
        adapter.autoResizeItems = autoResizeItems;

        adapter.resizeViewResourceID = resourceID;
        adapter.allowUserResize = userResize;

        adapter.reverseStack = reverseStack;

        adapter.itemLayout = itemLayout;

        rv.setAdapter(adapter);

        SimpleItemTouchHelperCallback callback = new SimpleItemTouchHelperCallback();
        callback.allowDrag = userMove;
        callback.allowSwipe = userDelete;
        callback.setAdapter(adapter);

        return adapter;

    }

}
