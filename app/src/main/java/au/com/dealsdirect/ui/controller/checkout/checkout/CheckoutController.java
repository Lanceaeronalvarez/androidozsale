package au.com.dealsdirect.ui.controller.checkout.checkout;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static au.com.dealsdirect.data.network.model.events.WishlistEventRequest.WishListInfo.ReferrerValue.PRODUCT_PAGE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.mysale.genie.utility.RxBus;
import com.stripe.android.model.CardBrand;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.cart.CartDetailsMapper;
import au.com.dealsdirect.data.cart.CartDetailsMapper.MappedShipment;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.events.CommonCheckoutRequest;
import au.com.dealsdirect.data.network.model.events.GA4EventParams;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetBestSellerResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.data.network.model.vouchers.Voucher;
import au.com.dealsdirect.service.braintree.FetchBraintreeClientTokenHandler;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.AgeRestrictionOperationType;
import au.com.dealsdirect.service.datacollection.enums.CheckoutUserActivityOperationType;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.deliveryoptions.DeliveryOptions;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.afterpay.AfterpayViewController;
import au.com.dealsdirect.ui.controller.bestsellers.BestSellersWidgetHelper;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostMvpView;
import au.com.dealsdirect.ui.controller.checkout.deliveryoptions.DeliveryOptionsController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.floatingimageviewer.FloatingImageViewerController;
import au.com.dealsdirect.ui.controller.klarna.KlarnaViewController;
import au.com.dealsdirect.ui.controller.lpay.LPayViewController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.saleitemdetails.HorizontalScrollingItemsAdapter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.controller.zippay.ZipPayViewController;
import au.com.dealsdirect.ui.custom.BottomPopupView;
import au.com.dealsdirect.ui.custom.BottomPopupWebViewContentAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.ProductQuantityLayout;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.ScreenUtils;
import au.com.dealsdirect.utils.ScrollingImageHorizontal.HorizontalRecyclerItemsViewHolder;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

public class CheckoutController extends BaseController implements CheckoutMvpView, CheckoutListener {
    public static final String CARD_PAYPAL = "Paypal";
    public static final String CARD_MASTERPASS = "Masterpass";
    public static final String CARD_VISA_CHECKOUT = "VisaCheckoutBraintree";
    public static final String CARD_MASTERCARD = "MasterCard";
    public static final String CARD_VISA = "Visa";

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    @BindView(R.id.controller_checkout_recyclerview_items)
    RecyclerView mRecyclerView;

    @BindView(R.id.partial_checkout_address_container_layout)
    ViewGroup mAddressContainerLayout;
    @BindView(R.id.partial_checkout_payment_container_layout)
    ViewGroup mPaymentContainerLayout;
    @BindView(R.id.partial_checkout_voucher_container_layout)
    ViewGroup mVoucherContainerLayout;

    @BindView(R.id.partial_checkout_address_new_address)
    ViewGroup mAddNewAddressLayout;
    @BindView(R.id.partial_checkout_payment_new_payment)
    ViewGroup mAddNewPaymentLayout;
    @BindView(R.id.partial_checkout_voucher_new_code)
    ViewGroup mAddNewVoucherLayout;

    @BindView(R.id.partial_checkout_summary_subtotal)
    TextView mSummarySubtotalTextView;
    @BindView(R.id.partial_checkout_summary_voucher)
    TextView mSummaryVoucherTextView;
    @BindView(R.id.partial_checkout_summary_shipping_label)
    TextView mSummaryShippingLabelTextView;
    @BindView(R.id.partial_checkout_summary_shipping_fee)
    TextView mSummaryShippingFeeTextView;
    @BindView(R.id.partial_checkout_summary_shipping_fee_container)
    ViewGroup mSummaryShippingFeeContainer;
    @BindView(R.id.partial_checkout_summary_tax)
    TextView mSummaryTaxTextView;
    @BindView(R.id.partial_checkout_summary_tax_container)
    ViewGroup mSummaryTaxContainer;

    @BindView(R.id.partial_checkout_summary_voucher_container)
    ViewGroup mVoucherValueContainer;
    @BindView(R.id.partial_checkout_voucher_value_text_view)
    TextView mVoucherValueTextView;

    @BindView(R.id.partial_checkout_address_container)
    ViewGroup mAddressLayout;
    @BindView(R.id.partial_checkout_payment_container)
    ViewGroup mPaymentLayout;
    @BindView(R.id.partial_checkout_summary_container)
    ViewGroup mSummaryLayout;

    @BindView(R.id.partial_checkout_summary_total)
    TextView mSummaryTotalTextView;

    @BindView(R.id.partial_checkout_address_change)
    View mAddressChangeView;
    @BindView(R.id.partial_checkout_payment_change)
    View mPaymentChangeView;

    @BindView(R.id.partial_checkout_age_restriction_container)
    ViewGroup mAgeRestrictionContainer;
    @BindView(R.id.partial_checkout_age_restriction_date_input)
    EditText mAgeRestrictionDateInput;
    @BindView(R.id.partial_checkout_age_restriction_description)
    TextView mAgeRestrictionDescription;
    @BindView(R.id.partial_checkout_age_restriction_notice)
    TextView mAgeRestrictionNotice;

    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mPayButton;
    @BindView(R.id.controller_checkout_button_pay)
    Button mPayButtonCheckout;
    @BindView(R.id.partial_checkout_button_g_pay_container)
    View mGPayButtonContainer;
    @BindView(R.id.partial_checkout_button_g_pay)
    View mGPayButton;
    @BindView(R.id.partial_checkout_button_paypal)
    RelativeLayout mPaypalButton;
    @BindView(R.id.partial_checkout_button_paypal_credit)
    RelativeLayout mPaypalCreditButton;
    @BindView(R.id.partial_checkout_button_masterpass)
    RelativeLayout mMasterpassButton;
    @BindView(R.id.partial_checkout_afterpay_panel_holder)
    LinearLayout mAfterpayHolder;
    @BindView(R.id.partial_checkout_afterpay_description)
    TextView mAfterpayDescription;
    @BindView(R.id.partial_checkout_afterpay_inlinelogo)
    ImageView mAfterpayInlineLogo;
    @BindView(R.id.partial_checkout_afterpay_info_button)
    ImageButton mAfterpayInfoButton;
    @BindView(R.id.partial_checkout_button_afterpay)
    RelativeLayout mAfterpayButton;
    @BindView(R.id.partial_checkout_lpay_panel_holder)
    ViewGroup mLPayHolder;
    @BindView(R.id.partial_checkout_button_lpay)
    View mLPayButton;
    @BindView(R.id.partial_checkout_button_lpay_logo)
    ImageView mLPayButtonLogoImageView;
    @BindView(R.id.partial_checkout_klarna_container)
    ViewGroup mKlarnaContainer;
    @BindView(R.id.partial_checkout_klarna_description)
    WebView mKlarnaDescriptionView;
    @BindView(R.id.partial_checkout_button_klarna)
    View mKlarnaButton;

    @BindView(R.id.partial_checkout_zippay_panel_holder)
    ViewGroup mZipPayHolder;
    @BindView(R.id.partial_checkout_button_zippay)
    View mZipPayButton;
    @Nullable
    @BindView(R.id.controller_checkout_orders_label)
    TextView mOrdersLabel;

    @Nullable
    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;
    @BindView(R.id.controller_checkout_container)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftButton;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;
    @BindView(R.id.checkout_scrollview)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.checkout_scrollview_container)
    @Nullable
    ViewGroup mScrollViewContainer;

    //DELIVERY OPTIONS UI
    @Nullable
    @BindView(R.id.delivery_option_root_layout)
    ViewGroup mDeliveryOptionRootLayout;
    @Nullable
    @BindView(R.id.delivery_option_text_view)
    TextView mDeliveryOptionTypeText;
    @Nullable
    @BindView(R.id.delivery_option_price_text_view)
    TextView mDeliveryOptionPriceTextView;
    @BindView(R.id.partial_checkout_summary_shipping_with_icon)
    RelativeLayout mFreeShippingLayout;

    @BindView(R.id.button_visa_checkout)
    Button mVcoButton;

    @Nullable
    @BindView(R.id.partial_checkout_empty_button)
    View mGoToShopButton;
    @Nullable
    @BindView(R.id.partial_checkout_empty_widget_area)
    ViewGroup mWidgetArea;

    private List<DeliveryOption> mDeliveryOptions;
    private DeliveryOption mSelectedDeliveryOption;
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;

    private List<MappedShipment> mItemList = new ArrayList<>();
    private List<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private List<DecorationInfoList> mDecorationInfoList = new ArrayList<>();
    private List<Voucher> mVouchers = new ArrayList<>();
    private CheckoutOrderAdapter mAdapter;

    private boolean mIsCartLoading = false;
    private boolean mIsPaymentMethodChanged = false;
    private PaymentMethod selectedPaymentMethod = null;
    private Double mDiscountValue;
    private Double mTotalValue;

    private CartDetailsMapper mValue;

    private CheckoutHostMvpView mCheckoutHostView = null;

    private CompositeDisposable mClickListeners;
    private CompositeDisposable mChangeClickListeners;

    private PaymentMethod mLastUserPaymentMethod;
    private boolean mHasSavedInstance = false;

    private boolean isShipmentAvailable = true;

    private boolean hasAgeRestriction = false;
    private Calendar birthday = null;

    private boolean isGPayAvailable = false;

    private BottomPopupView currentBottomPopupView = null;

    private int lastBestSellerItemPosition = -1;
    private HorizontalRecyclerItemsViewHolder widgetAreaHorizontalRecyclerItemsViewHolder = null;
    private BestSellersWidgetHelper bestSellersWidgetHelper = null;
    private RecentlyViewedWidgetHelper recentlyViewedWidgetHelper = null;

    public static CheckoutController newInstance() {
        return new CheckoutController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public static CheckoutController newInstance(CheckoutHostController checkoutHostController) {
        CheckoutController checkoutController = new CheckoutController(
                new BundleBuilder(new Bundle())
                        .build());
        checkoutController.mCheckoutHostView = checkoutHostController;
        return checkoutController;
    }

    public CheckoutController(Bundle args) {
        super(args);
    }

    private void changeAddress() {
        if (mDeliveryAddress == null) {
            showAddAddressController();
            mPresenter.setLastCartRedirection(DataCollector.EventParameters.LastRedirection.ADD_ADDRESS);
        } else {
            ViewAddressController.Parameters.DisplayViewAddress parameters = new ViewAddressController.Parameters
                    .DisplayViewAddress(true, mDeliveryAddress, false, "");

            ViewAddressController controller = ViewAddressController.newInstance(parameters);
            getRouter().pushController(RouterTransaction.with(controller)
                    .pushChangeHandler(new HorizontalChangeHandler(false))
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    private void changePayment() {
        if (!mPaymentList.isEmpty() && mPaymentList.size() >= 2) {
            //push to payment select
            showPaymentSelectController();
        } else {
            //push controller to add payment
            if (!isAddressValid()) {
                //push add new address fragment
                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                showAddAddressController();
            } else {
                showAddPaymentMethodController();
                mPresenter.setLastCartRedirection(DataCollector.EventParameters.LastRedirection.ADD_PAYMENT_METHOD);
            }
        }
    }


    private void changeVoucher() {
        AddVouchersController addVouchersController = new AddVouchersController(new Bundle());
        addVouchersController.setVouchers(mVouchers);
        addVouchersController.setAppliedPromoCodes(mValue.getPromoCodeList());
        addVouchersController.setCartDetailsListener(mappedValues -> mPresenter.updateCartValues(mappedValues));

        getRouter().pushController(RouterTransaction.with(addVouchersController)
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
        if (!mIsCartLoading && mPresenter.checkIsLoggedIn()) {
            loadCart(); //preload if logged in
        }

//        registerClickListeners();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_checkout, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
//        mVcoPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mToolbarLeftButton.setVisibility(getRouter().getBackstackSize() > 1 ? View.VISIBLE : View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);

        if (mActivity != null) {
            mActivity.performResetWithAuthFetch();
        }

        mToolbarLeftButton.setOnClickListener(v -> {
            mActivity.onBackPressed();
        });

        setUp(view);
    }


    @Override
    public void onDetach(View view) {
        hideLoading();

        if (widgetAreaHorizontalRecyclerItemsViewHolder != null) {
            widgetAreaHorizontalRecyclerItemsViewHolder.onViewRemoved();
        }

        super.onDetach(view);

//        unregisterClickListeners();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
    }

    @Override
    protected void setUp(View view) {
        if (mActivity != null) {
            mActivity.getMainController().showBottomNav();
        }

        mTitleTextView.setText(R.string.checkout_page_toolbar_title);

        if (!mPresenter.isTablet() || !getBoolean(R.bool.master_detail_enabled)) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new CheckoutOrderAdapter(
                    mActivity,
                    mItemList,
                    false,
                    mPresenter.isShippingByPostcodeEnabled(),
                    () -> {
                        if (mPresenter.getTemplateTextsRepository() == null) {
                            return null;
                        }
                        return mPresenter.getTemplateTextsRepository().getImpossibleToDeliverAtLocation();
                    },
                    this,
                    priceInfo -> showBottomPopupView(priceInfo));
            mAdapter.setEligibleProductsLinkListener(locationFilterHash -> mActivity.getMainController().openLocationFilterHash(locationFilterHash));
            mAdapter.setItemQuantityChangedListener(new CheckoutOrderAdapter.ItemQuantityChangedListener() {
                @Override
                public void onIncrease(String itemId, int newCount, ProductQuantityLayout view) {
                    mPresenter.fetchAdjustItemQuantity("IncreaseOrderItem", itemId, null, view);
                }

                @Override
                public void onDecrease(String itemId, int newCount, ProductQuantityLayout view) {
                    mPresenter.fetchAdjustItemQuantity("DecreaseOrderItem", itemId, null, view);

                    if (newCount == 0) {
                        logRemoveItemFromCart(view.getContext());
                    }
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        }

//        if (!mActivity.isBraintreeInitialized() && mActivity.isAuthorized()) {
//            mVcoPresenter.initializeBraintree();
//        }
//
//        if (mVcoPresenter.isVisaCheckoutEnabled() && mActivity.isAuthorized()) {
//            if (!mActivity.isBraintreeInitialized()) {
//                mVcoPresenter.initializeBraintree();
//            }
//            mVcoPresenter.setupVisaCheckout(true);
//        }

//        mVcoButton.setOnClickListener(action -> {
//            onVisaCheckoutButtonClicked();
//        });

        final boolean isGenoaPay = Settings.getSelectedCountry().countryId.equalsIgnoreCase("NZ");
        final int padding = (int) mActivity.getResources().getDimension(isGenoaPay ? R.dimen.genoa_button_logo_margin : R.dimen.lpay_button_logo_margin);
        mLPayButton.setBackgroundResource(isGenoaPay ? R.drawable.bg_genoapay_button : R.drawable.bg_lpay_button);
        mLPayButtonLogoImageView.setImageResource(isGenoaPay ? R.drawable.genoapay_logo_white : R.drawable.lpay_logo_white);
        mLPayButtonLogoImageView.setPadding(padding, padding, padding, padding);

        mGPayButtonContainer.setVisibility(View.GONE);
        setPaymentButtonsVisibility(getAllButtons(), View.GONE);
        hideAfterpayPanel();
        hideKlarnaPanel();
        hideLPayPanel();

        setupAgeRestriction();

        registerClickListeners();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == BraintreeRequestCodes.VISA_CHECKOUT) {
            showLoading(LoadingDialogType.DEFAULT);
            AppLogger.d("VC_onActivityResult", "Result got back from Visa Checkout SDK");
            String msg = "";

            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    msg = "User Canceled, Result Code : " + resultCode;
                    break;
                case VisaCheckoutSdk.ResultCode.RESULT_SDK_NOT_INITIALIZED:
                    msg = "Sdk not initialized  failed, Result Code : " + resultCode;
                    break;
                case VisaCheckoutSdk.ResultCode.RESULT_INITIALIZED_FAILED:
                    msg = "VisaPaymentInfo validation failed, Result Code : " + resultCode;
                    break;
                case Activity.RESULT_OK:
                    if (data != null) {
                        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                                mValue.getSummary().getTotal(), AppConstants.VCO);
                        break;
                    }
                default:
                    msg = "Purchase failed!";
                    break;
            }

            if (!msg.isEmpty()) {
                hideLoading();
                AppLogger.d("VC_onActivityResult", msg);
                onError(msg);
            }
        }*/
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
                        hidePaymentButtons();
                    }
                });
            }

            if (!isCartLoading()) {
                setCartIsLoading(true);
                mPresenter.callCartContent(null);
            }

        } else {
            showNoCartItemsLayout();
        }
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
    public boolean isCartLoading() {
        return mIsCartLoading;
    }

    @Override
    public void setCartIsLoading(boolean val) {
        this.mIsCartLoading = val;
    }

    @Override
    public void showCartDetails(List<MappedShipment> items) {

        showCartDetailsOnHost(items);

        showCartDetailsOnChild(items);
    }

    @Override
    public void showCartDetailsOnChild(List<MappedShipment> items) {
        if (items == null) { //do nothing (ie. when increasing order quantity, returns a soldout/out of stock message)
            return;
        }

        mItemList = items;

        refreshItemList(mDeliveryAddress != null);

        if (items.isEmpty()) {
            //no items
            showNoCartItemsLayout();
            CommonUtils.clearSaleItem(mActivity);
        } else {
            showCartItems();
        }
    }

    private void refreshItemList(boolean showFooter) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.replaceData(mActivity, mItemList, showFooter);
    }

    @Override
    public void showCartDetailsOnHost(List<MappedShipment> items) {
        if (mCheckoutHostView != null) {
            mCheckoutHostView.showCartDetails(items);
        }
    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {
        mDeliveryAddress = deliveryAddress;
        if (!decorationInfoList.isEmpty()) {
            mDecorationInfoList.clear();
            mDecorationInfoList.addAll(decorationInfoList);
        }

        if (deliveryAddress != null) {
            ((TextView) mAddressLayout.findViewById(R.id.partial_checkout_address_name)).setText(deliveryAddress.name);
            ((TextView) mAddressLayout.findViewById(R.id.partial_checkout_address_details)).setText(formAddressDetails(deliveryAddress));

            mAddNewAddressLayout.setVisibility(View.GONE);
            mAddressLayout.setVisibility(View.VISIBLE);
            mAddressChangeView.setVisibility(View.VISIBLE);
        } else {
            mAddNewAddressLayout.setVisibility(View.VISIBLE);
            mAddressLayout.setVisibility(View.GONE);
            mAddressChangeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showCartDetailsFooter(boolean show) {
        if (mCheckoutHostView != null) {
            mCheckoutHostView.showCartDetailsFooter(show);
        }
        refreshItemList(show);
    }

    @Override
    public void showCartDetailsPostcode(String postcode) {
        if (mCheckoutHostView != null) {
            mCheckoutHostView.showCartDetailsPostcode(postcode);
        }
        if (mAdapter == null) {
            return;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail) {
        if (getBoolean(R.bool.is_ozsale_app) &&
                (deliveryOptions != null && !deliveryOptions.isEmpty())) {
            mDeliveryOptions = deliveryOptions;
            mDeliveryServicePackageDetail = deliveryServicePackageDetail;

            mDeliveryOptionRootLayout.setVisibility(View.VISIBLE);

            String deliveryOptionName = "";
            Double deliveryOptionPrice = 0d;
            for (DeliveryOption option : deliveryOptions) {
                if (option.getSelected()) {
                    mSelectedDeliveryOption = option;
                    deliveryOptionName = option.getDeliveryOptions().get(0); //get name
                    deliveryOptionPrice = option.getPrice();
                    break;
                }
            }

            displayPaymentDetails();
            displayDeliveryOptionsUI(deliveryOptionName, deliveryOptionPrice);
        } else {
            if (mDeliveryOptionRootLayout != null) {
                mDeliveryOptionRootLayout.setVisibility(View.GONE);
            }
            displayPaymentDetails();
        }
    }

    private void displayDeliveryOptionsUI(String deliveryOptionName, Double deliveryOptionPrice) {
        mDeliveryOptionRootLayout.setOnClickListener(v -> showDeliveryOptionsController());

        String priceText = null;
        if (isAddressValid() && (!mPresenter.isShippingByPostcodeEnabled() || isShipmentAvailable) && deliveryOptionPrice != null) {
            priceText = deliveryOptionPrice > 0 ? PriceUtils.getPriceStringValue(deliveryOptionPrice) : mActivity.getResources().getString(R.string.free_text);
        }

        mDeliveryOptionTypeText.setVisibility(View.VISIBLE);
        mDeliveryOptionTypeText.setText(deliveryOptionName);
        mDeliveryOptionPriceTextView.setText(priceText);
        mDeliveryOptionTypeText.setTypeface(mDeliveryOptionTypeText.getTypeface(), Typeface.BOLD);
    }

    private void showDeliveryOptionsController() {
        mIsPaymentMethodChanged = false;
        String deliveryAddressId = mDeliveryAddress != null ? mDeliveryAddress.id : "";
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.DELIVERY_OPTIONS_LIST, new Gson().toJson(mDeliveryOptions, new TypeToken<List<DeliveryOption>>() {
        }.getType()));
        bundle.putString(BundleKeys.DELIVERY_OPTIONS_DELIVERY_ADDRESS_ID, deliveryAddressId);
        bundle.putString(BundleKeys.DELIVERY_OPTIONS_DELIVERY_SERVICE_PACKAGE_DETAIL, new Gson().toJson(mDeliveryServicePackageDetail, DeliveryServicePackageDetail.class));
        bundle.putBoolean(BundleKeys.DELIVERY_OPTIONS_IS_ADDRESS_VALID, isAddressValid() && (!mPresenter.isShippingByPostcodeEnabled() || isShipmentAvailable));
        getRouter().pushController(RouterTransaction.with(new DeliveryOptionsController(bundle)).
                pushChangeHandler(new HorizontalChangeHandler(false)).popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {

        if (paymentMethod == null) {
            mAddNewPaymentLayout.setVisibility(View.VISIBLE);
            mPaymentLayout.setVisibility(View.GONE);
            mPaymentChangeView.setVisibility(View.GONE);

            showPaymentButtons();
            mLastUserPaymentMethod = null;
        } else {
            mLastUserPaymentMethod = paymentMethod;
        }
    }

    private void displayPaymentDetails() {
        PaymentMethod paymentMethod = selectedPaymentMethod;
        if (paymentMethod != null) {

            if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) {
                mMasterpassButton.setVisibility(View.GONE);
                mVcoButton.setVisibility(View.GONE);
                mPaypalButton.setVisibility(View.VISIBLE);
                mPaypalCreditButton.setVisibility(View.GONE);
            } else {
                showPaymentButtons();
                mPaypalCreditButton.setVisibility(View.GONE);
                mPaypalButton.setVisibility(View.VISIBLE);
            }

            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).setText(paymentMethod.getPaymentType());
            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).setTypeface(((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).getTypeface(),
                    Typeface.BOLD);
            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_details)).setText(paymentMethod.getDescription());

//            Hardcoded visa checkout logo if visa checkout is payment type. this is due to api not wanting to update their response LOL.
            String visaCheckoutLogoUrl = "https://assets.secure.checkout.visa.com/VCO/images/acc_40x30_wht01.png";
            String paymentMethodImageUrl = paymentMethod.getImageUrl();
            if (paymentMethod.getPaymentType().equalsIgnoreCase("VisaCheckoutBraintree") || paymentMethod.getPaymentType().equalsIgnoreCase("VisaCheckoutCyberSource")) {
                paymentMethodImageUrl = visaCheckoutLogoUrl;
            }

            if (paymentMethodImageUrl != null) {
                ImageUtils.loadImage(paymentMethodImageUrl,
                        mPaymentLayout.findViewById(R.id.partial_checkout_payment_image));
            }

            //set brand icon for stripe
            if (paymentMethod.getProviderType().equalsIgnoreCase(AppConstants.STRIPE)) {
                ImageView cardBrandImageView = mPaymentLayout.findViewById(R.id.partial_checkout_payment_image);

                if (paymentMethod.getPaymentType().equalsIgnoreCase(AppConstants.AMEX) ||
                        paymentMethod.getPaymentType().equalsIgnoreCase(AppConstants.AMERICAN_EXPRESS)) {
                    cardBrandImageView.setImageResource(CardBrand.AmericanExpress.getIcon());
                } else {
                    cardBrandImageView.setImageResource(paymentMethod.getCardBrand().getIcon());
                }
            }

            mAddNewPaymentLayout.setVisibility(View.GONE);
            mPaymentLayout.setVisibility(View.VISIBLE);
            mPaymentChangeView.setVisibility(View.VISIBLE);


        } else {
            //Payment buttons
            showPaymentButtons();
            mAddNewPaymentLayout.setVisibility(View.VISIBLE);
            mPaymentLayout.setVisibility(View.GONE);
            mPaymentChangeView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {
        mAddNewVoucherLayout.setVisibility(View.VISIBLE);
        if (vouchers != null) {
            mVouchers = new ArrayList<>(vouchers);
        }
    }

    @Override
    public void setIsShipmentAvailable(boolean isShipmentAvailable) {
        this.isShipmentAvailable = isShipmentAvailable;
    }

    @Override
    public void showSummaryDetails(Summary summary) {
        if (summary != null) {
            mTotalValue = summary.getTotal();
            setupSummaryShipping(summary);
            setupSummaryVouchers(summary);

            if (mDeliveryAddress == null) {
                mSummaryTotalTextView.setVisibility(View.GONE);
            } else {
                mSummaryTotalTextView.setVisibility(View.VISIBLE);
                mSummaryTotalTextView.setText(PriceUtils.getPriceStringValue(summary.getTotal()));
            }
        }

    }

    private void setupSummaryShipping(Summary summary) {
        mSummaryShippingFeeContainer.setVisibility(View.VISIBLE);

        mSummarySubtotalTextView.setText(PriceUtils.getPriceStringValue(summary.getSubtotal()));
        String postcode = mDeliveryAddress != null ? mDeliveryAddress.getPostcode() : null;

        if (!mPresenter.isShippingByPostcodeEnabled() ||
                postcode == null || summary.getDelivery() == null) {
            mSummaryShippingLabelTextView.setText(mActivity.getResources().getString(R.string.shipping_text));
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(
                    mActivity.getResources().getString(R.string.shipping_text)
            );
            spannableStringBuilder.append(" (");
            int start = spannableStringBuilder.length();
            int color = mActivity.getResources().getColor(R.color.checkout_item_footer_other_text_color);
            spannableStringBuilder.append(
                    postcode,
                    new StyleSpan(BOLD),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.setSpan(
                    new ForegroundColorSpan(color),
                    start,
                    spannableStringBuilder.length(),
                    SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableStringBuilder.append(")");
            mSummaryShippingLabelTextView.setText(spannableStringBuilder);
        }

        if (mPresenter.isShippingByPostcodeEnabled() && !isShipmentAvailable) {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            final String unavailableText = mPresenter.getTemplateTextsRepository() == null ? "Unavailable" : mPresenter.getTemplateTextsRepository().getUnavailable();
            mSummaryShippingFeeTextView.setText(unavailableText);
            mSummaryShippingFeeTextView.setTextColor(mActivity.getResources().getColor(R.color.checkout_item_footer_red_text_color));
            mFreeShippingLayout.setVisibility(View.GONE);
        } else if (!isAddressValid()) {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mFreeShippingLayout.setVisibility(View.GONE);
            mSummaryShippingFeeTextView.setText(mActivity.getResources().getString(R.string.enter_address_above));
            mSummaryShippingFeeTextView.setTextColor(mActivity.getResources().getColor(R.color.enter_address_text_color));
        } else if (summary.getDelivery() == null) {
            mSummaryShippingFeeTextView.setVisibility(View.GONE);
            mFreeShippingLayout.setVisibility(View.GONE);
        } else if (summary.getDelivery() == 0) {
            mSummaryShippingFeeTextView.setVisibility(View.GONE);
            mFreeShippingLayout.setVisibility(View.VISIBLE);
        } else {
            mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
            mSummaryShippingFeeTextView.setText(PriceUtils.getPriceStringValue(summary.getDelivery()));
            mSummaryShippingFeeTextView.setTextColor(mActivity.getResources().getColor(R.color.text_dark));
            mFreeShippingLayout.setVisibility(View.GONE);
        }
    }

    private void setupSummaryVouchers(Summary summary) {
        mSummaryVoucherTextView.setText(PriceUtils.getPriceStringValue(summary.getDiscount()));
        mDiscountValue = summary.getDiscount();

        if (summary.getTax() > 0) {
            mSummaryTaxTextView.setText(PriceUtils.getPriceStringValue(summary.getTax()));
            mSummaryTaxContainer.setVisibility(View.VISIBLE);
        } else {
            mSummaryTaxContainer.setVisibility(View.GONE);
        }

        if (summary.getDiscount() > 0) {
            mVoucherValueContainer.setVisibility(View.VISIBLE);
            mVoucherValueTextView.setVisibility(View.VISIBLE);
            final String voucherValueText = PriceUtils.getPriceStringValue(summary.getDiscount()) + " " + getString(R.string.voucher);
            mVoucherValueTextView.setText(voucherValueText);
        } else {
            mVoucherValueTextView.setVisibility(View.GONE);
            mVoucherValueContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        mPaymentList.clear();
        mPaymentList.addAll(paymentList);

        selectedPaymentMethod = getCompatiblePaymentType(mLastUserPaymentMethod,
                selectedPaymentMethod,
                getSelectedDeliveryOption());

        displayPaymentDetails();
    }

    @Override
    public void showAfterpayPanel(boolean isAvailable, String description) {
        if (mActivity.getResources().getBoolean(R.bool.is_afterpay_disabled_client_override)) {
            mAfterpayHolder.setVisibility(View.GONE);
            return;
        }

        mAfterpayHolder.setVisibility(View.VISIBLE);
        SpannableStringBuilder spannableString = new SpannableStringBuilder(description);
        StringUtils.applySpanToSubstringsMatching(
                spannableString,
                new StyleSpan(BOLD),
                mActivity.getResources().getString(R.string.regex_currency),
                SPAN_EXCLUSIVE_INCLUSIVE);
        mAfterpayDescription.setText(spannableString);
        mAfterpayDescription.setTypeface(Typeface
                .createFromAsset(mActivity.getAssets(),
                        mActivity.getResources().getString(R.string.font_raleway_regular)));


        mAfterpayButton.setVisibility(isAvailable ? View.VISIBLE : View.GONE);
        mAfterpayInlineLogo.setVisibility(isAvailable ? View.GONE : View.VISIBLE);
    }

    @Override
    public void hideAfterpayPanel() {
        mAfterpayHolder.setVisibility(View.GONE);
    }

    @Override
    public void showLPayPanel() {
        mLPayHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLPayPanel() {
        mLPayHolder.setVisibility(View.GONE);
    }

    @Override
    public void showKlarnaPanel(String description) {
        if (description == null || description.isEmpty()) {
            mKlarnaDescriptionView.setVisibility(View.GONE);
        } else {
            mKlarnaDescriptionView.setVisibility(View.VISIBLE);
            mKlarnaDescriptionView.setWebViewClient(new WebViewClient() {

                @SuppressWarnings("deprecation")
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!url.contains("about:blank")) {
                        ActivityLaunchUtil.launchActivity(mActivity, url);
                    }
                    return true;
                }

            });
            String mHtmlHeader = StringUtils.applyStyleToCSS(new StringUtils.CSSStyle() {
                @Override
                public String getBodyFontName() {
                    return StringUtils.typeFaceFamilyFromFilename(
                            mActivity.getResources().getString(R.string.font_app_regular));
                }

                @Override
                public String getBodyFontColor() {
                    String hex = Integer.toHexString(
                            mActivity.getResources().getColor(R.color.text_extra_dark));
                    if (hex.length() > 6) {
                        hex = hex.substring(2);
                    }
                    return "#" + hex;
                }

                @Override
                public String getBoldFontName() {
                    return StringUtils.typeFaceFamilyFromFilename(
                            mActivity.getResources().getString(R.string.font_app_regular));
                }

                @Override
                public String getBoldFontColor() {
                    String hex = Integer.toHexString(
                            mActivity.getResources().getColor(R.color.text_extra_dark));
                    if (hex.length() > 6) {
                        hex = hex.substring(2);
                    }
                    return "#" + hex;
                }
            }, mActivity.getResources()
                    .getString(R.string.base_html_template_header));

            String mHtmlFooter = mActivity.getResources()
                    .getString(R.string.base_html_template_footer);
            mKlarnaDescriptionView.loadDataWithBaseURL(null, mHtmlHeader + description + mHtmlFooter,
                    "text/html", "UTF-8", null);
            mKlarnaDescriptionView.setBackgroundColor(mActivity.getResources().getColor(R.color.transparent));
        }

        mKlarnaButton.setOnClickListener(v -> onKlarnaButtonClick());

        mKlarnaButton.setVisibility(View.VISIBLE);
        mKlarnaContainer.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideKlarnaPanel() {
        mKlarnaButton.setVisibility(View.GONE);
        mKlarnaContainer.setVisibility(View.GONE);
    }

    @Override
    public void showZipPayPanel() {
        mZipPayHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideZipPayPanel() {
        mZipPayHolder.setVisibility(View.GONE);
    }

    private void onKlarnaButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("Klarna"));
        logInitiateCheckout(mActivity, AppConstants.KLARNA, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.KLARNA);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        Bundle bundle = new BundleBuilder(new Bundle())
                .build();

        KlarnaViewController controller = new KlarnaViewController(bundle);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler());

        if (mActivity.getMainController().getPopUpHostRouter() != null) {
            mActivity.getMainController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private String getSelectedDeliveryOption() {
        if (mSelectedDeliveryOption != null &&
                mSelectedDeliveryOption.getDeliveryOptions() != null &&
                !mSelectedDeliveryOption.getDeliveryOptions().isEmpty()) {
            return mSelectedDeliveryOption.getDeliveryOptions().get(0);
        }
        return "";
    }

    private PaymentMethod getCompatiblePaymentType(PaymentMethod lastPaymentMethod,
                                                   PaymentMethod selectedPaymentMethod,
                                                   String deliveryOption) {

        ArrayList<PaymentMethod> defaultPayments = new ArrayList<>();
        if (selectedPaymentMethod != null) defaultPayments.add(selectedPaymentMethod);
        if (lastPaymentMethod != null) defaultPayments.add(lastPaymentMethod);

        return !defaultPayments.isEmpty() ? defaultPayments.get(0) : null;
    }

    @Override
    public void storeCartDetails(CartDetailsMapper mappedValues) {
        mValue = mappedValues;
    }

    private boolean isThreeDSecureRequired() {
        if (mValue == null) {
            return false;
        }

        return mValue.getThreeDSecureRequired();
    }

    private double getCartTotalAmount() {
        if (mValue == null) {
            return 0d;
        }

        return mValue.getSummary().getTotal();
    }

    @Override
    public void triggerLoginTicket() {
        assert (mActivity) != null;
        mActivity.callLoginTicket(false);
    }

    @Override
    public void updateCheckoutBadge() {
        if (mActivity.isAuthorized()) {
            mActivity.getMainController().updatePartialCartItemsSize();
        }
    }

    @Override
    public Router getDisplayRouter() {
        return getRouter();
    }

//    @Override
//    public void initializeVisaCheckout() {
//        if (mVcoPresenter != null && isViewAttached()) {
//            if (mVcoPresenter.isVisaCheckoutEnabled()) {
//                mVcoPresenter.setupVisaCheckout(true);
//            }
//        }
//    }

    private void showShippingUnavailableMessage() {
        mActivity.showErrorMessage(mPresenter.getTemplateTextsRepository().getImpossibleToDeliverAtLocationMessage());
    }

    private boolean commonPaymentAbilityDetermination() {
        if (hasAgeRestriction && !isAgeValid()) {
            mAgeRestrictionNotice.setVisibility(View.VISIBLE);
            CommonUtils.shakeView(mAgeRestrictionNotice);
            return false;
        }
        if (!isAddressValid()) {
            showAddAddressController();
            return false;
        } else if (mPresenter.isShippingByPostcodeEnabled() && !isShipmentAvailable) {
            showShippingUnavailableMessage();
            return false;
        }
        return true;
    }

    private void onPayButtonClick() {
        String paymentProviderType = selectedPaymentMethod.getProviderType();
        String paymentLogType = AppConstants.REGULAR;

        if (!commonPaymentAbilityDetermination()) {
            logInitiateCheckout(
                    mActivity,
                    paymentProviderType,
                    mItemList.size(),
                    mValue.getSummary().getTotal(),
                    paymentLogType);
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (selectedPaymentMethod == null) {
            showAddPaymentMethodController();
        } else if (selectedPaymentMethod.getProviderType() != null &&
                selectedPaymentMethod.getProviderType().equalsIgnoreCase(AppConstants.STRIPE)) {
            if (mPresenter.isStripeEnabled() && mPresenter.getStripePublicKey() != null) {
                mActivity.callCreatePaymentTransactionStripe(AppConstants.STRIPE,
                        selectedPaymentMethod.getToken());
                paymentLogType = AppConstants.STRIPE;
            } else {
                CustomAlertDialog.showCustomAlertDialog(
                        mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        mActivity.getResources().getString(R.string.stripe_error_occured));
            }
        } else {
            if (mActivity.isBraintreeInitialized()) {
                mActivity.callCreatePaymentTransaction(
                        selectedPaymentMethod,
                        "",
                        isThreeDSecureRequired(),
                        getCartTotalAmount());
            } else {
                mActivity.fetchBraintreeAuthorization(new FetchBraintreeClientTokenHandler() {
                    @Override
                    public void onSuccess() {
                        mActivity.callCreatePaymentTransaction(
                                selectedPaymentMethod,
                                "",
                                isThreeDSecureRequired(),
                                getCartTotalAmount());
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }
        }

        logInitiateCheckout(
                mActivity,
                paymentProviderType,
                mItemList.size(),
                mValue.getSummary().getTotal(),
                paymentLogType);
    }

    private void onGPayButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("Google Pay"));
        logInitiateCheckout(mActivity, AppConstants.GPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.GPAY);

        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.payWithGoogle(mValue.getSummary().getTotal());
    }

    private void onPaypalButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("Paypal"));
        logInitiateCheckout(mActivity, AppConstants.PAYPAL, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.PAYPAL);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        mPresenter.setLastCartRedirection(DataCollector.EventParameters.LastRedirection.PAYPAL);
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            //If no selected payment method displayed, call paypal
            if (selectedPaymentMethod == null || selectedPaymentMethod.getProviderType().equalsIgnoreCase(AppConstants.STRIPE)) {
                mActivity.startPaypalPayment();
            } else {
                mActivity.callCreatePaymentTransaction(
                        selectedPaymentMethod,
                        "",
                        isThreeDSecureRequired(),
                        getCartTotalAmount());
            }
        }

    }

    private void onPaypalCreditButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("Paypal Credit"));
        logInitiateCheckout(mActivity, AppConstants.PAYPALCREDIT, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.PAYPALCREDIT);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        mPresenter.setLastCartRedirection(DataCollector.EventParameters.LastRedirection.PAYPAL);
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (selectedPaymentMethod == null) {
                mActivity.startPaypalCreditPayment(String.valueOf(mValue.getSummary().getTotal()));
            } else {
                mActivity.callCreatePaymentTransaction(
                        selectedPaymentMethod,
                        "",
                        isThreeDSecureRequired(),
                        getCartTotalAmount());
            }
        }

    }

    private void onMasterpassButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("Masterpass"));
        logInitiateCheckout(mActivity, AppConstants.MASTERPASS, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.MASTERPASS);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        mPresenter.setLastCartRedirection(DataCollector.EventParameters.LastRedirection.MASTERPASS);
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);
        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void onAfterpayInfoButtonClick() {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(FloatingImageViewerController.KEY_SOURCE_URL, mPresenter.getAfterpayLightboxImgUrl())
                .putInt(FloatingImageViewerController.KEY_SOURCE_DRAWABLE_ID, R.drawable.afterpay_lightbox)
                .build();

        FloatingImageViewerController controller = new FloatingImageViewerController(bundle);

        controller.setImageClickListener((view, x, y) -> {
            Intent openUrl = new Intent(Intent.ACTION_VIEW);
            openUrl.setData(Uri.parse(mPresenter.getAfterpayTermsLink()));
            startActivity(openUrl);
        });

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler());

        if (mActivity.getMainController().getPopUpHostRouter() != null) {
            mActivity.getMainController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private void onAfterpayButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("Afterpay"));
        logInitiateCheckout(mActivity, AppConstants.AFTERPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.AFTERPAY);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        Bundle bundle = new BundleBuilder(new Bundle())
                .build();

        AfterpayViewController controller = new AfterpayViewController(bundle);

        controller.setEventListener(new AfterpayViewController.EventListener() {
            @Override
            public void onError(String errorMessage) {
                logFailedTransaction(mActivity, errorMessage);
            }
        });

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler());

        if (mActivity.getMainController().getPopUpHostRouter() != null) {
            mActivity.getMainController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private void onLPayButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("LPay"));
        logInitiateCheckout(mActivity, AppConstants.LPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.LPAY);

        logCommonCheckoutEvent(mActivity, CheckoutUserActivityOperationType.LPAY_BUTTON_CLICK.getValue());

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        Bundle bundle = new BundleBuilder(new Bundle())
                .build();

        LPayViewController controller = new LPayViewController(bundle);
        controller.setGenoaPay(Settings.getSelectedCountry().countryId.equalsIgnoreCase("NZ"));

        controller.setEventListener(new LPayViewController.EventListener() {
            @Override
            public void onCreateOrder() {
                logCommonCheckoutEvent(mActivity, CheckoutUserActivityOperationType.LPAY_BUTTON_CREATE_ORDER.getValue());
            }

            @Override
            public void onCreateCharge() {
                logCommonCheckoutEvent(mActivity, CheckoutUserActivityOperationType.LPAY_BUTTON_CREATE_CHARGE.getValue());
            }

            @Override
            public void onError(String errorMessage) {
                logFailedTransaction(mActivity, errorMessage);
            }
        });

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler());

        if (mActivity.getMainController().getPopUpHostRouter() != null) {
            mActivity.getMainController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private void onZipPayButtonClick() {
        logGA4AddPaymentInfoEvent(createGA4AddPaymentInfoParams("ZipPay"));
        logInitiateCheckout(mActivity, AppConstants.ZIPPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.ZIPPAY);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        ZipPayViewController controller = new ZipPayViewController(new Bundle());

        controller.setEventListener(new ZipPayViewController.EventListener() {
            @Override
            public void onError(String errorMessage) {
                logFailedTransaction(mActivity, errorMessage);
            }
        });

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler());

        if (mActivity.getMainController().getPopUpHostRouter() != null) {
            mActivity.getMainController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private void scrollToTopOfPayment() {
        View topPayButton = getTopPayButton();

        if (topPayButton == null) {
            return;
        }

        int[] payButtonLocation = new int[2];
        int[] scrollViewLocation = new int[2];

        topPayButton.getLocationOnScreen(payButtonLocation);
        mNestedScrollView.getLocationOnScreen(scrollViewLocation);

        final int scrollBy = payButtonLocation[1] - scrollViewLocation[1];

        mNestedScrollView.scrollBy(0, scrollBy);
    }

    private View getTopPayButton() {
        List<View> buttons = new ArrayList<View>() {{
            add(mGPayButton);
            addAll(getAllButtons());
            add(mAfterpayButton);
            add(mKlarnaButton);
            add(mLPayButton);
            add(mZipPayButton);
        }};

        for (View button : buttons) {
            if (button != null && button.getVisibility() == View.VISIBLE) {
                return button;
            }
        }
        return null;
    }

    private boolean isAddressValid() {
        return mDeliveryAddress != null;
    }

    @Optional
    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {
        mActivity.getMainController().showShopController();
    }

    private String formAddressDetails(DeliveryAddress deliveryAddress) {

        return deliveryAddress.addressLines + ", "
                + deliveryAddress.suburb + ", "
                + deliveryAddress.state + ", "
                + deliveryAddress.postcode + ", "
                + deliveryAddress.phone;
    }

    private void showNoCartItemsLayout() {
        if (mPresenter.hasActiveCheckoutSession()) {
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put(DataCollector.EventParameters.TYPE, DataCollector.EventParameters.EventProgress.END.getValue());
            parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
            parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
            DataCollector.logEvent(Events.checkoutJourney, parameters);
        }

        hidePaymentButtons();
        if (mNoCartItemsLayout != null) {
            mNoCartItemsLayout.setVisibility(View.VISIBLE);
            if (currentBottomPopupView != null) {
                currentBottomPopupView.setListener(null);
                currentBottomPopupView.dismiss(false);
            }
            if (mGoToShopButton != null) {
                mGoToShopButton.setVisibility(View.VISIBLE);
                CommonUtils.fadeInView(mGoToShopButton, null);
            }
        }
        mCheckoutContainer.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();

        if (mPresenter.checkIsLoggedIn()) {
            mPresenter.loadRecentlyViewedItems();
        } else {
            mPresenter.loadBestSellers("");
        }

        if (mCheckoutHostView != null) {
            mCheckoutHostView.showCartDetailsOnHost(null);
        }
    }

    private void showCartItems() {
        GA4EventParams.GA4ViewCartParams ga4EventParams = new GA4EventParams.GA4ViewCartParams();
        prepareItemsForGA4EventParams(ga4EventParams);
        ga4EventParams.setCurrency(Settings.getSelectedCountry().currencyCode);
        ga4EventParams.setValue(mTotalValue);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.GA4_EVENT_PARAMS, ga4EventParams);
        DataCollector.logEvent(Events.addToCartJourneyViewCart, parameters);

        showPaymentButtons();
        if (mNoCartItemsLayout != null) {
            mNoCartItemsLayout.setVisibility(View.GONE);
        }
        mCheckoutContainer.setVisibility(View.VISIBLE);
    }

    private void hidePaymentButtons() {
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mButtonHolder.setVisibility(View.VISIBLE);
        setPaymentButtonsVisibility(getAllButtons(), View.GONE);
        checkVisiblePaymentButtons();
        showGPayButtonIfAvailable();
        ((MainActivity) getActivity()).isReadyToGPay(task -> {
            isGPayAvailable = task.isSuccessful() && mPresenter.isStripeEnabled();
            showGPayButtonIfAvailable();
        });
    }

    private void showGPayButtonIfAvailable() {
        if (!isViewAttached() || !isViewBound()) return;

        mGPayButtonContainer.setVisibility(isGPayAvailable ? View.VISIBLE : View.GONE);

    }

    private void setPaymentButtonsVisibility(List<View> buttons, int visibility) {
        for (View button : buttons) {
            button.setVisibility(visibility);
        }
    }

    private List<View> getAllButtons() {
        return new ArrayList<View>() {{
            add(mPayButton);
            add(mPaypalButton);
            add(mPaypalCreditButton);
            add(mMasterpassButton);
            add(mVcoButton);
        }};
    }

    private List<View> getSupposedlyVisibleButtons() {
        List<View> buttons = new ArrayList<>();
        if (selectedPaymentMethod != null) {
            String paymentType = selectedPaymentMethod.getPaymentType();
            if (!paymentType.equalsIgnoreCase(CARD_PAYPAL) ||
                    !paymentType.equalsIgnoreCase(CARD_MASTERPASS)) {
                buttons.add(mPayButton);
            }
            if (paymentType.equalsIgnoreCase(CARD_PAYPAL) && mPresenter.isPaypalEnabled()) {
                buttons.add(mPaypalButton);
            }
        } else {
            //no selected payment Method
            buttons.add(mPayButton);
            if (mPresenter.isPaypalEnabled()) buttons.add(mPaypalButton);
            if (mPresenter.isVcoEnabled()) buttons.add(mVcoButton);
            if (mPresenter.isMasterPassEnabled()) {
                buttons.add(mMasterpassButton);
            }
        }
        if (mPresenter.isPaypalCreditEnabled()) buttons.add(mPaypalCreditButton);
        return buttons;
    }

    private void checkVisiblePaymentButtons() {
        setPaymentButtonsVisibility(getSupposedlyVisibleButtons(), View.VISIBLE);
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (!mIsCartLoading) {
            loadCart();
        }
    }

    @Override
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);
        mIsPaymentMethodChanged = false;
        if (previousController instanceof AddPaymentController) {
            mIsPaymentMethodChanged = ((AddPaymentController) previousController).isPaymentMethodChanged();
        } else if (previousController instanceof PaymentSelectController) {
            final PaymentSelectController paymentSelectController = (PaymentSelectController) previousController;
            mIsPaymentMethodChanged = paymentSelectController.isPaymentMethodChanged();
            if (mIsPaymentMethodChanged) {
                selectedPaymentMethod = paymentSelectController.getSelectedPaymentMethod();
            }
        }
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        unregisterClickListeners();
        registerClickListeners();

        if (!mIsCartLoading &&
                !(previousController instanceof ViewAddressController) &&
                !(previousController instanceof AddVouchersController) &&
                (previousController != null || mPresenter.checkIsLoggedIn())) {
            loadCart();
        }

        if (previousController instanceof AddPaymentController) {
            AddPaymentController controller = (AddPaymentController) previousController;
            GA4EventParams.GA4AddPaymentInfoParams params = controller.getGa4AddPaymentInfoParams();
            if (params != null) {
                logGA4AddPaymentInfoEvent(params);
            }
        }

        if (previousController instanceof PaymentSelectController) {
            PaymentSelectController controller = (PaymentSelectController) previousController;
            GA4EventParams.GA4AddPaymentInfoParams params = controller.getGa4AddPaymentInfoParams();
            if (params != null) {
                logGA4AddPaymentInfoEvent(params);
            }
        }

        if (previousController instanceof AddNewAddressController) {
            AddNewAddressController controller = (AddNewAddressController) previousController;
            GA4EventParams.GA4AddShippingInfoParams params = controller.getGa4AddShippingInfoParams();
            if (params != null) {
                logGA4AddShippingInfoEvent(params);
            }
        }

        if (previousController instanceof ViewAddressController) {
            ViewAddressController controller = (ViewAddressController) previousController;
            GA4EventParams.GA4AddShippingInfoParams params = controller.getGa4AddShippingInfoParams();
            if (params != null) {
                logGA4AddShippingInfoEvent(params);
            }
        }
    }

    @Override
    public void onViewDidDisappear(Controller nextController) {
        super.onViewDidDisappear(nextController);

        unregisterClickListeners();
    }

    private void registerClickListeners() {
        if (mClickListeners != null) {
            return;
        }

        mClickListeners = new CompositeDisposable();
        mClickListeners.add(RxView.clicks(mPayButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onPayButtonClick()));
        mClickListeners.add(RxView.clicks(mPayButtonCheckout)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onPayButtonClick()));
        mClickListeners.add(RxView.clicks(mGPayButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onGPayButtonClick()));

        mClickListeners.add(RxView.clicks(mPaypalButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onPaypalButtonClick()));

        mClickListeners.add(RxView.clicks(mPaypalCreditButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onPaypalCreditButtonClick()));

        mClickListeners.add(RxView.clicks(mMasterpassButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onMasterpassButtonClick()));

        mClickListeners.add(RxView.clicks(mAfterpayInfoButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onAfterpayInfoButtonClick()));

        mClickListeners.add(RxView.clicks(mAfterpayButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onAfterpayButtonClick()));

        mClickListeners.add(RxView.clicks(mLPayButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onLPayButtonClick()));

        mClickListeners.add(RxView.clicks(mZipPayButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onZipPayButtonClick()));

        mChangeClickListeners = new CompositeDisposable();
        mChangeClickListeners.add(RxView.clicks(mAddressContainerLayout)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> changeAddress()));
        mChangeClickListeners.add(RxView.clicks(mAddressChangeView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> changeAddress()));

        mChangeClickListeners.add(RxView.clicks(mPaymentContainerLayout)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> changePayment()));
        mChangeClickListeners.add(RxView.clicks(mPaymentChangeView)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> changePayment()));

        mChangeClickListeners.add(RxView.clicks(mVoucherContainerLayout)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> changeVoucher()));
    }

    private void unregisterClickListeners() {
        if (mClickListeners != null) {
            mClickListeners.dispose();
        }
        mClickListeners = null;

        if (mChangeClickListeners != null) {
            mChangeClickListeners.dispose();
        }
        mChangeClickListeners = null;
    }

    private void showAddAddressController() {
        getRouter().pushController(RouterTransaction.with(new AddNewAddressController(new Gson().toJson(mDecorationInfoList), true))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void showPaymentSelectController() {
        mIsPaymentMethodChanged = false;

        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.PAYMENT_METHODS, new Gson().toJson(mPaymentList));
        bundle.putBoolean(BundleKeys.IS_FROM_CART, true);
        bundle.putString(BundleKeys.CART_TOTAL_COST, Double.toString(mValue.getSummary().getTotal()));
        if (mValue != null) {
            mValue.putInBundle(bundle, BundleKeys.CURRENT_ORDER_VALUE);
        }

        PaymentSelectController paymentSelectController = new PaymentSelectController(bundle);
        paymentSelectController.setSelectedPaymentMethod(selectedPaymentMethod);

        getRouter().pushController(RouterTransaction.with(paymentSelectController)
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void showAddPaymentMethodController() {
        mIsPaymentMethodChanged = false;

        AddPaymentController.Parameters.FromCheckout parameters = new AddPaymentController
                .Parameters.FromCheckout(
                Double.toString(mValue.getSummary().getTotal()),
                mValue);

        getRouter().pushController(RouterTransaction
                .with(AddPaymentController.newInstance(parameters))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

//    @Override
//    public void onVisaCheckoutButtonClicked() {
//
//        if (isProcessingVco) {
//            isProcessingVco = false;
//        }
//
//        double total = mValue == null ? 0.0 : mValue.getSummary().getTotal();
//
//        mPresenter.logInitiateCheckout(
//                mActivity,
//                PaymentInfo.VISA_CHECKOUT_BRAINTREE,
//                mItemList.size(),
//                total,
//                AppConstants.VCO);
//
//        if (!commonPaymentAbilityDetermination()) {
//            return;
//        }
//
//        getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.VISACHECKOUT);
//        PaymentInfo.setFabricPaymentType(DataCollector.EventParameters.PaymentOption.VCO.getValue());
//        mVcoPresenter.payWithVisaCheckout(mValue.getSummary().getTotal());
//    }

    private void selectStandardDeliveryOption() {
        for (DeliveryOption option : mDeliveryOptions) {
            boolean isSelected = option.getDeliveryOptions().get(0).equalsIgnoreCase(DeliveryOptions.STANDARD.toString());
            option.setSelected(isSelected);
        }
    }

    private SetDeliveryOption.OptionParameters createStandardDeliveryOptionRequest() {
        DeliveryOption standardDeliveryOption = null;

        for (DeliveryOption option : mDeliveryOptions) {
            if (option.getDeliveryOptions().get(0).equalsIgnoreCase(DeliveryOptions.STANDARD.toString())) {
                standardDeliveryOption = option;
                break;
            }
        }

        if (standardDeliveryOption == null) {
            return null;
        }

        standardDeliveryOption.setName(mPresenter.getStoredTemplateTexts(DeliveryOptions.KEY_DELIVERYOPTION_STANDARD_TITLE));

        SetDeliveryOption.OptionParameters optionParameters
                = new SetDeliveryOption.OptionParameters(mDeliveryAddress != null ? mDeliveryAddress.id : "", "",
                new Gson().toJson(standardDeliveryOption), null);

        return optionParameters;

    }

    @Override
    public void showItemDetail(View sourceView, int position, String seoIdentifierId, String imageUrl,
                               String skuId, String saleId, boolean isFreeDelivery,
                               String itemName, String brandName, String price, String oldPrice,
                               String productID) {

        if (CommonUtils.loadSaleItem(mActivity, productID).isEmpty()) {
            return;
        }

        SaleItemDetailsController.Parameters.FromCheckout parameters = new SaleItemDetailsController.Parameters.FromCheckout(
                position,
                imageUrl,
                CommonUtils.loadSaleItem(mActivity, productID),
                skuId,
                CommonUtils.loadSaleId(mActivity, productID),
                itemName,
                brandName,
                price,
                "",
                "",
                isFreeDelivery,
                false);

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

    private void setupAgeRestriction() {
        mAgeRestrictionDateInput.setInputType(InputType.TYPE_NULL);
        mAgeRestrictionDateInput.setClickable(true);
        mAgeRestrictionDateInput.setFocusableInTouchMode(false);
        mAgeRestrictionDateInput.setOnClickListener(view -> {
            final Calendar cal = Calendar.getInstance();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                cal.setTime(dateFormat.parse(mAgeRestrictionDateInput.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity,
                    R.style.DatePickerTheme,
                    (v, year, month, dayOfMonth) -> {

                        final Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        mAgeRestrictionDateInput.setText(dateFormat.format(calendar.getTime()));
                        birthday = calendar;
                        saveAgeRestrictionData();

                        logCommonCheckoutEvent(mActivity, isAgeValid() ? AgeRestrictionOperationType.VALID.getValue() : AgeRestrictionOperationType.NOTVALID.getValue());

                        mAgeRestrictionNotice.setVisibility(isAgeValid() ? View.GONE : View.VISIBLE);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
            logCommonCheckoutEvent(mActivity, AgeRestrictionOperationType.OPEN.getValue());
        });
    }

    @Override
    public void showAgeRestriction(boolean hasAgeRestriction) {
        this.hasAgeRestriction = hasAgeRestriction;
        mAgeRestrictionContainer.setVisibility(hasAgeRestriction ? View.VISIBLE : View.GONE);
        mAgeRestrictionNotice.setVisibility(birthday == null || isAgeValid() ? View.GONE : View.VISIBLE);

        if (mPresenter.getTemplateTextsRepository() != null) {
            if (mPresenter.getTemplateTextsRepository().getAgeRestrictedText() != null) {
                mAgeRestrictionDescription.setText(mPresenter.getTemplateTextsRepository().getAgeRestrictedText());
            }
            if (mPresenter.getTemplateTextsRepository().getPleaseConfirmAgeRestrictedText() != null) {
                mAgeRestrictionNotice.setText(mPresenter.getTemplateTextsRepository().getPleaseConfirmAgeRestrictedText());
            }
        }
    }

    @Override
    public void updateCartWithMappedValues(CartDetailsMapper mappedValues) {
        mPresenter.updateCartValues(mappedValues);
    }

    private boolean isAgeValid() {
        final Calendar now = Calendar.getInstance();
        return birthday != null && DateUtils.yearsBetweenCalendar(birthday, now) >= 18;
    }

    private void saveAgeRestrictionData() {
        if (birthday == null) {
            return;
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss", Locale.getDefault());
        final String dateString = dateFormat.format(birthday.getTime());
        final String postcode = mDeliveryAddress != null ? mDeliveryAddress.getPostcode() : null;
        mPresenter.saveAgeRestrictionData(dateString, postcode);
    }

    private void logGA4AddPaymentInfoEvent(GA4EventParams.GA4AddPaymentInfoParams params) {
        prepareItemsForGA4EventParams(params);
        params.setCoupon(PriceUtils.getPriceStringValue(mDiscountValue));
        params.setValue(mTotalValue);
        HashMap<String, Object> map = new HashMap<>();
        map.put(DataCollector.EventParameters.GA4_EVENT_PARAMS, params);
        DataCollector.logEvent(Events.GA4AddPaymentInfo, map);
    }

    private void logGA4AddShippingInfoEvent(GA4EventParams.GA4AddShippingInfoParams params) {
        prepareItemsForGA4EventParams(params);
        params.setCoupon(PriceUtils.getPriceStringValue(mDiscountValue));
        params.setValue(mTotalValue);
        HashMap<String, Object> map = new HashMap<>();
        map.put(DataCollector.EventParameters.GA4_EVENT_PARAMS, params);
        DataCollector.logEvent(Events.GA4AddPaymentInfo, map);
    }

    private void prepareItemsForGA4EventParams(GA4EventParams params) {
        final ArrayList<GA4EventParams.Item> ga4Items = new ArrayList<>();
        for (MappedShipment shipment : mItemList) {
            for (Item item : shipment.getMappedItems()) {
                GA4EventParams.Item ga4Item = new GA4EventParams.Item();
                ga4Item.setItemId(item.getItemID());
                ga4Item.setItemName(item.getItem());
                ga4Item.setPrice(item.getPrice());
                ga4Item.setQuantity(item.getQty());
                ga4Items.add(ga4Item);
            }
        }
        params.setItems(ga4Items);
    }

    private GA4EventParams.GA4AddPaymentInfoParams createGA4AddPaymentInfoParams(String paymentType) {
        GA4EventParams.GA4AddPaymentInfoParams params = new GA4EventParams.GA4AddPaymentInfoParams();
        params.setPaymentType(paymentType);
        params.setCurrency(Settings.getSelectedCountry().currencyCode);
        return params;
    }

    public void logInitiateCheckout(Context context, String paymentType, int numItems, double price,
                                    String selectedPaymentType) {
        CommonCheckoutRequest commonCheckoutRequest = new CommonCheckoutRequest();
        commonCheckoutRequest.setEventType(EventTypeId.EVENT_CHECKOUT);
        commonCheckoutRequest.setErrorDescription("");
        commonCheckoutRequest.setResult(0);
        commonCheckoutRequest.setGuestCheckout(false);

        switch (selectedPaymentType) {
            case AppConstants.GPAY:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.GPAY.getValue());
                break;
            case AppConstants.VCO:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.VCO.getValue());
                break;
            case AppConstants.AFTERPAY:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.AFTERPAY.getValue());
                break;
            case AppConstants.LPAY:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.LPAY.getValue());
                break;
            case AppConstants.REGULAR:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.REGULAR.getValue());
                break;
            case AppConstants.STRIPE:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.STRIPE.getValue());
                break;
            case AppConstants.MASTERPASS:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.MASTERPASS.getValue());
                break;
            case AppConstants.PAYPALCREDIT:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.PAYPALCREDIT.getValue());
                break;
            case AppConstants.PAYPAL:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.PAYPAL.getValue());
                break;
            case AppConstants.ZIPPAY:
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.ZIPPAY.getValue());
                break;
            default: // UNKNOWN
                commonCheckoutRequest.setOperation(DataCollector.EventParameters.Operation.UNKNOWN.getValue());
                break;
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.START_CHECKOUT_VALUE, price);
        parameters.put(DataCollector.EventParameters.START_CHECKOUT_CURRENCY,
                Settings.getSelectedCountry().currencySign);

        parameters.put(DataCollector.EventParameters.PAYMENT_METHOD_TYPE, paymentType);
        parameters.put(DataCollector.EventParameters.NUMBER_OF_ITEMS, numItems);
        parameters.put(DataCollector.EventParameters.PRICE, price);
        parameters.put(DataCollector.EventParameters.COUNTRY_ID, Settings.getSelectedCountry().countryId);
        parameters.put(DataCollector.EventParameters.START_CHECKOUT_REQUEST, commonCheckoutRequest);

        GA4EventParams.GA4BeginCheckoutParams ga4EventParams = new GA4EventParams.GA4BeginCheckoutParams();
        prepareItemsForGA4EventParams(ga4EventParams);
        ga4EventParams.setCurrency(Settings.getSelectedCountry().currencyCode);
        ga4EventParams.setValue(price);
        ga4EventParams.setCoupon(PriceUtils.getPriceStringValue(mDiscountValue));
        parameters.put(DataCollector.EventParameters.GA4_EVENT_PARAMS, ga4EventParams);

        DataCollector.logEvent(Events.InitiateCheckout, parameters);

        GA4EventParams.GA4PurchaseParams ga4PurchaseParams = new GA4EventParams.GA4PurchaseParams();
        prepareItemsForGA4EventParams(ga4PurchaseParams);
        ga4PurchaseParams.setCurrency(Settings.getSelectedCountry().currencyCode);
        ga4PurchaseParams.setValue(price);
        ga4PurchaseParams.setCoupon(PriceUtils.getPriceStringValue(mDiscountValue));
        mActivity.setGa4PurchaseParams(ga4PurchaseParams);

        mPresenter.setHasActiveCheckoutSession(true);
    }

    public void logCommonCheckoutEvent(Context context, int operation) {
        CommonCheckoutRequest request = new CommonCheckoutRequest();
        request.setEventType(EventTypeId.EVENT_CHECKOUT);
        request.setErrorDescription("");
        request.setResult(1);
        request.setGuestCheckout(false);
        request.setOperation(operation);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());

        parameters.put(DataCollector.EventParameters.COMMON_CHECKOUT_REQUEST, request);

        DataCollector.logEvent(Events.CommonCheckoutEvent, parameters);
    }

    public void logFailedTransaction(Context context, String errorMessage) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.PAYMENT_METHOD_TYPE,
                AppConstants.LPAY);
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.IS_NEW_USER,
                mPresenter.getIsNewUser());
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.RESULT, false);
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.SCREEN_NAME,
                AfterpayViewController.class.getSimpleName());
        parameters.put(au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.FAILED_TRANSACTION_MESSAGE,
                errorMessage);
        au.com.dealsdirect.service.datacollection.core.DataCollector.logEvent(Events.FailedTransaction, parameters);
    }

    public void logRemoveItemFromCart(Context context) {
        GA4EventParams.GA4RemoveFromCartParams ga4EventParams = new GA4EventParams.GA4RemoveFromCartParams();
        prepareItemsForGA4EventParams(ga4EventParams);
        ga4EventParams.setCurrency(Settings.getSelectedCountry().currencyCode);
        ga4EventParams.setValue(mTotalValue);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, context);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.GA4_EVENT_PARAMS, ga4EventParams);

        DataCollector.logEvent(Events.RemoveFromCart, parameters);
    }

    private void showBottomPopupView(String textContent) {
        if (mScrollViewContainer == null) {
            return;
        }
        if (currentBottomPopupView != null) {
            currentBottomPopupView.dismiss(true);
        }
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        currentBottomPopupView = new BottomPopupView(mScrollViewContainer, adapter);

        adapter.setWebViewContent(textContent);
        adapter.setOnCloseButtonClickListener(() -> currentBottomPopupView.dismiss(true));
        adapter.setWebViewClientOverrideUrlLoading(url -> {
            if (!url.contains("about:blank")) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
            }
            return true;
        });
        currentBottomPopupView.show(true);
    }

    @Override
    public void showBestSellers(List<GetBestSellerResponse> getBestSellerResponses) {
        if (mCheckoutHostView != null) {
            mCheckoutHostView.showBestSellers(getBestSellerResponses);
            return;
        }


        if (mWidgetArea == null) {
            return;
        }

        if (getBestSellerResponses == null || getBestSellerResponses.isEmpty()) {
            if (widgetAreaHorizontalRecyclerItemsViewHolder != null && mWidgetArea.indexOfChild(widgetAreaHorizontalRecyclerItemsViewHolder.itemView) < 0) {
                mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
            }
            return;
        }

        final List<SaleItemProduct> items = new ArrayList<>(getBestSellerResponses);

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(items, true, false, true);
        adapter.setOnItemTappedListener((item, position, size) -> {
            lastBestSellerItemPosition = position;

            SaleItemDetailsController.Parameters.FromSaleItemProduct parameters = new SaleItemDetailsController.Parameters.FromSaleItemProduct(item);

            RouterTransaction routerTransaction = RouterTransaction
                    .with(SaleItemDetailsController.newInstance(parameters));

            routerTransaction = routerTransaction
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            getRouter().pushController(routerTransaction);
        });

        adapter.setOnPriceInfoTappedListener(item -> mPresenter.getPricingInfoText(item.getSeoIdentifier()));

        adapter.setWishlistListener(new HorizontalScrollingItemsAdapter.WishlistListener() {
            @Override
            public void addToWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, true);
                };
                mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), "", delayedCallback);
            }

            @Override
            public void removeFromWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, false);
                };
                mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
            }

            @Override
            public boolean isProductInWishlist(SaleItemProduct item) {
                return mPresenter.isProductInWishlist(item.getId());
            }
        });

        final int orientation = ScreenUtils.getOrientation(mActivity);
        if (widgetAreaHorizontalRecyclerItemsViewHolder != null) {
            mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        }
        bestSellersWidgetHelper = new BestSellersWidgetHelper(adapter, mActivity, mPresenter.isTablet());
        widgetAreaHorizontalRecyclerItemsViewHolder = bestSellersWidgetHelper.createViewHolder(mWidgetArea, orientation);
        mWidgetArea.addView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        widgetAreaHorizontalRecyclerItemsViewHolder.onViewBound();
        bestSellersWidgetHelper.onBindViewHolder(widgetAreaHorizontalRecyclerItemsViewHolder, orientation);
    }

    @Override
    public void showPricingInfoText(String rrpText, Double totalPercentOff, Double originalPrice, String combinedPricingInfoText) {
        if (mCheckoutHostView != null) {
            mCheckoutHostView.showPricingInfoText(rrpText, totalPercentOff, originalPrice, combinedPricingInfoText);
            return;
        }

        if (mActivity.getSupplierOriginalPriceInfoHelper() != null) {
            showItemPricingInfoView(
                    mActivity.getSupplierOriginalPriceInfoHelper()
                            .getOriginalPriceInfoWebViewContent(rrpText, totalPercentOff, originalPrice));
        } else {
            showItemPricingInfoView(combinedPricingInfoText);
        }
    }

    @Override
    public void showRecentlyViewedItems(List<RecentlyViewedItemResponse> response) {
        if (mCheckoutHostView != null) {
            mCheckoutHostView.showRecentlyViewedItems(response);
            return;
        }


        if (mWidgetArea == null) {
            return;
        }

        if (response == null || response.isEmpty()) {
            if (widgetAreaHorizontalRecyclerItemsViewHolder != null && mWidgetArea.indexOfChild(widgetAreaHorizontalRecyclerItemsViewHolder.itemView) < 0) {
                mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
            }
            return;
        }

        final List<SaleItemProduct> items = new ArrayList<>(response);

        HorizontalScrollingItemsAdapter adapter = new HorizontalScrollingItemsAdapter(items, true, false, true);
        adapter.setOnItemTappedListener((item, position, size) -> {
            lastBestSellerItemPosition = position;

            SaleItemDetailsController.Parameters.FromSaleItemProduct parameters = new SaleItemDetailsController.Parameters.FromSaleItemProduct(item);

            RouterTransaction routerTransaction = RouterTransaction
                    .with(SaleItemDetailsController.newInstance(parameters));

            routerTransaction = routerTransaction
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            getRouter().pushController(routerTransaction);
        });

        adapter.setOnPriceInfoTappedListener(item -> mPresenter.getPricingInfoText(item.getSeoIdentifier()));

        adapter.setWishlistListener(new HorizontalScrollingItemsAdapter.WishlistListener() {
            @Override
            public void addToWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, true);
                };
                mPresenter.addProductToWishlist(item.getId(), item.getSeoIdentifier(), "", delayedCallback);
            }

            @Override
            public void removeFromWishlist(SaleItemProduct item) {
                final String productId = item.getId();
                CheckoutMvpPresenter.WishlistDelayedCallback delayedCallback = () -> {
                    logWishlistEvent(productId, false);
                };
                mPresenter.removeProductFromWishlist(item.getId(), delayedCallback);
            }

            @Override
            public boolean isProductInWishlist(SaleItemProduct item) {
                return mPresenter.isProductInWishlist(item.getId());
            }
        });

        final int orientation = ScreenUtils.getOrientation(mActivity);
        if (widgetAreaHorizontalRecyclerItemsViewHolder != null) {
            mWidgetArea.removeView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        }
        recentlyViewedWidgetHelper = new RecentlyViewedWidgetHelper(adapter, mActivity, mPresenter.isTablet());
        widgetAreaHorizontalRecyclerItemsViewHolder = recentlyViewedWidgetHelper.createViewHolder(mWidgetArea, orientation);
        mWidgetArea.addView(widgetAreaHorizontalRecyclerItemsViewHolder.itemView);
        widgetAreaHorizontalRecyclerItemsViewHolder.onViewBound();
        recentlyViewedWidgetHelper.onBindViewHolder(widgetAreaHorizontalRecyclerItemsViewHolder, orientation);
    }

    private void showItemPricingInfoView(String pricingInfoText) {
        if (mNoCartItemsLayout == null) {
            return;
        }
        if (currentBottomPopupView != null) {
            currentBottomPopupView.setListener(null);
            currentBottomPopupView.dismiss(true);
        }
        final BottomPopupWebViewContentAdapter adapter = new BottomPopupWebViewContentAdapter();
        currentBottomPopupView = new BottomPopupView(mNoCartItemsLayout, adapter);

        adapter.setWebViewContent(pricingInfoText);
        adapter.setOnCloseButtonClickListener(() -> currentBottomPopupView.dismiss(true));
        adapter.setWebViewClientOverrideUrlLoading(url -> {
            if (!url.contains("about:blank")) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
            }
            return true;
        });
        currentBottomPopupView.setListener(new BottomPopupView.BottomPopupViewListener() {
            @Override
            public void willShow() {
                if (mGoToShopButton == null) {
                    return;
                }
                CommonUtils.fadeOutView(mGoToShopButton, new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        mGoToShopButton.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mGoToShopButton.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onShow() {

            }

            @Override
            public void willDismiss() {

            }

            @Override
            public void onDismiss() {
                if (mGoToShopButton == null) {
                    return;
                }
                mGoToShopButton.setVisibility(View.VISIBLE);
                CommonUtils.fadeInView(mGoToShopButton, null);
            }
        });
        currentBottomPopupView.show(true);
    }

    private void logWishlistEvent(String productId, boolean liked) {
        WishlistEventRequest request = new WishlistEventRequest();
        request.setEventType(EventTypeId.EVENT_WISHLIST);

        WishlistEventRequest.WishListInfo wishlistInfo = new WishlistEventRequest.WishListInfo();
        request.setWishlistInfo(wishlistInfo);

        wishlistInfo.setOperation(liked ? 1 : 0);
        wishlistInfo.setProductId(productId);
        wishlistInfo.setReferrer(PRODUCT_PAGE);
        wishlistInfo.setProductsQuantity(mPresenter.wishlistCount());

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.WISHLIST_EVENT_REQUEST, request);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, TAG);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);

        DataCollector.logEvent(Events.WishlistEvent, parameters);
    }
}

