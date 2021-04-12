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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedSatisfactionValue;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.data.network.model.returns.step.Step;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.BottomSheetReturnSatisfactionDialog;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAdapter;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAddImageAdapter;
import au.com.dealsdirect.ui.controller.returns.returnsteps.ReturnTrackingView;
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

public class ReturnDetailsController extends BaseController implements ReturnDetailsMvpView {

    public static final String TAG = "ReturnDetailsController";

    private static final String KEY_PRODUCT_NAME = "ReturnDetailsController.KEY_PRODUCT_NAME";
    private static final String KEY_RETURN_ID = "ReturnDetailsController.RETURN_ID";
    private static final String KEY_IS_FROM_ORDERS = "ReturnDetailsController.KEY_IS_FROM_ORDERS";
    private ImageUploadUtil.UploadFileToServer uploadFileToServer;
    @BindView(R.id.controller_return_details_scrollview)
    ScrollView mReturnDetailsScrollView;
    @BindView(R.id.controller_return_details_recyclerview)
    RecyclerView mReturnDetailsControllerItemList;
    @BindView(R.id.partial_toolbar_title)
    TextView mReturnDetailsControllerToolbarTitle;
    @BindView(R.id.partial_toolbar_right_view)
    ImageView mReturnDetailsControllerToolbarRightOption;
    @BindView(R.id.controller_return_credit_card_refunds_value)
    TextView mReturnDetailsControllerCreditCardRefundsValue;
    @BindView(R.id.controller_return_store_credits_value)
    TextView mReturnDetailsControllerStoreCreditsValue;
    @BindView(R.id.controller_return_amount_info_button)
    ImageButton mReturnDetailsControllerReturnAmountInfoButton;
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
    @BindView(R.id.component_return_tracking)
    ReturnTrackingView trackingView;
    @Inject
    ReturnDetailsMvpPresenter<ReturnDetailsMvpView> mPresenter;
    private String mToolbarTitle = "";
    private String mReturnId = "";
    private boolean isFromOrders = false;
    private ArrayList<ImageAttachment> itemsList = new ArrayList<>();
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private boolean shouldUploadImage = false;
    private boolean isBackButtonPressed = false;
    private boolean hasSavedInstance = false;
    private String userMessage = "";
    private String attachmentId = "";
    private boolean willScrollToMessage = false;
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();

    HashMap<String, Boolean> hasSetSatisfaction = new HashMap<>();

    private ReturnDetailsListener returnDetailsListener = new ReturnDetailsListener() {
        @Override
        public void getImageFromDirectory(boolean uploadImage) {
            shouldUploadImage = true;

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
        public void removeImage(Bitmap bitmap, int position, boolean uploadImage, boolean isAddImageAdapter) {
            shouldUploadImage = true;

            ReturnDetailsAddImageAdapter adapter = ((ReturnDetailsAddImageAdapter) mReturnDetailsImageList.getAdapter());

            mImageFileHashMap.remove(position);
            final int previousSize = mImageUriArray.size();
            mImageUriArray.remove(position);

            adapter.notifyItemRemoved(position);
            if (previousSize == AppConstants.MAX_IMAGE_COUNT) {
                adapter.notifyItemInserted(mImageUriArray.size());
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
    };

    private CurrentReturn item = null;

    public ReturnDetailsController(Bundle args) {
        super(args);
        if (args.containsKey(KEY_PRODUCT_NAME)) {
            mToolbarTitle = args.getString(KEY_PRODUCT_NAME);
        }

        mReturnId = args.getString(KEY_RETURN_ID);

        if (args.containsKey(KEY_IS_FROM_ORDERS)) {
            isFromOrders = args.getBoolean(KEY_IS_FROM_ORDERS);
        }
    }

    public static ReturnDetailsController newInstance(CurrentReturn item) {
        return newInstance(item, false);
    }

    public static ReturnDetailsController newInstance(
            CurrentReturn item,
            boolean willScrollToMessage) {
        ReturnDetailsController controller = new ReturnDetailsController(
                new BundleBuilder(new Bundle())
                        .build());
        controller.item = item;
        controller.willScrollToMessage = willScrollToMessage;
        return controller;
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
        outState.putString(BundleKeys.KEY_TOOLBAR_TITLE, mToolbarTitle);
        outState.putString(BundleKeys.KEY_RETURN_ID, mReturnId);
        outState.putBoolean(BundleKeys.KEY_IS_FROM_ORDER, isFromOrders);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
        outState.putString(BundleKeys.KEY_USER_MESSAGE, mReturnDetailsWriteMessageEditText.getText().toString());
        outState.putBoolean(BundleKeys.KEY_SHOULD_UPLOAD_IMAGE, shouldUploadImage);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mToolbarTitle = savedInstanceState.getString(BundleKeys.KEY_TOOLBAR_TITLE, "");
        mReturnId = savedInstanceState.getString(BundleKeys.KEY_RETURN_ID, "");
        isFromOrders = savedInstanceState.getBoolean(BundleKeys.KEY_IS_FROM_ORDER, false);
        hasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
        userMessage = savedInstanceState.getString(BundleKeys.KEY_USER_MESSAGE, "");
        shouldUploadImage = savedInstanceState.getBoolean(BundleKeys.KEY_SHOULD_UPLOAD_IMAGE);
    }

    @Override
    protected void setUp(View view) {

        mReturnId = item.getId();
        mToolbarTitle = "Invoice " + item.getInvoiceNumber();
        mPresenter.loadCurrentReturnDetails(mReturnId);

        mReturnDetailsControllerToolbarRightOption.setVisibility(View.INVISIBLE);
        mReturnDetailsControllerToolbarTitle.setText(mToolbarTitle);

        if (hasSavedInstance) {
            mReturnDetailsWriteMessageEditText.setText(userMessage);
        }

        showCurrentReturnDetails(item);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showCurrentReturnDetails(CurrentReturn item) {
        ReturnReceivedRequest request = new ReturnReceivedRequest();
        request.setReturnId(item.getId());
        mPresenter.callGetReturnReceivedSatisfaction(request);

        if (willScrollToMessage && mReturnDetailsScrollView.isLaidOut()) {
            willScrollToMessage = false;
            scrollToMessage();
        }

        if (item == null) {
            return;
        }

        this.item = item;

        attachmentId = item.getAttachmentId();

        List<Step> steps = new ArrayList<>(item.getSteps());
        trackingView.setup(steps, item.getId(), (returnId, isReceived) -> {
            ReturnReceivedRequest returnReceivedRequest = new ReturnReceivedRequest();
            returnReceivedRequest.setReturnId(returnId);
            Boolean hasSetSatisfaction = ReturnDetailsController.this.hasSetSatisfaction.get(returnId);
            if (hasSetSatisfaction == null) {
                return;
            }
            if (isReceived) {
                if (hasSetSatisfaction) {
                    returnReceivedRequest.setSatisfaction(null);
                    mPresenter.callSetReturnReceived(returnReceivedRequest);
                } else {
                    mActivity.showReturnSatisfactionDialog(response -> {
                        ReturnDetailsController.this.hasSetSatisfaction.put(returnId, true);
                        switch (response) {
                            case BottomSheetReturnSatisfactionDialog.POSITIVE_RESPONSE:
                                returnReceivedRequest.setSatisfaction(ReturnReceivedSatisfactionValue.GOOD.getValue());
                                break;
                            case BottomSheetReturnSatisfactionDialog.NEGATIVE_RESPONSE:
                                returnReceivedRequest.setSatisfaction(ReturnReceivedSatisfactionValue.BAD.getValue());
                                break;
                            default:
                                returnReceivedRequest.setSatisfaction(ReturnReceivedSatisfactionValue.NEUTRAL.getValue());
                                break;
                        }
                        mPresenter.callSetReturnReceived(returnReceivedRequest);
                    });
                }
            } else {
                mPresenter.callSetReturnNotReceived(returnReceivedRequest);
            }
        });

        mImageFileHashMap.clear();

        mReturnDetailsSubtotal.setText(PriceUtils.getPriceStringValue(item.getTotalAmount()));
        mReturnDetailsControllerCreditCardRefundsValue.setText(item.getCreditAmount());
        mReturnDetailsControllerStoreCreditsValue.setText(item.getCreditAmount());
        mReturnDetailsControllerReturnAmountInfoButton.setVisibility(View.GONE);

        final ReturnDetailsAdapter adapter = new ReturnDetailsAdapter(item.getItems());
        LinearLayoutManager itemsLayoutManager = new LinearLayoutManager(mActivity);
        mReturnDetailsControllerItemList.setAdapter(adapter);
        mReturnDetailsControllerItemList.setLayoutManager(itemsLayoutManager);

        mReturnDetailsContactContainer.setVisibility(item.getContactNumber() != 0 ?
                View.VISIBLE : View.GONE);

        if (isFromOrders || item.getContactNumber() != 0) {
            String contactNumber = Integer.toString(item.getContactNumber());
            String concatenateContactNo = mActivity.getResources().getString(R.string.contact_number_return_details) + " " + contactNumber;
            mContactNumberText.setText(concatenateContactNo);
            GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
            getContactHistoryRequest.setNumber(item.getContactNumber());
            mPresenter.loadReturnContacts(getContactHistoryRequest);
        }

        mReturnDetailsReasonText.setText(item.getReason());

        final int attachmentSize = item.getAttachments().size();

        if (mImageUriArray.size() == 0 && attachmentSize != 0) {
            for (int i = 0; i < attachmentSize; i++) {
                ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
                imageLinks.setIsURL(true);
                imageLinks.setLink(item.getAttachments().get(i).getUrl());
                mImageUriArray.add(imageLinks);
            }
        }

        ReturnDetailsAddImageAdapter returnDetailsImageAdapter = new ReturnDetailsAddImageAdapter(mImageUriArray, returnDetailsListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true);
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
                    0,
                    true))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        });
    }

    @Override
    public void refreshReturnDetails(String setAttachmentResponse) {
        shouldUploadImage = false;

        if (isBackButtonPressed) {
            mActivity.onBackPressed();
        } else {
            if (attachmentId == null) {
                attachmentId = setAttachmentResponse;
            }
            mPresenter.loadCurrentReturnDetails(mReturnId);
        }
    }

    @Override
    public void getImageUrl(String imageUrl) {

        ImageAttachment items = new ImageAttachment();
        items.setType("image/jpeg");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size()) {
            shouldUploadImage = false;
            mPresenter.setAttachment(mReturnId, itemsList);
        }
    }

    @Override
    public void finishedSendMessage(String response) {
        if (shouldUploadImage) {
            uploadImages();
        }
        mReturnDetailsWriteMessageEditText.getText().clear();
        mPresenter.loadCurrentReturnDetails(mReturnId);/**/

        if (!response.isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.message_submitted));
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    "Message not sent");
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((resultCode == RESULT_OK || resultCode == AppConstants.REQUEST_CODE_FOR_SUCCESS) && data != null) {
            Uri chosenImageUri = data.getData();

            ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
            imageLinks.setIsURL(false);
            imageLinks.setLink(String.valueOf(chosenImageUri));
            final int previousSize = mImageUriArray.size();
            mImageUriArray.add(imageLinks);

            if (hasSavedInstance) {
                mPresenter.loadCurrentReturnDetails(mReturnId);
            } else {

                ReturnDetailsAddImageAdapter adapter = (ReturnDetailsAddImageAdapter) mReturnDetailsImageList.getAdapter();
                if (adapter != null) {
                    adapter.notifyItemInserted(mImageUriArray.size() - 1);
                    if (previousSize < mImageUriArray.size() && mImageUriArray.size() == AppConstants.MAX_IMAGE_COUNT) {
                        adapter.notifyItemRemoved(mImageUriArray.size());
                    }
                } else {
                    ReturnDetailsAddImageAdapter imageAdapter = new ReturnDetailsAddImageAdapter(mImageUriArray, returnDetailsListener);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true);
                    mReturnDetailsImageList.setAdapter(imageAdapter);
                    mReturnDetailsImageList.setLayoutManager(layoutManager);
                }
            }

            if (attachmentId == null) {
                mPresenter.setAttachment(mReturnId, new ArrayList<>());
            }

        }
    }

    @OnClick(R.id.controller_return_details_message_button)
    public void onClick() {
        if (shouldUploadImage && mReturnDetailsWriteMessageEditText.getText().toString().isEmpty()) {
            uploadImages();
        } else {
            if (item.getContactNumber() == 0) {
                hideKeyboard();
                CreateContactRequest createContactRequest = new CreateContactRequest();

                createContactRequest.setInvoiceNumber(item.getInvoiceNumber());

                createContactRequest.setSubject(mActivity.getResources().getString(R.string.returns_enquiry));

                if (mReturnDetailsWriteMessageEditText.getText().toString().isEmpty()) {
                    CustomAlertDialog.showCustomAlertDialog(mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getString(R.string.create_contact_fill_up));
                } else {
                    createContactRequest.setText(mReturnDetailsWriteMessageEditText.getText().toString());
                    mPresenter.sendMessage(mReturnId, createContactRequest);

                }
            } else {

                hideKeyboard();

                String replyMessage = mReturnDetailsWriteMessageEditText.getText().toString();

                ReplyContactRequest replyContactRequest = new ReplyContactRequest();
                replyContactRequest.setText(replyMessage);
                replyContactRequest.setNumber(item.getContactNumber());

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

    private void callUploadImage(int imageCount) {
        if (mImageFileHashMap.get(imageCount) == null) {
            if (imageCount < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imageCount + 1);
            }
            return;
        }
        uploadFileToServer = new ImageUploadUtil.UploadFileToServer(mActivity, true);
        uploadFileToServer.delegate = (imageUrl, imagePosition) -> {
            if (imageUrl != null) {
                getImageUrl(ImageUploadUtil.convertStringUrltoJSON(imageUrl));
            }

            if (imagePosition + 1 < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imagePosition + 1);
            }
        };
        uploadFileToServer.execute(attachmentId,
                mImageFileHashMap.get(imageCount), mPresenter.getUserAgent(), imageCount,
                GNotification.getDeviceID(mActivity) + System.currentTimeMillis() + ".jpg");
    }

    private void scrollToMessage() {
        int locationOfScrollView[] = new int[2];
        mReturnDetailsScrollView.getLocationInWindow(locationOfScrollView);
        int locationOfButton[] = new int[2];
        mReturnDetailsButton.getLocationInWindow(locationOfButton);
        mReturnDetailsScrollView.scrollTo(0, locationOfButton[1] - locationOfScrollView[1]);
    }

    @Override
    public void returnSatisfactionReceived(ReturnReceivedRequest request, boolean hasSetSatisfactionAlready) {
        hasSetSatisfaction.put(request.getReturnId(), hasSetSatisfactionAlready);
    }
}
