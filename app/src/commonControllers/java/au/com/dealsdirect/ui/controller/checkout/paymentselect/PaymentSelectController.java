package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSelectController extends BasePullToRefreshController implements PaymentSelectMvpView {

    private final static String PAYMENT_METHODS = "payment_methods";
    private final static String IS_FROM_CART = "is_from_cart";
    private final static String CART_TOTAL_COST = "cart_total_cost";

    @Inject
    PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;

    @BindView(R.id.payment_select_recyclerview)
    RecyclerView mRecyclerView;


    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mPaymentSelectToolbarTitle;
    @BindView(R.id.partial_toolbar_right_view)
    ImageView mPaymentSelectRightOption;
    @BindView(R.id.no_payment_method_placeholder)
    LinearLayout mNoPaymentPlaceholder;

    private PaymentSelectAdapter mAdapter;

    private ArrayList<PaymentMethod> mPaymentMethods = new ArrayList<>();
    private boolean isFromCart = false;
    private String mCartTotalCost;
    private int mItemListSize;


    public PaymentSelectController(String paymentMethodsJsonString, boolean isFromCart, String cartCost) {
        this(new BundleBuilder(new Bundle())
                .putString(PAYMENT_METHODS, paymentMethodsJsonString)
                .putBoolean(IS_FROM_CART, isFromCart)
                .putString(CART_TOTAL_COST, cartCost)
                .build());
    }

    public PaymentSelectController(Bundle args) {
        super(args);

        mPaymentMethods = JsonUtils.convertStringToObject(args.getString(PAYMENT_METHODS), new TypeToken<ArrayList<PaymentMethod>>() {}.getType());

        if (mPaymentMethods == null) {
            mPaymentMethods = new ArrayList<>();
        }
        isFromCart = args.getBoolean(IS_FROM_CART);
        mCartTotalCost = args.getString(CART_TOTAL_COST, "");
        mItemListSize = args.getInt(BundleKeys.ITEM_LIST_SIZE,-1);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillToolbar(inflater.inflate(R.layout.partial_toolbar_arrow, container, false));
        fillContent(inflater.inflate(R.layout.controller_payment_select, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
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
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.fetchUserPaymentMethods();
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    public void showPaymentList(List<PaymentMethod> paymentMethods) {
        int backstackSize = getRouter().getBackstackSize();
        String checkoutTag = getRouter().getBackstack().get(backstackSize - 1).tag();

        if (paymentMethods != null && paymentMethods.size() > 0) {
            mPaymentMethods = new ArrayList<>(paymentMethods);
            mAdapter.replaceData(mPaymentMethods);
            showPaymentMethodsPlaceholder(false);
        } else if (checkoutTag == getActivity().getString(R.string.checkout_controller)
                && (paymentMethods == null
                || paymentMethods.size() == 0)) {
            GateKeeper.push(getRouter(),
                    GateKeeper.Destination.PAYMENT_ADD,
                    new BundleBuilder(new Bundle())
                            .putBoolean(BundleKeys.IS_FROM_CART, isFromCart)
                            .putString(BundleKeys.CART_TOTAL_COST, mCartTotalCost)
                            .build()
                    , new HorizontalChangeHandler()
                    , new HorizontalChangeHandler());
        } else if (paymentMethods.size() == 0) {
            showPaymentMethodsPlaceholder(true);
        }
    }

    private void showPaymentMethodsPlaceholder(boolean val) {
        if (val) {
            mNoPaymentPlaceholder.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNoPaymentPlaceholder.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showRemovePaymentMethodResult(PaymentMethod paymentMethod, boolean result, String message) {
        mActivity.setPaymentMethodSelected(null);
        hideLoading();

        if (result) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, mActivity.getString(R.string.remove_payment_method));

            mPaymentMethods.remove(paymentMethod);
            mAdapter.notifyDataSetChanged();

            if (mAdapter.getItemCount() == 0) {
                showPaymentMethodsPlaceholder(true);
            }

        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);

        }
    }

    @Override
    protected void setUp(View view) {

        if (!isFromCart) {
            mPresenter.fetchUserPaymentMethods();
        }

        mPaymentSelectToolbarTitle.setText("Add Payment Method");
        if (mPresenter.isTablet()) {
            mPaymentSelectRightOption.setPadding(5, 5, 5, 5);
        } else {
            mPaymentSelectRightOption.setPadding(20, 20, 20, 20);
        }
        mPaymentSelectRightOption.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_add));

        mAdapter = new PaymentSelectAdapter(mActivity, mPaymentMethods, mPresenter, isFromCart);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);

        if (isFromCart) {
            mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> {
                mActivity.setPaymentMethodSelected(mPaymentMethods.get(position));
                mAdapter.notifyDataSetChanged();
                mActivity.onBackPressed();
            }));
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        hideKeyboard();
        if (getActivity() != null) {
            getActivity().onBackPressed();

        }
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void onAddPaymentMethod() {
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.PAYMENT_ADD,
                new BundleBuilder(new Bundle())
                        .putBoolean(BundleKeys.IS_FROM_CART, isFromCart)
                        .putString(BundleKeys.CART_TOTAL_COST, mCartTotalCost)
                        .putInt(BundleKeys.ITEM_LIST_SIZE, mItemListSize)
                        .build()
                , new HorizontalChangeHandler()
                , new HorizontalChangeHandler());
    }
}
