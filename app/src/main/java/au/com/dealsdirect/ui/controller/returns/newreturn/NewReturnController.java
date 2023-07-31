package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.List;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.returns.newreturn.adapter.NewReturnOrdersAdapter;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsController;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAddImageAdapter;
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
import butterknife.OnFocusChange;

import static android.app.Activity.RESULT_OK;

/*
 * Created by Ayi on 05/06/2017.
 */

public class NewReturnController extends BaseController implements NewReturnMvpView,
        ReturnDetailsListener, AsyncResponse {

    public static final String TAG = "NewReturnController";
    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";
    private static final String KEY_INVOICE_NUMBER = "NewReturnController.KEY_INVOICE_NUMBER";
    private static final String KEY_IS_FROM_ORDER = "NewReturnController.KEY_IS_FROM_ORDER";
    private static final String KEY_PRODUCT_ID = "NewReturnController.KEY_PRODUCT_ID";

    private static List mReturnItem;

    @BindView(R.id.partial_toolbar_title)
    TextView mNewReturnToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mNewReturnToolbarRightOption;

    @BindView(R.id.controller_new_return_field)
    EditText mNewReturnCreateReasonField;

    @BindView(R.id.new_returns_set_detail_recyclerview)
    RecyclerView mNewReturnOrderRecyclerView;

    @BindView(R.id.new_return_create_button)
    ImageButton mNewReturnOrderRequestButton;

    @BindView(R.id.new_return_image_recyclerview)
    RecyclerView mImageRecyclerView;

    @BindView(R.id.new_return_confirm_button)
    Button mConfirmButton;

    @BindView(R.id.new_return_reason_edittext)
    EditText mReasonEditText;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, java.util.List> updateList = new HashMap<>();

    private NewReturnOrdersAdapter mAdapter = null;
    private HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private ArrayList<SetAttachmentRequest.Items> itemsList = new ArrayList<>();
    private String mProductName = "";
    private String mReasonReturnText;

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;
    private boolean mHasSavedInstance = false;
    private int mSavedInvoiceNumber;
    private static int mInvoiceNumber;
    private static boolean isFromOrder = false;
    private static String mProductID = "";
    private static String mReturnId;
    private String mAttachmentId = "";
    ImageUploadUtil.UploadFileToServer uploadFileToServer;
    File imageFile;
    private ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();

    public static NewReturnController newInstance(List returnItem) {

        mReturnItem = returnItem;
        return new NewReturnController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public static NewReturnController newInstance(int invoiceNumber, boolean calledFromOrder, String productId) {

        mInvoiceNumber = invoiceNumber;
        isFromOrder = calledFromOrder;
        mProductID = productId;
        return new NewReturnController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public NewReturnController(Bundle args) {
        super(args);

        if (args.containsKey(KEY_INVOICE_NUMBER)) {
            mInvoiceNumber = args.getInt(KEY_INVOICE_NUMBER);
        }

        if (args.containsKey(KEY_IS_FROM_ORDER)) {
            isFromOrder = args.getBoolean(KEY_IS_FROM_ORDER);
        }

        if (args.containsKey(KEY_PRODUCT_ID)) {
            mProductID = args.getString(KEY_PRODUCT_ID);
        }
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_new_return, container, false);
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

        mNewReturnToolbarTitle.setText(R.string.request_new_return);
        mNewReturnToolbarRightOption.setVisibility(View.INVISIBLE);
        mNewReturnToolbarRightOption.setImageDrawable(getDrawable(R.drawable.ic_check));

        showLoading();

        addImageReturn();

        if (mHasSavedInstance) {
            mPresenter.getReturnOrderDetail(mSavedInvoiceNumber);
            mReasonEditText.setText(mReasonReturnText);
        } else {
            mPresenter.getReturnOrderDetail(isFromOrder ? mInvoiceNumber : mReturnItem.getInvoiceNo());
        }



    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
        if (mReturnItem != null) {
            outState.putInt(BundleKeys.KEY_INVOICE_NUMBER, mReturnItem.getInvoiceNo());
        }
        if (mReasonEditText != null && mReasonEditText.getText() != null) {
            outState.putString(BundleKeys.KEY_USER_MESSAGE, mReasonEditText.getText().toString());
        }
        outState.putString(BundleKeys.KEY_PRODUCT_ID, mProductID);
        outState.putBoolean(BundleKeys.KEY_IS_FROM_ORDER, isFromOrder);
        outState.putString(BundleKeys.KEY_PRODUCT_NAME, mProductName);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
        if (savedInstanceState.containsKey(BundleKeys.KEY_INVOICE_NUMBER)) {
            mSavedInvoiceNumber = savedInstanceState.getInt(BundleKeys.KEY_INVOICE_NUMBER);
        }
        mReasonReturnText = savedInstanceState.getString(BundleKeys.KEY_USER_MESSAGE, "");
        mProductID = savedInstanceState.getString(BundleKeys.KEY_PRODUCT_ID);
        isFromOrder = savedInstanceState.getBoolean(BundleKeys.KEY_IS_FROM_ORDER);
        mProductName = savedInstanceState.getString(BundleKeys.KEY_PRODUCT_NAME);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        hideKeyboard();
        super.onDestroyView(view);
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick() {
        hideKeyboard();
        if (mActivity != null) mActivity.onBackPressed();
    }

    @OnFocusChange(R.id.controller_new_return_field)
    void onMessageFieldFocusChange(View view, boolean hasFocus) {
        assert mActivity != null;
        if (hasFocus) {
            KeyboardUtils.setKeyboardAdjustResize(mActivity);
            assert mActivity.getMainController() != null;
            mActivity.getMainController().hideBottomNav();
        } else {
            KeyboardUtils.setKeyboardAdjustPan(mActivity);
            assert mActivity.getMainController() != null;
            mActivity.getMainController().showBottomNav();
        }
    }

    @Override
    public void finishCreateReturnRequest(CreateReturnRequestResponse createReturnResponse) {

        if (createReturnResponse != null && createReturnResponse.getResult()) {

            mReturnId = createReturnResponse.getValue().getReturnId();
            SetAttachmentRequest setAttachmentRequest = new SetAttachmentRequest();
            setAttachmentRequest.setId(mReturnId);
            setAttachmentRequest.setType("return");
            setAttachmentRequest.setListItems(new ArrayList<>());
            mPresenter.setAttachment(setAttachmentRequest, false);

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.error_creating_return));
        }
    }

    @Override
    public void finishReturnRequestTransaction() {
        CustomAlertDialog.showCustomAlertDialog(
                mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                getString(R.string.return_request_submitted));

        getRouter().pushController(RouterTransaction.with(
                ReturnDetailsController.newInstance(mReturnId, mProductName, false))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void loadReturnOrderDetail(NewReturnOrderDetailResponse newReturnsOrderDetail) {

        if (mHasSavedInstance) {
            mAdapter = new NewReturnOrdersAdapter(newReturnsOrderDetail.getList(), mActivity, mPresenter,
                    mProductID, mSavedInvoiceNumber);
        } else {
            mAdapter = new NewReturnOrdersAdapter(newReturnsOrderDetail.getList(), mActivity, mPresenter,
                    mProductID, isFromOrder ? mInvoiceNumber : mReturnItem.getInvoiceNo());
        }

        mNewReturnOrderRecyclerView.setAdapter(mAdapter);
        mNewReturnOrderRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onReturnValueUpdated(String itemId, int position, int productQuantityValue,
                                     boolean isChecked, String productName) {
        if (isChecked && productQuantityValue != 0) {
            mProductName = productName;
            java.util.List<Object> newList = new ArrayList<>();
            newList.add(itemId);
            newList.add(productQuantityValue);
            updateList.put(position, newList);
        } else {
            updateList.remove(position);
        }
    }

    @Override
    public void getAttachmentId(SetAttachmentResponse setAttachmentResponse) {
        mAttachmentId = setAttachmentResponse.getD().getValue();

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
        uploadFileToServer.delegate = this;
        uploadFileToServer.execute(mAttachmentId,
                mImageFileHashMap.get(imageCount), mPresenter.getUserAgent(), imageCount,
                GNotification.getDeviceID(mActivity)+System.currentTimeMillis()+".jpg");
    }

    @Override
    public void getImageUrl(String imageUrl) {
        SetAttachmentRequest.Items items = new SetAttachmentRequest.Items();
        items.setType("image/jpeg");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size()) {
            SetAttachmentRequest setAttachmentRequest = new SetAttachmentRequest();
            setAttachmentRequest.setId(mReturnId);
            setAttachmentRequest.setType("return");
            setAttachmentRequest.setListItems(itemsList);
            mPresenter.setAttachment(setAttachmentRequest, true);
        }
    }

    private void validateRequestReturnForm() {

        CreateReturnRequest createReturnRequest = new CreateReturnRequest();

        if (mHasSavedInstance) {
            createReturnRequest.invoiceNo = String.valueOf(mSavedInvoiceNumber);
        } else {
            createReturnRequest.invoiceNo = isFromOrder ? String.valueOf(mInvoiceNumber) : mReturnItem.getInvoiceNo().toString();
        }

        createReturnRequest.reason = mReasonEditText.getText().toString();
        createReturnRequest.items = getUpdateRequestList();

        if (createReturnRequest.items.size() == 0) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getResources().getString(R.string.please_select_item));
        } else if (createReturnRequest.reason.isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getResources().getString(R.string.please_fill_up_field));
        } else {
            mPresenter.addNewReturnOrderRequest(createReturnRequest);
            hideKeyboard();
        }
    }

    private java.util.List getUpdateRequestList() {

        java.util.List newRequestList = new ArrayList<>();
        if (updateList.size() != 0) {

            for (int key : updateList.keySet()) {

                java.util.List<Object> tempList = updateList.get(key);
                if (Integer.valueOf(tempList.get(1).toString()) != 0) {
                    newRequestList.add(tempList);
                }
            }

            return newRequestList;
        }

        return newRequestList;
    }

    @OnClick(R.id.new_return_confirm_button)
    void onReturnClick() {
        validateRequestReturnForm();
    }

    private void addImageReturn() {
        ReturnDetailsAddImageAdapter imageAdapter = new ReturnDetailsAddImageAdapter(mActivity, this,
                mImageUriArray);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mImageRecyclerView.setAdapter(imageAdapter);
        mImageRecyclerView.setLayoutManager(layoutManager);
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
    public void removeImage(Bitmap bitmap, int position, boolean uploadImage, boolean isAddImageAdapter) {

        ReturnDetailsAddImageAdapter adapter = ((ReturnDetailsAddImageAdapter) mImageRecyclerView.getAdapter());

        mImageFileHashMap.remove(adapter.dataPositionToItemPosition(position));
        mImageUriArray.remove(position);

        adapter.removeItem(adapter.dataPositionToItemPosition(position));

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

            ((ReturnDetailsAddImageAdapter) Objects.requireNonNull(mImageRecyclerView.getAdapter())).addItem();

        }
    }

    @Override
    public void asyncExecutionFinished(String imageUrl, int imagePosition) {

        getImageUrl(ImageUploadUtil.convertStringUrltoJSON(imageUrl));

        if (imagePosition + 1 < AppConstants.MAX_IMAGE_COUNT) {
            callUploadImage(imagePosition + 1);
        }
    }
}
