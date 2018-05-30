package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsController extends BaseController implements OrderDetailsMvpView {

    private static final String PAYMENT_ITEM = "PAYMENT_ITEM";
    private static final String PAYMENT_REF_NO = "PAYMENT_REF_NO";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";

    @Inject
    OrderDetailsMvpPresenter<OrderDetailsMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mOrderDetailsToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mOrderDetailsRightOption;

    @BindView(R.id.order_details_recyclerview)
    RecyclerView mRecyclerView;

    GetPaymentsList.ResponseValue.PaymentItem mOrderItem;

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
        mPaymentReferenceNo = args.getString(PAYMENT_REF_NO, "");
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_orders_details, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mOrderDetailsToolarTitle.setText(getString(R.string.order_sharp_colon) + mOrderItem.getPaymentReferenceNo());
        mOrderDetailsRightOption.setImageDrawable(null);

        GetOrderPaymentDetails.RequestValues requestValues = new GetOrderPaymentDetails.RequestValues(mPaymentReferenceNo);
        mPresenter.loadOrderDetails(requestValues);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showOrderDetails(GetOrderPaymentDetails.ResponseValue response) {
        GetOrderPaymentDetails.ResponseValue.Value orderDetails = response.getD().getValue();

        if (mOrderItem != null) {
            mRecyclerView.setAdapter(new OrderDetailsRecyclerViewAdapter(
                    orderDetails,
                    mOrderItem.getPaymentReferenceNo(),
                    mOrderItem.getOrders(),
                    mOrderItem.getTotal(),
                    mActivity,
                    mPresenter));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        }
    }

    @Override
    public void showOrderTrackingWeb(String link) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }
}
