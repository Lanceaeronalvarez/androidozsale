package au.com.dealsdirect.ui.controller.returns.returnorders.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersMvpPresenter;
import au.com.dealsdirect.ui.controller.returns.returnorders.viewholder.ReturnOrderViewHolder;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrdersAdapter extends RecyclerView.Adapter<ReturnOrderViewHolder> {

    private ReturnOrdersMvpPresenter mPresenter;

    private List<au.com.dealsdirect.data.network.model.returns.returnorders.List> mNewReturnOrderList;

    public ReturnOrdersAdapter(
            List<au.com.dealsdirect.data.network.model.returns.returnorders.List> newReturnsOrderList,
            ReturnOrdersMvpPresenter mvpPresenter) {

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

        holder.newReturnsOrderItemName.setText(newReturnsOrderName);

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
