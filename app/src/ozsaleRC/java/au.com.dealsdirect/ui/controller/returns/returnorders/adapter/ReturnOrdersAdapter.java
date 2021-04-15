package au.com.dealsdirect.ui.controller.returns.returnorders.adapter;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersMvpPresenter;
import au.com.dealsdirect.ui.controller.returns.returnorders.viewholder.ReturnOrderViewHolder;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrdersAdapter extends RecyclerView.Adapter<ReturnOrderViewHolder> {

    private ReturnOrdersMvpPresenter mPresenter;

    private List<au.com.dealsdirect.data.network.model.returns.returnorders.List> mNewReturnOrderList;
    private Context mContext;

    public ReturnOrdersAdapter(
            Context context,
            List<au.com.dealsdirect.data.network.model.returns.returnorders.List> newReturnsOrderList,
            ReturnOrdersMvpPresenter mvpPresenter) {

        mContext = context;
        mNewReturnOrderList = newReturnsOrderList;
        mPresenter = mvpPresenter;
    }

    @Override
    public ReturnOrderViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_return_order, parent, false);
        return new ReturnOrderViewHolder(v);
    }


    @Override
    public void onBindViewHolder(final ReturnOrderViewHolder holder, final int position) {

        String newReturnsOrderName = mNewReturnOrderList.get(position).getDescription();

        String invoiceText = mContext.getResources().getString(R.string.invoice_text) +" "+
                mNewReturnOrderList.get(position).getInvoiceNo();
        holder.newReturnsOrderItemName.setText(invoiceText);

        ReturnOrdersImageAdapter imageAdapter = new ReturnOrdersImageAdapter(mNewReturnOrderList.get(position).getItemImagesList());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        holder.newReturnsRecyclerView.setAdapter(imageAdapter);
        holder.newReturnsRecyclerView.setLayoutManager(layoutManager);
        imageAdapter.notifyDataSetChanged();

        holder.newReturnsOrderProductItem.setOnClickListener(view -> mPresenter.selectReturnOrderItem(mNewReturnOrderList.get(position)));
    }

    @Override
    public int getItemCount() {
        if (mNewReturnOrderList == null) {
            return 0;
        }
        return mNewReturnOrderList.size();
    }

}
