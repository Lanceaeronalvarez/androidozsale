package au.com.dealsdirect.ui.controller.contact.selectorder;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.addcontact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectorder.adapter.ContactOrderAdapter;
import au.com.dealsdirect.ui.controller.contact.selectorder.listener.ContactOrderClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/*
 * Created by DP on 05/06/2017.
 */

public class ContactSelectOrderController extends SwipeableBaseToolBarController
        implements ContactSelectOrderMvpView, ContactOrderClickListener {

    public static final String TAG = "ContactSelectOrderController";

    private static final String KEY_TEXT = "ContactSelectOrderController.KEY_TEXT";

    static List<ContactOrderList> mContactOrders;

    @Inject
    ContactSelectOrderMvpPresenter<ContactSelectOrderMvpView> mPresenter;

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

        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_contact_select_order, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mToolbarTitle.setText("select order");
        setupSwipingBehavior();
        mContactOrderItemListener = this;
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        if (mContactOrders.isEmpty()) {
            mContactOrdersRecyclerView.setVisibility(View.GONE);
            mContactSelectOrderPlaceholder.setVisibility(View.VISIBLE);
            mContactSelectOrderPlaceholder.setOnClickListener(v -> mActivity.onBackPressed());
        } else {
            mContactSelectOrderPlaceholder.setVisibility(View.GONE);
            mContactOrdersRecyclerView.setVisibility(View.VISIBLE);

            final ContactOrderAdapter adapter
                    = new ContactOrderAdapter
                    (mContactOrders, mActivity, mContactOrderItemListener);

            mContactOrdersRecyclerView.setAdapter(adapter);
            mContactOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
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
                mActivity,
                contactOrder.getInvoiceNo());

        ContactPreferenceHelper.setChosenOrderString(
                mActivity,
                contactOrder.getDescription());

        mActivity.onBackPressed();
    }

    @Override
    public void showContactOrders(List<ContactOrderList> contacOrderList) {

    }
}
