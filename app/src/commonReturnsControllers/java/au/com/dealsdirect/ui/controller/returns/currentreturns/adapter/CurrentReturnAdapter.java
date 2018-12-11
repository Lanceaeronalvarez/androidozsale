package au.com.dealsdirect.ui.controller.returns.currentreturns.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturns;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.Item;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsMvpPresenter;
import au.com.dealsdirect.ui.controller.returns.currentreturns.listener.CurrentReturnClickListener;
import au.com.dealsdirect.ui.controller.returns.currentreturns.viewholder.CurrentReturnViewHolder;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAdapter;
import au.com.dealsdirect.utils.DateUtils;

/**
 * dp Created by Admin on 6/29/17.
 */

public class CurrentReturnAdapter extends RecyclerView.Adapter<CurrentReturnViewHolder> {

    private CurrentReturnsMvpPresenter mPresenter;

    List<CurrentReturns> mCurrentReturnList = Collections.emptyList();
    List<GetReturnDetailsResponseBody> mReturnDetailsResponseBodyList = new LinkedList<>();

    Context mContext;

    public CurrentReturnAdapter(
            List<CurrentReturns> currentReturnsList,
            List<GetReturnDetailsResponseBody> returnDetailsResponseBodyList,
            Context context,
            CurrentReturnsMvpPresenter mvpPresenter) {

        this.mCurrentReturnList = currentReturnsList;
        this.mReturnDetailsResponseBodyList = returnDetailsResponseBodyList;
        this.mContext = context;
        mPresenter = mvpPresenter;
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

        String productName = mCurrentReturnList.get(position).getDescription();

        int productRequestNumber = mCurrentReturnList.get(position).getInvoiceNo();
        int orderNumber = mCurrentReturnList.get(position).getOrderNumber();
        String productRequestDate = mCurrentReturnList.get(position).getLastSavedDate();
        String returnRequestDateFormat = DateUtils.getTrimmedServerDateString(productRequestDate);

        Object isProductReturnRequestApprovedObject
                = mCurrentReturnList.get(position).getApprovedDate();

        String productApproveDate = "";
        String returnApproveDateFormat = "";

        if (isProductReturnRequestApprovedObject != null) {
            productApproveDate = isProductReturnRequestApprovedObject.toString();
            returnApproveDateFormat = DateUtils.getTrimmedServerDateString(productApproveDate);

        }

        String productRequestStatus = mCurrentReturnList.get(position).getReturnStatus();
        String productRAN = mCurrentReturnList.get(position).getRan();

        holder.currentReturnsRequestNumberValueTextView.setText(productRequestNumber + "");
        holder.currentReturnsRequestProductNameValueTextView.setText(productName);
        holder.currentReturnsRequestDateValueTextView.setText(returnRequestDateFormat);

        holder.currentReturnsRequestIsApprovedValueTextView.setText(returnApproveDateFormat);

        holder.currentReturnsRequestStatusValueTextView.setText(productRequestStatus);
        holder.currentReturnsRequestRANValueTextView.setText(productRAN);

        if (mReturnDetailsResponseBodyList.size() != 0) {
            updateHolderReturnItems(holder, position);
        }

        holder.currentReturnProductItem.setOnClickListener(view -> mPresenter.currentReturnSelected(
                orderNumber,
                position,
                productName,
                productRequestStatus,
                productRAN,
                returnRequestDateFormat,
                holder.currentReturnsRequestIsApprovedValueTextView.getText().toString(),
                mCurrentReturnList.get(position).getID()));

        holder.currentReturnItemsRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                mPresenter.currentReturnSelected(
                        orderNumber,
                        position,
                        productName,
                        productRequestStatus,
                        productRAN,
                        returnRequestDateFormat,
                        holder.currentReturnsRequestIsApprovedValueTextView.getText().toString(),
                        mCurrentReturnList.get(position).getID());
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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

            Log.d("itemiterator", "items = " + items.size() + " , subtotal = " + subTotal + " content = " + items.get(0).getCount() + " , " + items.get(0).getPrice());

            final ReturnDetailsAdapter adapter =
                    new ReturnDetailsAdapter(items, subTotal, mContext);
//

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            };

//            recyclerView.setLayoutManager(linearLayoutManager);

            holder.currentReturnItemsRecyclerView.setLayoutManager(linearLayoutManager);
            holder.currentReturnItemsRecyclerView.setNestedScrollingEnabled(false);

            holder.currentReturnItemsRecyclerView.setAdapter(adapter);

        }
    }

    public void updateReturnDetailsResponseBody(List<GetReturnDetailsResponseBody> list) {
        mReturnDetailsResponseBodyList = list;
        notifyDataSetChanged();
    }
}
