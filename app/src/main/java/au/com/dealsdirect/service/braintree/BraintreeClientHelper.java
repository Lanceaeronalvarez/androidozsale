package au.com.dealsdirect.service.braintree;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.BraintreeClient;
import com.braintreepayments.api.BraintreeRequestCodes;
import com.braintreepayments.api.BrowserSwitchResult;
import com.braintreepayments.api.CardClient;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPalAccountNonce;
import com.braintreepayments.api.PayPalCheckoutRequest;
import com.braintreepayments.api.PayPalClient;
import com.braintreepayments.api.PayPalFlowStartedCallback;
import com.braintreepayments.api.PayPalPaymentIntent;
import com.braintreepayments.api.PayPalVaultRequest;
import com.braintreepayments.api.ThreeDSecureClient;
import com.braintreepayments.api.ThreeDSecureRequest;
import com.braintreepayments.api.ThreeDSecureResult;

import javax.annotation.Nullable;

import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.PaymentInfo;

public class BraintreeClientHelper {

    private AppCompatActivity activity = null;

    private BraintreeClient mBraintreeClient = null;
    private ThreeDSecureClient mThreeDSecureClient = null;
    private ThreeDSecureVerificationHandler mThreeDSecureVerificationHandler = null;
    private PayPalClient mPayPalClient = null;
    private PaypalResultHandler mPaypalResultHandler = null;
    private CardClient mCardClient = null;
    private DataCollector mDataCollector = null;
    private String currencyCode = "";

    private final PaymentHandler paymentHandler = new PaymentHandler(this);

    public void activityOnResume() {
        final BrowserSwitchResult browserSwitchResult = mBraintreeClient.deliverBrowserSwitchResult(activity);
        if (browserSwitchResult != null) {
            switch (browserSwitchResult.getRequestCode()) {
                case BraintreeRequestCodes.THREE_D_SECURE:
                    mThreeDSecureClient.onBrowserSwitchResult(browserSwitchResult, this::handleThreeDSecureResult);
                    break;
                case BraintreeRequestCodes.PAYPAL:
                    mPayPalClient.onBrowserSwitchResult(browserSwitchResult, this::handlePaypalResult);
                    break;
                default:
                    break;
            }
        }
    }

    public void activityOnActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case BraintreeRequestCodes.THREE_D_SECURE:
                mThreeDSecureClient.onActivityResult(resultCode, data, this::handleThreeDSecureResult);
                break;
            case BraintreeRequestCodes.PAYPAL:
                // not supported?
                break;
            default:
                break;
        }
    }

    private void handleThreeDSecureResult(ThreeDSecureResult threeDSecureResult, Exception error) {
        if (threeDSecureResult != null && threeDSecureResult.getTokenizedCard() != null) {
            final String nonce = threeDSecureResult.getTokenizedCard().getString();
            if (mThreeDSecureVerificationHandler != null) {
                mThreeDSecureVerificationHandler.onGettingNonce(nonce);
            }
        } else {
            onError(error);
            if (mThreeDSecureVerificationHandler != null) {
                mThreeDSecureVerificationHandler.onError(error);
            }
        }
    }

    private void handlePaypalResult(PayPalAccountNonce payPalAccountNonce, Exception error) {
        if (payPalAccountNonce != null) {
            String nonce = payPalAccountNonce.getString();
            if (mPaypalResultHandler != null) {
                mPaypalResultHandler.onGettingNonce(nonce);
            }
        } else {
            onError(error);
            if (mPaypalResultHandler != null) {
                mPaypalResultHandler.onError(error);
            }
        }
    }

    public void onError(Exception error) {
        CustomAlertDialog.showCustomAlertDialog(activity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, error.getLocalizedMessage());
    }

    public void collectDeviceData(CollectDeviceDataHandler handler) {
        mDataCollector.collectDeviceData(activity, (deviceData, error) -> {
            if (handler != null) {
                handler.afterCollectDeviceData(deviceData);
            }
        });
    }

    public void reset() {
        mBraintreeClient = null;
        mThreeDSecureClient = null;
        mPayPalClient = null;
        mCardClient = null;
        mDataCollector = null;
    }

    public void initialize(AppCompatActivity activity, String authorization) {
        this.activity = activity;
        mBraintreeClient = new BraintreeClient(activity, authorization);
        mThreeDSecureClient = new ThreeDSecureClient(activity, mBraintreeClient);
        mPayPalClient = new PayPalClient(activity, mBraintreeClient);
        mCardClient = new CardClient(mBraintreeClient);
        mDataCollector = new DataCollector(mBraintreeClient);
    }

    public boolean isInitialized() {
        return mBraintreeClient != null &&
                mThreeDSecureClient != null &&
                mPayPalClient != null &&
                mCardClient != null &&
                mDataCollector != null;
    }

    public void threeDSecurePerformVerification(String nonce, String amount, ThreeDSecureVerificationHandler handler) {
        ThreeDSecureRequest request = new ThreeDSecureRequest();
        request.setAmount(amount);
        request.setNonce(nonce);
        mThreeDSecureVerificationHandler = handler;
        mThreeDSecureClient.performVerification(activity, request, this::handleThreeDSecureResult);
    }

    private void tokenizePayPalAccountWithCheckout(String totalCost) {
        PayPalCheckoutRequest request = new PayPalCheckoutRequest(totalCost);
        request.setCurrencyCode(currencyCode);
        request.setIntent(PayPalPaymentIntent.AUTHORIZE);

        mPayPalClient.tokenizePayPalAccount(activity, request);
    }

    private void tokenizePayPalAccountWithVault() {
        PayPalVaultRequest request = new PayPalVaultRequest();
        request.setShouldOfferCredit(true);
        request.setBillingAgreementDescription("test");

        mPayPalClient.tokenizePayPalAccount(activity, request);
    }

    public PaymentHandler getPaymentHandler() {
        return paymentHandler;
    }

    public interface ThreeDSecureVerificationHandler {
        void onGettingNonce(String nonce);

        void onError(Exception error);
    }

    public interface PaypalResultHandler {
        void onGettingNonce(String nonce);

        void onError(Exception error);
    }

    public interface CollectDeviceDataHandler {
        void afterCollectDeviceData(String deviceData);
    }

    public static class PaymentHandler {
        private final BraintreeClientHelper parent;

        private PaymentHandler(BraintreeClientHelper parent) {
            this.parent = parent;
        }

        public void startPaypalPayment() {
            if (parent.mPayPalClient == null) {
                return;
            }

            parent.tokenizePayPalAccountWithVault();
        }

        public void startPaypalCreditPayment(String totalCost) {
            if (parent.mPayPalClient == null) {
                return;
            }

            parent.tokenizePayPalAccountWithCheckout(totalCost);
        }
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}
