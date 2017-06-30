package au.com.dealsdirect.ui.controller.returns.currentreturns.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturns;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.Item;
import au.com.dealsdirect.ui.controller.returns.currentreturns.listener.CurrentReturnClickListener;
import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnAdapter extends RecyclerView.Adapter<CurrentReturnViewHolder> {

    private final CurrentReturnClickListener mListener;
    private int lastPosition = -1;
    private int currentPosition = 0;
    private CurrentReturnViewHolder mCurrentReturnsHolder;

    List<CurrentReturns> mCurrentReturnList = Collections.emptyList();
    List<GetReturnDetailsResponseBody> mReturnDetailsResponseBodyList = new LinkedList<>();

    Context mContext;

    public CurrentReturnAdapter(
            List<CurrentReturns> currentReturnsList,
            List<GetReturnDetailsResponseBody> returnDetailsResponseBodyList,
            Context context,
            CurrentReturnClickListener listener){

        this.mCurrentReturnList = currentReturnsList;
        this.mReturnDetailsResponseBodyList = returnDetailsResponseBodyList;
        this.mContext = context;
        this.mListener = listener;
    }


    @Override
    public CurrentReturnViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_current_return, parent,
                        false);
        CurrentReturnViewHolder holder = new CurrentReturnViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(CurrentReturnViewHolder holder, int position) {
        String cardViewTransition = mCurrentReturnList.get(position).getID();

        holder.currentReturnProductItem.setTransitionName(cardViewTransition);
        mCurrentReturnsHolder = holder;
        currentPosition = position;

        String productName = mCurrentReturnList.get(position).getRan();

//        int productRequestNumber = mCurrentReturnList.get(position).getOrderNumber();
//        String productRequestDate = mCurrentReturnList.get(position).getLastSavedDate();
//        String returnRequestDateFormat = DateUtils.getTrimmedServerDateString(productRequestDate);
        String isRequestApproved;

        boolean isProductReturnRequestApprovedBoolean;
        Object isProductReturnRequestApprovedObject
                = mCurrentReturnList.get(position).getApprovedDate();

        if (isProductReturnRequestApprovedObject == null){
            isProductReturnRequestApprovedBoolean = false;
            isRequestApproved = "pending";
        }else {
            isProductReturnRequestApprovedBoolean = true;
            isRequestApproved = "YES";
        }

        String productRequestStatus = mCurrentReturnList.get(position).getReturnStatus();
        String productRAN = mCurrentReturnList.get(position).getRan();

//        holder.currentReturnsRequestNumberValueTextView.setText(productRequestNumber+"");
        holder.currentReturnsRequestProductNameValueTextView.setText(productName);
//        holder.currentReturnsRequestDateValueTextView.setText(returnRequestDateFormat);

        holder.currentReturnsRequestIsApprovedValueTextView.setText(isRequestApproved);

        holder.currentReturnsRequestStatusValueTextView.setText(productRequestStatus);
        holder.currentReturnsRequestRANValueTextView.setText(productRAN);

        if (mReturnDetailsResponseBodyList.size()!=0){
            updateHolderReturnItems(holder, position);
        }
//
        holder.currentReturnProductItem.setOnClickListener(view -> mListener.onCurrentReturnClickListener(
                holder,
                position,
                productRequestStatus,
                productRAN,
                "22",
                isRequestApproved,
                mCurrentReturnList.get(position).getID()));

//        holder.currentReturnItemsRecyclerView.setOnClickListener(view -> mListener.onCurrentReturnClickListener(
//                holder,
//                position,
//                productRequestStatus,
//                productRAN,
//                "33",
//                isRequestApproved,
//                mCurrentReturnList.get(position).getID()));

        //setAnimation(holder.currentReturnProductItem, position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mCurrentReturnList.size();
    }

    private void updateHolderReturnItems(CurrentReturnViewHolder holder, int position) {

        if (mReturnDetailsResponseBodyList.size() > position) {

            List<Item> items = mReturnDetailsResponseBodyList.get(position)
                    .getValue()
                    .getItems();
            Double subTotal = mReturnDetailsResponseBodyList.get(position)
                    .getValue()
                    .getTotal();

//            final MyReturnDetailsRecyclerViewAdapter adapter =
//                    new MyReturnDetailsRecyclerViewAdapter(items, subTotal, mContext);
//
//
//            holder.currentReturnItemsRecyclerView.setAdapter(adapter);
//
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally(){
                    return false;
                }
            };

//            recyclerView.setLayoutManager(linearLayoutManager);

            holder.currentReturnItemsRecyclerView.setLayoutManager(linearLayoutManager);
            holder.currentReturnItemsRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    public void updateReturnDetailsResponseBody(List<GetReturnDetailsResponseBody> list){
        mReturnDetailsResponseBodyList = list;
        notifyDataSetChanged();
    }
}
