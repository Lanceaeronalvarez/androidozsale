package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Message;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.setattachmentforcontact.SetAttachmentForContactRequest;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryOnClickRatingListener;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AsyncResponse;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * dp Created by Admin on 6/21/17.
 */

public class ViewContactHistoryController extends BaseController implements ViewContactHistoryMvpView,
        ReturnDetailsListener, AsyncResponse {

    public static final String TAG = "ViewContactHistoryController";
    private static final String KEY_CONTACT_NO = "ContactHistoryNo";
    private static final String KEY_CONTACT_INVOICE_NO = "ContactHistoryInvoiceNo";
    private static final String KEY_CONTACT_TIMESTAMP = "ContactHistoryTimeStamp";
    private static final String KEY_CONTACT_SUBJECT = "ContactSubject";
    private static final String KEY_IS_FROM_RETURN_DETAILS = "KEY_IS_FROM_RETURN_DETAILS";

    @BindView(R.id.contact_history_recycler_view)
    RecyclerView mContactHistoryRecyclerView;

    @BindView(R.id.partial_toolbar_details_subtitle_textview)
    TextView mContactHistorySaleSubTitle;

    @BindView(R.id.partial_toolbar_field_title_right_option)
    ImageView mContactHistoryRightOption;

    @BindView(R.id.partial_toolbar_details_upper_title_textview)
    TextView mContactHistoryTitle;

    @BindView(R.id.controller_view_contacts_history_message_field)
    EditText mContactHistoryMessageField;

    @BindView(R.id.view_contact_history_image_recyclerview)
    RecyclerView mImageRecyclerView;

    private String mTimeStamp;
    private int mInvoiceNumber;
    private int mContactNumber;
    private String mContactSubject;
    private boolean mHasSavedInstance = false;
    private boolean isFromReturnDetails = false;
    private ViewContactsAddImageAdapter mImageAdapter;
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private ArrayList<SetAttachmentForContactRequest.Item> itemsList = new ArrayList<>();
    private String mAttachmentId = "";
    private String mMessageId = "";
    ImageUploadUtil.UploadFileToServer uploadFileToServer;
    private LinearLayoutManager mLayoutManager;
    private final ContactHistoryOnClickRatingListener contactHistoryOnClickRatingListener = new ContactHistoryOnClickRatingListener() {
        @Override
        public void onClickSmile() {
            mPresenter.closeTicketSatisfaction(AppConstants.SMILE_ICON, Integer.toString(mContactNumber));
        }

        @Override
        public void onClickNeutral() {
            mPresenter.closeTicketSatisfaction(AppConstants.NEUTRAL_ICON, Integer.toString(mContactNumber));
        }

        @Override
        public void onClickSad() {
            mPresenter.closeTicketSatisfaction(AppConstants.SAD_ICON, Integer.toString(mContactNumber));
        }
    };

    @Inject
    ViewContactHistoryPresenter<ViewContactHistoryMvpView> mPresenter;

    public static ViewContactHistoryController newInstance(
            String contactSubject,
            int invoiceNo,
            String lastAnswer,
            int contactNo,
            boolean fromReturnDetails) {

        return new ViewContactHistoryController(
                new BundleBuilder(new Bundle())
                        .putInt(KEY_CONTACT_NO, contactNo)
                        .putInt(KEY_CONTACT_INVOICE_NO, invoiceNo)
                        .putString(KEY_CONTACT_TIMESTAMP, lastAnswer)
                        .putString(KEY_CONTACT_SUBJECT, contactSubject)
                        .putBoolean(KEY_IS_FROM_RETURN_DETAILS, fromReturnDetails)
                        .build());
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
        mInvoiceNumber = getArgs().getInt(KEY_CONTACT_INVOICE_NO);
        mTimeStamp = getArgs().getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = getArgs().getInt(KEY_CONTACT_NO);
        mContactSubject = getArgs().getString(KEY_CONTACT_SUBJECT);
        isFromReturnDetails = getArgs().getBoolean(KEY_IS_FROM_RETURN_DETAILS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTACT_INVOICE_NO, mInvoiceNumber);
        outState.putString(KEY_CONTACT_TIMESTAMP, mTimeStamp);
        outState.putInt(KEY_CONTACT_NO, mContactNumber);
        outState.putString(KEY_CONTACT_SUBJECT, mContactSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInvoiceNumber = savedInstanceState.getInt(KEY_CONTACT_INVOICE_NO);
        mTimeStamp = savedInstanceState.getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = savedInstanceState.getInt(KEY_CONTACT_NO);
        mContactSubject = savedInstanceState.getString(KEY_CONTACT_SUBJECT);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_view_contact_history, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setUp(View view) {

        KeyboardUtils.setKeyboardAdjustResize(mActivity);

        mContactHistoryRightOption.setVisibility(View.INVISIBLE);
        mContactHistoryTitle.setText(mContactSubject);

        if (mInvoiceNumber > 0) {
            mContactHistorySaleSubTitle.setText(Integer.toString(mInvoiceNumber));
            mContactHistorySaleSubTitle.setVisibility(View.VISIBLE);
        } else {
            mContactHistorySaleSubTitle.setVisibility(View.GONE);
        }

        mImageAdapter = new ViewContactsAddImageAdapter(mActivity, this,
                mImageUriArray);
        mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mLayoutManager);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        mContactHistoryRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        KeyboardUtils.setKeyboardAdjustPan(mActivity);
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
    }

    @Override
    public void showContactHistory(GetContactHistoryResponse response) {
        setupMessagesAdapter(response.getMessages());

        if (mImageUriArray.size() != 0) {
            mMessageId = response.getMessages().get(0).getId();

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
            setAttachmentRequest.setNumber(mContactNumber);
            setAttachmentRequest.setItems(new ArrayList<>());
            mPresenter.setAttachment(setAttachmentRequest, false);
        } else {
            mPresenter.getTicketSatisfaction(String.valueOf(mContactNumber));
        }


    }

    @OnClick(R.id.partial_toolbar_field_title_left_option)
    void onBackClick() {
        mActivity.onBackPressed();
    }

    @OnClick(R.id.controller_view_contacts_history_reply_button)
    void onReplyClick() {

        KeyboardUtils.hideSoftInput(mActivity);

        String replyMessage = mContactHistoryMessageField.getText().toString();
        int contactId = mContactNumber;

        ReplyContactRequest replyContactRequest = new ReplyContactRequest();
        replyContactRequest.setText(replyMessage);
        replyContactRequest.setNumber(contactId);

        if (replyContactRequest.getText().isEmpty()) {

            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                    mActivity.getString(R.string.create_contact_fill_up)
            );

        } else {
            mPresenter.replyContact(replyContactRequest);
        }
    }

    @OnClick(R.id.controller_view_contacts_history_attach_photo)
    void onAttachImage() {

        if (mImageFileHashMap.size() < AppConstants.MAX_IMAGE_COUNT && mImageUriArray.size() < AppConstants.MAX_IMAGE_COUNT) {
            getImageFromDirectory(true);
        }
    }

    @Override
    public void repliedContactSwitchView(String replyContact) {
        if (replyContact.equals("true")) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.message_submitted));
            mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
            mContactHistoryMessageField.setText("");
        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity, CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    mActivity.getString(R.string.error_creating_message));
        }
    }

    @Override
    public void refreshViewContactMessage() {
        mImageFileHashMap.clear();
        itemsList.clear();
        mImageUriArray.clear();
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageAdapter.notifyDataSetChanged();

        mImageRecyclerView.setVisibility(View.GONE);
        mPresenter.loadContactHistory(createContactHistoryRequest(getArgs().getInt(KEY_CONTACT_NO)));
    }

    @Override
    public void setAttachmentId(String attachmentId) {
        mAttachmentId = attachmentId;

        if (mImageFileHashMap != null) {
            callUploadImage(0);
        }
    }

    @Override
    public void showTicketSatisfaction(boolean hasTicketSatisfaction) {
        setupMessagesAdapter(hasTicketSatisfaction);
    }

    private void setupMessagesAdapter(List<Message> messages) {
        setupMessagesAdapter(messages, null);
    }

    private void setupMessagesAdapter(boolean hasSatisfactionRating) {
        setupMessagesAdapter(null, hasSatisfactionRating);
    }

    private void setupMessagesAdapter(List<Message> messages, Boolean hasSatisfactionRating) {
        ContactHistoryAdapter adapter;
        if (mContactHistoryRecyclerView.getAdapter() instanceof ContactHistoryAdapter) {
            adapter = (ContactHistoryAdapter) mContactHistoryRecyclerView.getAdapter();

            final List<Message> oldMessages = adapter.getMessages();
            final boolean oldHasSatisfactionRating = adapter.hasRating();

            boolean willRefreshFooter = false;

            if (hasSatisfactionRating != null) {
                adapter.setHasRating(hasSatisfactionRating);
                if (oldHasSatisfactionRating != hasSatisfactionRating) {
                    willRefreshFooter = true;
                }
            }

            if (messages != null) {
                final boolean isLastMessageFromStaff = messages.get(0).isStaff();
                final boolean isLastOldMessageFromStaff = oldMessages.isEmpty() ? false : oldMessages.get(0).isStaff();
                if (messages.size() > oldMessages.size()) {
                    int index = 0;
                    while (!messages.get(index).getId().equals(oldMessages.get(0).getId())) {
                        index += 1;
                    }

                    adapter.setMessages(messages);
                    if (index > 0) {
                        adapter.notifyItemRangeInserted(adapter.getStartIndexOfMessages(), index);
                    }

                    if (isLastMessageFromStaff != isLastOldMessageFromStaff) {
                        willRefreshFooter = true;
                    }
                } else {
                    willRefreshFooter = false;
                    adapter.notifyDataSetChanged();
                }
            }

            if (willRefreshFooter) {
                adapter.notifyItemChanged(adapter.getFooterIndex());
            }

            mContactHistoryRecyclerView.smoothScrollToPosition(0);
        } else {
            adapter = new ContactHistoryAdapter(
                    messages != null ? messages : new ArrayList<>(),
                    hasSatisfactionRating != null ? hasSatisfactionRating : true,
                    contactHistoryOnClickRatingListener);

            mContactHistoryRecyclerView.setAdapter(adapter);
            mContactHistoryRecyclerView.scrollToPosition(0);
        }
    }

    private GetContactHistoryRequest createContactHistoryRequest(int number) {
        GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
        getContactHistoryRequest.setNumber(number);
        return getContactHistoryRequest;
    }

    @Override
    public void getImageFromDirectory(boolean uploadImage) {
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
        ViewContactsAddImageAdapter adapter = ((ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter());
        mImageUriArray.remove(position);
        if (adapter != null) {
            adapter.removeItem(position);
        }

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

    private void getImageUrl(String imageUrl) {
        SetAttachmentForContactRequest.Item items = new SetAttachmentForContactRequest.Item();
        items.setType("image/png");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size()) {
            SetAttachmentForContactRequest setAttachmentRequest = new SetAttachmentForContactRequest();
            setAttachmentRequest.setMessageId(mMessageId);
            setAttachmentRequest.setNumber(mContactNumber);
            setAttachmentRequest.setItems(itemsList);
            mPresenter.setAttachment(setAttachmentRequest, true);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == AppConstants.REQUEST_CODE_FOR_SUCCESS) {
            Uri chosenImageUri = data.getData();

            mImageRecyclerView.setVisibility(View.VISIBLE);

            ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
            imageLinks.setIsURL(false);
            imageLinks.setLink(String.valueOf(chosenImageUri));
            mImageUriArray.add(0, imageLinks);

            ((ViewContactsAddImageAdapter) Objects.requireNonNull(mImageRecyclerView.getAdapter())).addItem();

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
}
