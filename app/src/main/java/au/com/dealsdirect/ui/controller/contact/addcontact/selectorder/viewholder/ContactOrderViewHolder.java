package au.com.dealsdirect.ui.controller.contact.addcontact.selectorder.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactOrderViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout contactOrderRowLayout;
    public TextView contactOrderTitleRowTextView;
    public ImageView contactOrderRowCheckImageView;

    public ContactOrderViewHolder(View itemView) {
        super(itemView);

        contactOrderRowLayout = (LinearLayout) itemView
                .findViewById(R.id.my_contact_select_subject_recycler_row_layout);

        contactOrderTitleRowTextView = (TextView) itemView
                .findViewById(R.id.contact_subject_row_item_name);

        contactOrderRowCheckImageView = (ImageView) itemView
                .findViewById(R.id.contact_subject_row_item_check_image_view);

    }
}
