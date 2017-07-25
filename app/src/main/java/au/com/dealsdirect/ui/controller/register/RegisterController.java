package au.com.dealsdirect.ui.controller.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
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

    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
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

    @OnClick(R.id.controller_register_sign_up_button)
    void onSignUpClick() {
        mPresenter.registerUser(
                mRegisterForenameField.getText().toString(),
                mRegisterSurnameField.getText().toString(),
                mRegisterEmailField.getText().toString(),
                mRegisterPasswordField.getText().toString());
    }

    @OnClick(R.id.controller_register_login_text)
    void onLoginClick(){
        getActivity().onBackPressed();
    }


    @OnClick(R.id.facebook_login_button)
    void onFacebookLoginClick(){
        mPresenter.onFacebookLogin(getActivity(), mCallbackManager);
    }

    @Override
    public void showRegisterSuccessful(String loginTicket) {
        getActivity().onBackPressed();

        CustomAlertDialog.showCustomAlertDialog(getActivity(),
                CustomAlertDialog.CustomDialogIconState.POSITIVE,"register successful");
        Log.d("Register", "Successful");
    }

    @Override
    public void showRegisterError(String message) {
        Log.d("Register", "Error message = "+message);
        CustomAlertDialog.showCustomAlertDialog(getActivity(),
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,message);
    }

    @Override
    public void showLoginSuccessful(String loginTicket) {
        getActivity().onBackPressed();

    }

    @Override
    public void showLoginError(String message) {

    }
}
