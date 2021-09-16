package au.com.dealsdirect.ui.controller.contact.selectsubject;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.selectsubject.adapter.ContactSubjectAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectSubjectController extends BaseController
        implements ContactSelectSubjectMvpView {

    public static final String TAG = "ContactSelectSubjectController";
    public static final String KEY_INVOICE_NUMBER = "ContactSelectSubjectController.InvoiceNumber";
    public static final String KEY_ORDER_NUMBER = "ContactSelectSubjectController.OrderNumber";

    private ContactSubjectAdapter mAdapter;
    private boolean mHasSavedInstance = false;

    private String mSubjecId = "";
    private String mSubject = "";
    private boolean mIsInvoiceRequired = false;
    private List<String> mActions = new ArrayList<>();

    private int mOrderNumber = 0;
    private int mInvoiceNumber = 0;

    @Inject
    ContactSelectSubjectMvpPresenter<ContactSelectSubjectMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_left_view)
    TextView mCancelButton;

    @BindView(R.id.partial_toolbar_title)
    TextView mViewContactsToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mViewContactsToolbarRightOption;

    @BindView(R.id.my_contact_select_subject_recycler_view)
    RecyclerView mContactSelectSubjectControllerRecyclerView;

    public static ContactSelectSubjectController newInstance() {
        return new ContactSelectSubjectController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public static ContactSelectSubjectController newInstance(int orderNumber, int invoiceNumber) {
        return new ContactSelectSubjectController(
                new BundleBuilder(new Bundle())
                        .putInt(KEY_INVOICE_NUMBER, invoiceNumber)
                        .putInt(KEY_ORDER_NUMBER, orderNumber)
                        .build());
    }

    public ContactSelectSubjectController(Bundle args) {
        super(args);
        mInvoiceNumber = args.getInt(KEY_INVOICE_NUMBER, 0);
        mOrderNumber = args.getInt(KEY_ORDER_NUMBER, 0);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_contact_select_subject, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mPresenter.loadContactUsSubjects();
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mCancelButton.setText(getResource().getString(R.string.cancel));

        mViewContactsToolarTitle.setText(getResource().getString(R.string.select_a_subject));
        mViewContactsToolbarRightOption.setVisibility(View.INVISIBLE);

        mAdapter = new ContactSubjectAdapter(new ArrayList<>(), this::onContactSubjectItemSelected);

        mContactSelectSubjectControllerRecyclerView.setAdapter(mAdapter);
        mContactSelectSubjectControllerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
    }

    public void onContactSubjectItemSelected(ContactSubjectResponse response) {
        mSubjecId = response.getId();
        mSubject = response.getName();
        mIsInvoiceRequired = response.getRequiresInvoice();
        mActions = response.getActions();

        AddContactController controller = (AddContactController) getRouter().getControllerWithTag(AddContactController.TAG);
        if (controller == null) {
            controller = AddContactController.newInstance();
            setupAddContactController(controller);
            RouterTransaction routerTransaction = RouterTransaction.with(controller)
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler())
                    .tag(AddContactController.TAG);
            getRouter().replaceTopController(routerTransaction);
        } else {
            setupAddContactController(controller);
            getRouter().popCurrentController();
        }
    }

    private void setupAddContactController(AddContactController controller) {
        controller.setSubjectId(mSubjecId);
        controller.setSubject(mSubject);
        controller.setIsInvoiceRequired(mIsInvoiceRequired);
        controller.setInvoiceNumber(mInvoiceNumber);
        controller.setOrderNumber(mOrderNumber);
        controller.setActions(mActions);
    }

    @Override
    public void showContactSubjects(List<ContactSubjectResponse> contactSubjectList) {
        mAdapter.replaceData(contactSubjectList);
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        mActivity.onBackPressed();
    }

    public int getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.mOrderNumber = orderNumber;
    }

    public int getInvoiceNumber() {
        return mInvoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.mInvoiceNumber = invoiceNumber;
    }
}
