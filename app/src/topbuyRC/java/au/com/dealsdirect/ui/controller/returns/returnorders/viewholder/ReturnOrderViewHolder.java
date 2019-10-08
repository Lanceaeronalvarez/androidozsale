package au.com.dealsdirect.ui.controller.returns.returnorders.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrderViewHolder extends RecyclerView.ViewHolder{

    public LinearLayout newReturnsOrderProductItem;

    public ImageView newReturnsOrderItemImageView;

    public TextView newReturnsOrderItemName;
    public TextView newReturnsOrderItemCountValueTextView;
    public TextView newReturnsOrderTotalCostValueTextView;
    public TextView newReturnsOrderStatusValueTextView;
    public TextView newReturnsOrderRequestNumberTextView;

    public ReturnOrderViewHolder(View itemView) {
        super(itemView);

        newReturnsOrderProductItem = (LinearLayout) itemView.
                findViewById(R.id.new_returns_order_row_container);

        newReturnsOrderItemName = (TextView) itemView.
                findViewById(R.id.new_current_orders_item_name);

        newReturnsOrderItemImageView = (ImageView) itemView.
                findViewById(R.id.new_return_order_item_image_view);

        newReturnsOrderItemCountValueTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_count_value);

        newReturnsOrderTotalCostValueTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_total_cost_value);

        newReturnsOrderStatusValueTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_status_value);

        newReturnsOrderRequestNumberTextView = (TextView) itemView.
                findViewById(R.id.new_returns_order_item_request_value);

    }
}
