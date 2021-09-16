package au.com.dealsdirect.ui.controller.returns.returndetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequestOld;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.Item;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAdapter;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAddImageAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/*
 * Created by Ayi on 05/06/2017.
 */

public class ReturnDetailsController extends BaseController implements ReturnDetailsMvpView,
        ReturnDetailsListener {

    public static final String TAG = "ReturnDetailsController";

    private static final String KEY_TEXT = "ReturnDetailsController.KEY_TEXT";
    private static final String KEY_ORDER_NUMBER = "ReturnDetailsController.KEY_ORDER_NUMBER";
    private static final String KEY_PRODUCT_NAME = "ReturnDetailsController.KEY_PRODUCT_NAME";
    private static final String KEY_REQUEST_DATE = "ReturnDetailsController.REQUEST_DATE";
    private static final String KEY_IS_APPROVED = "ReturnDetailsController.IS_APPROVED";
    private static final String KEY_STATUS = "ReturnDetailsController.STATUS";
    private static final String KEY_RAN = "ReturnDetailsController.RAN";
    private static final String KEY_RETURN_ID = "ReturnDetailsController.RETURN_ID";
    private static final String KEY_IS_FROM_ORDERS = "ReturnDetailsController.KEY_IS_FROM_ORDERS";
    ImageUploadUtil.UploadFileToServer uploadFileToServer;
    @BindView(R.id.controller_return_details_order_number)
    TextView mOrderNumberTextView;
    @BindView(R.id.controller_return_details_order_product_delivery_from_date_value)
    TextView mReturnDetailsControllerRequestDateValue;
    @BindView(R.id.controller_return_details_product_is_approved_value)
    TextView mReturnDetailsControllerisApprovedValue;
    @BindView(R.id.controller_return_details_item_status_value)
    TextView mReturnDetailsControllerItemStatus;
    @BindView(R.id.my_current_return_item_RAN_value)
    TextView mReturnDetailsControllerRanValue;
    @BindView(R.id.controller_return_details_recyclerview)
    RecyclerView mReturnDetailsControllerItemList;
    @BindView(R.id.partial_toolbar_title)
    TextView mReturnDetailsControllerToolbarTitle;
    @BindView(R.id.partial_toolbar_right_view)
    ImageView mReturnDetailsControllerToolbarRightOption;
    @BindView(R.id.controller_return_details_total_container)
    ViewGroup mReturnDetailsTotalContainer;
    @BindView(R.id.controller_return_details_total_value)
    TextView mReturnDetailsControllerTotalValue;
    @BindView(R.id.controller_return_details_reason_container)
    RelativeLayout mReturnDetailsReasonContainer;
    @BindView(R.id.controller_return_details_contact_text)
    TextView mContactNumberText;
    @BindView(R.id.controller_return_details_reason_value)
    TextView mReturnDetailsReasonText;
    @BindView(R.id.contact_history_message_text_view)
    TextView mContactMessageText;
    @BindView(R.id.controller_return_details_contact_date)
    TextView mContactMessageDate;
    @BindView(R.id.controller_return_details_subject_text)
    TextView mReturnDetailsSubjectText;
    @BindView(R.id.controller_return_details_contact_layout)
    FrameLayout mContactMessageContainer;
    @BindView(R.id.controller_return_details_date_container)
    RelativeLayout mContactDateContainer;
    @BindView(R.id.controller_return_details_contact_container)
    RelativeLayout mReturnDetailsContactContainer;
    @BindView(R.id.controller_return_details_image_recyclerview)
    RecyclerView mReturnDetailsImageList;
    @BindView(R.id.controller_return_details_send_message_edittext)
    EditText mReturnDetailsWriteMessageEditText;
    @BindView(R.id.controller_return_details_message_button)
    Button mReturnDetailsButton;
    @BindView(R.id.controller_return_details_subtotal_value)
    TextView mReturnDetailsSubtotal;
    @Inject
    ReturnDetailsMvpPresenter<ReturnDetailsMvpView> mPresenter;
    private String mProductName = "";
    private int mOrderNumber = 0;
    private String mReturnID = "";
    private String mRequestDate = "";
    private String mIsApproved = "";
    private String mStatus = "";
    private String mRAN = "";
    private boolean isFromOrders = false;
    private String contactNumber;
    private String attachmentId = "";
    private String mInvoiceNumber = "";
    private String mContactNumber = "";
    private ArrayList<SetAttachmentRequest.Items> itemsList = new ArrayList<>();
    private Bitmap newBitmap;
    private File imageFile;
    private ArrayList<Bitmap> mBitmapArray = new ArrayList<Bitmap>();
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private ArrayList<SetAttachmentRequest.Items> mApiImageItems = new ArrayList<>();
    private boolean shouldUploadImage = false;
    private boolean isBackButtonPressed = false;
    private boolean isFromAddImageAdapter = false;
    private int attachmentSize = 0;
    private boolean hasSavedInstance = false;
    private String userMessage = "";
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();

    public ReturnDetailsController(Bundle args) {
        super(args);
        if (args.containsKey(KEY_PRODUCT_NAME)) {
            mProductName = args.getString(KEY_PRODUCT_NAME);
        }

        if (args.containsKey(KEY_ORDER_NUMBER)) {
            mOrderNumber = args.getInt(KEY_ORDER_NUMBER);
        }

        mReturnID = args.getString(KEY_RETURN_ID);

        if (args.containsKey(KEY_REQUEST_DATE)) {
            mRequestDate = args.getString(KEY_REQUEST_DATE);
        }

        if (args.containsKey(KEY_IS_APPROVED)) {
            mIsApproved = args.getString(KEY_IS_APPROVED);
        }

        if (args.containsKey(KEY_STATUS)) {
            mStatus = args.getString(KEY_STATUS);
        }

        if (args.containsKey(KEY_RAN)) {
            mRAN = args.getString(KEY_RAN);
        }

        if (args.containsKey(KEY_IS_FROM_ORDERS)) {
            isFromOrders = args.getBoolean(KEY_IS_FROM_ORDERS);
        }
    }

    public static ReturnDetailsController newInstance(
            String productName,
            int orderNumber,
            String returnID,
            String requestDate,
            String isApproved,
            String status,
            String RAN) {

        return new ReturnDetailsController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_PRODUCT_NAME, productName)
                        .putInt(KEY_ORDER_NUMBER, orderNumber)
                        .putString(KEY_RETURN_ID, returnID)
                        .putString(KEY_REQUEST_DATE, requestDate)
                        .putString(KEY_IS_APPROVED, isApproved)
                        .putString(KEY_STATUS, status)
                        .putString(KEY_RAN, RAN)
                        .build());
    }

    public static ReturnDetailsController newInstance(
            String returnID,
            String productName,
            boolean fromOrders) {

        return new ReturnDetailsController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_RETURN_ID, returnID)
                        .putString(KEY_PRODUCT_NAME, productName)
                        .putBoolean(KEY_IS_FROM_ORDERS, fromOrders)
                        .build());
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_return_details, container, false);

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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(BundleKeys.KEY_PRODUCT_NAME, mProductName);
        outState.putInt(BundleKeys.KEY_ORDER_NUMBER, mOrderNumber);
        outState.putString(BundleKeys.KEY_REQUEST_DATE, mRequestDate);
        outState.putString(BundleKeys.KEY_IS_APPROVED, mIsApproved);
        outState.putString(BundleKeys.KEY_STATUS, mStatus);
        outState.putString(BundleKeys.KEY_RAN, mRAN);
        outState.putString(BundleKeys.KEY_RETURN_ID, mReturnID);
        outState.putBoolean(BundleKeys.KEY_IS_FROM_ORDER, isFromOrders);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
        outState.putString(BundleKeys.KEY_USER_MESSAGE, mReturnDetailsWriteMessageEditText.getText().toString());
        outState.putBoolean(BundleKeys.KEY_SHOULD_UPLOAD_IMAGE, shouldUploadImage);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mProductName = savedInstanceState.getString(BundleKeys.KEY_PRODUCT_NAME, "");
        mOrderNumber = savedInstanceState.getInt(BundleKeys.KEY_ORDER_NUMBER, 0);
        mRequestDate = savedInstanceState.getString(BundleKeys.KEY_REQUEST_DATE, "");
        mIsApproved = savedInstanceState.getString(BundleKeys.KEY_IS_APPROVED, "");
        mStatus = savedInstanceState.getString(BundleKeys.KEY_STATUS, "");
        mRAN = savedInstanceState.getString(BundleKeys.KEY_RAN, "");
        mReturnID = savedInstanceState.getString(BundleKeys.KEY_RETURN_ID, "");
        isFromOrders = savedInstanceState.getBoolean(BundleKeys.KEY_IS_FROM_ORDER, false);
        hasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
        userMessage = savedInstanceState.getString(BundleKeys.KEY_USER_MESSAGE, "");
        shouldUploadImage = savedInstanceState.getBoolean(BundleKeys.KEY_SHOULD_UPLOAD_IMAGE);
    }

    @Override
    protected void setUp(View view) {

        mReturnDetailsControllerToolbarRightOption.setVisibility(View.INVISIBLE);
        mReturnDetailsControllerToolbarTitle.setText(mProductName);
        mOrderNumberTextView.append(" " + mOrderNumber);
        mReturnDetailsControllerRequestDateValue.setText(mRequestDate);
        mReturnDetailsControllerisApprovedValue.setText(mIsApproved);
        mReturnDetailsControllerItemStatus.setText(mStatus);
        mReturnDetailsControllerRanValue.setText(mRAN);

        if (hasSavedInstance) {
            mReturnDetailsWriteMessageEditText.setText(userMessage);
        }

        mPresenter.loadCurrentReturnDetails(mReturnID);

    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody) {

        mApiImageItems.clear();
        mBitmapArray.clear();
        mImageFileHashMap.clear();

        List<Item> items = getReturnDetailsResponseBody.getValue().getItems();
        Double subTotal = getReturnDetailsResponseBody.getValue().getTotal();
        mInvoiceNumber = getReturnDetailsResponseBody.getValue().getInvoiceNumber();
        mContactNumber = String.valueOf(getReturnDetailsResponseBody.getValue().getContactNumber());

        attachmentId = getReturnDetailsResponseBody.getValue().getAttachmentId();

        String subtotal = Settings.getSelectedCountry().currencySign +
                getReturnDetailsResponseBody.getValue().getTotal();
        mReturnDetailsSubtotal.setText(subtotal);
        mReturnDetailsControllerTotalValue.setText(PriceUtils.getPriceStringValue(subTotal));

        ReturnDetailsAdapter adapter = new ReturnDetailsAdapter(items, subTotal, mActivity);
        LinearLayoutManager itemsLayoutManager = new LinearLayoutManager(mActivity);
        mReturnDetailsControllerItemList.setAdapter(adapter);
        mReturnDetailsControllerItemList.setLayoutManager(itemsLayoutManager);

        mReturnDetailsTotalContainer.setVisibility(View.VISIBLE);

        mReturnDetailsContactContainer.setVisibility(getReturnDetailsResponseBody.getValue().getContactNumber() != 0 ?
                View.VISIBLE : View.GONE);

        if (isFromOrders || getReturnDetailsResponseBody.getValue().getContactNumber() != 0) {
            contactNumber = String.valueOf(getReturnDetailsResponseBody.getValue().getContactNumber());
            String concatenateContactNo = mActivity.getResources().getString(R.string.contact_number_return_details) + contactNumber;
            mContactNumberText.setText(concatenateContactNo);
            GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
            getContactHistoryRequest.setNumber(getReturnDetailsResponseBody.getValue().getContactNumber());
            mPresenter.loadReturnContacts(getContactHistoryRequest);
        }

        mReturnDetailsReasonText.setText(getReturnDetailsResponseBody.getValue().getReason());

        attachmentSize = getReturnDetailsResponseBody.getValue().getAttachments().size();

        if (mImageUriArray.size() == 0 && attachmentSize != 0) {
            for (int i = 0; i < attachmentSize; i++) {
                ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
                imageLinks.setIsURL(true);
                imageLinks.setLink(getReturnDetailsResponseBody.getValue().getAttachments().get(i).getUrl());
                mImageUriArray.add(0, imageLinks);
            }
        }

        ReturnDetailsAddImageAdapter returnDetailsImageAdapter = new ReturnDetailsAddImageAdapter(mActivity, this,
                mImageUriArray);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mReturnDetailsImageList.setAdapter(returnDetailsImageAdapter);
        mReturnDetailsImageList.setLayoutManager(layoutManager);
    }

    @Override
    public void showContactMessageReturn(GetContactHistoryResponse responseValue) {

        GetContactHistoryResponse.Message lastItemPosition = responseValue.getMessages().get(0);

        mContactMessageDate.setText(DateUtils.getDateForContactMessages(lastItemPosition.getMessageDate()));
        mContactMessageText.setText(lastItemPosition.getText());
        mReturnDetailsSubjectText.setText(responseValue.getSubject());

        mReturnDetailsContactContainer.setOnClickListener(v -> {
            getRouter().pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                    responseValue.getSubject(),
                    responseValue.getInvoiceNumber(),
                    DateUtils.getDateForContactMessages(lastItemPosition.getMessageDate()),
                    Integer.parseInt(contactNumber),
                    true))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        });
    }

    @Override
    public void refreshReturnDetails(SetAttachmentResponse setAttachmentResponse) {
        shouldUploadImage = false;

        if (isBackButtonPressed) {
            mActivity.onBackPressed();
        } else {
            if (attachmentId == null) {
                attachmentId = setAttachmentResponse.getD().getValue();
            }
            mPresenter.loadCurrentReturnDetails(mReturnID);
        }
    }

    @Override
    public void getImageUrl(String imageUrl) {

        SetAttachmentRequest.Items items = new SetAttachmentRequest.Items();
        items.setType("image/jpeg");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size()) {
            shouldUploadImage = false;
            SetAttachmentRequest setAttachmentRequest = new SetAttachmentRequest();
            setAttachmentRequest.setId(mReturnID);
            setAttachmentRequest.setType("return");
            setAttachmentRequest.setListItems(itemsList);
            mPresenter.setAttachment(setAttachmentRequest);
        }
    }

    @Override
    public void finishedSendMessage(String response) {
        if (shouldUploadImage) {
            uploadImages();
        }
        mReturnDetailsWriteMessageEditText.getText().clear();
        mPresenter.loadCurrentReturnDetails(mReturnID);
        String message;
        if (response.equals("true")) {
            message = mActivity.getString(R.string.message_submitted);
        } else {
            message = response;
        }
        CustomAlertDialog.showCustomAlertDialog(
                mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                message);
    }


    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        isBackButtonPressed = true;
        if (shouldUploadImage) {
            uploadImages();
        } else {
            mActivity.onBackPressed();
        }


    }

    @Override
    public void getImageFromDirectory(boolean uploadImage) {
        shouldUploadImage = uploadImage;

        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    AppConstants.REQUEST_CODE_PERMISSION);
        } else {

            Intent cameraIntent = new Intent(Intent.ACTION_PICK);
            cameraIntent.setType("image/*");
            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(cameraIntent, AppConstants.REQUEST_CODE_FOR_SUCCESS);
            }
        }
    }

    @Override
    public void removeImage(Bitmap image, int position, boolean uploadImage, boolean isAddImageAdapter) {

        ReturnDetailsAddImageAdapter adapter = ((ReturnDetailsAddImageAdapter) mReturnDetailsImageList.getAdapter());

        isFromAddImageAdapter = isAddImageAdapter;

        shouldUploadImage = uploadImage;

        mImageFileHashMap.remove(adapter.dataPositionToItemPosition(position));
        mImageUriArray.remove(position);

        adapter.removeItem(adapter.dataPositionToItemPosition(position));
    }

    @Override
    public void addItemFromLink(String url, int position, Bitmap bitmap) {

        SetAttachmentRequest.Items items = new SetAttachmentRequest.Items();
        items.setType("image/jpeg");
        items.setUrl(url);
        mApiImageItems.add(items);

        mBitmapArray.add(bitmap);

        newBitmap = ImageUploadUtil.imageResizeConversion(bitmap,
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK || resultCode == AppConstants.REQUEST_CODE_FOR_SUCCESS) && data != null) {
            Uri chosenImageUri = data.getData();
            Bitmap mBitmap = null;

            ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
            imageLinks.setIsURL(false);
            imageLinks.setLink(String.valueOf(chosenImageUri));
            mImageUriArray.add(0, imageLinks);

            if (hasSavedInstance) {
                mPresenter.loadCurrentReturnDetails(mReturnID);
            } else {

                if (mReturnDetailsImageList.getAdapter() != null) {
                    ((ReturnDetailsAddImageAdapter) mReturnDetailsImageList.getAdapter()).addItem();
                } else {
                    ReturnDetailsAddImageAdapter imageAdapter = new ReturnDetailsAddImageAdapter(mActivity, this,
                            mImageUriArray);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
                    mReturnDetailsImageList.setAdapter(imageAdapter);
                    mReturnDetailsImageList.setLayoutManager(layoutManager);
                }
            }

            if (attachmentId == null) {
                mPresenter.setAttachment(ImageUploadUtil.getAttachmentIdRequest(mReturnID));
            }

        }
    }

    @OnClick(R.id.controller_return_details_message_button)
    public void onClick() {
        if (shouldUploadImage && mReturnDetailsWriteMessageEditText.getText().toString().isEmpty()) {
            uploadImages();
        } else {
            if (mContactNumber == null || mContactNumber.equals("0")) {
                hideKeyboard();
                CreateContactRequestOld createContactRequest = new CreateContactRequestOld();

                if (!mInvoiceNumber.isEmpty()) {
                    createContactRequest.invoiceNo = Integer.valueOf(mInvoiceNumber);
                } else {
                    createContactRequest.invoiceNo = 0;
                }

                createContactRequest.subj = mActivity.getResources().getString(R.string.returns_enquiry);

                if (mReturnDetailsWriteMessageEditText.getText().toString().isEmpty()) {
                    CustomAlertDialog.showCustomAlertDialog(mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getString(R.string.create_contact_fill_up));
                } else {
                    createContactRequest.msg = mReturnDetailsWriteMessageEditText.getText().toString();
                    mPresenter.sendMessage(createContactRequest);

                }
            } else {

                hideKeyboard();

                String replyMessage = mReturnDetailsWriteMessageEditText.getText().toString();

                ReplyContactRequest replyContactRequest = new ReplyContactRequest();
                replyContactRequest.setText(replyMessage);
                replyContactRequest.setNumber(Integer.parseInt(mContactNumber));

                if (replyContactRequest.getText().isEmpty()) {

                    CustomAlertDialog.showCustomAlertDialog(
                            mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getString(R.string.create_contact_fill_up)
                    );

                } else {
                    mPresenter.replyMessage(replyContactRequest);
                }
            }
        }
    }

    private void uploadImages() {
        if (mImageFileHashMap != null) {
            callUploadImage(0);
        }
    }

    private void callUploadImage(int imagePosition) {
        if (mImageFileHashMap.get(imagePosition) == null) {
            if (imagePosition < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imagePosition + 1);
            }
            return;
        }
        uploadFileToServer = new ImageUploadUtil.UploadFileToServer(mActivity, true);
        uploadFileToServer.delegate = (urlString, imagePosition1) -> {
            getImageUrl(ImageUploadUtil.convertStringUrltoJSON(urlString));

            if (imagePosition1 + 1 < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imagePosition1 + 1);
            }
        };
        uploadFileToServer.execute(attachmentId,
                mImageFileHashMap.get(imagePosition), mPresenter.getUserAgent(), imagePosition,
                GNotification.getDeviceID(mActivity) + System.currentTimeMillis() + ".jpg");
    }

}
