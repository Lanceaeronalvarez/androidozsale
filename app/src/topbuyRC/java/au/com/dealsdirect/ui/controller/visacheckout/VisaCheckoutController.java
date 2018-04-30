package au.com.dealsdirect.ui.controller.visacheckout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.braintreepayments.api.VisaCheckout;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaCheckoutSdkInitListener;
import com.visa.checkout.widget.VisaCheckoutButton;

import java.math.BigInteger;

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
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.ViewUtils;


/**
 * Created by smartwave on 05/04/2018.
 */

public abstract class VisaCheckoutController extends BaseController implements VisaCheckoutMvpView{

    protected VisaCheckoutController(Bundle args) {
        super(args);
    }

    @Inject
    protected VisaCheckoutMvpPresenter<VisaCheckoutMvpView> mVcoPresenter;

    protected VisaCheckoutButton mVisaCheckoutButton;

    private ControllerComponent mControllerComponent;

    private ViewGroup mVisaCheckoutBtnParent;

    private  ViewTreeObserver.OnGlobalLayoutListener mVisaCheckoutOnGlobalLayoutListener;

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        mControllerComponent = DaggerControllerComponent.builder()
                .controllerModule(new ControllerModule(this))
                .activityComponent(((BaseActivity) getActivity()).getActivityComponent())
                .build();
        mControllerComponent.inject(this);
        return super.onCreateView(inflater, container);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        registerForActivityResult(BraintreeRequestCodes.VISA_CHECKOUT);
        mVisaCheckoutButton = (VisaCheckoutButton) view.findViewById(R.id.button_visacheckout);
        if(mVisaCheckoutButton!=null) {
            mVisaCheckoutBtnParent = ((ViewGroup) mVisaCheckoutButton.getParent());
            mVisaCheckoutButton.setCheckoutListener(new VisaCheckoutButton.CheckoutWithVisaListener() {
                @Override
                public void onClick() {
                    onVisaCheckoutButtonClicked();
                }
            });
        }
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);

//      Reattach presenter, global layout listeners
        mVcoPresenter.onAttach(this);
        if(mVisaCheckoutButton!=null) {
            mVisaCheckoutOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mVisaCheckoutButton.setButtonWidth((int) ViewUtils.pxToDp(((ViewGroup) mVisaCheckoutButton.getParent()).getWidth()));
                    mVisaCheckoutButton.requestLayout();
                }
            };

            mVisaCheckoutBtnParent.getViewTreeObserver().addOnGlobalLayoutListener(mVisaCheckoutOnGlobalLayoutListener);
        }
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
//      remove global layout listeners
        if(mVisaCheckoutButton!=null) {
            mVisaCheckoutBtnParent.getViewTreeObserver().removeOnGlobalLayoutListener(mVisaCheckoutOnGlobalLayoutListener);
        }
    }

    @Override
    public void onSetupVisaCheckoutNative(Profile profile) {
        // Initialize SDK
        VisaCheckoutSdk.init(mActivity, profile,
                new VisaCheckoutSdkInitListener() {
                    @Override
                    public void status(int code, String message) {

                        AppLogger.d("VC_Init", "Code:" + code + "  Message:" + message);

                        if (mVisaCheckoutButton != null) {
                            switch (code){
                                case VisaCheckoutSdk.Status.SUCCESS:
                                case VisaCheckoutSdk.Status.SDK_PAUSED:
                                case VisaCheckoutSdk.Status.SDK_RESUMED:
                                    mVisaCheckoutButton.setEnabled(true);
                                    break;
                                default:
                                    mVisaCheckoutButton.setEnabled(false);
                                    break;
                            }
                        }
                    }
                });
    }

    @Override
    public void onSetupVisaCheckoutBraintree(String paymentToken, String paymentType) {
        mActivity.onAuthorizationFetched(paymentToken, paymentType);

        if (mActivity.getBraintreeFragment() != null) {
            VisaCheckout.createProfileBuilder(mActivity.getBraintreeFragment(), new BraintreeResponseListener<Profile.ProfileBuilder>() {
                @Override
                public void onResponse(Profile.ProfileBuilder profileBuilder) {

                    // On success of fetching of profile builder, initialize visa sdk
                    VisaCheckoutSdk.init(mActivity, profileBuilder.build(), new VisaCheckoutSdkInitListener() {
                        @Override
                        public void status(int code, String message) {

                            AppLogger.d("VC_Init", "Code:" + code + "  Message:" + message);

                            if (mVisaCheckoutButton != null) {
                                switch (code) {
                                    case VisaCheckoutSdk.Status.SUCCESS:
                                    case VisaCheckoutSdk.Status.SDK_PAUSED:
                                    case VisaCheckoutSdk.Status.SDK_RESUMED:
                                        mVisaCheckoutButton.setEnabled(true);
                                        break;
                                    default:
                                        mVisaCheckoutButton.setEnabled(false);
                                        break;
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onStartVisaCheckoutIntent(PurchaseInfo purchaseInfo) {
        Intent intent = VisaCheckoutSdk.getCheckoutIntent(mActivity, purchaseInfo);

        // Call result in inherited base class
        startActivityForResult(intent, BraintreeRequestCodes.VISA_CHECKOUT);
    }

    @Override
    public void onStartVisaCheckoutAuthorize(PurchaseInfo.PurchaseInfoBuilder purchaseInfoBuilder) {
        VisaCheckout.authorize(mActivity.getBraintreeFragment(),purchaseInfoBuilder);
    }

    @Override
    public void doAuthenticateLoginWithVisaCheckoutBraintree(VisaCheckoutNonce visaCheckoutNonce) {
        mVcoPresenter.authenticateLoginWithVisaCheckoutBraintree(visaCheckoutNonce);
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

}
