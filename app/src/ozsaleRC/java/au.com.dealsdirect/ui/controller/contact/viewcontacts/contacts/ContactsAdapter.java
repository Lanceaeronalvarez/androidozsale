package au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpPresenter;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/20/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewContactsItemViewHolder> {

    List<GetContactsResponse.ContactList> mCurrentContactsList = Collections.emptyList();
    private ViewContactsMvpPresenter mPresenter;

    public void replace(List<GetContactsResponse.ContactList> items) {
        mCurrentContactsList = items;
        notifyDataSetChanged();
    }

    public ContactsAdapter(
            List<GetContactsResponse.ContactList> contactLists,
            ViewContactsMvpPresenter mvpPresenter) {

        mCurrentContactsList = contactLists;
        mPresenter = mvpPresenter;
    }

    @Override
    public ViewContactsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_item, parent, false);
        return new ViewContactsItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewContactsItemViewHolder holder, int position) {

        String itemSubject = mCurrentContactsList.get(position).getSubject();
        String itemLastAnswer = mCurrentContactsList.get(position).getLastAnswer();
        String itemLastComment = mCurrentContactsList.get(position).getLastComment();
        String dateOfContactItem = mCurrentContactsList.get(position).getLastComment();
        String dateHeaderFormatOfItem = DateUtils.getDayOfWeekFromDateString(itemLastAnswer.toString());

        holder.contactUsTitleTextView.setText(StringUtils.toTitleCase(itemSubject));
        holder.contactUsDescriptionTextView.setText(itemLastComment);
        holder.contactUsTimeStampTextView.setText(DateUtils.getDateForContactMessages(itemLastAnswer));
        holder.contactItem.setOnClickListener(v -> mPresenter.selectContact(mCurrentContactsList.get(position)));
    }

    @Override
    public int getItemCount() {
        return mCurrentContactsList.size();
    }

    static class ViewContactsItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.my_contact_us_recycler_row_item_layout)
        LinearLayout contactItem;
        @BindView(R.id.my_contact_us_row_title_text_view)
        TextView contactUsTitleTextView;
        @BindView(R.id.my_contact_us_row_item_time_stamp_text_view)
        TextView contactUsTimeStampTextView;
        @BindView(R.id.my_contact_us_row_description_text_view)
        TextView contactUsDescriptionTextView;

        public ViewContactsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
