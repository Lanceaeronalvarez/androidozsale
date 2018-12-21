package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.api.models.PayPalRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.mysale.genie.utility.RxBus;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaPaymentSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.service.event.ActionTracker;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostController;
import au.com.dealsdirect.ui.controller.checkout.checkouthost.CheckoutHostMvpView;
import au.com.dealsdirect.ui.controller.checkout.deliveryoptions.DeliveryOptionsController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.toggleswitch.OurPayToggleSwitch;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.KEY_OURPAY_TC_VALIDATION_FAILED;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends VisaCheckoutController implements CheckoutMvpView, FetchTokenHandler {
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
    @BindView(R.id.partial_checkout_summary_pay_today_price)
    TextView mSummaryPayTodayTextView;
    @Nullable
    @BindView(R.id.partial_checkout_summary_pay_today_container)
    ViewGroup mSummaryPayTodayContainer;
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

    private RelativeLayout mButtonOurpay;
    private OurPayToggleSwitch mCheckBoxOurpayTC;

    private List<DeliveryOption> mDeliveryOptions;
    private DeliveryOption mSelectedDeliveryOption;
    private boolean mIsOurPaySelectDeliveryOption;
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;

    private List<Item> mItemList = new ArrayList<>();
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

    private Value mValue;

    private OurpayPanel ourpayPanel;

    private CheckoutHostMvpView mCheckoutHostView;

    private CompositeDisposable mClickListeners;
    private CompositeDisposable mChangeClickListeners;

    private PaymentMethod mLastUserPaymentMethod;
    private boolean mHasSavedInstance = false;

    private ControllerChangeHandler.ControllerChangeListener newControllerChangeHandler;

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
            getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.ADD_ADDRESS);
        } else {
            getRouter().pushController(RouterTransaction.with(new ViewAddressController(true, mDeliveryAddress))
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
                getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.ADD_PAYMENT_METHOD);
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
        if (mClickListeners != null) {
            mClickListeners.dispose();
        }
        mClickListeners = null;

        if (mChangeClickListeners != null) {
            mChangeClickListeners.dispose();
        }
        mChangeClickListeners = null;
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        if (newControllerChangeHandler != null) {
            getRouter().removeChangeListener(newControllerChangeHandler);
            newControllerChangeHandler = null;
        }
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
            mAdapter = new CheckoutOrderAdapter(mActivity, mItemList, mPresenter);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        }

        //Code for returning to checkout, call reload
        CheckoutController currentController = this;
        newControllerChangeHandler = new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to == currentController && (mActivity != null && mActivity.isAuthorized())) {
                    loadCart();
                }
            }
        };
        getRouter().addChangeListener(newControllerChangeHandler);

        if (mVcoPresenter.isVisaCheckoutEnabled()) {
            mVcoPresenter.setupVisaCheckout();
        }
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
                        VisaPaymentSummary visaPaymentSummary = data.getParcelableExtra(VisaCheckoutSdk.INTENT_PAYMENT_SUMMARY);
                        if (visaPaymentSummary != null) {
                            // Successful VCO
                            showLoading();
                            mActivity.callCreatePaymentTransactionVco(visaPaymentSummary);
                            mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().getTotal());
                        }
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
    public void showMyPayDetails(Value value, Ourpay ourpay) {

        if (value != null) {
            mOurpay = ourpay;
            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
            boolean isMyPayEnabled = mActivity.getIsMyPayEnabled();

            if (ourpay != null && isMyPayEnabled && ourpay.isCanUse()) {

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
                    if (isOurPaySelectDeliveryMethod()) { // show ourpay select related summary
                        mSummaryOurpaySelectPriceTextView.setText(PriceUtils.getPriceStringValue(mDeliveryServicePackageDetail.getAmount()));
                        mSummaryPayTodayTextView.setText(PriceUtils.getPriceStringValue(ourpay.getInitialAmount()));
                    }

                } else {
                    //checkout controller not visible
                    Log.d("ourpay", "checkout controller is not visible");

                }

            } else {
                Log.d(CheckoutController.class.getName(), "mypay disabled");
            }
        }
    }

    @Override
    public void showCartDetails(List<Item> items) {

        showCartDetailsOnHost(items);

        showCartDetailsOnChild(items);
    }

    @Override
    public void showCartDetailsOnChild(List<Item> items) {
        if (items == null) { //do nothing (ie. when increasing order quantity, returns a soldout/out of stock message)
            return;
        }

        mItemList = items;

        if (items.isEmpty()) {
            //no items
            showNoCartItemsLayout();
        } else {

            if (mAdapter != null) {
                mAdapter.replaceData(items);
            }

            showCartItems();
        }
    }

    @Override
    public void showCartDetailsOnHost(List<Item> items) {
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
            if (mDeliveryOptionRootLayout != null) { mDeliveryOptionRootLayout.setVisibility(View.GONE); }
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
                mVisaCheckoutButton.setVisibility(View.GONE);
                mPayButton.setVisibility(View.GONE);
                mPaypalButton.setVisibility(View.VISIBLE);
                mPaypalCreditButton.setVisibility(View.GONE);
            } else {
                showPaymentButtons();
                mPaypalButton.setVisibility(View.GONE);
                mPaypalCreditButton.setVisibility(View.GONE);
            }


            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).setText(paymentMethod.getPaymentType());
            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_details)).setText(paymentMethod.getDescription());

//            Hardcoded visa checkout logo if visa checkout is payment type. this is due to api not wanting to update their response LOL.
            String visaCheckoutLogoUrl = "https://assets.secure.checkout.visa.com/VCO/images/acc_40x30_wht01.png";
            String paymentMethodImageUrl = paymentMethod.getImageUrl();
            if (paymentMethod.getPaymentType().equalsIgnoreCase("VisaCheckoutBraintree") || paymentMethod.getPaymentType().equalsIgnoreCase("VisaCheckoutCyberSource")) {
                paymentMethodImageUrl = visaCheckoutLogoUrl;
            }
            ImageUtils.loadImage(paymentMethodImageUrl,
                    mPaymentLayout.findViewById(R.id.partial_checkout_payment_image));

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
            mSummaryShippingFeeContainer.setVisibility(summary.getDelivery() == 0 ? View.GONE : View.VISIBLE);
            mSummaryShippingFeeTextView.setText(PriceUtils.getPriceStringValue(summary.getDelivery()));
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
                mSummaryPayTodayContainer.setVisibility(View.VISIBLE);
                mSummaryShippingFeeContainer.setVisibility(View.GONE);
            } else {
                mSummaryOurpaySelectContainer.setVisibility(View.GONE);
                mSummaryPayTodayContainer.setVisibility(View.GONE);
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
        showMyPayDetails(mValue, mOurpay);
        displayPaymentDetails();
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
    public void storeCartDetails(Value value) {
        //Set 3DS value
        if (value != null) {
            PaymentInfo.setThreeDSecureRequired(value.threeDSecureRequired);
            PaymentInfo.setCartCost(value.getSummary().getTotal());
        }

        mValue = value;
        mPresenter.generateOurpay(mValue);
    }

    @Override
    public void triggerLoginTicket() {
        assert (mActivity) != null;
        mActivity.callLoginTicket();
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

    private void onPayButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }
        mActionTracker.startCheckoutEvent();
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (mActivity.getPaymentMethodSelected() == null) {
                showAddPaymentMethodController();
            } else {
                PaymentInfo.setFabricPaymentType(PaymentInfo.isThreeDSecureRequired() ?
                        ActionTracker.PaymentOption.THREEDS.getValue() :
                        ActionTracker.PaymentOption.REGULAR.getValue());
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
                mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().getTotal());
            }
        }
    }

    private void onPaypalButtonClick() {

        if (!isAddressValid()) {
            //push add new address fragment
            showAddAddressController();
            return;
        }
        getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.PAYPAL);
        mActionTracker.startCheckoutEvent();
        PaymentInfo.setFabricPaymentType(ActionTracker.PaymentOption.PAYPAL.getValue());
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            //If no selected payment method displayed, call paypal
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalPayment();
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
                mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().getTotal());
            }
        }
    }

    private void onPaypalCreditButtonClick() {
        if (!isAddressValid()) {
            showAddAddressController();
            return;
        }
        getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.PAYPAL);
        mActionTracker.startCheckoutEvent();
        PaymentInfo.setFabricPaymentType(ActionTracker.PaymentOption.PAYPAL.getValue());
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalCreditPayment(String.valueOf(mValue.getSummary().getTotal()));
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
                mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().getTotal());
            }
        }

    }

    private void onMasterpassButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }
        getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.MASTERPASS);
        mActionTracker.startCheckoutEvent();
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);
        PaymentInfo.setFabricPaymentType(ActionTracker.PaymentOption.MASTERPASS.getValue());
        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));

        mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().getTotal());

    }

    private void onOurpayButtonClick() {
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

                        getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.OURPAY_PHONE_VERIFIATION);

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
                        mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().getTotal());
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
            mActionTracker.checkoutJourney(ActionTracker.EventProgress.END.getValue());
        }

        hidePaymentButtons();
        if (mNoCartItemsLayout != null) {
            mNoCartItemsLayout.setVisibility(View.VISIBLE);
        }
        mCheckoutContainer.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void showCartItems() {
        mActionTracker.addToCartJourneyViewCart();

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
        for(View button : buttons) {
            button.setVisibility(visibility);
        }
    }

    private List<View> getAllButtons() {
        return new ArrayList<View>() {{
            add(mPayButton);
            add(mPaypalButton);
            add(mPaypalCreditButton);
            add(mMasterpassButton);
            add(mVisaCheckoutButton);
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
            if (mPresenter.isVcoEnabled()) buttons.add(mVisaCheckoutButton);
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

    private void ourpayPaymentSubmit() {
        PaymentInfo.setFabricPaymentType(PaymentInfo.isThreeDSecureRequired() ?
                ActionTracker.PaymentOption.OURPAY3DS.getValue() :
                ActionTracker.PaymentOption.OURPAY.getValue());
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
        bundle.putString(BundleKeys.CURRENT_ORDER_VALUE, new Gson().toJson(mValue, Value.class));

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
        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }
        getPresenter().setLastCartRedirection(ActionTracker.LastRedirection.VISACHECKOUT);
        mActionTracker.startCheckoutEvent();
        PaymentInfo.setFabricPaymentType(ActionTracker.PaymentOption.VCO.getValue());
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
}

