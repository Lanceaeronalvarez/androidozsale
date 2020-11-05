package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

    @BindView(R.id.contact_history_item_container)
    ViewGroup contactHistoryItemContainer;
    @BindView(R.id.contact_history_message_text_view)
    TextView contactHistoryMessageTextView;
    @BindView(R.id.contact_history_recyclerview)
    RecyclerView contactHistoryMessageRecyclerView;
    @BindView(R.id.contact_history_user_satisfaction)
    LinearLayout contactHistoryUserSatisfactionContainer;
    @BindView(R.id.contact_history_thank_you_feedback)
    LinearLayout contactHistoryThankYouContainer;
    @BindView(R.id.contact_history_smile)
    ImageButton contactHistorySmileButton;
    @BindView(R.id.contact_history_neutral)
    ImageButton contactHistoryNeutralBUtton;
    @BindView(R.id.contact_history_sad)
    ImageButton contactHistorySadButton;

    public ContactHistoryViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }
}
