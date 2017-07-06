package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.mysale.genie.utility.Prefs;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderList;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.addcontact.selectsubject.ContactSelectSubjectController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseController implements AddContactMvpView {

    public static final String TAG = "AddContactController";

    private static final String KEY_TEXT = "AddContactController.KEY_TEXT";
    private static final String KEY_FROM_FRAGMENT_ID = "AddContactController.KEY_FROM";
    private static final String KEY_CONTACT_SUBJECT = "CONTACT_SUBJECT";
    private static final String KEY_CONTACT_NUMBER = "CONTACT_NUMBER";
    private static final String KEY_INVOICE_NUMBER = "CONTACT_INVOICE_NUMBER";


    @BindView(R.id.partial_toolbar_filter_view)
    ImageView mAddContactToolbarRightOption;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mAddContactToolbarTitle;

    @BindView(R.id.controller_add_contact_subject_text)
    TextView mAddContactSubjectText;

    @BindView(R.id.controller_add_contact_order_text)
    TextView mAddContactOrderText;

    @BindView(R.id.controller_add_contact_message_field)
    EditText mAddContactMessageField;

    @Inject
    AddContactMvpPresenter<AddContactMvpView> mPresenter;


    List<String> mContactSubjects;
    List<ContactOrderList> mContactOrders;

    boolean hasLoadedSubjects = false;
    boolean hasLoadedOrders = false;

//    static GetContactsResponse.ContactList mContactUsObject;
    private static int mContactNumber;
    private int mInvoiceNumber;
    private String mChosenOptionInvoice;

    private String mContactHistoryChosenSubject;
    private String mContactHistoryChosenOrder;
    private static String mFromFragmentId;

    public static AddContactController newInstance(
            String fromFragmentId,
            Bundle bundle) {

        bundle.putString(KEY_FROM_FRAGMENT_ID, fromFragmentId);
        return new AddContactController(bundle);
    }

    public static AddContactController newInstance() {
        Prefs.putBoolean("isSubjectsLoaded",false);

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_FROM_FRAGMENT_ID, "CONTACT_US")
                        .build());
    }


    public AddContactController(Bundle args) {
        super(args);
        if (!args.isEmpty()){
            if (args.containsKey(KEY_FROM_FRAGMENT_ID)){
                mFromFragmentId = args.getString(KEY_FROM_FRAGMENT_ID);
            }

            if (args.containsKey(KEY_CONTACT_SUBJECT)){
                mContactHistoryChosenSubject = args.getString(KEY_CONTACT_SUBJECT);
            }

            if (args.containsKey(KEY_CONTACT_NUMBER)){
                mContactNumber = args.getInt(KEY_CONTACT_NUMBER);
            }

            if (args.containsKey(KEY_INVOICE_NUMBER)){
                mInvoiceNumber = args.getInt(KEY_INVOICE_NUMBER);
            }
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_contact, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        //mPresenter.loadSample(new SampleRequest());
        mAddContactToolbarRightOption.setImageDrawable(
                getResources().getDrawable(R.drawable.ic_check));
        mAddContactToolbarTitle.setText(R.string.create_contact);

        boolean isSubjectsLoaded = Prefs.getBoolean("isSubjectsLoaded",false);

        if (!isSubjectsLoaded){

            mPresenter.loadContactUsSubjects();
            Prefs.putBoolean("isSubjectsLoaded", true);
        }
        else{
//            setupDefaultBottomButton(mBaseActivity.getString(R.string.submit), onClickListener);
        }

        final String chosenSubject = mAddContactSubjectText.getText().toString();
        final String chosenOrder = mAddContactOrderText.getText().toString();

        if (mFromFragmentId.equals("CONTACT_HISTORY")) {

            if (mInvoiceNumber==0)
                mAddContactOrderText.setText(String.valueOf("None"));
            else
                mAddContactOrderText.setText(String.valueOf(mInvoiceNumber));

            mAddContactSubjectText.setText(mContactHistoryChosenSubject);

            mAddContactSubjectText.setClickable(false);
            mAddContactOrderText.setClickable(false);

            View.OnClickListener onClickListener = view1 -> {
                String replyMessage = mAddContactMessageField.getText().toString();
                int contactId = mContactNumber;

            ReplyContactRequest replyContactRequest = new ReplyContactRequest();
            replyContactRequest.comments = replyMessage;
            replyContactRequest.contactNo = contactId;

                if (replyContactRequest.comments.isEmpty()){

    //                CustomAlertDialog.showCustomAlertDialog(
    //                        mBaseActivity,
    //                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
    //                        mBaseActivity.getString(R.string.please_input_message)
    //                );

                }else{
                    mPresenter.replyContact(replyContactRequest);
                }
            };

            mAddContactToolbarRightOption.setOnClickListener(onClickListener);
        } else if(mFromFragmentId.equals("CONTACT_US")){

            String currentSubject = ContactPreferenceHelper.getChosenSubject(getActivity());
            String currentOrder = ContactPreferenceHelper.getChosenOrder(getActivity());
            mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(getActivity());

//            myContactsSelectSubjectChosenOption.setText(mContactHistoryChosenSubject);
            mPresenter.loadContactUsOrders();

            mPresenter.loadContactUsSubjects();

//            if (!hasLoadedOrders) {
//                Log.d("clickable", "from contact load orders");
//
//                mPresenter.loadContactUsOrders();
//
//            } if (!hasLoadedSubjects){
//                Log.d("clickable", "from contact load subjects");
//
//                mPresenter.loadContactUsSubjects();
//            }

            if (!currentOrder.isEmpty()) {

                showContactFirstOrderFromPreference(currentOrder);

            }

            if (!currentSubject.isEmpty()) {

                showContactFirstSubjectFromPreference(currentSubject);

            }

            View.OnClickListener onClickListener = view12 -> {
//
                CreateContactRequest createContactRequest = new CreateContactRequest();

                if (mChosenOptionInvoice != ""){
                    createContactRequest.invoiceNo = Integer.valueOf(mChosenOptionInvoice);
                } else {
                    createContactRequest.invoiceNo = 0;
                }

                createContactRequest.subj = mAddContactSubjectText.getText().toString();

                if (mAddContactMessageField.getText().toString().isEmpty()){


//                    CustomAlertDialog.showCustomAlertDialog(
//                            mBaseActivity,
//                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                            mBaseActivity.getString(R.string.please_write_a_message));

                }  else {

                    createContactRequest.msg = mAddContactMessageField.getText().toString();

                    mPresenter.createNewContact(createContactRequest);

                }
            };
            mAddContactToolbarRightOption.setOnClickListener(onClickListener);

        }
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }


    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBack(){
        getActivity().onBackPressed();
    }

    @OnClick(R.id.partial_toolbar_filter_view)
    void onCreate(){

        getRouter().setRoot(RouterTransaction.with(AccountController.newInstance())
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler()));

    }

    @OnClick(R.id.controller_add_contact_subject_text)
    void onClickSubjectText(){

        if (hasLoadedSubjects)
            getRouter().pushController(RouterTransaction.with(ContactSelectSubjectController.newInstance(mContactSubjects))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        else {

//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity,
//                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    mBaseActivity.getString(R.string.loading_subjects));
        }
    }

    @OnClick(R.id.controller_add_contact_order_text)
    void onClickOrderText(){

        if(hasLoadedOrders){

            getRouter().pushController(RouterTransaction.with(ContactSelectOrderController.newInstance(mContactOrders))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));

        } else if (hasLoadedOrders && mContactOrders != null && mContactOrders.size() == 0){

//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity,
//                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
//                    mBaseActivity.getString(R.string.you_have_no_order));

        } else {

        }

    }

    @Override
    public void showContactFirstSubject(List<String> contactSubjectList) {

        mContactSubjects = contactSubjectList;
        hasLoadedSubjects = true;
    }

    @Override
    public void showContactFirstOrder(List<ContactOrderList> contactOrderList) {

        mContactOrders = contactOrderList;
        hasLoadedOrders = true;
    }

    @Override
    public void contactCreatedSwitchView(CreateContactResponse createContactResponse) {
        if (createContactResponse.getCreateContact().getResult()){

//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.message_submitted));

            getActivity().onBackPressed();
        }else{
//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.error_creating_message));
        }
    }

    @Override
    public void repliedContactSwitchView(ReplyContact replyContact) {

        if (replyContact.getResult()){
//
//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.message_submitted));
            getActivity().onBackPressed();
        }else{
//            CustomAlertDialog.showCustomAlertDialog(
//                    mBaseActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
//                    mBaseActivity.getString(R.string.error_creating_message));
        }
    }

    public void showContactFirstSubjectFromPreference(String subject){

        mAddContactSubjectText.setText(subject);
    }

    public void showContactFirstOrderFromPreference(String order){

        mAddContactOrderText.setText(order);
    }
}
