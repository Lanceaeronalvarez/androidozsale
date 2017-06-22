package au.com.dealsdirect.ui.controller.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Paul on 6/20/17.
 */

public class DetailsController extends BaseController implements DetailsMvpView {

    @Inject
    DetailsMvpPresenter<DetailsMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mSaveUserDetailsButton;

    @BindView(R.id.controller_details_text_username)
    EditText mUserNameText;

    @BindView(R.id.controller_details_text_firstname)
    EditText mFirstNameText;

    @BindView(R.id.controller_details_text_lastname)
    EditText mLastNameText;

    @BindView(R.id.controller_details_text_dateofbirth)
    EditText mDateOfBirthText;

    @BindView(R.id.controller_details_text_gender)
    EditText mGenderText;

    @BindView(R.id.controller_details_text_emailaddress)
    EditText mEmailAddressText;

    @BindView(R.id.controller_details_text_password)
    EditText mPasswordText;

    @BindView(R.id.controller_details_text_newpassword)
    EditText mNewPasswordText;

    @BindView(R.id.controller_details_text_confirmpassword)
    EditText mConfirmPasswordText;

    private GetUserDetailsResponse mUserDetailsResponse;


    public DetailsController(Bundle args){
        super(args);
    }

    public static DetailsController newInstance() {

        return new DetailsController(new BundleBuilder(new Bundle()).build());
    }


    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_user_details, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mSaveUserDetailsButton.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_check));
        mTitleTextView.setText("My Details");
        mPresenter.loadUser(0);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void loadDetails(GetUserDetailsResponse userDetailsResponse) {
        if (userDetailsResponse.d.isAuthenticated()) {
            mUserDetailsResponse = userDetailsResponse;
            mUserNameText.setText(userDetailsResponse.d.Value.getUsername());
            mFirstNameText.setText(userDetailsResponse.d.Value.getForename());
            mLastNameText.setText(userDetailsResponse.d.Value.getSurname());
            mEmailAddressText.setText(userDetailsResponse.d.Value.getEmail());
            if(userDetailsResponse.d.Value.getDateOfBirth() != null) mDateOfBirthText
                    .setText(userDetailsResponse.d.Value.getDateOfBirth().toString());
        }

    }

    @OnClick(R.id.partial_toolbar_filter_view)
    public void saveUserDetails(){
        String username = mUserNameText.getText().toString();
        String firstname = mFirstNameText.getText().toString();
        String lastname = mLastNameText.getText().toString();
        boolean gender = true;
        String dateofbirth = mDateOfBirthText.getText().toString();
        String email = mEmailAddressText.getText().toString();
        String password = mPasswordText.getText().toString();
        String newpassword = mNewPasswordText.getText().toString();
        String confirmpassword = mConfirmPasswordText.getText().toString();

        mPresenter.sendUserDetails(username, firstname, lastname, dateofbirth, gender, email,
                password, newpassword, confirmpassword);
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    public void onBackClick() {
        getActivity().onBackPressed();
    }

}
