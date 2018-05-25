package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.CircularTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryViewHolder extends RecyclerView.ViewHolder{


    @BindView(R.id.contact_history_row_message_acronym)
    CircularTextView mContactUserImage;

    @BindView(R.id.contact_history_row_message_text_view)
    TextView mContactMessageTextView;


    public ContactHistoryViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
