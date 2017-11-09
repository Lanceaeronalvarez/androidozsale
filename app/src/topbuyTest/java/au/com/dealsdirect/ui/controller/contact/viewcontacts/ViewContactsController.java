package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactitem.ContactItemByDate;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.ui.base.BaseToolBarController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsClickListener;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.viewcontactdate.ViewContactDateAdapter;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.utils.DateUtils.getTrimmedServerDateString;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsController extends BaseToolBarController implements ViewContactsMvpView, ContactsClickListener {

    public static final String TAG = "ContactController";
    private static final String KEY_TEXT = "ContactController.KEY_TEXT";

    private ContactsAdapter mContactAdapter;
    private ContactsClickListener mContactClickListener;
    private ViewContactDateAdapter mContactDateAdapter;

    @BindView(R.id.contacts_recycler_view)
    RecyclerView mViewContactsRecyclerView;

    @BindView(R.id.no_contacts_placeholder)
    LinearLayout mPlaceholderLayout;

    @Inject
    ViewContactsMvpPresenter<ViewContactsMvpView> mPresenter;


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
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_view_contacts, container, false));

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mViewContactsRecyclerView.setVisibility(View.GONE);
        mPresenter.loadContacts();
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mToolbarTitle.setText("contact us");

        assert (mActivity) != null;
        mActivity.getMainController().showBottomNav();

        mContactClickListener = this;
        setUp(view);
        mPresenter.loadContacts();

    }

    @Override
    protected void setUp(View view) {

        assert (mActivity) != null;
        mActivity.setDraggableViewPager(false);
        mContactAdapter = new ContactsAdapter(new ArrayList<>(), mActivity, mContactClickListener);

        mContactDateAdapter = new ViewContactDateAdapter(new ArrayList<>(), mActivity, mContactClickListener);

        mViewContactsRecyclerView.setAdapter(mContactDateAdapter);
        mViewContactsRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        hideKeyboard();
    }

    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showContactItems(GetContactsResponse.Response myContacts) {

//        List<GetContactsResponse.ContactList> items = myContacts.getList();
//        if (items != null && items.size() != 0) {
//            mContactAdapter.replace(myContacts.getList());
//            mViewContactsRecyclerView.setVisibility(View.VISIBLE);
//            mPlaceholderLayout.setVisibility(View.GONE);
//        } else {
//            mPlaceholderLayout.setVisibility(View.VISIBLE);
//            mViewContactsRecyclerView.setVisibility(View.GONE);
//        }

        List<GetContactsResponse.ContactList> items = myContacts.getList();
        if (items != null && items.size() != 0) {
            mContactDateAdapter.replace(getDifferentDates(items));
            mViewContactsRecyclerView.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
        } else {
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mViewContactsRecyclerView.setVisibility(View.GONE);
        }

    }

//    @OnClick(R.id.partial_toolbar_filter_view)
//    void addContact() {
//        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance())
//                .pushChangeHandler(new VerticalChangeHandler())
//                .popChangeHandler(new VerticalChangeHandler()));
//
//    }

    public ArrayList<ContactItemByDate> getDifferentDates(List<GetContactsResponse.ContactList> lists) {

        List<String> dateSet = new LinkedList<>();
        String dateHeaderFormat;

        for (int i = 0; i < lists.size(); i++) {
            dateHeaderFormat = getTrimmedServerDateString(lists.get(i).getLastAnswer());

            if (!dateSet.contains(dateHeaderFormat))
                dateSet.add(dateHeaderFormat);
        }

        ArrayList<ContactItemByDate> tempList = new ArrayList<>();
        for (int x = 0; x < dateSet.size(); x++) {

            ContactItemByDate contactItemByDate = new ContactItemByDate();
            List<GetContactsResponse.ContactList> tempLists
                    = new LinkedList<>();

            for (int y = 0; y < lists.size(); y++) {

                String listDateHeaderFormat
                        = getTrimmedServerDateString(lists.get(y).getLastAnswer());

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
        Object timeStamp = contactList.getLastAnswer();

        String saleName;
        int invoiceNo;
        String timeStampString;
        String contactSubject = contactList.getSubject();

        if (saleNameObject != null) {

            saleName = saleNameObject.toString();
        } else {

            saleName = "No Order Number";
        }

        if (invoiceNumber != null) {

            invoiceNo = (int) invoiceNumber;
        } else {

            invoiceNo = 0;
        }

        if (timeStamp != null) {

            timeStampString = DateUtils.getDateForContactMessages(timeStamp.toString());

        } else {

            timeStampString = "";
        }

        Router router = getRouter();
        router.pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                contactSubject,
                saleName,
                invoiceNo,
                timeStampString,
                contactList.getContactNo()))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @OnClick(R.id.controller_view_contacts_write_message)
    void onWriteMessageClick(){
        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance())
                .tag(CurrentReturnsController.TAG)
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }
}
