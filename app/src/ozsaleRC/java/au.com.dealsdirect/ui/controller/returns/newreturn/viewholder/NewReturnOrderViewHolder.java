package au.com.dealsdirect.ui.controller.returns.newreturn.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 7/24/17.
 */

public class NewReturnOrderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.new_return_request_item_name)
    public TextView newReturnItemNameTextView;
    @BindView(R.id.new_returns_order_item_count_value)
    public TextView newReturnItemQuantityText;
    @BindView(R.id.new_return_item_price)
    public TextView newReturnItemPriceTextView;
    @BindView(R.id.new_returns_order_subtotal)
    public TextView newReturnItemSubTotalTextView;
    @BindView(R.id.new_return_detail_item_image)
    public ImageView newReturnItemImageView;
    @BindView(R.id.new_return_item_detail_checkbox)
    public CheckBox newReturnItemCheckBox;
    @BindView(R.id.new_return_order_quantity)
    public ProductQuantityLayout productQuantityLayout;

    public NewReturnOrderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}
