package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.ContactItemByDate;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.contact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.listener.ContactClickListener;
import au.com.dealsdirect.ui.controller.contact.selectsubject.ContactSelectSubjectController;
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

    @Nullable
    @BindView(R.id.contact_detail_container)
    ViewGroup mContactDetailContainer;

    @Inject
    ViewContactsMvpPresenter<ViewContactsMvpView> mPresenter;

    public ViewContactsMvpView viewContactsMvpView;
    private boolean mHasSavedState;

    private Router mContactDetailRouter;

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

        mActivity.getMainController().setViewpagerDraggable(false);

        setUp(view);
        mPresenter.loadContacts();

    }

    @Override
    protected void setUp(View view) {

        assert (mActivity) != null;

        mActivity.setContactsController(this);

        if (mPresenter.isTablet()) {
            mContactDetailRouter = getChildRouter(mContactDetailContainer);
        }

        mViewContactsAddNewMessage.setVisibility(mPresenter.isTablet() ? View.GONE : View.VISIBLE);

        mViewContactsToolarTitle.setText(getResource().getString(R.string.account_contact_us));
        mViewContactsToolbarLeftOption.setVisibility(View.INVISIBLE);
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
    public boolean handleBack() {
        if(mContactDetailRouter != null && mContactDetailRouter.getBackstackSize() <= 1){
            mActivity.getHomeController().goBackToHomePage();
            return true;
        }

        return super.handleBack();
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
    public void showContactItems(GetContactsResponse.Response myContacts) {

        mActivity.setDraggableViewPager(false);

        List<GetContactsResponse.ContactList> items = myContacts.getList();
        if (items != null && items.size() != 0) {
            mContactAdapter.replace(myContacts.getList());
            mViewContactsRecyclerViewContainer.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
            mViewContactsAddNewMessage.setVisibility(mPresenter.isTablet() ? View.GONE : View.VISIBLE);
        } else {
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mViewContactsRecyclerViewContainer.setVisibility(View.GONE);
            mViewContactsAddNewMessage.setVisibility(View.VISIBLE);
        }

        int visibility = getResource().getBoolean(R.bool.contacts_toolbar_addmessage_visibility) ? View.VISIBLE : View.INVISIBLE;
        mViewContactsToolbarRightOption.setVisibility(visibility);

        if (mPresenter.isTablet()) {
            mViewContactsToolbarRightOption.setPadding(5, 5, 5, 5);
        } else {
            mViewContactsToolbarRightOption.setPadding(20, 20, 20, 20);
        }
        mViewContactsToolbarRightOption.setImageResource(R.drawable.ic_add);
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void addContact() {
        ContactPreferenceHelper.clear(mActivity);
        RouterTransaction routerTransaction = RouterTransaction.with(ContactSelectSubjectController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler());
        if (mPresenter.isTablet()) {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.CONTACT_SELECT_SUBJECT, routerTransaction);
        } else {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.CONTACT_SELECT_SUBJECT, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        }
    }

    @OnClick(R.id.controller_contacts_new_message_button)
    void addNewMessage() {
        addContact();
    }

    public ArrayList<ContactItemByDate> getDifferentDates(List<GetContactsResponse.ContactList> lists) {

        List<String> dateSet = new LinkedList<>();
        String dateHeaderFormat;

        for (int i = 0; i < lists.size(); i++) {
            dateHeaderFormat = DateUtils.getTrimmedServerDateString(lists.get(i).getLastAnswer());

            if (!dateSet.contains(dateHeaderFormat))
                dateSet.add(dateHeaderFormat);
        }

        ArrayList<ContactItemByDate> tempList = new ArrayList<>();
        for (int x = 0; x < dateSet.size(); x++) {

            ContactItemByDate contactItemByDate = new ContactItemByDate();
            List<GetContactsResponse.ContactList> tempLists = new LinkedList<>();

            for (int y = 0; y < lists.size(); y++) {
                String listDateHeaderFormat = DateUtils.getTrimmedServerDateString(lists.get(y).getLastAnswer());

                if (dateSet.get(x).equals(listDateHeaderFormat)) {
                    tempLists.add(lists.get(y));
                }
            }
            contactItemByDate.setContactItemList(tempLists);
            contactItemByDate.setDateHeaderFormat(dateSet.get(x));
            tempList.add(contactItemByDate);
            //   contactItemByDateList.add(tempList);
        }

        return tempList;
    }

    @Override
    public void onContactClicked(GetContactsResponse.ContactList contactList) {

        Object saleNameObject = contactList.getSaleName();
        Object invoiceNumber = contactList.getInvoiceNo();
        String timeStamp = contactList.getLastAnswer();

        String saleName;
        int invoiceNo;
        String timeStampString;
        String contactSubject = contactList.getSubject();


        saleName = saleNameObject != null ? saleNameObject.toString() : "";
        invoiceNo = invoiceNumber != null ? (int) invoiceNumber : 0;
        timeStampString = DateUtils.getDateForContactMessages(timeStamp);

        RouterTransaction routerTransaction = RouterTransaction.with(ViewContactHistoryController.newInstance(
                contactSubject,
                saleName,
                invoiceNo,
                timeStampString,
                contactList.getContactNo()))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        if (mPresenter.isTablet()) {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.CONTACT_HISTORY, routerTransaction);
        } else {
            getDisplayRouter().pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                    contactSubject,
                    saleName,
                    invoiceNo,
                    timeStampString,
                    contactList.getContactNo()))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public ViewContactsMvpPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public Router getDisplayRouter() {
        return mPresenter.isTablet() ? mContactDetailRouter : getRouter();
    }

    @Override
    public void getViewContactsView() {
        mActivity.getMainController().getHomeController().setContactsController(this);
    }

    @Override
    public void sendOrderMessage(boolean isCalledFromOrders, int invoiceNumber, String description) {
        RouterTransaction routerTransaction = RouterTransaction.with(AddContactController.newInstance(invoiceNumber,isCalledFromOrders,description))
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler());
        if (mPresenter.isTablet()) {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.ADD_CONTACT, routerTransaction);
        } else {
            getDisplayRouter().pushController(routerTransaction);
        }
    }

    public void resetContactDetailRouter() {
        if (mContactDetailContainer != null && mContactDetailRouter != null) {
            mContactDetailRouter.popToRoot();
            mContactDetailRouter.popCurrentController();
            mContactDetailContainer.removeAllViews();
        }
    }
}
