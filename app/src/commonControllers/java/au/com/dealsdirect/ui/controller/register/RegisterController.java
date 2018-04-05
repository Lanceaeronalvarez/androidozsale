package au.com.dealsdirect.ui.controller.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class RegisterController extends BaseController implements RegisterMvpView {

    public static final String TAG = "RegisterController";

    private static final String KEY_TEXT = "RegisterController.KEY_TEXT";

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Inject
    RegisterMvpPresenter<RegisterMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
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

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_register, container, false);

        getControllerComponent().inject(this);
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
        // Setup views here
        //mPresenter.loadSample(new SampleRequest());

        mToolBarTitle.setText(getResources().getString(R.string.register_title));

        mActivity.setDraggableViewPager(false);

        mTermsLink.setOnClickListener(action -> {

            GateKeeper.push(getRouter(),
                    GateKeeper.Destination.LEGALITIES,
                    new BundleBuilder(new Bundle())
                            .putString(BundleKeys.TEMPLATE_KEY, "TermsAndConditions_Text")
                            .putString(BundleKeys.TITLE, "Terms and Conditions")
                            .build(),
                    new HorizontalChangeHandler(false),
                    new HorizontalChangeHandler());
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
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

    @OnClick(R.id.controller_register_sign_up_button)
    void onSignUpClick() {
        if (mTermsCheck.isChecked()) {
            mPresenter.registerUser(
                    mRegisterForenameField.getText().toString(),
                    mRegisterSurnameField.getText().toString(),
                    mRegisterEmailField.getText().toString(),
                    mRegisterPasswordField.getText().toString(),
                    mTermsCheck.isChecked());
        } else {
            onError(R.string.please_accept_terms_and_conditions);
        }
    }

    @OnClick(R.id.controller_register_login_text)
    void onLoginClick() {
        mActivity.onBackPressed();
    }


    @OnClick(R.id.controller_login_fb_layout)
    void onFacebookLoginClick() {
        mPresenter.onFacebookLogin(mActivity, mCallbackManager);
    }


    @Override
    public void showLoginSuccessful(String loginTicket) {
        mActivity.loginSuccessHandler(getRouter(), AppConstants.POP_FLAG.ROOT, AppConstants.AUTH_FLAG.REGISTER);
    }

    @Override
    public void showLoginError(String message) {
        mActivity.loginErrorHandler(message);

    }
}
