package au.com.dealsdirect.ui.controller.contact.selectorder.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactOrderViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout contactOrderRowLayout;
    public TextView contactOrderTitleRowTextView;
    public RecyclerView contactOrderRecyclerview;

    public ContactOrderViewHolder(View itemView) {
        super(itemView);

        contactOrderRowLayout = itemView.findViewById(R.id.contact_select_order_row_layout);
        contactOrderTitleRowTextView = itemView.findViewById(R.id.contact_order_row_item_text);
        contactOrderRecyclerview = itemView.findViewById(R.id.viewholder_contact_order_recyclerview);
    }
}
