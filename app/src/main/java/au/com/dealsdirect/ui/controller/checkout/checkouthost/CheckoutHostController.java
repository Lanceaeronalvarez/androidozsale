package au.com.dealsdirect.ui.controller.checkout.checkouthost;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.MappedShipment;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutListener;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutOrderAdapter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Created by smartwave on 13/06/2018.
 */

public class CheckoutHostController extends BaseController implements CheckoutHostMvpView, CheckoutListener {

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
    private List<MappedShipment> mItemList = new ArrayList<>();

    private boolean hasDeliveryAddress = false;
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
        //disable toolbar left and right buttons
        mToolbarLeftButton.setVisibility(View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);
        mTitleTextView.setText(getString(R.string.account_orders));

        mCheckoutDetailRouter = getChildRouter(mCheckoutDetailContainer);
        CommonControllerChangeListener.addToRouter(mCheckoutDetailRouter);

        if (!mHasSavedInstance || mActivity.getCheckoutController() == null) {
            mCheckoutController = CheckoutController.newInstance();
            mCheckoutDetailRouter.setRoot(RouterTransaction.with(mCheckoutController).tag(CheckoutController.class.getName()));
        } else {
            mCheckoutController = mActivity.getCheckoutController();
        }

        mCheckoutDetailView = mCheckoutController;

        mAdapter = new CheckoutOrderAdapter(mActivity, mItemList, mPresenter, this);
        mAdapter.setShouldAddSpacerOnTop(mPresenter.isTablet());
        mAdapter.setEligibleProductsLinkListener(locationFilterHash -> mActivity.getMainController().openLocationFilterHash(locationFilterHash));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (mCheckoutDetailView != null) {
            loadCart();
        }
    }

    @Override
    public boolean handleBack() {
        if (mCheckoutDetailRouter.getBackstackSize() == 1) {
            mActivity.getMainController().showShopController();
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
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.loadCart();
        }
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        mCheckoutController.onOrientationChanged(newConfiguration);
    }

    @Override
    public void showMyPayDetails(CheckoutDetailsMapper value, Ourpay ourpay) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showMyPayDetails(value, ourpay);
        }
    }

    @Override
    public void showCartDetails(List<MappedShipment> items) {

        showCartDetailsOnChild(items);

        showCartDetailsOnHost(items);
    }

    @Override
    public void showCartDetailsOnChild(List<MappedShipment> items) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showCartDetailsOnChild(items);
        }
    }

    @Override
    public void showCartDetailsOnHost(List<MappedShipment> items) {
        if (items == null) return;
        mItemList = items;

        refreshItemList(hasDeliveryAddress);
        mNoCartItemsLayout.setVisibility(View.GONE);
        mCheckoutContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showAddressDetails(deliveryAddress, decorationInfoList);
            mCheckoutDetailView.showCartDetailsFooter(deliveryAddress != null);
            mCheckoutDetailView.showCartDetailsPostcode(deliveryAddress != null ? deliveryAddress.getPostcode() : null);
        }
    }

    @Override
    public void showCartDetailsFooter(boolean show) {
        hasDeliveryAddress = show;
        refreshItemList(hasDeliveryAddress);
    }

    @Override
    public void showCartDetailsPostcode(String postcode) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.setPostcodeOverride(postcode);
        mAdapter.notifyDataSetChanged();
    }

    private void refreshItemList(boolean showFooter) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.replaceData(mItemList, showFooter);
    }


    @Override
    public void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showDeliveryOptions(deliveryOptions, deliveryServicePackageDetail);
        }
    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showPaymentDetails(paymentMethod);
        }
    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showVoucherDetails(vouchers);
        }
    }

    @Override
    public void setIsShipmentAvailable(boolean isShipmentAvailable) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setIsShipmentAvailable(isShipmentAvailable);
        }
    }

    @Override
    public void showSummaryDetails(Summary summary) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showSummaryDetails(summary);
        }
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setPaymentList(paymentList);
        }
    }

    @Override
    public void showAfterpayPanel(boolean isAvailable, String description) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showAfterpayPanel(isAvailable, description);
        }
    }

    @Override
    public void hideAfterpayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideAfterpayPanel();
        }
    }

    @Override
    public void showLPayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showLPayPanel();
        }
    }

    @Override
    public void hideLPayPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideLPayPanel();
        }
    }

    @Override
    public void showKlarnaPanel(String description) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showKlarnaPanel(description);
        }
    }

    @Override
    public void hideKlarnaPanel() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.hideKlarnaPanel();
        }
    }

    @Override
    public void storeCartDetails(CheckoutDetailsMapper value) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.storeCartDetails(value);
        }
    }

    @Override
    public void triggerLoginTicket() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.triggerLoginTicket();
        }
    }

    @Override
    public void updateCheckoutBadge() {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.updateCheckoutBadge();
        }
    }

    @Override
    public boolean isCartLoading() {
        if (mCheckoutDetailView != null) {
            return mCheckoutDetailView.isCartLoading();
        } else {
            return false;
        }
    }

    @Override
    public void setCartIsLoading(boolean val) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setCartIsLoading(val);
        }
    }

    @Override
    public CheckoutMvpPresenter getPresenter() {
        if (mCheckoutDetailView != null) {
            return mCheckoutDetailView.getPresenter();
        } else {
            return null;
        }
    }

    @Override
    public boolean isOurPaySelectDeliveryMethod() {
        if (mCheckoutDetailView != null) {
            return mCheckoutDetailView.isOurPaySelectDeliveryMethod();
        } else {
            return false;
        }
    }

    @Override
    public void setIsPaymentMethodChanged(boolean isPaymentMethodChanged) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.setIsPaymentMethodChanged(isPaymentMethodChanged);
        }
    }

    @Override
    public Router getDisplayRouter() {
        return mCheckoutDetailRouter;
    }

//    @Override
//    public void initializeVisaCheckout() {
//
//    }

    private void showNoCartItemsLayout() {
        mNoCartItemsLayout.setVisibility(View.VISIBLE);
        mCheckoutContainer.setVisibility(View.GONE);
    }

    @Optional
    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {
        mActivity.getMainController().showShopController();
    }

    @Override
    public void showItemDetail(View sourceView, int position, String seoIdentifierId, String imageUrl,
                               String skuId, String saleId, boolean isFreeDelivery,
                               String itemName, String brandName, String price, String oldPrice,
                               String productID) {

        if (CommonUtils.loadSaleItem(mActivity, productID).isEmpty()) {
            return;
        }

        SaleItemDetailsController.Parameters.FromCheckout parameters = new SaleItemDetailsController.Parameters.FromCheckout(position,
                null,
                imageUrl,
                CommonUtils.loadSaleItem(mActivity, productID),
                skuId,
                CommonUtils.loadSaleId(mActivity, productID),
                itemName,
                brandName,
                price,
                oldPrice,
                "", "", isFreeDelivery, false);

        RouterTransaction routerTransaction = RouterTransaction
                .with(SaleItemDetailsController.newInstance(parameters));

        int[] originalPos = new int[2];
        sourceView.getLocationOnScreen(originalPos);
        int left = originalPos[0];
        int top = originalPos[1];
        int width = sourceView.getWidth();
        int height = sourceView.getHeight();
        routerTransaction = routerTransaction
                .pushChangeHandler(new ArcZoomChangeHandler(left, top, width, height))
                .popChangeHandler(new ArcZoomChangeHandler(left, top, width, height));

        getRouter().pushController(routerTransaction);

    }

    public Router getCheckoutDetailRouter() {
        return mCheckoutDetailRouter;
    }

    @Override
    public void showAgeRestriction(boolean hasAgeRestriction) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.showAgeRestriction(hasAgeRestriction);
        }
    }

    @Override
    public void updateCartWithValue(Value value) {
        if (mCheckoutDetailView != null) {
            mCheckoutDetailView.updateCartWithValue(value);
        }
    }
}
