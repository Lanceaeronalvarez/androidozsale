package au.com.dealsdirect.ui.controller.checkout.paymentselect;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.reflect.TypeToken;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.ui.custom.SimpleDividerItemDecoration;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.JsonUtils;
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
    private CheckoutDetailsMapper mValue;


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
        mValue = CheckoutDetailsMapper.decompress(args.getByteArray(BundleKeys.CURRENT_ORDER_VALUE));
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
    public void refreshContents() {
        super.refreshContents();
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
            goToPaymentController();
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
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.an_error_has_occurred));
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
            if (!mPaymentMethods.isEmpty()) {
                showPaymentList(mPaymentMethods);
                mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> {
                    if (mActivity.getPaymentMethodSelected() != mPaymentMethods.get(position)) {
                        mCheckoutMvpView.setIsPaymentMethodChanged(true);
                    }
                    mActivity.setPaymentMethodSelected(mPaymentMethods.get(position));
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
        goToPaymentController();
    }

    private void goToPaymentController() {
        AddPaymentController controller;
        if (isFromCart) {
            AddPaymentController.Parameters.FromCheckout parameters = new AddPaymentController
                    .Parameters.FromCheckout(mIsOurpaySelectDeliveryMethod,
                    Double.toString(mValue.getSummary().getTotal()),
                    mValue);
            controller = AddPaymentController.newInstance(parameters);
        } else {
            controller = AddPaymentController.newInstance();
        }

        getRouter().pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}
