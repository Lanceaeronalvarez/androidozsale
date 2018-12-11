package au.com.dealsdirect.ui.controller.checkout.checkouthost;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutOrderAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by smartwave on 13/06/2018.
 */

public class CheckoutHostController extends BaseController implements CheckoutHostMvpView {

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    @BindView(R.id.checkout_detail_container)
    ViewGroup mCheckoutDetailContainer;
    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;
    @BindView(R.id.controller_checkout_container)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.partial_toolbar_left_view)
    TextView mToolbarLeftButton;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;


    @BindView(R.id.controller_checkout_orders_label)
    TextView mOrdersLabel;

    @BindView(R.id.controller_checkout_recyclerview_items)
    RecyclerView mRecyclerView;

    private Router mCheckoutDetailRouter;
    private CheckoutController mCheckoutController;
    private CheckoutMvpView mCheckoutDetailView;
    private CheckoutOrderAdapter mAdapter;
    private List<Item> mItemList = new ArrayList<>();
    private boolean mIsCheckoutHostUpdated;
    private boolean mHasSavedInstance = false;


    public static CheckoutHostController newInstance() {
        return new CheckoutHostController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CheckoutHostController(Bundle args) {
        super(args);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_checkout_host, container, false);
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
    protected void setUp(View view) {
        mActivity.getMainController().setCheckoutHostController(this);

//disable toolbar left and right buttons
        mToolbarLeftButton.setVisibility(View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);
        mTitleTextView.setText(getString(R.string.account_orders));

        mCheckoutDetailRouter = getChildRouter(mCheckoutDetailContainer);

        if (!mHasSavedInstance || mActivity.getCheckoutController() == null) {
            mCheckoutController = CheckoutController.newInstance();
            mCheckoutDetailRouter.setRoot(RouterTransaction.with(mCheckoutController).tag(getString(R.string.checkout_controller)));
        } else {
            mCheckoutController = mActivity.getCheckoutController();
        }

        mCheckoutDetailView = mCheckoutController;

        mAdapter = new CheckoutOrderAdapter(mActivity, mItemList, mPresenter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));

    }

    @Override
    public boolean handleBack() {
        if(mCheckoutDetailRouter.getBackstackSize() == 1){
            mActivity.getHomeController().resetVisibleContainer();
            return true;
        }

        return super.handleBack();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @Override
    public void loadCart() {
        mCheckoutDetailView.loadCart();
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        mCheckoutController.onOrientationChanged(newConfiguration);
    }

    @Override
    public void showMyPayDetails(Value value, Ourpay ourpay) {
        mCheckoutDetailView.showMyPayDetails(value, ourpay);
    }

    @Override
    public void showCartDetails(List<Item> items) {

        showCartDetailsOnChild(items);

        showCartDetailsOnHost(items);
    }

    @Override
    public void showCartDetailsOnChild(List<Item> items) {
        if(mCheckoutDetailView != null){
            mCheckoutDetailView.showCartDetailsOnChild(items);
        }
    }

    @Override
    public void showCartDetailsOnHost(List<Item> items) {
        if(items == null) return;
        mItemList = items;

        mAdapter.replaceData(items);
        mNoCartItemsLayout.setVisibility(View.GONE);
        mCheckoutContainer.setVisibility(View.VISIBLE);
        int showOrdersLabel = getBoolean(R.bool.is_checkout_orders_label_visible) ? View.VISIBLE : View.GONE;

        mOrdersLabel.setVisibility(showOrdersLabel);
    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {
        mCheckoutDetailView.showAddressDetails(deliveryAddress, decorationInfoList);
    }

    @Override
    public void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail) {
        mCheckoutDetailView.showDeliveryOptions(deliveryOptions, deliveryServicePackageDetail);
    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {
        mCheckoutDetailView.showPaymentDetails(paymentMethod);
    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {
        mCheckoutDetailView.showVoucherDetails(vouchers);
    }

    @Override
    public void showSummaryDetails(Summary summary) {
        mCheckoutDetailView.showSummaryDetails(summary);
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        mCheckoutDetailView.setPaymentList(paymentList);
    }

    @Override
    public void storeCartDetails(Value value) {
        mCheckoutDetailView.storeCartDetails(value);
    }

    @Override
    public void triggerLoginTicket() {
        mCheckoutDetailView.triggerLoginTicket();
    }

    @Override
    public void updateCheckoutBadge() {
        mCheckoutDetailView.updateCheckoutBadge();
    }

    @Override
    public boolean isCartLoading() {
        return mCheckoutDetailView.isCartLoading();
    }

    @Override
    public void setCartIsLoading(boolean val) {
        mCheckoutDetailView.setCartIsLoading(val);
    }

    @Override
    public CheckoutMvpPresenter getPresenter() {
        return mCheckoutDetailView.getPresenter();
    }

    @Override
    public boolean isOurPaySelectDeliveryMethod() {
        return mCheckoutDetailView.isOurPaySelectDeliveryMethod();
    }

    @Override
    public boolean setIsPaymentMethodChanged(boolean isPaymentMethodChanged) {
        return mCheckoutDetailView.setIsPaymentMethodChanged(isPaymentMethodChanged);
    }

    @Override
    public void showPromoCodeApplied(String promoCode, boolean isPromoCodeApplied) {
        mCheckoutDetailView.showPromoCodeApplied(promoCode, isPromoCodeApplied);
    }

    @Override
    public Router getDisplayRouter() {
        return mCheckoutDetailRouter;
    }

    private void showNoCartItemsLayout() {
        mNoCartItemsLayout.setVisibility(View.VISIBLE);
        mCheckoutContainer.setVisibility(View.GONE);
    }

    @Optional
    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {
        mActivity.setShopsAsVisibleContainer();
    }
}
