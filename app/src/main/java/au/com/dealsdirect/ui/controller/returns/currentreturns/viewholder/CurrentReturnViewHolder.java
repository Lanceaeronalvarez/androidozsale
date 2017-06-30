package au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnViewHolder extends RecyclerView.ViewHolder {

    public CardView currentReturnProductItem;

    public TextView currentReturnsRequestNumberValueTextView;
    public TextView currentReturnsRequestProductNameValueTextView;
    public TextView currentReturnsRequestDateValueTextView;
    public TextView currentReturnsRequestIsApprovedValueTextView;

    public TextView currentReturnsRequestStatusValueTextView;
    public TextView currentReturnsRequestRANValueTextView;
    public RelativeLayout currentReturnsCardContentContainer;

    public RecyclerView currentReturnItemsRecyclerView;

    public CurrentReturnViewHolder(View itemView) {
        super(itemView);

        currentReturnsCardContentContainer = (RelativeLayout) itemView.
                findViewById(R.id.viewholder_current_return_content_container);

        currentReturnProductItem = (CardView) itemView.
                findViewById(R.id.view_holder_my_current_returns_row_container);

        currentReturnItemsRecyclerView = (RecyclerView) itemView.
                findViewById(R.id.current_return_items_recyclerview);

        currentReturnsRequestNumberValueTextView = (TextView) itemView.
                findViewById(R.id.current_return_request_number_text_value);

        currentReturnsRequestProductNameValueTextView = (TextView) itemView.
                findViewById(R.id.my_current_returns_product_name);

        currentReturnsRequestDateValueTextView = (TextView) itemView.
                findViewById(R.id.my_order_product_delivery_from_date_value);

        currentReturnsRequestIsApprovedValueTextView = (TextView) itemView.
                findViewById(R.id.my_current_returns_product_is_approved_value);

        currentReturnsRequestStatusValueTextView = (TextView) itemView.
                findViewById(R.id.my_current_return_item_status_value);

        currentReturnsRequestRANValueTextView = (TextView) itemView.
                findViewById(R.id.my_current_return_item_RAN_value);

    }
}
