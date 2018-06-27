package au.com.dealsdirect.ui.controller.contact.selectorder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectorder.adapter.ContactOrderAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectOrderController extends BaseController implements ContactSelectOrderMvpView {

    public static final String TAG = "ContactSelectOrderController";

    private List<ContactOrderList> mContactOrders;

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

    private ContactOrderAdapter mAdapter;

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

        mContactOrdersToolarTitle.setText(getString(R.string.select_a_sale));
        mContactOrdersToolbarRightOption.setVisibility(View.INVISIBLE);

        mAdapter = new ContactOrderAdapter(mContactOrders, mPresenter);

        mContactOrdersRecyclerView.setAdapter(mAdapter);
        mContactOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void onContactOrderSelected(ContactOrderList contactOrder) {
        ContactPreferenceHelper.setChosenInvoiceString(mActivity, contactOrder.getInvoiceNo());
        ContactPreferenceHelper.setChosenOrderString(mActivity, contactOrder.getDescription());

        mActivity.onBackPressed();
    }

    @Override
    public void showContactOrders(List<ContactOrderList> contactOrderList) {
        mContactOrders = contactOrderList;
        if (mContactOrders.isEmpty()) {
            mSelectSaleSubtitle.setVisibility(View.GONE);
            mContactOrdersRecyclerView.setVisibility(View.GONE);
            mContactSelectOrderPlaceholder.setVisibility(View.VISIBLE);
            mContactSelectOrderPlaceholder.setOnClickListener(v -> mActivity.onBackPressed());
        } else {
            mSelectSaleSubtitle.setVisibility(View.VISIBLE);
            mContactSelectOrderPlaceholder.setVisibility(View.GONE);
            mContactOrdersRecyclerView.setVisibility(View.VISIBLE);
            mAdapter.replaceData(mContactOrders);
        }
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        mActivity.onBackPressed();
    }
}
