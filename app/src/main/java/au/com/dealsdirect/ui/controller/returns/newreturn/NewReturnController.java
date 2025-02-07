package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.service.fcm.GNotification;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.login.PopUpHostController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.returns.newreturn.adapter.NewReturnOrdersAdapter;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsListener;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAddImageAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUploadUtil;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.KeyboardUtils;
import au.com.dealsdirect.utils.LoadingDialogType;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class NewReturnController extends BaseController implements NewReturnMvpView {

    public static final String TAG = "NewReturnController";
    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";
    private static final String KEY_INVOICE_NUMBER = "NewReturnController.KEY_INVOICE_NUMBER";
    private static final String KEY_IS_FROM_ORDER = "NewReturnController.KEY_IS_FROM_ORDER";
    private static final String KEY_PRODUCT_ID = "NewReturnController.KEY_PRODUCT_ID";

    @BindView(R.id.partial_toolbar_title)
    TextView mNewReturnToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mInfoButton;

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
    private HashSet<NewReturnItem> updateList = new HashSet<>();

    private final HashMap<Integer, File> mImageFileHashMap = new HashMap<>();
    private final ArrayList<ImageAttachment> itemsList = new ArrayList<>();
    private String mProductName = "";
    private String mReasonReturnText;

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;
    private boolean mHasSavedInstance = false;
    private int mSavedInvoiceNumber = -1;
    private int mInvoiceNumber = -1;
    private boolean isFromOrder = false;
    private String mProductID = "";
    private String mReturnId = null;
    private String mAttachmentId = "";
    ImageUploadUtil.UploadFileToServer uploadFileToServer;
    private final ArrayList<ImageUtils.ImageLink> mImageUriArray = new ArrayList<>();

    private boolean someImagesWereNotUploaded = false;

    private final ReturnDetailsListener returnDetailsListener = new ReturnDetailsListener() {
        @Override
        public void getImageFromDirectory(boolean uploadImage) {
            final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                    registerActivityResultLauncher(TAG, new ActivityResultContracts.PickVisualMedia(), uri -> {
                        if (uri == null) {
                            return;
                        }

                        ImageUtils.ImageLink imageLinks = new ImageUtils.ImageLink();
                        imageLinks.setIsURL(false);
                        imageLinks.setLink(uri.toString());
                        final int previousSize = mImageUriArray.size();
                        mImageUriArray.add(imageLinks);

                        final ReturnDetailsAddImageAdapter adapter = (ReturnDetailsAddImageAdapter) mImageRecyclerView.getAdapter();
                        if (adapter != null) {
                            adapter.notifyItemInserted(mImageUriArray.size() - 1);
                            if (previousSize < mImageUriArray.size() && mImageUriArray.size() == AppConstants.MAX_IMAGE_COUNT) {
                                adapter.notifyItemRemoved(mImageUriArray.size());
                            }
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
        public void removeImage(Bitmap bitmap, int position, boolean uploadImage, boolean isAddImageAdapter) {

            ReturnDetailsAddImageAdapter adapter = ((ReturnDetailsAddImageAdapter) mImageRecyclerView.getAdapter());

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

    public static NewReturnController newInstance(GetReturnOrders returnItem) {
        return newInstance(returnItem.getInvoiceNumber(), false, null);
    }

    public static NewReturnController newInstance(int invoiceNumber, boolean calledFromOrder, String productId) {

        NewReturnController controller = new NewReturnController(
                new BundleBuilder(new Bundle())
                        .build());
        controller.mInvoiceNumber = invoiceNumber;
        controller.isFromOrder = calledFromOrder;
        controller.mProductID = productId;
        return controller;
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

        showLoading(LoadingDialogType.DEFAULT);

        setupImageList();

        if (mHasSavedInstance) {
            mPresenter.getReturnOrderDetail(mSavedInvoiceNumber);
            mReasonEditText.setText(mReasonReturnText);
        } else {
            mPresenter.getReturnOrderDetail(mInvoiceNumber);
        }

        mInfoButton.setVisibility(View.INVISIBLE);
        mInfoButton.setImageResource(R.drawable.ic_info_encircled);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
        outState.putInt(BundleKeys.KEY_INVOICE_NUMBER, mInvoiceNumber);
        if (mReasonEditText != null && mReasonEditText.getText() != null) {
            outState.putString(BundleKeys.KEY_USER_MESSAGE, mReasonEditText.getText().toString());
        }
        outState.putString(BundleKeys.KEY_PRODUCT_ID, mProductID);
        outState.putBoolean(BundleKeys.KEY_IS_FROM_ORDER, isFromOrder);
        outState.putString(BundleKeys.KEY_TOOLBAR_TITLE, mProductName);
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
        mProductName = savedInstanceState.getString(BundleKeys.KEY_TOOLBAR_TITLE);
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

        if (createReturnResponse != null &&
                createReturnResponse.getId() != null && !createReturnResponse.getId().isEmpty()) {

            mReturnId = createReturnResponse.getId();
            if (mImageFileHashMap.isEmpty()) {
                finishReturnRequestTransaction();
            } else {
                mPresenter.setAttachment(mReturnId, itemsList, false);
            }

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.error_creating_return));
        }
    }

    @Override
    public void finishReturnRequestTransaction() {
        showSuccess();
    }

    @Override
    public void loadReturnOrderDetail(List<NewReturnItem> newReturnsOrderDetail) {

        final NewReturnOrderViewHolder.ValueChangedListener listener = (item, isChecked) -> {
            if (isChecked && item.getQuantity() != 0) {
                mProductName = item.getName();
                updateList.add(item);
            } else {
                updateList.remove(item);
            }
        };

        NewReturnOrdersAdapter adapter;
        if (mHasSavedInstance) {
            adapter = new NewReturnOrdersAdapter(newReturnsOrderDetail, mActivity, listener,
                    mProductID, mSavedInvoiceNumber);
        } else {
            adapter = new NewReturnOrdersAdapter(newReturnsOrderDetail, mActivity, listener,
                    mProductID, isFromOrder ? mInvoiceNumber : mInvoiceNumber);
        }

        mNewReturnOrderRecyclerView.setAdapter(adapter);
        mNewReturnOrderRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void getAttachmentId(String setAttachmentResponse) {
        mAttachmentId = setAttachmentResponse;

        someImagesWereNotUploaded = false;
        callUploadImage(0);
    }

    private void callUploadImage(int imageCount) {
        if (mImageFileHashMap.get(imageCount) == null) {
            if (imageCount < AppConstants.MAX_IMAGE_COUNT) {
                callUploadImage(imageCount + 1);
            } else if (someImagesWereNotUploaded) {
                showDialogSomeImagesWereNotUploaded();
            } else {
                finishReturnRequestTransaction();
            }
            return;
        }
        uploadFileToServer = new ImageUploadUtil.UploadFileToServer(mActivity, true);
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
            } else {
                finishReturnRequestTransaction();
            }
        };
        uploadFileToServer.execute(mAttachmentId,
                mImageFileHashMap.get(imageCount), mPresenter.getUserAgent(), imageCount,
                GNotification.getDeviceID(mActivity) + System.currentTimeMillis() + ".jpg");
    }

    @Override
    public void getImageUrl(String imageUrl) {
        ImageAttachment items = new ImageAttachment();
        items.setType("image/jpeg");
        items.setUrl(imageUrl);
        itemsList.add(items);

        if (mImageFileHashMap.size() == itemsList.size() && mReturnId != null) {
            mPresenter.setAttachment(mReturnId, itemsList, true);
        }
    }

    private void validateRequestReturnForm() {

        CreateReturnRequest createReturnRequest = new CreateReturnRequest();

        if (mHasSavedInstance) {
            createReturnRequest.setInvoiceNumber(mSavedInvoiceNumber);
        } else {
            createReturnRequest.setInvoiceNumber(mInvoiceNumber);
        }

        createReturnRequest.setReason(mReasonEditText.getText().toString());
        if (updateList.isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getResources().getString(R.string.please_select_item));
        } else if (createReturnRequest.getReason().isEmpty()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, getResources().getString(R.string.please_fill_up_field));
        } else {
            createReturnRequest.setItems(new ArrayList<>(updateList));
            mPresenter.addNewReturnOrderRequest(createReturnRequest);
            hideKeyboard();
        }
    }

    @OnClick(R.id.new_return_confirm_button)
    void onReturnClick() {
        validateRequestReturnForm();
    }

    private void setupImageList() {
        final ReturnDetailsAddImageAdapter imageAdapter = new ReturnDetailsAddImageAdapter(
                mImageUriArray,
                returnDetailsListener);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, true);
        mImageRecyclerView.setAdapter(imageAdapter);
        mImageRecyclerView.setLayoutManager(layoutManager);
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void openInfo() {
        final Router router = mActivity.getMainController().getPopUpHostRouter();
        final Bundle bundle = new BundleBuilder(new Bundle())
                .putSerializable(BundleKeys.KEY_POP_UP_HOST_DESTINATION, GateKeeper.Destination.NEW_RETURNS_INFO)
                .build();

        RouterTransaction routerTransaction = RouterTransaction
                .with(new PopUpHostController(bundle))
                .pushChangeHandler(new FadeChangeHandler())
                .popChangeHandler(new FadeChangeHandler());

        router.replaceTopController(routerTransaction);
    }

    private void showSuccess() {
        boolean hasPopped = false;
        for (int i = 0; i < getRouter().getBackstack().size(); i++) {
            if (getRouter().getBackstack().get(i).controller() instanceof CurrentReturnsController) {
                List<RouterTransaction> newStack = new ArrayList<>(getRouter().getBackstack().subList(0, i + 1));
                getRouter().setBackstack(newStack, new HorizontalChangeHandler());
                hasPopped = true;
                break;
            }
        }

        if (!hasPopped) {
            List<RouterTransaction> newStack = new ArrayList<>();
            if (!mPresenter.isTablet()) {
                newStack.add(getRouter().getBackstack().get(0)); //this should be the My Account Menu
            }
            newStack.add(RouterTransaction.with(CurrentReturnsController.newInstance()));
            getRouter().setBackstack(newStack, new HorizontalChangeHandler());
        }
    }

    private void showDialogSomeImagesWereNotUploaded() {
        final DialogInterface.OnClickListener onClickListener = (dialogInterface, i) -> finishReturnRequestTransaction();
        new AlertDialog.Builder(mActivity)
                .setMessage("Some images were not uploaded or attached.")
                .setNeutralButton("Okay", onClickListener)
                .show();
    }
}
