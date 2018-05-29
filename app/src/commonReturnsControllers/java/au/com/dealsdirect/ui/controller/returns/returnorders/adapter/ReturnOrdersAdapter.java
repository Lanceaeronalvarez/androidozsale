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
import au.com.dealsdirect.ui.controller.returns.returnorders.listener.ReturnOrderClickListener;
import au.com.dealsdirect.ui.controller.returns.returnorders.viewholder.ReturnOrderViewHolder;
import au.com.dealsdirect.utils.PriceUtils;

/**
 * dp Created by Admin on 6/30/17.
 */

public class ReturnOrdersAdapter extends RecyclerView.Adapter<ReturnOrderViewHolder> {

    private final ReturnOrderClickListener mListener;
    private int lastPosition = -1;

    List<au.com.dealsdirect.data.network.model.returns.returnorders.List>
            mNewReturnOrderList;

    Context mContext;

    public ReturnOrdersAdapter(
            List<au.com.dealsdirect.data.network.model.returns.returnorders.List> newReturnsOrderList,
            Context context,
            ReturnOrderClickListener listener){

        this.mNewReturnOrderList = newReturnsOrderList;
        this.mContext = context;
        this.mListener = listener;
    }

    @Override public ReturnOrderViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_return_order,
                        parent,
                        false);
        return new ReturnOrderViewHolder(v);
    }


    @Override public void onBindViewHolder(final ReturnOrderViewHolder holder, final int position) {

        String newReturnsOrderName = mNewReturnOrderList.get(position).getDescription();
        String newReturnsRequestNumber = mNewReturnOrderList.get(position).getInvoiceNo().toString();
        int newReturnsOrderItemCount = mNewReturnOrderList.get(position).getItemsCount();
        Double newReturnsOrderTotalCost = mNewReturnOrderList.get(position)
                .getTotal();
        String newReturnsOrderStatus = mNewReturnOrderList.get(position).getStatus();
        String tempNewReturnsOrderItemCount = Integer.toString(newReturnsOrderItemCount);

        String costWithCurrency = PriceUtils.getPriceStringValue(newReturnsOrderTotalCost);

        holder.newReturnsOrderRequestNumberTextView.setText(newReturnsRequestNumber);
        holder.newReturnsOrderItemName.setText(newReturnsOrderName);
        holder.newReturnsOrderItemCountValueTextView.setText(tempNewReturnsOrderItemCount);


        holder.newReturnsOrderTotalCostValueTextView
                .setText(costWithCurrency);
        holder.newReturnsOrderStatusValueTextView.setText(newReturnsOrderStatus);


        holder.newReturnsOrderProductItem.setOnClickListener(view ->
                mListener.onReturnOrderItemClicked
                (mNewReturnOrderList.get(position)));
    }

    @Override public int getItemCount() {
        if (mNewReturnOrderList == null){
            return 0;
        }
        return mNewReturnOrderList.size();
    }

    @Override public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}
