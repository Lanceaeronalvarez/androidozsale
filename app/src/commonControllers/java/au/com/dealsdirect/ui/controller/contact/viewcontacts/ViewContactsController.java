package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import au.com.dealsdirect.ui.base.BasePullToRefreshController;
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsClickListener;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsController extends BasePullToRefreshController implements ViewContactsMvpView, ContactsClickListener {

    public static final String TAG = "ContactController";
    private static final String KEY_TEXT = "ContactController.KEY_TEXT";

    private ContactsAdapter mContactAdapter;
    private ContactsClickListener mContactClickListener;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mViewContactsToolarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mViewContactsToolbarRightOption;

    @BindView(R.id.partial_toolbar_left_view)
    ImageView mViewContactsToolbarLeftOption;

    @BindView(R.id.contacts_recycler_view)
    RecyclerView mViewContactsRecyclerView;

    @BindView(R.id.no_contacts_placeholder)
    RelativeLayout mPlaceholderLayout;

    @Inject
    ViewContactsMvpPresenter<ViewContactsMvpView> mPresenter;

    static String mFromFragmentId;

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

        fillToolbar(inflater.inflate(R.layout.partial_toolbar_arrow, container, false));
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

        mViewContactsToolarTitle.setText("contact Us");
        mViewContactsToolbarLeftOption.setVisibility(View.INVISIBLE);
        mViewContactsToolbarRightOption.setVisibility(View.INVISIBLE);

        mContactAdapter = new ContactsAdapter(new ArrayList<>(), mActivity, mContactClickListener);

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
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showContactItems(GetContactsResponse.Response myContacts) {

        List<GetContactsResponse.ContactList> items = myContacts.getList();
        if (items != null && items.size() != 0) {
            mContactAdapter.replace(myContacts.getList());
            mViewContactsRecyclerView.setVisibility(View.VISIBLE);
            mPlaceholderLayout.setVisibility(View.GONE);
        } else {
            mPlaceholderLayout.setVisibility(View.VISIBLE);
            mViewContactsRecyclerView.setVisibility(View.GONE);
        }

        mViewContactsToolbarRightOption.setVisibility(View.VISIBLE);
        if(mPresenter.isTablet()){
            mViewContactsToolbarRightOption.setPadding(5, 5, 5, 5);
        } else {
            mViewContactsToolbarRightOption.setPadding(20, 20, 20, 20);
        }
        mViewContactsToolbarRightOption.setImageResource(R.drawable.ic_add);
    }

    @OnClick(R.id.partial_toolbar_right_view)
    void addContact() {
        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance())
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));

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
            List<GetContactsResponse.ContactList> tempLists
                    = new LinkedList<>();

            for (int y = 0; y < lists.size(); y++) {

                String listDateHeaderFormat
                        = DateUtils.getTrimmedServerDateString(lists.get(y).getLastAnswer());

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

}
