package au.com.dealsdirect.ui.controller.forgotpassword;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordResponseBody;
import au.com.dealsdirect.ui.base.BaseToolBarController;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 07/14/2017.
 */

public class ForgotPasswordController extends SwipeableBaseToolBarController implements ForgotPasswordMvpView {

//    @BindView(R.id.partial_toolbar_arrow_title)
//    TextView mForgotPasswordTitle;
//
//    @BindView(R.id.partial_toolbar_filter_view)
//    ImageButton mForgotPasswordRightOptionView;

    @BindView(R.id.controller_forgot_password_email_edittext)
    EditText mForgotPasswordEmailForm;

    public static final String TAG = "ForgotPasswordController";

    private static final String KEY_TEXT = "ForgotPassword.KEY_TEXT";

    @Inject
    ForgotPasswordMvpPresenter<ForgotPasswordMvpView> mPresenter;

    public static ForgotPasswordController newInstance() {

        return new ForgotPasswordController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ForgotPasswordController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_forgot_password, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        setupSwipingBehavior();
    }

    @Override
    protected void setUp(View view) {
        // Setup views here

        mToolbarTitle.setText(getResources().getText(R.string.forgot_password));
//        mForgotPasswordRightOptionView.setVisibility(View.INVISIBLE);
//        mForgotPasswordTitle.setText(getResources().getText(R.string.forgot_password));
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        hideKeyboard();
        super.onDestroyView(view);
    }

//    @OnClick(R.id.partial_toolbar_arrow_view)
//    void onBackClick(){
//        hideKeyboard();
//        mActivity.onBackPressed();
//    }

    @OnClick(R.id.controller_forgot_password_send_button)
    void onForgotPasswordClick() {

        String inputEmail = mForgotPasswordEmailForm.getText().toString();
        if (inputEmail.isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getResources().getString(R.string.input_email_address));

        } else {

            mPresenter.forgotPassword(inputEmail);
        }

    }

    @Override
    public void showForgotPasswordResponse(ForgotPasswordResponseBody response) {

        if (response.getForgotPasswordResponse().getResult()) {
            hideKeyboard();
            mActivity.onBackPressed();

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.request_sent)
            );

        } else {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    response.getForgotPasswordResponse().getMessage()
            );

        }
    }

    @Override
    public void showForgotPasswordError() {
        //noinspection ConstantConditions
        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                "please try again");
//        mActivity.onBackPressed();
    }
}
