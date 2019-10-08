package au.com.dealsdirect.ui.controller.orders.orders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.gson.Gson;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersController extends SwipeableBaseToolBarController implements OrdersMvpView {

    @Inject
    OrdersMvpPresenter<OrdersMvpView> mPresenter;

    @BindView(R.id.orders_recycler_view)
    RecyclerViewPager mRecyclerView;
    @BindView(R.id.no_orders_layout)
    RelativeLayout mPlaceholderLayout;

    OrdersRecyclerViewAdapter mAdapter;
    ArrayList<GetPaymentsList.ResponseValue.PaymentItem> mOrders = new ArrayList<>();

    public static OrdersController newInstance() {

        return new OrdersController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public OrdersController(Bundle args) {
        super(args);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_orders, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPlaceholderLayout.setVisibility(View.GONE);
        mPresenter.loadOrders();
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> showOrderDetails(position)));
        mPlaceholderLayout.setVisibility(View.GONE);
        mPresenter.loadOrders();

    }

    @Override
    protected void onAttach(@NonNull View view) {
        setupDefaultBottomButton(mActivity.getString(R.string.shop_now), (v) -> {
            mActivity.onBackPressed();
            mActivity.setDraggableViewPager(true);
            mActivity.getMainController().getHomeViewPager().setCurrentItem(1);
        });
        super.onAttach(view);
    }

    @Override
    public void showOrders(ArrayList<GetPaymentsList.ResponseValue.PaymentItem> orders) {
        if (orders.size() > 0) {
            mOrders = orders;
            mAdapter = new OrdersRecyclerViewAdapter(orders, mActivity);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
            hideBottomLayout();

        } else {
            mRecyclerView.setVisibility(View.GONE);
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            showBottomLayout();
        }
    }

    @Override
    public void showOrderDetails(int position) {
        String paymentRefNo = String.valueOf(mOrders.get(position).getPaymentReferenceNo());
        String jsonData = new Gson().toJson(mOrders.get(position));
        getRouter().pushController(RouterTransaction.with(new OrderDetailsController(jsonData, paymentRefNo, position))
                .pushChangeHandler(new VerticalChangeHandler(false))
                .popChangeHandler(new VerticalChangeHandler()));
    }
}
