package au.com.dealsdirect.ui.controller.returns.newreturn.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;

/**
 * dp Created by Admin on 7/24/17.
 */

public class NewReturnOrderViewHolder extends RecyclerView.ViewHolder {

    public TextView newReturnItemNameTextView;
    public TextView newReturnItemPriceTextView;
    public TextView newReturnItemSubTotalTextView;
    public TextView newReturnItemCountTextView;

    public RelativeLayout newReturnItemSizeRowContainer;
    public RelativeLayout newReturnItemContainer;
    public ImageView newReturnItemImageView;
    public View newReturnItemViewDivider;
    public CheckBox newReturnItemCheckBox;


    public NewReturnOrderViewHolder(View itemView) {
        super(itemView);

        newReturnItemNameTextView = (TextView) itemView.
                findViewById(R.id.new_return_request_item_name);

        newReturnItemPriceTextView = (TextView) itemView.
                findViewById(R.id.new_return_request_item_total_cost);

        newReturnItemSubTotalTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_size_return_subtotal);

        newReturnItemImageView = (ImageView) itemView.
                findViewById(R.id.new_return_set_detail_item_image);

        newReturnItemViewDivider = itemView.
                findViewById(R.id.my_returns_set_detail_item_divider);

        newReturnItemCountTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_count_value);

        newReturnItemContainer = (RelativeLayout) itemView.
                findViewById(R.id.new_return_item_set_detail_container);

        newReturnItemCheckBox = (CheckBox) itemView.
                findViewById(R.id.new_return_item_detail_checkbox);

    }
}
