package au.com.dealsdirect.ui.controller.returns.returnorders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnController;
import au.com.dealsdirect.ui.controller.returns.returnorders.adapter.ReturnOrdersAdapter;
import au.com.dealsdirect.ui.controller.returns.returnorders.listener.ReturnOrderClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class ReturnOrdersController extends BaseController
        implements ReturnOrdersMvpView, ReturnOrderClickListener {

    public static final String TAG = "ReturnOrdersController";

    private static final String KEY_TEXT = "ReturnOrdersController.KEY_TEXT";

    private ReturnOrderClickListener mReturnOrderClickListener;


    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mReturnOrdersControllerToolbarRightOption;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mReturnOrdersControllerToolbarTitle;

    @BindView(R.id.controller_returns_select_orders_recyclerview)
    RecyclerView mReturnOrdersRecyclerView;

    @BindView(R.id.no_returns_placeholder)
    LinearLayout mPlaceholderLayout;

    @BindView(R.id.controller_return_orders_list_container)
    RelativeLayout mReturnOrdersListContainer;

    @Inject
    ReturnOrdersMvpPresenter<ReturnOrdersMvpView> mPresenter;

    public static ReturnOrdersController newInstance() {

        return new ReturnOrdersController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ReturnOrdersController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_return_orders, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);

    }

    @Override
    protected void setUp(View view) {
        // Setup views here

        mReturnOrdersControllerToolbarTitle.setText("New Return");
        mReturnOrdersControllerToolbarRightOption.setVisibility(View.INVISIBLE);
        mReturnOrderClickListener = this;

        mPresenter.loadOrders();

    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick(){
        mActivity.onBackPressed();
    }

    @Override
    public void showOrders(List<au.com.dealsdirect.data.network.model.returns.returnorders.List> newReturnsOrders) {
        if(newReturnsOrders==null){
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mReturnOrdersListContainer.setVisibility(View.GONE);
//            newReturnsSubTitle.setVisibility(View.GONE);
//            newReturnsSelectOrderRecyclerView.setVisibility(View.GONE);
//            mPlaceholderLayout.setVisibility(View.VISIBLE);
//            ((TextView) mPlaceholderLayout.findViewById(R.id.welcome)).setText(R.string.my_return_orders);
            return;
        }else if (newReturnsOrders.isEmpty()){
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mReturnOrdersListContainer.setVisibility(View.GONE);
        } else {
            mPlaceholderLayout.setVisibility(View.GONE);
            mReturnOrdersListContainer.setVisibility(View.VISIBLE);
        }

        final ReturnOrdersAdapter adapter
                = new ReturnOrdersAdapter
                (newReturnsOrders,mActivity, mReturnOrderClickListener);

        mReturnOrdersRecyclerView
                .addItemDecoration(
                        new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL)
                );

        mReturnOrdersRecyclerView.setAdapter(adapter);
        mReturnOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        //  newReturnsSelectOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onReturnOrderItemClicked(au.com.dealsdirect.data.network.model.returns.returnorders.List newReturnsOrder) {
        getRouter().pushController(RouterTransaction.with(NewReturnController.newInstance(newReturnsOrder))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}
