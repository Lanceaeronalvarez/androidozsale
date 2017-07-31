package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.CircularTextView;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryViewHolder extends RecyclerView.ViewHolder{

    LinearLayout contactHistoryItem;
    CircularTextView contactHistoryItemCircularTextView;
    TextView contactHistoryMessageTextView;

    public ContactHistoryViewHolder(View itemView) {
        super(itemView);

        contactHistoryItem = (LinearLayout) itemView
                .findViewById(R.id.my_contact_history_recycler_row_item_layout);

        contactHistoryItemCircularTextView = (CircularTextView) itemView
                .findViewById(R.id.my_contact_history_row_message_acronym);

        contactHistoryMessageTextView = (TextView) itemView
                .findViewById(R.id.my_contact_history_row_message_text_view);

    }
}
