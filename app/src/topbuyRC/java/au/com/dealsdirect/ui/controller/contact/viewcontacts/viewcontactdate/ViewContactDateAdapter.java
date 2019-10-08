package au.com.dealsdirect.ui.controller.contact.viewcontacts.viewcontactdate;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.ContactItemByDate;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsClickListener;

/**
 * dp Created by Admin on 6/20/17.
 */

public class ViewContactDateAdapter extends
        RecyclerView.Adapter<ViewContactDateAdapter.MyContactsContactUsDateContainerViewHolder> {

    ArrayList<ContactItemByDate> mDateSet;
    Context mContext;
    private ContactsClickListener mContactClickListener;

    public ViewContactDateAdapter(
            ArrayList<ContactItemByDate> dateSet,
            Context context,
            ContactsClickListener contactsClickListener) {

        this.mDateSet = dateSet;
        this.mContext = context;
        this.mContactClickListener = contactsClickListener;
    }

    @Override
    public MyContactsContactUsDateContainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_contact_date,
                        parent,
                        false);

        return new MyContactsContactUsDateContainerViewHolder(v);
    }

    public void replace(ArrayList<ContactItemByDate> items){
        mDateSet = items;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyContactsContactUsDateContainerViewHolder holder, int position) {

        final String contactDateLastAnswer =  mDateSet.get(position).getDateHeaderFormat();

        holder.mContactDateViewHolderTextView.setText(contactDateLastAnswer);

        final List<GetContactsResponse.ContactList> contactItems = mDateSet.get(position).getContactItemList();

        final ContactsAdapter adapter
                = new ContactsAdapter(contactItems , mContext, mContactClickListener);
        holder.mContactDateItemRecyclerView.setAdapter(adapter);
        holder.mContactDateItemRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

    }

    @Override
    public int getItemCount() {
        if (mDateSet == null){
            return 0;
        }
        return mDateSet.size();
    }

    static class MyContactsContactUsDateContainerViewHolder extends RecyclerView.ViewHolder{

        TextView mContactDateViewHolderTextView;

        RecyclerView mContactDateItemRecyclerView;

        public MyContactsContactUsDateContainerViewHolder(View itemView) {
            super(itemView);

            mContactDateViewHolderTextView = (TextView) itemView
                    .findViewById(R.id.viewholder_contact_date_text);

            mContactDateItemRecyclerView = (RecyclerView) itemView
                    .findViewById(R.id.viewholder_contact_date_items_recycler_view);
        }

    }

}
