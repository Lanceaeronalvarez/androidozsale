package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactsubjecttemplates.ContactSubjectTemplatesResponse;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.setattachmentforcontact.SetAttachmentForContactRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.enums.FeatureUsageEventType;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.contact.listener.ContactSuggestionsClickListener;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactsAddImageAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsController;
import au.com.dealsdirect.ui.controller.orders.tracking.OrderTrackingClickListener;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnController;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AsyncResponse;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.StringUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseController
        implements AddContactMvpView,
        ReturnDetailsListener,
        ContactSelectOrderController.OnInvoiceNumberSubmittedListener,
        AsyncResponse {

    public static final String TAG = "AddContactController";
    public static final String ORDER_NUMBER = "ORDER_NUMBER";
    public static final String INVOICE_NUMBER = "INVOICE_NUMBER";
    public static final String PRESET_MESSAGE = "PRESET_MESSAGE";

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mAddContactToolbarRightOption;

    @BindView(R.id.partial_toolbar_title)
    TextView mAddContactToolbarTitle;

    @BindView(R.id.controller_add_contact_subject_title)
    TextView mAddContactSubjectTitle;

    @BindView(R.id.controller_add_contact_order_title)
    TextView mAddContactOrderTitle;

    @BindView(R.id.controller_add_contact_subject_text)
    TextView mAddContactSubjectText;

    @BindView(R.id.controller_add_contact_order_container)
    ViewGroup mAddContactOrderContainer;

    @BindView(R.id.controller_add_contact_order_text)
    TextView mAddContactOrderText;

    @BindView(R.id.controller_view_contacts_history_message_field)
    EditText mAddContactMessageField;

    @BindView(R.id.controller_view_contacts_history_reply_button)
    ImageButton mAddContactMessageSend;

    @BindView(R.id.contact_suggestions_container)
    RecyclerView mContactSuggestions;

    @BindView(R.id.view_contact_history_image_recyclerview)
    RecyclerView mImageRecyclerView;

    @Inject
    AddContactMvpPresenter<AddContactMvpView> mPresenter;

    private String mSubjectId = "";
    private String mSubject = "";
    private int mOrderNumber;
    private int mInvoiceNumber;
    private List<String> mActions = new ArrayList<>();
    private String mAttachmentId = "";
    private int mContactNumber = 0;
    private boolean mIsInvoiceRequired = true;
    private String mPresetMessage = "";

    private ViewContactsMvpView mViewContactsMvpView;
    private boolean mHasSavedInstance = false;
    private ViewContactsAddImageAdapter mImageAdapter;
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private String mMessageId = "";
    private int mNumber = -1;
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private ArrayList<SetAttachmentForContactRequest.Item> itemsList = new ArrayList<>();
    ImageUploadUtil.UploadFileToServer uploadFileToServer;

    private ContactSuggestionsAdapter contactSuggestionsAdapter = null;

    private final OrderTrackingClickListener orderTrackingClickListener = new OrderTrackingClickListener() {
        @Override
        public void onOrderItemTrackingButtonClick(String url, String errorMessage) {
            ActivityLaunchUtil.launchActivity(mActivity, url, errorMessage);
        }

        @Override
        public void onNodeTapped(GetOrdersResponse.Order.Invoice.Delivery.Step upperStep, GetOrdersResponse.Order.Invoice.Delivery.Step lowerStep) {
            mActivity.showOrderTrackingStepBottomDialog(upperStep, lowerStep);
        }

        @Override
        public void onOrderReceivedToggle(String invoiceId, int invoiceNumber, boolean isReceived) {
            // do nothing
        }
    };

    private final ContactSuggestionsClickListener contactSuggestionsClickListener = new ContactSuggestionsClickListener() {
        @Override
        public void onClickAction(ContactSubjectTemplatesResponse.Suggestion.Mobile.Link link) {
            logContactSuggestionsFeatureUsageEvent(false);
            switch (link.getRouteKey()) {
                case "order_details":
                    gotoOrderDetails(OrderDetailsController.newInstance(mInvoiceNumber));
                    break;
                case "delivery_address":
                    ViewAddressController viewAddressController = ViewAddressController.newInstance();
                    viewAddressController.setOnAddressSelected(addressesItem -> {
                        getRouter().popCurrentController();
                        ChangeDeliveryAddressRequest request = new ChangeDeliveryAddressRequest();
                        request.setAddressId(addressesItem.getAddressId());
                        request.setInvoiceId(null);
                        request.setInvoiceNumber(mInvoiceNumber);
                        mPresenter.changeDeliveryAddress(request, mInvoiceNumber);
                        viewAddressController.setOnAddressSelected(null);
                    });
                    getRouter().pushController(RouterTransaction.with(viewAddressController)
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                    break;
                case "request_return":
                    getRouter().popToRoot();
                    mActivity.getAccountController().showMyReturns();
                    mActivity.getAccountController().getDisplayRouter()
                            .pushController(RouterTransaction.with(NewReturnController.newInstance(mInvoiceNumber, false, null))
                                    .pushChangeHandler(new HorizontalChangeHandler())
                                    .popChangeHandler(new HorizontalChangeHandler()));
                    break;
                case "ourpay":
                    getRouter().popToRoot();
                    mActivity.getAccountController().showMyAccountsOurpay();
                    break;
                case "select":
                    // there is no select menu
                    break;
                case "my_payments":
                    getRouter().popToRoot();
                    mActivity.getAccountController().showMyPaymentsController();
                    break;
                case "search":
                    mActivity.getMainController().showShopController();
                    break;
                default:
                    // unhandled routeKey
                    break;
            }
        }

        @Override
        public void onClickInfo(ContactSubjectTemplatesResponse.Suggestion.Mobile.Link link) {
            logContactSuggestionsFeatureUsageEvent(true);
            switch (link.getRouteKey()) {
                case "help_section":
                    String url = Settings.getSelectedCountry().myAccount + link.getUrl();
                    url = url.replaceAll("(?<=[^:\\s])(\\/+\\/)", "/");
                    Intent openUrl = new Intent(Intent.ACTION_VIEW);
                    openUrl.setData(Uri.parse(url));
                    startActivity(openUrl);
                    break;
                case "ourpay_website":
                case "select_website":
                    openInfoPage(link.getTitle(), link.getUrl());
                    break;
                default:
                    // unsupported routeKey
                    break;
            }
        }

        private void openInfoPage(String title, String url) {
            Bundle bundle = new BundleBuilder(new Bundle())
                    .putString(BundleKeys.KEY_WEBVIEW_CONTROLLER_URL, url)
                    .putString(BundleKeys.KEY_WEBVIEW_CONTROLLER_TITLE, title)
                    .build();

            RouterTransaction transaction = RouterTransaction
                    .with(ControllerFactory
                            .getInstance(GateKeeper.Destination.COMMON_WEBVIEW, bundle))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler());
            getRouter().pushController(transaction);
        }
    };

    public static AddContactController newInstance() {
        return new AddContactController(
                new BundleBuilder(new Bundle()).build());
    }

    public static AddContactController newInstance(
            int invoiceNo,
            String presetMessage) {

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putInt(INVOICE_NUMBER, invoiceNo)
                        .putString(PRESET_MESSAGE, presetMessage)
                        .build());
    }

    public static AddContactController newInstance(
            int orderNumber,
            int invoiceNo,
            String presetMessage) {

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putInt(ORDER_NUMBER, orderNumber)
                        .putInt(INVOICE_NUMBER, invoiceNo)
                        .putString(PRESET_MESSAGE, presetMessage)
                        .build());
    }

    public AddContactController(Bundle args) {
        super(args);
        mOrderNumber = args.getInt(ORDER_NUMBER);
        mInvoiceNumber = args.getInt(INVOICE_NUMBER);
        mPresetMessage = args.getString(PRESET_MESSAGE, "");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.CONTACT_SUBJECT, mSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSubject = savedInstanceState.getString(BundleKeys.CONTACT_SUBJECT);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_add_contact, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        if (mHasSavedInstance) {
            mViewContactsMvpView = mActivity.getContactsController();
        } else {
            mViewContactsMvpView = ((ViewContactsMvpView) mActivity.getCurrentRouter().getControllerWithTag(ViewContactsMvpView.TAG));
        }
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);

        mAddContactToolbarRightOption.setVisibility(View.INVISIBLE);
        mAddContactToolbarTitle.setText(R.string.new_message);

        setupFields();

        if (mSubjectId != null && !mSubjectId.isEmpty()) {
            mPresenter.getContactSubjectsTemplates(mSubjectId);
        }

        if (!mPresetMessage.isEmpty()) {
            mAddContactMessageField.setText(mPresetMessage);
            mAddContactMessageField.setCompoundDrawables(null, null, null, null);
        }


        mAddContactMessageSend.setOnClickListener(v -> sendMessage());

        mImageAdapter = new ViewContactsAddImageAdapter(mActivity, this,
                mImageUriArray);
        mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupFields() {
        String addContactOrderText;
        if (mInvoiceNumber != 0) {
            addContactOrderText = getString(R.string.invoice_text) + " " + mInvoiceNumber;
        } else {
            addContactOrderText = getString(R.string.select_a_sale);
        }

        mAddContactOrderText.setText(addContactOrderText);

        mAddContactOrderContainer.setVisibility(mIsInvoiceRequired ? View.VISIBLE : View.GONE);

        if (!mSubject.isEmpty()) {
            mAddContactSubjectText.setText(mSubject);
        }
    }

    private static class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }

    private void validateSubjectField() throws ValidationException {
        if (mSubject == null || mSubject.isEmpty()) {
            throw new ValidationException(mActivity.getString(R.string.create_contact_empty_subject_error));
        }
    }

    private void validateInvoiceField() throws ValidationException {
        if (mIsInvoiceRequired && mInvoiceNumber <= 0) {
            throw new ValidationException(mActivity.getString(R.string.create_contact_empty_invoice_error));
        }
    }

    private void validateMessageField() throws ValidationException {
        if (mAddContactMessageField.getText().toString().isEmpty()) {
            throw new ValidationException(mActivity.getString(R.string.create_contact_empty_message_error));
        }
    }

    private void sendMessage() {
        hideKeyboard();

        try {
            validateSubjectField();
            validateInvoiceField();
            validateMessageField();

            CreateContactRequest createContactRequest = new CreateContactRequest();

            if (mIsInvoiceRequired) {
                createContactRequest.setInvoiceNumber(mInvoiceNumber);
            } else {
                createContactRequest.setInvoiceNumber(0);
            }

            createContactRequest.setSubject(mSubject);

            createContactRequest.setText(mAddContactMessageField.getText().toString());
            mPresenter.createNewContact(createContactRequest);
        } catch (ValidationException e) {
            onError(e.getMessage());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDestroyView(View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
    }

    @OnClick({R.id.partial_toolbar_left_view})
    void onBack() {
        mActivity.onBackPressed();
    }


    @OnClick(R.id.controller_add_contact_order_container)
    void onClickOrderContainer() {
        if (!mPresetMessage.isEmpty()) {
            return;
        }

        mAddContactMessageField.clearFocus();
        hideKeyboard();

        ContactSelectOrderController controller = ContactSelectOrderController.newInstance();
        controller.setActions(mActions);
        controller.setListener(this);
        getRouter().pushController(RouterTransaction.with(controller)
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void contactCreatedSwitchView(String createContactResponse) {
        if (createContactResponse != null &&
                !createContactResponse.isEmpty() &&
                StringUtils.isNumeric(createContactResponse)) {
            mContactNumber = Integer.parseInt(createContactResponse);

            if (mImageUriArray.size() != 0) {
                GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
                getContactHistoryRequest.setNumber(mContactNumber);
                mPresenter.loadContactHistory(getContactHistoryRequest);
            } else {
                showViewContactHistory();
            }
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.error_creating_message));
        }
    }

    @Override
    public void showViewContactHistory() {
        mImageFileHashMap.clear();
        itemsList.clear();
        mImageUriArray.clear();
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.notifyDataSetChanged();
        mImageRecyclerView.setVisibility(View.GONE);

        ViewContactHistoryController controller = ViewContactHistoryController.newInstance(
                mSubject,
                mInvoiceNumber,
                mAddContactMessageField.getText().toString(),
                mContactNumber,
                false);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        getRouter().popToRoot();
        getRouter().pushController(routerTransaction);
    }

    @Override
    public void showContactSuccess(GetContactHistoryResponse response) {

        mMessageId = response.getMessages().get(0).getId();
        mNumber = response.getNumber();

        for (int i = 0; i < mImageUriArray.size(); i++) {
            try {
                Uri uri = Uri.parse(mImageUriArray.get(i).getLink());
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getActivity().getContentResolver(), uri));
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                }
                addItemFromLink("", i, bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SetAttachmentForContactRequest setAttachmentRequest = new SetAttachmentForContactRequest();
        setAttachmentRequest.setMessageId(mMessageId);
        setAttachmentRequest.setNumber(mNumber);
        setAttachmentRequest.setItems(new ArrayList<>());
        mPresenter.setAttachment(setAttachmentRequest, false);
    }

    private void logContactSuggestionsFeatureUsageEvent(boolean isHelp) {
        switch (mSubjectId) {
            case "412d3876570c4403a85064230857b0af":
                logContactSuggestionsFeatureUsageEvent(FeatureUsageEventType.Suggestions.WHERE_ORDER);
                break;
            case "cc812c0879594476a0645656277a03dd":
                logContactSuggestionsFeatureUsageEvent(isHelp ?
                        FeatureUsageEventType.Suggestions.AMEND_HELP :
                        FeatureUsageEventType.Suggestions.AMEND_ORDER
                );
                break;
            case "55f0c53ad6f84ce38f0df675bc4c4ad2":
                logContactSuggestionsFeatureUsageEvent(FeatureUsageEventType.Suggestions.CHANGE_ADDRESS);
                break;
            case "3b116e8ca1724c0c9b4b9168b8684aec":
                logContactSuggestionsFeatureUsageEvent(isHelp ?
                        FeatureUsageEventType.Suggestions.RETURNS_HELP :
                        FeatureUsageEventType.Suggestions.RETURN_INCORRECT
                );
                break;
            case "d4dc457fd28b4284b2084e29c2d540e4":
                logContactSuggestionsFeatureUsageEvent(isHelp ?
                        FeatureUsageEventType.Suggestions.RETURNS_HELP :
                        FeatureUsageEventType.Suggestions.RETURNS_INQUIRY
                );
                break;
            case "2a874ce9719a4ceb89f5fd75c898350f":
                logContactSuggestionsFeatureUsageEvent(isHelp ?
                        FeatureUsageEventType.Suggestions.OURPAY_SITE :
                        FeatureUsageEventType.Suggestions.OURPAY
                );
                break;
            case "829cc717fad548e5ab63a536e072dc5d":
                logContactSuggestionsFeatureUsageEvent(isHelp ?
                        FeatureUsageEventType.Suggestions.SELECT_SITE :
                        FeatureUsageEventType.Suggestions.SELECT
                );
                break;
            case "9dc2ea035fb94df182286fb9f30767c1":
                logContactSuggestionsFeatureUsageEvent(isHelp ?
                        FeatureUsageEventType.Suggestions.PAYMENTS_HELP :
                        FeatureUsageEventType.Suggestions.PAYMENTS
                );
                break;
            case "1b48c5c3c2134544bf053ab3a0a6e410":
                logContactSuggestionsFeatureUsageEvent(FeatureUsageEventType.Suggestions.INVITE_FRIEND);
                break;
            case "b9a90268fc4c414da8f19e568ca8eb91":
                logContactSuggestionsFeatureUsageEvent(FeatureUsageEventType.Suggestions.TECH_HELP);
                break;
            case "fb3f64a7d9704ba785679a9b0bae9c7b":
                // Comments and suggestions
                break;
            case "96fa3e0cb7b542c599257829167b66e3":
                // COVID 19
                break;
            case "d2e48710da034bada719dba3123be4c5":
                logContactSuggestionsFeatureUsageEvent(FeatureUsageEventType.Suggestions.OTHERS_HELP);
                break;
            default:
                // unhandled
                break;
        }
    }

    private void logContactSuggestionsFeatureUsageEvent(int event) {
        FeatureUsageEventRequest featureUsageEventRequest = new FeatureUsageEventRequest();
        featureUsageEventRequest.setEventType(EventTypeId.EVENT_FEATURE_USAGE);
        featureUsageEventRequest.setFeatureInfo(new FeatureUsageEventRequest.FeatureInfo(event));

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(DataCollector.EventParameters.SCREEN_NAME, "Add New Contact");
        parameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        parameters.put(DataCollector.EventParameters.FEATURE_EVENT_REQUEST, featureUsageEventRequest);

        DataCollector.logEvent(Events.FeatureUsageEvent, parameters);
    }

    @Override
    public void showContactSuggestions(ContactSubjectTemplatesResponse contactSubjectTemplatesResponse) {
        contactSuggestionsAdapter = new ContactSuggestionsAdapter(
                contactSuggestionsClickListener,
                orderTrackingClickListener);
        mContactSuggestions.setAdapter(contactSuggestionsAdapter);
        mContactSuggestions.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

        if (contactSubjectTemplatesResponse == null) {
            contactSuggestionsAdapter.setTemplates(null);
            contactSuggestionsAdapter.notifyDataSetChanged();
            return;
        }

        for (ContactSubjectTemplatesResponse.Suggestion suggestion : contactSubjectTemplatesResponse.getSuggestions()) {
            if (suggestion.getMobile() != null) {
                switch (suggestion.getMobile().getType().toLowerCase()) {
                    case "action":
                        contactSuggestionsAdapter.setShouldHideActions(
                                (!(mOrderNumber > 0 && mInvoiceNumber > 0) && mIsInvoiceRequired) ||
                                        suggestion.getMobile().getLink().getRouteKey().equalsIgnoreCase("select")
                        );
                        break;
                    case "component":
                        switch (suggestion.getMobile().getSubtype().toLowerCase()) {
                            case "ordertracking":
                                if (mOrderNumber > 0 && mInvoiceNumber > 0) {
                                    mPresenter.getOrderTrackingDetails(mOrderNumber, mInvoiceNumber);
                                } else if (contactSuggestionsAdapter != null) {
                                    contactSuggestionsAdapter.setOrderTracker(null);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        contactSuggestionsAdapter.setTemplates(contactSubjectTemplatesResponse.getSuggestions());
        contactSuggestionsAdapter.notifyDataSetChanged();
        mContactSuggestions.setVisibility(View.VISIBLE);
    }

    @Override
    public void showOrderTracker(GetOrdersResponse.Order.Invoice.Delivery delivery) {
        if (contactSuggestionsAdapter == null) {
            return;
        }
        if (mOrderNumber <= 0 || mInvoiceNumber <= 0) {
            contactSuggestionsAdapter.setOrderTracker(null);
        } else {
            contactSuggestionsAdapter.setOrderTracker(delivery);
        }
        contactSuggestionsAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.controller_add_contact_subject_container)
    void addContact() {
        GateKeeper.push(getRouter(), GateKeeper.Destination.CONTACT_SELECT_SUBJECT, new HorizontalChangeHandler(), new HorizontalChangeHandler());
    }

    @OnClick(R.id.controller_view_contacts_history_attach_photo)
    void onAttachImage() {

        if (mImageFileHashMap.size() < AppConstants.MAX_IMAGE_COUNT && mImageUriArray.size() < AppConstants.MAX_IMAGE_COUNT) {
            getImageFromDirectory(true);
        }
    }

    @Override
    public void getImageFromDirectory(boolean uploadImage) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{
                            Manifest.permission.READ_MEDIA_IMAGES},
                    1);
        } else if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppConstants.REQUEST_CODE_PERMISSION);
        }else {
            Intent cameraIntent = new Intent(Intent.ACTION_PICK);
            cameraIntent.setType("image/*");
            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(cameraIntent, AppConstants.REQUEST_CODE_FOR_SUCCESS);
            }
        }
    }

    @Override
    public void setAttachmentId(String attachmentId) {
        mAttachmentId = attachmentId;

        if (mImageFileHashMap != null) {
            callUploadImage(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == AppConstants.REQUEST_CODE_FOR_SUCCESS) {
            Uri chosenImageUri = data.getData();

            ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
            imageLinks.setIsURL(false);
            imageLinks.setLink(String.valueOf(chosenImageUri));

            mImageUriArray.add(0, imageLinks);

            ((ViewContactsAddImageAdapter) Objects.requireNonNull(mImageRecyclerView.getAdapter())).addItem();

            mImageRecyclerView.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void removeImage(Bitmap image, int position, boolean uploadImage, boolean isAddImageAdapter) {
        ViewContactsAddImageAdapter adapter = ((ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter());
        mImageUriArray.remove(position);
        adapter.removeItem(position);

        if (mImageUriArray.size() == 0) {
            mImageRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void addItemFromLink(String url, int position, Bitmap bitmap) {
        Bitmap newBitmap = ImageUploadUtil.imageResizeConversion(bitmap,
                ImageUploadUtil.convertImageLimitToBytes(mPresenter.getImageLimit()),
                ImageUploadUtil.getFileSize(bitmap));

        if (newBitmap != null && ImageUploadUtil.getFileSizeInMb(
                ImageUploadUtil.getFileSize(newBitmap)) < mPresenter.getImageLimit()) {

            File file = ImageUploadUtil.getFileForUpload(mActivity, position, newBitmap);
            if (file != null) {
                mImageFileHashMap.put(position, file);
            }

        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    getString(R.string.error_upload_image));
        }
    }

    @Override
    public void asyncExecutionFinished(String imageUrl, int imagePosition) {
        getImageUrl(ImageUploadUtil.convertStringUrltoJSON(imageUrl));

        if (mImageRecyclerView != null) {
            ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder vh = (ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder)
                    mImageRecyclerView.findViewHolderForLayoutPosition(imagePosition);

            if (vh != null) {
                ((ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter()).hideVisibility(vh);
            }
        }

        if (imagePosition + 1 < AppConstants.MAX_IMAGE_COUNT) {
            callUploadImage(imagePosition + 1);
        }
    }

    private void getImageUrl(String imageUrl) {
        SetAttachmentForContactRequest.Item items = new SetAttachmentForContactRequest.Item();
        items.setType("image/png");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size()) {
            SetAttachmentForContactRequest setAttachmentRequest = new SetAttachmentForContactRequest();
            setAttachmentRequest.setMessageId(mMessageId);
            setAttachmentRequest.setNumber(mNumber);
            setAttachmentRequest.setItems(itemsList);
            mPresenter.setAttachment(setAttachmentRequest, true);
        }
    }

    private void callUploadImage(int imageCount) {
        if (mImageFileHashMap.get(imageCount) == null) {
            if (imageCount < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imageCount + 1);
            }
            return;
        }

        if (mImageRecyclerView != null) {
            ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder vh = (ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder)
                    mImageRecyclerView.findViewHolderForLayoutPosition(imageCount);

            if (vh != null) {
                ((ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter()).showProgressBar(vh);
            }
        }

        uploadFileToServer = new ImageUploadUtil.UploadFileToServer(mActivity, false);
        uploadFileToServer.delegate = this;
        uploadFileToServer.execute(mAttachmentId,
                mImageFileHashMap.get(imageCount), mPresenter.getUserAgent(), imageCount,
                GNotification.getDeviceID(mActivity) + System.currentTimeMillis() + ".jpg");
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        mSubject = subject;
    }

    public String getSubjectId() {
        return mSubjectId;
    }

    public void setSubjectId(String subjectId) {
        mSubjectId = subjectId;
    }

    public int getInvoiceNumber() {
        return mInvoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        mInvoiceNumber = invoiceNumber;
    }

    public int getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        mOrderNumber = orderNumber;
    }

    public List<String> getActions() {
        return mActions;
    }

    public void setActions(List<String> actions) {
        mActions = actions;
    }

    public boolean isIsInvoiceRequired() {
        return mIsInvoiceRequired;
    }

    public void setIsInvoiceRequired(boolean isInvoiceRequired) {
        mIsInvoiceRequired = isInvoiceRequired;
    }

    @Override
    public void onInvoiceNumberSubmitted(int orderNumber, int invoiceNumber) {
        setOrderNumber(orderNumber);
        setInvoiceNumber(invoiceNumber);
    }

    private void gotoOrderDetails(OrderDetailsController controller) {
        getRouter().popToRoot();
        mActivity.getAccountController().showMyOrders();
        mActivity.getAccountController().getDisplayRouter()
                .pushController(RouterTransaction.with(controller)
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showDeliveryAddressChanged(boolean success, GetOrdersResponse.Order preloadedOrderDetails) {
        if (success) {
            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(), CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.address_change_success));
            if (preloadedOrderDetails == null) {
                getRouter().popToRoot();
            } else {
                gotoOrderDetails(OrderDetailsController.newInstance(preloadedOrderDetails));
            }
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(), CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.address_change_fail));
        }
    }
}
