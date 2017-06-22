package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.DateUtils;

/**
 * dp Created by Admin on 6/22/17.
 */

public class ContactHistoryAdapter
        extends RecyclerView.Adapter<ContactHistoryViewHolder>{

    List<au.com.dealsdirect.data.network.model.contacthistory.List>
            mCurrentContactsHistoryList = Collections.emptyList();

    Context mContext;

    public ContactHistoryAdapter(
            List<au.com.dealsdirect.data.network.model.contacthistory.List> contactitemsList,
            Context context) {

        this.mCurrentContactsHistoryList = contactitemsList;
        this.mContext = context;
    }

    @Override
    public ContactHistoryViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_contact_history,
                        parent,
                        false);

        return new ContactHistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactHistoryViewHolder holder, int position) {

        Object userName = mCurrentContactsHistoryList.get(position).getUserName();
        Object contactDate = mCurrentContactsHistoryList.get(position).getDate();
        Object contactItemInvoiceNumber = mCurrentContactsHistoryList.get(position).getInvoiceNo();
        Object contactMessage = mCurrentContactsHistoryList.get(position).getText();

        String contactSubject = mCurrentContactsHistoryList.get(position).getSubject();


//        String dateHeaderFormatOfItem = DateUtils.getTrimmedServerDateString(contactDate.toString());

        if (userName != null){
            holder.contactHistoryItemUserNameTextView
                    .setText(userName.toString());
        }else{
            holder.contactHistoryItemUserNameTextView
                    .setText("unknown");
        }

        if (contactSubject != null){

            holder.contactHistoryDescriptionTextView
                    .setText(contactSubject.toString());
        }else{

            holder.contactHistoryDescriptionTextView.setText("");
        }


        if (contactMessage != null){
            holder.contactHistoryMessageTextView.setText(contactMessage.toString());
        }else{
            holder.contactHistoryMessageTextView.setText("Nothing to display");
        }

        if (contactDate != null){
            String itemLastAnswerTimeFormat
                    = DateUtils.getTimeFromDateString(contactDate.toString());

            holder.contactHistoryItemDateStampTextView
                    .setText(itemLastAnswerTimeFormat);

            holder.contactHistoryItemTimeStampTextView
                    .setText(itemLastAnswerTimeFormat);

        }else{
            holder.contactHistoryItemDateStampTextView.setText("");
        }

    }

    @Override
    public int getItemCount() {
        if (mCurrentContactsHistoryList == null){
            return 0;
        }
        return mCurrentContactsHistoryList.size();
    }

}
