package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.mysale.genie.utility.Prefs;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.BaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.ContactSelectSubjectController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.transitions.ReverseVerticalChangeHandler;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseToolBarController implements AddContactMvpView {

    public static final String TAG = "AddContactController";

    private static final String KEY_TEXT = "AddContactController.KEY_TEXT";
    private static final String KEY_FROM_FRAGMENT_ID = "AddContactController.KEY_FROM";
    private static final String KEY_CONTACT_SUBJECT = "CONTACT_SUBJECT";
    private static final String KEY_CONTACT_NUMBER = "CONTACT_NUMBER";
    private static final String KEY_INVOICE_NUMBER = "CONTACT_INVOICE_NUMBER";


    @BindView(R.id.partial_toolbar_right_view)
    ImageView mAddContactToolbarRightOption;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mAddContactToolbarTitle;

    @BindView(R.id.controller_add_contact_subject_title)
    TextView mAddContactSubjectTitle;

    @BindView(R.id.controller_add_contact_order_title)
    TextView mAddContactOrderTitle;

    @BindView(R.id.controller_add_contact_subject_text)
    TextView mAddContactSubjectText;

    @BindView(R.id.controller_add_contact_order_text)
    TextView mAddContactOrderText;

    @BindView(R.id.controller_add_contact_message_field)
    EditText mAddContactMessageField;

    @BindView(R.id.controller_add_contact_message_send)
    ImageButton mAddContactMessageSend;

    @BindView(R.id.controller_add_contact_selector_container)
    FrameLayout mAddContactSelectorContainer;

    @Inject
    AddContactMvpPresenter<AddContactMvpView> mPresenter;
    
    List<String> mContactSubjects;
    List<ContactOrderList> mContactOrders;

    boolean hasLoadedSubjects = false;
    boolean hasLoadedOrders = false;

    //    static GetContactsResponse.ContactList mContactUsObject;
    private static int mContactNumber;
    private int mInvoiceNumber;
    private String mChosenOptionInvoice;

    private String mContactHistoryChosenSubject;
    private String mContactHistoryChosenOrder;
    private static String mFromFragmentId;

    public static AddContactController newInstance(
            String fromFragmentId,
            Bundle bundle) {

        bundle.putString(KEY_FROM_FRAGMENT_ID, fromFragmentId);
        return new AddContactController(bundle);
    }

    public static AddContactController newInstance() {
        Prefs.putBoolean("isSubjectsLoaded", false);

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_FROM_FRAGMENT_ID, "CONTACT_US")
                        .build());
    }


    public AddContactController(Bundle args) {
        super(args);
        if (!args.isEmpty()) {
            if (args.containsKey(KEY_FROM_FRAGMENT_ID)) {
                mFromFragmentId = args.getString(KEY_FROM_FRAGMENT_ID);
            }

            if (args.containsKey(KEY_CONTACT_SUBJECT)) {
                mContactHistoryChosenSubject = args.getString(KEY_CONTACT_SUBJECT);
            }

            if (args.containsKey(KEY_CONTACT_NUMBER)) {
                mContactNumber = args.getInt(KEY_CONTACT_NUMBER);
            }

            if (args.containsKey(KEY_INVOICE_NUMBER)) {
                mInvoiceNumber = args.getInt(KEY_INVOICE_NUMBER);
            }
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_contact, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        // used for TB, null for DD
        if (mToolbarTitle != null) mToolbarTitle.setText("new message");
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        KeyboardUtils.setKeyboardAdjustResize(mActivity);
        mActivity.getMainController().hideBottomNav();

        getChildRouter(mAddContactSelectorContainer).addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                mAddContactSubjectText.setText(ContactPreferenceHelper.getChosenSubject(mActivity));
                mAddContactOrderText.setText(ContactPreferenceHelper.getChosenOrder(mActivity));
                if (!isPush) {
                    mAddContactSubjectTitle.setSelected(false);
                    mAddContactOrderTitle.setSelected(false);
                }
            }
        });

        mAddContactToolbarRightOption.setVisibility(View.INVISIBLE);
        mAddContactToolbarTitle.setText(R.string.create_contact);

        boolean isSubjectsLoaded = Prefs.getBoolean("isSubjectsLoaded", false);

        if (!isSubjectsLoaded) {

            mPresenter.loadContactUsSubjects();
            Prefs.putBoolean("isSubjectsLoaded", true);
        } else {
//            setupDefaultBottomButton(mBaseActivity.getString(R.string.submit), onClickListener);
        }

        final String chosenSubject = mAddContactSubjectText.getText().toString();
        final String chosenOrder = mAddContactOrderText.getText().toString();

        if (mFromFragmentId.equals("CONTACT_HISTORY")) {

            if (mInvoiceNumber == 0)
                mAddContactOrderText.setText(String.valueOf("None"));
            else
                mAddContactOrderText.setText(String.valueOf(mInvoiceNumber));

            mAddContactSubjectText.setText(mContactHistoryChosenSubject);

            mAddContactSubjectText.setClickable(false);
            mAddContactOrderText.setClickable(false);

            View.OnClickListener onClickListener = view1 -> {
                String replyMessage = mAddContactMessageField.getText().toString();
                int contactId = mContactNumber;

                ReplyContactRequest replyContactRequest = new ReplyContactRequest();
                replyContactRequest.comments = replyMessage;
                replyContactRequest.contactNo = contactId;

                if (replyContactRequest.comments.isEmpty()) {

                    //                CustomAlertDialog.showCustomAlertDialog(
                    //                        mBaseActivity,
                    //                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    //                        mBaseActivity.getString(R.string.please_input_message)
                    //                );

                } else {
                    mPresenter.replyContact(replyContactRequest);
                }
            };

            mAddContactMessageSend.setOnClickListener(onClickListener);
        } else if (mFromFragmentId.equals("CONTACT_US")) {

            String currentSubject = ContactPreferenceHelper.getChosenSubject(mActivity);
            String currentOrder = ContactPreferenceHelper.getChosenOrder(mActivity);
            mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(mActivity);

//            myContactsSelectSubjectChosenOption.setText(mContactHistoryChosenSubject);
            mPresenter.loadContactUsOrders();

            mPresenter.loadContactUsSubjects();

//            if (!hasLoadedOrders) {
//                Log.d("clickable", "from contact load orders");
//
//                mPresenter.loadContactUsOrders();
//
//            } if (!hasLoadedSubjects){
//                Log.d("clickable", "from contact load subjects");
//
//                mPresenter.loadContactUsSubjects();
//            }

            if (!currentOrder.isEmpty()) {

                showContactFirstOrderFromPreference(currentOrder);

            }

            if (!currentSubject.isEmpty()) {

                showContactFirstSubjectFromPreference(currentSubject);

            }

            View.OnClickListener onClickListener = view12 -> {
                mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(mActivity);
                CreateContactRequest createContactRequest = new CreateContactRequest();

                if (mChosenOptionInvoice != "") {
                    createContactRequest.invoiceNo = Integer.valueOf(mChosenOptionInvoice);
                } else {
                    createContactRequest.invoiceNo = 0;
                }

                createContactRequest.subj = mAddContactSubjectText.getText().toString();

                if (mAddContactMessageField.getText().toString().isEmpty()
                        || mAddContactSubjectText.getText().toString().isEmpty()) {

                    CustomAlertDialog.showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getString(R.string.create_contact_fill_up));

                } else {

                    createContactRequest.msg = mAddContactMessageField.getText().toString();

                    mPresenter.createNewContact(createContactRequest);

                }
            };
            mAddContactMessageSend.setOnClickListener(onClickListener);

        }
    }

    @Override
    public void onDestroyView(View view) {
        ContactPreferenceHelper.clear(mActivity);
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }


    @OnClick(R.id.partial_toolbar_left_view)
    void onBack() {
        mActivity.onBackPressed();
    }

    @OnFocusChange(R.id.controller_add_contact_message_field)
    void onMessageFieldFocusChange(View view, boolean hasFocus) {
        if (hasFocus && getChildRouter(mAddContactSelectorContainer).getBackstackSize() > 0) {
            getChildRouter(mAddContactSelectorContainer).popCurrentController();
        }
    }

    @OnClick(R.id.controller_add_contact_subject_container)
    void onClickSubjectContainer() {

        mAddContactMessageField.clearFocus();
        mAddContactSubjectTitle.requestFocus();
        hideKeyboard();

        if (hasLoadedSubjects) {
            if (mAddContactSubjectTitle.isSelected()) {
                getChildRouter(mAddContactSelectorContainer).popCurrentController();
                mAddContactSubjectTitle.setSelected(false);
            } else {
                getChildRouter(mAddContactSelectorContainer).setPopsLastView(true).setRoot(RouterTransaction.with(ContactSelectSubjectController.newInstance(mContactSubjects))
                        .pushChangeHandler(new ReverseVerticalChangeHandler())
                        .popChangeHandler(new ReverseVerticalChangeHandler()));
                mAddContactSubjectTitle.setSelected(true);
                mAddContactOrderTitle.setSelected(false);
            }
        } else {

//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity,
//                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    mBaseActivity.getString(R.string.loading_subjects));
        }
    }

    @OnClick(R.id.controller_add_contact_order_container)
    void onClickOrderContainer() {

        mAddContactMessageField.clearFocus();
        mAddContactSubjectTitle.requestFocus();
        hideKeyboard();

        if (hasLoadedOrders) {
            if (mAddContactOrderTitle.isSelected()) {
                getChildRouter(mAddContactSelectorContainer).popCurrentController();
                mAddContactOrderTitle.setSelected(false);
            } else {
                getChildRouter(mAddContactSelectorContainer).setPopsLastView(true).setRoot(RouterTransaction.with(ContactSelectOrderController.newInstance(mContactOrders))
                        .pushChangeHandler(new ReverseVerticalChangeHandler())
                        .popChangeHandler(new ReverseVerticalChangeHandler()));
                mAddContactSubjectTitle.setSelected(false);
                mAddContactOrderTitle.setSelected(true);
            }
        } else if (hasLoadedOrders && mContactOrders != null && mContactOrders.size() == 0) {

//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity,
//                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    mBaseActivity.getString(R.string.you_have_no_order));

        } else {

        }

    }

    @Override
    public void showContactFirstSubject(List<String> contactSubjectList) {

        mContactSubjects = contactSubjectList;
        hasLoadedSubjects = true;
    }

    @Override
    public void showContactFirstOrder(List<ContactOrderList> contactOrderList) {

        mContactOrders = contactOrderList;
        hasLoadedOrders = true;
    }

    @Override
    public void contactCreatedSwitchView(CreateContactResponse createContactResponse) {
        if (createContactResponse.getCreateContact().getResult()) {

//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.message_submitted));

            mActivity.onBackPressed();
        } else {
//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.error_creating_message));
        }
    }

    @Override
    public void repliedContactSwitchView(ReplyContact replyContact) {

        if (replyContact.getResult()) {
//
//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.message_submitted));
            mActivity.onBackPressed();
        } else {
//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.error_creating_message));
        }
    }

    public void showContactFirstSubjectFromPreference(String subject) {

        mAddContactSubjectText.setText(subject);
    }

    public void showContactFirstOrderFromPreference(String order) {

        mAddContactOrderText.setText(order);
    }
}
