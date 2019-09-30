package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryController extends BaseController implements ViewContactHistoryMvpView {

    public static final String TAG = "ViewContactHistoryController";
    private static final String KEY_CONTACT_NO = "ContactHistoryNo";
    private static final String KEY_CONTACT_INVOICE_NO = "ContactHistoryInvoiceNo";
    private static final String KEY_CONTACT_TIMESTAMP = "ContactHistoryTimeStamp";
    private static final String KEY_CONTACT_NAME = "ContactHistoryName";
    private static final String KEY_CONTACT_SUBJECT = "ContactSubject";
    private static final String KEY_IS_FROM_RETURN_DETAILS = "KEY_IS_FROM_RETURN_DETAILS";

    @BindView(R.id.contact_history_recycler_view)
    RecyclerView mContactHistoryRecyclerView;

    @BindView(R.id.partial_toolbar_details_subtitle_textview)
    TextView mContactHistorySaleSubTitle;

    @BindView(R.id.partial_toolbar_field_title_right_option)
    ImageView mContactHistoryRightOption;

    @BindView(R.id.partial_toolbar_details_upper_title_textview)
    TextView mContactHistoryTitle;

    @BindView(R.id.controller_view_contacts_history_message_field)
    EditText mContactHistoryMessageField;

    private String mSaleNameObject;
    private String mTimeStamp;
    private int mInvoiceNumber;
    private int mContactNumber;
    private String mContactSubject;
    private boolean mHasSavedInstance = false;
    private boolean isFromReturnDetails = false;

    @Inject
    ViewContactHistoryPresenter<ViewContactHistoryMvpView> mPresenter;

    public static ViewContactHistoryController newInstance(
            String contactSubject,
            String saleName,
            int invoiceNo,
            String lastAnswer,
            int contactNo,
            boolean fromReturnDetails) {

        return new ViewContactHistoryController(
                new BundleBuilder(new Bundle())
                        .putInt(KEY_CONTACT_NO, contactNo)
                        .putString(KEY_CONTACT_NAME, saleName)
                        .putInt(KEY_CONTACT_INVOICE_NO, invoiceNo)
                        .putString(KEY_CONTACT_TIMESTAMP, lastAnswer)
                        .putString(KEY_CONTACT_SUBJECT, contactSubject)
                        .putBoolean(KEY_IS_FROM_RETURN_DETAILS, fromReturnDetails)
                        .build());
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
        mSaleNameObject = getArgs().getString(KEY_CONTACT_NAME);
        mInvoiceNumber = getArgs().getInt(KEY_CONTACT_INVOICE_NO);
        mTimeStamp = getArgs().getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = getArgs().getInt(KEY_CONTACT_NO);
        mContactSubject = getArgs().getString(KEY_CONTACT_SUBJECT);
        isFromReturnDetails = getArgs().getBoolean(KEY_IS_FROM_RETURN_DETAILS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTACT_NAME, mSaleNameObject);
        outState.putInt(KEY_CONTACT_INVOICE_NO, mInvoiceNumber);
        outState.putString(KEY_CONTACT_TIMESTAMP, mTimeStamp);
        outState.putInt(KEY_CONTACT_NO, mContactNumber);
        outState.putString(KEY_CONTACT_SUBJECT, mContactSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSaleNameObject = savedInstanceState.getString(KEY_CONTACT_NAME);
        mInvoiceNumber = savedInstanceState.getInt(KEY_CONTACT_INVOICE_NO);
        mTimeStamp = savedInstanceState.getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = savedInstanceState.getInt(KEY_CONTACT_NO);
        mContactSubject = savedInstanceState.getString(KEY_CONTACT_SUBJECT);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_view_contact_history, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
    }

    @Override
    protected void setUp(View view) {

        KeyboardUtils.setKeyboardAdjustResize(mActivity);

        mContactHistoryRightOption.setVisibility(View.INVISIBLE);
        mContactHistoryTitle.setText(mContactSubject);

        if (!mSaleNameObject.isEmpty()) {
            mContactHistorySaleSubTitle.setText(mInvoiceNumber + ": " + mSaleNameObject);
        } else {
            mContactHistorySaleSubTitle.setText(R.string.no_order_number);
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
    }

    @Override
    public void showContactHistory(java.util.List<List> myContactItems) {

        Log.d("contacts", myContactItems.size() + " ");
        ContactHistoryAdapter adapter = new ContactHistoryAdapter(myContactItems, mActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);

        mContactHistoryRecyclerView.setAdapter(adapter);
        mContactHistoryRecyclerView.setLayoutManager(layoutManager);
        mContactHistoryRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @OnClick(R.id.partial_toolbar_field_title_left_option)
    void onBackClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_view_contacts_history_reply_button)
    void onReplyClick() {

        KeyboardUtils.hideSoftInput(mActivity);

        String replyMessage = mContactHistoryMessageField.getText().toString();
        int contactId = mContactNumber;

        ReplyContactRequest replyContactRequest = new ReplyContactRequest();
        replyContactRequest.comments = replyMessage;
        replyContactRequest.contactNo = contactId;

        if (replyContactRequest.comments.isEmpty()) {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.create_contact_fill_up)
            );

        } else {
            mPresenter.replyContact(replyContactRequest);
        }
    }

    @Override
    public void repliedContactSwitchView(ReplyContact replyContact) {
        if (replyContact.getResult()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.message_submitted));
            mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
            mContactHistoryMessageField.setText("");
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.error_creating_message));
        }
    }

    private GetContactHistoryRequest createContactHistoryRequest(int contactNo) {
        GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
        getContactHistoryRequest.contactNo = contactNo;
        return getContactHistoryRequest;
    }
}
