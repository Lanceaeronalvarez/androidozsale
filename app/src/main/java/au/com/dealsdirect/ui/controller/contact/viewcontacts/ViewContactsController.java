package au.com.dealsdirect.ui.controller.contact.viewcontacts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import au.com.dealsdirect.ui.controller.contact.addcontact.AddContactController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.contacts.ContactsClickListener;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.viewcontactdate.ViewContactDateAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class ViewContactsController extends BaseController implements ViewContactsMvpView, ContactsClickListener {

    public static final String TAG = "ContactController";
    private static final String KEY_TEXT = "ContactController.KEY_TEXT";

    private ViewContactDateAdapter mContactDateAdapter;
    private ContactsClickListener mContactClickListener;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mViewContactsToolarTitle;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mViewContactsToolbarRightOption;

    @BindView(R.id.partial_toolbar_arrow_view)
    ImageView mViewContactsToolbarLeftOption;

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
        View view = inflater.inflate(R.layout.controller_view_contacts, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mContactClickListener = this;
        setUp(view);
        mPresenter.loadContacts();

    }

    @Override
    protected void setUp(View view) {
        mViewContactsToolarTitle.setText("Contact Us");
        mViewContactsToolbarRightOption.setVisibility(View.INVISIBLE);
        mViewContactsToolbarLeftOption.setVisibility(View.INVISIBLE);

        mContactDateAdapter =
                new ViewContactDateAdapter(new ArrayList<>(),getActivity(), mContactClickListener);

        mViewContactsRecyclerView.setAdapter(mContactDateAdapter);
        mViewContactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

//    @OnClick(R.id.partial_toolbar_arrow_view)
//    public void onBackClick() {
//        getActivity().onBackPressed();
//    }


    @Override
    public void showContactItems(GetContactsResponse.Response myContacts) {

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

    @OnClick(R.id.controller_view_contacts_add_button)
    void addContact(){
        getRouter().pushController(RouterTransaction.with(AddContactController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }


    public ArrayList<ContactItemByDate>
    getDifferentDates(List<GetContactsResponse.ContactList> lists) {

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

        if(saleNameObject != null){

            saleName = saleNameObject.toString();
        }else{

            saleName = "No Order Number";
        }

        if (invoiceNumber != null){

            invoiceNo = (int) invoiceNumber;
        }else{

            invoiceNo = 0;
        }

        if (timeStamp != null){

            timeStampString = DateUtils.getTimeFromDateString(timeStamp.toString());

        }else{

            timeStampString = "";
        }

        getRouter().pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                saleName,
                invoiceNo,
                timeStampString,
                contactList.getContactNo()))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}
