package au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.controller.contact.listener.ContactClickListener;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpPresenter;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * dp Created by Admin on 6/20/17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewContactsItemViewHolder> {

    List<GetContactsResponse> mCurrentContactsList = Collections.emptyList();
    private ViewContactsMvpPresenter mPresenter;
    private ContactClickListener mContactClickListener;

    public void replace(List<GetContactsResponse> items) {
        mCurrentContactsList = items;
        notifyDataSetChanged();
    }

    public ContactsAdapter(
            List<GetContactsResponse> contactLists,
            ViewContactsMvpPresenter mvpPresenter,
            ContactClickListener contactClickListener) {

        mCurrentContactsList = contactLists;
        mPresenter = mvpPresenter;
        mContactClickListener = contactClickListener;
    }

    @Override
    public ViewContactsItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_item, parent, false);
        return new ViewContactsItemViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewContactsItemViewHolder holder, int position) {

        String itemSubject = mCurrentContactsList.get(position).getSubject();
        String itemLastAnswer = mCurrentContactsList.get(position).getLastMessageDate();
        String itemLastComment = mCurrentContactsList.get(position).getLastMessage();
        String dateOfContactItem = mCurrentContactsList.get(position).getLastMessage();
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

        @BindView(R.id.my_contact_us_row_layout)
        LinearLayout contactItem;
        @BindView(R.id.my_contact_us_row_title_text_view)
        TextView contactUsTitleTextView;
        @BindView(R.id.my_contact_us_row_item_time_stamp_text_view)
        TextView contactUsTimeStampTextView;
        @BindView(R.id.my_contact_us_row_description_text_view)
        TextView contactUsDescriptionTextView;
        @BindView(R.id.controller_contacts_new_message_button)
        Button contactUsCreateMessageButton;

        public ViewContactsItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
