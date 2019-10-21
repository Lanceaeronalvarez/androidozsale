package au.com.dealsdirect.ui.controller.saleitems;

import java.util.HashMap;

import javax.annotation.Nonnull;

import static au.com.dealsdirect.ui.controller.saleitems.SaleItemsController.GridViewMode;

public class GridViewModePreferenceHelper {
    public static class GridViewModePreference {
        public final static GridViewModePreference LARGER_IMAGES = new GridViewModePreference("LARGER_IMAGES");
        public final static GridViewModePreference MORE_IMAGES = new GridViewModePreference("MORE_IMAGES");
        public final static GridViewModePreference EITHER = new GridViewModePreference("EITHER");

        private String mStringValue = "";

        private GridViewModePreference(String stringValue) {
            mStringValue = stringValue;
        }

        @Nonnull
        @Override
        public String toString() {
            return mStringValue;
        }
    }

    private long mTimestampForGridViewMode = System.currentTimeMillis();

    private HashMap<GridViewMode, Long> mTimeElapsedForGridViewMode = new HashMap<>();

    public void resetTimeElapsed() {
        mTimeElapsedForGridViewMode.clear();
    }

    public void resetTimestamp() {
        mTimestampForGridViewMode = System.currentTimeMillis();
    }

    public void incrementTimeElapsedForGridViewMode(GridViewMode currentGridViewMode) {
        long currentTimestamp = System.currentTimeMillis();

        long timeElapsed = currentTimestamp - mTimestampForGridViewMode;

        Long currentTimeElapsed = mTimeElapsedForGridViewMode.get(currentGridViewMode);
        if (currentTimeElapsed == null) {
            currentTimeElapsed = (long) 0;
        }

        mTimeElapsedForGridViewMode.put(currentGridViewMode, currentTimeElapsed + timeElapsed);
        mTimestampForGridViewMode = currentTimestamp;
    }

    public GridViewModePreference computeGridViewModePreference() {
        Long moreImagesModeTimeElapsed = mTimeElapsedForGridViewMode.get(GridViewMode.MORE_IMAGES);
        if (moreImagesModeTimeElapsed == null) {
            moreImagesModeTimeElapsed = (long) 0;
        }
        Long largerImagesModeTimeElapsed = mTimeElapsedForGridViewMode.get(GridViewMode.LARGER_IMAGES);
        if (largerImagesModeTimeElapsed == null) {
            largerImagesModeTimeElapsed = (long) 0;
        }

        double totalTimeElapsed = moreImagesModeTimeElapsed + largerImagesModeTimeElapsed;

        if (moreImagesModeTimeElapsed / totalTimeElapsed > 0.7) {
            return GridViewModePreference.MORE_IMAGES;
        } else if (largerImagesModeTimeElapsed / totalTimeElapsed > 0.7) {
            return GridViewModePreference.LARGER_IMAGES;
        } else {
            return GridViewModePreference.EITHER;
        }
    }
}
