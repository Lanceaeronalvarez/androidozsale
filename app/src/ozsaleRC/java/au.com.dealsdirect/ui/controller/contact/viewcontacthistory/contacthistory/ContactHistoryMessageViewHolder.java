package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ImageDisplayAdapter;
import au.com.dealsdirect.utils.CommonUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryMessageViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.contact_history_item_container)
    ViewGroup contactHistoryItemContainer;
    @BindView(R.id.contact_history_message_text_view)
    TextView contactHistoryMessageTextView;
    @BindView(R.id.contact_history_recyclerview)
    RecyclerView contactHistoryMessageRecyclerView;

    public ContactHistoryMessageViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void setup(Message message) {
        final CharSequence contactMessage = CommonUtils.checkIfStringHasHtmlElements(message.getText()) ?
                Html.fromHtml(message.getText()) : message.getText();
        final boolean isStaff = message.isStaff();
        final List<Message.Attachment> attachments = message.getAttachments();

//        Change background/textcolor, text gravities if isStaff
        itemView.setSelected(isStaff);
        contactHistoryMessageTextView.setSelected(isStaff);

        FrameLayout.LayoutParams contactHistoryItemParams = (FrameLayout.LayoutParams) contactHistoryItemContainer.getLayoutParams();
        contactHistoryItemParams.gravity = isStaff ? Gravity.START : Gravity.END;

        LinearLayout.LayoutParams messageTextViewParams = (LinearLayout.LayoutParams) contactHistoryMessageTextView.getLayoutParams();
        messageTextViewParams.gravity = isStaff ? Gravity.START : Gravity.END;

        contactHistoryMessageTextView.setText(contactMessage);

        if (attachments.isEmpty()) {
            contactHistoryMessageRecyclerView.setVisibility(View.GONE);
        } else {
            contactHistoryMessageRecyclerView.setVisibility(View.VISIBLE);
            final ImageDisplayAdapter mAdapter = new ImageDisplayAdapter(itemView.getContext(),
                    message.getAttachments());
            final LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), RecyclerView.HORIZONTAL, false);
            contactHistoryMessageRecyclerView.setAdapter(mAdapter);
            contactHistoryMessageRecyclerView.setLayoutManager(layoutManager);
        }
    }
}
