package au.com.dealsdirect.ui.controller.shops.adapter;

public interface ResettableDimensions {
    void resetDimensions();
    void setupDimensions(int orientation);
    int getNumberOfColumns();
    boolean isUseOldBannerDimensions();
    void setUseOldBannerDimensions(boolean useOldBannerDimensions);
}
