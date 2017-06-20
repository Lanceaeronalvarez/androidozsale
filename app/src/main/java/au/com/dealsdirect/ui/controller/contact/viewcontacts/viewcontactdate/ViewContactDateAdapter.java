package au.com.dealsdirect.ui.controller.contact.viewcontacts.viewcontactdate;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.viewcontactitem.ContactItemByDate;
import au.com.dealsdirect.data.network.model.viewcontactitem.GetContactsResponse;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.viewcontactitem.ViewContactItemAdapter;

/**
 * dp Created by Admin on 6/20/17.
 */

public class ViewContactDateAdapter extends
        RecyclerView.Adapter<ViewContactDateAdapter.MyContactsContactUsDateContainerViewHolder> {

    ArrayList<ContactItemByDate> mDateSet;
    Context mContext;

    public ViewContactDateAdapter(ArrayList<ContactItemByDate> dateSet, Context context) {

        this.mDateSet = dateSet;
        this.mContext = context;
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

        final ViewContactItemAdapter adapter
                = new ViewContactItemAdapter(contactItems , mContext);

        holder.mContactDateItemRecyclerView.setAdapter(adapter);
        holder.mContactDateItemRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        holder.mContactDateItemRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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
