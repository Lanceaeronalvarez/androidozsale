package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
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
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.mysale.genie.utility.RxBus;
import com.stripe.android.model.CardBrand;
import com.visa.checkout.VisaCheckoutSdk;

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
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.AgeRestrictionOperationType;
import au.com.dealsdirect.service.datacollection.enums.CheckoutUserActivityOperationType;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.afterpay.AfterpayViewController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.MappedShipment;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostMvpView;
import au.com.dealsdirect.ui.controller.checkout.deliveryoptions.DeliveryOptionsController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.floatingimageviewer.FloatingImageViewerController;
import au.com.dealsdirect.ui.controller.klarna.KlarnaViewController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.lpay.LPayViewController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.toggleswitch.OurPayToggleSwitch;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static android.graphics.Typeface.BOLD;
import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.KEY_OURPAY_TC_VALIDATION_FAILED;
import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.getTemplateText;

public class CheckoutController extends VisaCheckoutController implements CheckoutMvpView, FetchTokenHandler, CheckoutListener {
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
    @Nullable
    @BindView(R.id.partial_checkout_summary_ourpay_select_price)
    TextView mSummaryOurpaySelectPriceTextView;
    @Nullable
    @BindView(R.id.partial_checkout_summary_ourpay_select_container)
    ViewGroup mSummaryOurpaySelectContainer;

    @BindView(R.id.partial_checkout_summary_voucher_container)
    ViewGroup mVoucherValueContainer;
    @BindView(R.id.partial_checkout_voucher_value_text_view)
    TextView mVoucherValueTextView;
    @Nullable
    @BindView(R.id.partial_checkout_voucher_promo_code_text_view)
    TextView mVoucherPromoCodeTextView;

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
    @BindView(R.id.partial_checkout_ourpay_panel_holder)
    LinearLayout mOurpayHolder;
    @BindView(R.id.partial_checkout_klarna_container)
    ViewGroup mKlarnaContainer;
    @BindView(R.id.partial_checkout_klarna_description)
    WebView mKlarnaDescriptionView;
    @BindView(R.id.partial_checkout_button_klarna)
    View mKlarnaButton;
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

    //DELIVERY OPTIONS UI
    @Nullable
    @BindView(R.id.delivery_option_root_layout)
    ViewGroup mDeliveryOptionRootLayout;
    @Nullable
    @BindView(R.id.delivery_option_non_ourpay_text_view)
    TextView mDeliveryOptionTypeText;
    @Nullable
    @BindView(R.id.delivery_option_ourpay_select_container)
    ViewGroup mDeliveryOptionTypeOurPay;
    @Nullable
    @BindView(R.id.delivery_option_price_text_view)
    TextView mDeliveryOptionPriceTextView;
    @Nullable
    @BindView(R.id.delivery_option_ourpay_select_description_text_view)
    TextView mDeliveryOptionOurpaySelectDescriptionTextView;
    @BindView(R.id.partial_checkout_summary_shipping_with_icon)
    RelativeLayout mFreeShippingLayout;

    @BindView(R.id.button_visa_checkout)
    Button mVcoButton;

    private RelativeLayout mButtonOurpay;
    private OurPayToggleSwitch mCheckBoxOurpayTC;

    private List<DeliveryOption> mDeliveryOptions;
    private DeliveryOption mSelectedDeliveryOption;
    private boolean mIsOurPaySelectDeliveryOption;
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;

    private List<MappedShipment> mItemList = new ArrayList<>();
    private ArrayList<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private ArrayList<DecorationInfoList> mDecorationInfoList = new ArrayList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();
    private CheckoutOrderAdapter mAdapter;
    private Ourpay mOurpay;

    private boolean mIsCartLoading = false;
    private boolean mIsVoucherAdded = false;
    private boolean mIsPaymentMethodChanged = false;
    private String mAddressPhoneNumber;
    private Double mDiscountValue;

    private CheckoutDetailsMapper mValue;

    private OurpayPanel ourpayPanel;

    private CheckoutHostMvpView mCheckoutHostView = null;

    private CompositeDisposable mClickListeners;
    private CompositeDisposable mChangeClickListeners;

    private PaymentMethod mLastUserPaymentMethod;
    private boolean mHasSavedInstance = false;
    private HashMap<String, Object> parameters = new HashMap<>();
    private boolean isStripe;

    private boolean isShipmentAvailable = true;

    private boolean hasAgeRestriction = false;
    private Calendar birthday = null;

    private boolean isGPayAvailable = false;

    public static CheckoutController newInstance() {
        return new CheckoutController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public CheckoutController(Bundle args) {
        super(args);
    }

    private void changeAddress() {
        if (mDeliveryAddress == null) {
            showAddAddressController();
            getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.ADD_ADDRESS);
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
                getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.ADD_PAYMENT_METHOD);
            }
        }
    }


    private void changeVoucher() {

        boolean isNoDiscount = true;
        if (mDiscountValue != 0) {
            isNoDiscount = false;
        }


        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.VOUCHERS, new Gson().toJson(mVouchers))
                .putBoolean(BundleKeys.IS_VOUCHER_ADDED, mIsVoucherAdded)
                .putBoolean(BundleKeys.IS_CART_NO_DISCOUNT, isNoDiscount)
                .build();

        getRouter().pushController(RouterTransaction.with(new AddVouchersController(bundle))
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
        mVcoPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        //disable toolbar left and right buttons
        mToolbarLeftButton.setVisibility(View.INVISIBLE);
        mToolbarRightButton.setVisibility(View.INVISIBLE);

        if (mActivity != null) {
            mActivity.performResetWithAuthFetch();
        }

        setUp(view);
    }


    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);

//        unregisterClickListeners();
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        showMyPayDetails(mValue, mOurpay);
    }

    @Override
    protected void setUp(View view) {
        if (mActivity != null) {
            mActivity.getMainController().showBottomNav();
        }

        mTitleTextView.setText(R.string.checkout_page_toolbar_title);

        mActivity.setCheckoutController(this);

        if (!mPresenter.isTablet() || !getBoolean(R.bool.master_detail_enabled)) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new CheckoutOrderAdapter(mActivity, mItemList, mPresenter, this);
            mAdapter.setEligibleProductsLinkListener(locationFilterHash -> mActivity.getMainController().openLocationFilterHash(locationFilterHash));
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        }

        if (!mActivity.isBraintreeInitialized() && mActivity.isAuthorized()) {
            mVcoPresenter.initializeBraintree();
        }

        if (mVcoPresenter.isVisaCheckoutEnabled() && mActivity.isAuthorized()) {
            if (!mActivity.isBraintreeInitialized()) {
                mVcoPresenter.initializeBraintree();
            }
            mVcoPresenter.setupVisaCheckout(true);
        }

        mVcoButton.setOnClickListener(action -> {
            onVisaCheckoutButtonClicked();
        });

        final boolean isGenoaPay = Settings.getSelectedCountry().countryId.equalsIgnoreCase("NZ");
        final int padding = (int) mActivity.getResources().getDimension(isGenoaPay ? R.dimen.genoa_button_logo_margin : R.dimen.lpay_button_logo_margin);
        mLPayButton.setBackgroundResource(isGenoaPay ? R.drawable.bg_genoapay_button : R.drawable.bg_lpay_button);
        mLPayButtonLogoImageView.setImageResource(isGenoaPay ? R.drawable.genoapay_logo_white : R.drawable.lpay_logo_white);
        mLPayButtonLogoImageView.setPadding(padding, padding, padding, padding);

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

        if (requestCode == BraintreeRequestCodes.VISA_CHECKOUT) {
            showLoading();
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
        }
    }

    @Override
    public void loadCart() {
        if (mPresenter == null || mActivity == null) return;

        if (mPresenter.checkIsLoggedIn()) {
            RxBus.instance().post(IntrospectionUtils.EVENT_CHECKOUT_SCREEN);

            if (!mActivity.isBraintreeInitialized()) {
                mActivity.fetchAuthorization(this);
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
    public boolean isCartLoading() {
        return mIsCartLoading;
    }

    @Override
    public void setCartIsLoading(boolean val) {
        this.mIsCartLoading = val;
    }


    @Override
    public void showMyPayDetails(CheckoutDetailsMapper mappedValues, Ourpay ourpay) {

        if (mappedValues != null) {
            mOurpay = ourpay;
            final PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();

            if (ourpay != null && ourpay.isCanUse()) {

                if (((MainActivity) getActivity()).getMainController().isCheckoutPageVisible()) {
                    Log.d("ourpay", "checkout controller is visible");
                    final boolean isPaymentInvalid = paymentMethod != null &&
                            ((paymentMethod.getPaymentType().equalsIgnoreCase(CARD_MASTERPASS) ||
                                    paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) ||
                                    paymentMethod.getPaymentType().equalsIgnoreCase(CARD_VISA_CHECKOUT));
                    OurpayStateManager.setOurpayAccordingToPaymentMethod(ourpay, isPaymentInvalid);
                    PaymentInfo.setOurpay(ourpay);

                    ourpayPanel = new OurpayPanel(mActivity, getRouter());
                    ourpayPanel.setUnavailableNoticeText(mPresenter.getTemplateTextsRepository().getOurpayUnavailableText());
                    mOurpayHolder.removeAllViews();
                    mOurpayHolder.addView(ourpayPanel.generatePanel(ourpay, isRowVisible -> {
                        if (isRowVisible) {
                            new Handler().postDelayed(() -> mNestedScrollView.fullScroll(View.FOCUS_DOWN), 400);
                        }
                    }));


                    mButtonOurpay = mOurpayHolder.findViewById(R.id.rl_button_ourpay);
                    if (mButtonOurpay != null) {
                        isStripe = paymentMethod != null && paymentMethod.getProviderType().equalsIgnoreCase(AppConstants.STRIPE);
                        if (ourpay.getMode() == Ourpay.OurpayMode.NORMAL) {
                            mButtonOurpay.setOnClickListener(view -> onOurpayButtonClick(isStripe));
                        } else {
                            mButtonOurpay.setOnClickListener(view -> onOffloadOurpayButtonClick());
                        }
                    }


                    mCheckBoxOurpayTC = mOurpayHolder.findViewById(R.id.ourpay_toggle_switch_tc);
                    if (mCheckBoxOurpayTC != null) {
                        OurpayPanel.TermsAndConditionStates termsAndConditionStatesState = OurpayPanel.TermsAndConditionStates.values()[ourpay.getTermsAndConditionsCheckboxState()];
                        mCheckBoxOurpayTC.setOurPayToggleSwitch(termsAndConditionStatesState);

                    }
                    if (isOurPaySelectDeliveryMethod() && mDeliveryServicePackageDetail != null) { // show ourpay select related summary
                        mSummaryOurpaySelectPriceTextView.setText(PriceUtils.getPriceStringValue(mDeliveryServicePackageDetail.getAmount()));
                    }

                } else {
                    //checkout controller not visible
                    Log.d("ourpay", "checkout controller is not visible");

                }

            } else {
                removeOurpayView();
                Log.d(CheckoutController.class.getName(), "mypay disabled");
            }
        }
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
        mAdapter.replaceData(mItemList, showFooter);
    }

    @Override
    public void showCartDetailsOnHost(List<MappedShipment> items) {
        if (getCheckoutHostView() != null) {
            getCheckoutHostView().showCartDetails(items);
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
            mAddressPhoneNumber = deliveryAddress.phone;

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
        if (getCheckoutHostView() != null) {
            getCheckoutHostView().showCartDetailsFooter(show);
        }
        refreshItemList(show);
    }

    @Override
    public void showCartDetailsPostcode(String postcode) {
        if (getCheckoutHostView() != null) {
            getCheckoutHostView().showCartDetailsPostcode(postcode);
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
                    String ourpayOption = OurpayTemplateText.DeliveryOptions.OURPAYSELECT.toString();
                    setIsOurPaySelectedDeliveryOption(mSelectedDeliveryOption.getDeliveryOptions()
                            .get(0).equalsIgnoreCase(ourpayOption));
                    deliveryOptionName = option.getDeliveryOptions().get(0); //get name
                    deliveryOptionPrice = option.getPrice();
                    break;
                }
            }

            //need to invalidate paypal/masterpass if ourpayselect delivery method is chosen;
            //mIsPaymentMethodChanged is set to true from PaymentSelectController or AddPaymentController if they choose
            //or add a payment method. It is then set to false when going back to those screens from CheckoutController
            if (isOurPaySelectDeliveryMethod() && mIsPaymentMethodChanged && checkPaymentMethodValidForOurPaySelect()) {
                return;
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

    private CheckoutHostMvpView getCheckoutHostView() {
        if (mCheckoutHostView == null) {
            if (getBoolean(R.bool.is_tablet) && getBoolean(R.bool.master_detail_enabled)) {
                CheckoutHostController existingController = mActivity.getMainController().getCheckoutHostController();
                if (!mHasSavedInstance || existingController == null) {
                    if (mActivity.getCheckoutRouter() != null) {
                        mCheckoutHostView = (CheckoutHostMvpView) mActivity.getCheckoutRouter()
                                .getControllerWithTag(CheckoutHostController.class.getName());
                    }
                } else {
                    mCheckoutHostView = existingController;
                }
            }
        }

        return mCheckoutHostView;
    }

    private void displayDeliveryOptionsUI(String deliveryOptionName, Double deliveryOptionPrice) {
        mDeliveryOptionRootLayout.setOnClickListener(v -> showDeliveryOptionsController());

        String ourpaySelectDescription = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_DESCRIPTION);
        String ourpaySelectBeforePurchaseDesc = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY);
        String freeText = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_FREE);

        String priceText = null;
        if (isAddressValid() && (!mPresenter.isShippingByPostcodeEnabled() || isShipmentAvailable) && deliveryOptionPrice != null) {
            priceText = deliveryOptionPrice > 0 ? PriceUtils.getPriceStringValue(deliveryOptionPrice) : mActivity.getResources().getString(R.string.free_text);
        }

        if (deliveryOptionName.equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.STANDARD.toString()) ||
                deliveryOptionName.equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.EXPRESS.toString())) {
            mDeliveryOptionTypeText.setVisibility(View.VISIBLE);
            mDeliveryOptionTypeOurPay.setVisibility(View.GONE);

            mDeliveryOptionTypeText.setText(deliveryOptionName);
            mDeliveryOptionPriceTextView.setText(priceText);
            mDeliveryOptionTypeText.setTypeface(mDeliveryOptionTypeText.getTypeface(), Typeface.BOLD);
        } else if (deliveryOptionName.equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.OURPAYSELECT.toString())) {

            if (mDeliveryServicePackageDetail != null) {
                String remainingFreeQty = mDeliveryServicePackageDetail.getRemainingCount().toString();
                ourpaySelectBeforePurchaseDesc = ourpaySelectBeforePurchaseDesc.replace(OurpayTemplateText.KEY_DELIVERYOPTION_FREE_DELIVERY_QTY, remainingFreeQty);

                mDeliveryOptionTypeText.setVisibility(View.GONE);
                mDeliveryOptionTypeOurPay.setVisibility(View.VISIBLE);

                if (mDeliveryServicePackageDetail.getPurchased()) {
                    mDeliveryOptionPriceTextView.setText(priceText != null ? freeText : null);
                    mDeliveryOptionOurpaySelectDescriptionTextView.setText(ourpaySelectBeforePurchaseDesc);
                } else {
                    mDeliveryOptionPriceTextView.setText(priceText);
                    mDeliveryOptionOurpaySelectDescriptionTextView.setText(ourpaySelectDescription);
                }
            }
        }
    }

    private boolean checkPaymentMethodValidForOurPaySelect() {

        PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
        if (paymentMethod != null) {
            if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_MASTERPASS)) {
                selectStandardDeliveryOption();
                String postcode = mDeliveryAddress != null ? mDeliveryAddress.getPostcode() : null;
                mPresenter.setDeliveryOption(createStandardDeliveryOptionRequest(), postcode);
                return true;
            }
        }

        return false;

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
        PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
        if (paymentMethod != null) {

            if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) {
                mMasterpassButton.setVisibility(View.GONE);
                mVcoButton.setVisibility(View.GONE);
                mPayButton.setVisibility(View.GONE);
                mPaypalButton.setVisibility(View.VISIBLE);
                mPaypalCreditButton.setVisibility(View.GONE);
            } else {
                showPaymentButtons();
                mPaypalButton.setVisibility(View.GONE);
                mPaypalCreditButton.setVisibility(View.GONE);
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
            setupSummaryShipping(summary);
            setupSummaryVouchers(summary);

            if (mDeliveryAddress == null) {
                mSummaryTotalTextView.setVisibility(View.GONE);
            } else {
                mSummaryTotalTextView.setVisibility(View.VISIBLE);
                mSummaryTotalTextView.setText(PriceUtils.getPriceStringValue(summary.getTotal()));
            }

            // show ourpay select related summary, should been purchased yet if visible.
            if (isOurPaySelectDeliveryMethod() && !mDeliveryServicePackageDetail.getPurchased()) {
                mSummaryOurpaySelectContainer.setVisibility(View.VISIBLE);
                mSummaryOurpaySelectPriceTextView.setText(PriceUtils.getPriceStringValue(summary.getSelect()));
                mSummaryTotalTextView.setText(PriceUtils.getPriceStringValue(summary.getTotalWithSelect()));
            } else {
                mSummaryOurpaySelectContainer.setVisibility(View.GONE);
            }
        }

    }

    private void setupSummaryShipping(Summary summary) {
        if (isOurPaySelectDeliveryMethod() && !mDeliveryServicePackageDetail.getPurchased()) {
            mSummaryShippingFeeContainer.setVisibility(View.GONE);
        } else {
            mSummaryShippingFeeContainer.setVisibility(View.VISIBLE);
        }

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
            mIsVoucherAdded = true;
            mVoucherValueContainer.setVisibility(View.VISIBLE);
            mVoucherValueTextView.setVisibility(View.VISIBLE);
            mVoucherValueTextView.setText(PriceUtils.getPriceStringValue(summary.getDiscount()) + " " + getString(R.string.voucher));
        } else {
            mIsVoucherAdded = false;
            mVoucherValueTextView.setVisibility(View.GONE);
            mVoucherValueContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        mPaymentList.clear();
        mPaymentList.addAll(paymentList);

        PaymentMethod paymentMethod = getCompatiblePaymentType(mLastUserPaymentMethod,
                mActivity.getPaymentMethodSelected(),
                paymentList,
                getSelectedDeliveryOption());
        mActivity.setPaymentMethodSelected(paymentMethod);

        showMyPayDetails(mValue, mOurpay);
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

    private void onKlarnaButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.TYPE_KLARNA, mItemList.size(),
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
                                                   List<PaymentMethod> paymentMethodList,
                                                   String deliveryOption) {

        ArrayList<PaymentMethod> defaultPayments = new ArrayList();
        if (selectedPaymentMethod != null) defaultPayments.add(selectedPaymentMethod);
        if (lastPaymentMethod != null) defaultPayments.add(lastPaymentMethod);

        switch (deliveryOption) {

            case DeliveryOption.DELIVERY_OPTION_OURPAY_SELECT:

                ArrayList<PaymentMethod> availablePaymentMethods = defaultPayments;
                defaultPayments.addAll(paymentMethodList);

                for (PaymentMethod method : availablePaymentMethods) {
                    if (method.canUseOurPaySelect()) return method;
                }
                break;

            default:
                return defaultPayments.size() > 0 ? defaultPayments.get(0) : null;
        }
        return null;
    }

    @Override
    public void storeCartDetails(CheckoutDetailsMapper mappedValues) {
        //Set 3DS value
        if (mappedValues != null) {
            PaymentInfo.setThreeDSecureRequired(mappedValues.getThreeDSecureRequired());
            PaymentInfo.setCartCost(mappedValues.getSummary().getTotal());
        }

        mValue = mappedValues;
        mPresenter.generateOurpay(mValue);
    }

    @Override
    public void triggerLoginTicket() {
        assert (mActivity) != null;
        mActivity.callLoginTicket(false);
    }

    @Override
    public void updateCheckoutBadge() {
        if (mActivity.isAuthorized()) {
            mActivity.getMainController().updateBasketItemsQuantity();
        }
    }


    @Override
    public CheckoutMvpPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public boolean isOurPaySelectDeliveryMethod() {
        return mIsOurPaySelectDeliveryOption;
    }

    @Override
    public boolean setIsPaymentMethodChanged(boolean isPaymentMethodChanged) {
        return mIsPaymentMethodChanged = isPaymentMethodChanged;
    }

    @Override
    public void showPromoCodeApplied(String promoCode, boolean isPromoCodeApplied) {
        mVoucherPromoCodeTextView.setText(promoCode);
        mVoucherPromoCodeTextView.setVisibility(isPromoCodeApplied ? View.VISIBLE : View.GONE);
    }

    @Override
    public Router getDisplayRouter() {
        return getRouter();
    }

    @Override
    public void initializeVisaCheckout() {
        if (mVcoPresenter != null && isViewAttached()) {
            if (mVcoPresenter.isVisaCheckoutEnabled()) {
                mVcoPresenter.setupVisaCheckout(true);
            }
        }
    }

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
        String paymentLogType = AppConstants.REGULAR;

        if (!commonPaymentAbilityDetermination()) {
            mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                    mValue.getSummary().getTotal(), paymentLogType);
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.getPaymentMethodSelected() == null) {
            showAddPaymentMethodController();
        } else if (mActivity.getPaymentMethodSelected().getProviderType() != null &&
                mActivity.getPaymentMethodSelected().getProviderType().equalsIgnoreCase(AppConstants.STRIPE)) {
            if (mPresenter.isStripeEnabled() && mPresenter.getStripePublicKey() != null) {
                mActivity.callCreatePaymentTransactionStripe(AppConstants.STRIPE,
                        mActivity.getPaymentMethodSelected().getToken());
                paymentLogType = AppConstants.STRIPE;
            } else {
                CustomAlertDialog.showCustomAlertDialog(
                        mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        mActivity.getResources().getString(R.string.stripe_error_occured));
            }
        } else {
            if (mActivity.isBraintreeInitialized()) {
                if (mActivity.getPaymentMethodSelected() == null) {
                    showAddPaymentMethodController();
                } else {
                    PaymentInfo.setFabricPaymentType(PaymentInfo.isThreeDSecureRequired() ?
                            DataCollector.EventParameters.PaymentOption.THREEDS.getValue() :
                            DataCollector.EventParameters.PaymentOption.REGULAR.getValue());
                    PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                    mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
                }
            }
        }

        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), paymentLogType);
    }

    private void onGPayButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.TYPE_GPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.GPAY);

        MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return;
        }
        activity.payWithGoogle(mValue.getSummary().getTotal());
    }

    private void onPaypalButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.PAYPAL);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.PAYPAL);
        PaymentInfo.setFabricPaymentType(DataCollector.EventParameters.PaymentOption.PAYPAL.getValue());
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            //If no selected payment method displayed, call paypal
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalPayment();
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
            }
        }
    }

    private void onPaypalCreditButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.PAYPALCREDIT);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.PAYPAL);
        PaymentInfo.setFabricPaymentType(DataCollector.EventParameters.PaymentOption.PAYPAL.getValue());
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalCreditPayment(String.valueOf(mValue.getSummary().getTotal()));
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
            }
        }

    }

    private void onMasterpassButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.MASTERPASS);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.MASTERPASS);
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);
        PaymentInfo.setFabricPaymentType(DataCollector.EventParameters.PaymentOption.MASTERPASS.getValue());
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
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.TYPE_AFTERPAY, mItemList.size(),
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
                mPresenter.logFailedTransaction(mActivity, errorMessage);
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
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.TYPE_LPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.LPAY);

        mPresenter.logCommonCheckoutEvent(mActivity, CheckoutUserActivityOperationType.LPAY_BUTTON_CLICK.getValue());

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
                mPresenter.logCommonCheckoutEvent(mActivity, CheckoutUserActivityOperationType.LPAY_BUTTON_CREATE_ORDER.getValue());
            }

            @Override
            public void onCreateCharge() {
                mPresenter.logCommonCheckoutEvent(mActivity, CheckoutUserActivityOperationType.LPAY_BUTTON_CREATE_CHARGE.getValue());
            }

            @Override
            public void onError(String errorMessage) {
                mPresenter.logFailedTransaction(mActivity, errorMessage);
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

    private void onOurpayButtonClick(boolean isStripeOption) {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.OURPAY);

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        setIsOurPaySelectedDeliveryOption(true);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        if (mActivity.getPaymentMethodSelected() == null) {

            showAddPaymentMethodController();

        } else {

            if (!mActivity.getPaymentMethodSelected().getPaymentType().equalsIgnoreCase(CARD_PAYPAL) && PaymentInfo.getOurpay().isCanUse()) {

                if (mCheckBoxOurpayTC != null && mCheckBoxOurpayTC.getCheckedTogglePosition() != 0) {
                    CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, OurpayTemplateText.getText(mActivity, KEY_OURPAY_TC_VALIDATION_FAILED));
                    return;
                }

                if (PaymentInfo.getOurpay().isPhoneVerificationRequired()) {
                    Bundle bundle = new BundleBuilder(new Bundle())
                            .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.SMS_VERIFICATION)
                            .putString(BundleKeys.PHONE_KEY, mAddressPhoneNumber)
                            .build();

                    getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.OURPAY_PHONE_VERIFIATION);

                    if (mPresenter.isTablet() && getBoolean(R.bool.is_ozsale_app)) {
                        GateKeeper.setRoot(mActivity.getMainController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                                pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
                    } else {
                        GateKeeper.push(getRouter(), GateKeeper.Destination.SMS_VERIFICATION, bundle,
                                new HorizontalChangeHandler(false),
                                new HorizontalChangeHandler());
                    }
                } else {
                    ourpayPaymentSubmit(isStripeOption);
                }
            }
        }
    }

    private void onOffloadOurpayButtonClick() {
        mActivity.showOffloadOurpayDialog(null,
                mKlarnaButton.getVisibility() == View.VISIBLE ?
                        v -> onKlarnaButtonClick() : null,
                v -> scrollToTopOfPayment());
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
        List<View> buttons = new ArrayList<View>(){{
            add(mGPayButton);
            add(mPayButton);
            add(mPaypalButton);
            add(mVcoButton);
            add(mAfterpayButton);
            add(mKlarnaButton);
            add(mLPayButton);
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
        }
        mCheckoutContainer.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void showCartItems() {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, CheckoutController.class.getSimpleName());
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
        if (mActivity.getPaymentMethodSelected() != null) {
            String paymentType = mActivity.getPaymentMethodSelected().getPaymentType();
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
            if (!isOurPaySelectDeliveryMethod() && mPresenter.isMasterPassEnabled()) {
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
    public void onSuccess() {
        if (!isAttached()) return;
        //loadCartContent(); //Do we have to reload cart on bt token fetch?
    }

    @Override
    public void onFailure() {
        if (!isAttached()) return;
        hidePaymentButtons();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (!mIsCartLoading) {
            loadCart();
        }
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        unregisterClickListeners();
        registerClickListeners();

        if (!mIsCartLoading && !(previousController instanceof ViewAddressController) &&
                (previousController != null || mPresenter.checkIsLoggedIn())) {
            loadCart();
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

    private void ourpayPaymentSubmit(boolean isStripeOption) {
        PaymentInfo.setFabricPaymentType(PaymentInfo.isThreeDSecureRequired() ?
                DataCollector.EventParameters.PaymentOption.OURPAY3DS.getValue() :
                DataCollector.EventParameters.PaymentOption.OURPAY.getValue());
        PaymentInfo.setPaymentType(PaymentInfo.TYPE_MYPAY);

        if (isStripeOption) {
            PaymentInfo.setProvider(AppConstants.STRIPE);
            mActivity.callCreatePaymentTransactionStripe(PaymentInfo.getPaymentType(), PaymentInfo.getPaymentMethod().getToken());
        } else {
            PaymentInfo.setProvider(PaymentInfo.TYPE_BRAINTREE);
            if (mActivity.isBraintreeInitialized()) {
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
            }
        }

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
        bundle.putBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, isOurPaySelectDeliveryMethod());
        bundle.putString(BundleKeys.CART_TOTAL_COST, Double.toString(mValue.getSummary().getTotal()));
        if (mValue != null) {
            mValue.putInBundle(bundle, BundleKeys.CURRENT_ORDER_VALUE);
        }

        getRouter().pushController(RouterTransaction.with(new PaymentSelectController(bundle))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void showAddPaymentMethodController() {
        mIsPaymentMethodChanged = false;

        AddPaymentController.Parameters.FromCheckout parameters = new AddPaymentController
                .Parameters.FromCheckout(isOurPaySelectDeliveryMethod(),
                Double.toString(mValue.getSummary().getTotal()),
                mValue);

        getRouter().pushController(RouterTransaction
                .with(AddPaymentController.newInstance(parameters))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public void removeOurpayView() {
        if (mOurpayHolder != null)
            mOurpayHolder.removeAllViews();
    }

    @Override
    public void onVisaCheckoutButtonClicked() {

        if (isProcessingVco) {
            isProcessingVco = false;
        }

        double total = mValue == null ? 0.0 : mValue.getSummary().getTotal();

        mPresenter.logInitiateCheckout(
                mActivity,
                PaymentInfo.VISA_CHECKOUT_BRAINTREE,
                mItemList.size(),
                total,
                AppConstants.VCO);

        if (!commonPaymentAbilityDetermination()) {
            return;
        }

        getPresenter().setLastCartRedirection(DataCollector.EventParameters.LastRedirection.VISACHECKOUT);
        PaymentInfo.setFabricPaymentType(DataCollector.EventParameters.PaymentOption.VCO.getValue());
        mVcoPresenter.payWithVisaCheckout(mValue.getSummary().getTotal());
    }

    private void selectStandardDeliveryOption() {
        for (DeliveryOption option : mDeliveryOptions) {
            boolean isSelected = option.getDeliveryOptions().get(0).equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.STANDARD.toString());
            option.setSelected(isSelected);
        }
    }

    private SetDeliveryOption.OptionParameters createStandardDeliveryOptionRequest() {
        DeliveryOption standardDeliveryOption = null;

        for (DeliveryOption option : mDeliveryOptions) {
            if (option.getDeliveryOptions().get(0).equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.STANDARD.toString())) {
                standardDeliveryOption = option;
                break;
            }
        }

        if (standardDeliveryOption == null) {
            return null;
        }

        standardDeliveryOption.setName(mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_STANDARD_TITLE));

        SetDeliveryOption.OptionParameters optionParameters
                = new SetDeliveryOption.OptionParameters(mDeliveryAddress != null ? mDeliveryAddress.id : "", "",
                new Gson().toJson(standardDeliveryOption), "");

        return optionParameters;

    }

    private void setIsOurPaySelectedDeliveryOption(boolean isOurPaySelected) {
        mIsOurPaySelectDeliveryOption = isOurPaySelected;
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

                        mPresenter.logCommonCheckoutEvent(mActivity, isAgeValid() ? AgeRestrictionOperationType.VALID.getValue() : AgeRestrictionOperationType.NOTVALID.getValue());

                        mAgeRestrictionNotice.setVisibility(isAgeValid() ? View.GONE : View.VISIBLE);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
            mPresenter.logCommonCheckoutEvent(mActivity, AgeRestrictionOperationType.OPEN.getValue());
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
    public void updateCartWithValue(Value value) {
        CheckoutDetailsMapper mappedValues = new CheckoutDetailsMapper(value);
        mPresenter.updateCartValues(mappedValues);
        showAgeRestriction(mappedValues.isAgeRestricted() == null ? false : mappedValues.isAgeRestricted());
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
}

