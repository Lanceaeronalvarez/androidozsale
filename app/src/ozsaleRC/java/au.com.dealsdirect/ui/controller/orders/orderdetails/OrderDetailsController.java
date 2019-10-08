package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsController extends BaseController implements OrderDetailsMvpView, OrderDetailsClickListener {

    private static final String PAYMENT_ITEM = "PAYMENT_ITEM";
    private static final String PAYMENT_REF_NO = "PAYMENT_REF_NO";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";
    private static final String STATUS = "STATUS";
    private static final String LINK = "LINK";
    private static final String ESTIMATED_DELIVERY = "ESTIMATED_DELIVERY";
    private static final String ORDER_DETAILS = "ORDER_DETAILS";

    public abstract static class Parameters {
        private Parameters() {
        }

        public static final class FromOrdersList extends Parameters {
            String mPaymentRefNo;
            HashMap<String, String> mStatus;
            String mLink;
            HashMap<String, String> mEstimatedDelivery;
            GetPaymentsList.ResponseValue.PaymentItem mOrders;

            public FromOrdersList(String paymentRefNo,
                                  HashMap<String, String>status,
                                  String link,
                                  HashMap<String, String>estimatedDelivery,
                                  GetPaymentsList.ResponseValue.PaymentItem orderDetails) {

                mPaymentRefNo = paymentRefNo;
                mStatus = status;
                mLink = link;
                mEstimatedDelivery = estimatedDelivery;
                mOrders = orderDetails;

            }

            public String getPaymentRefNo() {
                return mPaymentRefNo;
            }

            public HashMap<String, String> getStatus() {
                return mStatus;
            }

            public String getLink() {
                return mLink;
            }

            public HashMap<String, String> getEstimatedDelivery() {
                return mEstimatedDelivery;
            }

            public GetPaymentsList.ResponseValue.PaymentItem getOrderDetails() {
                return mOrders;
            }
        }
    }

    @Inject
    OrderDetailsMvpPresenter<OrderDetailsMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mOrderDetailsToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mOrderDetailsRightOption;

    @BindView(R.id.order_details_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.delivery_price_text_view)
    TextView mDeliveryPriceTextValue;

    @BindView(R.id.voucher_payment_text_view)
    TextView mVoucherPaymentTextView;

    @BindView(R.id.credit_card_payment_text_view)
    TextView mCreditCardPaymentTextView;

    @BindView(R.id.total_text_view)
    TextView mTotalTextView;

    @BindView(R.id.delivery_address_text_view)
    TextView mDeilveryAddressTextView;

    @BindView(R.id.approved_date_text_view)
    TextView mApprovedDateTextView;

    String mPaymentReferenceNo;
    HashMap<String, String> mStatus;
    String mLink;
    HashMap<String, String> mEstimatedDelivery;
    GetOrderPaymentDetails.ResponseValue.Value mOrderDetails;
    GetPaymentsList.ResponseValue.PaymentItem mOrders;

    public OrderDetailsController(String paymentRefNo, HashMap<String, String> status, String link,
                                  HashMap<String, String> estimatedDelivery,
                                  GetOrderPaymentDetails.ResponseValue.Value orderDetails) {
        this(new BundleBuilder(new Bundle())
                .putString(PAYMENT_REF_NO, paymentRefNo)
                .putSerializable(STATUS, status)
                .putString(LINK, link)
                .putSerializable(ESTIMATED_DELIVERY, estimatedDelivery)
                .putString(ORDER_DETAILS, orderDetails.toString())
                .build());
    }

    public static OrderDetailsController newInstance(Parameters parameters) {
        OrderDetailsController controller = new OrderDetailsController(
                new BundleBuilder(new Bundle()).build());

        controller.mPaymentReferenceNo = ((Parameters.FromOrdersList) parameters).getPaymentRefNo();
        controller.mStatus = ((Parameters.FromOrdersList) parameters).getStatus();
        controller.mLink = ((Parameters.FromOrdersList) parameters).getLink();
        controller.mEstimatedDelivery = ((Parameters.FromOrdersList) parameters).getEstimatedDelivery();
        controller.mOrders = ((Parameters.FromOrdersList) parameters).getOrderDetails();

        return controller;
    }


    public OrderDetailsController(Bundle args) {
        super(args);
        mPaymentReferenceNo = args.getString(PAYMENT_REF_NO, "");
        mStatus = (HashMap<String, String>) args.getSerializable(STATUS);
        mLink = args.getString(LINK, "");
        mEstimatedDelivery = (HashMap<String, String>) args.getSerializable(ESTIMATED_DELIVERY);
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PAYMENT_REF_NO, mPaymentReferenceNo);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPaymentReferenceNo = savedInstanceState.getString(PAYMENT_REF_NO, "");
    }

    @Override
    protected void setUp(View view) {

        mRecyclerView.setAdapter(new OrderDetailsRecyclerViewAdapter(mActivity,null,this, mStatus, mLink,
                mEstimatedDelivery, mOrders));
        mRecyclerView.addItemDecoration(new OrderDetailItemDecorator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
    }

    @Override
    public void onViewWillAppear(Controller previousController) {

        if (mOrderDetailsToolbarTitle != null) {
            mOrderDetailsToolbarTitle.setText(getString(R.string.account_orders));
            mOrderDetailsRightOption.setImageDrawable(null);
        }

    }

    @Override
    public void onViewDidAppear(Controller previousController) {

        mRecyclerView.setAdapter(new OrderDetailsRecyclerViewAdapter(mActivity,null,this, mStatus, mLink,
                mEstimatedDelivery, mOrders));
        mRecyclerView.addItemDecoration(new OrderDetailItemDecorator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);

        GetOrderPaymentDetails.RequestValues requestValues = new GetOrderPaymentDetails.RequestValues(mPaymentReferenceNo);
        mPresenter.loadOrderDetails(requestValues);
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
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

        mOrderDetailsToolbarTitle.setText(String.format(getString(R.string.order_sharp), String.valueOf(orderDetails.getPaymentReferenceNo())));

        //price breakdown
        mDeliveryPriceTextValue.setText(PriceUtils.getPriceStringValue(orderDetails.getTotal().getDeliveryAmount()));
        mVoucherPaymentTextView.setText(PriceUtils.getPriceStringValue(orderDetails.getTotal().getDiscountAmount()));
        mCreditCardPaymentTextView.setText(PriceUtils.getPriceStringValue(orderDetails.getTotal().getCreditCardAmount()));
        mTotalTextView.setText(PriceUtils.getPriceStringValue(orderDetails.getTotal().getTotalAmount()));

        //delivery details
        //take the first address of the first item since all of the items have the same address
        String date = DateUtils.getDateFromStringInFormat(orderDetails.getApprovedDate(), AppConstants.MP_DATE_TIME_FORMAT);
        mDeilveryAddressTextView.setText(orderDetails.getOrders().get(0).getDeliveryAddress());
        mApprovedDateTextView.setText(date);

        //set adapter
        mRecyclerView.setAdapter(new OrderDetailsRecyclerViewAdapter(mActivity,orderDetails,this, mStatus, mLink,
                mEstimatedDelivery, null));
        mRecyclerView.addItemDecoration(new OrderDetailItemDecorator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
    }

    @Override
    public void showOrderTrackingWeb(String link) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.SOURCE, DataCollector.EventParameters.ViewSource.ORDER_DETAILS);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, OrderDetailsController.class.getSimpleName());
        DataCollector.logEvent(Events.CVOrderTrack, parameters);

        ActivityLaunchUtil.launchActivity(mActivity, link, getString(R.string.no_order_tracking_message));
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }

    @Override
    public void showOrderDialog(View view, ArrayList<String> arrayList, HashMap<String,String> hashMap) {

        if (mPresenter.isTablet()) {
            mActivity.showPopupMenu(view, arrayList, hashMap);
        } else {
            mActivity.showOrderBottomDialog(arrayList, hashMap);
        }
    }

    @Override
    public void callOrderReceived(String orderID) {

        OrderReceivedRequest orderReceivedRequest = new OrderReceivedRequest();
        orderReceivedRequest.setOrderId(orderID);
        orderReceivedRequest.setSatisfaction("");
        mPresenter.callOrderReceived(orderReceivedRequest);
    }
}
