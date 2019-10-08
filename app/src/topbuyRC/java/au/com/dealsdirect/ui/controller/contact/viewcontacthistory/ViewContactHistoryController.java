package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

import static au.com.dealsdirect.utils.BundleKeys.CONTACT_INVOICE;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_INVOICE_NUMBER;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_NAME;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_NUMBER;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_SUBJECT;
import static au.com.dealsdirect.utils.BundleKeys.CONTACT_TIME_STAMP;
import static au.com.dealsdirect.utils.BundleKeys.FROM_FRAGMENT_ID;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryController extends SwipeableBaseToolBarController implements ViewContactHistoryMvpView {

    public static final String TAG = "ViewContactHistoryController";

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
                        .putInt(CONTACT_NUMBER, contactNo)
                        .putString(CONTACT_NAME, saleName)
                        .putInt(CONTACT_INVOICE_NUMBER, invoiceNo)
                        .putString(CONTACT_TIME_STAMP, lastAnswer)
                        .putString(CONTACT_SUBJECT, contactSubject)
                        .build());
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
        mSaleNameObject = args.getString(CONTACT_NAME);
        mInvoiceNumber = args.getInt(CONTACT_INVOICE_NUMBER);
        mTimeStamp = args.getString(CONTACT_TIME_STAMP);
        mContactNumber = args.getInt(CONTACT_NUMBER);
        mContactSubject = args.getString(CONTACT_SUBJECT);
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
        setupSwipingBehavior();
        setUp(view);
        setupDefaultBottomButton("reply", view1 -> {
            onReplyClick();
        });
        mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(CONTACT_NUMBER)));
    }

    @Override
    protected void setUp(View view) {
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

    private void onReplyClick() {
        Bundle bundle = new Bundle();
        bundle.putString(FROM_FRAGMENT_ID,"CONTACT_HISTORY");
        bundle.putString(CONTACT_SUBJECT, mContactSubject);
        bundle.putInt(CONTACT_INVOICE, mInvoiceNumber);
        bundle.putInt(CONTACT_NUMBER, mContactNumber);


        GateKeeper.push(getRouter(),
                GateKeeper.Destination.ADD_CONTACT,
                bundle
                ,new VerticalChangeHandler(false)
                ,new VerticalChangeHandler());
//
//        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance("CONTACT_HISTORY", mContactSubject,mInvoiceNumber))
//                .pushChangeHandler(new HorizontalChangeHandler())
//                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void repliedContactSwitchView(ReplyContact replyContact) {
        if (replyContact.getResult()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.message_submitted));
            mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(CONTACT_NUMBER)));
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
