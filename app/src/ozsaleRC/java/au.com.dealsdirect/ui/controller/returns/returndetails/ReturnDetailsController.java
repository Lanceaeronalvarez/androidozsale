package au.com.dealsdirect.ui.controller.returns.returndetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
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
import au.com.dealsdirect.utils.AppLogger;
import au.com.dealsdirect.utils.AsyncResponse;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;
import static android.app.Activity.RESULT_OK;

/*
 * Created by Ayi on 05/06/2017.
 */

public class ReturnDetailsController extends BaseController implements ReturnDetailsMvpView,
        ReturnDetailsListener, AsyncResponse {

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
    private ArrayList<File> mImageFileArray = new ArrayList<>();
    ImageUploadUtil.UploadFileToServer uploadFileToServer;
    private ArrayList<SetAttachmentRequest.Items> mApiImageItems = new ArrayList<>();
    private boolean shouldUploadImage = false;
    private boolean isBackButtonPressed = false;
    private boolean isFromAddImageAdapter = false;
    private int attachmentSize = 0;

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
    RecyclerView mReturnDetailsControllerRecyclerView;

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
    RecyclerView mReturnDetailsRecyclerview;

    @BindView(R.id.controller_return_details_send_message_edittext)
    EditText mReturnDetailsWriteMessageEditText;

    @BindView(R.id.controller_return_details_message_button)
    Button mReturnDetailsButton;

    @BindView(R.id.controller_return_details_subtotal_value)
    TextView mReturnDetailsSubtotal;

    @Inject
    ReturnDetailsMvpPresenter<ReturnDetailsMvpView> mPresenter;

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
                        .putString(KEY_PRODUCT_NAME,productName)
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
    protected void setUp(View view) {

        mReturnDetailsControllerToolbarRightOption.setVisibility(View.INVISIBLE);
        mReturnDetailsControllerToolbarTitle.setText(mProductName);
        mOrderNumberTextView.append(" " + mOrderNumber);
        mReturnDetailsControllerRequestDateValue.setText(mRequestDate);
        mReturnDetailsControllerisApprovedValue.setText(mIsApproved);
        mReturnDetailsControllerItemStatus.setText(mStatus);
        mReturnDetailsControllerRanValue.setText(mRAN);

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
        mImageFileArray.clear();

        List<Item> items = getReturnDetailsResponseBody.getValue().getItems();
        Double subTotal = getReturnDetailsResponseBody.getValue().getTotal();
        mInvoiceNumber = getReturnDetailsResponseBody.getValue().getInvoiceNumber();
        mContactNumber = String.valueOf(getReturnDetailsResponseBody.getValue().getContactNumber());

        attachmentId = getReturnDetailsResponseBody.getValue().getAttachmentId();

        String subtotal = Settings.getSelectedCountry().currencySign +
                getReturnDetailsResponseBody.getValue().getTotal();
        mReturnDetailsSubtotal.setText(subtotal);

        ReturnDetailsAdapter adapter = new ReturnDetailsAdapter(items, subTotal, mActivity);

        mReturnDetailsControllerTotalValue.setText(PriceUtils.getPriceStringValue(subTotal));
        mReturnDetailsControllerRecyclerView.setAdapter(adapter);
        mReturnDetailsControllerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mReturnDetailsTotalContainer.setVisibility(View.VISIBLE);

        mReturnDetailsContactContainer.setVisibility(getReturnDetailsResponseBody.getValue().getContactNumber() != 0 ?
                View.VISIBLE : View.GONE);

        if (isFromOrders || getReturnDetailsResponseBody.getValue().getContactNumber() != 0) {
            contactNumber = String.valueOf(getReturnDetailsResponseBody.getValue().getContactNumber());
            String concatenateContactNo = mActivity.getResources().getString(R.string.contact_number_return_details) + contactNumber;
            mContactNumberText.setText(concatenateContactNo);
            GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
            getContactHistoryRequest.contactNo = getReturnDetailsResponseBody.getValue().getContactNumber();
            mPresenter.loadReturnContacts(getContactHistoryRequest);
        }

        mReturnDetailsReasonText.setText(getReturnDetailsResponseBody.getValue().getReason());

        attachmentSize = getReturnDetailsResponseBody.getValue().getAttachments().size();

        ReturnDetailsAddImageAdapter returnDetailsImageAdapter = new ReturnDetailsAddImageAdapter(mActivity,this,
                new ArrayList<>(),
                getReturnDetailsResponseBody.getValue().getAttachments());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mReturnDetailsRecyclerview.setAdapter(returnDetailsImageAdapter);
        mReturnDetailsRecyclerview.setLayoutManager(layoutManager);
    }

    @Override
    public void showContactMessageReturn(GetContactHistoryResponse.ResponseValue responseValue) {

        au.com.dealsdirect.data.network.model.contacthistory.List lastItemPosition = responseValue.getList().get(0);

        mContactMessageDate.setText(DateUtils.getDateForContactMessages(lastItemPosition.getDate()));
        mContactMessageText.setText(lastItemPosition.getText());
        mReturnDetailsSubjectText.setText(lastItemPosition.getSubject());

        mReturnDetailsContactContainer.setOnClickListener(v -> {
           getRouter().pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                    lastItemPosition.getSubject(),
                    "",
                    lastItemPosition.getInvoiceNo(),
                    DateUtils.getDateForContactMessages(lastItemPosition.getDate()),
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

        if (mBitmapArray.size() == itemsList.size()) {
            shouldUploadImage = false;
            SetAttachmentRequest setAttachmentRequest = new SetAttachmentRequest();
            setAttachmentRequest.setId(mReturnID);
            setAttachmentRequest.setType("return");
            setAttachmentRequest.setListItems(itemsList);
            mPresenter.setAttachment(setAttachmentRequest);
        }
    }

    @Override
    public void finishedSendMessage(String message) {
        if (shouldUploadImage) {
            uploadImages();
        }
        mReturnDetailsWriteMessageEditText.getText().clear();
        mPresenter.loadCurrentReturnDetails(mReturnID);
        CustomAlertDialog.showCustomAlertDialog(
                mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                message);
    }


    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick(){
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

        if(ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
        }
        else {

            Intent cameraIntent = new Intent(Intent.ACTION_PICK);
            cameraIntent.setType("image/*");
            if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(cameraIntent, 1000);
            }
        }
    }

    @Override
    public void removeImage(Bitmap image, int position, boolean uploadImage, boolean isAddImageAdapter) {

        isFromAddImageAdapter = isAddImageAdapter;

        shouldUploadImage = uploadImage;

        if (image != null && mBitmapArray.contains(image)) {
            mBitmapArray.remove(position - 1);
            mImageFileArray.remove(position - 1);
            ((ReturnDetailsAddImageAdapter) mReturnDetailsRecyclerview.getAdapter()).removeItem(position);
        }
    }

    @Override
    public void addItemFromApi(String url, Bitmap bitmap) {

        SetAttachmentRequest.Items items = new SetAttachmentRequest.Items();
        items.setType("image/jpeg");
        items.setUrl(url);
        mApiImageItems.add(items);

        mBitmapArray.add(bitmap);

        try {

            File f = new File(mActivity.getCacheDir(), mPresenter.getEventUserId()+mBitmapArray.size()+".jpg");
            f.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = null;
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();

            mImageFileArray.add(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mApiImageItems.size() == attachmentSize && !isBackButtonPressed) {
            ReturnDetailsAddImageAdapter imageAdapter = new ReturnDetailsAddImageAdapter(mActivity, this, mBitmapArray,
                    new ArrayList<>());
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            mReturnDetailsRecyclerview.setAdapter(imageAdapter);
            mReturnDetailsRecyclerview.setLayoutManager(layoutManager);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == 1000)
        {
            Uri chosenImageUri = data.getData();
            Bitmap mBitmap = null;
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), chosenImageUri);

                newBitmap = ImageUploadUtil.imageResizeConversion(mBitmap,
                        ImageUploadUtil.convertImageLimitToBytes(mPresenter.getImageLimit()),
                        ImageUploadUtil.getFileSize(mBitmap));

                
                if (newBitmap != null && ImageUploadUtil.getFileSizeInMb(
                        ImageUploadUtil.getFileSize(newBitmap)) < 1) {
                    File f = new File(mActivity.getCacheDir(), GNotification.getDeviceID(mActivity)+mBitmapArray.size()+".jpg");
                    f.createNewFile();

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();
                    mImageFileArray.add(0, f);
                    mBitmapArray.add(0,mBitmap);
                    ((ReturnDetailsAddImageAdapter) mReturnDetailsRecyclerview.getAdapter()).addItem();
                } else {
                    CustomAlertDialog.showCustomAlertDialog(mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getResources().getString(R.string.error_upload_image));
                }

                if (attachmentId == null) {
                    mPresenter.setAttachment(ImageUploadUtil.getAttachmentIdRequest(mReturnID));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                CustomAlertDialog.showCustomAlertDialog(mActivity,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        mActivity.getResources().getString(R.string.error_upload_image));
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
                CreateContactRequest createContactRequest = new CreateContactRequest();

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
                replyContactRequest.comments = replyMessage;
                replyContactRequest.contactNo = Integer.parseInt(mContactNumber);

                if (replyContactRequest.comments.isEmpty()) {

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
        if (mBitmapArray != null && mImageFileArray != null) {
            callUploadImage(0);
        }
    }

    @Override
    public void asyncExecutionFinished(String urlString, int imagePosition) {

        // set image url from response
        getImageUrl(ImageUploadUtil.convertStringUrltoJSON(urlString));

        int imageUploadedSize = imagePosition + 1;
        if (imageUploadedSize != mBitmapArray.size()) {
            int newImagePosition = imagePosition + 1;
            callUploadImage(newImagePosition);
        }
    }

    private void callUploadImage(int imagePosition) {
        uploadFileToServer = new ImageUploadUtil.UploadFileToServer(mActivity);
        uploadFileToServer.delegate = this;
        uploadFileToServer.execute(attachmentId,
                mImageFileArray.get(imagePosition), mPresenter.getUserAgent(), imagePosition,
                GNotification.getDeviceID(mActivity)+System.currentTimeMillis()+".jpg");
    }

}
