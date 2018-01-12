package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.mysale.genie.utility.Prefs;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.ContactSelectSubjectController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static au.com.dealsdirect.utils.BundleKeys.CONTACT_INVOICE;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_NUMBER;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_SUBJECT;
import static au.com.dealsdirect.utils.BundleKeys.FROM_FRAGMENT_ID;

/**
 * dp Created by Admin on 6/20/17.
 */

@SuppressWarnings("ConstantConditions")
public class AddContactController extends SwipeableBaseToolBarController implements AddContactMvpView {

    public static final String TAG = "AddContactController";


    @BindView(R.id.controller_add_contact_subject_title)
    TextView mAddContactSubjectTitle;

    @BindView(R.id.controller_add_contact_order_title)
    TextView mAddContactOrderTitle;

    @BindView(R.id.controller_add_contact_subject_text)
    TextView  mAddContactSubjectText;

    @BindView(R.id.controller_add_contact_order_text)
    TextView mAddContactOrderText;

    @BindView(R.id.controller_add_contact_message_field)
    EditText mAddContactMessageField;

    private Consumer onClickListener;

    @Inject
    AddContactMvpPresenter<AddContactMvpView> mPresenter;
    
    private List<String> mContactSubjects;
    private List<ContactOrderList> mContactOrders;

    private boolean hasLoadedSubjects = false;
    private boolean hasLoadedOrders = false;

    //    static GetContactsResponse.ContactList mContactUsObject;
    private static int mContactNumber;
    private int mInvoiceNumber;
    private String mChosenOptionInvoice;

    private String mContactHistoryChosenSubject;
    private static String mFromFragmentId;

    public static AddContactController newInstance(
            String fromFragmentId,
            String contactSubject,
            int invoiceNo) {

        return new AddContactController(new BundleBuilder(new Bundle())
                .putString(FROM_FRAGMENT_ID, fromFragmentId)
                .putString(CONTACT_SUBJECT, contactSubject)
                .putInt(CONTACT_INVOICE, invoiceNo)
                .build());
    }

    public static AddContactController newInstance() {
        Prefs.putBoolean("isSubjectsLoaded", false);

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putString(BundleKeys.FROM_FRAGMENT_ID, "CONTACT_US")
                        .build());
    }


    public AddContactController(Bundle args) {
        super(args);
        if (!args.isEmpty()) {
            if (args.containsKey(FROM_FRAGMENT_ID)) {
                mFromFragmentId = args.getString(FROM_FRAGMENT_ID);

            }

            if (args.containsKey(CONTACT_SUBJECT)) {
                mContactHistoryChosenSubject = args.getString(CONTACT_SUBJECT);
            }

            if (args.containsKey(CONTACT_NUMBER)) {
                mContactNumber = args.getInt(CONTACT_NUMBER);
            }

            if (args.containsKey(CONTACT_INVOICE)) {
                mInvoiceNumber = args.getInt(CONTACT_INVOICE);
            }
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_add_contact, container, false));

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mToolbarTitle.setText("new message");
        setupSwipingBehavior();

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        Log.d("addContactController", "from = "+mFromFragmentId);

        KeyboardUtils.setKeyboardAdjustResize(mActivity);
        mActivity.getMainController().hideBottomNav();

        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

                if (!mFromFragmentId.equals("CONTACT_HISTORY")){
                    if (mAddContactSubjectText!=null){
                        mAddContactSubjectText.setText(ContactPreferenceHelper.getChosenSubject(mActivity));
                        mAddContactOrderText.setText(ContactPreferenceHelper.getChosenOrder(mActivity));
                        if (!isPush) {
                            mAddContactSubjectTitle.setSelected(false);
                            mAddContactOrderTitle.setSelected(false);
                        }
                    }
                }
            }
        });

        boolean isSubjectsLoaded = Prefs.getBoolean("isSubjectsLoaded", false);

        if (!isSubjectsLoaded) {

            mPresenter.loadContactUsSubjects();
            Prefs.putBoolean("isSubjectsLoaded", true);
        } else {
            setupDefaultBottomButton("submit", onClickListener);
        }

//        final String chosenSubject = mAddContactSubjectText.getText().toString();
//        final String chosenOrder = mAddContactOrderText.getText().toString();

        if (mFromFragmentId.equals("CONTACT_HISTORY")) {

            Log.d("addContactController", "Contact history");
            if (mInvoiceNumber == 0) {
                Log.d("addContactController", "mInvoiceNumber == 0");

                mAddContactOrderText.setText(String.valueOf("None"));
            }
            else {
                Log.d("addContactController", "mInvoiceNumber == 0 else");

                mAddContactOrderText.setText(String.valueOf(mInvoiceNumber));
            }

            Log.d("addContactController", "subject = "+mContactHistoryChosenSubject);

//            mAddContactSubjectText.setText(mContactHistoryChosenSubject);

            mAddContactSubjectText.setClickable(false);
            mAddContactOrderText.setClickable(false);

            onClickListener = view1 -> {
                String replyMessage = mAddContactMessageField.getText().toString();
                int contactId = mContactNumber;

                ReplyContactRequest replyContactRequest = new ReplyContactRequest();
                replyContactRequest.comments = replyMessage;
                replyContactRequest.contactNo = contactId;

                if (replyContactRequest.comments.isEmpty()) {

                    CustomAlertDialog.showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            "please input message"
                    );

                } else {
                    mPresenter.replyContact(replyContactRequest);
                }
            };

            setupDefaultBottomButton("submit", onClickListener);


        } else if (mFromFragmentId.equals("CONTACT_US")) {

            String currentSubject = ContactPreferenceHelper.getChosenSubject(mActivity);
            String currentOrder = ContactPreferenceHelper.getChosenOrder(mActivity);
            mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(mActivity);

//            myContactsSelectSubjectChosenOption.setText(mContactHistoryChosenSubject);
            mPresenter.loadContactUsOrders();

            mPresenter.loadContactUsSubjects();

            if (!hasLoadedOrders) {

                mPresenter.loadContactUsOrders();

            } if (!hasLoadedSubjects){

                mPresenter.loadContactUsSubjects();
            }

            if (!currentOrder.isEmpty()) {

                showContactFirstOrderFromPreference(currentOrder);
            }

            if (!currentSubject.isEmpty()) {

                showContactFirstSubjectFromPreference(currentSubject);

            }

            onClickListener = view12 -> {
                mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(mActivity);
                CreateContactRequest createContactRequest = new CreateContactRequest();

                if (!mChosenOptionInvoice.equals("")) {
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

        }
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        ContactPreferenceHelper.clear(mActivity);
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

//    @OnFocusChange(R.id.controller_add_contact_message_field)
//    void onMessageFieldFocusChange(View view, boolean hasFocus) {
//        if (hasFocus && getRouter().getBackstackSize() > 0) {
//            getRouter().popCurrentController();
//        }
//    }

    @OnClick(R.id.controller_add_contact_subject_container)
    void onClickSubjectContainer() {

        mAddContactMessageField.clearFocus();
        mAddContactSubjectTitle.requestFocus();
        hideKeyboard();

        if (hasLoadedSubjects) {
            if (mAddContactSubjectTitle.isSelected()) {
                getRouter().popCurrentController();
                mAddContactSubjectTitle.setSelected(false);
            } else {
                getRouter().pushController(RouterTransaction.with(ContactSelectSubjectController.newInstance(mContactSubjects))
                        .pushChangeHandler(new FadeChangeHandler(false))
                        .popChangeHandler(new FadeChangeHandler()));
                mAddContactSubjectTitle.setSelected(true);
                mAddContactOrderTitle.setSelected(false);
            }
        } else {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Loading subjects");
        }
    }

    @OnClick(R.id.controller_add_contact_order_container)
    void onClickOrderContainer() {

        mAddContactMessageField.clearFocus();
        mAddContactSubjectTitle.requestFocus();
        hideKeyboard();

        if (hasLoadedOrders) {
            if (mAddContactOrderTitle.isSelected()) {
                getRouter().popCurrentController();
                mAddContactOrderTitle.setSelected(false);
            } else {
                getRouter().pushController(RouterTransaction.with(ContactSelectOrderController.newInstance(mContactOrders))
                        .pushChangeHandler(new FadeChangeHandler(false))
                        .popChangeHandler(new FadeChangeHandler()));
                mAddContactSubjectTitle.setSelected(false);
                mAddContactOrderTitle.setSelected(true);
            }
        } else if (hasLoadedOrders && mContactOrders != null && mContactOrders.size() == 0) {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "You have no orders");

        } else {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Loading orders");
        }

    }

    @Override
    public void showContactFirstSubject(List<String> contactSubjectList) {

        mContactSubjects = contactSubjectList;
        hasLoadedSubjects = true;

        setupDefaultBottomButton("submit", onClickListener);

    }

    @Override
    public void showContactFirstOrder(List<ContactOrderList> contactOrderList) {

        mContactOrders = contactOrderList;
        hasLoadedOrders = true;
    }

    @Override
    public void contactCreatedSwitchView(CreateContactResponse createContactResponse) {
        if (createContactResponse.getCreateContact().getResult()) {

            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getActivity().getString(R.string.message_submitted));

            mActivity.onBackPressed();
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getActivity().getString(R.string.error_creating_message));
        }
    }

    @Override
    public void repliedContactSwitchView(ReplyContact replyContact) {

        if (replyContact.getResult()) {

            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getActivity().getString(R.string.message_submitted));
            getRouter().popCurrentController();
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getActivity().getString(R.string.error_creating_message));
        }
    }

    private void showContactFirstSubjectFromPreference(String subject) {

        mAddContactSubjectText.setText(subject);
    }

    private void showContactFirstOrderFromPreference(String order) {

        mAddContactOrderText.setText(order);
    }
}
