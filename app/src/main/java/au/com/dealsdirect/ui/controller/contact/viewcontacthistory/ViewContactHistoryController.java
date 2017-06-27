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

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

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

    @BindView(R.id.contact_history_recycler_view)
    RecyclerView contactHistoryRecyclerView;

    @BindView(R.id.controller_view_contact_history_header_title)
    TextView contactHistoryHeaderTitle;

    @BindView(R.id.contact_history_header_invoice_value)
    TextView contactHistoryInvoiceText;

    @BindView(R.id.contact_history_header_time_stamp)
    TextView contactHistoryTimeStamp;

    @Inject
    ViewContactHistoryPresenter<ViewContactHistoryMvpView> mPresenter;

    public static ViewContactHistoryController newInstance(
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
                        .build());
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
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

        mPresenter.loadContactHistory(getArgs().getInt(KEY_CONTACT_NO));
    }

    @Override
    protected void setUp(View view) {

        // mContactList.getInvoiceNo()
        String saleNameObject = getArgs().getString(KEY_CONTACT_NAME);
        int invoiceNumber = getArgs().getInt(KEY_CONTACT_INVOICE_NO);
        String timeStamp = getArgs().getString(KEY_CONTACT_TIMESTAMP);

        contactHistoryHeaderTitle.setText(saleNameObject);
        contactHistoryInvoiceText.setText(invoiceNumber);
        contactHistoryTimeStamp.setText(timeStamp);

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
}
