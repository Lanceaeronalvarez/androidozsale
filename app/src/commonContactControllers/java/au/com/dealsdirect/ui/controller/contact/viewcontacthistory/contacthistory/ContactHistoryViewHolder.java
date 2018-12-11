package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryViewHolder extends RecyclerView.ViewHolder{

    LinearLayout contactHistoryItem;
    ImageView contactHistoryItemUserImageView;
    TextView contactHistoryItemUserNameTextView;
    TextView contactHistoryItemDateStampTextView;
    TextView contactHistoryItemTimeStampTextView;
    TextView contactHistoryDescriptionTextView;
    TextView contactHistoryMessageTextView;
    LinearLayout contactHistoryItemContainer;

    public ContactHistoryViewHolder(View itemView) {
        super(itemView);

        contactHistoryItemUserImageView = (ImageView) itemView
                .findViewById(R.id.contact_history_item_user_image);

        contactHistoryItem = (LinearLayout) itemView
                .findViewById(R.id.my_contact_history_recycler_row_item_layout);

        contactHistoryItemUserNameTextView = (TextView) itemView
                .findViewById(R.id.my_contact_history_username_row_text_view);

        contactHistoryItemDateStampTextView = (TextView) itemView
                .findViewById(R.id.my_contact_history_row_date_stamp_text_view);

        contactHistoryItemTimeStampTextView = (TextView) itemView
                .findViewById(R.id.my_contact_history_row_time_stamp_text_view);

        contactHistoryDescriptionTextView = (TextView) itemView
                .findViewById(R.id.my_contact_history_row_subject_text_view);

        contactHistoryMessageTextView = (TextView) itemView
                .findViewById(R.id.my_contact_history_row_message_text_view);

        contactHistoryItemContainer = itemView
                .findViewById(R.id.my_contact_history_item_container_layout);

    }
}
