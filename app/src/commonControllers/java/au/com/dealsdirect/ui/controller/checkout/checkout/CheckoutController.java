package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.rxbinding2.view.RxView;
import com.mysale.genie.utility.RxBus;
import com.mysale.genie.utility.config.model.getappsettings.Checkout;
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
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.deliveryoptions.DeliveryOptionsController;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
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

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    @Nullable @BindView(R.id.controller_checkout_recyclerview_items)
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

    @BindView(R.id.partial_checkout_voucher_value_text_view)
    TextView mVoucherValueTextView;
    @BindView(R.id.partial_checkout_voucher_promo_code_text_view)
    TextView mVoucherPromoCodeTextView;

    @BindView(R.id.partial_checkout_address_container)
    ViewGroup mAddressLayout;
    @BindView(R.id.partial_checkout_payment_container)
    ViewGroup mPaymentLayout;
    @BindView(R.id.partial_checkout_summary_container)
    ViewGroup mSummaryLayout;

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
    ImageButton mToolbarLeftButton;
    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;
    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;
    @BindView(R.id.checkout_scrollview)
    NestedScrollView mNestedScrollView;

    //DELIVERY OPTIONS UI
    @BindView(R.id.delivery_option_root_layout)
    ViewGroup mDeliveryOptionRootLayout;
    @BindView(R.id.delivery_option_non_ourpay_text_view)
    TextView mDeliveryOptionTypeText;
    @BindView(R.id.delivery_option_ourpay_select_container)
    ViewGroup mDeliveryOptionTypeOurPay;
    @BindView(R.id.delivery_option_price_text_view)
    TextView mDeliveryOptionPriceTextView;
    @BindView(R.id.delivery_option_ourpay_select_description_text_view)
    TextView mDeliveryOptionOurpaySelectDescriptionTextView;
    @BindView(R.id.delivery_option_ourpay_select_before_purchase_description_text_view)
    TextView mDeliveryOptionOurpaySelectBeforePurchaseDescriptionTextView;


    private RelativeLayout mButtonOurpay;
    private CheckBox mCheckBoxOurpayTC;

    private List<DeliveryOption> mDeliveryOptions;
    private DeliveryOption mSelectedDeliveryOption;
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;

    private List<Item> mItemList = new ArrayList<>();
    private ArrayList<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private ArrayList<DecorationInfoList> mDecorationInfoList = new ArrayList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();
    private CheckoutOrderAdapter mAdapter;

    private boolean mIsCartLoading = false;
    private boolean mIsVoucherAdded = false;
    private boolean mIsPaymentMethodChanged = false;
    private String mAddressPhoneNumber;

    private Value mValue;

    public OurpayPanel ourpayPanel;

    private CheckoutMvpView mCheckoutHostView;

    private CompositeDisposable mClickListeners;
    private CompositeDisposable mChangeClickListeners;

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
        } else {
            getRouter().pushController(RouterTransaction.with(new ViewAddressController(true, mDeliveryAddress))
                    .pushChangeHandler(new HorizontalChangeHandler(false))
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    private void changePayment() {
        if (mPaymentList.size() > 1) {
//                  //push to payment select
            showPaymentSelectController();
        } else {
            //push controller to add payment
            if (!isAddressValid()) {
                //push add new address fragment
                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                showAddAddressController();
            } else {
                showAddPaymentMethodController();
            }
        }
    }


    private void changeVoucher() {
        TextView discountTextView = (TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher);
        boolean isNoDiscount = true;
        if (discountTextView != null) {
            if (!discountTextView.getText().toString().equals("$0")) {
                isNoDiscount = false;
            }
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
        registerForActivityResult(BraintreeRequestCodes.VISA_CHECKOUT);

        mCheckoutHostView = (CheckoutMvpView) mActivity.getCheckoutRouter().getControllerWithTag(getString(R.string.checkout_host_controller));
        return view;
    }


    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        //disable toolbar left and right buttons
        mToolbarLeftButton.setVisibility(View.GONE);
        mToolbarRightButton.setVisibility(View.GONE);

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
        mPresenter.onDetach();
        super.onDestroyView(view);
    }


    @Override
    protected void setUp(View view) {
        if (mActivity != null) {
            mActivity.getMainController().showBottomNav();
            mActivity.getMainController().setViewpagerDraggable(false);
        }

        mTitleTextView.setText(!mActivity.isTablet() ? R.string.checkout_page_toolbar_title : R.string.checkout_page_tablet_toolbar_title);

        if(!mActivity.isTablet()) {
            mAdapter = new CheckoutOrderAdapter(mActivity, mItemList, mPresenter);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        }

        mPayButton.setOnClickListener(view1 -> onPayButtonClick());
        mPaypalButton.setOnClickListener(view2 -> onPaypalButtonClick());
        mMasterpassButton.setOnClickListener(view3 -> onMasterpassButtonClick());

        //Code for returning to checkout, call reload
        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to instanceof CheckoutController && mActivity.isAuthorized()) {
                    loadCart();
                }
            }
        });

        if (mVcoPresenter.isVisaCheckoutEnabled()) {
            mVcoPresenter.setupVisaCheckout();
        }
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
                            mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().total);
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

            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
            boolean isMyPayEnabled = mActivity.getIsMyPayEnabled();

            if (ourpay != null && isMyPayEnabled && ourpay.isCanUse()) {

                if (((MainActivity) getActivity()).getMainController().getHomeController().isCheckoutRouterVisible()) {
                    Log.d("ourpay", "checkout controller is visible");
                    OurpayStateManager.setOurpayAccordingToPaymentMethod(ourpay, paymentMethod);
                    PaymentInfo.setOurpay(ourpay);

                    ourpayPanel = new OurpayPanel((BaseActivity) mActivity, getRouter());
                    mOurpayHolder.removeAllViews();
                    if (mOurpayHolder.getChildCount() == 0) { //add view if there is no childview yet
                        mOurpayHolder.addView(ourpayPanel.generatePanel(PaymentInfo.getOurpay(), isRowVisible -> {
                            if (isRowVisible) {
                                new Handler().postDelayed(() -> mNestedScrollView.fullScroll(View.FOCUS_DOWN), 400);
                            }
                        }));
                    }

                    mButtonOurpay = (RelativeLayout) mOurpayHolder.findViewById(R.id.rl_button_ourpay);
                    mButtonOurpay.setOnClickListener(view -> onOurpayButtonClick());

                    if (ourpay.getTermsAndConditionsCheckboxState() != 0) {
                        mCheckBoxOurpayTC = (CheckBox) mOurpayHolder.findViewById(R.id.ourpay_checkbox_tc);
                    }

                    if (isOurPaySelectDeliveryMethod()) { // show ourpay select related summary
                        ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_ourpay_select_price)).setText(PriceUtils.getPriceStringValue(mDeliveryServicePackageDetail.getAmount()));
                        ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_pay_today_price)).setText(PriceUtils.getPriceStringValue(ourpay.getAmount()));
                        ourpayPanel.getCartAmountHeader().setVisibility(View.GONE);
                    } else {
                        ourpayPanel.getCartAmountHeader().setVisibility(View.VISIBLE);
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

        if(mCheckoutHostView != null){
            mCheckoutHostView.showCartDetails(items);
        }

        if (items == null) { //do nothing (ie. when increasing order quantity, returns a soldout/out of stock message)
            return;
        }

        mItemList = items;

        if (items.isEmpty()) {
            //no items
            showNoCartItemsLayout();
        } else {

            if(mAdapter != null) {
                mAdapter.replaceData(items);
            }

            showCartItems();
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
        if (getResources().getBoolean(R.bool.is_ozsale_app)) {

            mDeliveryOptions = deliveryOptions;
            mDeliveryServicePackageDetail = deliveryServicePackageDetail;

            mDeliveryOptionRootLayout.setVisibility(View.VISIBLE);

            checkPaymentMethodValidForOurPaySelect();

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
            displayPaymentDetails();
        }
    }

    private void displayDeliveryOptionsUI(String deliveryOptionName, Double deliveryOptionPrice) {
        mDeliveryOptionRootLayout.setOnClickListener(v -> showDeliveryOptionsController());

        String ourpaySelectDescription = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_DESCRIPTION);
        String ourpaySelectBeforePurchaseDesc = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_OURPAY_OPS_INFO_REMAINING_BEFORE_PURCHASE_FREE_DELIVERY);
        String freeText = mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_OPS_FREE);

        if (OurpayTemplateText.DeliveryOptions.STANDARD.equalsName(deliveryOptionName) ||
                OurpayTemplateText.DeliveryOptions.EXPRESS.equalsName(deliveryOptionName)) {
            mDeliveryOptionTypeText.setVisibility(View.VISIBLE);
            mDeliveryOptionTypeOurPay.setVisibility(View.GONE);
            mDeliveryOptionOurpaySelectBeforePurchaseDescriptionTextView.setVisibility(View.GONE);
            mDeliveryOptionTypeText.setText(deliveryOptionName);
            mDeliveryOptionPriceTextView.setText(PriceUtils.getPriceStringValue(deliveryOptionPrice));
        } else if (OurpayTemplateText.DeliveryOptions.OURPAYSELECT.equalsName(deliveryOptionName)) {

            if (mDeliveryServicePackageDetail != null) {
                String remainingFreeQty = mDeliveryServicePackageDetail.getRemainingCount().toString();
                ourpaySelectBeforePurchaseDesc = ourpaySelectBeforePurchaseDesc.replace(OurpayTemplateText.KEY_DELIVERYOPTION_FREE_DELIVERY_QTY, remainingFreeQty);

                mDeliveryOptionTypeText.setVisibility(View.GONE);
                mDeliveryOptionTypeOurPay.setVisibility(View.VISIBLE);
                mDeliveryOptionOurpaySelectBeforePurchaseDescriptionTextView.setVisibility(View.VISIBLE);
                mDeliveryOptionOurpaySelectDescriptionTextView.setText(ourpaySelectDescription);
                mDeliveryOptionOurpaySelectBeforePurchaseDescriptionTextView.setText(ourpaySelectBeforePurchaseDesc);

                String finalPriceText = mDeliveryServicePackageDetail.getPurchased() ? freeText : PriceUtils.getPriceStringValue(mDeliveryServicePackageDetail.getAmount());
                mDeliveryOptionPriceTextView.setText(finalPriceText);
            }
        }
    }

    private void checkPaymentMethodValidForOurPaySelect() {
        //need to invalidate paypal/masterpass if ourpayselect delivery method is chosen;
        //mIsPaymentMethodChanged is set to true from PaymentSelectController or AddPaymentController if they choose
        //or add a payment method. It is then set to false when going back to those screens from CheckoutController
        if (isOurPaySelectDeliveryMethod() && mIsPaymentMethodChanged) {
            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
            if (paymentMethod != null) {
                if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL) || paymentMethod.getPaymentType().equalsIgnoreCase(CARD_MASTERPASS)
                        || paymentMethod.getPaymentType().equalsIgnoreCase(CARD_VISA_CHECKOUT)) {
                    mPresenter.setDeliveryOption(createStandardDeliveryOptionRequest());
                }
            }
        }
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
            mActivity.setPaymentMethodSelected(null);
            return;

        } else if (mActivity.getPaymentMethodSelected() == null) {
            mActivity.setPaymentMethodSelected(paymentMethod);
        }
    }

    private void displayPaymentDetails() {
        PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
        if (paymentMethod != null) {

            if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) {
                mVisaCheckoutButton.setVisibility(View.GONE);
                mPayButton.setVisibility(View.GONE);
                mPaypalButton.setVisibility(View.VISIBLE);

                if (mSelectedDeliveryOption != null && OurpayTemplateText.DeliveryOptions.OURPAYSELECT.equalsName(mSelectedDeliveryOption.getDeliveryOptions().get(0))) {
                    //set
                    mActivity.setPaymentMethodSelected(findFirstPaymentMethodValidForOurpaySelect());
                    paymentMethod = mActivity.getPaymentMethodSelected();
                }
            } else {
                showPaymentButtons();
                mPaypalButton.setVisibility(View.GONE);
            }

            mMasterpassButton.setVisibility(View.GONE);

            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).setText(paymentMethod.getPaymentType());
            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_details)).setText(paymentMethod.getDescription());

            ImageUtils.loadImage(mActivity
                    , paymentMethod.getImageUrl()
                    , (ImageView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_image));

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
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_subtotal)).setText(PriceUtils.getPriceStringValue(summary.subtotal));
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_shipping_fee)).setText(PriceUtils.getPriceStringValue(summary.delivery));
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher)).setText(PriceUtils.getPriceStringValue(summary.discount));

            if (summary.tax > 0) {
                ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_tax)).setText(PriceUtils.getPriceStringValue(summary.tax));
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_tax_container).setVisibility(View.VISIBLE);
            } else
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_tax_container).setVisibility(View.GONE);


            if (summary.discount > 0) {
                ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher)).setText(PriceUtils.getPriceStringValue(summary.discount));
                mIsVoucherAdded = true;
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher_container).setVisibility(View.VISIBLE);
                mVoucherValueTextView.setVisibility(View.VISIBLE);
                mVoucherValueTextView.setText(PriceUtils.getPriceStringValue(summary.discount) + " " + getString(R.string.voucher));
            } else {
                mIsVoucherAdded = false;
                mVoucherValueTextView.setVisibility(View.GONE);
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher_container).setVisibility(View.GONE);
            }
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_total)).setText(PriceUtils.getPriceStringValue(summary.total));

            // show ourpay select related summary, should been purchased yet if visible.
            if (isOurPaySelectDeliveryMethod() && !mDeliveryServicePackageDetail.getPurchased()) {
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_ourpay_select_container).setVisibility(View.VISIBLE);
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_pay_today_container).setVisibility(View.VISIBLE);
            } else {
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_ourpay_select_container).setVisibility(View.GONE);
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_pay_today_container).setVisibility(View.GONE);
            }
        }


    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        mPaymentList.clear();
        mPaymentList.addAll(paymentList);
    }

    @Override
    public void storeCartDetails(Value value) {
        //Set 3DS value
        if (value != null) {
            PaymentInfo.setThreeDSecureRequired(value.threeDSecureRequired);
            PaymentInfo.setCartCost(value.getSummary().total);
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
        return mDeliveryServicePackageDetail != null;
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

    private void onPayButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (mActivity.getPaymentMethodSelected() == null) {
                showAddPaymentMethodController();
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
                mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().total);
            }
        }
    }

    private void onPaypalButtonClick() {

        if (!isAddressValid()) {
            //push add new address fragment
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            //If no selected payment method displayed, call paypal
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalPayment();
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
                mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().total);
            }
        }
    }

    private void onMasterpassButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));

        mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().total);

    }

    private void onOurpayButtonClick() {
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        assert (mActivity) != null;

        if (mActivity.isBraintreeInitialized()) {

            if (!isAddressValid()) {

                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                showAddAddressController();

            } else if (mActivity.getPaymentMethodSelected() == null) {

                showAddPaymentMethodController();

            } else {

                if (!mActivity.getPaymentMethodSelected().getPaymentType().equalsIgnoreCase(CARD_PAYPAL) && PaymentInfo.getOurpay().isCanUse()) {

                    if (mCheckBoxOurpayTC != null && !mCheckBoxOurpayTC.isChecked()) {
                        CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, OurpayTemplateText.getText(mActivity, KEY_OURPAY_TC_VALIDATION_FAILED));
                        return;
                    }

                    if (PaymentInfo.getOurpay().isPhoneVerificationRequired()) {

                        getRouter().pushController(RouterTransaction.with(OurpaySMSVerificationController.newInstance(mAddressPhoneNumber))
                                .pushChangeHandler(new HorizontalChangeHandler(false))
                                .popChangeHandler(new HorizontalChangeHandler()));
                    } else {
                        ourpayPaymentSubmit();
                        mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mItemList.size(), mValue.getSummary().total);
                    }
                }
            }
        }
    }

    private boolean isAddressValid() {
        return mDeliveryAddress != null;
    }

    @Optional @OnClick(R.id.partial_checkout_empty_button)
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
        hidePaymentButtons();
        if (mNoCartItemsLayout != null) {
            mNoCartItemsLayout.setVisibility(View.VISIBLE);
        }
        mCheckoutContainer.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void showCartItems() {
        showPaymentButtons();
        if (mNoCartItemsLayout != null) {
            mNoCartItemsLayout.setVisibility(View.GONE);
        }
        mCheckoutContainer.setVisibility(View.VISIBLE);
        int showOrdersLabel = getResources().getBoolean(R.bool.is_checkout_orders_label_visible) ? View.VISIBLE : View.GONE;

        if (mOrdersLabel != null) {
            mOrdersLabel.setVisibility(showOrdersLabel);
        }
    }

    private void hidePaymentButtons() {
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mButtonHolder.setVisibility(View.VISIBLE);
        checkVisiblePaymentButtons();
    }

    private void checkVisiblePaymentButtons() {
        mPayButton.setVisibility(isOurPaySelectDeliveryMethod() ? View.GONE : View.VISIBLE);
        mPaypalButton.setVisibility(isOurPaySelectDeliveryMethod() ? View.GONE : View.VISIBLE);
        mMasterpassButton.setVisibility(isOurPaySelectDeliveryMethod() ? View.GONE : View.VISIBLE);
        mVisaCheckoutButton.setVisibility(isOurPaySelectDeliveryMethod() ? View.GONE : View.VISIBLE);
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

    private void ourpayPaymentSubmit() {
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
        bundle.putString(BundleKeys.CART_TOTAL_COST, Double.toString(mValue.getSummary().total));
        bundle.putString(BundleKeys.CURRENT_ORDER_VALUE, new Gson().toJson(mValue, Value.class));

        getRouter().pushController(RouterTransaction.with(new PaymentSelectController(bundle))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void showAddPaymentMethodController() {
        mIsPaymentMethodChanged = false;

        Bundle bundle = new Bundle();
        bundle.putBoolean(BundleKeys.IS_FROM_CART, true);
        bundle.putBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, isOurPaySelectDeliveryMethod());
        bundle.putString(BundleKeys.CART_TOTAL_COST, Double.toString(mValue.getSummary().total));
        bundle.putString(BundleKeys.CURRENT_ORDER_VALUE, new Gson().toJson(mValue, Value.class));
        getRouter().pushController(RouterTransaction.with(new AddPaymentController(bundle))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public void removeOurpayView() {
        mOurpayHolder.removeAllViews();
    }

    public void clearOurpayGraphBitmapsAndListeners() {
        if (ourpayPanel != null) {
            ourpayPanel.clearOurpayGraphBitmapsAndListeners();

        }
    }


    public void setIsGraphVisible(boolean isVisible) {
        if (ourpayPanel != null) {
            ourpayPanel.setIsGraphVisible(isVisible);
        }
    }

    @Override
    public void onVisaCheckoutButtonClicked() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        mVcoPresenter.payWithVisaCheckout(mValue.getSummary().total);
    }


    private PaymentMethod findFirstPaymentMethodValidForOurpaySelect() {
        PaymentMethod firstPaymentMethod = null;
        for (int i = 0; i < mPaymentList.size(); i++) {
            PaymentMethod paymentMethod = mPaymentList.get(i);
            if (!paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL) && !paymentMethod.getPaymentType().equalsIgnoreCase(CARD_MASTERPASS)
                    && !paymentMethod.getPaymentType().equalsIgnoreCase(CARD_VISA_CHECKOUT)) {
                firstPaymentMethod = paymentMethod;
            }
        }
        return firstPaymentMethod;
    }

    private SetDeliveryOption.OptionParameters createStandardDeliveryOptionRequest() {
        DeliveryOption standardDeliveryOption = null;

        for (DeliveryOption option : mDeliveryOptions) {
            if (OurpayTemplateText.DeliveryOptions.STANDARD.equalsName(option.getDeliveryOptions().get(0))) {
                standardDeliveryOption = option;
                break;
            }
        }

        if (standardDeliveryOption == null) {
            return null;
        }

        standardDeliveryOption.setName(mActivity.getMyTemplateTexts(OurpayTemplateText.KEY_DELIVERYOPTION_STANDARD_TITLE));
        standardDeliveryOption.setSelected(true);

        SetDeliveryOption.OptionParameters optionParameters
                = new SetDeliveryOption.OptionParameters(mDeliveryAddress != null ? mDeliveryAddress.id : "", "",
                new Gson().toJson(standardDeliveryOption), "");

        return optionParameters;

    }
}

