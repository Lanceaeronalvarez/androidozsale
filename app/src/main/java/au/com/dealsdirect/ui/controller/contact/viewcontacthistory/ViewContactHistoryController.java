package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryController extends BaseController implements ViewContactHistoryMvpView{

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

    @BindView(R.id.controller_view_contact_history_header_title)
    TextView contactHistoryHeaderTitle;

    @BindView(R.id.contact_history_header_invoice_value)
    TextView contactHistoryInvoiceText;

    @BindView(R.id.contact_history_header_time_stamp)
    TextView contactHistoryTimeStamp;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mContactHistoryRightOption;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mContactHistoryTitle;

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
                        .putInt(KEY_CONTACT_NO,contactNo)
                        .putString(KEY_CONTACT_NAME, saleName)
                        .putInt(KEY_CONTACT_INVOICE_NO, invoiceNo)
                        .putString(KEY_CONTACT_TIMESTAMP,lastAnswer)
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
        mPresenter.loadContactHistory(getArgs().getInt(KEY_CONTACT_NO));
    }

    @Override
    protected void setUp(View view) {

        // mContactList.getInvoiceNo()

        contactHistoryHeaderTitle.setText(mSaleNameObject);
        contactHistoryInvoiceText.setText(mInvoiceNumber+"");
        contactHistoryTimeStamp.setText(mTimeStamp);
        mContactHistoryRightOption.setVisibility(View.INVISIBLE);
        mContactHistoryTitle.setText(R.string.contact_history);
        // mContactList.getInvoiceNo()

        if(!mSaleNameObject.isEmpty()){

            contactHistoryHeaderTitle.setText(mSaleNameObject);
        }else{

            contactHistoryHeaderTitle.setText(R.string.no_order_number);
        }

    }

    @Override
    public void showContactHistory(java.util.List<List> myContactItems) {

        Log.d("contacts" , myContactItems.size()+ " ");
        final ContactHistoryAdapter adapter
                = new ContactHistoryAdapter
                (myContactItems,getActivity());

        contactHistoryRecyclerView
                .setAdapter(adapter);
        contactHistoryRecyclerView
                .setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.controller_view_contacts_history_reply_button)
    void onReplyClick(){

        Bundle bundle = new BundleBuilder(new Bundle())
                .putInt("CONTACT_NUMBER",mContactNumber)
                .putString("CONTACT_SUBJECT", mContactSubject)
                .putInt("CONTACT_INVOICE_NUMBER", mInvoiceNumber)
                .build();

        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance("CONTACT_HISTORY", bundle))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}
