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
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

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

        String mContactMessage = mCurrentContactsHistoryList.get(position).getText();

        if (mCurrentContactsHistoryList.get(position).getIsStaff()) {
            holder.mContactMessageTextView.setSelected(true);
            holder.mContactMessageTextView.setBackgroundResource(R.drawable.bg_message_incoming);
            holder.mContactUserImage.setText("DD");
            holder.mContactUserImage.setVisibility(View.VISIBLE);
            holder.mContactUserImage.setSolidColor("#EDC7D5");
        } else {
            holder.mContactUserImage.setVisibility(View.INVISIBLE);
        }

        if (mContactMessage != null) {
            holder.mContactMessageTextView.setText(mContactMessage);
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
