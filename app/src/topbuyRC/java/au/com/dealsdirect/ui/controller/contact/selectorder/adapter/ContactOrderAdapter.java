package au.com.dealsdirect.ui.controller.contact.selectorder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.ui.controller.contact.addcontact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectorder.listener.ContactOrderClickListener;
import au.com.dealsdirect.ui.controller.contact.selectorder.viewholder.ContactOrderViewHolder;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactOrderAdapter extends RecyclerView.Adapter<ContactOrderViewHolder>  {

    List<ContactOrderList> mCurrentContactOrderList = Collections.emptyList();
    Context mContext;
    ContactOrderClickListener myContactOrderItemClickListener;

    private ImageView lastChecked;

    public ContactOrderAdapter(
            List<ContactOrderList> contactOrders,
            Context context,
            ContactOrderClickListener contactOrderItemClickListener) {

        this.mCurrentContactOrderList = contactOrders;
        this.mContext = context;
        this.myContactOrderItemClickListener = contactOrderItemClickListener;
    }

    @Override
    public ContactOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_contact_order,
                        parent,
                        false);

        return new ContactOrderViewHolder(v);
    }


    @Override
    public void onBindViewHolder(
            final ContactOrderViewHolder holder, final int position) {


        holder.contactOrderTitleRowTextView
                .setText(mCurrentContactOrderList.get(position).getDescription() + " ("
                        + mCurrentContactOrderList.get(position).getInvoiceNo() + ")");

        holder.contactOrderRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                myContactOrderItemClickListener.onContactOrderItemClicked
                        (mCurrentContactOrderList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mCurrentContactOrderList == null){
            return 0;
        }
        return mCurrentContactOrderList.size();
    }
}
