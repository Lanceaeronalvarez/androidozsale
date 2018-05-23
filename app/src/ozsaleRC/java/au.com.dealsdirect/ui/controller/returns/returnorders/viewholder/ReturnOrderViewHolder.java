package au.com.dealsdirect.ui.controller.returns.returnorders.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrderViewHolder extends RecyclerView.ViewHolder{

    public LinearLayout newReturnsOrderProductItem;

    public TextView newReturnsOrderItemName;

    public ReturnOrderViewHolder(View itemView) {
        super(itemView);

        newReturnsOrderProductItem = (LinearLayout) itemView.
                findViewById(R.id.new_returns_order_row_container);

        newReturnsOrderItemName = (TextView) itemView.
                findViewById(R.id.new_current_orders_item_name);

    }
}
