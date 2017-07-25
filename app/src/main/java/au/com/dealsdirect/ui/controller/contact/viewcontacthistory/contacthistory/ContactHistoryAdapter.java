package au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.utils.ColorUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.StringUtils;

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

        Object userName = mCurrentContactsHistoryList.get(position).getUserName();
        Object contactDate = mCurrentContactsHistoryList.get(position).getDate();
        Object contactItemInvoiceNumber = mCurrentContactsHistoryList.get(position).getInvoiceNo();
        Object contactMessage = mCurrentContactsHistoryList.get(position).getText();

        String contactSubject = mCurrentContactsHistoryList.get(position).getSubject();

        if (position % 2 == 0) {
            holder.contactHistoryMessageTextView.setBackgroundResource(R.drawable.bg_message_incoming);
            holder.contactHistoryItemCircularTextViewLeft.setText("DD");
            holder.contactHistoryItemCircularTextViewLeft.setVisibility(View.VISIBLE);
            holder.contactHistoryItemCircularTextViewLeft.setSolidColor(ColorUtils.getOvalColor(position));
            holder.contactHistoryItemCircularTextViewRight.setVisibility(View.GONE);
        } else {
            holder.contactHistoryItemCircularTextViewLeft.setVisibility(View.GONE);
            holder.contactHistoryItemCircularTextViewRight.setVisibility(View.VISIBLE);
            holder.contactHistoryItemCircularTextViewRight.setSolidColor("#CACACA");
            if (userName != null) {
                holder.contactHistoryItemCircularTextViewRight.setText(StringUtils.getInitials(userName.toString()));
            } else {
                holder.contactHistoryItemCircularTextViewRight.setText("JD");
            }
        }

//        String dateHeaderFormatOfItem = DateUtils.getTrimmedServerDateString(contactDate.toString());

        if (contactMessage != null) {
            holder.contactHistoryMessageTextView.setText(contactMessage.toString());
        } else {
            holder.contactHistoryMessageTextView.setText("Nothing to display");
        }
    }

    @Override
    public int getItemCount() {
        if (mCurrentContactsHistoryList == null) {
            return 0;
        }
        return mCurrentContactsHistoryList.size();
    }

}
