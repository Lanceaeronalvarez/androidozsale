package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.support.v7.widget.RecyclerView;

/**
 * Created by MTC on 2019-07-16.
 */
public interface CheckoutListener {

    void showItemDetail(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl,
                        String skuId, String saleId, boolean isFreeDelivery,
                        String itemName, String brandName, String price, String oldPrice,
                        String productID);

}
