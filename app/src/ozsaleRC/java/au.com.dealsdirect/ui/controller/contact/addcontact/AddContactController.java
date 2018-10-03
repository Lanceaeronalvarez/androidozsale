package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseController implements AddContactMvpView {

    public static final String TAG = "AddContactController";

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

    private ViewContactsMvpView mViewContactsMvpView;

    public static AddContactController newInstance() {
        return new AddContactController(
                new BundleBuilder(new Bundle()).build());
    }


    public AddContactController(Bundle args) {
        super(args);
        if (!args.isEmpty()) {
            mChosenSubject = args.getString(BundleKeys.CONTACT_SUBJECT);
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_contact, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mViewContactsMvpView = ((ViewContactsMvpView) mActivity.getHomeController().getCurrentRouter().getControllerWithTag(ViewContactsMvpView.TAG));
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
        if (!mPresenter.isTablet()) mActivity.getMainController().hideBottomNav();

        mAddContactToolbarRightOption.setVisibility(View.INVISIBLE);
        mAddContactToolbarTitle.setText(R.string.new_message);
        mActivity.setDraggableViewPager(false);

        if(ContactPreferenceHelper.getChosenInvoice(mActivity).isEmpty()) {
            mAddContactOrderText.setText(getString(R.string.select_a_sale));
        } else {
            mAddContactOrderText.setText(ContactPreferenceHelper.getChosenInvoice(mActivity) + " " + ContactPreferenceHelper.getChosenOrder(mActivity));
        }
        mAddContactSubjectText.setText(mChosenSubject);

        mAddContactMessageSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage(){
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
        ContactPreferenceHelper.clear(mActivity);
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }


    @OnClick(R.id.partial_toolbar_left_view)
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
                    createContactResponse.getCreateContact().getValue()))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());

            if (mPresenter.isTablet() && getBoolean(R.bool.master_detail_enabled)) {
                mViewContactsMvpView.getPresenter().loadContacts();
                mViewContactsMvpView.getDisplayRouter().popToRoot();
                GateKeeper.setRoot(mViewContactsMvpView.getDisplayRouter(), GateKeeper.Destination.CONTACT_HISTORY, routerTransaction);
            } else {
                getRouter().popToRoot();
                getRouter().pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                        mChosenSubject,
                        saleName,
                        invoiceNo,
                        mAddContactMessageField.getText().toString(),
                        createContactResponse.getCreateContact().getValue()))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));

            }

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.error_creating_message));
        }

    }

}
