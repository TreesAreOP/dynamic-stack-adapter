package de.taop.hskl.dynamicStackAdapter;

import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Adrian on 26.07.2017.
 */

public class DynamicStackSaveManager {

    private static final String STATE_ITEMS = "dynamic-stack-state-items";
    private static final String STATE_PERCENTAGE = "dynamic-stack-state-percentage-of-items";

    private static ArrayList<Float> percentageOfItems = new ArrayList<>();

    public static void clearHeightOfItems() {
        percentageOfItems.clear();
    }

    private static void setPercentageOfPosition(int position, float percentage) {
        percentageOfItems.add(position, percentage);
    }

    private static float getPercentageOfPosition(int position) {
        float height = percentageOfItems.get(position);
        return height <= 0f ? -1f : height;
    }

    public static void saveDynamicStackAdapter(Bundle outState, DynamicStackAdapter adapter) {
        outState.putSerializable(STATE_ITEMS, adapter.getDataSet());

        float percentage = 0f;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            DynamicStackViewHolder vh = ((DynamicStackViewHolder) adapter.container.findViewHolderForAdapterPosition(i));
            if (vh != null) {
                setPercentageOfPosition(i, vh.calculateHeightPercentage());
                percentage += getPercentageOfPosition(i);
            }
        }

        outState.putSerializable(STATE_PERCENTAGE, percentageOfItems);
    }

    public static void restoreDynamicStackAdapter(final Bundle savedInstanceState, final DynamicStackAdapter adapter) {
        adapter.setDataSet((ArrayList) savedInstanceState.getSerializable(STATE_ITEMS));
        percentageOfItems = (ArrayList<Float>) savedInstanceState.getSerializable(STATE_PERCENTAGE);

        adapter.container.post(new Runnable() {
            @Override
            public void run() {

                float extraHeight;
                float heightOfAllItems = 0f;

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    float percentage = getPercentageOfPosition(i);

                    DynamicStackViewHolder vh = (DynamicStackViewHolder) adapter.container.findViewHolderForAdapterPosition(i);
                    vh.percentage = percentage;
                    vh.itemView.getLayoutParams().height =
                            percentage <= 0f ? adapter.minHeightPX : (int) (percentage * (adapter.container.getHeight() - adapter.marginPixels));
                    vh.itemView.requestLayout();
                    vh.isExpanded = vh.itemView.getLayoutParams().height > adapter.minHeightPX;

                    heightOfAllItems += vh.itemView.getLayoutParams().height;
                }

                DynamicStackViewHolder vh = (DynamicStackViewHolder) adapter.container.findViewHolderForAdapterPosition(adapter.getItemCount() - 1);
                if (heightOfAllItems > (adapter.container.getHeight() - adapter.marginPixels)) {
                    vh.itemView.getLayoutParams().height -= (int) (heightOfAllItems - adapter.container.getHeight() - adapter.marginPixels);
                    vh.itemView.requestLayout();
                }

            }
        });


    }


}
