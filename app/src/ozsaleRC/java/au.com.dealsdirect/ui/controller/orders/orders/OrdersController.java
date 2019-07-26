package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersController extends BasePullToRefreshController implements OrdersMvpView, OrderItemClickListener{
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

    @BindView(R.id.contentFrame)
    FrameLayout mContentLayout;

    @BindView(R.id.controller_orders_shop_now_button)
    Button mShopNowButton;

    public static OrdersController newInstance() {

        return new OrdersController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public OrdersController(Bundle args) {
        super(args);
    }

    OrdersRecyclerViewAdapter mAdapter;
    ArrayList<GetPaymentsList.ResponseValue.PaymentItem> mOrders = new ArrayList<>();

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container, ToolBarType.ARROW);

        setToolBarVisible(getResource().getBoolean(R.bool.orders_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_orders, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.loadOrders();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.loadOrders();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mToolbarLeftView.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);
        mOrdersToolbarTitle.setText(getString(R.string.account_orders));
        mOrdersRightOption.setImageDrawable(null);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        showLoading();
        mPresenter.loadOrders();

        mShopNowButton.setOnClickListener(view1 -> {
            mActivity.getHomeController().getCurrentRouter().popToRoot();
            mActivity.getHomeController().showFirstTabController();
        });

        mActivity.getMainController().setViewpagerDraggable(false);
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    public void showOrders(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders) {

        boolean hasOrders = orders.size() > 0;

        if (hasOrders) {
            mOrders = orders;
            mAdapter = new OrdersRecyclerViewAdapter(mActivity,this, orders);
            mRecyclerView.setAdapter(mAdapter);
        }

        mContentLayout.setVisibility(hasOrders ? View.VISIBLE : View.GONE);
        mPlaceholderLayout.setVisibility(hasOrders ? View.GONE : View.VISIBLE);
        mShopNowButton.setVisibility(hasOrders ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showOrderDetails(int position) {

    }

    @Override
    public void showOrderDetails(String referenceNumber, HashMap<String, String> status, String link, HashMap<String, String> estimatedDelivery) {
        getRouter().pushController(RouterTransaction.with(new OrderDetailsController(referenceNumber,
                status, link, estimatedDelivery))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }

    @Override
    public void onOrderItemClick(String referenceNumber, HashMap<String, String> status, String link, HashMap<String, String> estimatedDelivery) {
        showOrderDetails(referenceNumber, status, link, estimatedDelivery);
    }

    @Override
    public void onOrderItemTrackingButtonClick(String url, String errorMessage) {
        ActivityLaunchUtil.launchActivity(mActivity, url, errorMessage);
    }

    @Override
    public void onOrderItemShowOptions(ArrayList<String> arrayList, HashMap<String,String> hashMap) {
        mActivity.showBottomDialog(arrayList, hashMap);
    }
}
