package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.OnClickAttachmentListener;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final boolean SHOULD_DISPLAY_LINK_ATTACHMENT_LIST = true;

    private List<Message> mCurrentContactsHistoryList;
    private final int VIEW_TYPE_RATING_FOOTER = 1;
    private final int VIEW_TYPE_MESSAGE = 0;
    private boolean mHasRating;
    private ContactHistoryOnClickRatingListener mListener;
    private OnClickAttachmentListener onClickAttachmentListener;

    private GetContactHistoryResponse.Escalate escalate = null;
    private ContactHistoryMessageViewHolder.OnClickEscalateListener escalateAction = null;

    public ContactHistoryAdapter(List<Message> messages, boolean hasRating,
                                 OnClickAttachmentListener onClickAttachmentListener,
                                 ContactHistoryOnClickRatingListener listener) {
        this.mCurrentContactsHistoryList = new ArrayList<>(messages);
        this.mHasRating = hasRating;
        this.mListener = listener;
        this.onClickAttachmentListener = onClickAttachmentListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_RATING_FOOTER:
                return new ContactHistoryRatingViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.footer_user_satisfaction, parent, false));
            default:
                return new ContactHistoryMessageViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.viewholder_contact_history, parent, false),
                        onClickAttachmentListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_RATING_FOOTER:
                final ContactHistoryRatingViewHolder ratingViewHolder = (ContactHistoryRatingViewHolder) holder;

                ratingViewHolder.setup(shouldShowRatingFooter(),
                        new ContactHistoryOnClickRatingListener() {
                            @Override
                            public void onClickSmile() {
                                mHasRating = true;
                                if (mListener != null) {
                                    mListener.onClickSmile();
                                }
                            }

                            @Override
                            public void onClickNeutral() {
                                mHasRating = true;
                                if (mListener != null) {
                                    mListener.onClickNeutral();
                                }
                            }

                            @Override
                            public void onClickSad() {
                                mHasRating = true;
                                if (mListener != null) {
                                    mListener.onClickSad();
                                }
                            }
                        });
                break;
            default: {
                final ContactHistoryMessageViewHolder messageHolder = (ContactHistoryMessageViewHolder) holder;
                messageHolder.setup(getMessageAtPosition(position), SHOULD_DISPLAY_LINK_ATTACHMENT_LIST);
                messageHolder.setupEscalate(getAdjustedPosition(position) == 0 ? escalate : null, escalateAction);
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mCurrentContactsHistoryList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_RATING_FOOTER : VIEW_TYPE_MESSAGE;
    }

    private int getAdjustedPosition(int position) {
        return position - getStartIndexOfMessages();
    }

    public int getAdjustedFirstPosition() {
        return getAdjustedPosition(0);
    }

    public Message getMessageAtPosition(int position) {
        return mCurrentContactsHistoryList.get(getAdjustedPosition(position));
    }

    public boolean shouldShowRatingFooter() {
        if (!mCurrentContactsHistoryList.isEmpty()) {
            final boolean lastMessageIsFromStaff = mCurrentContactsHistoryList.get(0).isStaff();
            return !mHasRating && lastMessageIsFromStaff;
        }
        return false;
    }

    public List<Message> getMessages() {
        return new ArrayList<>(mCurrentContactsHistoryList);
    }

    public void setMessages(List<Message> messages) {
        mCurrentContactsHistoryList = new ArrayList<>(messages);
    }

    public boolean hasRating() {
        return mHasRating;
    }

    public void setHasRating(boolean hasRating) {
        mHasRating = hasRating;
    }

    public int getStartIndexOfMessages() {
        return 1;
    }

    public int getFooterIndex() {
        return 0;
    }

    public GetContactHistoryResponse.Escalate getEscalate() {
        return escalate;
    }

    public void setEscalate(GetContactHistoryResponse.Escalate escalate) {
        this.escalate = escalate;
    }

    public ContactHistoryMessageViewHolder.OnClickEscalateListener getEscalateAction() {
        return escalateAction;
    }

    public void setEscalateAction(ContactHistoryMessageViewHolder.OnClickEscalateListener escalateAction) {
        this.escalateAction = escalateAction;
    }
}
