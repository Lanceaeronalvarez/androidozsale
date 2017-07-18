package au.com.dealsdirect.ui.controller.shops.listener;

/**
 * dp Created by Admin on 6/8/17.
 */

public interface BannerClickListener {

    void onBannerClicked(
            String saleId,
            String bannerTitle,
            String bannerId,
            int position,
            String imageUrl,
            boolean isAvailable);
}
