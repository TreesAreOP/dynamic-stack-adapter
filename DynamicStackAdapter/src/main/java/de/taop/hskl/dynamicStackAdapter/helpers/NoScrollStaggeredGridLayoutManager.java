package de.taop.hskl.dynamicStackAdapter.helpers;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * @author Adrian Bernhart
 */

public class NoScrollStaggeredGridLayoutManager extends StaggeredGridLayoutManager {
    public NoScrollStaggeredGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NoScrollStaggeredGridLayoutManager(int i, int vertical) {
        super(i, vertical);
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

}
