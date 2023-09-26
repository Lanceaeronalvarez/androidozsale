package au.com.dealsdirect.ui.controller.visacheckout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import androidx.annotation.NonNull;

//import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.visa.checkout.ManualCheckoutSession;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaCheckoutSdkInitListener;
import com.visa.checkout.VisaPaymentSummary;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.di.component.ControllerComponent;
import au.com.dealsdirect.di.component.DaggerControllerComponent;
import au.com.dealsdirect.di.module.ControllerModule;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.utils.AppLogger;

public abstract class VisaCheckoutController extends BaseController implements VisaCheckoutMvpView {

    protected VisaCheckoutController(Bundle args) {
        super(args);
    }

    @Inject
    protected VisaCheckoutMvpPresenter<VisaCheckoutMvpView> mVcoPresenter;
/*
    protected Button mVcoButton;

    private ControllerComponent mControllerComponent;

    private ViewGroup mVisaCheckoutBtnParent;

    private ViewTreeObserver.OnGlobalLayoutListener mVisaCheckoutOnGlobalLayoutListener;

    private Profile mProfile;

    public boolean isProcessingVco = false;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        mControllerComponent = DaggerControllerComponent.builder()
                .controllerModule(new ControllerModule(this, mActivity))
                .activityComponent(((BaseActivity) getActivity()).getActivityComponent())
                .build();
        mControllerComponent.inject(this);
        return super.onCreateView(inflater, container);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        //registerForActivityResult(BraintreeRequestCodes.VISA_CHECKOUT);
        mVcoButton = (Button) view.findViewById(R.id.button_visa_checkout);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

//      Reattach presenter, global layout listeners
        mVcoPresenter.onAttach(this);
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    private void initSdk(Profile profile) {
        // Initialize SDK
        VisaCheckoutSdk.init(mActivity, profile, new VisaCheckoutSdkInitListener() {
            @Override
            public void status(int code, String message) {

                AppLogger.d("VC_Init", "Code:" + code + "  Message:" + message);

                if (mVcoButton != null) {
                    switch (code) {
                        case VisaCheckoutSdk.Status.SUCCESS:
                        case VisaCheckoutSdk.Status.SDK_PAUSED:
                        case VisaCheckoutSdk.Status.SDK_RESUMED:
                            mVcoButton.setEnabled(true);
                            break;
                        default:
                            mVcoButton.setEnabled(false);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onSetupVisaCheckoutNative(Profile profile) {
        initSdk(profile);
        mProfile = profile;
    }

    @Override
    public void onSetupVisaCheckoutBraintree(String paymentToken, String paymentType, boolean isFromCheckout) {
        initializeBrainTree(paymentToken, paymentType);

        if (mActivity.getBraintreeClient() != null) {
            mActivity.getBraintreeClient().getVisaCheckoutHandler().createProfile(
                    mActivity.getResources().getString(R.string.app_name), profile -> {
                        initSdk(profile);
                        mProfile = profile;
                    });
        }
    }

    @Override
    public void onStartVisaCheckoutIntent(PurchaseInfo purchaseInfo) {
        Intent intent = VisaCheckoutSdk.getCheckoutIntent(mActivity, purchaseInfo);

        // Call result in inherited base class
        //startActivityForResult(intent, BraintreeRequestCodes.VISA_CHECKOUT);
    }

    public void initializeVisaCheckoutButton(PurchaseInfo.PurchaseInfoBuilder purchaseInfoBuilder,
                                             boolean fromCheckout) {

        if (mProfile == null && mActivity.getBraintreeClient() != null) {
            mActivity.getBraintreeClient().getVisaCheckoutHandler().createProfile(
                    mActivity.getResources().getString(R.string.app_name), profile -> {
                        initSdk(profile);
                        mProfile = profile;
                    });
        }

        VisaCheckoutSdk.initManualCheckoutSession(mActivity, mProfile, purchaseInfoBuilder.build(),
                new ManualCheckoutSession() {
                    @Override
                    public void onReady(ManualCheckoutLaunchHandler manualCheckoutLaunchHandler) {

                        if (isProcessingVco) {
                            return;
                        }

                        isProcessingVco = true;
                        manualCheckoutLaunchHandler.launch();
                    }

                    @Override
                    public void onResult(VisaPaymentSummary visaPaymentSummary) {
                        switch (visaPaymentSummary.getStatusName()) {
                            case VisaPaymentSummary.PAYMENT_CANCEL:
                                // The customer canceled the Visa Checkout flow
                                break;
                            case VisaPaymentSummary.PAYMENT_SUCCESS:
                                if (mActivity.getBraintreeClient() != null) {
                                    mActivity.getBraintreeClient().getVisaCheckoutHandler().tokenize(visaPaymentSummary);
                                }
                                break;
                            case VisaPaymentSummary.PAYMENT_ERROR:
                                break;
                            case VisaPaymentSummary.PAYMENT_FAILURE:
                                break;
                            default:
                                // There was an issue processing Visa Checkout
                                break;
                        }

                    }
                });

    }

    @Override
    public void doAuthenticateLoginWithVisaCheckoutBraintree(String firstname, String lastName, String email, String callId, String paymentNonce) {
        mVcoPresenter.authenticateLoginWithVisaCheckoutBraintree(firstname, lastName, email, callId, paymentNonce);
    }

    @Override
    public void showPasswordVerification(LoginVisa.RequestValue.Data requestData, boolean isAccountExists, String accountEmail) {

    }

    @Override
    public void showLoginVisaSuccess(String loginTicket) {

    }

    @Override
    public void onVisaCheckoutButtonClicked() {

    }

    @Override
    public void setVisaCheckoutActionType(int visaCheckoutActionType) {
        mActivity.setVisaCheckoutActionType(visaCheckoutActionType);
    }

    @Override
    public void initializeBrainTree(String token, String paymentType) {
        mActivity.onAuthorizationFetched(token, paymentType);
    }

 */
}
