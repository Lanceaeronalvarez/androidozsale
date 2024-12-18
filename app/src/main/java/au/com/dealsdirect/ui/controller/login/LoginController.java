package au.com.dealsdirect.ui.controller.login;
/*
 * Created by CodeineBot on 6/15/17.
 */

import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.LoginType.FACEBOOK;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.LoginType.FORGOT_PASSWORD;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.LoginType.LOGIN;
import static au.com.dealsdirect.service.datacollection.core.DataCollector.EventParameters.LoginType.NO_ACTION;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;

import java.util.HashMap;
import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordController;
import au.com.dealsdirect.ui.controller.register.RegisterController;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import au.com.dealsdirect.utils.recaptcha.ReCaptchaWebHelper;
import butterknife.BindView;
import butterknife.OnClick;

public class LoginController extends BaseController implements LoginMvpView {

    private static final boolean SHOULD_SHOW_LEGALITIES = false;

    public static final String TAG = "LoginController";
    public static final String AUTH_HANDLER = "AUTH_HANDLER";

    @Inject
    LoginMvpPresenter<LoginMvpView> mPresenter;

    @BindView(R.id.toolbar_title_login)
    Toolbar mToolbar;
    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.controller_login_email_edittext)
    EditText mEmailEditText;
    @BindView(R.id.controller_login_password_edittext)
    EditText mPasswordEditText;
    @BindView(R.id.controller_login_button)
    Button mLoginButton;
    @BindView(R.id.controller_login_signup_text)
    TextView mSignUpTextView;
    @BindView(R.id.controller_login_forgot_password_text)
    TextView mForgotPasswordTextView;
    @BindView(R.id.partial_toolbar_left_view)
    View mLeftButton;

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

    @BindView(R.id.login_base_container)
    ViewGroup mBaseContainer;

    private String mLoginMethod = NO_ACTION;
    private boolean isLoginSuccess = false;

    private CallbackManager mCallbackManager;

    private ReCaptchaWebHelper reCaptchaWebHelper = null;

    public static LoginController newInstance() {
        return new LoginController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);


    public LoginController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        if (mPresenter.isTablet()) {
            mActivity.getMainController().setNavigationBarEnabled(false);
        } else {
            mActivity.getMainController().hideBottomNav();
        }

        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        if (mPresenter.isTablet()) {
            mActivity.getMainController().setNavigationBarEnabled(false);
        } else {
            mActivity.getMainController().hideBottomNav();
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_login, container, false);
        getControllerComponent().inject(this);

        // Init facebook callback
        registerForActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
        mCallbackManager = CallbackManager.Factory.create();
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
        boolean shouldToolbarBeVisible = mActivity.getResources().getBoolean(R.bool.login_toolbar_visibility);

        mToolbar.setVisibility(shouldToolbarBeVisible ? View.VISIBLE : View.GONE);
        mToolbarTitle.setText(mActivity.getResources().getString(R.string.login_title));

        mLoginButton.setOnClickListener(view1 -> {
            startLogin();
        });

        if (mLegalitiesContainer != null && SHOULD_SHOW_LEGALITIES) {
            mAboutUsTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_ABOUT_US, getString(R.string.account_about_us)));
            mTncTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_TNC, getString(R.string.account_tnc)));
            mPrivacyTextView.setOnClickListener(v -> onLegalitiesClicked(BundleKeys.TEMPLATE_KEY_PRIVACY, getString(R.string.account_privacy)));
        }

        mAboutUsTextView.setVisibility(SHOULD_SHOW_LEGALITIES ? View.VISIBLE : View.INVISIBLE);
        mTncTextView.setVisibility(SHOULD_SHOW_LEGALITIES ? View.VISIBLE : View.INVISIBLE);
        mPrivacyTextView.setVisibility(SHOULD_SHOW_LEGALITIES ? View.VISIBLE : View.INVISIBLE);
    }

    private void startLogin() {
        if (mEmailEditText.getText().toString().isEmpty() ||
                mPasswordEditText.getText().toString().isEmpty()) {
            mActivity.loginErrorHandler(getResources().getString(R.string.please_fill_up_all_the_fields));
        } else {
            callLoginApi();
        }
    }


    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick({R.id.partial_toolbar_right_view, R.id.controller_login_close_icon})
    void onCloseIconClick() {
        mActivity.onBackPressed();
    }

    @Override
    public void showLoginStart() {
        mLoginButton.setEnabled(false);
    }

    @Override
    public void showLoginSuccessful(String loginTicket, boolean isFacebookLogin) {
        isLoginSuccess = true;
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.BACK, AppConstants.AUTH_FLAG.LOGIN);
    }

    @Override
    public boolean handleBack() {
        if (reCaptchaWebHelper != null) {
            reCaptchaWebHelper.onBackPressed();
            reCaptchaWebHelper = null;
            return true;
        }

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.METHOD, mLoginMethod);
        parameters.put(DataCollector.EventParameters.RESULT, isLoginSuccess);
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, LoginController.class.getSimpleName());
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        DataCollector.logEvent(Events.Login, parameters);

        if (mPresenter.isTablet()) {
            mActivity.getMainController().setNavigationBarEnabled(true);
        } else {
            mActivity.getMainController().showBottomNav();
        }

        mActivity.getMainController().goToPreviousContainerFromLogin(mActivity.isAuthorized());

        if (mPresenter.isTablet() && getBoolean(R.bool.master_detail_enabled)) {
            return true;
        }

        if (!isLoginSuccess) {
            mActivity.cancelAuthHandlers();
        }


        hideKeyboard();

        return super.handleBack();
    }

    @Override
    public void showLoginError(String message, boolean isFacebookLogin) {
        mLoginButton.setEnabled(true);
        mActivity.loginErrorHandler(message);
    }

    @Override
    public void showRegistration() {
        getRouter().replaceTopController(RouterTransaction.with(RegisterController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void showForgotPassword() {
        mLoginMethod = FORGOT_PASSWORD;
        getRouter().pushController(RouterTransaction.with(ForgotPasswordController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void callLoginApi() {
        hideKeyboard();

        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        reCaptchaWebHelper = new ReCaptchaWebHelper();
        reCaptchaWebHelper.initiateReCaptchaV2Challenge(
                mBaseContainer,
                mActivity.getResources().getString(R.string.recaptcha_site_key),
                token -> {
                    mPresenter.loginViaEmail(email, password, token);
                    reCaptchaWebHelper = null;
                });
    }

    @OnClick(R.id.controller_login_signup_text)
    void onSignUpClick() {
        mLoginMethod = LOGIN;
        showRegistration();
    }

    @OnClick(R.id.controller_login_fb_layout)
    void onFacebookButtonClick() {
        mLoginMethod = FACEBOOK;
        mPresenter.onFacebookLogin(mActivity, mCallbackManager, 0, false, false);
    }

    @OnClick(R.id.controller_login_forgot_password_text)
    void onForgotPasswordClick() {
        showForgotPassword();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackPress() {
        mActivity.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

    @OnClick(R.id.controller_login_email_container)
    public void onClickEmailContainer() {
        KeyboardUtils.showSoftInput(mEmailEditText, mActivity);
    }

    @OnClick(R.id.controller_login_password_container)
    public void onClickPasswordContainer() {
        KeyboardUtils.showSoftInput(mPasswordEditText, mActivity);
    }
}
