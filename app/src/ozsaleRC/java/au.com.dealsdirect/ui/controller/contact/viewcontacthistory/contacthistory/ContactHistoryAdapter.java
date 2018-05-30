package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.DateUtils;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryAdapter extends RecyclerView.Adapter<ContactHistoryViewHolder> {

    List<au.com.dealsdirect.data.network.model.contacthistory.List> mCurrentContactsHistoryList = Collections.emptyList();
    Context mContext;

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
//  Commented unused data as of the moment 05/22/2018 JPA
//        Object userName = mCurrentContactsHistoryList.get(position).getUserName();


        Object contactDate = mCurrentContactsHistoryList.get(position).getDate();
        Object contactMessage = mCurrentContactsHistoryList.get(position).getText();
        boolean isStaff = mCurrentContactsHistoryList.get(position).getIsStaff();
//        String contactSubject = mCurrentContactsHistoryList.get(position).getSubject();


        String dateHeaderFormatOfItem = DateUtils.getTrimmedServerDateString(contactDate.toString());

//        if (userName != null){
//            holder.contactHistoryItemUserNameTextView
//                    .setText(userName.toString());
//        }else{
//            holder.contactHistoryItemUserNameTextView
//                    .setText("unknown");
//        }
//
//        if (contactSubject != null){
//
//            holder.contactHistoryDescriptionTextView
//                    .setText(contactSubject.toString());
//        }else{
//
//            holder.contactHistoryDescriptionTextView.setText("");
//        }

//        Change background/textcolor, text gravities if isStaff
        holder.itemView.setSelected(isStaff);
        holder.contactHistoryMessageTextView.setSelected(isStaff);
        holder.contactHistoryItemDateStampTextView.setSelected(isStaff);
        holder.contactHistoryItemTimeStampTextView.setSelected(isStaff);

        if(isStaff){

            FrameLayout.LayoutParams contactHistoryItemParams = (FrameLayout.LayoutParams) holder.contactHistoryItemContainer.getLayoutParams();
            contactHistoryItemParams.gravity = Gravity.START;

            LinearLayout.LayoutParams dateTimeStampTextViewParams = (LinearLayout.LayoutParams) holder.contactHistoryDateTimeContainer.getLayoutParams();
            dateTimeStampTextViewParams.gravity = Gravity.START;

            LinearLayout.LayoutParams messageTextViewParams = (LinearLayout.LayoutParams) holder.contactHistoryMessageTextView.getLayoutParams();
            messageTextViewParams.gravity = Gravity.START;
        }


        if (contactMessage != null) {
            holder.contactHistoryMessageTextView.setText(contactMessage.toString());
        } else {
            holder.contactHistoryMessageTextView.setText("Nothing to display");
        }

        if (contactDate != null) {
            String itemLastAnswerTimeFormat = DateUtils.getTimeFromDateString(contactDate.toString());

            holder.contactHistoryItemDateStampTextView.setText(dateHeaderFormatOfItem);
            holder.contactHistoryItemTimeStampTextView.setText(itemLastAnswerTimeFormat);

        } else {
            holder.contactHistoryItemDateStampTextView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mCurrentContactsHistoryList.size();
    }

}
