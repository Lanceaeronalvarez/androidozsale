package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.ui.base.BaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryController extends BaseToolBarController implements ViewContactHistoryMvpView {

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

    @BindView(R.id.contact_history_header_title)
    TextView mContactHistoryTitle;

    @BindView(R.id.contact_history_header_time_stamp)
    TextView mContactHistoryTimeStamp;

    @BindView(R.id.controller_view_contact_history_header_invoice_value)
    TextView mContactHistoryHeaderInvoiceValue;

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

        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_view_contact_history, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mToolbarTitle.setText("message");
        setUp(view);
        mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
    }

    @Override
    protected void setUp(View view) {

        KeyboardUtils.setKeyboardAdjustResize(mActivity);
        mActivity.getMainController().hideBottomNav();

//        mContactHistoryTimeStamp.setText(mTimeStamp);

        if (!mSaleNameObject.isEmpty()) {
            mContactHistoryTitle.setText(mSaleNameObject);
        } else {
            mContactHistoryTitle.setText(R.string.no_order_number);
        }

        mContactHistoryHeaderInvoiceValue.setText(""+mInvoiceNumber);
        mContactHistoryTimeStamp.setText(mTimeStamp);
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
//        layoutManager.setStackFromEnd(true);

        contactHistoryRecyclerView.setAdapter(adapter);
        contactHistoryRecyclerView.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.controller_view_contacts_history_reply_button)
    void onReplyClick() {
        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance("CONTACT_HISTORY", mContactSubject,mInvoiceNumber))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void repliedContactSwitchView(ReplyContact replyContact) {
        if (replyContact.getResult()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.message_submitted));
            mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
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
