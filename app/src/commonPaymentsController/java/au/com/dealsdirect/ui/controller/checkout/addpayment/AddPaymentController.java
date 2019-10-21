package au.com.dealsdirect.ui.controller.checkout.addpayment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.cardform.OnCardFormScanListener;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.utils.CardType;
import com.braintreepayments.cardform.view.CardEditText;
import com.braintreepayments.cardform.view.CardForm;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.gson.Gson;
import com.mysale.genie.utility.RxBus;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaPaymentSummary;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.IntrospectionUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.KEY_OURPAY_TC_VALIDATION_FAILED;

/*
 * Created by smartwave on 29/06/2017.
 */

public class AddPaymentController extends VisaCheckoutController implements AddPaymentMvpView, OnCardFormSubmitListener, CardEditText.OnCardTypeChangedListener, OnCardFormScanListener {
    private final static String IS_FROM_CART = "is_from_cart";
    private final static String CART_TOTAL_COST = "cart_total_cost";

    @Inject
    AddPaymentMvpPresenter<AddPaymentMvpView> mPresenter;

    @BindView(R.id.card_form)
    CardForm mCardForm;
    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mButtonPay;
    @BindView(R.id.partial_checkout_button_paypal_text)
    TextView mTextPaypal;
    @BindView(R.id.partial_checkout_button_paypal)
    RelativeLayout mButtonPaypal;
    @BindView(R.id.partial_checkout_button_masterpass)
    RelativeLayout mMasterpassButton;
    @BindView(R.id.partial_checkout_button_paypal_credit)
    RelativeLayout mButtonPaypalCredit;
    @BindView(R.id.partial_checkout_ourpay_panel_holder)
    LinearLayout mOurpayHolder;
    @BindView(R.id.add_payment_scrollview)
    NestedScrollView mNestedScrollView;


    @BindView(R.id.partial_toolbar_title)
    TextView mViewAddressToolarTitle;
    @BindView(R.id.partial_toolbar_right_view)
    ImageView mViewAddressRightOption;

    private CheckoutMvpView mCheckoutMvpView;

    private boolean isFromCart = false;
    private boolean isPayPalSubmitClicked = false;
    private boolean mIsOurpaySelectDeliveryMethod = false;
    private String mCartTotalCost;
    private Value mValue;

    public static AddPaymentController newInstance() {
        return new AddPaymentController(new BundleBuilder(new Bundle()).build());
    }

    public AddPaymentController(Bundle args) {
        super(args);
        isFromCart = args.getBoolean(BundleKeys.IS_FROM_CART, false);
        mIsOurpaySelectDeliveryMethod = args.getBoolean(BundleKeys.IS_OURPAY_SELECT_DELIVERY_METHOD, false);
        mCartTotalCost = args.getString(BundleKeys.CART_TOTAL_COST, "");
        mValue = new Gson().fromJson(args.getString(BundleKeys.CURRENT_ORDER_VALUE, ""), Value.class);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_payment, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mVcoPresenter.onAttach(this);

        mCheckoutMvpView = (CheckoutMvpView) getRouter().getControllerWithTag(getString(R.string.checkout_controller));
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mViewAddressToolarTitle.setText("Add New Payment");
        mViewAddressRightOption.setVisibility(View.INVISIBLE);

        mCardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .actionLabel("Purchase")
                .setup(getActivity());
        mCardForm.setOnCardFormSubmitListener(this);
        mCardForm.setOnCardTypeChangedListener(this);
        mCardForm.setOnCardFormScanListener(this);
        mCardForm.setCameraIcon(getResources().getDrawable(R.drawable.bg_credit_card));
        mCardForm.setToolbarColor(getResources().getColor(R.color.toolbar_active_skin));
        mCardForm.setCameraBackground(null);
        mCardForm.setEditTextDrawable(getResources().getDrawable(R.drawable.bg_edit_text_rounded), R.drawable.bg_edit_text_rounded);
        mButtonPay.setVisibility(View.VISIBLE);
        mButtonPaypal.setVisibility(View.VISIBLE);
        mTextPaypal.setVisibility(View.VISIBLE);


        mButtonPay.setOnClickListener(action -> {
            onCardFormSubmit();
            mCheckoutMvpView.setIsPaymentMethodChanged(true);
        });

        mButtonPaypal.setOnClickListener(action -> {
            onPaypalSubmit();
            mCheckoutMvpView.setIsPaymentMethodChanged(true);
        });

        if (isFromCart) {

            if (mVcoPresenter.isVisaCheckoutEnabled()) {
                mVcoPresenter.setupVisaCheckout();
            }

            if(mPresenter.isMasterPassEnabled()) {
                mMasterpassButton.setVisibility(View.VISIBLE);
            }

            mMasterpassButton.setOnClickListener(action -> {
                onMasterpassButtonClick();
                mCheckoutMvpView.setIsPaymentMethodChanged(true);
            });

            if (mPresenter.isPaypalCreditEnabled()){
                mButtonPaypalCredit.setVisibility(View.VISIBLE);
                mButtonPaypalCredit.setOnClickListener(action -> {
                    onPaypalCreditSubmit();
                });
            }

        } else {
            mMasterpassButton.setEnabled(false);
            mMasterpassButton.setVisibility(View.GONE);
            mVisaCheckoutButton.setVisibility(View.GONE);
            mButtonPaypalCredit.setVisibility(View.GONE);
        }

        if (((MainActivity) getActivity()).isBraintreeInitialized()) {

            showPaymentButtons();
        } else {
            ((MainActivity) getActivity()).fetchAuthorization(new FetchTokenHandler() {
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
        if (mButtonHolder != null)
            mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        if (mButtonHolder != null) {
            mButtonHolder.setVisibility(View.VISIBLE);
            checkVisiblePaymentButtons();
        }
    }

    private void checkVisiblePaymentButtons() {
        mButtonPay.setVisibility(mIsOurpaySelectDeliveryMethod ? View.GONE : View.VISIBLE);
        mButtonPaypal.setVisibility(mIsOurpaySelectDeliveryMethod ? View.GONE : View.VISIBLE);
        mMasterpassButton.setVisibility(mIsOurpaySelectDeliveryMethod ? View.GONE : View.VISIBLE);
        mButtonPaypalCredit.setVisibility(mIsOurpaySelectDeliveryMethod || !mPresenter.isPaypalCreditEnabled() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCardFormSubmit() {
        hideKeyboard();

        if (mCardForm.isValid() && mActivity.getBraintreeFragment() != null) {
            showLoading();
            mActivity.onPurchase(mCardForm);

        } else if (mCardForm.isValid() && mActivity.getBraintreeFragment() == null) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Please wait for payments to finish initializing");

        } else {
            mCardForm.validate();
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

    public void onPaypalCreditSubmit(){
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
    public void showMyPayDetails(Value value, Ourpay ourpay) {
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
        getActivity().onBackPressed();
    }

    @OnClick(R.id.bt_camera)
    void launchCamera() {

        if (!mPresenter.isDebug()) {
            Answers.getInstance().logCustom(new CustomEvent("Credit Cart Scanning")
                    .putCustomAttribute("Type", "Start"));
        }

        mCardForm.scanCard(getActivity());
    }

    @Override
    public void onCardFormScan() {
        //This callback is called when successful CC scanning

        mCardForm.getCardEditText().setEnabled(false);
        if (!mPresenter.isDebug()) {
            Answers.getInstance().logCustom(new CustomEvent("Credit Cart Scanning")
                    .putCustomAttribute("Type", "Success"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BraintreeRequestCodes.VISA_CHECKOUT) {
            showLoading();
            AppLogger.d("VC_onActivityResult", "Result got back from Visa Checkout SDK");
            String msg = "";

            if (resultCode == Activity.RESULT_OK && data != null) {
                VisaPaymentSummary visaPaymentSummary = data.getParcelableExtra(VisaCheckoutSdk.INTENT_PAYMENT_SUMMARY);
                if (visaPaymentSummary != null) {
                    // Successful VCO
                    showLoading();
                    mActivity.callCreatePaymentTransactionVco(visaPaymentSummary);
                    mPresenter.facebookInitiatedCheckout(PaymentInfo.getPaymentType(), mValue.getItemsCount(), Double.valueOf(mCartTotalCost));
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
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
        }
    }

    @Override
    public void onVisaCheckoutButtonClicked() {
        mVcoPresenter.payWithVisaCheckout(Double.valueOf(mCartTotalCost));
    }
}
