package au.com.dealsdirect.ui.controller.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.facebook.CallbackManager;
import com.facebook.internal.CallbackManagerImpl;
import com.mysale.genie.utility.RxBus;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.IntrospectionUtils;
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
    private MainActivity mActivity;

    public static RegisterController newInstance() {

        return new RegisterController(
                new BundleBuilder(new Bundle())
                        .build());
    }


    public static RegisterController newInstance(AuthHandler authHandler) {
        mAuthHandler = authHandler;
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
        mActivity = (MainActivity) getActivity();
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
        ((MainActivity) getActivity()).setDraggableViewPager(false);

        mTermsLink.setOnClickListener(action -> {
            getRouter().pushController(RouterTransaction.with(
                    new LegalitiesController("TermsAndConditions_Text", "Terms and Conditions"))
                    .popChangeHandler(new HorizontalChangeHandler())
                    .pushChangeHandler(new HorizontalChangeHandler()));
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

    @OnClick(R.id.controller_register_close_icon)
    void onCloseIconClick() {
        getRouter().popToRoot(new VerticalChangeHandler());
    }

    @OnClick(R.id.controller_register_back_icon)
    void onBackIconClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.controller_register_sign_up_button)
    void onSignUpClick() {
        mPresenter.registerUser(
                mRegisterForenameField.getText().toString(),
                mRegisterSurnameField.getText().toString(),
                mRegisterEmailField.getText().toString(),
                mRegisterPasswordField.getText().toString(),
                mTermsCheck.isChecked());
    }

    @OnClick(R.id.controller_register_login_text)
    void onLoginClick() {
        getActivity().onBackPressed();
    }


    @OnClick(R.id.facebook_login_button)
    void onFacebookLoginClick() {
        mPresenter.onFacebookLogin(getActivity(), mCallbackManager);
    }



    @Override
    public void showLoginSuccessful(String loginTicket) {
        RxBus.instance().post(IntrospectionUtils.EVENT_LOGIN);

        getRouter().popToRoot();

        if (mAuthHandler!=null)
            mAuthHandler.success();

        hideKeyboard();
        mActivity.getMainController().getHomeController().getPresenter().callGetBasketItemsQuantity();
        mActivity.getMainController().showBottomNav();
    }

    @Override
    public void showLoginError(String message) {
        if (mAuthHandler != null)
            mAuthHandler.error();

        CustomAlertDialog.showCustomAlertDialog(
                getActivity(),
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                message);

    }
}
