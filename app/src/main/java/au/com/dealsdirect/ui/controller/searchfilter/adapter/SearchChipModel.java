package au.com.dealsdirect.ui.controller.searchfilter.adapter;

/**
 * Created by smartwave on 24/07/2017.
 */

public class SearchChipModel {

    public SearchChipModel(String mFilterType, String mChipTitle, String key, int mIndex) {
        this.mFilterType = mFilterType;
        this.mChipTitle = mChipTitle;
        this.mKey = key;
        this.mIndex = mIndex;
    }

    public String getFilterType() {
        return mFilterType;
    }

    public void setFilterType(String mFilterType) {
        this.mFilterType = mFilterType;
    }

    public String getChipTitle() {
        return mChipTitle;
    }

    public void setChipTitle(String mChipTitle) {
        this.mChipTitle = mChipTitle;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
    }

    private String mFilterType;
    private String mChipTitle;
    private String mKey;
    private int mIndex;
    private int minValue;

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    private int maxValue;
}
