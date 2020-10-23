package au.com.dealsdirect.ui.controller.contact.addcontact;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.ContactPreferenceHelper;
import au.com.dealsdirect.ui.controller.contact.selectorder.ContactSelectOrderController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactsAddImageAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsMvpView;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AsyncResponse;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * dp Created by Admin on 6/20/17.
 */

public class AddContactController extends BaseController implements AddContactMvpView,
        ReturnDetailsListener, AsyncResponse {

    public static final String TAG = "AddContactController";
    public static final String INVOICE_NUMBER = "INVOICE_NUMBER";
    public static final String IS_CALLED_FROM_ORDERS = "IS_CALLED_FROM_ORDERS";
    public static final String ITEM_DESCRIPTION = "ITEM_DESCRIPTION";

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

    @BindView(R.id.controller_add_contact_order_text)
    TextView mAddContactOrderText;

    @BindView(R.id.controller_view_contacts_history_message_field)
    EditText mAddContactMessageField;

    @BindView(R.id.controller_view_contacts_history_reply_button)
    ImageButton mAddContactMessageSend;

    @BindView(R.id.controller_add_contact_selector_container)
    FrameLayout mAddContactSelectorContainer;

    @BindView(R.id.view_contact_history_image_recyclerview)
    RecyclerView mImageRecyclerView;

    @Inject
    AddContactMvpPresenter<AddContactMvpView> mPresenter;

    private String mChosenOptionInvoice;

    private String mChosenSubject = "";
    private int mInvoiceNumber;
    private String mDescription = "";
    private boolean isCalledFromOrders = false;
    private String mAttachmentId = "";
    private int contactValue = 0;

    private ViewContactsMvpView mViewContactsMvpView;
    private boolean mHasSavedInstance = false;
    private ViewContactsAddImageAdapter mImageAdapter;
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private String mMessageId = "";
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private ArrayList<SetAttachmentRequest.Items> itemsList = new ArrayList<>();
    ImageUploadUtil.UploadFileToServer uploadFileToServer;

    public static AddContactController newInstance() {
        return new AddContactController(
                new BundleBuilder(new Bundle()).build());
    }

    public static AddContactController newInstance(
            int invoiceNo,
            boolean isCalledFromOrders,
            String itemDescription) {

        return new AddContactController(
                new BundleBuilder(new Bundle())
                        .putInt(INVOICE_NUMBER, invoiceNo)
                        .putBoolean(IS_CALLED_FROM_ORDERS, isCalledFromOrders)
                        .putString(ITEM_DESCRIPTION, itemDescription)
                        .build());
    }


    public AddContactController(Bundle args) {
        super(args);
        mInvoiceNumber = getArgs().getInt(INVOICE_NUMBER);
        mDescription = getArgs().getString(ITEM_DESCRIPTION, "");
        isCalledFromOrders = getArgs().getBoolean(IS_CALLED_FROM_ORDERS, false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.CONTACT_SUBJECT, mChosenSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mChosenSubject = savedInstanceState.getString(BundleKeys.CONTACT_SUBJECT);
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

        if (ContactPreferenceHelper.getChosenInvoice(mActivity).isEmpty() &&
                !isCalledFromOrders) {
            mAddContactOrderText.setText(getString(R.string.select_a_sale));
        } else {

            if (isCalledFromOrders) {
                ContactPreferenceHelper.setChosenInvoiceString(mActivity, String.valueOf(mInvoiceNumber));
                ContactPreferenceHelper.setChosenOrderString(mActivity, mDescription);
            }

            mAddContactOrderText.setText(ContactPreferenceHelper.getChosenInvoice(mActivity)
                    + " " + ContactPreferenceHelper.getChosenOrder(mActivity));
        }
        String message = ContactPreferenceHelper.getContactMessage(mActivity);
        if (!message.isEmpty()) {
            mAddContactMessageField.setText(message);
        }

        mChosenSubject = ContactPreferenceHelper.getChosenSubject(mActivity);
        if (!mChosenSubject.isEmpty()) {
            mAddContactSubjectText.setText(ContactPreferenceHelper.getChosenSubject(mActivity));
        }

        mAddContactMessageSend.setOnClickListener(v -> sendMessage());

        mImageAdapter = new ViewContactsAddImageAdapter(mActivity, this,
                mImageUriArray);
        mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void sendMessage() {
        ContactPreferenceHelper.clear(mActivity);
        hideKeyboard();
        mChosenOptionInvoice = ContactPreferenceHelper.getChosenInvoice(mActivity);
        CreateContactRequest createContactRequest = new CreateContactRequest();

        if (!mChosenOptionInvoice.isEmpty()) {
            createContactRequest.invoiceNo = Integer.valueOf(mChosenOptionInvoice);
        } else {
            createContactRequest.invoiceNo = 0;
        }

        createContactRequest.subj = mChosenSubject;

        if (mAddContactMessageField.getText().toString().isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.create_contact_fill_up));
        } else {
            createContactRequest.msg = mAddContactMessageField.getText().toString();
            mPresenter.createNewContact(createContactRequest);

        }
    }

    @Override
    public void onDestroyView(View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        ContactPreferenceHelper.setContactMessage(mActivity, mAddContactMessageField.getText().toString());
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
        mAddContactMessageField.clearFocus();
        hideKeyboard();

        getRouter().pushController(RouterTransaction.with(ContactSelectOrderController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler()).popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void contactCreatedSwitchView(CreateContactResponse createContactResponse) {
        if (createContactResponse.getCreateContact().getResult()) {
            contactValue = createContactResponse.getCreateContact().getValue();

            if (mImageUriArray.size() != 0) {
                GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
                getContactHistoryRequest.contactNo = contactValue;
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

        String saleName = !ContactPreferenceHelper.getChosenOrder(mActivity).isEmpty() ? ContactPreferenceHelper.getChosenOrder(mActivity) : "";
        int invoiceNo = !mChosenOptionInvoice.isEmpty() ? Integer.valueOf(mChosenOptionInvoice) : 0;

        ViewContactHistoryController controller = ViewContactHistoryController.newInstance(
                mChosenSubject,
                saleName,
                invoiceNo,
                mAddContactMessageField.getText().toString(),
                contactValue,
                false);

        RouterTransaction routerTransaction = RouterTransaction.with(controller)
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());

        getRouter().popToRoot();
        getRouter().pushController(routerTransaction);
    }

    @Override
    public void showContactSuccess(java.util.List<List> myContactItems) {

        mMessageId = myContactItems.get(0).getId();

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

        SetAttachmentRequest setAttachmentRequest = new SetAttachmentRequest();
        setAttachmentRequest.setId(mMessageId);
        setAttachmentRequest.setType("contact");
        setAttachmentRequest.setListItems(new ArrayList<>());
        mPresenter.setAttachment(setAttachmentRequest, false);
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
        if(ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppConstants.REQUEST_CODE_PERMISSION);
        }
        else {

            Intent cameraIntent = new Intent(Intent.ACTION_PICK);
            cameraIntent.setType("image/*");
            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(cameraIntent, AppConstants.REQUEST_CODE_FOR_SUCCESS);
            }
        }
    }

    @Override
    public void getAttachmentId(SetAttachmentResponse setAttachmentResponse) {
        mAttachmentId = setAttachmentResponse.getD().getValue();

        if (mImageFileHashMap != null) {
            callUploadImage(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == AppConstants.REQUEST_CODE_FOR_SUCCESS)
        {
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
                    mActivity.getResources().getString(R.string.error_upload_image));
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
        SetAttachmentRequest.Items items = new SetAttachmentRequest.Items();
        items.setType("image/png");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size()) {
            SetAttachmentRequest setAttachmentRequest = new SetAttachmentRequest();
            setAttachmentRequest.setId(mMessageId);
            setAttachmentRequest.setType("contact");
            setAttachmentRequest.setListItems(itemsList);
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
                GNotification.getDeviceID(mActivity)+System.currentTimeMillis()+".jpg");
    }
}
