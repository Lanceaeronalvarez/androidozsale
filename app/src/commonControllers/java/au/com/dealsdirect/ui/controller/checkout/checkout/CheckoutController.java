package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.stripe.android.model.Card;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaPaymentSummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
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
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.toggleswitch.OurPayToggleSwitch;
import au.com.dealsdirect.ui.custom.transitions.ArcZoomChangeHandler;
import au.com.dealsdirect.ui.main.CardInfo;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
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
import static android.text.Spanned.SPAN_EXCLUSIVE_INCLUSIVE;
import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.KEY_OURPAY_TC_VALIDATION_FAILED;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends VisaCheckoutController implements CheckoutMvpView, FetchTokenHandler, CheckoutListener {
    public static final String CARD_PAYPAL = "Paypal";
    public static final String CARD_MASTERPASS = "Masterpass";
    public static final String CARD_VISA_CHECKOUT = "VisaCheckoutBraintree";
    public static final String CARD_MASTERCARD = "MasterCard";
    public static final String CARD_VISA = "Visa";
    private final int OurPayTCDisabled = 0;
    private final int OurPayTCShowUnchecked = 1;
    private final int OurPayTCShowChecked = 2;

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

    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mPayButton;
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
    @BindView(R.id.partial_checkout_ourpay_panel_holder)
    LinearLayout mOurpayHolder;
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

    private CheckoutHostMvpView mCheckoutHostView;

    private CompositeDisposable mClickListeners;
    private CompositeDisposable mChangeClickListeners;

    private PaymentMethod mLastUserPaymentMethod;
    private boolean mHasSavedInstance = false;
    private HashMap<String, Object> parameters = new HashMap<>();

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
            getRouter().pushController(RouterTransaction.with(new ViewAddressController(true, mDeliveryAddress, false, ""))
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

//        registerClickListeners();
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_checkout, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mVcoPresenter.onAttach(this);

        if (getBoolean(R.bool.is_tablet) && getBoolean(R.bool.master_detail_enabled)) {
            CheckoutHostController existingController = mActivity.getMainController().getCheckoutHostController();
            if (!mHasSavedInstance || existingController == null) {
                mCheckoutHostView = (CheckoutHostMvpView) mActivity.getCheckoutRouter().getControllerWithTag(getString(R.string.checkout_host_controller));
            } else {
                mCheckoutHostView = existingController;
            }
        }
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
            mAdapter.setEligibleProductsLinkListener(locationFilterHash -> mActivity.getHomeController().openLocationFilterHash(locationFilterHash));
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        }

        if(!mActivity.isBraintreeInitialized() && mActivity.isAuthorized()) {
            mVcoPresenter.initializeBraintree();
        }

        if(mVcoPresenter.isVisaCheckoutEnabled() && mActivity.isAuthorized()) {
            if (!mActivity.isBraintreeInitialized()) {
                mVcoPresenter.initializeBraintree();
            }
            mVcoPresenter.setupVisaCheckout(true);
        }

        mVcoButton.setOnClickListener(action -> {
            onVisaCheckoutButtonClicked();
        });
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
                mPresenter.callCartContent();
            }

        } else {
            showNoCartItemsLayout();
        }

        mActivity.getMainController().setViewpagerDraggable(false);
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
            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();

            if (ourpay != null && ourpay.isCanUse()) {

                if (((MainActivity) getActivity()).getMainController().getHomeController().isCheckoutRouterVisible()) {
                    Log.d("ourpay", "checkout controller is visible");
                    boolean isPaymentInvalid = paymentMethod == null ? false : (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_MASTERPASS) ||
                            paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) ||
                            paymentMethod.getPaymentType().equalsIgnoreCase(CARD_VISA_CHECKOUT);
                    OurpayStateManager.setOurpayAccordingToPaymentMethod(ourpay, isPaymentInvalid);
                    PaymentInfo.setOurpay(ourpay);

                    ourpayPanel = new OurpayPanel(mActivity, getRouter());
                    mOurpayHolder.removeAllViews();
                    mOurpayHolder.addView(ourpayPanel.generatePanel(ourpay, isRowVisible -> {
                        if (isRowVisible) {
                            new Handler().postDelayed(() -> mNestedScrollView.fullScroll(View.FOCUS_DOWN), 400);
                        }
                    }));


                    mButtonOurpay = mOurpayHolder.findViewById(R.id.rl_button_ourpay);
                    if (mButtonOurpay != null) {
                        mButtonOurpay.setOnClickListener(view -> onOurpayButtonClick());
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

        if (items.isEmpty()) {
            //no items
            showNoCartItemsLayout();
            CommonUtils.clearSaleItem(mActivity);
        } else {

            if (mAdapter != null) {
                mAdapter.replaceData(items);
            }

            showCartItems();
        }
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
    public void showDeliveryOptions(List<DeliveryOption> deliveryOptions, DeliveryServicePackageDetail deliveryServicePackageDetail) {
        if (getBoolean(R.bool.is_ozsale_app) && (deliveryOptions != null && !deliveryOptions.isEmpty())) {
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

    private void displayDeliveryOptionsUI(String deliveryOptionName, Double deliveryOptionPrice) {
        mDeliveryOptionRootLayout.setOnClickListener(v -> showDeliveryOptionsController());

        String ourpaySelectDescription = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_DESCRIPTION);
        String ourpaySelectBeforePurchaseDesc = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY);
        String freeText = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_FREE);

        if (deliveryOptionName.equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.STANDARD.toString()) ||
                deliveryOptionName.equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.EXPRESS.toString())) {
            mDeliveryOptionTypeText.setVisibility(View.VISIBLE);
            mDeliveryOptionTypeOurPay.setVisibility(View.GONE);

            mDeliveryOptionTypeText.setText(deliveryOptionName);
            mDeliveryOptionPriceTextView.setText(PriceUtils.getPriceStringValue(deliveryOptionPrice));
            mDeliveryOptionTypeText.setTypeface(mDeliveryOptionTypeText.getTypeface(), Typeface.BOLD);
        } else if (deliveryOptionName.equalsIgnoreCase(OurpayTemplateText.DeliveryOptions.OURPAYSELECT.toString())) {

            if (mDeliveryServicePackageDetail != null) {
                String remainingFreeQty = mDeliveryServicePackageDetail.getRemainingCount().toString();
                ourpaySelectBeforePurchaseDesc = ourpaySelectBeforePurchaseDesc.replace(OurpayTemplateText.KEY_DELIVERYOPTION_FREE_DELIVERY_QTY, remainingFreeQty);

                mDeliveryOptionTypeText.setVisibility(View.GONE);
                mDeliveryOptionTypeOurPay.setVisibility(View.VISIBLE);

                if (mDeliveryServicePackageDetail.getPurchased()) {
                    mDeliveryOptionPriceTextView.setText(freeText);
                    mDeliveryOptionOurpaySelectDescriptionTextView.setText(ourpaySelectBeforePurchaseDesc);
                } else {
                    mDeliveryOptionPriceTextView.setText(PriceUtils.getPriceStringValue(mDeliveryServicePackageDetail.getAmount()));
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
                mPresenter.setDeliveryOption(createStandardDeliveryOptionRequest());
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
                    cardBrandImageView.setImageResource(Card.getBrandIcon(Card.CardBrand.AMERICAN_EXPRESS));
                } else {
                    cardBrandImageView.setImageResource(Card.getBrandIcon(Card.asCardBrand(paymentMethod.getPaymentType())));
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
    public void showSummaryDetails(Summary summary) {
        if (summary != null) {
            mSummarySubtotalTextView.setText(PriceUtils.getPriceStringValue(summary.getSubtotal()));

            if (summary.getDelivery() == 0) {
                mSummaryShippingFeeTextView.setVisibility(View.GONE);
                mFreeShippingLayout.setVisibility(View.VISIBLE);
            } else {
                mSummaryShippingFeeTextView.setVisibility(View.VISIBLE);
                mSummaryShippingFeeTextView.setText(PriceUtils.getPriceStringValue(summary.getDelivery()));
                mFreeShippingLayout.setVisibility(View.GONE);
            }

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

            mSummaryTotalTextView.setText(PriceUtils.getPriceStringValue(summary.getTotal()));

            // show ourpay select related summary, should been purchased yet if visible.
            if (isOurPaySelectDeliveryMethod() && !mDeliveryServicePackageDetail.getPurchased()) {
                mSummaryOurpaySelectContainer.setVisibility(View.VISIBLE);
                mSummaryShippingFeeContainer.setVisibility(View.GONE);
                mSummaryOurpaySelectPriceTextView.setText(PriceUtils.getPriceStringValue(summary.getSelect()));
                mSummaryTotalTextView.setText(PriceUtils.getPriceStringValue(summary.getTotalWithSelect()));
            } else {
                mSummaryOurpaySelectContainer.setVisibility(View.GONE);
            }
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

        if (paymentMethod != null &&
                paymentMethod.getProviderType() != null &&
                paymentMethod.getProviderType().equalsIgnoreCase(AppConstants.STRIPE)) {
            mPresenter.setStripePaymentMethodId(paymentMethod.getToken());
        }

        showMyPayDetails(mValue, mOurpay);
        displayPaymentDetails();
    }

    @Override
    public void showAfterpayPanel(boolean isAvailable, String description) {
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
        HomeController homeController = mActivity.getMainController().getHomeController();
        if (mActivity.isAuthorized()) {
            homeController.getPresenter().callGetBasketItemsQuantity();
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

    private void onPayButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.REGULAR);

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.getPaymentMethodSelected() == null) {
            showAddPaymentMethodController();
        } else if (mActivity.getPaymentMethodSelected().getProviderType() != null &&
                    mActivity.getPaymentMethodSelected().getProviderType().equalsIgnoreCase(AppConstants.STRIPE)) {
            if (mPresenter.isStripeEnabled() && mPresenter.getStripePublicKey() != null) {
                if (mActivity.getPaymentMethodSelected().getToken() == null ||
                        mActivity.getPaymentMethodSelected().getToken().isEmpty()) {
                    mActivity.createStripePaymentMethod();
                } else {
                    mActivity.callCreatePaymentTransactionStripe(AppConstants.STRIPE,
                            mActivity.getPaymentMethodSelected().getToken());
                }
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
    }

    private void onPaypalButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.PAYPAL);

        if (!isAddressValid()) {
            //push add new address fragment
            showAddAddressController();
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

        if (!isAddressValid()) {
            showAddAddressController();
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

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
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

        if (mActivity.getHomeController().getPopUpHostRouter() != null) {
            mActivity.getHomeController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private void onAfterpayButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.TYPE_AFTERPAY, mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.AFTERPAY);

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        Bundle bundle = new BundleBuilder(new Bundle())
                .build();

        AfterpayViewController controller = new AfterpayViewController(bundle);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .popChangeHandler(new FadeChangeHandler())
                .pushChangeHandler(new FadeChangeHandler());

        if (mActivity.getHomeController().getPopUpHostRouter() != null) {
            mActivity.getHomeController().getPopUpHostRouter().setRoot(routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    private void onOurpayButtonClick() {
        mPresenter.logInitiateCheckout(mActivity, PaymentInfo.getPaymentType(), mItemList.size(),
                mValue.getSummary().getTotal(), AppConstants.OURPAY);

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            setIsOurPaySelectedDeliveryOption(true);

            if (!isAddressValid()) {

                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                showAddAddressController();

            } else if (mActivity.getPaymentMethodSelected() == null) {

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
                            GateKeeper.setRoot(mActivity.getHomeController().getPopUpHostRouter(), GateKeeper.Destination.POP_UP_HOST, RouterTransaction.with(new PopUpHostController(bundle)).
                                    pushChangeHandler(new FadeChangeHandler()).popChangeHandler(new FadeChangeHandler()));
                        } else {
                            GateKeeper.push(getRouter(), GateKeeper.Destination.SMS_VERIFICATION, bundle,
                                    new HorizontalChangeHandler(false),
                                    new HorizontalChangeHandler());
                        }
                    } else {
                        ourpayPaymentSubmit();
                    }
                }
            }
        }
    }

    private boolean isAddressValid() {
        return mDeliveryAddress != null;
    }

    @Optional
    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {

        mActivity.setShopsAsVisibleContainer();
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

        registerClickListeners();

        if (!mIsCartLoading) {
            loadCart();
        }
    }

    @Override
    public void onViewDidDisappear(Controller nextController) {
        super.onViewDidDisappear(nextController);

        unregisterClickListeners();
    }

    private void registerClickListeners() {
        mClickListeners = new CompositeDisposable();
        mClickListeners.add(RxView.clicks(mPayButton)
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action -> onPayButtonClick()));
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

    private void ourpayPaymentSubmit() {
        PaymentInfo.setFabricPaymentType(PaymentInfo.isThreeDSecureRequired() ?
                DataCollector.EventParameters.PaymentOption.OURPAY3DS.getValue() :
                DataCollector.EventParameters.PaymentOption.OURPAY.getValue());
        PaymentInfo.setPaymentType(PaymentInfo.TYPE_MYPAY);
        mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
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

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
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
    public void showItemDetail(RecyclerView.ViewHolder viewHolder, int position, String seoIdentifierId, String imageUrl,
                               String skuId, String saleId, boolean isFreeDelivery,
                               String itemName, String brandName, String price, String oldPrice,
                               String productID) {

        if (CommonUtils.loadSaleItem(mActivity, productID).isEmpty()) {
            return;
        }

        SaleItemDetailsController.Parameters.FromItemsList parameters = new SaleItemDetailsController
                .Parameters.FromItemsList(position,
                null,
                imageUrl,
                CommonUtils.loadSaleItem(mActivity, productID),
                skuId,
                CommonUtils.loadSaleId(mActivity, productID),
                itemName,
                brandName,
                price,
                oldPrice,
                "",
                "",
                isFreeDelivery,
                false);

        RouterTransaction routerTransaction = RouterTransaction
                .with(SaleItemDetailsController.newInstance(parameters));

        int[] originalPos = new int[2];
        viewHolder.itemView.getLocationOnScreen(originalPos);
        int left = originalPos[0];
        int top = originalPos[1];
        int width = viewHolder.itemView.getWidth();
        int height = viewHolder.itemView.getHeight();
        routerTransaction = routerTransaction
                .pushChangeHandler(new ArcZoomChangeHandler(left, top, width, height))
                .popChangeHandler(new ArcZoomChangeHandler(left, top, width, height));

        getRouter().pushController(routerTransaction);

    }
}

