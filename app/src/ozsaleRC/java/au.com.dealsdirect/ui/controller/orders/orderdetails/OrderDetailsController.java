package au.com.dealsdirect.ui.controller.orders.orderdetails;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedSatisfactionValue;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.orders.BottomSheetOrderSatisfactionDialog;
import au.com.dealsdirect.ui.controller.orders.menu.OrdersMenuHelper;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingClickListener;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsController extends BaseController implements OrderDetailsMvpView, OrderDetailsClickListener, OrderTrackingClickListener {

    private static final String PAYMENT_ITEM = "PAYMENT_ITEM";
    private static final String ORDER_NUMBER = "ORDER_NUMBER";
    private static final String SELECTED_ITEM = "SELECTED_ITEM";
    private static final String STATUS = "STATUS";
    private static final String LINK = "LINK";
    private static final String SHIP_FROM = "SHIP_FROM";
    private static final String ESTIMATED_DELIVERY = "ESTIMATED_DELIVERY";
    private static final String SHIP_TO = "SHIP_TO";
    private static final String ORDER_DETAILS = "ORDER_DETAILS";

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

    private int mOrderNumber = 0;
    private GetOrdersResponse.Order mOrderDetails = null;

    private OrderDetailsRecyclerViewAdapter mAdapter = null;

    HashMap<Integer, Boolean> hasSetSatisfaction = new HashMap<>();

    public static OrderDetailsController newInstance(int orderNumber) {
        OrderDetailsController controller = new OrderDetailsController(
                new BundleBuilder(new Bundle()).build());

        controller.mOrderNumber = orderNumber;
        controller.mOrderDetails = null;

        return controller;
    }

    public static OrderDetailsController newInstance(GetOrdersResponse.Order preloadedOrderDetails) {
        OrderDetailsController controller = new OrderDetailsController(
                new BundleBuilder(new Bundle()).build());

        controller.mOrderNumber = preloadedOrderDetails.getNumber();
        controller.mOrderDetails = preloadedOrderDetails;

        return controller;
    }


    public OrderDetailsController(Bundle args) {
        super(args);
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
        outState.putInt(ORDER_NUMBER, mOrderNumber);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOrderNumber = savedInstanceState.getInt(ORDER_NUMBER, 0);
    }

    @Override
    protected void setUp(View view) {
        mAdapter = new OrderDetailsRecyclerViewAdapter(mOrderDetails, this, this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                getItemDecorationRecyclerView(parent, view, outRect);
            }
        });

        mOrderDetailsToolbarTitle.setText(String.format(getString(R.string.order_sharp), Integer.toString(mOrderNumber)));
        mOrderDetailsRightOption.setImageDrawable(null);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        ViewCompat.setNestedScrollingEnabled(mRecyclerView, false);
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        mPresenter.loadOrderDetails(mOrderNumber);
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.loadOrderDetails(mOrderNumber);
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    private int getItemDecorationRecyclerView(RecyclerView parent, View view, Rect outRect) {
        int position = parent.getChildAdapterPosition(view);
        int viewType = parent.getAdapter().getItemViewType(position);

        if (viewType == OrderDetailsRecyclerViewAdapter.VIEW_TYPE_SALE_NAME && position != 0) {
            float margin = mActivity.getResources().getDisplayMetrics().density *
                    mActivity.getResources().getDimension(R.dimen.margin_small);
            return outRect.top = (int) margin;
        }

        return 0;
    }

    @Override
    public void showOrderDetails(GetOrdersResponse.Order orderDetails) {
        mOrderDetails = orderDetails;

        for (GetOrdersResponse.Order.Invoice invoice : mOrderDetails.getInvoices()) {
            OrderReceivedRequest request = new OrderReceivedRequest();
            request.setInvoiceId(invoice.getId());
            request.setInvoiceNumber(invoice.getNumber());
            mPresenter.callGetOrderReceivedSatisfaction(request);
        }

        //price breakdown
        mDeliveryPriceTextValue.setText(PriceUtils.getPriceStringValue(orderDetails.getPayment().getDeliveryAmount()));
        mVoucherPaymentTextView.setText(PriceUtils.getPriceStringValue(orderDetails.getPayment().getDiscountAmount()));
        mCreditCardPaymentTextView.setText(PriceUtils.getPriceStringValue(orderDetails.getPayment().getPaymentAmount()));
        mTotalTextView.setText(PriceUtils.getPriceStringValue(orderDetails.getPayment().getTotalAmount()));

        mAdapter.replaceData(mOrderDetails);
        mAdapter.notifyDataSetChanged();
        ;
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
    public void showOrderDialog(String invoiceId, int invoiceNumber, List<String> invoiceActions) {
        if (mPresenter.isTablet()) {
            OrdersMenuHelper.showPopupMenu(
                    mActivity.getMainController(),
                    invoiceId,
                    invoiceNumber,
                    null,
                    invoiceActions,
                    addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(invoiceId);
                        request.setInvoiceNumber(invoiceNumber);
                        mPresenter.changeDeliveryAddress(request);
                    },
                    request -> mPresenter.cancelInvoiceItem(request));
        } else {
            OrdersMenuHelper.showOrderBottomDialog(
                    this,
                    invoiceId,
                    invoiceNumber,
                    null,
                    invoiceActions,
                    addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(invoiceId);
                        request.setInvoiceNumber(invoiceNumber);
                        mPresenter.changeDeliveryAddress(request);
                    },
                    request -> mPresenter.cancelInvoiceItem(request));
        }
    }

    @Override
    public void showOrderDialog(String invoiceId, int invoiceNumber, GetOrdersResponse.Order.Invoice.Product product) {
        if (mPresenter.isTablet()) {
            OrdersMenuHelper.showPopupMenu(
                    this,
                    invoiceId,
                    invoiceNumber,
                    product,
                    product.getActions(),
                    addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(invoiceId);
                        request.setInvoiceNumber(invoiceNumber);
                        mPresenter.changeDeliveryAddress(request);
                    },
                    request -> mPresenter.cancelInvoiceItem(request));
        } else {
            OrdersMenuHelper.showOrderBottomDialog(
                    this,
                    invoiceId,
                    invoiceNumber,
                    product,
                    product.getActions(),
                    addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(invoiceId);
                        request.setInvoiceNumber(invoiceNumber);
                        mPresenter.changeDeliveryAddress(request);
                    },
                    request -> mPresenter.cancelInvoiceItem(request));
        }
    }

    @Override
    public void orderSatisfactionReceived(int invoiceNumber, boolean hasSetSatisfaction) {
        this.hasSetSatisfaction.put(invoiceNumber, hasSetSatisfaction);
    }

    @Override
    public void onOrderItemTrackingButtonClick(String url, String errorMessage) {
        ActivityLaunchUtil.launchActivity(mActivity, url, errorMessage);
    }

    @Override
    public void onNodeTapped(GetOrdersResponse.Order.Invoice.Delivery.Step upperStep, GetOrdersResponse.Order.Invoice.Delivery.Step lowerStep) {
        mActivity.showOrderTrackingStepBottomDialog(upperStep, lowerStep);
    }

    @Override
    public void onOrderReceivedToggle(String invoiceId, int invoiceNumber, boolean isReceived) {
        OrderReceivedRequest orderReceivedRequest = new OrderReceivedRequest();
        orderReceivedRequest.setInvoiceId(invoiceId);
        orderReceivedRequest.setInvoiceNumber(invoiceNumber);
        Boolean hasSetSatisfaction = this.hasSetSatisfaction.get(invoiceNumber);
        if (hasSetSatisfaction == null) {
            return;
        }
        if (isReceived) {
            if (hasSetSatisfaction) {
                orderReceivedRequest.setSatisfaction(null);
                mPresenter.callSetOrderReceived(orderReceivedRequest);
            } else {
                mActivity.showOrderSatisfactionDialog(response -> {
                    OrderDetailsController.this.hasSetSatisfaction.put(invoiceNumber, true);
                    switch (response) {
                        case BottomSheetOrderSatisfactionDialog.POSITIVE_RESPONSE:
                            orderReceivedRequest.setSatisfaction(OrderReceivedSatisfactionValue.GOOD.getValue());
                            break;
                        case BottomSheetOrderSatisfactionDialog.NEGATIVE_RESPONSE:
                            orderReceivedRequest.setSatisfaction(OrderReceivedSatisfactionValue.BAD.getValue());
                            break;
                        default:
                            orderReceivedRequest.setSatisfaction(OrderReceivedSatisfactionValue.NEUTRAL.getValue());
                            break;
                    }
                    mPresenter.callSetOrderReceived(orderReceivedRequest);
                });
            }
        } else {
            mPresenter.callSetOrderNotReceived(orderReceivedRequest);
        }
    }

    @Override
    public void onReceivedSet(int invoiceNumber) {
        mPresenter.loadOrderDetails(mOrderNumber);
    }

    @Override
    public void addressChanged() {
        CustomAlertDialog.showCustomAlertDialog(
                getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                mActivity.getString(R.string.address_change_success));
        mPresenter.loadOrderDetails(mOrderNumber);
    }

    public GetOrdersResponse.Order getOrderDetails() {
        return mOrderDetails;
    }
}
