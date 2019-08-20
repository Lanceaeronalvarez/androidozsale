package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.List;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.returns.newreturn.adapter.NewReturnOrdersAdapter;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/*
 * Created by Ayi on 05/06/2017.
 */

public class NewReturnController extends BaseController implements NewReturnMvpView {

    public static final String TAG = "NewReturnController";
    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";
    private static final String KEY_INVOICE_NUMBER = "NewReturnController.KEY_INVOICE_NUMBER";
    private static final String KEY_IS_FROM_ORDER = "NewReturnController.KEY_IS_FROM_ORDER";
    private static final String KEY_PRODUCT_ID = "NewReturnController.KEY_PRODUCT_ID";

    private View.OnClickListener onClickListener;

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

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, java.util.List> updateList = new HashMap<>();

    private NewReturnOrdersAdapter mAdapter = null;
    private ArrayList<Object> mRequestItems = new ArrayList<>();

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;
    private boolean mHasSavedInstance = false;
    private int mSavedInvoiceNumber;
    private static int mInvoiceNumber;
    private static boolean isFromOrder = false;
    private static String mProductID = "";

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

        if (mHasSavedInstance) {
            mPresenter.getReturnOrderDetail(mSavedInvoiceNumber);
        } else {
            mPresenter.getReturnOrderDetail(isFromOrder ? mInvoiceNumber : mReturnItem.getInvoiceNo());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
        if (mReturnItem != null) outState.putInt(BundleKeys.KEY_INVOICE_NUMBER, mReturnItem.getInvoiceNo());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
        if (savedInstanceState.containsKey(BundleKeys.KEY_INVOICE_NUMBER)) mSavedInvoiceNumber = savedInstanceState.getInt(BundleKeys.KEY_INVOICE_NUMBER);
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
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getString(R.string.return_request_submitted));

            ArrayList<RouterTransaction> backstack = new ArrayList<>(getRouter().getBackstack());


            boolean hasFound = false;
            for (int i = 0; i < backstack.size(); i++) {
                RouterTransaction routerTransaction = backstack.get(i);
                if (routerTransaction.controller() instanceof CurrentReturnsController) {
                    hasFound = true;
                    while (i + 1 < backstack.size()) {
                        backstack.remove(i + 1);
                    }
                }
            }

            if (!hasFound) {
                RouterTransaction root = null;
                if (!backstack.isEmpty()) {
                    root = backstack.get(0);
                }

                RouterTransaction routerTransaction = RouterTransaction.with(
                        CurrentReturnsController.newInstance())
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler());

                backstack.clear();
                if (root != null) {
                    backstack.add(root);
                }
                backstack.add(routerTransaction);
            }

            getRouter().setBackstack(backstack, new HorizontalChangeHandler());

        } else {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.NEGATIVE, getString(R.string.error_creating_return));
        }
    }

    @Override
    public void loadReturnOrderDetail(NewReturnOrderDetailResponse newReturnsOrderDetail) {

        mAdapter = new NewReturnOrdersAdapter(newReturnsOrderDetail.getList(), mActivity, mPresenter, mProductID);

        mNewReturnOrderRecyclerView.setAdapter(mAdapter);
        mNewReturnOrderRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onReturnValueUpdated(String itemId, int position, int productQuantityValue, boolean isChecked) {
        if (isChecked && productQuantityValue != 0) {
            java.util.List<Object> newList = new ArrayList<>();
            newList.add(itemId);
            newList.add(productQuantityValue);
            updateList.put(position, newList);
        } else {
            updateList.remove(position);
        }
    }

    private void validateRequestReturnForm() {

        CreateReturnRequest createReturnRequest = new CreateReturnRequest();

        if (mHasSavedInstance) {
            createReturnRequest.invoiceNo = String.valueOf(mSavedInvoiceNumber);
        } else {
            createReturnRequest.invoiceNo = isFromOrder ? String.valueOf(mInvoiceNumber) : mReturnItem.getInvoiceNo().toString();
        }

        createReturnRequest.reason = mNewReturnCreateReasonField.getText().toString();
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

    @OnClick(R.id.new_return_create_button)
    void OnRequestReturn() {
        validateRequestReturnForm();
    }

}
