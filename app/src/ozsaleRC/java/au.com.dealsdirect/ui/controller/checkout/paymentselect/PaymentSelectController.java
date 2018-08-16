package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.ui.custom.SimpleDividerItemDecoration;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSelectController extends BaseController implements PaymentSelectMvpView {

    @Inject
    PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;

    @BindView(R.id.payment_select_recyclerview)
    RecyclerView mRecyclerView;


    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;
    @BindView(R.id.partial_toolbar_title)
    TextView mPaymentSelectToolbarTitle;
    @BindView(R.id.no_payment_method_placeholder)
    RelativeLayout mNoPaymentPlaceholder;
    @BindView(R.id.controller_payment_description_text)
    TextView mPaymentSubtitleText;

    private CheckoutMvpView mCheckoutMvpView;

    private PaymentSelectAdapter mAdapter;

    private ArrayList<PaymentMethod> mPaymentMethods = new ArrayList<>();
    private boolean isFromCart = false;
    private boolean mIsOurpaySelectDeliveryMethod = false;
    private String mCartTotalCost;
    private Value mValue;


    public static PaymentSelectController newInstance() {
        return new PaymentSelectController(new BundleBuilder(new Bundle()).build());
    }

    public PaymentSelectController(Bundle args) {
        super(args);

        mPaymentMethods = JsonUtils.convertStringToObject(args.getString(BundleKeys.PAYMENT_METHODS), new TypeToken<ArrayList<PaymentMethod>>() {
        }.getType());

        if (mPaymentMethods == null) {
            mPaymentMethods = new ArrayList<>();
        }

        isFromCart = args.getBoolean(BundleKeys.IS_FROM_CART, false);
        mIsOurpaySelectDeliveryMethod = args.getBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, false);
        mCartTotalCost = args.getString(BundleKeys.CART_TOTAL_COST, "");
        mValue = new Gson().fromJson(args.getString(BundleKeys.CURRENT_ORDER_VALUE, ""), Value.class);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_payment_select, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mCheckoutMvpView = (CheckoutMvpView) getRouter().getControllerWithTag(getString(R.string.checkout_controller));
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.fetchUserPaymentMethods();
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    public void showPaymentList(List<PaymentMethod> paymentMethods) {
        int backstackSize = getRouter().getBackstackSize();
        String checkoutTag = getRouter().getBackstack().get(backstackSize - 1).tag();

        for (int i = 0; i < paymentMethods.size(); i++) {
            paymentMethods.get(i).setId(i);
        }

        boolean hasPaymentMethod = paymentMethods.size() > 0;
        mPaymentSubtitleText.setVisibility(hasPaymentMethod ? View.VISIBLE : View.GONE);

        if (paymentMethods != null && hasPaymentMethod) {
            mPaymentMethods = new ArrayList<>(paymentMethods);
            mAdapter.replaceData(mPaymentMethods);
            showPaymentMethodsPlaceholder(false);
        } else if (checkoutTag == getActivity().getString(R.string.checkout_controller) && (paymentMethods == null || !hasPaymentMethod)) {
            GateKeeper.push(getRouter(),
                    GateKeeper.Destination.PAYMENT_ADD,
                    new BundleBuilder(new Bundle())
                            .putBoolean(BundleKeys.IS_FROM_CART, isFromCart)
                            .putString(BundleKeys.CART_TOTAL_COST, mCartTotalCost)
                            .build()
                    , new HorizontalChangeHandler()
                    , new HorizontalChangeHandler());
        } else if (!hasPaymentMethod) {
            showPaymentMethodsPlaceholder(true);
        }
    }

    private void showPaymentMethodsPlaceholder(boolean isPlaceholderVisible) {
        mNoPaymentPlaceholder.setVisibility(isPlaceholderVisible ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(isPlaceholderVisible ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showRemovePaymentMethodResult(PaymentMethod paymentMethod, boolean result, String message) {
        mActivity.setPaymentMethodSelected(null);
        hideLoading();

        if (result) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, mActivity.getString(R.string.remove_payment_method));

            mPaymentMethods.remove(paymentMethod);

            boolean hasPaymentMethod = mAdapter.getItemCount() > 0;
            mPaymentSubtitleText.setVisibility(hasPaymentMethod ? View.VISIBLE : View.GONE);
            showPaymentMethodsPlaceholder(!hasPaymentMethod);

        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);
            removePaymentFailed();
        }
    }

    @Override
    public void removePaymentFailed() {
        mAdapter.replaceData(mPaymentMethods);
    }

    @Override
    protected void setUp(View view) {

        mToolbarLeftView.setVisibility(mPresenter.isTablet() && !isFromCart ? View.INVISIBLE : View.VISIBLE);
        mPaymentSelectToolbarTitle.setText(getString(R.string.my_payments));

        mAdapter = new PaymentSelectAdapter(mActivity, mPaymentMethods, mPresenter, isFromCart);
        RecyclerViewSwipeManager recyclerViewSwipeManager = new RecyclerViewSwipeManager();
        RecyclerView.Adapter wrappedAdapter = recyclerViewSwipeManager.createWrappedAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(wrappedAdapter);
        recyclerViewSwipeManager.attachRecyclerView(mRecyclerView);

        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mActivity, SimpleDividerItemDecoration.VERTICAL_LIST));


        if (!isFromCart) {
            showLoading();
            mPresenter.fetchUserPaymentMethods();
        } else {
            if(!mPaymentMethods.isEmpty()) {
                showPaymentList(mPaymentMethods);
                mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> {
                    mActivity.setPaymentMethodSelected(mPaymentMethods.get(position));
                    mAdapter.notifyDataSetChanged();
                    mActivity.onBackPressed();
                }));
            }
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        hideKeyboard();
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    @OnClick(R.id.controller_payment_button)
    public void onAddPaymentMethod() {
        BundleBuilder bundleBuilder = new BundleBuilder(new Bundle());
        bundleBuilder.putBoolean(BundleKeys.IS_FROM_CART, isFromCart)
                .putBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, mIsOurpaySelectDeliveryMethod);

        if (isFromCart) {
            bundleBuilder.putString(BundleKeys.CART_TOTAL_COST, Double.toString(mValue.getSummary().getTotal()));
            bundleBuilder.putString(BundleKeys.CURRENT_ORDER_VALUE, new Gson().toJson(mValue, Value.class));
        }

        GateKeeper.push(getRouter(),
                GateKeeper.Destination.PAYMENT_ADD,
                bundleBuilder.build()
                , new HorizontalChangeHandler()
                , new HorizontalChangeHandler());
    }
}
