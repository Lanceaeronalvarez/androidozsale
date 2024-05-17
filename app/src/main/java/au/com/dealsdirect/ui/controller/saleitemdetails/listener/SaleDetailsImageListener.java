package au.com.dealsdirect.ui.controller.saleitemdetails.listener;

public interface SaleDetailsImageListener {

    void imagesLoaded();

    void onImageRescale(float scale);

    void toggleClipPadding(boolean isClipped);

    int getVerticalOffset();

    void onClick(int position);
}
