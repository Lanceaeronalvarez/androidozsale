package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsController extends SwipeableBaseToolBarController implements OrderDetailsMvpView {

    private static final String PAYMENT_ITEM = "PAYMENT_ITEM";
    private static final String PAYMENT_REF_NO = "PAYMENT_REF_NO";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";

    @Inject
    OrderDetailsMvpPresenter<OrderDetailsMvpView> mPresenter;

    @BindView(R.id.order_details_recyclerview)
    RecyclerView mRecyclerView;

    GetPaymentsList.ResponseValue.PaymentItem mOrderItem;
    GetOrderPaymentDetails.ResponseValue.Value mOrderDetails;

    int mSelectedPosition;
    String mPaymentReferenceNo;

    public OrderDetailsController(String paymentItemString, String paymentRefNo, int position) {
        this(new BundleBuilder(new Bundle())
                .putString(PAYMENT_ITEM, paymentItemString)
                .putString(PAYMENT_REF_NO, paymentRefNo)
                .putInt(SELECTED_ITEM, position)
                .build());
    }


    public OrderDetailsController(Bundle args) {
        super(args);
        mOrderItem = new Gson().fromJson(args.getString(PAYMENT_ITEM, ""), GetPaymentsList.ResponseValue.PaymentItem.class);
        mSelectedPosition = args.getInt(SELECTED_ITEM, 0);
        mPaymentReferenceNo = args.getString(PAYMENT_REF_NO, "");
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_orders_details, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mToolbarTitle.setText(R.string.my_orders);
        setupSwipingBehavior();
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        GetOrderPaymentDetails.RequestValues requestValues =
                new GetOrderPaymentDetails.RequestValues(mPaymentReferenceNo);
        mPresenter.loadOrderDetails(requestValues);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showOrderDetails(GetOrderPaymentDetails.ResponseValue response) {
        if (response != null) {
            mOrderDetails = response.getD().getValue();
        }

        if (mOrderItem != null) {
            mRecyclerView.setAdapter(new OrderDetailsRecyclerViewAdapter(
                    mOrderDetails,
                    mOrderItem.getPaymentReferenceNo(),
                    mOrderItem.getOrders(),
                    mOrderItem.getTotal(),
                    mActivity));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void showOrderTrackingWeb(String link) {

    }

}
