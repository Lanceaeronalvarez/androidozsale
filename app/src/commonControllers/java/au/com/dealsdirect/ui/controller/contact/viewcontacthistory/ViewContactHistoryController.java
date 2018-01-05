package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

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
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryController extends BaseController implements ViewContactHistoryMvpView {

    public static final String TAG = "ViewContactHistoryController";
    private static final String KEY_TEXT = "ViewContactHistoryController.KEY_TEXT";
    private static final String KEY_CONTACT_NO = "ContactHistoryNo";
    private static final String KEY_CONTACT_INVOICE_NO = "ContactHistoryInvoiceNo";
    private static final String KEY_CONTACT_TIMESTAMP = "ContactHistoryTimeStamp";
    private static final String KEY_CONTACT_NAME = "ContactHistoryName";
    private static final String KEY_CONTACT_SUBJECT = "ContactSubject";
    private static final String KEY_CONTACT_ORDER = "ContactOrder";

    @BindView(R.id.contact_history_recycler_view)
    RecyclerView contactHistoryRecyclerView;

    @BindView(R.id.controller_view_contact_history_header_subject)
    TextView mContactHistorySubject;

    @BindView(R.id.controller_view_contact_history_header_sale)
    TextView mContactHistorySale;

    @BindView(R.id.contact_history_header_time_stamp)
    TextView mContactHistoryTimeStamp;

    @BindView(R.id.contact_history_list_time_stamp)
    TextView mContactHistoryListTimeStamp;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mContactHistoryRightOption;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mContactHistoryTitle;

    @BindView(R.id.controller_view_contacts_history_message_field)
    EditText mContactHistoryMessageField;

    private String mSaleNameObject;
    private String mTimeStamp;
    private int mInvoiceNumber;
    private int mContactNumber;
    private String mContactSubject;

    @Inject
    ViewContactHistoryPresenter<ViewContactHistoryMvpView> mPresenter;

    public static ViewContactHistoryController newInstance(
            String contactSubject,
            String saleName,
            int invoiceNo,
            String lastAnswer,
            int contactNo) {

        return new ViewContactHistoryController(
                new BundleBuilder(new Bundle())
                        .putInt(KEY_CONTACT_NO, contactNo)
                        .putString(KEY_CONTACT_NAME, saleName)
                        .putInt(KEY_CONTACT_INVOICE_NO, invoiceNo)
                        .putString(KEY_CONTACT_TIMESTAMP, lastAnswer)
                        .putString(KEY_CONTACT_SUBJECT, contactSubject)
                        .build());
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
        mSaleNameObject = getArgs().getString(KEY_CONTACT_NAME);
        mInvoiceNumber = getArgs().getInt(KEY_CONTACT_INVOICE_NO);
        mTimeStamp = getArgs().getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = getArgs().getInt(KEY_CONTACT_NO);
        mContactSubject = getArgs().getString(KEY_CONTACT_SUBJECT);
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
        mActivity.getMainController().hideBottomNav();

        mContactHistorySubject.setText(StringUtils.toTitleCase(mContactSubject));
        mContactHistoryTimeStamp.setText(mTimeStamp);
        mContactHistoryListTimeStamp.setText(mTimeStamp);
        mContactHistoryRightOption.setVisibility(View.INVISIBLE);
        mContactHistoryTitle.setText(R.string.contact_history);

        if (!mSaleNameObject.isEmpty()) {
            mContactHistorySale.setText(mSaleNameObject);
        } else {
            mContactHistorySale.setText(R.string.no_order_number);
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showContactHistory(java.util.List<List> myContactItems) {

        Log.d("contacts", myContactItems.size() + " ");
        ContactHistoryAdapter adapter = new ContactHistoryAdapter(myContactItems, mActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);

        contactHistoryRecyclerView.setAdapter(adapter);
        contactHistoryRecyclerView.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_view_contacts_history_reply_button)
    void onReplyClick() {

//        Bundle bundle = new BundleBuilder(new Bundle())
//                .putInt("CONTACT_NUMBER", mContactNumber)
//                .putString("CONTACT_SUBJECT", mContactSubject)
//                .putInt("CONTACT_INVOICE_NUMBER", mInvoiceNumber)
//                .build();
//
//        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance("CONTACT_HISTORY", bundle))
//                .pushChangeHandler(new HorizontalChangeHandler())
//                .popChangeHandler(new HorizontalChangeHandler()));

        KeyboardUtils.hideSoftInput(mActivity);

        String replyMessage = mContactHistoryMessageField.getText().toString();
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

    public GetContactHistoryRequest createContactHistoryRequest(int contactNo) {
        GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
        getContactHistoryRequest.contactNo = contactNo;
        return  getContactHistoryRequest;
    }
}
