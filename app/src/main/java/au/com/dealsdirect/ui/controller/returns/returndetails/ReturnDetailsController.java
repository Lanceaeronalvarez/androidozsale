package au.com.dealsdirect.ui.controller.returns.returndetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import au.com.dealsdirect.utils.ActivityLaunchUtil;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.StringUtils;
import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.controller_return_details_progress_description_default)
    TextView progressDescriptionDefault;
    @BindView(R.id.controller_return_details_progress_description_container)
    ViewGroup progressDescriptionContainer;
    @BindView(R.id.controller_return_details_progress_description)
    WebView progressDescription;

    @Inject
    ReturnDetailsMvpPresenter<ReturnDetailsMvpView> mPresenter;
    private String mToolbarTitle = "";
    private String mReturnId = "";
    private boolean isFromOrders = false;
    private final ArrayList<ImageAttachment> itemsList = new ArrayList<>();
    private final HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private boolean shouldUploadImage = false;
    private boolean isBackButtonPressed = false;
    private boolean hasSavedInstance = false;
    private String userMessage = "";
    private String attachmentId = "";
    private boolean willScrollToMessage = false;
    private final ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();

    private boolean someImagesWereNotUploaded = false;

    private final HashMap<String, Boolean> hasSetSatisfaction = new HashMap<>();

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia = null;

    private final ReturnDetailsListener returnDetailsListener = new ReturnDetailsListener() {
        @Override
        public void getImageFromDirectory(boolean uploadImage) {
            ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                    registerActivityResultLauncher(TAG, new ActivityResultContracts.PickVisualMedia(), uri -> {
                        if (uri == null) {
                            return;
                        }

                        ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
                        imageLinks.setIsURL(false);
                        imageLinks.setLink(uri.toString());
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
                                initReturnDetailsImageAdapter();
                            }
                        }

                        if (attachmentId == null) {
                            mPresenter.setAttachment(mReturnId, new ArrayList<>());
                        }
                    });

            if (pickMedia == null) {
                return;
            }

            shouldUploadImage = true;

            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        }

        @Override
        public void removeImage(Bitmap bitmap, int position, boolean uploadImage, boolean isAddImageAdapter) {
            shouldUploadImage = true;

            ReturnDetailsAddImageAdapter adapter = ((ReturnDetailsAddImageAdapter) mReturnDetailsImageList.getAdapter());

            mImageFileHashMap.remove(position);
            final int previousSize = mImageUriArray.size();
            mImageUriArray.remove(position);

            if (adapter != null) {
                adapter.notifyItemRemoved(position);
                if (previousSize == AppConstants.MAX_IMAGE_COUNT) {
                    adapter.notifyItemInserted(mImageUriArray.size());
                }
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
        outState.putString(BundleKeys.KEY_USER_MESSAGE, userMessage);
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

        if (item != null) {
            mReturnId = item.getId();
            mToolbarTitle = "Invoice " + item.getInvoiceNumber();
        } else {
            mReturnId = "";
            mToolbarTitle = "";
        }
        mPresenter.loadCurrentReturnDetails(mReturnId);

        mReturnDetailsControllerToolbarRightOption.setVisibility(View.INVISIBLE);
        mReturnDetailsControllerToolbarTitle.setText(mToolbarTitle);

        if (hasSavedInstance) {
            mReturnDetailsWriteMessageEditText.setText(userMessage);
        }
        mReturnDetailsWriteMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userMessage = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        progressDescription.getSettings().setTextZoom(100);
        progressDescription.getSettings().setJavaScriptEnabled(true);
        progressDescription.getSettings().setDomStorageEnabled(true);

        progressDescription.setWebViewClient(new WebViewClient() {

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                ActivityLaunchUtil.launchActivity(mActivity, url);
                return true;
            }

        });

        showCurrentReturnDetails(item);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showCurrentReturnDetails(CurrentReturn item) {
        if (item == null) {
            return;
        }

        ReturnReceivedRequest request = new ReturnReceivedRequest();
        request.setReturnId(item.getId());
        mPresenter.callGetReturnReceivedSatisfaction(request);

        if (willScrollToMessage && mReturnDetailsScrollView.isLaidOut()) {
            willScrollToMessage = false;
            scrollToMessage();
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

        for (Step step : steps) {
            if (step.getStatus().equalsIgnoreCase("success")) {
                setupProgressDescription(step.getText());
            }
        }

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

        if (mImageUriArray.isEmpty() && attachmentSize != 0) {
            for (int i = 0; i < attachmentSize; i++) {
                ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
                imageLinks.setIsURL(true);
                imageLinks.setLink(item.getAttachments().get(i).getUrl());
                mImageUriArray.add(imageLinks);
            }
        }

        initReturnDetailsImageAdapter();
    }

    private void initReturnDetailsImageAdapter() {
        ReturnDetailsAddImageAdapter returnDetailsImageAdapter = new ReturnDetailsAddImageAdapter(mImageUriArray, returnDetailsListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true);
        mReturnDetailsImageList.setAdapter(returnDetailsImageAdapter);
        mReturnDetailsImageList.setLayoutManager(layoutManager);
    }

    public void setupProgressDescription(String sourceText) {
        if (sourceText == null || sourceText.isEmpty()) {
            progressDescriptionDefault.setVisibility(View.VISIBLE);
            progressDescriptionContainer.setVisibility(View.GONE);
            return;
        }
        progressDescriptionDefault.setVisibility(View.GONE);
        progressDescriptionContainer.setVisibility(View.VISIBLE);

        final String htmlHeader = StringUtils.applyStyleToCSS(new StringUtils.CSSStyle() {
            @Override
            public String getBodyFontName() {
                return StringUtils.typeFaceFamilyFromFilename(
                        mActivity.getResources().getString(R.string.font_app_light));
            }

            @Override
            public String getBodyFontColor() {
                String hex = Integer.toHexString(
                        mActivity.getResources().getColor(R.color.text_medium));
                if (hex.length() > 6) {
                    hex = hex.substring(2);
                }
                return "#" + hex;
            }

            @Override
            public String getBoldFontName() {
                return StringUtils.typeFaceFamilyFromFilename(
                        mActivity.getResources().getString(R.string.font_app_regular));
            }

            @Override
            public String getBoldFontColor() {
                String hex = Integer.toHexString(
                        mActivity.getResources().getColor(R.color.text_medium));
                if (hex.length() > 6) {
                    hex = hex.substring(2);
                }
                return "#" + hex;
            }
        }, mActivity.getResources()
                .getString(R.string.base_html_template_header));

        final String htmlFooter = mActivity.getResources()
                .getString(R.string.base_html_template_footer);

        progressDescription.loadDataWithBaseURL(null, htmlHeader + sourceText + htmlFooter,
                "text/html", "UTF-8", null);
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
                            null,
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

    @OnClick(R.id.controller_return_details_message_button)
    public void onClick() {
        if (item == null) {
            return;
        }
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
            someImagesWereNotUploaded = false;
            callUploadImage(0);
        }
    }

    private void callUploadImage(int imageCount) {
        if (mImageFileHashMap.get(imageCount) == null) {
            if (imageCount < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imageCount + 1);
            } else if (someImagesWereNotUploaded) {
                showDialogSomeImagesWereNotUploaded();
            } else if (isBackButtonPressed) {
                mActivity.onBackPressed();
            }
            return;
        }
        uploadFileToServer = new ImageUploadUtil.UploadFileToServer(new ImageUploadUtil.UploadFileToServer.ProgressIndicatorProvider() {
            @Override
            public void showProgressIndicator() {
                if (mActivity != null) {
                    mActivity.showLoading(LoadingDialogType.NOLOGO);
                }
            }

            @Override
            public void hideProgressIndicator() {
                if (mActivity != null) {
                    mActivity.hideLoading();
                }
            }
        });
        uploadFileToServer.delegate = (imageUrl, imagePosition) -> {
            if (imageUrl != null) {
                try {
                    getImageUrl(ImageUploadUtil.convertStringUrltoJSON(imageUrl));
                } catch (Exception e) {
                    someImagesWereNotUploaded = true;
                }
            } else {
                someImagesWereNotUploaded = true;
            }

            if (imagePosition + 1 < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imagePosition + 1);
            } else if (someImagesWereNotUploaded) {
                showDialogSomeImagesWereNotUploaded();
            } else if (isBackButtonPressed) {
                mActivity.onBackPressed();
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

    private void showDialogSomeImagesWereNotUploaded() {
        final DialogInterface.OnClickListener onClickListener = (dialogInterface, i) -> {
            if (isBackButtonPressed) {
                mActivity.onBackPressed();
            }
        };
        new AlertDialog.Builder(mActivity)
                .setMessage("Some images were not uploaded or attached.")
                .setNeutralButton("Okay", onClickListener)
                .show();
    }
}
