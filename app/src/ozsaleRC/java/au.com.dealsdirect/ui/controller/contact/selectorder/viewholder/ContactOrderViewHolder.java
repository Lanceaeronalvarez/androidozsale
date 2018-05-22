package au.com.dealsdirect.ui.controller.contact.selectorder.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactOrderViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout contactOrderRowLayout;
    public TextView contactOrderTitleRowTextView;

    public ContactOrderViewHolder(View itemView) {
        super(itemView);

        contactOrderRowLayout = (RelativeLayout) itemView.findViewById(R.id.contact_select_order_row_layout);
        contactOrderTitleRowTextView = (TextView) itemView.findViewById(R.id.contact_order_row_item_text);
    }
}
