package au.com.dealsdirect.ui.controller.returns.newreturn.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;

/**
 * dp Created by Admin on 7/24/17.
 */

public class NewReturnOrderViewHolder extends RecyclerView.ViewHolder{

    public TextView newReturnItemNameTextView;
    public TextView newReturnItemPriceTextView;
    public TextView newReturnItemSizeTextView;
    public TextView newReturnItemCountTextView;
    public TextView newReturnItemIdTextView;

    public RelativeLayout newReturnItemSizeRowContainer;
    public RelativeLayout newReturnItemContainer;


    public ImageView newReturnItemImageView;
    public View newReturnItemViewDivider;

    public ProductQuantityLayout productQuantityLayout;

    public NewReturnOrderViewHolder(View itemView) {
        super(itemView);

//        newReturnsOrderTrackingValueTextView = (TextView) itemView.
//                findViewById(R.id.new_returns_order_item_tracking_value);

        newReturnItemNameTextView = (TextView) itemView.
                findViewById(R.id.new_return_request_item_name);
        newReturnItemPriceTextView = (TextView) itemView.
                findViewById(R.id.new_return_request_item_total_cost);

        newReturnItemImageView = (ImageView) itemView.
                findViewById(R.id.new_return_set_detail_item_image);

        newReturnItemViewDivider = itemView.
                findViewById(R.id.my_returns_set_detail_item_divider);

        newReturnItemSizeTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_size_return_value);

        newReturnItemCountTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_count_value);

        newReturnItemIdTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_id_value);

        productQuantityLayout = (ProductQuantityLayout) itemView.
                findViewById(R.id.new_returns_select_order_item_quantaty_selector);

        newReturnItemSizeRowContainer = (RelativeLayout) itemView.
                findViewById(R.id.new_returns_order_item_size_detail);

        newReturnItemContainer = (RelativeLayout) itemView.
                findViewById(R.id.new_return_item_set_detail_container);

    }
}
