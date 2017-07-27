package au.com.dealsdirect.ui.controller.contact.addcontact.selectorder;

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
import au.com.dealsdirect.ui.controller.contact.addcontact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectorder.adapter.ContactOrderAdapter;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectorder.listener.ContactOrderClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectOrderController extends BaseController
        implements ContactSelectOrderMvpView, ContactOrderClickListener {

    public static final String TAG = "ContactSelectOrderController";

    private static final String KEY_TEXT = "ContactSelectOrderController.KEY_TEXT";

    static List<ContactOrderList> mContactOrders;

    @Inject
    ContactSelectOrderMvpPresenter<ContactSelectOrderMvpView> mPresenter;

    @BindView(R.id.controller_contact_select_order_header)
    TextView mContactOrdersHeader;

    @BindView(R.id.my_contact_select_order_recycler_view)
    RecyclerView mContactOrdersRecyclerView;

    @BindView(R.id.controller_contact_select_order_placeholder)
    TextView mContactSelectOrderPlaceholder;

    private ContactOrderClickListener mContactOrderItemListener;


    public static final String ARGUMENT_VIEW_CONTRACT_SELECT_ORDER_FRAGMENT_ID =
            "MY_CONTRACT_SELECT_ORDER_FRAGMENT_ID";

    public static ContactSelectOrderController newInstance(
            List<ContactOrderList> contactOrders) {

        mContactOrders = contactOrders;
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

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mContactOrderItemListener = this;
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        if (mContactOrders.isEmpty()) {
            mContactOrdersHeader.setVisibility(View.GONE);
            mContactOrdersRecyclerView.setVisibility(View.GONE);
            mContactSelectOrderPlaceholder.setVisibility(View.VISIBLE);
            mContactSelectOrderPlaceholder.setOnClickListener(v -> getActivity().onBackPressed());
        } else {
            mContactSelectOrderPlaceholder.setVisibility(View.GONE);
            mContactOrdersRecyclerView.setVisibility(View.VISIBLE);

            final ContactOrderAdapter adapter
                    = new ContactOrderAdapter
                    (mContactOrders, getActivity(), mContactOrderItemListener);

            mContactOrdersRecyclerView.setAdapter(adapter);
            mContactOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void onContactOrderItemClicked(ContactOrderList contactOrder) {
        ContactPreferenceHelper.setChosenInvoiceString(
                getActivity(),
                contactOrder.getInvoiceNo());

        ContactPreferenceHelper.setChosenOrderString(
                getActivity(),
                contactOrder.getDescription());

        getActivity().onBackPressed();
    }

    @Override
    public void showContactOrders(List<ContactOrderList> contacOrderList) {

    }
}
