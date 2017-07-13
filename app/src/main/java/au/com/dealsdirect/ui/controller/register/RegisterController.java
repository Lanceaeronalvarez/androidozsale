package au.com.dealsdirect.ui.controller.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class RegisterController extends BaseController implements RegisterMvpView {

    public static final String TAG = "RegisterController";

    private static final String KEY_TEXT = "RegisterController.KEY_TEXT";

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

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_register, container, false);

        getControllerComponent().inject(this);

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

    @Override
    public void showRegisterSuccessful(String loginTicket) {
        getActivity().onBackPressed();
        Log.d("Register", "Successful");
    }

    @Override
    public void showRegisterError(String message) {
        Log.d("Register", "Error");
    }
}
