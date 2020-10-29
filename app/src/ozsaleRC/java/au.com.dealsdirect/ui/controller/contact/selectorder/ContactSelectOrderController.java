package au.com.dealsdirect.ui.controller.contact.selectorder;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.selectorder.adapter.ContactOrderAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectOrderController extends BaseController implements ContactSelectOrderMvpView {

    public static final String TAG = "ContactSelectOrderController";

    private List<ContactOrderResponse> mContactOrders;

    @Inject
    ContactSelectOrderMvpPresenter<ContactSelectOrderMvpView> mPresenter;

    @BindView(R.id.partial_toolbar_title)
    TextView mContactOrdersToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mContactOrdersToolbarRightOption;

    @BindView(R.id.my_contact_select_order_recycler_view)
    RecyclerView mContactOrdersRecyclerView;

    @BindView(R.id.controller_contact_select_order_placeholder)
    TextView mContactSelectOrderPlaceholder;

    @BindView(R.id.controller_select_sale_subtitle)
    TextView mSelectSaleSubtitle;

    @BindView(R.id.controller_contact_select_order_invoice_input_container)
    ViewGroup mInvoiceInputContainer;

    @BindView(R.id.controller_contact_select_order_invoice_input)
    EditText mInvoiceInputField;

    private ContactOrderAdapter mAdapter;
    private int screenWidth;
    private int screenHeight;

    private int mOrderNumber;
    private int mInvoiceNumber;

    private List<String> mActions = new ArrayList<>();

    private OnInvoiceNumberSubmittedListener listener = null;

    public static ContactSelectOrderController newInstance() {
        return new ContactSelectOrderController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ContactSelectOrderController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_contact_select_order, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mPresenter.loadContactUsOrders();

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {

        mContactOrdersToolarTitle.setText(getString(R.string.select_invoice));
        mContactOrdersToolbarRightOption.setVisibility(View.INVISIBLE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;

        mAdapter = new ContactOrderAdapter(mActivity, mContactOrders, mPresenter, screenHeight, screenWidth);

        mContactOrdersRecyclerView.setAdapter(mAdapter);
        mContactOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mSelectSaleSubtitle.setVisibility(View.GONE);
        mContactOrdersRecyclerView.setVisibility(View.GONE);
        mInvoiceInputContainer.setVisibility(View.GONE);
        mContactSelectOrderPlaceholder.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void onContactOrderSelected(ContactOrderResponse contactOrderResponse) {
        submitInvoiceNumber(contactOrderResponse.getNumber(), contactOrderResponse.getOrderNumber());
    }

    @Override
    public void showContactOrders(List<ContactOrderResponse> contactOrderList) {
        mContactOrders = filterInvoiceList(contactOrderList);
        mContactSelectOrderPlaceholder.setVisibility(View.GONE);
        mSelectSaleSubtitle.setVisibility(View.VISIBLE);
        if (mContactOrders.isEmpty()) {
            mSelectSaleSubtitle.setText(getString(R.string.enter_invoice_number));
            mContactOrdersRecyclerView.setVisibility(View.GONE);
            mInvoiceInputContainer.setVisibility(View.VISIBLE);
        } else {
            mSelectSaleSubtitle.setText(getString(R.string.my_sales));
            mContactOrdersRecyclerView.setVisibility(View.VISIBLE);
            mInvoiceInputContainer.setVisibility(View.GONE);
            mAdapter.replaceData(mContactOrders);
        }
    }

    private List<ContactOrderResponse> filterInvoiceList(List<ContactOrderResponse> source) {
        if (mActions.isEmpty()) {
            return source;
        }
        List<ContactOrderResponse> output = new ArrayList<>();
        for (ContactOrderResponse invoice : source) {
            if (isInvoiceValid(invoice, mActions)) {
                output.add(invoice);
            }
        }
        return output;
    }

    private boolean isInvoiceValid(ContactOrderResponse invoice, List<String> actions) {
        for (String invoiceAction : invoice.getActions()) {
            for (String action : actions) {
                if (invoiceAction.equals(action)) {
                    return true;
                }
            }
        }
        return false;
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_contact_select_order_invoice_input_button)
    void onSubmitClick() {
        // no order number?
        submitInvoiceNumber(-1, Integer.parseInt(mInvoiceInputField.getText().toString()));
    }

    private void submitInvoiceNumber(int orderNumber, int invoiceNumber) {
        mOrderNumber = orderNumber;
        mInvoiceNumber = invoiceNumber;
        if (listener != null) {
            listener.onInvoiceNumberSubmitted(mOrderNumber, mInvoiceNumber);
        }
        mActivity.onBackPressed();
    }

    public int getInvoiceNumber() {
        return mInvoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        mInvoiceNumber = invoiceNumber;
    }

    public List<String> getActions() {
        return mActions;
    }

    public void setActions(List<String> actions) {
        mActions = actions;
    }

    public void setListener(OnInvoiceNumberSubmittedListener listener) {
        this.listener = listener;
    }

    public interface OnInvoiceNumberSubmittedListener {
        void onInvoiceNumberSubmitted(int orderNumber, int invoiceNumber);
    }
}
