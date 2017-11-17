package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseToolBarController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.utils.BundleKeys.CART_TOTAL_COST;
import static au.com.dealsdirect.utils.BundleKeys.IS_FROM_CART;
import static au.com.dealsdirect.utils.BundleKeys.PAYMENT_METHODS;

/*
 * Created by smartwave on 30/06/2017.
 */

public class PaymentSelectController extends BaseToolBarController implements PaymentSelectMvpView {

    @Inject
    PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;

    @BindView(R.id.payment_select_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.btn_add)
    Button mAddButton;

    private PaymentSelectAdapter mAdapter;

    private ArrayList<PaymentMethod> mPaymentMethods = new ArrayList<>();
    private boolean isFromCart = false;
    private String mCartTotalCost;

    public PaymentSelectController(Bundle args) {
        super(args);

        mPaymentMethods = JsonUtils.convertStringToObject(args.getString(BundleKeys.PAYMENT_METHODS), new TypeToken<ArrayList<PaymentMethod>>() {}.getType());

        if (mPaymentMethods == null) {
            mPaymentMethods = new ArrayList<>();
        }
        isFromCart = args.getBoolean(BundleKeys.IS_FROM_CART);
        mCartTotalCost = args.getString(BundleKeys.CART_TOTAL_COST, "");
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

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
                            .putBoolean(BundleKeys.IS_FROM_CART,isFromCart)
                            .putString(BundleKeys.CART_TOTAL_COST, mCartTotalCost)
                            .build()
                    ,new HorizontalChangeHandler()
                    ,new HorizontalChangeHandler());
        } else if (paymentMethods.size() == 0) {
            showPaymentMethodsPlaceholder(true);
        }
    }

    private void showPaymentMethodsPlaceholder(boolean val) {
        if (val) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
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

        mToolbarTitle.setText("my payment methods");

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

    @OnClick(R.id.btn_add)
    public void onAddPaymentMethod() {
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.PAYMENT_ADD,
                new BundleBuilder(new Bundle())
                .putBoolean(BundleKeys.IS_FROM_CART,isFromCart)
                .putString(BundleKeys.CART_TOTAL_COST, mCartTotalCost)
                .build()
                ,new HorizontalChangeHandler()
                ,new HorizontalChangeHandler());
    }
}
