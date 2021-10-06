package au.com.dealsdirect.ui.controller.contact.selectorder.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderMvpPresenter;
import au.com.dealsdirect.ui.controller.contact.selectorder.viewholder.ContactOrderViewHolder;

/**
 * dp Created by Admin on 7/5/17.
 */

public class ContactOrderAdapter extends RecyclerView.Adapter<ContactOrderViewHolder> {

    private List<ContactOrderResponse> mCurrentContactOrderList;
    private ContactSelectOrderMvpPresenter mPresenter;
    private Context mContext;
    private int mScreenHeight;
    private int mScreenWidth;

    public ContactOrderAdapter(Context context, List<ContactOrderResponse> contactOrders, ContactSelectOrderMvpPresenter mvpPresenter,
                               int screenHeight, int screenWidth) {
        mContext = context;
        mCurrentContactOrderList = contactOrders;
        mPresenter = mvpPresenter;
        mScreenHeight = screenHeight;
        mScreenWidth = screenWidth;
    }

    @Override
    public ContactOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_contact_order, parent, false);
        return new ContactOrderViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ContactOrderViewHolder holder, final int position) {
        String text = holder.itemView.getContext().getResources().getString(R.string.invoice_text) + " " + mCurrentContactOrderList.get(position).getNumber();
        holder.contactOrderTitleRowTextView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.contactOrderTitleRowTextView.setText(text);
        holder.contactOrderRowLayout.setOnClickListener(v -> mPresenter.selectContactOrder(mCurrentContactOrderList.get(position)));

        if (mCurrentContactOrderList.get(position).getItems() != null &&
                mCurrentContactOrderList.get(position).getItems().size() != 0) {

            holder.contactOrderRecyclerview.setVisibility(View.VISIBLE);

            int columns = mPresenter.isTablet() ? mContext.getResources().getInteger(R.integer.sponsored_banner_column_count_for_tablet) :
                    mContext.getResources().getInteger(R.integer.sponsored_banner_column_count);

            ContactInvoiceAdapter contactInvoiceAdapter = new ContactInvoiceAdapter(mScreenHeight,
                    mScreenWidth,
                    mCurrentContactOrderList.get(position).getItems(),
                    columns);


            LinearLayoutManager mProductImagesRvLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            holder.contactOrderRecyclerview.setLayoutManager(mProductImagesRvLayoutManager);

            holder.contactOrderRecyclerview.setAdapter(contactInvoiceAdapter);
            holder.itemView.setOnClickListener(v -> mPresenter.selectContactOrder(mCurrentContactOrderList.get(position)));

        } else {
            holder.contactOrderRecyclerview.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mCurrentContactOrderList == null ? 0 : mCurrentContactOrderList.size();
    }

    public void replaceData(List<ContactOrderResponse> contactOrderLists) {
        mCurrentContactOrderList = contactOrderLists;
        notifyDataSetChanged();
    }
}
