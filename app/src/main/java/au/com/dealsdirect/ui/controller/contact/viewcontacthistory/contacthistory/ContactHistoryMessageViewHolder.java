package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Escalate;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message.Attachment;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ImageAttachmentsDisplayAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.LinkAttachmentsDisplayAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.OnClickAttachmentListener;
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
    @BindView(R.id.contact_history_image_thumbnails)
    RecyclerView imageThumbnailRecyclerView;
    @BindView(R.id.contact_history_link_list)
    RecyclerView linkListRecyclerView;
    @BindView(R.id.contact_history_escalate_container)
    ViewGroup escalateContainer;
    @BindView(R.id.contact_history_escalate_text_view)
    TextView escalateTextView;

    private final OnClickAttachmentListener onClickAttachmentListener;

    public ContactHistoryMessageViewHolder(View itemView, OnClickAttachmentListener onClickAttachmentListener) {
        super(itemView);

        this.onClickAttachmentListener = onClickAttachmentListener;

        ButterKnife.bind(this, itemView);
    }

    public void setup(Message message, boolean shouldShowLinks) {
        final CharSequence contactMessage = CommonUtils.checkIfStringHasHtmlElements(message.getText()) ?
                Html.fromHtml(message.getText()) : message.getText();
        final boolean isStaff = message.isStaff();

//        Change background/textcolor, text gravities if isStaff
        itemView.setSelected(isStaff);
        contactHistoryMessageTextView.setSelected(isStaff);

        final int gravity = isStaff ? Gravity.START : Gravity.END;
        setupLayoutParamsGravity(contactHistoryItemContainer, gravity);
        setupLayoutParamsGravity(contactHistoryMessageTextView, gravity);
        setupLayoutParamsGravity(escalateContainer, gravity);
        setupLayoutParamsGravity(escalateTextView, gravity);

        contactHistoryMessageTextView.setText(contactMessage);

        final List<Attachment> imageAttachments = shouldShowLinks ? message.getImageAttachments() : message.getAttachments();
        final List<Attachment> linkAttachments = shouldShowLinks ? message.getLinkAttachments() : new LinkedList<>();

        if (imageAttachments.isEmpty()) {
            imageThumbnailRecyclerView.setVisibility(View.GONE);
            imageThumbnailRecyclerView.setAdapter(null);
        } else {
            imageThumbnailRecyclerView.setVisibility(View.VISIBLE);
            final ImageAttachmentsDisplayAdapter mAdapter = new ImageAttachmentsDisplayAdapter(imageAttachments, onClickAttachmentListener);
            final GridLayoutManager layoutManager = new GridLayoutManager(itemView.getContext(), Math.max(1, Math.min(4, imageAttachments.size())));
            imageThumbnailRecyclerView.setAdapter(mAdapter);
            imageThumbnailRecyclerView.setLayoutManager(layoutManager);
        }

        if (linkAttachments.isEmpty()) {
            linkListRecyclerView.setVisibility(View.GONE);
            linkListRecyclerView.setAdapter(null);
        } else {
            linkListRecyclerView.setVisibility(View.VISIBLE);
            final LinkAttachmentsDisplayAdapter mAdapter = new LinkAttachmentsDisplayAdapter(linkAttachments, onClickAttachmentListener);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), RecyclerView.VERTICAL, false);
            linkListRecyclerView.setAdapter(mAdapter);
            linkListRecyclerView.setLayoutManager(layoutManager);
        }
    }

    public void setupEscalate(Escalate escalate, OnClickEscalateListener onClickEscalateListener) {
        if (escalate != null) {
            final Context context = itemView.getContext();

            final int backgroundColor = Color.parseColor(escalate.getBackgroundColor());
            final int textColor = Color.parseColor(escalate.getTextColor());
            Drawable background = AppCompatResources.getDrawable(context, R.drawable.bg_contact_history_escalate);
            if (background != null) {
                background = background.mutate();
                background.setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN));
            }

            escalateContainer.setBackground(background);
            escalateContainer.setVisibility(View.VISIBLE);
            escalateContainer.setOnClickListener(v -> onClickEscalateListener.onClick());
            escalateTextView.setText(escalate.getText());
            escalateTextView.setTextColor(textColor);
        } else {
            escalateContainer.setVisibility(View.GONE);
            escalateContainer.setOnClickListener(null);
            escalateTextView.setText(null);
        }
    }

    private void setupLayoutParamsGravity(View view, int gravity) {
        final LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        layoutParams.gravity = gravity;
    }

    public interface OnClickEscalateListener {
        void onClick();
    }
}
