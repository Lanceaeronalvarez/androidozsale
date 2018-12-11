package au.com.dealsdirect.ui.controller.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.braintreepayments.api.models.BraintreeRequestCodes;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.google.gson.Gson;
import com.visa.checkout.VisaCheckoutSdk;
import com.visa.checkout.VisaPaymentSummary;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.data.pref.AppPreferencesHelper;
import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.ui.controller.visacheckout.VisaCheckoutController;
import au.com.dealsdirect.ui.custom.toggleswitch.CustomToggleSwitch;
import au.com.dealsdirect.ui.main.MainPresenter;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/*
 * Created by Ayi on 05/06/2017.
 */

public class RegisterController extends VisaCheckoutController implements RegisterMvpView {

    public static final String TAG = "RegisterController";

    private static final String KEY_TEXT = "RegisterController.KEY_TEXT";

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Inject
    RegisterMvpPresenter<RegisterMvpView> mPresenter;

    @Inject
    VisaCheckoutMvpPresenter<VisaCheckoutMvpView> mVcoPresenter;

    @BindView(R.id.controller_register_consent_switch_layout)
    ViewGroup mConsentSwitchesRootLayout;

    @BindView(R.id.partial_toolbar_title)
    TextView mToolBarTitle;

    @BindView(R.id.controller_register_forename_field)
    TextView mRegisterForenameField;

    @BindView(R.id.controller_register_surname_field)
    TextView mRegisterSurnameField;

    @BindView(R.id.controller_register_email_field)
    TextView mRegisterEmailField;

    @BindView(R.id.controller_register_password_field)
    TextView mRegisterPasswordField;

    @BindView(R.id.controller_register_terms_conditions_check)
    CheckBox mTermsCheck;

    @BindView(R.id.controller_register_terms_link)
    TextView mTermsLink;

    @BindView(R.id.controller_register_sign_up_button)
    Button mSignUpButton;

    @Nullable
    @BindView(R.id.controller_register_tnc_toggle)
    CustomToggleSwitch mTermsToggle;

    @Nullable
    @BindView(R.id.controller_register_tnc_text)
    TextView mTermsText;

    @Nullable
    @BindView(R.id.controller_register_emails_toggle)
    CustomToggleSwitch mEmailsToggle;

    @Nullable
    @BindView(R.id.controller_register_emails_text)
    TextView mEmailsText;

    @Nullable
    @BindView(R.id.controller_login_legalities_container)
    ViewGroup mLegalitiesContainer;

    @Nullable
    @BindView(R.id.controller_login_about_us_textview)
    TextView mAboutUsTextView;

    @Nullable
    @BindView(R.id.controller_login_tnc_textview)
    TextView mTncTextView;

    @Nullable
    @BindView(R.id.controller_login_privacy_textview)
    TextView mPrivacyTextView;

    @Nullable
    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mLeftButton;

    private boolean isRegisterSuccess = false;

    public static RegisterController newInstance() {

        return new RegisterController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public RegisterController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_register, container, false);

        getControllerComponent().inject(this);
        registerForActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
        registerForActivityResult(BraintreeRequestCodes.VISA_CHECKOUT);
        mCallbackManager = CallbackManager.Factory.create();
        mVcoPresenter.onAttach(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        //mPresenter.loadSample(new SampleRequest());

        mToolBarTitle.setText(getResources().getString(R.string.register_title));

        mConsentSwitchesRootLayout.setVisibility(mPresenter.isGdprDisabled() ? View.GONE : View.VISIBLE);

        mActivity.setDraggableViewPager(false);

        if (mTermsLink != null) {
            mTermsLink.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC, getString(R.string.account_tnc)));
        }
        mSignUpButton.setOnClickListener(v -> onSignUpClicked());

        if (mVcoPresenter.isVisaCheckoutEnabled()) {
            mVcoPresenter.setupVisaCheckout();
        }

        if (mLegalitiesContainer != null) {
            mAboutUsTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_ABOUT_US, getString(R.string.account_about_us)));
            mTncTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC, getString(R.string.account_tnc)));
            mPrivacyTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_PRIVACY, getString(R.string.account_privacy)));
        }

        /* GDPR split type registration */
        if (mTermsText != null) {
            mTermsText.setText(Html.fromHtml(mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_TERMS_TEXT)));
            mTermsText.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC,
                    getString(R.string.account_tnc)));
        }

        if (mEmailsText != null) {
            mEmailsText.setText(Html.fromHtml(mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_EMAILS_TEXT)));
        }

        if (mTermsToggle != null && mPresenter.getGdprIsChecked(AppPreferencesHelper.CONSENT_TNC_CHECKED)) {
            mTermsToggle.setCheckedTogglePosition(0);
        }

        if (mEmailsToggle != null && mPresenter.getGdprIsChecked(AppPreferencesHelper.CONSENT_EMAILS_CHECKED)) {
            mEmailsToggle.setCheckedTogglePosition(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == BraintreeRequestCodes.VISA_CHECKOUT) {
            AppLogger.d("VC_onActivityResult", "Result got back from Visa Checkout SDK");
            String msg = "";

            if (resultCode == Activity.RESULT_OK && data != null) {
                VisaPaymentSummary visaPaymentSummary = data.getParcelableExtra(VisaCheckoutSdk.INTENT_PAYMENT_SUMMARY);
                if (visaPaymentSummary != null) {
                    // Successful VCO
                    showLoading();
                    mVcoPresenter.authenticateLoginWithVisaCheckoutNative(visaPaymentSummary);
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
                AppLogger.d("VC_onActivityResult", msg);
                onError(msg);
            }
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (!mPresenter.isTablet()) {
            mActivity.getMainController().hideBottomNav();
        }
        super.onAttach(view);
    }

    @Override
    public void onDestroyView(View view) {
//        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void onCloseIconClick() {
        getRouter().popToRoot(new VerticalChangeHandler());
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackArrowClick() {
        onBackIconClick();
    }

    @OnClick(R.id.controller_register_back_icon)
    void onBackIconClick() {
        hideKeyboard();
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_register_login_text)
    void onLoginClick() {
        mActivity.onBackPressed();
    }


    @OnClick(R.id.controller_login_fb_layout)
    void onFacebookLoginClick() {
        if ((mTermsCheck != null && !mTermsCheck.isChecked())) {

            String templateTextError = mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_TERMS_WARNING);

            if ((templateTextError == null || templateTextError.equals(""))
                    && !mPresenter.isGdprDisabled()) {
                onError(R.string.please_accept_terms_and_conditions);
            } else {
                onError(templateTextError);
            }

        } else {
            mPresenter.onFacebookLogin(mActivity, mCallbackManager, 1);
        }
    }

    @Override
    public void showLoginSuccessful(String loginTicket, boolean isFacebookLogin) {
        isRegisterSuccess = true;
        mPresenter.setIsNewUser(true);
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.ROOT, AppConstants.AUTH_FLAG.REGISTER);
    }

    @Override
    public void showLoginError(String message, boolean isFacebookLogin) {
        mActivity.loginErrorHandler(message);
        mSignUpButton.setEnabled(true);
    }

    @Override
    public void showPasswordVerification(LoginVisa.RequestValue.Data requestData, boolean isAccountExists, String accountEmail) {
        BundleBuilder bundleBuilder = new BundleBuilder(new Bundle());
        bundleBuilder.putBoolean(BundleKeys.KEY_ACCOUNT_EXISTS, isAccountExists);
        bundleBuilder.putString(BundleKeys.KEY_ACCOUNT_EMAIL, accountEmail);
        bundleBuilder.putString(BundleKeys.KEY_LOGIN_VISA_REQUEST_DATA, new Gson().toJson(requestData));

        GateKeeper.push(getRouter(), GateKeeper.Destination.PASSWORD_VERIFICATION, bundleBuilder.build(), new HorizontalChangeHandler(), new HorizontalChangeHandler());
    }


    @Override
    public void showLoginVisaSuccess(String loginTicket) {
        isRegisterSuccess = true;
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.ROOT, AppConstants.AUTH_FLAG.REGISTER);
    }

    @Override
    public void onVisaCheckoutButtonClicked() {
        mVcoPresenter.loginWithVisaCheckout();
    }

    private void onLegalitiesClicked(String key, String title) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, key)
                .putString(BundleKeys.LEGALITIES_TITLE, title)
                .build();

        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LEGALITIES,
                bundle,
                new HorizontalChangeHandler(false),
                new HorizontalChangeHandler());
    }

    private boolean isFormEmpty() {
        return mRegisterForenameField.getText().toString().isEmpty() &&
                mRegisterSurnameField.getText().toString().isEmpty() &&
                mRegisterEmailField.getText().toString().isEmpty() &&
                mRegisterPasswordField.getText().toString().isEmpty();
    }

    private void onSignUpClicked() {
        if ((mTermsCheck != null && !mTermsCheck.isChecked())) {

            String templateTextError = mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_TERMS_WARNING);

            if ((templateTextError == null || templateTextError.equals(""))
                    && !mPresenter.isGdprDisabled()) {
                onError(R.string.please_accept_terms_and_conditions);
            } else {
                onError(templateTextError);
            }

        } else if (isFormEmpty()) {
            onError(R.string.please_fill_out_the_form);
        } else {

            boolean tncAccepted = (mTermsCheck != null && mTermsCheck.isChecked());

            mPresenter.registerUser(
                    mRegisterForenameField.getText().toString(),
                    mRegisterSurnameField.getText().toString(),
                    mRegisterEmailField.getText().toString(),
                    mRegisterPasswordField.getText().toString(),
                    tncAccepted,
                    true);
        }
    }
}
