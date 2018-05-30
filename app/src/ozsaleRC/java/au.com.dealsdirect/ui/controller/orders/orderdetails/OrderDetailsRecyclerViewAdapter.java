package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsRecyclerViewAdapter extends RecyclerView.Adapter<OrderDetailsRecyclerViewAdapter.OrdersViewHolder> {


    private View mView;
    private GetOrderPaymentDetails.ResponseValue.Value mOrderDetails;

    private ArrayList<GetPaymentsList.ResponseValue.Order> mOrderList = new ArrayList<>();
    private GetPaymentsList.ResponseValue.Total mTotal;
    private RecyclerView mItemsRecyclerView;

    private Context mContext;
    private int mPaymentReferenceNo;

    private OrderDetailsMvpPresenter<OrderDetailsMvpView> mPresenter;

    public OrderDetailsRecyclerViewAdapter(
            GetOrderPaymentDetails.ResponseValue.Value orderDetails,
            int paymentReferenceNo,
            ArrayList<GetPaymentsList.ResponseValue.Order> orderList,
            GetPaymentsList.ResponseValue.Total total,
            Context context,
            OrderDetailsMvpPresenter<OrderDetailsMvpView> presenter) {

        this.mOrderList = orderList;
        this.mContext = context;
        this.mPaymentReferenceNo = paymentReferenceNo;
        this.mOrderDetails = orderDetails;
        this.mTotal = total;
        this.mPresenter = presenter;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_order_order_details, parent, false);
        OrdersViewHolder holder = new OrdersViewHolder(v);
        mView = v;

        return holder;
    }

    @Override
    public void onBindViewHolder(OrdersViewHolder holder, final int position) {

        mItemsRecyclerView = (RecyclerView)
                mView.findViewById(R.id.order_items_recyclerview);

        mItemsRecyclerView.setAdapter(new OrderDetailsItemsRecyclerViewAdapter(
                mContext,
                position,
                mOrderDetails,
                mOrderList));

        mItemsRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager
                .VERTICAL, false));


        GetPaymentsList.ResponseValue.Order item = mOrderList.get(position);

        holder.trackHereTextView.setVisibility(item.getLink().isEmpty() ? View.GONE : View.VISIBLE);

        String approvedTime = DateUtils.getTimeFromDateString(item.getTracker().getApprovedDate());

        holder.orderStatusTextView.setText(item.getStatus());

        String orderDateValue = DateUtils.getTrimmedServerDateString(item.getTracker().getApprovedDate());

        holder.orderDateTextView.setText(orderDateValue + " at " + approvedTime);

        holder.deliveryTextValue.setText(mOrderDetails.getOrders().get(position).getDeliveryAddress());

        holder.trackHereTextView.setOnClickListener(v -> mPresenter.showTrackingWeb(item.getLink()));
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void replace(ArrayList<GetPaymentsList.ResponseValue.Order> orders) {
        mOrderList = new ArrayList<>(orders);
        notifyDataSetChanged();
    }

    static class OrdersViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.order_items_recyclerview)
        RecyclerView mItemsRecyclerView;

        @BindView(R.id.delivery_text_view)
        TextView deliveryTextValue;

        @BindView(R.id.order_date_text_view)
        TextView orderDateTextView;

        @BindView(R.id.status_text_view)
        TextView orderStatusTextView;

        @BindView(R.id.controller_order_track_here_text)
        TextView trackHereTextView;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
