package au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.ContactItemByDate;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.custom.CircularTextView;
import au.com.dealsdirect.utils.ColorUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.StringUtils;

/**
 * dp Created by Admin on 6/20/17.
 */

public class ContactsAdapter
        extends RecyclerView.Adapter<ContactsAdapter.ViewContactsItemViewHolder> {

    List<GetContactsResponse.ContactList> mCurrentContactsList = Collections.emptyList();
    private ContactsClickListener mContactClickListener;
    Context mContext;

    public void replace(List<GetContactsResponse.ContactList> items) {
        mCurrentContactsList = items;
        notifyDataSetChanged();
    }

    public ContactsAdapter(
            List<GetContactsResponse.ContactList> contactitemsList,
            Context context,
            ContactsClickListener contactsClickListener) {


        this.mCurrentContactsList = contactitemsList;
        this.mContactClickListener = contactsClickListener;
        this.mContext = context;

    }

    @Override
    public ViewContactsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_item, parent, false);

        return new ViewContactsItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewContactsItemViewHolder holder, int position) {

        Object itemSubject = mCurrentContactsList.get(position).getSubject();
        Object itemLastAnswer = mCurrentContactsList.get(position).getLastAnswer();
        Object itemLastComment = mCurrentContactsList.get(position).getLastComment();
        String dateOfContactItem = mCurrentContactsList.get(position).getLastComment();
        String dateHeaderFormatOfItem = DateUtils.getDayOfWeekFromDateString(itemLastAnswer.toString());

        holder.circularTextView.setText("DD");

        holder.circularTextView.setSolidColor(ColorUtils.getOvalColor(position));

        if (itemSubject != null) {
            holder.contactUsTitleTextView.setText(StringUtils.toTitleCase(itemSubject.toString()));
        } else {
            holder.contactUsTitleTextView.setText("");
        }

        if (itemLastComment != null) {
            holder.contactUsDescriptionTextView.setText(itemLastComment.toString());
        } else {
            holder.contactUsDescriptionTextView.setText("");
        }

        if (itemLastAnswer != null) {
            String itemLastAnswerTimeFormat = DateUtils.getDateForContactMessages(itemLastAnswer.toString());

            holder.contactUsTimeStampTextView.setText(itemLastAnswerTimeFormat);

        } else {
            holder.contactUsTimeStampTextView.setText("");
        }

        holder.contactItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContactClickListener.onContactClicked(mCurrentContactsList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mCurrentContactsList == null) {
            return 0;
        }
        return mCurrentContactsList.size();
    }

    static class ViewContactsItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout contactItem;
        CircularTextView circularTextView;
        TextView contactUsTitleTextView;
        TextView contactUsTimeStampTextView;
        TextView contactUsDescriptionTextView;

        public ViewContactsItemViewHolder(View itemView) {
            super(itemView);

            contactItem = (LinearLayout) itemView
                    .findViewById(R.id.my_contact_us_recycler_row_item_layout);

            circularTextView = (CircularTextView) itemView
                    .findViewById(R.id.my_contact_us_row_acronym);

            contactUsTitleTextView = (TextView) itemView
                    .findViewById(R.id.my_contact_us_row_title_text_view);

            contactUsTimeStampTextView = (TextView) itemView
                    .findViewById(R.id.my_contact_us_row_item_time_stamp_text_view);

            contactUsDescriptionTextView = (TextView) itemView
                    .findViewById(R.id.my_contact_us_row_description_text_view);
        }
    }

}
