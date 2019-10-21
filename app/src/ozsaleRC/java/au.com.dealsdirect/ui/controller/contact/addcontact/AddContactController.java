package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseController implements AddContactMvpView {

    public static final String TAG = "AddContactController";
    public static final String INVOICE_NUMBER = "INVOICE_NUMBER";
    public static final String IS_CALLED_FROM_ORDERS = "IS_CALLED_FROM_ORDERS";
    public static final String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mAddContactToolbarRightOption;

    @BindView(R.id.partial_toolbar_title)
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

    private String mChosenOptionInvoice;

    private String mChosenSubject = "";
    private int mInvoiceNumber;
    private String mDescription = "";
    private boolean isCalledFromOrders = false;

    private ViewContactsMvpView mViewContactsMvpView;
    private boolean mHasSavedInstance = false;

    public static AddContactController newInstance() {
        return new AddContactController(
                new BundleBuilder(new Bundle()).build());
    }

    public static AddContactController newInstance(
            int invoiceNo,
            boolean isCalledFromOrders,
            String itemDescription) {

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putInt(INVOICE_NUMBER, invoiceNo)
                        .putBoolean(IS_CALLED_FROM_ORDERS, isCalledFromOrders)
                        .putString(ITEM_DESCRIPTION, itemDescription)
                        .build());
    }


    public AddContactController(Bundle args) {
        super(args);
        mInvoiceNumber = getArgs().getInt(INVOICE_NUMBER);
        mDescription = getArgs().getString(ITEM_DESCRIPTION, "");
        isCalledFromOrders = getArgs().getBoolean(IS_CALLED_FROM_ORDERS, false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.CONTACT_SUBJECT, mChosenSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mChosenSubject = savedInstanceState.getString(BundleKeys.CONTACT_SUBJECT);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_contact, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        if (mHasSavedInstance) {
            mViewContactsMvpView = mActivity.getContactsController();
        } else {
            mViewContactsMvpView = ((ViewContactsMvpView) mActivity.getHomeController().getCurrentRouter().getControllerWithTag(ViewContactsMvpView.TAG));
        }
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);

        mAddContactToolbarRightOption.setVisibility(View.INVISIBLE);
        mAddContactToolbarTitle.setText(R.string.new_message);
        mActivity.setDraggableViewPager(false);

        if (ContactPreferenceHelper.getChosenInvoice(mActivity).isEmpty() &&
                !isCalledFromOrders) {
            mAddContactOrderText.setText(getString(R.string.select_a_sale));
        } else {

            if (isCalledFromOrders) {
                ContactPreferenceHelper.setChosenInvoiceString(mActivity, String.valueOf(mInvoiceNumber));
                ContactPreferenceHelper.setChosenOrderString(mActivity, mDescription);
            }

            mAddContactOrderText.setText(ContactPreferenceHelper.getChosenInvoice(mActivity)
                    + " " + ContactPreferenceHelper.getChosenOrder(mActivity));
        }
        String message = ContactPreferenceHelper.getContactMessage(mActivity);
        if (!message.isEmpty()) {
            mAddContactMessageField.setText(message);
        }

        mChosenSubject = ContactPreferenceHelper.getChosenSubject(mActivity);
        if (!mChosenSubject.isEmpty()) {
            mAddContactSubjectText.setText(ContactPreferenceHelper.getChosenSubject(mActivity));
        }

        mAddContactMessageSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        ContactPreferenceHelper.clear(mActivity);
        hideKeyboard();
        mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(mActivity);
        CreateContactRequest createContactRequest = new CreateContactRequest();

        if (!mChosenOptionInvoice.isEmpty()) {
            createContactRequest.invoiceNo = Integer.valueOf(mChosenOptionInvoice);
        } else {
            createContactRequest.invoiceNo = 0;
        }

        createContactRequest.subj = mChosenSubject;

        if (mAddContactMessageField.getText().toString().isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.create_contact_fill_up));
        } else {
            createContactRequest.msg = mAddContactMessageField.getText().toString();
            mPresenter.createNewContact(createContactRequest);

        }
    }

    @Override
    public void onDestroyView(View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        ContactPreferenceHelper.setContactMessage(mActivity, mAddContactMessageField.getText().toString());
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
    }

    @OnClick({R.id.partial_toolbar_left_view})
    void onBack() {
        mActivity.onBackPressed();
    }


    @OnClick(R.id.controller_add_contact_order_container)
    void onClickOrderContainer() {
        mAddContactMessageField.clearFocus();
        hideKeyboard();

        getRouter().pushController(RouterTransaction.with(ContactSelectOrderController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void contactCreatedSwitchView(CreateContactResponse createContactResponse) {
        if (createContactResponse.getCreateContact().getResult()) {

            String saleName = !ContactPreferenceHelper.getChosenOrder(mActivity).isEmpty() ? ContactPreferenceHelper.getChosenOrder(mActivity) : "";
            int invoiceNo = !mChosenOptionInvoice.isEmpty() ? Integer.valueOf(mChosenOptionInvoice) : 0;

            RouterTransaction routerTransaction = RouterTransaction.with(ViewContactHistoryController.newInstance(
                    mChosenSubject,
                    saleName,
                    invoiceNo,
                    mAddContactMessageField.getText().toString(),
                    createContactResponse.getCreateContact().getValue(),
                    false))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            getRouter().popToRoot();
            getRouter().pushController(routerTransaction);

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.error_creating_message));
        }
    }

    @OnClick(R.id.controller_add_contact_subject_container)
    void addContact() {
        GateKeeper.push(getRouter(), GateKeeper.Destination.CONTACT_SELECT_SUBJECT, new HorizontalChangeHandler(), new HorizontalChangeHandler());
    }
}
