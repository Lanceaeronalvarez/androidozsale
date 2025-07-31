package au.com.dealsdirect.ui.controller.details;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesResponse;
import au.com.dealsdirect.data.network.model.preferencecenter.UpdateEmailSubscriptionResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetEmailSubscriptionTemplatesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.userdetails.UpdateUserEmailSubscriptionRequest;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.account.AccountDeletionConfirmationDialog;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.transitions.CustomSpinnerAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.LoadingDialogType;
import butterknife.BindView;
import butterknife.OnClick;
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthCalculator;
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthLevel;
import nu.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter;

public class DetailsController extends BasePullToRefreshController implements DetailsMvpView, PreferenceCategoriesClickListener {

    private final static boolean SHOULD_SHOW_SUCCESS_DIALOGS = false;
    private final static String UNSUBSCRIBE_PREFERENCE_KEY = "Unsubscribe";
    private final static String UNSUBSCRIBE_PREFERENCE_TEXT = "Unsubscribe.";
    private final static String PREFERENCE_CATEGORIES_PROPERTY = "preference_categories";
    private final static String PREFERENCE_MEMBER_PROPERTY = "member_preference";

    private final static int showApiResponseMillis = 3000;

    @Inject
    DetailsMvpPresenter<DetailsMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;

    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mSaveUserDetailsButton;

    @BindView(R.id.controller_details_firstname_wrapper)
    TextInputLayout mFirstNameTextWrapper;

    @BindView(R.id.controller_details_text_firstname)
    EditText mFirstNameText;

    @BindView(R.id.controller_details_lastname_wrapper)
    TextInputLayout mLastNameTextWrapper;

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

    @BindView(R.id.controller_details_checkbox_recycler)
    RecyclerView categoriesRecyclerView;

    @BindView(R.id.controller_details_categories_header)
    TextView emailCategoryPreferencesHeaderTextView;

    @BindView(R.id.controller_unsubscribe_button)
    Button unsubscribeButton;

    @BindView(R.id.controller_success_message)
    TextView emailCategoryPreferencesSuccessMessage;

    @BindView(R.id.controller_details_text_mobile_number)
    EditText mMobileNumberEditText;

    @BindView(R.id.controller_details_sms_opt_in_checkbox)
    CheckBox mSmsOptInCheckbox;

    private final List<RadioButton> emailSubscriptionPreferencesRadioButtonsList = new ArrayList<>();
    private final Map<String, RadioButton> emailSubscriptionPreferencesRadioButtonsMap = new HashMap<>();
    private final Map<Integer, String> emailSubscriptionPreferencesRadioButtonsIdMap = new HashMap<>();

    @BindView(R.id.controller_account_deletion_button)
    Button mAccountDeletionButton;


    private Date dateOfBirth = null;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    private final SimpleDateFormat serverDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat uiDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private String userDetailsId = null;
    private GetUserDetailsResponse currentUserDetails = null;

    private String emailSubscriptionPreference = null;
    private Date emailSubscriptionPreferenceDate = null;

    private GetEmailSubscriptionTemplatesResponse emailSubscriptionTemplates = null;

    private PreferenceCategoriesAdapter mAdapter;

    Map<String, String> mCategoriesList = new LinkedHashMap<>();

    HashMap<String, Boolean> mCategories = new HashMap<>();

    boolean isCategorySelectionEnabled = true;

    private final RadioGroup.OnCheckedChangeListener emailSubscriptionPreferenceRadioGroupChangeListener = (group, checkedId) -> {
        emailSubscriptionPreference = emailSubscriptionPreferencesRadioButtonsIdMap.get(checkedId);
        emailSubscriptionPreferenceDate = Calendar.getInstance().getTime();

        if (isCategorySelectionEnabled == false) {
            isCategorySelectionEnabled = true;
            setRecyclerAdapter();
        }

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
    public void onUpdateEmailSubscriptionPreferenceWithResponse(UpdateEmailSubscriptionResponse response) {
        emailCategoryPreferencesSuccessMessage.setVisibility(View.VISIBLE);
        emailCategoryPreferencesSuccessMessage.setText(response.getMessage());

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!isViewAttached() || emailCategoryPreferencesSuccessMessage == null) {
                return;
            }
            emailCategoryPreferencesSuccessMessage.setVisibility(View.GONE);
        }, showApiResponseMillis);
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
        unsubscribeButton.setText(Html.fromHtml("<u><i>Unsubscribe</i></u>"));
        mAccountDeletionButton.setText(Html.fromHtml("<u>Request Account and Data Deletion</u>"));
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

        mCategories = new HashMap<>();

        showLoading(LoadingDialogType.DEFAULT);

        mPresenter.getEmailSubscriptionTemplates();
        mPresenter.loadUser(setUserDetailsRequest);

        mFirstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasSpecialCharactersFirstName()) {
                    mFirstNameTextWrapper.setError(getString(R.string.special_character_error));
                } else if (isNameDuplicate()) {
                    mFirstNameTextWrapper.setError(getString(R.string.duplicate_name_error));
                } else {
                    mFirstNameTextWrapper.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mLastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (hasSpecialCharactersLastName()) {
                    mLastNameTextWrapper.setError(getString(R.string.special_character_error));
                } else if (isNameDuplicate()) {
                    mLastNameTextWrapper.setError(getString(R.string.duplicate_name_error));
                } else {
                    mLastNameTextWrapper.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

        if (userDetailsResponse.getCategories() != null) {
            mCategories.putAll(userDetailsResponse.getCategories());
        }

        if (emailSubscriptionPreference.equals(UNSUBSCRIBE_PREFERENCE_KEY)) {
            unsubscribeRecycler();
        } else {
            isCategorySelectionEnabled = true;
            setRecyclerAdapter();
        }

        String mobileNumber = userDetailsResponse.getMobileNumber();
        Boolean willReceiveSMS = userDetailsResponse.willReceiveSMS();
        mMobileNumberEditText.setText(mobileNumber != null ? mobileNumber.startsWith("+") ? mobileNumber.replace(getString(R.string.country_code_mobile_number), "") : mobileNumber : "");
        mSmsOptInCheckbox.setChecked(willReceiveSMS != null ? willReceiveSMS : false);
    }

    @Override
    public void saveUserDetailsSuccess() {
        clearPasswordFields();

        String firstname = getFieldValue(mFirstNameText);
        String lastname = getFieldValue(mLastNameText);
        boolean gender = getFieldValueOfGender();
        String email = getFieldValue(mEmailAddressText);
        String dateOfBirth = getFieldValueOfDateOfBirth();
        String mobileNumber = getFieldValue(mMobileNumberEditText);

        currentUserDetails.setForename(firstname);
        currentUserDetails.setSurname(lastname);
        currentUserDetails.setGender(gender);
        currentUserDetails.setEmail(email);
        currentUserDetails.setDateOfBirth(dateOfBirth);
        currentUserDetails.setMobileNumber(mobileNumber);

        if (isEmailSubscriptionPreferenceChanged()) {
            updateEmailSubscriptionPreference(mEmailAddressText.getText().toString());
        } else {
            String message = "Changes Saved";
            if (SHOULD_SHOW_SUCCESS_DIALOGS) {
                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, message);
            } else {
                emailCategoryPreferencesSuccessMessage.setVisibility(View.VISIBLE);
                emailCategoryPreferencesSuccessMessage.setText(message);

                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    if (!isViewAttached() || emailCategoryPreferencesSuccessMessage == null) {
                        return;
                    }
                    emailCategoryPreferencesSuccessMessage.setVisibility(View.GONE);
                }, showApiResponseMillis);
            }
        }
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
    public void onGetEmailSubscriptionTemplates(List<GetEmailSubscriptionTemplatesResponse> list) {
        for (int it = 0; it < list.size(); it++) {
            if (list.get(it).getProperty().equals(PREFERENCE_CATEGORIES_PROPERTY)) {
                GetEmailSubscriptionTemplatesResponse categoryPreferenceOption = list.get(it);
                for (int itOption = 0; itOption < categoryPreferenceOption.getOptions().size(); itOption++) {
                    String key = categoryPreferenceOption.getOptions().get(itOption).getPreference().toLowerCase();
                    mCategoriesList.put(categoryPreferenceOption.getOptions().get(itOption).getText(), categoryPreferenceOption.getOptions().get(itOption).getPreference());
                    if (!mCategories.containsKey(key)) {
                        mCategories.put(key, false);
                    }
                }
                if (!categoryPreferenceOption.getOptions().isEmpty()) {
                    emailCategoryPreferencesHeaderTextView.setText(categoryPreferenceOption.getTitle());
                    mCategoriesList.put(categoryPreferenceOption.getSelectAllText(), "all");
                    setRecyclerAdapter();
                }
            }
            if (list.get(it).getProperty().equals(PREFERENCE_MEMBER_PROPERTY)) {
                emailSubscriptionTemplates = list.get(it);
                if (emailSubscriptionTemplates == null) {
                    emailSubscriptionPreferencesContainerView.setVisibility(View.GONE);
                    return;
                }
                new Handler(mActivity.getMainLooper()).post(() -> {
                    emailSubscriptionPreferencesContainerView.setVisibility(View.VISIBLE);

                    // Set Header Text
                    emailSubscriptionPreferencesHeaderTextView.setText(emailSubscriptionTemplates.getTitle());

                    // Inflate/Hide RadioButtons according to Options size
                    final int optionsSizeDifference = emailSubscriptionTemplates.getOptions().size() - emailSubscriptionPreferencesRadioButtonsList.size();
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
                    for (int i = 0; i < emailSubscriptionTemplates.getOptions().size(); i++) {
                        RadioButton radioButton = emailSubscriptionPreferencesRadioButtonsList.get(i);
                        GetEmailSubscriptionTemplatesResponse.Option option = emailSubscriptionTemplates.getOptions().get(i);

                        radioButton.setVisibility(View.VISIBLE);
                        radioButton.setText(Html.fromHtml(option.getText()));
                        radioButton.setChecked(false);
                        emailSubscriptionPreferencesRadioButtonsMap.put(option.getPreference(), radioButton);
                        emailSubscriptionPreferencesRadioButtonsIdMap.put(radioButton.getId(), option.getPreference());
                    }
                    emailSubscriptionPreferencesRadioGroup.setOnCheckedChangeListener(emailSubscriptionPreferenceRadioGroupChangeListener);

                    updateEmailSubscriptionPreferenceRadioGroupSelection();
                });
            }
        }
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

            AlertDialog dialogBuilder = new AlertDialog.Builder(mActivity).create();
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.confirm_password_dialog, null);

            final EditText dialogConfirmPasswordText = (EditText) dialogView.findViewById(R.id.confirm_password_edit_text);
            final TextView dialogConfirmChangesButton = (TextView) dialogView.findViewById(R.id.confirm_changes_text);
            final TextView dialogCloseButton = (TextView) dialogView.findViewById(R.id.close_text);
            final TextView confirmPasswordErrorText = (TextView) dialogView.findViewById(R.id.confirm_password_error_text);
            final TextView forgotPasswordText = (TextView) dialogView.findViewById(R.id.forgot_password_text);

            dialogConfirmChangesButton.setOnClickListener(v -> {

                if (dialogConfirmPasswordText.getText().toString().isEmpty() || dialogConfirmPasswordText.getText().toString() == "") {
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
                String password = getFieldValue(dialogConfirmPasswordText);
                String mobileNumber = getFieldValue(mMobileNumberEditText).startsWith("+") ? getFieldValue(mMobileNumberEditText).replace(getString(R.string.country_code_mobile_number), "") : getString(R.string.country_code_mobile_number) + getFieldValue(mMobileNumberEditText);

                mPresenter.sendUserDetails(createUserDetailRequest(email,
                                firstname,
                                lastname,
                                dateOfBirth,
                                gender,
                                email,
                                password,
                                "",
                                "",
                                mobileNumber),
                        success -> {
                            if (success) {
                                dialogBuilder.dismiss();
                            } else {
                                confirmPasswordErrorText.setVisibility(View.VISIBLE);
                                confirmPasswordErrorText.setText("Incorrect Password");
                            }
                        });

            });

            dialogCloseButton.setOnClickListener(v -> {
                dialogBuilder.dismiss();
            });

            forgotPasswordText.setOnClickListener(v -> {
                dialogBuilder.dismiss();
                getRouter().pushController(RouterTransaction.with(ForgotPasswordController.newInstance())
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
            });

            dialogBuilder.setView(dialogView);
            dialogBuilder.show();

        } else if (isEmailSubscriptionPreferenceChanged()) {
            updateEmailSubscriptionPreference(currentUserDetails.getEmail());
        }
    }

    @OnClick(R.id.controller_details_reset_password)
    public void onClickResetPassword() {
        getRouter().pushController(RouterTransaction.with(ForgotPasswordController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.change_password_button)
    public void onClickChangePassword() {
        AlertDialog dialogBuilder = new AlertDialog.Builder(mActivity).create();
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_password_dialog, null);

        final EditText dialogCurrentPasswordText = (EditText) dialogView.findViewById(R.id.current_password_edit_text);
        final EditText dialogNewPasswordText = (EditText) dialogView.findViewById(R.id.new_password_edit_text);
        final EditText dialogConfirmPasswordText = (EditText) dialogView.findViewById(R.id.confirm_password_edit_text);
        final TextView dialogConfirmChangesButton = (TextView) dialogView.findViewById(R.id.confirm_changes_text);
        final TextView dialogCloseButton = (TextView) dialogView.findViewById(R.id.close_text);
        final TextView currentPasswordErrorText = (TextView) dialogView.findViewById(R.id.current_password_error_text);
        final TextView newPasswordErrorText = (TextView) dialogView.findViewById(R.id.new_password_error_text);
        final TextView confirmPasswordErrorText = (TextView) dialogView.findViewById(R.id.confirm_password_error_text);
        final TextView forgotPasswordText = (TextView) dialogView.findViewById(R.id.forgot_password_text);
        PasswordStrengthMeter meter = (PasswordStrengthMeter) dialogView.findViewById(R.id.passwordInputMeter);

        meter.setStrengthLevels(new PasswordStrengthLevel[]{
                new PasswordStrengthLevel("Weak", android.R.color.darker_gray), // level 0
                new PasswordStrengthLevel("Weak", android.R.color.holo_red_dark), // level 1
                new PasswordStrengthLevel("Good", android.R.color.holo_orange_dark), // level 2
                new PasswordStrengthLevel("Good", android.R.color.holo_orange_dark), // level 3
                new PasswordStrengthLevel("Strong", android.R.color.holo_green_dark), // level 4
                new PasswordStrengthLevel("Strong", android.R.color.holo_green_dark)}); // level 5

        meter.setEditText(dialogNewPasswordText);
        meter.setPasswordStrengthCalculator(new PasswordStrengthCalculator() {
            @Override
            public int calculatePasswordSecurityLevel(String password) {
                // Do some calculation and return an int corresponding to the "points" or "level" the user password got
                Pattern pattern;
                final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
                pattern = Pattern.compile(PASSWORD_PATTERN);

                if (password.length() >= 8 && password.matches("[a-zA-Z ]+")) {
                    return 1;
                } else if (password.length() >= 8 && password.matches("[a-zA-Z0-9 ]+")) {
                    return 3;
                } else if (pattern.matcher(password).matches()) {
                    return 5;
                } else {
                    return 1;
                }

            }

            @Override
            public int getMinimumLength() {
                // Define the minimum length of a password. Anything below this should always yield a score of 0
                return 8;
            }

            @Override
            public boolean passwordAccepted(int level) {
                // Define whether or not the level is an accepted level or not.
                return level > 1;
            }

            @Override
            public void onPasswordAccepted(String password) {
                // Called when the password entered meets your requirements of length and strength levels
            }
        });
        dialogNewPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                meter.setVisibility(View.VISIBLE);
                if (dialogNewPasswordText.getText().toString().equals(dialogConfirmPasswordText.getText().toString())) {
                    newPasswordErrorText.setVisibility(View.GONE);
                    confirmPasswordErrorText.setVisibility(View.GONE);
                } else {
                    newPasswordErrorText.setVisibility(View.VISIBLE);
                    confirmPasswordErrorText.setVisibility(View.VISIBLE);
                    newPasswordErrorText.setText("Password does not match");
                    confirmPasswordErrorText.setText("Password does not match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialogConfirmPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (dialogConfirmPasswordText.getText().toString().equals(dialogNewPasswordText.getText().toString())) {
                    newPasswordErrorText.setVisibility(View.GONE);
                    confirmPasswordErrorText.setVisibility(View.GONE);
                } else {
                    newPasswordErrorText.setVisibility(View.VISIBLE);
                    confirmPasswordErrorText.setVisibility(View.VISIBLE);
                    newPasswordErrorText.setText("Password does not match");
                    confirmPasswordErrorText.setText("Password does not match");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dialogConfirmChangesButton.setOnClickListener(v -> {

            String firstname = getFieldValue(mFirstNameText);
            String lastname = getFieldValue(mLastNameText);
            boolean gender = getFieldValueOfGender();
            String email = getFieldValue(mEmailAddressText);
            String dateOfBirth = getFieldValueOfDateOfBirth();
            String password = getFieldValue(dialogCurrentPasswordText);
            String newpassword = getFieldValue(dialogNewPasswordText);
            String confirmpassword = getFieldValue(dialogConfirmPasswordText);
            String mobileNumber = getFieldValue(mMobileNumberEditText).startsWith("+") ? getFieldValue(mMobileNumberEditText).substring(3) : getString(R.string.country_code_mobile_number) + getFieldValue(mMobileNumberEditText);

            if (dialogConfirmPasswordText.getText().toString().isEmpty() || dialogConfirmPasswordText.getText().toString() == "") {
                CustomAlertDialog.showCustomAlertDialog(mActivity,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getString(R.string.controller_user_details_enter_password));
                return;
            }

            if (newpassword.equals(confirmpassword)) {
                mPresenter.sendUserDetails(createUserDetailRequest(
                        email,
                        firstname,
                        lastname,
                        dateOfBirth,
                        gender,
                        email,
                        password,
                        newpassword,
                        confirmpassword,
                        mobileNumber), new DetailsPasswordCallback() {
                    @Override
                    public void onEvent(boolean success) {
                        if (success) {
                            dialogBuilder.dismiss();
                        } else {
                            confirmPasswordErrorText.setVisibility(View.VISIBLE);
                            currentPasswordErrorText.setText("Incorrect Password");
                        }
                    }
                });
            } else {
                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE, mActivity.getString(R.string.password_does_not_match));
            }

        });

        dialogCloseButton.setOnClickListener(v -> {
            dialogBuilder.dismiss();
        });

        forgotPasswordText.setOnClickListener(v -> {
            dialogBuilder.dismiss();
            getRouter().pushController(RouterTransaction.with(ForgotPasswordController.newInstance())
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();

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

    @OnClick(R.id.controller_unsubscribe_button)
    public void unsubscribeClicked() {
        updateEmailUnsubscribePreference();
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();

        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        userDetailsId = null;
        mPresenter.loadUser(setUserDetailsRequest);
    }

    private boolean hasSpecialCharactersFirstName() {
        return !mFirstNameText.getText().toString().trim().matches("[a-zA-Z ]+")
                || mFirstNameText.getText().toString().trim().length() < 2;
    }

    private boolean hasSpecialCharactersLastName() {
        return !mLastNameText.getText().toString().trim().matches("[a-zA-Z ]+") ||
                mLastNameText.getText().toString().trim().length() < 2;
    }

    private boolean isNameDuplicate() {
        return mFirstNameText.getText().toString().trim().equalsIgnoreCase(mLastNameText.getText().toString().trim());
    }

    private void updateDateOfBirthField() {
        mDateOfBirthText.setText(dateOfBirth == null ? null : uiDateFormat.format(dateOfBirth));
    }

    public SetUserDetailsRequest createUserDetailRequest(String userName,
                                                         String firstName,
                                                         String lastName,
                                                         String dateOfBirth,
                                                         boolean gender,
                                                         String email,
                                                         String password,
                                                         String newPassword,
                                                         String confirmPassword,
                                                         String mobileNumber) {
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
        userDetailsRequest.setMobileNumber(mobileNumber);

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
        String mobileNumber = getFieldValue(mMobileNumberEditText).startsWith("+") ? getFieldValue(mMobileNumberEditText).substring(3) : getFieldValue(mMobileNumberEditText);

        String currentFirstname = currentUserDetails.getForename();
        String currentSurname = currentUserDetails.getSurname();
        String currentEmail = currentUserDetails.getEmail();

        Date currentDateOfBirth = getDateFromServerDateString(currentUserDetails.getDateOfBirth());

        boolean currentGender = currentUserDetails.getGender();

        boolean isMobileNumberValid = false;
        String currentMobileNumber = currentUserDetails.getMobileNumber() != null ?
                currentUserDetails.getMobileNumber() : "";

        if (!currentMobileNumber.equals(mobileNumber)) {
            if (getString(R.string.country_code_mobile_number).equals("+61")) {
                if (mobileNumber.length() == 9 && mobileNumber.startsWith(getString(R.string.country_code_mobile_number_start))) {
                    isMobileNumberValid = true;
                } else {
                    CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.invalid_phone_number));
                }
            }
            if (getString(R.string.country_code_mobile_number).equals("+64")) {
                if (mobileNumber.length() <= 10 && mobileNumber.length() >= 9 && mobileNumber.startsWith(getString(R.string.country_code_mobile_number_start))) {
                    isMobileNumberValid = true;
                } else {
                    CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.invalid_phone_number));
                }
            }
        }

        return !currentFirstname.equals(firstname) ||
                !currentSurname.equals(lastname) ||
                !currentEmail.equals(email) ||
                (currentGender != gender && getBoolean(R.bool.is_gender_enabled)) ||
                (currentDateOfBirth == null && dateOfBirth != null) ||
                (currentDateOfBirth != null && currentDateOfBirth.equals(dateOfBirth)) ||
                !newpassword.isEmpty() ||
                isMobileNumberValid;
    }

    private boolean isEmailSubscriptionPreferenceChanged() {
        return currentUserDetails != null &&
                (emailSubscriptionPreferenceDate != null &&
                        !emailSubscriptionPreferenceDate.equals(getDateFromServerDateString(currentUserDetails.getPreferenceDate())) ||
                        mSmsOptInCheckbox.isChecked() != currentUserDetails.willReceiveSMS());
    }

    private void updateEmailSubscriptionPreference(String email) {
        UpdateUserEmailSubscriptionRequest request = new UpdateUserEmailSubscriptionRequest();
        request.setEmail(email);
        request.setPreference(emailSubscriptionPreference);
        request.setStatus(emailSubscriptionPreference != null && !emailSubscriptionPreference.equalsIgnoreCase(UNSUBSCRIBE_PREFERENCE_KEY));
        request.setCategories(mCategories);
        request.setWillReceiveSMS(mSmsOptInCheckbox.isChecked());

        mPresenter.updateEmailSubscriptionPreference(request);
    }

    private void updateEmailUnsubscribePreference() {
        UpdateUserEmailSubscriptionRequest request = new UpdateUserEmailSubscriptionRequest();
        request.setEmail(currentUserDetails.getEmail());
        request.setPreference(UNSUBSCRIBE_PREFERENCE_KEY);
        request.setStatus(false);
        request.setWillReceiveSMS(mSmsOptInCheckbox.isChecked());

        mPresenter.updateEmailSubscriptionPreference(request);
        emailSubscriptionPreferencesRadioGroup.clearCheck();
        unsubscribeRecycler();
    }

    private String getFieldValue(TextView textView) {
        return textView.getText().toString();
    }

    private String getFieldValueOfDateOfBirth() {
        return dateOfBirth == null ? "" : serverDateFormat.format(dateOfBirth);
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

    @Override
    public void onCategoryClicked(HashMap<String, Boolean> category) {
        emailSubscriptionPreferenceDate = Calendar.getInstance().getTime();
        mCategories = category;
        setRecyclerAdapter();
    }

    public void setRecyclerAdapter() {
        mAdapter = new PreferenceCategoriesAdapter(mActivity, this, mCategoriesList, mCategories, isCategorySelectionEnabled);
        categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        categoriesRecyclerView.setMotionEventSplittingEnabled(false);
        categoriesRecyclerView.setAdapter(mAdapter);
        categoriesRecyclerView.setNestedScrollingEnabled(false);
        mAdapter.notifyDataSetChanged();

    }

    public void unsubscribeRecycler() {
        isCategorySelectionEnabled = false;
        setRecyclerAdapter();
    }

}
