package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ImageDisplayAdapter;
import au.com.dealsdirect.utils.DateUtils;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryAdapter extends RecyclerView.Adapter<ContactHistoryViewHolder> {

    private List<GetContactHistoryResponse.Message> mCurrentContactsHistoryList = Collections.emptyList();
    private Context mContext;

    public ContactHistoryAdapter(List<GetContactHistoryResponse.Message> messages, Context context) {
        Collections.reverse(messages);
        this.mCurrentContactsHistoryList = messages;
        this.mContext = context;
    }

    @Override
    public ContactHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_history, parent, false);
        return new ContactHistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactHistoryViewHolder holder, int position) {
//  Commented unused data as of the moment 07/11/2018 JPA
//  Dont remove comments

//        Object userName = mCurrentContactsHistoryList.get(position).getUserName();

        String contactDate = mCurrentContactsHistoryList.get(position).getMessageDate();
        String contactMessage = mCurrentContactsHistoryList.get(position).getText();
        boolean isStaff = mCurrentContactsHistoryList.get(position).isStaff();
//        String contactSubject = mCurrentContactsHistoryList.get(position).getSubject();

        String dateHeaderFormatOfItem = DateUtils.getTrimmedServerDateString(contactDate.toString());

//        Change background/textcolor, text gravities if isStaff
        holder.itemView.setSelected(isStaff);
        holder.contactHistoryMessageTextView.setSelected(isStaff);

        if (isStaff) {
            FrameLayout.LayoutParams contactHistoryItemParams = (FrameLayout.LayoutParams) holder.contactHistoryItemContainer.getLayoutParams();
            contactHistoryItemParams.gravity = Gravity.START;

//            LinearLayout.LayoutParams dateTimeStampTextViewParams = (LinearLayout.LayoutParams) holder.contactHistoryDateTimeContainer.getLayoutParams();
//            dateTimeStampTextViewParams.gravity = Gravity.START;

            LinearLayout.LayoutParams messageTextViewParams = (LinearLayout.LayoutParams) holder.contactHistoryMessageTextView.getLayoutParams();
            messageTextViewParams.gravity = Gravity.START;

        }

        holder.contactHistoryMessageTextView.setText(contactMessage);

        holder.contactHistoryMessageRecyclerView.setVisibility(mCurrentContactsHistoryList.get(position).getAttachments().size() != 0 ?
                View.VISIBLE : View.GONE);

        if (mCurrentContactsHistoryList.get(position).getAttachments().size() != 0) {
            ImageDisplayAdapter mAdapter = new ImageDisplayAdapter(mContext,
                    mCurrentContactsHistoryList.get(position).getAttachments());
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
            holder.contactHistoryMessageRecyclerView.setAdapter(mAdapter);
            holder.contactHistoryMessageRecyclerView.setLayoutManager(layoutManager);
        }

//        if (!contactDate.isEmpty()) {
//            String itemLastAnswerTimeFormat = DateUtils.getTimeFromDateString(contactDate.toString());
//
//            holder.contactHistoryItemDateStampTextView.setText(dateHeaderFormatOfItem);
//            holder.contactHistoryItemTimeStampTextView.setText(itemLastAnswerTimeFormat);
//        } else {
//            holder.contactHistoryItemDateStampTextView.setText("");
//        }
    }

    @Override
    public int getItemCount() {
        return mCurrentContactsHistoryList.size();
    }

}
