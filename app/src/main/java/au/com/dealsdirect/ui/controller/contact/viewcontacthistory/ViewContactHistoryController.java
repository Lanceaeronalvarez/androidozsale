package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

import android.annotation.SuppressLint;
import android.app.Activity;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse.Escalate;
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
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;

public class ViewContactHistoryController extends BaseController implements ViewContactHistoryMvpView,
        ReturnDetailsListener, AsyncResponse {

    public static final String TAG = "ViewContactHistoryController";
    private static final String KEY_CONTACT_NO = "ContactHistoryNo";
    private static final String KEY_CONTACT_INVOICE_NO = "ContactHistoryInvoiceNo";
    private static final String KEY_CONTACT_TIMESTAMP = "ContactHistoryTimeStamp";
    private static final String KEY_CONTACT_SUBJECT = "ContactSubject";
    private static final String KEY_IS_FROM_RETURN_DETAILS = "KEY_IS_FROM_RETURN_DETAILS";
    private static final String ESCALATE_ACTION_KEY = "o_escalate";

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
    private Integer mInvoiceNumber;
    private Integer mContactNumber;
    private String mContactSubject;
    private boolean mHasSavedInstance = false;
    private boolean isFromReturnDetails = false;
    private ViewContactsAddImageAdapter mImageAdapter;
    private final ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();
    private final HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private final ArrayList<SetAttachmentForContactRequest.Item> itemsList = new ArrayList<>();
    private String mAttachmentId = "";
    private String mMessageId = "";
    private ImageUploadUtil.UploadFileToServer uploadFileToServer;

    private ContactHistoryAdapter contactHistoryAdapter = null;

    private final ContactHistoryOnClickRatingListener contactHistoryOnClickRatingListener = new ContactHistoryOnClickRatingListener() {
        @Override
        public void onClickSmile() {
            String contactNumber = mContactNumber != null ? Integer.toString(mContactNumber) : "0";
            mPresenter.closeTicketSatisfaction(AppConstants.SMILE_ICON, contactNumber);
        }

        @Override
        public void onClickNeutral() {
            String contactNumber = mContactNumber != null ? Integer.toString(mContactNumber) : "0";
            mPresenter.closeTicketSatisfaction(AppConstants.NEUTRAL_ICON, contactNumber);
        }

        @Override
        public void onClickSad() {
            String contactNumber = mContactNumber != null ? Integer.toString(mContactNumber) : "0";
            mPresenter.closeTicketSatisfaction(AppConstants.SAD_ICON, contactNumber);
        }
    };

    @Inject
    ViewContactHistoryPresenter<ViewContactHistoryMvpView> mPresenter;

    public static ViewContactHistoryController newInstance(
            String contactSubject,
            Integer invoiceNo,
            String lastAnswer,
            Integer contactNo,
            boolean fromReturnDetails) {

        Bundle bundle = new Bundle();
        if (contactNo != null) {
            bundle.putInt(KEY_CONTACT_NO, contactNo);
        }
        if (invoiceNo != null) {
            bundle.putInt(KEY_CONTACT_INVOICE_NO, invoiceNo);
        }
        bundle.putString(KEY_CONTACT_TIMESTAMP, lastAnswer);
        bundle.putString(KEY_CONTACT_SUBJECT, contactSubject);
        bundle.putBoolean(KEY_IS_FROM_RETURN_DETAILS, fromReturnDetails);

        return new ViewContactHistoryController(bundle);
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
        mInvoiceNumber = getArgs().getInt(KEY_CONTACT_INVOICE_NO, -1);
        if (mInvoiceNumber == -1) {
            mInvoiceNumber = null;
        }
        mTimeStamp = getArgs().getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = getArgs().getInt(KEY_CONTACT_NO, -1);
        if (mContactNumber == -1) {
            mContactNumber = null;
        }
        mContactSubject = getArgs().getString(KEY_CONTACT_SUBJECT);
        isFromReturnDetails = getArgs().getBoolean(KEY_IS_FROM_RETURN_DETAILS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mInvoiceNumber != null) {
            outState.putInt(KEY_CONTACT_INVOICE_NO, mInvoiceNumber);
        }
        outState.putString(KEY_CONTACT_TIMESTAMP, mTimeStamp);
        if (mContactNumber != null) {
            outState.putInt(KEY_CONTACT_NO, mContactNumber);
        }
        outState.putString(KEY_CONTACT_SUBJECT, mContactSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mInvoiceNumber = savedInstanceState.getInt(KEY_CONTACT_INVOICE_NO, -1);
        if (mInvoiceNumber == -1) {
            mInvoiceNumber = null;
        }
        mTimeStamp = savedInstanceState.getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = savedInstanceState.getInt(KEY_CONTACT_NO, -1);
        if (mContactNumber == -1) {
            mContactNumber = null;
        }
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
        mPresenter.loadContactHistory(createContactHistoryRequest(mContactNumber != null ? mContactNumber : 0));
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void setUp(View view) {

        KeyboardUtils.setKeyboardAdjustResize(mActivity);

        mContactHistoryRightOption.setVisibility(View.INVISIBLE);
        mContactHistoryTitle.setText(mContactSubject);

        if (mInvoiceNumber != null) {
            mContactHistorySaleSubTitle.setText(Integer.toString(mInvoiceNumber));
            mContactHistorySaleSubTitle.setVisibility(View.VISIBLE);
        } else {
            mContactHistorySaleSubTitle.setVisibility(View.GONE);
        }

        mImageAdapter = new ViewContactsAddImageAdapter(mActivity, this,
                mImageUriArray);
        LinearLayoutManager imageLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(imageLayoutManager);

        LinearLayoutManager contactHistoryLayoutManager = new LinearLayoutManager(mActivity);
        contactHistoryLayoutManager.setStackFromEnd(true);
        contactHistoryLayoutManager.setReverseLayout(true);
        mContactHistoryRecyclerView.setLayoutManager(contactHistoryLayoutManager);
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
        setupMessagesAdapter(response.getEscalate(), response.getActions());

        if (!mImageUriArray.isEmpty()) {
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
            setAttachmentRequest.setNumber(mContactNumber != null ? mContactNumber : 0);
            setAttachmentRequest.setItems(new ArrayList<>());
            mPresenter.setAttachment(setAttachmentRequest, false);
        } else {
            String contactNumber = mContactNumber != null ? Integer.toString(mContactNumber) : "0";
            mPresenter.getTicketSatisfaction(String.valueOf(contactNumber));
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
        int contactId = mContactNumber != null ? mContactNumber : 0;

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
            mPresenter.loadContactHistory(createContactHistoryRequest(mContactNumber != null ? mContactNumber : 0));
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
        mPresenter.loadContactHistory(createContactHistoryRequest(mContactNumber != null ? mContactNumber : 0));
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

    private void setupMessagesAdapter(Escalate escalate, List<String> actions) {
        if (contactHistoryAdapter != null) {
            if (actions.contains(ESCALATE_ACTION_KEY)) {
                contactHistoryAdapter.setEscalateAction(() -> mPresenter.escalateContact(createContactHistoryRequest(mContactNumber != null ? mContactNumber : 0)));
            } else {
                contactHistoryAdapter.setEscalateAction(null);
            }
            contactHistoryAdapter.setEscalate(escalate);
            contactHistoryAdapter.notifyItemChanged(contactHistoryAdapter.getAdjustedFirstPosition());
        }
    }

    private void setupMessagesAdapter(List<Message> messages, Boolean hasSatisfactionRating) {
        if (mContactHistoryRecyclerView.getAdapter() instanceof ContactHistoryAdapter) {
            contactHistoryAdapter = (ContactHistoryAdapter) mContactHistoryRecyclerView.getAdapter();

            final List<Message> oldMessages = contactHistoryAdapter.getMessages();
            final boolean oldHasSatisfactionRating = contactHistoryAdapter.hasRating();

            boolean willRefreshFooter = false;

            if (hasSatisfactionRating != null) {
                contactHistoryAdapter.setHasRating(hasSatisfactionRating);
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

                    contactHistoryAdapter.setMessages(messages);
                    if (index > 0) {
                        contactHistoryAdapter.notifyItemRangeInserted(contactHistoryAdapter.getStartIndexOfMessages(), index);
                    }

                    if (isLastMessageFromStaff != isLastOldMessageFromStaff) {
                        willRefreshFooter = true;
                    }
                } else {
                    willRefreshFooter = false;
                    contactHistoryAdapter.notifyDataSetChanged();
                }
            }

            if (willRefreshFooter) {
                contactHistoryAdapter.notifyItemChanged(contactHistoryAdapter.getFooterIndex());
            }

            mContactHistoryRecyclerView.smoothScrollToPosition(0);
        } else {
            contactHistoryAdapter = new ContactHistoryAdapter(
                    messages != null ? messages : new ArrayList<>(),
                    hasSatisfactionRating != null ? hasSatisfactionRating : true,
                    (type, url) -> mActivity.openAttachment(url),
                    contactHistoryOnClickRatingListener);

            mContactHistoryRecyclerView.setAdapter(contactHistoryAdapter);
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
        final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerActivityResultLauncher(TAG, new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri == null) {
                        return;
                    }

                    mImageRecyclerView.setVisibility(View.VISIBLE);

                    ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
                    imageLinks.setIsURL(false);
                    imageLinks.setLink(uri.toString());
                    mImageUriArray.add(0, imageLinks);

                    ViewContactsAddImageAdapter adapter = (ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter();
                    if (adapter != null) {
                        adapter.addItem();
                    }
                });

        if (pickMedia == null) {
            return;
        }
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    @Override
    public void removeImage(Bitmap image, int position, boolean uploadImage, boolean isAddImageAdapter) {
        ViewContactsAddImageAdapter adapter = ((ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter());
        mImageUriArray.remove(position);
        if (adapter != null) {
            adapter.removeItem(position);
        }

        if (mImageUriArray.isEmpty()) {
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
                ViewContactsAddImageAdapter adapter = (ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.showProgressBar(vh);
                }
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
            setAttachmentRequest.setNumber(mContactNumber != null ? mContactNumber : 0);
            setAttachmentRequest.setItems(itemsList);
            mPresenter.setAttachment(setAttachmentRequest, true);
        }
    }

    @Override
    public void asyncExecutionFinished(String imageUrl, int imagePosition) {
        if (imageUrl == null) {
            onError("Image may not be uploaded.");
        }
        getImageUrl(ImageUploadUtil.convertStringUrltoJSON(imageUrl));

        if (mImageRecyclerView != null) {
            ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder vh = (ViewContactsAddImageAdapter.ViewContactsAddImageViewHolder)
                    mImageRecyclerView.findViewHolderForLayoutPosition(imagePosition);

            if (vh != null) {
                ViewContactsAddImageAdapter adapter = (ViewContactsAddImageAdapter) mImageRecyclerView.getAdapter();
                if (adapter != null) {
                    adapter.hideVisibility(vh);
                }
            }
        }

        if (imagePosition + 1 < AppConstants.MAX_IMAGE_COUNT) {
            callUploadImage(imagePosition + 1);
        }
    }

    @Override
    public void escalateContactResult(boolean result) {
        mPresenter.loadContactHistory(createContactHistoryRequest(mContactNumber != null ? mContactNumber : 0));
    }
}
