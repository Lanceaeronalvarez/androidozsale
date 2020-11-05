package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ImageDisplayAdapter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.CommonUtils;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryAdapter extends RecyclerView.Adapter<ContactHistoryViewHolder> {

    private List<GetContactHistoryResponse.Message> mCurrentContactsHistoryList;
    private Context mContext;
    private final int VIEW_TYPE_FOOTER = 1;
    private final int VIEW_TYPE_CELL = 0;
    private boolean mHasRating;
    private ContactHistoryListener mListener;

    public ContactHistoryAdapter(Context context, List<GetContactHistoryResponse.Message> messages, boolean hasRating, ContactHistoryListener listener) {
        this.mCurrentContactsHistoryList = messages;
        this.mContext = context;
        this.mHasRating = hasRating;
        this.mListener = listener;
    }

    @Override
    public ContactHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CELL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_history, parent, false);
            return new ContactHistoryViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_user_satisfaction, parent, false);
            return new ContactHistoryViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ContactHistoryViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_CELL) {

            final GetContactHistoryResponse.Message message = mCurrentContactsHistoryList.get(position - 1);
            final CharSequence contactMessage = CommonUtils.checkIfStringHasHtmlElements(message.getText()) ?
                    Html.fromHtml(message.getText()) : message.getText();
            final boolean isStaff = message.isStaff();
            final List<GetContactHistoryResponse.Message.Attachment> attachments = message.getAttachments();

//        Change background/textcolor, text gravities if isStaff
            holder.itemView.setSelected(isStaff);
            holder.contactHistoryMessageTextView.setSelected(isStaff);

            FrameLayout.LayoutParams contactHistoryItemParams = (FrameLayout.LayoutParams) holder.contactHistoryItemContainer.getLayoutParams();
            contactHistoryItemParams.gravity = isStaff ? Gravity.START : Gravity.END;

            LinearLayout.LayoutParams messageTextViewParams = (LinearLayout.LayoutParams) holder.contactHistoryMessageTextView.getLayoutParams();
            messageTextViewParams.gravity = isStaff ? Gravity.START : Gravity.END;

            holder.contactHistoryMessageTextView.setText(contactMessage);

            if (attachments.isEmpty()) {
                holder.contactHistoryMessageRecyclerView.setVisibility(View.GONE);
            } else {
                holder.contactHistoryMessageRecyclerView.setVisibility(View.VISIBLE);
                final ImageDisplayAdapter mAdapter = new ImageDisplayAdapter(mContext,
                        message.getAttachments());
                final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
                holder.contactHistoryMessageRecyclerView.setAdapter(mAdapter);
                holder.contactHistoryMessageRecyclerView.setLayoutManager(layoutManager);
            }
        } else {
            final boolean lastmessageIsFromStaff = mCurrentContactsHistoryList.get(0).isStaff();
            if (lastmessageIsFromStaff && !mHasRating) {
                holder.contactHistoryUserSatisfactionContainer.setSelected(lastmessageIsFromStaff);
                holder.contactHistoryUserSatisfactionContainer.setVisibility(View.VISIBLE);
            } else {
                holder.contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
            }

            holder.contactHistorySmileButton.setOnClickListener(v -> {
                holder.contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
                countdown(holder);
                mListener.generateCloseTicket(AppConstants.SMILE_ICON);
            });

            holder.contactHistoryNeutralBUtton.setOnClickListener(v -> {
                holder.contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
                countdown(holder);
                mListener.generateCloseTicket(AppConstants.NEUTRAL_ICON);
            });

            holder.contactHistorySadButton.setOnClickListener(v -> {
                holder.contactHistoryUserSatisfactionContainer.setVisibility(View.GONE);
                countdown(holder);
                mListener.generateCloseTicket(AppConstants.SAD_ICON);
            });
        }

    }

    @Override
    public int getItemCount() {
        return mCurrentContactsHistoryList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    private void countdown(ContactHistoryViewHolder holder) {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                holder.contactHistoryThankYouContainer.setVisibility(View.VISIBLE);
            }

            public void onFinish() {
                holder.contactHistoryThankYouContainer.setVisibility(View.GONE);
            }
        }.start();
    }

}
