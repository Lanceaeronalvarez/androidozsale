package au.com.dealsdirect.ui.controller.register;

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.RegisterMethod.FACEBOOK;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.RegisterMethod.NO_ACTION;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.RegisterMethod.REGISTRATION;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
//import com.visa.checkout.VisaCheckoutSdk;


import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.BuildConfig;
import au.com.dealsdirect.R;
import au.com.dealsdirect.data.pref.AppPreferencesHelper;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpPresenter;
import au.com.dealsdirect.ui.base.VisaCheckoutMvpView;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.custom.toggleswitch.CustomToggleSwitch;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BackChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import au.com.dealsdirect.utils.recaptcha.ReCaptchaWebHelper;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class RegisterController extends BaseController implements RegisterMvpView {

    private static final boolean SHOULD_SHOW_LEGALITIES = false;

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

    @BindView(R.id.controller_register_forename_container)
    TextInputLayout mRegisterForenameContainer;

    @BindView(R.id.controller_register_forename_field)
    EditText mRegisterForenameField;

    @BindView(R.id.controller_register_surname_container)
    TextInputLayout mRegisterSurnameContainer;

    @BindView(R.id.controller_register_surname_field)
    EditText mRegisterSurnameField;

    @BindView(R.id.controller_register_email_field)
    EditText mRegisterEmailField;

    @BindView(R.id.controller_register_password_field)
    EditText mRegisterPasswordField;

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
    TextView mLeftButton;

    @BindView(R.id.button_visa_checkout)
    Button mVcoButton;

    @BindView(R.id.register_base_container)
    ViewGroup mBaseContainer;

    @BindView(R.id.register_voucher_text)
    TextView mRegisterVoucher;

    @BindView(R.id.controller_login_fb_layout)
    RelativeLayout mfbLoginButton;

    private String mRegisterMethod = NO_ACTION;
    private boolean isRegisterSuccess = false;

    private ReCaptchaWebHelper reCaptchaWebHelper = null;

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
//        registerForActivityResult(BraintreeRequestCodes.VISA_CHECKOUT);
        mCallbackManager = CallbackManager.Factory.create();
//        mVcoPresenter.onAttach(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (mPresenter.isTablet()) {
            mActivity.getMainController().setNavigationBarEnabled(false);
        } else {
            mActivity.getMainController().hideBottomNav(true);
        }
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        //mPresenter.loadSample(new SampleRequest());

        mToolBarTitle.setText(getResources().getString(R.string.register_title));
//        mVcoButton.setVisibility(mVcoPresenter.isVisaCheckoutEnabled() ? View.VISIBLE :
//                View.GONE);
        mVcoButton.setVisibility(View.GONE);

        mfbLoginButton.setVisibility(mPresenter.isFacebookLoginEnabled() ? View.VISIBLE : View.GONE);

        if(getResources().getBoolean(R.bool.is_registration_voucher_visible)){
            mRegisterVoucher.setVisibility(View.VISIBLE);
        }

        if (getResources().getBoolean(R.bool.is_ozsale_app)) {
            if (mLeftButton != null) {
                mLeftButton.setVisibility(View.VISIBLE);
                mLeftButton.setText(getResources().getString(R.string.myaccount_log_in));
            }
        }

        mConsentSwitchesRootLayout.setVisibility(mPresenter.isGdprDisabled() ? View.GONE : View.VISIBLE);

        if (mTermsLink != null) {
            mTermsLink.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC, getString(R.string.account_tnc)));
        }
        mSignUpButton.setOnClickListener(v -> onSignUpClicked());

//        if (mVcoPresenter.isVisaCheckoutEnabled()) {
//            mVcoPresenter.setupVisaCheckout(false);
//        }

        if (mLegalitiesContainer != null && SHOULD_SHOW_LEGALITIES) {
            mAboutUsTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_ABOUT_US, getString(R.string.account_about_us)));
            mTncTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC, getString(R.string.account_tnc)));
            mPrivacyTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_PRIVACY, getString(R.string.account_privacy)));
        }

        mAboutUsTextView.setVisibility(SHOULD_SHOW_LEGALITIES ? View.VISIBLE : View.INVISIBLE);
        mTncTextView.setVisibility(SHOULD_SHOW_LEGALITIES ? View.VISIBLE : View.INVISIBLE);
        mPrivacyTextView.setVisibility(SHOULD_SHOW_LEGALITIES ? View.VISIBLE : View.INVISIBLE);

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

        mRegisterForenameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasSpecialCharactersFirstName()) {
                    mRegisterForenameContainer.setError(getString(R.string.special_character_error));
                } else if (isNameDuplicate()) {
                    mRegisterForenameContainer.setError(getString(R.string.duplicate_name_error));
                } else {
                    mRegisterForenameContainer.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mRegisterSurnameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasSpecialCharactersLastName()) {
                    mRegisterSurnameContainer.setError(getString(R.string.special_character_error));
                } else if (isNameDuplicate()) {
                    mRegisterSurnameContainer.setError(getString(R.string.duplicate_name_error));
                } else {
                    mRegisterSurnameContainer.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /*mVcoButton.setOnClickListener(action -> {
            onVisaCheckoutButtonClicked();
        });*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        /*if (requestCode == BraintreeRequestCodes.VISA_CHECKOUT) {
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
                AppLogger.d("VC_onActivityResult", msg);
                onError(msg);
            }
        }*/

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (!mPresenter.isTablet()) {
            mActivity.getMainController().hideBottomNav(true);
        }
        super.onAttach(view);
    }

    @Override
    public void onDestroyView(View view) {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.METHOD, mRegisterMethod);
        parameters.put(DataCollector.EventParameters.RESULT, isRegisterSuccess);
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, RegisterController.class.getSimpleName());
        DataCollector.logEvent(Events.SignUp, parameters);
        super.onDestroyView(view);
    }

    @Override
    public boolean handleBack() {
        if (reCaptchaWebHelper != null) {
            reCaptchaWebHelper.onBackPressed();
            reCaptchaWebHelper = null;
            return true;
        }

        if (!isRegisterSuccess) {
            mActivity.cancelAuthHandlers();
        }

        if (mPresenter.isTablet()) {
            mActivity.getMainController().setNavigationBarEnabled(true);
        } else {
            mActivity.getMainController().showBottomNav(true);
        }
        mActivity.getMainController().goToPreviousContainerFromLogin(mActivity.isAuthorized());

        return super.handleBack();
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void onCloseIconClick() {
        hideKeyboard();
        mActivity.onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackArrowClick() {
        onBackIconClick();
    }

    @OnClick(R.id.controller_register_back_icon)
    void onBackIconClick() {
        hideKeyboard();
        getRouter().replaceTopController(RouterTransaction.with(LoginController.newInstance())
                .pushChangeHandler(new BackChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));

    }

    @OnClick(R.id.controller_register_login_text)
    void onLoginClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_login_fb_layout)
    void onFacebookLoginClick() {
        mRegisterMethod = FACEBOOK;
        if ((mTermsCheck != null && !mTermsCheck.isChecked()) ||
                (mTermsToggle != null && mTermsToggle.getCheckedTogglePosition() != 0)) {

            String templateTextError = mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_TERMS_WARNING);

            if (templateTextError == null || templateTextError.equals("")) {
                onError(R.string.please_accept_terms_and_conditions);
            } else {
                onError(templateTextError);
            }

        } else if (mEmailsToggle != null && mEmailsToggle.getCheckedTogglePosition() == -1) {
            onError(R.string.please_select_an_option_for_promotional_emails);
        } else {
            boolean tncAccepted = (mTermsCheck != null && mTermsCheck.isChecked()) ||
                    (mTermsToggle != null && mTermsToggle.getCheckedTogglePosition() == 0);

            boolean emailsAccepted = mEmailsToggle != null && mEmailsToggle.getCheckedTogglePosition() == 0;

            mPresenter.onFacebookLogin(mActivity, mCallbackManager, 1, tncAccepted, emailsAccepted);
        }
    }


    @Override
    public void showLoginSuccessful(String loginTicket, boolean isFacebookLogin) {
        isRegisterSuccess = true;
        mPresenter.setIsNewUser(true);
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.BACK, AppConstants.AUTH_FLAG.REGISTER);
    }

    @Override
    public void showLoginError(String message, boolean isFacebookLogin) {
        mActivity.loginErrorHandler(message);
        if (isViewAttached() && isAttached() && mSignUpButton != null) {
            mSignUpButton.setEnabled(true);
        }
    }

//    @Override
//    public void showPasswordVerification(LoginVisa.RequestValue.Data requestData, boolean isAccountExists, String accountEmail) {
//        BundleBuilder bundleBuilder = new BundleBuilder(new Bundle());
//        bundleBuilder.putBoolean(BundleKeys.KEY_ACCOUNT_EXISTS, isAccountExists);
//        bundleBuilder.putString(BundleKeys.KEY_ACCOUNT_EMAIL, accountEmail);
//        bundleBuilder.putString(BundleKeys.KEY_LOGIN_VISA_REQUEST_DATA, new Gson().toJson(requestData));
//
//        GateKeeper.push(getRouter(), GateKeeper.Destination.PASSWORD_VERIFICATION, bundleBuilder.build(), new HorizontalChangeHandler(), new HorizontalChangeHandler());
//    }


//    @Override
//    public void showLoginVisaSuccess(String loginTicket) {
//        isRegisterSuccess = true;
//        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.ROOT, AppConstants.AUTH_FLAG.REGISTER);
//    }

//    @Override
//    public void onVisaCheckoutButtonClicked() {
//        mRegisterMethod = VCO;
//
//        if (isProcessingVco) {
//            isProcessingVco = false;
//        }
//
//        if ((mTermsCheck != null && !mTermsCheck.isChecked()) ||
//                (mTermsToggle != null && mTermsToggle.getCheckedTogglePosition() != 0)) {
//
//            String templateTextError = mPresenter.getGdprTemplateTexts(
//                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_TERMS_WARNING);
//
//            if (templateTextError == null || templateTextError.equals("")) {
//                onError(R.string.please_accept_terms_and_conditions);
//            } else {
//                onError(templateTextError);
//            }
//
//        } else if (mEmailsToggle != null && mEmailsToggle.getCheckedTogglePosition() == -1) {
//            onError(R.string.please_select_an_option_for_promotional_emails);
//        } else {
//            mVcoPresenter.loginWithVisaCheckout();
//        }
//    }

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

    private boolean hasSpecialCharactersFirstName() {
        //English only; minimum of 3 characters; No duplicate of names;
        return !mRegisterForenameField.getText().toString().trim().matches("[a-zA-Z ]+") ||
                mRegisterForenameField.getText().toString().trim().length() < 2;
    }

    private boolean hasSpecialCharactersLastName() {
        return !mRegisterSurnameField.getText().toString().trim().matches("[a-zA-Z ]+") ||
                mRegisterSurnameField.getText().toString().trim().length() < 2;
    }

    private boolean isNameDuplicate() {
        return mRegisterForenameField.getText().toString().trim().equalsIgnoreCase(mRegisterSurnameField.getText().toString().trim());
    }

    private void onSignUpClicked() {
        mRegisterMethod = REGISTRATION;
        if ((mTermsCheck != null && !mTermsCheck.isChecked()) ||
                (mTermsToggle != null && mTermsToggle.getCheckedTogglePosition() != 0)) {

            String templateTextError = mPresenter.getGdprTemplateTexts(
                    AppPreferencesHelper.CONSENT_WITH_REGISTRATION_TERMS_WARNING);

            if (templateTextError == null || templateTextError.equals("")) {
                onError(R.string.please_accept_terms_and_conditions);
            } else {
                onError(templateTextError);
            }

        } else if (mEmailsToggle != null && mEmailsToggle.getCheckedTogglePosition() == -1) {
            onError(R.string.please_select_an_option_for_promotional_emails);
        } else if (isFormEmpty()) {
            onError(R.string.please_fill_out_the_form);
        } else {

            boolean tncAccepted = (mTermsCheck != null && mTermsCheck.isChecked()) ||
                    (mTermsToggle != null && mTermsToggle.getCheckedTogglePosition() == 0);

            boolean emailsAccepted = mEmailsToggle != null && mEmailsToggle.getCheckedTogglePosition() == 0;

            hideKeyboard();

            reCaptchaWebHelper = new ReCaptchaWebHelper();
            reCaptchaWebHelper.initiateReCaptchaV2Challenge(
                    mBaseContainer,
                    mActivity.getResources().getString(R.string.recaptcha_site_key),
                    token -> {
                        mPresenter.registerUser(
                                mRegisterForenameField.getText().toString(),
                                mRegisterSurnameField.getText().toString(),
                                mRegisterEmailField.getText().toString(),
                                mRegisterPasswordField.getText().toString(),
                                tncAccepted,
                                emailsAccepted,
                                token);
                        reCaptchaWebHelper = null;
                    });
        }
    }

    @OnClick(R.id.controller_register_email_entry_container)
    public void onEmailEntryContainerClick() {
        KeyboardUtils.showSoftInput(mRegisterEmailField, mActivity);
    }

    @OnClick(R.id.controller_register_forename_entry_container)
    public void onForenameEntryContainerClick() {
        KeyboardUtils.showSoftInput(mRegisterForenameField, mActivity);
    }

    @OnClick(R.id.controller_register_surname_entry_container)
    public void onSurnameEntryContainerClick() {
        KeyboardUtils.showSoftInput(mRegisterSurnameField, mActivity);
    }

    @OnClick(R.id.controller_register_password_entry_container)
    public void onPasswordEntryContainerClick() {
        KeyboardUtils.showSoftInput(mRegisterPasswordField, mActivity);
    }
}
