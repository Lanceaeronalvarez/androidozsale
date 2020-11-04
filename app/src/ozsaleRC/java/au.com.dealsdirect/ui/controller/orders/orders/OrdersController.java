package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.paginate.Paginate;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedSatisfactionValue;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.orders.BottomSheetOrderSatisfactionDialog;
import au.com.dealsdirect.ui.controller.orders.menu.OrdersMenuHelper;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingClickListener;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PaginateUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersController extends BaseController implements OrdersMvpView, OrderItemClickListener, OrderTrackingClickListener {
    @Inject
    OrdersMvpPresenter<OrdersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;

    @BindView(R.id.partial_toolbar_title)
    TextView mOrdersToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mOrdersRightOption;

    @BindView(R.id.orders_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_orders_layout)
    RelativeLayout mPlaceholderLayout;

    @BindView(R.id.controller_orders_shop_now_button)
    Button mShopNowButton;

    private final Paginate.Callbacks mPaginateCallbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {
            loadMore(monthInterval);
        }

        @Override
        public boolean isLoading() {
            return isLoadingInProgress;
        }

        @Override
        public boolean hasLoadedAllItems() {
            return hasLoadedAllItems;
        }
    };
    private Paginate mPaginateManager;

    private boolean isLoadingInProgress = false;
    private boolean hasLoadedAllItems = false;

    private final static int monthIntervalWhenEmpty = 12;
    private final static int monthInterval = 3;
    private final Set<String> previousDates = new HashSet<>();
    private String nextDate = "";
    private boolean hasTriedLongMonthInterval = false;

    private int changeAddressRequestInvoiceNumber = -1;
    private String changeAddressRequestAddress = "";

    private OrdersRecyclerViewAdapter mAdapter = null;

    private final HashMap<Integer, Boolean> hasSetSatisfaction = new HashMap<>();

    public static OrdersController newInstance() {

        return new OrdersController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public OrdersController(Bundle args) {
        super(args);
    }

    private LinkedList<GetOrdersResponse.Order> mOrders = new LinkedList<>();

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_orders, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        reloadOrders();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        reloadOrders();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
    }

    @Override
    protected void setUp(View view) {
        mToolbarLeftView.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);
        mOrdersToolbarTitle.setText(getString(R.string.account_orders));
        mOrdersRightOption.setImageDrawable(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

        if (mOrders.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mPlaceholderLayout.setVisibility(View.GONE);
            mShopNowButton.setVisibility(View.GONE);
        } else {
            initializeAdapter();
            mRecyclerView.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
            mShopNowButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        if (mOrders != null && !mOrders.isEmpty()) {
            if (previousController instanceof OrderDetailsController) {
                OrderDetailsController orderDetailsController = (OrderDetailsController) previousController;
                showOrder(orderDetailsController.getOrderDetails());
            }
        } else {
            showLoading();
            reloadOrders();
        }

        mShopNowButton.setOnClickListener(view1 -> {
            mActivity.getMainController().getCurrentRouter().popToRoot();
            mActivity.getMainController().showShopController();
        });
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    public void showOrders(GetOrdersResponse orders) {
        nextDate = orders.getNextDate();
        showOrders(orders.getOrders());
    }

    @Override
    public void showOrders(List<GetOrdersResponse.Order> response) {
        mOrders.addAll(response);

        if (mAdapter == null) {
            initializeAdapter();
        } else {
            OrdersRecyclerViewAdapter.ItemCounts itemCounts = mAdapter.addData(response);
            mAdapter.notifyItemRangeInserted(
                    itemCounts.getOldCount(), itemCounts.getNewCount() - itemCounts.getOldCount());
        }

        if (response.isEmpty()) {
            if (!hasTriedLongMonthInterval) {
                loadMore(monthIntervalWhenEmpty);
                hasTriedLongMonthInterval = true;
            } else {
                mPaginateManager.setHasMoreDataToLoad(false);
                hasLoadedAllItems = true;
                if (mOrders.isEmpty()) {
                    mRecyclerView.setVisibility(View.GONE);
                    mPlaceholderLayout.setVisibility(View.VISIBLE);
                    mShopNowButton.setVisibility(View.VISIBLE);
                }
            }
        } else {
            hasTriedLongMonthInterval = false;
            mRecyclerView.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
            mShopNowButton.setVisibility(View.GONE);
        }


        isLoadingInProgress = false;
    }

    @Override
    public void showOrder(GetOrdersResponse.Order order) {
        updateOrderAtPosition(findPositionOfOrder(order), order);
        Set<Integer> indices = mAdapter.updateItem(order);
        for (Integer index : indices) {
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void addressChanged(String newAddress) {
        for (GetOrdersResponse.Order order : mOrders) {
            for (GetOrdersResponse.Order.Invoice invoice : order.getInvoices()) {
                if (invoice.getNumber().equals(changeAddressRequestInvoiceNumber)) {
                    // TODO: when address is migrated, consider improving the new address string to be identical to the original string
                    invoice.getDelivery().setTo(newAddress);
                    changeAddressRequestInvoiceNumber = -1;
                    showOrder(order);
                    return;
                }
            }
        }
        changeAddressRequestInvoiceNumber = -1;
    }

    @Override
    public void showOrderDetails(int position) {
        GetOrdersResponse.Order order = mOrders.get(position);

        getRouter().pushController(RouterTransaction.with(OrderDetailsController.newInstance(order))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.controller_orders_shop_now_button)
    public void onShopNowButtonPressed() {
        mActivity.getMainController().getCurrentRouter().popToRoot();
        mActivity.getMainController().showShopController();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }

    @Override
    public void onOrderItemClick(int position) {
        showOrderDetails(position);
    }

    @Override
    public void onOrderItemTrackingButtonClick(String url, String errorMessage) {
        ActivityLaunchUtil.launchActivity(mActivity, url, errorMessage);
    }

    @Override
    public void onOrderItemShowOptions(View anchor, int orderNumber, String invoiceId, int invoiceNumber, List<String> actions) {
        if (mPresenter.isTablet()) {
            OrdersMenuHelper.showPopupMenu(
                    this,
                    anchor,
                    orderNumber,
                    invoiceId,
                    invoiceNumber,
                    null,
                    actions,
                    addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(invoiceId);
                        request.setInvoiceNumber(invoiceNumber);
                        mPresenter.changeDeliveryAddress(request);
                        changeAddressRequestInvoiceNumber = invoiceNumber;
                    },
                    request -> mPresenter.cancelInvoice(request));
        } else {
            OrdersMenuHelper.showOrderBottomDialog(
                    this,
                    orderNumber,
                    invoiceId,
                    invoiceNumber,
                    null,
                    actions,
                    addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(invoiceId);
                        request.setInvoiceNumber(invoiceNumber);
                        mPresenter.changeDeliveryAddress(request);
                        changeAddressRequestInvoiceNumber = invoiceNumber;
                    },
                    request -> mPresenter.cancelInvoice(request));
        }
    }

    @Override
    public void orderSatisfactionReceived(OrderReceivedRequest request, boolean hasSetSatisfactionAlready) {
        if (hasSetSatisfactionAlready) {
            hasSetSatisfaction.put(request.getInvoiceNumber(), true);
            request.setSatisfaction(null);
            mPresenter.callSetOrderReceived(request);
        } else {
            hasSetSatisfaction.put(request.getInvoiceNumber(), false);
            mActivity.showOrderSatisfactionDialog(response -> {
                hasSetSatisfaction.put(request.getInvoiceNumber(), true);
                switch (response) {
                    case BottomSheetOrderSatisfactionDialog.POSITIVE_RESPONSE:
                        request.setSatisfaction(OrderReceivedSatisfactionValue.GOOD.getValue());
                        break;
                    case BottomSheetOrderSatisfactionDialog.NEGATIVE_RESPONSE:
                        request.setSatisfaction(OrderReceivedSatisfactionValue.BAD.getValue());
                        break;
                    default:
                        request.setSatisfaction(OrderReceivedSatisfactionValue.NEUTRAL.getValue());
                        break;
                }
                mPresenter.callSetOrderReceived(request);
            });
        }
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
        if (isReceived) {
            if (hasSetSatisfaction == null) {
                mPresenter.callGetOrderReceivedSatisfaction(orderReceivedRequest);
            } else {
                orderSatisfactionReceived(orderReceivedRequest, hasSetSatisfaction);
            }
        } else {
            mPresenter.callSetOrderNotReceived(orderReceivedRequest);
        }
    }

    private void reloadOrders() {
        hasTriedLongMonthInterval = false;
        isLoadingInProgress = true;
        hasLoadedAllItems = false;

        mOrders.clear();
        if (mAdapter != null) {
            mAdapter.clearData();
            mAdapter.notifyDataSetChanged();
        }

        previousDates.clear();
        nextDate = DateUtils.getServerDateStringWithTimeZone(new Date());
        mPresenter.loadOrders(nextDate, monthInterval);
        previousDates.add(nextDate);
    }

    private int findPositionOfOrder(GetOrdersResponse.Order order) {
        if (order == null) {
            return -1;
        }
        for (int i = 0; i < mOrders.size(); i++) {
            if (mOrders.get(i).getNumber().equals(order.getNumber())) {
                return i;
            }
        }
        return -1;
    }

    private void updateOrderAtPosition(int position, GetOrdersResponse.Order order) {
        if (position < 0 || position >= mOrders.size()) {
            return;
        }

        mOrders.remove(position);
        mOrders.add(position, order);
    }

    private void loadMore(int interval) {
        if (nextDate != null && !nextDate.isEmpty() && !previousDates.contains(nextDate)) {
            isLoadingInProgress = true;
            mPresenter.loadOrders(nextDate, interval);
            previousDates.add(nextDate);
        }
    }

    private void initializeAdapter() {
        if (mAdapter == null) {
            mAdapter = new OrdersRecyclerViewAdapter(this, this, mOrders);
            mAdapter.setSubtitle(getResources().getString(R.string.my_orders));
        }
        mRecyclerView.setAdapter(mAdapter);
        if (mPaginateManager != null) {
            mPaginateManager.unbind();
        }
        mPaginateManager = PaginateUtils.init(mRecyclerView, mPaginateCallbacks);
    }
}
