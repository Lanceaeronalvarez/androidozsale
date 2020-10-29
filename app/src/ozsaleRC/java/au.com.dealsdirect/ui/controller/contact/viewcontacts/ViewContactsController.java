package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.listener.ContactClickListener;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsController extends BaseController implements ViewContactsMvpView {

    private ContactsAdapter mContactAdapter;

    @BindView(R.id.partial_toolbar_title)
    TextView mViewContactsToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mViewContactsToolbarRightOption;

    @BindView(R.id.partial_toolbar_left_view)
    View mViewContactsToolbarLeftOption;

    @BindView(R.id.contacts_recycler_view_container)
    ViewGroup mViewContactsRecyclerViewContainer;

    @BindView(R.id.contacts_recycler_view)
    RecyclerView mViewContactsRecyclerView;

    @BindView(R.id.no_contacts_placeholder)
    RelativeLayout mPlaceholderLayout;

    @BindView(R.id.controller_contacts_new_message_button)
    Button mViewContactsAddNewMessage;

    @Inject
    ViewContactsMvpPresenter<ViewContactsMvpView> mPresenter;

    public ViewContactsMvpView viewContactsMvpView;
    private boolean mHasSavedState;

    public static ViewContactsController newInstance() {

        return new ViewContactsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ViewContactsController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_view_contacts, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.loadContacts();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.loadContacts();
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mActivity.getMainController().showBottomNav();

        setUp(view);
        mPresenter.loadContacts();

    }

    @Override
    protected void setUp(View view) {

        assert (mActivity) != null;

        mActivity.setContactsController(this);

        mViewContactsAddNewMessage.setVisibility(View.VISIBLE);

        mViewContactsToolarTitle.setText(getResource().getString(R.string.account_contact_us));
        mViewContactsToolbarLeftOption.setVisibility(mPresenter.isTablet() ? View.INVISIBLE : View.VISIBLE);
        mViewContactsToolbarRightOption.setVisibility(View.INVISIBLE);

        mContactAdapter = new ContactsAdapter(new ArrayList<>(), mPresenter, new ContactClickListener() {
            @Override
            public void onCreateMessageClick() {
                addContact();
            }
        });

        mViewContactsRecyclerView.setAdapter(mContactAdapter);
        mViewContactsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        hideKeyboard();
    }

    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedState = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showContactItems(List<GetContactsResponse> contacts) {

        if (contacts != null && contacts.size() != 0) {
            mContactAdapter.replace(contacts);
            mViewContactsRecyclerViewContainer.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
            mViewContactsAddNewMessage.setVisibility(View.VISIBLE);
        } else {
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mViewContactsRecyclerViewContainer.setVisibility(View.GONE);
            mViewContactsAddNewMessage.setVisibility(View.VISIBLE);
        }

        int visibility = getResource().getBoolean(R.bool.contacts_toolbar_addmessage_visibility) ? View.VISIBLE : View.INVISIBLE;
        mViewContactsToolbarRightOption.setVisibility(visibility);

        mViewContactsToolbarRightOption.setPadding(20, 20, 20, 20);
        mViewContactsToolbarRightOption.setImageResource(R.drawable.ic_add);
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void addContact() {
        GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.CONTACT_SELECT_SUBJECT, new HorizontalChangeHandler(), new HorizontalChangeHandler());
    }

    @OnClick(R.id.controller_contacts_new_message_button)
    void addNewMessage() {
        addContact();
    }

    @Override
    public void onContactClicked(GetContactsResponse contact) {

        Object saleNameObject = contact.getSubject();
        Object invoiceNumber = contact.getInvoiceNumber();
        String timeStamp = contact.getLastMessageDate();

        String saleName;
        int invoiceNo;
        String timeStampString;
        String contactSubject = contact.getSubject();


        saleName = saleNameObject != null ? saleNameObject.toString() : "";
        invoiceNo = invoiceNumber != null ? (int) invoiceNumber : 0;
        timeStampString = DateUtils.getDateForContactMessages(timeStamp);

        RouterTransaction routerTransaction = RouterTransaction.with(ViewContactHistoryController.newInstance(
                contactSubject,
                saleName,
                invoiceNo,
                timeStampString,
                contact.getNumber(),
                false))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        getDisplayRouter().pushController(routerTransaction);
    }

    @Override
    public ViewContactsMvpPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public Router getDisplayRouter() {
        return getRouter();
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackPress() {
        mActivity.onBackPressed();
    }
}
