package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.DateUtils;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryAdapter extends RecyclerView.Adapter<ContactHistoryViewHolder> {

    private List<au.com.dealsdirect.data.network.model.contacthistory.List> mCurrentContactsHistoryList = Collections.emptyList();
    private Context mContext;

    public ContactHistoryAdapter(List<au.com.dealsdirect.data.network.model.contacthistory.List> contactitemsList, Context context) {
        Collections.reverse(contactitemsList);
        this.mCurrentContactsHistoryList = contactitemsList;
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

        String contactDate = mCurrentContactsHistoryList.get(position).getDate();
        String contactMessage = mCurrentContactsHistoryList.get(position).getText();
        boolean isStaff = mCurrentContactsHistoryList.get(position).getIsStaff();
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
