package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.ui.custom.SimpleDividerItemDecoration;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersController extends BasePullToRefreshController implements OrdersMvpView {
    @Inject
    OrdersMvpPresenter<OrdersMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;

    @BindView(R.id.partial_toolbar_title)
    TextView mOrdersToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mOrdersRightOption;

    @BindView(R.id.orders_recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.no_orders_layout)
    RelativeLayout mPlaceholderLayout;

    @BindView(R.id.contentFrame)
    FrameLayout mContentLayout;

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
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mToolbarLeftView.setVisibility(mActivity.isTablet() ? View.GONE : View.VISIBLE);
        mOrdersToolarTitle.setText(mActivity.getResources().getString(R.string.account_orders));
        mOrdersRightOption.setImageDrawable(null);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> showOrderDetails(position)));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mActivity, LinearLayout.VERTICAL));
        mPresenter.loadOrders();
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    public void showOrders(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders) {
        if (orders.size() > 0) {
            mOrders = orders;
            mAdapter = new OrdersRecyclerViewAdapter(mActivity, orders);
            mRecyclerView.setAdapter(mAdapter);
            mContentLayout.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);

        } else {
            mContentLayout.setVisibility(View.GONE);
            mPlaceholderLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showOrderDetails(int position) {
        String paymentRefNo = String.valueOf(mOrders.get(position).getPaymentReferenceNo());
        String paymentItemString = new Gson().toJson(mOrders.get(position));
        getRouter().pushController(RouterTransaction.with(new OrderDetailsController(paymentItemString, paymentRefNo, position))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        mActivity.onBackPressed();
    }
}
