package au.com.dealsdirect.ui.controller.checkout.addpayment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
//import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.cardform.OnCardFormScanListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.mysale.genie.utility.RxBus;
import com.stripe.android.model.CardBrand;
import com.stripe.android.view.CardNumberEditText;
import com.stripe.android.view.CvcEditText;
//import com.visa.checkout.VisaCheckoutSdk;

import java.util.HashMap;
import java.util.Set;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.data.network.model.events.GA4EventParams;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.toggleswitch.OurPayToggleSwitch;
import au.com.dealsdirect.service.braintree.FetchBraintreeClientTokenHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ExpiryDateEditText;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.PaymentOption;
import static au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutDetailsMapper.decompress;

/*
 * Created by smartwave on 29/06/2017.
 */

public class AddPaymentController extends BaseController implements AddPaymentMvpView, OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener, OnCardFormScanListener {

    public static abstract class Parameters {
        private Parameters() {
        }

        public static final class FromCheckout extends Parameters {
            private Boolean mIsOurpaySelectDeliveryMethod;
            private String mCartTotalCost;
            private CheckoutDetailsMapper mCurrentOrderValue;

            public FromCheckout(Boolean isOurpaySelectDeliveryMethod,
                                String cartTotalCost,
                                CheckoutDetailsMapper currentOrderValue) {
                mIsOurpaySelectDeliveryMethod = isOurpaySelectDeliveryMethod;
                mCartTotalCost = cartTotalCost;
                mCurrentOrderValue = currentOrderValue;
            }

            public Boolean getIsOurpaySelectDeliveryMethod() {
                return mIsOurpaySelectDeliveryMethod;
            }

            public String getCartTotalCost() {
                return mCartTotalCost;
            }

            public CheckoutDetailsMapper getCurrentOrderValue() {
                return mCurrentOrderValue;
            }
        }
    }

    @Inject
    AddPaymentMvpPresenter<AddPaymentMvpView> mPresenter;

    @BindView(R.id.card_form)
    CardForm mCardForm;
    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mPayButton;
    @BindView(R.id.add_payment_checkout_buttons)
    ViewGroup mCheckoutButtons;
    @BindView(R.id.add_payment_add_button)
    Button mAddButton;
    @BindView(R.id.partial_checkout_button_paypal_text)
    TextView mTextPaypal;
    @BindView(R.id.partial_checkout_button_paypal)
    RelativeLayout mPaypalButton;
    @BindView(R.id.partial_checkout_button_paypal_credit)
    RelativeLayout mPaypalCreditButton;
    @BindView(R.id.button_visa_checkout)
    Button mVcoButton;
    @BindView(R.id.partial_checkout_button_masterpass)
    RelativeLayout mMasterpassButton;
    @BindView(R.id.partial_checkout_ourpay_panel_holder)
    LinearLayout mOurpayHolder;
    @BindView(R.id.add_payment_scrollview)
    ScrollView mNestedScrollView;
    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mCameraButton;
    @BindView(R.id.partial_checkout_line_above_afterpay)
    View mLineView;
    @BindView(R.id.partial_checkout_afterpay_panel_holder)
    View mAfterpayPanel;
    @BindView(R.id.partial_checkout_lpay_panel_holder)
    View mLPayPanel;

    @BindView(R.id.partial_checkout_button_klarna)
    RelativeLayout mKlarnaButton;
    @BindView(R.id.partial_checkout_button_g_pay_container)
    RelativeLayout mGPayButton;

    // Stripe
    @BindView(R.id.stripe_form_layout)
    LinearLayout mStripeLayout;
    @BindView(R.id.stripe_card_form_card_number)
    CardNumberEditText mStripeCardNumber;
    @BindView(R.id.stripe_card_form_expiration)
    ExpiryDateEditText mStripeExpiryDate;
    @BindView(R.id.stripe_card_form_cvv)
    CvcEditText mStripeCVV;

    @BindView(R.id.partial_toolbar_title)
    TextView mViewAddressToolarTitle;

    private CheckoutMvpView mCheckoutMvpView;

    private boolean isFromCart;
    private boolean isPayPalSubmitClicked = false;
    private boolean mIsOurpaySelectDeliveryMethod;
    private String mCartTotalCost;
    private CheckoutDetailsMapper mCurrentOrderValue;
    private GA4EventParams.GA4AddPaymentInfoParams ga4AddPaymentInfoParams = null;

    public static AddPaymentController newInstance() {
        return new AddPaymentController(new BundleBuilder(new Bundle()).build());
    }

    public static AddPaymentController newInstance(Parameters parameters) {
        AddPaymentController controller = AddPaymentController.newInstance();

        if (parameters instanceof Parameters.FromCheckout) {
            controller.isFromCart = true;
            controller.mIsOurpaySelectDeliveryMethod = ((Parameters.FromCheckout) parameters).getIsOurpaySelectDeliveryMethod();
            controller.mCartTotalCost = ((Parameters.FromCheckout) parameters).getCartTotalCost();
            controller.mCurrentOrderValue = ((Parameters.FromCheckout) parameters).getCurrentOrderValue();
        }

        return controller;
    }

    public AddPaymentController(Bundle args) {
        super(args);
        isFromCart = args.getBoolean(BundleKeys.IS_FROM_CART, false);
        mIsOurpaySelectDeliveryMethod = args.getBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, false);
        mCartTotalCost = args.getString(BundleKeys.CART_TOTAL_COST, "");
        mCurrentOrderValue = decompress(args.getByteArray(BundleKeys.CURRENT_ORDER_VALUE));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(BundleKeys.IS_FROM_CART, isFromCart);
        outState.putBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, mIsOurpaySelectDeliveryMethod);
        outState.putString(BundleKeys.CART_TOTAL_COST, mCartTotalCost);
        if (mCurrentOrderValue != null) {
            mCurrentOrderValue.putInBundle(outState, BundleKeys.CURRENT_ORDER_VALUE);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isFromCart = savedInstanceState.getBoolean(BundleKeys.IS_FROM_CART);
        mIsOurpaySelectDeliveryMethod = savedInstanceState.getBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD);
        mCartTotalCost = savedInstanceState.getString(BundleKeys.CART_TOTAL_COST);
        mCurrentOrderValue = decompress(savedInstanceState.getByteArray(BundleKeys.CURRENT_ORDER_VALUE));
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_payment, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
//        mVcoPresenter.onAttach(this);

        mCheckoutMvpView = (CheckoutMvpView) getRouter().getControllerWithTag(CheckoutController.class.getName());
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        if (isFromCart) {

            Set<PaymentOption> paymentOptions = mCurrentOrderValue.getAvailablePaymentOptions();

            if (paymentOptions.contains(PaymentOption.OURPAY)) {
                mPresenter.generateOurpay(mCurrentOrderValue);
            }
        }
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mViewAddressToolarTitle.setText("Add New Payment");

        mLineView.setVisibility(View.GONE);
        mAfterpayPanel.setVisibility(View.GONE);
        mLPayPanel.setVisibility(View.GONE);
        mKlarnaButton.setVisibility(View.GONE);
        mGPayButton.setVisibility(View.GONE);
        mPayButton.setText("Add");
        mPayButton.setBackground(getResources().getDrawable(R.drawable.bg_button_login));

//        mVcoButton.setVisibility(mVcoPresenter.isVisaCheckoutEnabled() ? View.VISIBLE :
//                View.GONE);
        mVcoButton.setVisibility(View.GONE);

        mCheckoutButtons.setVisibility(isFromCart ? View.VISIBLE : View.GONE);
        mAddButton.setVisibility(isFromCart ? View.GONE : View.VISIBLE);

        mStripeLayout.setVisibility(View.VISIBLE);

        mCardForm.cardRequired(true)
                .expirationRequired(false)
                .cvvRequired(false)
                .actionLabel("Purchase")
                .setup(getActivity());
        mCardForm.setVisibility(View.GONE);

        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);
        mCardForm.setOnCardFormScanListener(this);
        mCameraButton.setBackground(null);
        mCameraButton.setImageDrawable(getResources().getDrawable(R.drawable.bg_credit_card));
//        mCameraButton.setVisibility(View.VISIBLE);
        mCameraButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        mCameraButton.setPadding(0, 0, 0, 0);
        DrawableCompat.setTint(
                DrawableCompat.wrap(mCameraButton.getDrawable()),
                ContextCompat.getColor(mActivity, R.color.toolbar_text_dark));
        mCardForm.setToolbarColor(getResources().getColor(R.color.toolbar_active_skin));
        mPayButton.setVisibility(View.VISIBLE);
        mPaypalButton.setVisibility(View.VISIBLE);
        mTextPaypal.setVisibility(View.VISIBLE);

        mAddButton.setOnClickListener(action -> {
            onCardFormSubmit();
        });

        mPayButton.setOnClickListener(action -> {
            onCardFormSubmit();
            if (isFromCart) {
                mCheckoutMvpView.setIsPaymentMethodChanged(true);
            }
        });

        mPaypalButton.setOnClickListener(action -> {
            onPaypalSubmit();
            if (isFromCart) {
                mCheckoutMvpView.setIsPaymentMethodChanged(true);
            }
        });

        mPaypalCreditButton.setOnClickListener(action -> {
            onPaypalCreditSubmit();
            if (isFromCart) {
                mCheckoutMvpView.setIsPaymentMethodChanged(true);
            }
        });

        if (mActivity.isBraintreeInitialized()) {
            showPaymentButtons();
        } else {
            hidePaymentButtons();
            mActivity.fetchBraintreeAuthorization(new FetchBraintreeClientTokenHandler() {
                @Override
                public void onSuccess() {
                    showPaymentButtons();
                }

                @Override
                public void onFailure() {
                    hidePaymentButtons();
                }
            });
        }

        if (isFromCart) {
            mMasterpassButton.setOnClickListener(action -> {
                onMasterpassButtonClick();
                mCheckoutMvpView.setIsPaymentMethodChanged(true);
            });

        } else {
            mVcoButton.setVisibility(View.GONE);
            mMasterpassButton.setVisibility(View.GONE);
            mPaypalCreditButton.setVisibility(View.GONE);
        }

//        mVcoButton.setOnClickListener(action -> {
//            if (isProcessingVco) {
//                isProcessingVco = false;
//            }
//            onVisaCheckoutButtonClicked();
//        });


        mStripeCardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, CardBrand.Unknown.getIcon(), 0);

        mStripeCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mStripeCardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, mStripeCardNumber.getCardBrand().getIcon(), 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
                mStripeCardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, mStripeCardNumber.getCardBrand().getIcon(), 0);
            }
        });

        mStripeExpiryDate.setActivity(mActivity);
    }

    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        if (isPayPalSubmitClicked) {
            showLoading();
        }
        isPayPalSubmitClicked = false;
    }

    private void hidePaymentButtons() {
        if (mButtonHolder != null) {
            mButtonHolder.setVisibility(View.GONE);
        }
    }

    private void showPaymentButtons() {
        if (mButtonHolder != null) {
            mButtonHolder.setVisibility(View.VISIBLE);
            checkVisiblePaymentButtons();
        }
    }

    private void checkVisiblePaymentButtons() {
        if (mCurrentOrderValue == null) {
            mPayButton.setVisibility(View.VISIBLE);
            mPaypalButton.setVisibility(mPresenter.isPayPalEnabled() ? View.VISIBLE : View.GONE);
            mMasterpassButton.setVisibility(isFromCart && mPresenter.isMasterPassEnabled() && !mIsOurpaySelectDeliveryMethod ? View.VISIBLE : View.GONE);
            mPaypalCreditButton.setVisibility(mPresenter.isPaypalCreditEnabled() ? View.VISIBLE : View.GONE);
        } else {
            mPaypalCreditButton.setVisibility(View.GONE);

            Set<PaymentOption> paymentOptions = mCurrentOrderValue.getAvailablePaymentOptions();

            if (paymentOptions.contains(PaymentOption.BRAINTREE)) {
                mPayButton.setVisibility(View.VISIBLE);
            } else {
                mPayButton.setVisibility(View.GONE);
            }

            if (paymentOptions.contains(PaymentOption.BRAINTREEPAYPAL)) {
                mPaypalButton.setVisibility(View.VISIBLE);
            } else {
                mPaypalButton.setVisibility(View.GONE);
            }

            if (paymentOptions.contains(PaymentOption.MASTERPASSPAYMENT)) {
                mMasterpassButton.setVisibility(View.VISIBLE);
            } else {
                mMasterpassButton.setVisibility(View.GONE);
            }

//            if (paymentOptions.contains(PaymentOption.VISACHECKOUT) && mVcoPresenter.isVisaCheckoutEnabled()) {
//                mVcoButton.setVisibility(View.VISIBLE);
//            } else {
                mVcoButton.setVisibility(View.GONE);
//            }
        }
    }

    @Override
    public void onCardFormSubmit() {
        hideKeyboard();

        if (!mStripeCVV.getText().toString().equalsIgnoreCase("")
                && !mStripeCardNumber.getText().toString().equalsIgnoreCase("")) {

            mStripeExpiryDate.validate();

            if (mStripeExpiryDate.isValid()) {
                mActivity.createStripePaymentMethod(mStripeCardNumber.getText().toString(), Integer.parseInt(mStripeExpiryDate.getMonth()),
                        Integer.parseInt(mStripeExpiryDate.getYear()), mStripeCVV.getText().toString());
            }

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getResources().getString(R.string.stripe_add_card_error));
        }

    }

    @Override
    public void onCardTypeChanged(CardType cardType) {

    }

    @Override
    public void onPaypalSubmit() {
        isPayPalSubmitClicked = true;
        showLoading();
        mActivity.startPaypalPayment();
    }

    private void onPaypalCreditSubmit() {
        showLoading();
        mActivity.startPaypalCreditPayment(mCartTotalCost);
    }


    @Override
    public void showAddPaymentResult(boolean result, String paymentType) {
        hideLoading();
        if (result) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Payment method added!");

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Can't add payment method");
        }
        getRouter().handleBack();
    }

    @Override
    public void clearFields() {
        mCardForm.getCardEditText().getText().clear();
        mCardForm.getCvvEditText().getText().clear();
        mCardForm.getExpirationDateEditText().getText().clear();
    }

    @Override
    public void showMyPayDetails(CheckoutDetailsMapper value, Ourpay ourpay) {
        if (value != null) {

            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
            boolean isMyPayEnabled = mActivity.getIsMyPayEnabled();

            if (ourpay != null && isMyPayEnabled) {
                boolean isPaymentInvalid = paymentMethod == null ? false : paymentMethod.getPaymentType().equalsIgnoreCase(OurpayStateManager.CARD_MASTERPASS);
                OurpayStateManager.setOurpayAccordingToPaymentMethod(ourpay, isPaymentInvalid);

                OurpayPanel ourpayPanel = new OurpayPanel((BaseActivity) mActivity, getRouter());
                mOurpayHolder.removeAllViews();
                if (mOurpayHolder.getChildCount() == 0) { //add view if there is no childview yet
                    mOurpayHolder.addView(ourpayPanel.generatePanel(ourpay, isRowVisible -> {
                        if (isRowVisible) {
                            new Handler().postDelayed(() -> mNestedScrollView.fullScroll(View.FOCUS_DOWN), 400);
                        }
                    }));
                }

                RelativeLayout mButtonOurpay = (RelativeLayout) mOurpayHolder.findViewById(R.id.rl_button_ourpay);
                mButtonOurpay.setOnClickListener(view -> onCardFormSubmit());

                OurPayToggleSwitch mOurpayTncCheckBox = mOurpayHolder.findViewById(R.id.ourpay_toggle_switch_tc);
                mOurpayTncCheckBox.setClickable(false);
                OurpayPanel.TermsAndConditionStates termsAndConditionStatesState = OurpayPanel.TermsAndConditionStates.values()[value.getOurPaySelectTermsAndConditions()];
                mOurpayTncCheckBox.setOurPayToggleSwitch(termsAndConditionStatesState);
            }
        }
    }

    @Override
    public void onMasterpassButtonClick() {

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    public boolean isCalledFromAccounts() {
        return !isFromCart;
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackPressed() {
        hideKeyboard();
        mActivity.onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void launchCamera() {

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.EVENT_PROGRESS, DataCollector.EventParameters.EventProgress.START);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, AddPaymentController.class.getSimpleName());
        DataCollector.logEvent(Events.CCScan, parameters);
        mCardForm.scanCard(getActivity());
    }

    @Override
    public void onCardFormScan() {
        //This callback is called when successful CC scanning

//        if (!isFromCart) {
//            mCardForm.getCardEditText().setEnabled(false);
//        }
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.EVENT_PROGRESS, DataCollector.EventParameters.EventProgress.SUCCESS);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, AddPaymentController.class.getSimpleName());
        DataCollector.logEvent(Events.CCScan, parameters);

    }

    @Override
    public void onScanCardResult(String cardNumber) {
        if (isFromCart) {
            mStripeCardNumber.setText(cardNumber);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == BraintreeRequestCodes.VISA_CHECKOUT) {
            showLoading();
            AppLogger.d("VC_onActivityResult", "Result got back from Visa Checkout SDK");
            String msg = "";

            if (resultCode == Activity.RESULT_CANCELED) {
                msg = "User Canceled, Result Code : " + resultCode;
            } else if (resultCode == VisaCheckoutSdk.ResultCode.RESULT_SDK_NOT_INITIALIZED) {
                msg = "Sdk not initialized  failed, Result Code : " + resultCode;
            } else if (resultCode == VisaCheckoutSdk.ResultCode.RESULT_INITIALIZED_FAILED) {
                msg = "VisaPaymentInfo validation failed, Result Code : " + resultCode;
            } else {
                msg = "Purchase failed!";
            }

            if (!msg.isEmpty()) {
                hideLoading();
                AppLogger.d("VC_onActivityResult", msg);
                onError(msg);
            }
        }*/
    }

    /*@Override
    public void onVisaCheckoutButtonClicked() {
        if (isProcessingVco) {
            isProcessingVco = false;
        }
        if (isFromCart && !mCartTotalCost.isEmpty()) {
            mVcoPresenter.payWithVisaCheckout(Double.valueOf(mCartTotalCost));
        } else {
            //TODO: should call Flow for addPaymentMethod
            return;
        }
    }*/

    @OnClick(R.id.stripe_card_form_card_number_container)
    public void onCardNumberContainerClick() {
        KeyboardUtils.showSoftInput(mStripeCardNumber, mActivity);
    }

    @OnClick(R.id.stripe_card_form_cvv_container)
    public void onCvvContainerClick() {
        KeyboardUtils.showSoftInput(mStripeCVV, mActivity);
    }

    @OnClick(R.id.stripe_card_form_expiration_container)
    public void onExpirationContainerClick() {
        KeyboardUtils.showSoftInput(mStripeExpiryDate, mActivity);
    }

    public void logAddPaymentWhileFromCart(String paymentType) {
        GA4EventParams.GA4AddPaymentInfoParams params = new GA4EventParams.GA4AddPaymentInfoParams();
        params.setPaymentType(paymentType);
        params.setCurrency(Settings.getSelectedCountry().currencyCode);
        ga4AddPaymentInfoParams = params;
    }

    public GA4EventParams.GA4AddPaymentInfoParams getGa4AddPaymentInfoParams() {
        return ga4AddPaymentInfoParams;
    }
}
