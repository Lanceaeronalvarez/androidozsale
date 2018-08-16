package au.com.dealsdirect.ui.controller.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.braintreepayments.api.VisaCheckout;
import com.braintreepayments.api.interfaces.BraintreeResponseListener;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.braintreepayments.api.models.VisaCheckoutNonce;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.visa.checkout.Profile;
import com.visa.checkout.PurchaseInfo;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaCheckoutSdkInitListener;
import com.visa.checkout.VisaPaymentSummary;
import com.visa.checkout.widget.VisaCheckoutButton;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.base.SwipeableVisaCheckoutController;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.ui.base.VisaCheckoutPresenter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ViewUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;

/*
 * Created by Ayi on 05/06/2017.
 */

public class RegisterController extends SwipeableVisaCheckoutController implements RegisterMvpView,VisaCheckoutMvpView {

    public static final String TAG = "RegisterController";

    private static final String KEY_TEXT = "RegisterController.KEY_TEXT";

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

//    @Inject
//    RegisterMvpPresenter<RegisterMvpView> mPresenter;
//
    @Inject
    RegisterPresenter<RegisterController> mPresenter;
    @Inject
    VisaCheckoutPresenter<VisaCheckoutMvpView> mVcoPresenter;

    @BindView(R.id.controller_register_forename_field)
    TextView mRegisterForenameField;

    @BindView(R.id.controller_register_surname_field)
    TextView mRegisterSurnameField;

    @BindView(R.id.controller_register_email_field)
    TextView mRegisterEmailField;

    @BindView(R.id.controller_register_password_field)
    TextView mRegisterPasswordField;

    @BindView(R.id.controller_register_sign_up_button)
    Button mSignUpButton;

    private VisaPaymentSummary mVisaPaymentSummary;

//    @BindView(R.id.controller_register_terms_conditions_check)
//    CheckBox mTermsCheck;

//    @BindView(R.id.controller_register_terms_link)
//    TextView mTermsLink;

    private static AuthHandler mAuthHandler;

    public static RegisterController newInstance() {

        return new RegisterController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public RegisterController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_register, container, false));

        getControllerComponent().inject(this);
        registerForActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
        registerForActivityResult(BraintreeRequestCodes.VISA_CHECKOUT);
        mCallbackManager = CallbackManager.Factory.create();
        mPresenter.onAttach(this);
        mVcoPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setupSwipingBehavior();
        setUp(view);
    }



    @Override
    protected void setUp(View view) {
        mToolbarTitle.setText("sign up");

        mActivity.setDraggableViewPager(false);

        if(mVcoPresenter.isVisaCheckoutEnabled()){
            mVcoPresenter.setupVisaCheckout();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == BraintreeRequestCodes.VISA_CHECKOUT){
            AppLogger.d("VC_onActivityResult", "Result got back from Visa Checkout SDK");
            String msg = "";

            if (resultCode == Activity.RESULT_OK && data != null) {
                mVisaPaymentSummary = data.getParcelableExtra(VisaCheckoutSdk.INTENT_PAYMENT_SUMMARY);
                if (mVisaPaymentSummary != null) {
                    // Successful VCO
                    mVcoPresenter.authenticateLoginWithVisaCheckoutNative(mVisaPaymentSummary);
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

            if(!msg.isEmpty()) {
                AppLogger.d("VC_onActivityResult", msg);
                onError(msg);
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

//    @OnClick(R.id.controller_register_close_icon)
//    void onCloseIconClick() {
//        getRouter().popToRoot(new VerticalChangeHandler());
//    }

//    @OnClick(R.id.controller_register_back_icon)
//    void onBackIconClick() {
//        hideKeyboard();
//        mActivity.onBackPressed();
//    }

    @OnClick(R.id.controller_register_sign_up_button)
    void onSignUpClick() {
        mPresenter.registerUser(
                mRegisterForenameField.getText().toString(),
                mRegisterSurnameField.getText().toString(),
                mRegisterEmailField.getText().toString(),
                mRegisterPasswordField.getText().toString(),
                true,
                true);

//        if (mTermsCheck.isChecked()) {
//            mPresenter.registerUser(
//                    mRegisterForenameField.getText().toString(),
//                    mRegisterSurnameField.getText().toString(),
//                    mRegisterEmailField.getText().toString(),
//                    mRegisterPasswordField.getText().toString(),
//                    mTermsCheck.isChecked());
//        } else {
//            onError(R.string.please_accept_terms_and_conditions);
//        }
    }

    @OnClick(R.id.controller_register_login_text)
    void onLoginClick() {
        mActivity.onBackPressed();
    }


    @OnClick(R.id.facebook_login_button)
    void onFacebookLoginClick() {
        mPresenter.onFacebookLogin(mActivity, mCallbackManager, 1);
    }


    @Override
    public void showLoginSuccessful(String loginTicket) {
        //Call facebook registration successful analytics
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.ROOT, AppConstants.AUTH_FLAG.REGISTER);
    }

    @Override
    public void showLoginError(String message) {
        mActivity.loginErrorHandler(message);

    }

    @Override
    public void showPasswordVerification(LoginVisa.RequestValue.Data requestData, boolean isAccountExists, String accountEmail) {
        BundleBuilder bundleBuilder = new BundleBuilder(new Bundle());
        bundleBuilder.putBoolean(BundleKeys.KEY_ACCOUNT_EXISTS,isAccountExists);
        bundleBuilder.putString(BundleKeys.KEY_ACCOUNT_EMAIL,accountEmail);
        bundleBuilder.putString(BundleKeys.KEY_LOGIN_VISA_REQUEST_DATA, new Gson().toJson(requestData));

        GateKeeper.push(getRouter(), GateKeeper.Destination.PASSWORD_VERIFICATION,bundleBuilder.build(),new VerticalChangeHandler(), new VerticalChangeHandler());
    }

    @Override
    public void showLoginVisaSuccess(String loginTicket) {
        showLoginSuccessful(loginTicket);
    }

    @Override
    public void onVisaCheckoutButtonClicked() {
        mVcoPresenter.loginWithVisaCheckout();
    }

    @Override
    public void doAuthenticateLoginWithVisaCheckoutBraintree(VisaCheckoutNonce visaCheckoutNonce) {
        mVcoPresenter.authenticateLoginWithVisaCheckoutBraintree(visaCheckoutNonce);
    }

}
