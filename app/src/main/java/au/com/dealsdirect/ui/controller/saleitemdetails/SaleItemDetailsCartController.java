package au.com.dealsdirect.ui.controller.saleitemdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.mysale.genie.utility.RxBus;

import org.w3c.dom.Text;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.service.braintree.FetchBraintreeClientTokenHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutListener;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutOrderAdapter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutTitleFromShippingFeeHelper;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;

public class SaleItemDetailsCartController extends BaseController implements CheckoutMvpView, CheckoutListener {

    @BindView(R.id.shopping_cart_button_close)
    ImageButton mCloseButton;
    @BindView(R.id.controller_checkout_recyclerview_items)
    RecyclerView mRecyclerView;
    @BindView(R.id.shopping_cart_button_view_cart)
    Button mContinueToCheckoutButton;
    @BindView(R.id.shopping_cart_subtotal)
    TextView mCartSubTotalText;
    @BindView(R.id.shopping_cart_shipping_total)
    TextView mCartShippingTotalText;
    @BindView(R.id.shopping_cart_postcode)
    TextView mCartPostcode;
    @BindView(R.id.shopping_cart_total)
    TextView mCartTotalText;
    @BindView(R.id.shopping_cart_card)
    TextView mCardNumberText;
    @BindView(R.id.shopping_cart_button_pay)
    RelativeLayout mPayButton;
    @BindView(R.id.shopping_cart_button_paypal)
    RelativeLayout mPaypalButton;
    @BindView(R.id.shopping_cart_button_afterpay)
    LinearLayout mAfterpayButton;

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    private CartDetailsMapper mCartDetails = null;

    private CheckoutOrderAdapter mAdapter;

    private boolean mIsCartLoading = false;
    private PaymentMethod selectedPaymentMethod = null;
    private Double mDiscountValue;
    private Double mTotalValue;

    private DeliveryAddress mDeliveryAddress = null;


    public SaleItemDetailsCartController(Bundle args) {
        super(args);
    }

    public static SaleItemDetailsCartController newInstance() {
        return new SaleItemDetailsCartController(new BundleBuilder(new Bundle()).build());
    }

    public static SaleItemDetailsCartController newInstance(CartDetailsMapper cartDetailsResponse) {
        SaleItemDetailsCartController controller = SaleItemDetailsCartController.newInstance();

        controller.mCartDetails = cartDetailsResponse;

        return controller;
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
        if (!mIsCartLoading && mPresenter.checkIsLoggedIn()) {
            loadCart(); //preload if logged in
        }

        mCloseButton.setOnClickListener(v -> {
            getRouter().popCurrentController();
        });
    }

    @Override
    protected void setUp(View view) {
        if (!mPresenter.isTablet() || !getBoolean(R.bool.master_detail_enabled)) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new CheckoutOrderAdapter(
                    mActivity,
                    false,
                    mPresenter.isShippingByPostcodeEnabled(),
                    getTitleFromShippingFeeHelper(),
                    () -> {
                        if (mPresenter.getTemplateTextsRepository() == null) {
                            return null;
                        }
                        return mPresenter.getTemplateTextsRepository().getUnavailable();
                    },
                    this, priceInfo -> showBottomPopupView(priceInfo));
            mAdapter.setItemQuantityChangedListener(new CheckoutOrderAdapter.ItemQuantityChangedListener() {
                @Override
                public void onIncrease(String itemId, int newCount, ProductQuantityLayout view) {
                    mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", itemId, null, view);
                }

                @Override
                public void onDecrease(String itemId, int newCount, ProductQuantityLayout view) {
                    mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", itemId, null, view);

                    if (newCount == 0) {
//                        logRemoveItemFromCart(view.getContext());
                    }
                }
            });

        }

        mContinueToCheckoutButton.setOnClickListener(v -> {
            goToCheckoutScreen();
        });
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.shopping_cart_dialog, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    private void goToCheckoutScreen() {
        mActivity.getMainController().getCheckoutRouter().popToRoot();
        mActivity.getMainController().showCheckoutController();
    }

    @Override
    public void showItemDetail(View sourceView, int position, String seoIdentifierId, String imageUrl, String skuId, String saleId, boolean isFreeDelivery, String itemName, String brandName, String price, String oldPrice, String productID) {

    }

    @Override
    public PaymentMethod getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    @Override
    public void setSelectedPaymentMethod(PaymentMethod selectedPaymentMethod) {
        this.selectedPaymentMethod = selectedPaymentMethod;
    }

    @Override
    public void loadCart() {
        if (mPresenter == null || mActivity == null) return;

        if (mPresenter.checkIsLoggedIn()) {
            RxBus.instance().post(IntrospectionUtils.EVENT_CHECKOUT_SCREEN);

            if (!mActivity.isBraintreeInitialized()) {
                mActivity.fetchBraintreeAuthorization(new FetchBraintreeClientTokenHandler() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure() {
                        if (!isAttached()) return;
//                        hidePaymentButtons();
                    }
                });
            }

            if (!isCartLoading()) {
                setCartIsLoading(true);
                mPresenter.callCartContent(null);
            }

        }
    }

    @Override
    public void showCartDetails(CartDetailsMapper cart) {
        showCartDetailsOnHost(cart);

        showCartDetailsOnChild(cart);
    }

    @Override
    public void showCartDetailsOnChild(CartDetailsMapper cart) {
        mCartDetails = cart;

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

        refreshItemList(mDeliveryAddress != null);
    }

    @Override
    public void showCartDetailsOnHost(CartDetailsMapper cart) {

    }

    @Override
    public void showCartDetailsFooter(boolean show) {

    }

    @Override
    public void showCartDetailsPostcode(String postcode) {

    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {
        mDeliveryAddress = deliveryAddress;
    }

    @Override
    public void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail) {

    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {
        mCardNumberText.setText(paymentMethod.getDescription());
    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {

    }

    @Override
    public void showSummaryDetails(Summary summary) {
        if (summary != null) {
            mTotalValue = summary.getTotal();

            mCartSubTotalText.setText(PriceUtils.getPriceStringValue(summary.getSubtotal()));
            mCartTotalText.setText(PriceUtils.getPriceStringValue(summary.getTotal()));

        }
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {

    }

    @Override
    public void showAfterpayPanel(boolean isAvailable, String description) {

    }

    @Override
    public void hideAfterpayPanel() {

    }

    @Override
    public void showLPayPanel() {

    }

    @Override
    public void hideLPayPanel() {

    }

    @Override
    public void showKlarnaPanel(String description) {

    }

    @Override
    public void hideKlarnaPanel() {

    }

    @Override
    public void showZipPayPanel() {

    }

    @Override
    public void hideZipPayPanel() {

    }

    @Override
    public void storeCartDetails(CartDetailsMapper mappedValues) {

    }

    @Override
    public void triggerLoginTicket() {

    }

    @Override
    public void updateCheckoutBadge() {

    }

    @Override
    public boolean isCartLoading() {
        return mIsCartLoading;
    }

    @Override
    public void setCartIsLoading(boolean val) {
        this.mIsCartLoading = val;
    }

    @Override
    public Router getDisplayRouter() {
        return null;
    }

    @Override
    public void setIsShipmentAvailable(boolean isShipmentAvailable) {

    }

    @Override
    public void showAgeRestriction(boolean hasAgeRestriction) {

    }

    @Override
    public void updateCartWithMappedValues(CartDetailsMapper mappedValues) {

    }

    @Override
    public void showBestSellers(List<GetBestSellerResponse> getBestSellerResponses) {

    }

    @Override
    public void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText) {

    }

    @Override
    public void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response) {

    }

    private void refreshItemList(boolean showFooter) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.replaceData(mCartDetails, true, false, showFooter);
    }

    private CheckoutTitleFromShippingFeeHelper getTitleFromShippingFeeHelper() {
        String impossibleToDeliverAtLocationText;
        if (mPresenter.getTemplateTextsRepository().getImpossibleToDeliverAtLocation() == null) {
            impossibleToDeliverAtLocationText = "Unavailable";
        } else {
            impossibleToDeliverAtLocationText = mPresenter.getTemplateTextsRepository().getImpossibleToDeliverAtLocation();
        }

        return new CheckoutTitleFromShippingFeeHelper(
                mActivity,
                null,
                mPresenter.isShippingByPostcodeEnabled(),
                impossibleToDeliverAtLocationText,
                locationFilterHash -> mActivity.getMainController().openLocationFilterHash(locationFilterHash));
    }

    private void showBottomPopupView(String textContent) {

    }
}
