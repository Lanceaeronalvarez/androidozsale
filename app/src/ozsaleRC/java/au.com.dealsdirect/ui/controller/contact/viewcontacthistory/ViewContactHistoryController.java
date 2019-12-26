package au.com.dealsdirect.ui.controller.contact.viewcontacthistory;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.List;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContact;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.contacthistory.ContactHistoryAdapter;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAddImageAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.AppLogger;
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
    private static final String KEY_CONTACT_NAME = "ContactHistoryName";
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

    private String mSaleNameObject;
    private String mTimeStamp;
    private int mInvoiceNumber;
    private int mContactNumber;
    private String mContactSubject;
    private boolean mHasSavedInstance = false;
    private boolean isFromReturnDetails = false;
    private ViewContactsAddImageAdapter mImageAdapter;
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private ArrayList<SetAttachmentRequest.Items> itemsList = new ArrayList<>();
    private String mAttachmentId = "";
    private String mMessageId = "";
    ImageUploadUtil.UploadFileToServer uploadFileToServer;
    private LinearLayoutManager mLayoutManager;

    @Inject
    ViewContactHistoryPresenter<ViewContactHistoryMvpView> mPresenter;

    public static ViewContactHistoryController newInstance(
            String contactSubject,
            String saleName,
            int invoiceNo,
            String lastAnswer,
            int contactNo,
            boolean fromReturnDetails) {

        return new ViewContactHistoryController(
                new BundleBuilder(new Bundle())
                        .putInt(KEY_CONTACT_NO, contactNo)
                        .putString(KEY_CONTACT_NAME, saleName)
                        .putInt(KEY_CONTACT_INVOICE_NO, invoiceNo)
                        .putString(KEY_CONTACT_TIMESTAMP, lastAnswer)
                        .putString(KEY_CONTACT_SUBJECT, contactSubject)
                        .putBoolean(KEY_IS_FROM_RETURN_DETAILS, fromReturnDetails)
                        .build());
    }

    public ViewContactHistoryController(Bundle args) {
        super(args);
        mSaleNameObject = getArgs().getString(KEY_CONTACT_NAME);
        mInvoiceNumber = getArgs().getInt(KEY_CONTACT_INVOICE_NO);
        mTimeStamp = getArgs().getString(KEY_CONTACT_TIMESTAMP);
        mContactNumber = getArgs().getInt(KEY_CONTACT_NO);
        mContactSubject = getArgs().getString(KEY_CONTACT_SUBJECT);
        isFromReturnDetails = getArgs().getBoolean(KEY_IS_FROM_RETURN_DETAILS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTACT_NAME, mSaleNameObject);
        outState.putInt(KEY_CONTACT_INVOICE_NO, mInvoiceNumber);
        outState.putString(KEY_CONTACT_TIMESTAMP, mTimeStamp);
        outState.putInt(KEY_CONTACT_NO, mContactNumber);
        outState.putString(KEY_CONTACT_SUBJECT, mContactSubject);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSaleNameObject = savedInstanceState.getString(KEY_CONTACT_NAME);
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

    @Override
    protected void setUp(View view) {

        KeyboardUtils.setKeyboardAdjustResize(mActivity);

        mContactHistoryRightOption.setVisibility(View.INVISIBLE);
        mContactHistoryTitle.setText(mContactSubject);

        if (!mSaleNameObject.isEmpty()) {
            mContactHistorySaleSubTitle.setText(mInvoiceNumber + ": " + mSaleNameObject);
        } else {
            mContactHistorySaleSubTitle.setText(R.string.no_order_number);
        }

        mImageAdapter = new ViewContactsAddImageAdapter(mActivity, this,
                mImageUriArray);
        mLayoutManager = new LinearLayoutManager(mActivity, RecyclerView.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(mImageAdapter);
        mImageRecyclerView.setLayoutManager(mLayoutManager);
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
    public void showContactHistory(java.util.List<List> myContactItems) {

        if (mImageUriArray.size() != 0) {
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
        } else {
            Log.d("contacts", myContactItems.size() + " ");
            ContactHistoryAdapter adapter = new ContactHistoryAdapter(myContactItems, mActivity);

            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);

            mContactHistoryRecyclerView.setAdapter(adapter);
            mContactHistoryRecyclerView.setLayoutManager(layoutManager);
            mContactHistoryRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
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
        replyContactRequest.comments = replyMessage;
        replyContactRequest.contactNo = contactId;

        if (replyContactRequest.comments.isEmpty()) {

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
    public void repliedContactSwitchView(ReplyContact replyContact) {
        if (replyContact.getResult()) {
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
    public void getAttachmentId(SetAttachmentResponse setAttachmentResponse) {
        mAttachmentId = setAttachmentResponse.getD().getValue();

        if (mImageFileHashMap != null) {
            callUploadImage(0);
        }
    }

    private GetContactHistoryRequest createContactHistoryRequest(int contactNo) {
        GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
        getContactHistoryRequest.contactNo = contactNo;
        return getContactHistoryRequest;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || resultCode == AppConstants.REQUEST_CODE_FOR_SUCCESS)
        {
            Uri chosenImageUri = data.getData();

            mImageRecyclerView.setVisibility(View.VISIBLE);

            ImageUtils.ImageLink imageLink = new ImageUtils.ImageLink(String.valueOf(chosenImageUri), false);
            mImageUriArray.add(0, imageLink);

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
