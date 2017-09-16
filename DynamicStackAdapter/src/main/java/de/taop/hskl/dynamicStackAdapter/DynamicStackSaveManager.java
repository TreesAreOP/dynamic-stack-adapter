package de.taop.hskl.dynamicStackAdapter;

import android.os.Bundle;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Created by Adrian on 26.07.2017.
 */

public class DynamicStackSaveManager {

    private static final String STATE_ITEMS = "dynamic-stack-state-items";
    private static final String STATE_PERCENTAGE = "dynamic-stack-state-percentage-of-items";

    private static String[] percentageOfItems;

    private static int maxItems;

    public static void initializeSaveManger(int maxItems) {
        DynamicStackSaveManager.maxItems = maxItems;
        percentageOfItems = new String[maxItems];
    }

    public static void clearHeightOfItems() {
        percentageOfItems = new String[maxItems];
    }

    public static void setPercentageOfPosition(int position, BigDecimal percentage) {
        percentageOfItems[position] = percentage.toPlainString();
    }

    public static BigDecimal getPercentageOfPosition(int position) {
        return new BigDecimal(percentageOfItems[position]);
    }

    public static void saveDynamicStackAdapter(Bundle outState, DynamicStackAdapter adapter) {
        outState.putSerializable(STATE_ITEMS, adapter.getDataSet());

        BigDecimal percentage = new BigDecimal(0);

        for (int i = 0; i < adapter.getItemCount(); i++) {
            DynamicStackViewHolder vh = ((DynamicStackViewHolder) adapter.container.findViewHolderForAdapterPosition(i));
            if (vh != null) {
                setPercentageOfPosition(i, vh.percentage);
                percentage = percentage.add(getPercentageOfPosition(i));
            }
        }

        outState.putSerializable(STATE_PERCENTAGE, percentageOfItems);
    }

    public static void restoreDynamicStackAdapter(final Bundle savedInstanceState, final DynamicStackAdapter adapter) {
        adapter.setDataSet((ArrayList) savedInstanceState.getSerializable(STATE_ITEMS));
        percentageOfItems = (String[]) savedInstanceState.getSerializable(STATE_PERCENTAGE);

        adapter.container.post(new Runnable() {
            @Override
            public void run() {

                float extraHeight;
                float heightOfAllItems = 0f;

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    BigDecimal percentage = getPercentageOfPosition(i);

                    DynamicStackViewHolder vh = (DynamicStackViewHolder) adapter.container.findViewHolderForAdapterPosition(i);

                    if (vh != null) {
                        vh.percentage = percentage;

                        BigDecimal minHeightBD = new BigDecimal(adapter.minHeightPX).setScale(4, BigDecimal.ROUND_HALF_UP);
                        BigDecimal containerBD = new BigDecimal(adapter.container.getHeight()).setScale(4, BigDecimal.ROUND_HALF_UP);
                        BigDecimal marginBD = new BigDecimal(adapter.marginPixels).setScale(4, BigDecimal.ROUND_HALF_UP);

                        BigDecimal minPercentage = minHeightBD.divide(containerBD.subtract(marginBD), BigDecimal.ROUND_HALF_UP).setScale(4, RoundingMode.HALF_UP);

                        BigDecimal newHeight = percentage.multiply(containerBD.subtract(marginBD));

                        vh.itemView.getLayoutParams().height = percentage.compareTo(minPercentage) <= 0 ? adapter.minHeightPX : newHeight.intValue();
                        vh.itemView.requestLayout();
                        vh.isExpanded = vh.itemView.getLayoutParams().height > adapter.minHeightPX;

                        heightOfAllItems += vh.itemView.getLayoutParams().height;
                    }
                }

                DynamicStackViewHolder vh = (DynamicStackViewHolder) adapter.container.findViewHolderForAdapterPosition(adapter.getItemCount() - 1);
                if (heightOfAllItems > (adapter.container.getHeight() - adapter.marginPixels)) {
                    vh.itemView.getLayoutParams().height -= (int) (heightOfAllItems - adapter.container.getHeight() - adapter.marginPixels);
                    vh.itemView.requestLayout();
                }

            }
        });


    }


    public static boolean positionWasSavedBefore(int position) {
        return percentageOfItems[position] != null;
    }

    static void removeSavedPosition(int position) {
        percentageOfItems[position] = null;
    }

    /**
     * Deletes already saved value at toPosition!
     *
     * @param fromPosition
     * @param toPosition
     */
    static void moveSavedPosition(int fromPosition, int toPosition) {
        String tmp = percentageOfItems[fromPosition];
        percentageOfItems[fromPosition] = null;
        percentageOfItems[toPosition] = tmp;
    }

    static void swapSavedPosition(int fromPosition, int toPosition) {
        String tmp = percentageOfItems[fromPosition];
        percentageOfItems[fromPosition] = percentageOfItems[toPosition];
        percentageOfItems[toPosition] = tmp;
    }

}
