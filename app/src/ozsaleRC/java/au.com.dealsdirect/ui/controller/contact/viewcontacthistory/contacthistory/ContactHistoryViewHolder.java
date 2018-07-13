package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryViewHolder extends RecyclerView.ViewHolder{

//    LinearLayout contactHistoryItem;
//    ImageView contactHistoryItemUserImageView;
//    TextView contactHistoryItemUserNameTextView;
//    TextView contactHistoryDescriptionTextView;

    @BindView(R.id.contact_history_item_container)
    ViewGroup contactHistoryItemContainer;
    @BindView(R.id.contact_history_message_text_view)
    TextView contactHistoryMessageTextView;

    public ContactHistoryViewHolder(View itemView) {
        super(itemView);
//  Commented unused data as of the moment 05/22/2018 JPA
//        contactHistoryItemUserImageView = (ImageView) itemView
//                .findViewById(R.id.contact_history_item_user_image);
//
//        contactHistoryItem = (LinearLayout) itemView
//                .findViewById(R.id.my_contact_history_recycler_row_item_layout);
//
//        contactHistoryItemUserNameTextView = (TextView) itemView
//                .findViewById(R.id.my_contact_history_username_row_text_view);

//        contactHistoryDescriptionTextView = (TextView) itemView
//                .findViewById(R.id.my_contact_history_row_subject_text_view);

        ButterKnife.bind(this, itemView);
    }
}
