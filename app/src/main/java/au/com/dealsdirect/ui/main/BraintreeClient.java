package au.com.dealsdirect.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.DataCollector;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.ThreeDSecure;
import com.braintreepayments.api.VisaCheckout;
import com.braintreepayments.api.exceptions.AuthenticationException;
import com.braintreepayments.api.exceptions.AuthorizationException;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ConfigurationException;
import com.braintreepayments.api.exceptions.DownForMaintenanceException;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.exceptions.ServerException;
import com.braintreepayments.api.exceptions.UnexpectedException;
import com.braintreepayments.api.exceptions.UpgradeRequiredException;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.visa.checkout.Profile;
import com.visa.checkout.VisaPaymentSummary;

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BraintreeUtils;

public class BraintreeClient implements BrainTreeListeners {

    private AppCompatActivity activity = null;

    private BraintreeFragment mBraintreeFragment = null;

    private BraintreeClientListener listener = null;

    private final VisaCheckoutHandler visaCheckoutHandler = new VisaCheckoutHandler(this);

    private final PaymentHandler paymentHandler = new PaymentHandler(this);

    @Override
    public void onCancel(int requestCode) {
        PaymentInfo.setThreeDSecureCalled(false);
        if (listener != null) {
            listener.hideLoading();
        }
    }

    @Override
    public void onError(Exception error) {
        if (!(error instanceof ErrorWithResponse)) {

            if (mBraintreeFragment != null) {
                if (error instanceof AuthenticationException || error instanceof AuthorizationException ||
                        error instanceof UpgradeRequiredException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_DEV_ERROR);
                } else if (error instanceof ConfigurationException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_CONFIG_ERROR);
                } else if (error instanceof ServerException || error instanceof UnexpectedException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_SERVER_ERROR);
                } else if (error instanceof DownForMaintenanceException) {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_SERVER_UNAVAILABLE);
                } else {
                    mBraintreeFragment.sendAnalyticsEvent(BraintreeUtils.SDK_ERROR);
                }

                //Call braintree client reset on error
                if (listener != null) {
                    listener.performResetWithAuthFetch();
                }
            }
        } else {
            if (listener != null) {
                listener.hideLoading();
            }
            CustomAlertDialog.showCustomAlertDialog(activity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getBrainTreeErrorMessage(((ErrorWithResponse) error)));
        }
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        if (listener != null) {
            final BraintreeClientListener.VisaCheckoutNonceDetails details;
            if (paymentMethodNonce instanceof VisaCheckoutNonce) {
                final VisaCheckoutNonce visaCheckoutNonce = (VisaCheckoutNonce) paymentMethodNonce;
                details = new BraintreeClientListener.VisaCheckoutNonceDetails() {
                    @Override
                    public boolean isVisaCheckoutNonce() {
                        return true;
                    }

                    @Override
                    public String getFirstname() {
                        return visaCheckoutNonce.getUserData().getUserFirstName();
                    }

                    @Override
                    public String getLastname() {
                        return visaCheckoutNonce.getUserData().getUserLastName();
                    }

                    @Override
                    public String getEmail() {
                        return visaCheckoutNonce.getUserData().getUserEmail();
                    }

                    @Override
                    public String getCallId() {
                        return visaCheckoutNonce.getCallId();
                    }
                };
            } else {
                details = new BraintreeClientListener.VisaCheckoutNonceDetails() {
                    @Override
                    public boolean isVisaCheckoutNonce() {
                        return false;
                    }

                    @Override
                    public String getFirstname() {
                        return null;
                    }

                    @Override
                    public String getLastname() {
                        return null;
                    }

                    @Override
                    public String getEmail() {
                        return null;
                    }

                    @Override
                    public String getCallId() {
                        return null;
                    }
                };
            }
            listener.onPaymentNonceCreated(paymentMethodNonce.getNonce(), details);
        }
    }

    public void collectDeviceData(String kountMerchantId, BraintreeClientCollectDeviceDataHandler handler) {
        if (kountMerchantId != null && !kountMerchantId.isEmpty()) {
            DataCollector.collectDeviceData(mBraintreeFragment, kountMerchantId, s -> {
                if (handler != null) {
                    handler.onHandle(s);
                }
            });
        } else {
            DataCollector.collectDeviceData(mBraintreeFragment, s -> {
                if (handler != null) {
                    handler.onHandle(s);
                }
            });
        }
    }

    public void reset() {
        // TODO
    }

    public BraintreeFragment getFragment() {
        return mBraintreeFragment;
    }

    public void initialize(AppCompatActivity activity, String authorization) {
        this.activity = activity;

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(activity, authorization);

        } catch (InvalidArgumentException e) {
            onError(e);
        }
    }

    public boolean isInitialized() {
        return mBraintreeFragment != null;
    }

    private String getBrainTreeErrorMessage(ErrorWithResponse error) {
        String errMessage = activity.getResources().getString(R.string.an_error_has_occurred);

        if (!(error == null)) {
            if (error.getFieldErrors() != null && !error.getFieldErrors().isEmpty()) {
                BraintreeError err = error.getFieldErrors().get(0);
                List<BraintreeError> fieldErrors = err.getFieldErrors();
                while (fieldErrors != null && !fieldErrors.isEmpty()) {
                    if (fieldErrors.get(0) != null) {
                        err = fieldErrors.get(0);
                        fieldErrors = err.getFieldErrors();
                    } else {
                        break;
                    }
                }
                errMessage = err.getMessage();
            } else if (!error.getMessage().isEmpty()) {
                errMessage = error.getMessage();
            }
        }

        return errMessage;
    }

    public void ThreeDSecurePerformVerification(String nonce, String amount) {
        ThreeDSecure.performVerification(getFragment(), nonce, amount);
    }

    public void setListener(BraintreeClientListener listener) {
        this.listener = listener;
    }

    public PaymentHandler getPaymentHandler() {
        return paymentHandler;
    }

    public VisaCheckoutHandler getVisaCheckoutHandler() {
        return visaCheckoutHandler;
    }

    public interface BraintreeClientListener {
        void hideLoading();

        void onPaymentNonceCreated(String paymentMethodNonce, VisaCheckoutNonceDetails visaCheckoutNonceDetails);

        void performResetWithAuthFetch();

        interface VisaCheckoutNonceDetails {
            boolean isVisaCheckoutNonce();

            String getFirstname();

            String getLastname();

            String getEmail();

            String getCallId();
        }
    }

    public interface BraintreeClientCollectDeviceDataHandler {
        void onHandle(String s);
    }

    public static class PaymentHandler {
        private final BraintreeClient parent;

        private PaymentHandler(BraintreeClient parent) {
            this.parent = parent;
        }

        public void startPaypalPayment() {
            if (parent.getFragment() == null) {
                return;
            }
            PayPalRequest request = new PayPalRequest();
            PayPal.requestBillingAgreement(parent.getFragment(), request);
        }

        public void startPaypalCreditPayment(String totalCost) {
            if (parent.getFragment() == null) {
                return;
            }
            PayPalRequest request = new PayPalRequest(totalCost)
                    .offerCredit(true); // Offer PayPal Credit
            PayPal.requestOneTimePayment(parent.getFragment(), request);
        }
    }

    public static class VisaCheckoutHandler {
        private final BraintreeClient parent;

        private VisaCheckoutHandler(BraintreeClient parent) {
            this.parent = parent;
        }

        public void tokenize(VisaPaymentSummary visaPaymentSummary) {
            VisaCheckout.tokenize(parent.getFragment(), visaPaymentSummary);
        }

        public void createProfile(String displayName, CreateProfileListener listener) {
            VisaCheckout.createProfileBuilder(parent.getFragment(), profileBuilder -> {
                profileBuilder.setDisplayName(displayName);
                if (listener != null) {
                    listener.onResponse(profileBuilder.build());
                }
            });
        }

        public interface CreateProfileListener {
            void onResponse(Profile profile);
        }
    }
}
