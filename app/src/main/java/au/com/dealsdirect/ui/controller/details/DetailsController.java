package au.com.dealsdirect.ui.controller.details;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetEmailSubscriptionTemplatesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.userdetails.UpdateUserEmailSubscriptionRequest;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.account.AccountDeletionConfirmationDialog;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.transitions.CustomSpinnerAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class DetailsController extends BasePullToRefreshController implements DetailsMvpView {

    private final static boolean SHOULD_SHOW_SUCCESS_DIALOGS = false;
    private final static String UNSUBSCRIBE_PREFERENCE_KEY = "Unsubscribe";
    private final static String UNSUBSCRIBE_PREFERENCE_TEXT = "Unsubscribe.";

    @Inject
    DetailsMvpPresenter<DetailsMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mSaveUserDetailsButton;

    @BindView(R.id.controller_details_text_firstname)
    EditText mFirstNameText;

    @BindView(R.id.controller_details_text_lastname)
    EditText mLastNameText;

    @BindView(R.id.controller_details_text_birthday)
    EditText mDateOfBirthText;

    @BindView(R.id.controller_details_spinner_gender)
    Spinner mGenderSpinner;

    @BindView(R.id.controller_details_text_emailaddress)
    EditText mEmailAddressText;

    @BindView(R.id.controller_details_text_password)
    EditText mPasswordText;

    @BindView(R.id.controller_details_text_new_password)
    EditText mNewPasswordText;

    @BindView(R.id.controller_details_text_confirm_password)
    EditText mConfirmPasswordText;

    @BindView(R.id.controller_details_promo_emails_container)
    ViewGroup emailSubscriptionPreferencesContainerView;

    @BindView(R.id.controller_details_promo_emails_header)
    TextView emailSubscriptionPreferencesHeaderTextView;

    @BindView(R.id.controller_details_promo_emails_radio_group)
    RadioGroup emailSubscriptionPreferencesRadioGroup;

    private final List<RadioButton> emailSubscriptionPreferencesRadioButtonsList = new ArrayList<>();
    private final Map<String, RadioButton> emailSubscriptionPreferencesRadioButtonsMap = new HashMap<>();
    private final Map<Integer, String> emailSubscriptionPreferencesRadioButtonsIdMap = new HashMap<>();

    @BindView(R.id.controller_account_deletion_button)
    View mAccountDeletionButton;


    private Date dateOfBirth = null;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat uiDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String userDetailsId = null;
    private GetUserDetailsResponse currentUserDetails = null;

    private String emailSubscriptionPreference = null;
    private Date emailSubscriptionPreferenceDate = null;

    private GetEmailSubscriptionTemplatesResponse emailSubscriptionTemplates = null;

    private final RadioGroup.OnCheckedChangeListener emailSubscriptionPreferenceRadioGroupChangeListener = (group, checkedId) -> {
        emailSubscriptionPreference = emailSubscriptionPreferencesRadioButtonsIdMap.get(checkedId);
        emailSubscriptionPreferenceDate = Calendar.getInstance().getTime();
    };

    public DetailsController(Bundle args) {
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
        View view = super.inflateView(inflater, container);

        setToolBarVisible(getResource().getBoolean(R.bool.details_toolbar_visibility));
        fillContent(inflater.inflate(R.layout.controller_user_details, container, false));

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
        mSaveUserDetailsButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));
        mSaveUserDetailsButton.setVisibility(getBoolean(R.bool.is_ozsale_app) ? View.INVISIBLE : View.VISIBLE);
        mTitleTextView.setText(getString(R.string.account_details));
        mToolbarLeftView.setVisibility(mPresenter.isTablet() && getBoolean(R.bool.master_detail_enabled) ? View.INVISIBLE : View.VISIBLE);

        if (getBoolean(R.bool.is_gender_enabled)) {
            List<String> list = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.genders)));
            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(mActivity,
                    R.layout.row_custom_spinner_drop_down,
                    list);
            mGenderSpinner.setAdapter(customSpinnerAdapter);
        }

        onDateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateOfBirth = calendar.getTime();
            updateDateOfBirthField();
        };

        mDateOfBirthText.setOnClickListener(v -> {
            final Calendar calender = Calendar.getInstance();
            if (dateOfBirth != null) {
                calender.setTime(dateOfBirth);
            }
            new DatePickerDialog(mActivity, R.style.DatePickerTheme, onDateSetListener,
                    calender.get(Calendar.YEAR), calender.get(Calendar.MONTH),
                    calender.get(Calendar.DAY_OF_MONTH)).show();
        });

        mAccountDeletionButton.setVisibility(View.GONE);
        mAccountDeletionButton.setOnClickListener(v -> accountDeletionButtonPressed());

        userDetailsId = null;

        if (emailSubscriptionPreferencesRadioGroup.getChildCount() > 0) {
            for (int i = 0; i < emailSubscriptionPreferencesRadioGroup.getChildCount(); i++) {
                final RadioButton radioButton = (RadioButton) emailSubscriptionPreferencesRadioGroup.getChildAt(i);
                emailSubscriptionPreferencesRadioButtonsList.add(radioButton);
                radioButton.setVisibility(View.GONE);
            }
        }

        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        showLoading();
        mPresenter.getEmailSubscriptionTemplates();
        mPresenter.loadUser(setUserDetailsRequest);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void loadDetails(GetUserDetailsResponse userDetailsResponse) {
        currentUserDetails = userDetailsResponse;

        mFirstNameText.setText(userDetailsResponse.getForename());
        mLastNameText.setText(userDetailsResponse.getSurname());
        mEmailAddressText.setText(userDetailsResponse.getEmail());

        dateOfBirth = getDateFromServerDateString(userDetailsResponse.getDateOfBirth());
        updateDateOfBirthField();

        int genderItem = 0;
        if (!userDetailsResponse.getGender()) {
            genderItem = 1;
        }

        if (getBoolean(R.bool.is_gender_enabled)) {
            mGenderSpinner.setSelection(genderItem);
        }

        userDetailsId = userDetailsResponse.getID();
        mAccountDeletionButton.setVisibility(userDetailsId != null && !userDetailsId.isEmpty() ? View.VISIBLE : View.GONE);
        emailSubscriptionPreference = userDetailsResponse.getReceiveInvitations() ?
                userDetailsResponse.getMemberPreference() : UNSUBSCRIBE_PREFERENCE_KEY;
        emailSubscriptionPreferenceDate = getDateFromServerDateString(userDetailsResponse.getPreferenceDate());

        updateEmailSubscriptionPreferenceRadioGroupSelection();
    }

    @Override
    public void saveUserDetailsSuccess() {
        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                "User details is saved");
    }

    @Override
    public void saveUserDetailsFailed(String message) {
        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                message);
    }

    @Override
    public void onSaveReceiveSales(SaveReceiveSalesResponse saveReceiveSalesResponse) {
        if (currentUserDetails == null) {
            currentUserDetails = new GetUserDetailsResponse();
        }
        String firstname = getFieldValue(mFirstNameText);
        String lastname = getFieldValue(mLastNameText);
        boolean gender = getFieldValueOfGender();
        String email = getFieldValue(mEmailAddressText);
        String dateOfBirth = getFieldValueOfDateOfBirth();

        currentUserDetails.setForename(firstname);
        currentUserDetails.setSurname(lastname);
        currentUserDetails.setDateOfBirth(dateOfBirth);
        currentUserDetails.setEmail(email);
        currentUserDetails.setGender(gender);

        clearPasswordFields();

        if (isEmailSubscriptionPreferenceChanged()) {
            updateEmailSubscriptionPreference(mEmailAddressText.getText().toString());
        } else if (SHOULD_SHOW_SUCCESS_DIALOGS) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, "Changes Saved");
        }
    }

    @Override
    public void onUpdateEmailSubscriptionPreference() {
        if (currentUserDetails == null) {
            return;
        }
        currentUserDetails.setMemberPreference(emailSubscriptionPreference);
        currentUserDetails.setPreferenceDate(serverDateFormat.format(emailSubscriptionPreferenceDate));

        if (SHOULD_SHOW_SUCCESS_DIALOGS) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, "Changes Saved");
        }
    }

    @Override
    public void onGetEmailSubscriptionTemplates(GetEmailSubscriptionTemplatesResponse response) {
        emailSubscriptionTemplates = response;
        if (response == null) {
            emailSubscriptionPreferencesContainerView.setVisibility(View.GONE);
            return;
        }
        new Handler(mActivity.getMainLooper()).post(() -> {
            emailSubscriptionPreferencesContainerView.setVisibility(View.VISIBLE);

            // Set Header Text
            emailSubscriptionPreferencesHeaderTextView.setText(response.getTitle());

            // Inflate/Hide RadioButtons according to Options size
            final int optionsSizeDifference = response.getOptions().size() - emailSubscriptionPreferencesRadioButtonsList.size();
            if (optionsSizeDifference > 0) {
                for (int i = 0; i < optionsSizeDifference; i++) {
                    addNewEmailSubscriptionRadioButton();
                }
            }
            if (optionsSizeDifference < 0) {
                for (int i = optionsSizeDifference; i < 0; i++) {
                    final RadioButton radioButton = emailSubscriptionPreferencesRadioButtonsList.get(
                            emailSubscriptionPreferencesRadioButtonsList.size() + i);
                    radioButton.setVisibility(View.GONE);
                }
            }

            // Setup RadioButtons
            emailSubscriptionPreferencesRadioButtonsMap.clear();
            emailSubscriptionPreferencesRadioButtonsIdMap.clear();
            emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(null);
            for (int i = 0; i < response.getOptions().size(); i++) {
                RadioButton radioButton = emailSubscriptionPreferencesRadioButtonsList.get(i);
                GetEmailSubscriptionTemplatesResponse.Option option = response.getOptions().get(i);

                radioButton.setVisibility(View.VISIBLE);
                radioButton.setText(Html.fromHtml(option.getText()));
                radioButton.setChecked(false);
                emailSubscriptionPreferencesRadioButtonsMap.put(option.getPreference(), radioButton);
                emailSubscriptionPreferencesRadioButtonsIdMap.put(radioButton.getId(), option.getPreference());
            }
            emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(emailSubscriptionPreferenceRadioGroupChangeListener);

            updateEmailSubscriptionPreferenceRadioGroupSelection();
            addUnsubcribeOption();
        });
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void saveUserDetails() {
        hideKeyboard();

        if (currentUserDetails == null ||
                (mPresenter.isTablet() && !mActivity.isAuthorized()) ||
                (getBoolean(R.bool.master_detail_enabled) && !mActivity.isAuthorized())) {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    getString(R.string.controller_user_details_login_prompt));
            return;
        }

        if (isThereAnyChangesInUserDetails()) {
            if (mPasswordText.getText().toString().isEmpty() || mPasswordText.getText().toString() == "") {
                CustomAlertDialog.showCustomAlertDialog(mActivity,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getString(R.string.controller_user_details_enter_password));
                return;
            }

            String firstname = getFieldValue(mFirstNameText);
            String lastname = getFieldValue(mLastNameText);
            boolean gender = getFieldValueOfGender();
            String email = getFieldValue(mEmailAddressText);
            String dateOfBirth = getFieldValueOfDateOfBirth();
            String password = getFieldValue(mConfirmPasswordText);
            String newpassword = getFieldValue(mNewPasswordText);
            String confirmpassword = getFieldValue(mConfirmPasswordText);

            if (newpassword.isEmpty()) {
                mPresenter.sendUserDetails(createUserDetailRequest(email, firstname, lastname, dateOfBirth,
                        gender, email, password, "", ""));
            } else {
                if (newpassword.equals(confirmpassword)) {
                    mPresenter.sendUserDetails(createUserDetailRequest(email, firstname, lastname, dateOfBirth,
                            gender, email, password, newpassword, confirmpassword));
                } else {
                    CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, mActivity.getString(R.string.password_does_not_match));
                }
            }
        } else if (isEmailSubscriptionPreferenceChanged()) {
            updateEmailSubscriptionPreference(currentUserDetails.getEmail());
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        hideKeyboard();
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_details_button)
    public void saveChanges() {
        saveUserDetails();
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();

        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        userDetailsId = null;
        mPresenter.loadUser(setUserDetailsRequest);
    }

    private void updateDateOfBirthField() {
        mDateOfBirthText.setText(dateOfBirth == null ? null : uiDateFormat.format(dateOfBirth));
    }

    public SetUserDetailsRequest createUserDetailRequest(String userName, String firstName, String lastName,
                                                         String dateOfBirth, boolean gender, String email,
                                                         String password, String newPassword, String confirmPassword) {
        SetUserDetailsRequest userDetailsRequest = new SetUserDetailsRequest();
        userDetailsRequest.setUserName(userName);
        userDetailsRequest.setFirstname(firstName);
        userDetailsRequest.setSurname(lastName);
        userDetailsRequest.setDateBirth(dateOfBirth);
        userDetailsRequest.setGender(gender);
        userDetailsRequest.setEmail(email);
        userDetailsRequest.setPassword(password);
        userDetailsRequest.setNewPassword(newPassword);
        userDetailsRequest.setConfirmPassword(confirmPassword);

        return userDetailsRequest;
    }

    private void accountDeletionButtonPressed() {
        if (userDetailsId != null) { // button should not be visible when null?
            mActivity.showAccountDeletionConfirmationDialog(result -> {
                if (result == AccountDeletionConfirmationDialog.Result.CONFIRMED) {
                    mPresenter.accountDeletion(userDetailsId);
                    mActivity.callLogout(null);
                    getRouter().popCurrentController();
                }
            });
        }
    }

    private boolean isThereAnyChangesInUserDetails() {
        if (currentUserDetails == null) {
            return true;
        }

        String firstname = getFieldValue(mFirstNameText);
        String lastname = getFieldValue(mLastNameText);
        boolean gender = getFieldValueOfGender();
        String email = getFieldValue(mEmailAddressText);
        String newpassword = getFieldValue(mNewPasswordText);

        String currentFirstname = currentUserDetails.getForename();
        String currentSurname = currentUserDetails.getSurname();
        String currentEmail = currentUserDetails.getEmail();

        Date currentDateOfBirth = getDateFromServerDateString(currentUserDetails.getDateOfBirth());

        boolean currentGender = currentUserDetails.getGender();

        return !currentFirstname.equals(firstname) ||
                !currentSurname.equals(lastname) ||
                !currentEmail.equals(email) ||
                (currentGender != gender && getBoolean(R.bool.is_gender_enabled)) ||
                (currentDateOfBirth == null && dateOfBirth != null) ||
                (currentDateOfBirth != null && currentDateOfBirth.equals(dateOfBirth)) ||
                !newpassword.isEmpty();
    }

    private boolean isEmailSubscriptionPreferenceChanged() {
        return currentUserDetails != null &&
                emailSubscriptionPreferenceDate != null &&
                !emailSubscriptionPreferenceDate.equals(getDateFromServerDateString(currentUserDetails.getPreferenceDate()));
    }

    private void updateEmailSubscriptionPreference(String email) {
        UpdateUserEmailSubscriptionRequest request = new UpdateUserEmailSubscriptionRequest();
        request.setEmail(email);
        request.setPreference(emailSubscriptionPreference);
        request.setStatus(!emailSubscriptionPreference.equalsIgnoreCase(UNSUBSCRIBE_PREFERENCE_KEY));

        mPresenter.updateEmailSubscriptionPreference(request);
    }

    private String getFieldValue(TextView textView) {
        return textView.getText().toString();
    }

    private String getFieldValueOfDateOfBirth() {
        return dateOfBirth == null ? null : serverDateFormat.format(dateOfBirth);
    }

    private boolean getFieldValueOfGender() {
        return getBoolean(R.bool.is_gender_enabled) && mGenderSpinner.getSelectedItem().toString().equals("Male");
    }

    private void clearPasswordFields() {
        mConfirmPasswordText.setText("");
        mPasswordText.setText("");
        mNewPasswordText.setText("");
    }

    private Date getDateFromServerDateString(String source) {
        try {
            return serverDateFormat.parse(source);
        } catch (Exception e) {
            return null;
        }
    }

    private RadioButton addNewEmailSubscriptionRadioButton() {
        // The following seems to add a RadioButton into the RadioGroup immediately
        LayoutInflater.from(emailSubscriptionPreferencesRadioGroup.getContext()).inflate(
                R.layout.controller_user_details_radio_button,
                emailSubscriptionPreferencesRadioGroup);

        final RadioButton newRadioButton = (RadioButton) emailSubscriptionPreferencesRadioGroup.getChildAt(
                emailSubscriptionPreferencesRadioGroup.getChildCount() - 1);
        emailSubscriptionPreferencesRadioButtonsList.add(newRadioButton);
        return newRadioButton;
    }

    private void addUnsubcribeOption() {
        for (String key : emailSubscriptionPreferencesRadioButtonsMap.keySet()) {
            if (key.equalsIgnoreCase(UNSUBSCRIBE_PREFERENCE_KEY)) {
                return; // the radio button already exists
            }
        }

        RadioButton radioButton;
        if (emailSubscriptionPreferencesRadioButtonsList.size() + 1 < emailSubscriptionTemplates.getOptions().size()) {
            radioButton = emailSubscriptionPreferencesRadioButtonsList.get(emailSubscriptionTemplates.getOptions().size());
        } else {
            radioButton = addNewEmailSubscriptionRadioButton();
        }
        radioButton.setVisibility(View.VISIBLE);
        radioButton.setText(UNSUBSCRIBE_PREFERENCE_TEXT);
        emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(null);
        radioButton.setChecked(emailSubscriptionPreference != null &&
                emailSubscriptionPreference.equalsIgnoreCase(UNSUBSCRIBE_PREFERENCE_KEY));
        emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(emailSubscriptionPreferenceRadioGroupChangeListener);
        emailSubscriptionPreferencesRadioButtonsMap.put(UNSUBSCRIBE_PREFERENCE_KEY, radioButton);
        emailSubscriptionPreferencesRadioButtonsIdMap.put(radioButton.getId(), UNSUBSCRIBE_PREFERENCE_KEY);
    }

    private void updateEmailSubscriptionPreferenceRadioGroupSelection() {
        emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(null);
        final RadioButton selectedRadioButton = emailSubscriptionPreferencesRadioButtonsMap.get(emailSubscriptionPreference);
        if (selectedRadioButton != null) {
            selectedRadioButton.setChecked(true);
        }
        emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(emailSubscriptionPreferenceRadioGroupChangeListener);
    }

    @OnClick(R.id.controller_details_text_firstname_container)
    public void onFirstNameContainerClick() {
        KeyboardUtils.showSoftInput(mFirstNameText, mActivity);
    }

    @OnClick(R.id.controller_details_text_lastname_container)
    public void onLastNameContainerClick() {
        KeyboardUtils.showSoftInput(mLastNameText, mActivity);
    }

    @OnClick(R.id.controller_details_text_birthday_container)
    public void onBirthdayContainerClick() {
        KeyboardUtils.showSoftInput(mDateOfBirthText, mActivity);
    }

    @OnClick(R.id.controller_details_text_emailaddress_container)
    public void onEmailContainerClick() {
        KeyboardUtils.showSoftInput(mEmailAddressText, mActivity);
    }

    @OnClick(R.id.controller_details_text_password_container)
    public void onPasswordContainerClick() {
        KeyboardUtils.showSoftInput(mPasswordText, mActivity);
    }

    @OnClick(R.id.controller_details_text_new_password_container)
    public void onNewPasswordContainerClick() {
        KeyboardUtils.showSoftInput(mNewPasswordText, mActivity);
    }

    @OnClick(R.id.controller_details_text_confirm_password_cotainer)
    public void onConfirmPasswordContainerClick() {
        KeyboardUtils.showSoftInput(mConfirmPasswordText, mActivity);
    }
}
