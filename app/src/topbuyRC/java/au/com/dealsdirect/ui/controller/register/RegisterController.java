package au.com.dealsdirect.ui.controller.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class RegisterController extends SwipeableBaseToolBarController implements RegisterMvpView {

    public static final String TAG = "RegisterController";

    private static final String KEY_TEXT = "RegisterController.KEY_TEXT";

    private CallbackManager mCallbackManager = CallbackManager.Factory.create();

    @Inject
    RegisterMvpPresenter<RegisterMvpView> mPresenter;

    @BindView(R.id.controller_register_forename_field)
    TextView mRegisterForenameField;

    @BindView(R.id.controller_register_surname_field)
    TextView mRegisterSurnameField;

    @BindView(R.id.controller_register_email_field)
    TextView mRegisterEmailField;

    @BindView(R.id.controller_register_password_field)
    TextView mRegisterPasswordField;

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

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_register, container, false));

        getControllerComponent().inject(this);
        registerForActivityResult(CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode());
        mCallbackManager = CallbackManager.Factory.create();
        mPresenter.onAttach(this);

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

//        mTermsLink.setOnClickListener(action -> {
//            getRouter().pushController(RouterTransaction.with(
//                    new LegalitiesController("TermsAndConditions_Text", "Terms and Conditions"))
//                    .popChangeHandler(new HorizontalChangeHandler())
//                    .pushChangeHandler(new HorizontalChangeHandler()));
//        });
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
}
