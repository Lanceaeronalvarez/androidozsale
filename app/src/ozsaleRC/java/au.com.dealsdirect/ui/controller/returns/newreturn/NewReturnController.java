package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.List;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.returns.newreturn.adapter.NewReturnOrdersAdapter;
import au.com.dealsdirect.ui.controller.returns.newreturn.listener.NewReturnOrderUpdateListener;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.KeyboardUtils;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/*
 * Created by Ayi on 05/06/2017.
 */

public class NewReturnController extends BaseController implements NewReturnMvpView, NewReturnOrderUpdateListener {

    public static final String TAG = "NewReturnController";
    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";

    private View.OnClickListener onClickListener;

    private static List mReturnItem;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mNewReturnToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mNewReturnToolbarRightOption;

    @BindView(R.id.controller_new_return_field)
    EditText mNewReturnCreateReasonField;

    @BindView(R.id.new_returns_set_detail_recyclerview)
    RecyclerView mNewReturnOrderRecyclerView;

    @BindView(R.id.new_return_create_button)
    Button mNewReturnOrderRequestButton;

    @SuppressLint("UseSparseArrays")
    private HashMap<Integer, java.util.List> updateList = new HashMap<>();

    private HashMap<String, NewReturnOrderViewHolder> newReturnOrderViewMap = new HashMap<>();
    private NewReturnOrdersAdapter mAdapter = null;
    private NewReturnOrderDetailResponse mNewReturnsOrderDetail;

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;

    public static NewReturnController newInstance(List returnItem) {

        mReturnItem = returnItem;
        return new NewReturnController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public NewReturnController(Bundle args) {
        super(args);
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

        mNewReturnToolbarTitle.setText(R.string.create_return);
        mNewReturnToolbarRightOption.setVisibility(View.GONE);
        mNewReturnToolbarRightOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_check));

        mPresenter.getReturnOrderDetail(mReturnItem.getInvoiceNo());
        onClickListener = view1 -> {
            validateRequestReturnForm();

        };

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
    public void finishCreateReturnRequest(CreateReturnRequestResponseBody createReturnRequest) {

        if (createReturnRequest.getCreateReturnRequestResponse().getResult()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getResources().getString(R.string.return_request_submitted));
        }

        getRouter().popToTag("CurrentReturnController");
        hideKeyboard();
    }

    @Override
    public void loadReturnOrderDetail(NewReturnOrderDetailResponse newReturnsOrderDetail) {

        mNewReturnsOrderDetail = newReturnsOrderDetail;

        final NewReturnOrdersAdapter adapter
                = new NewReturnOrdersAdapter
                (newReturnsOrderDetail.getList(), mActivity, this);
        mAdapter = adapter;

        mNewReturnOrderRecyclerView.setAdapter(adapter);
        mNewReturnOrderRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    @Override
    public void onReturnValueUpdated(NewReturnOrderViewHolder holder, int position, String returnId, boolean isAdding, int productQuantityValue) {

        String itemId = holder.newReturnItemIdTextView.getText().toString();

        if (productQuantityValue != 0) {
            java.util.List<Object> newList = new ArrayList<>();
            newList.add(itemId);
            newList.add(productQuantityValue);
            updateList.put(position, newList);

        } else {
            updateList.remove(position);

        }
    }

    private void performRequestReturn() {

        java.util.List<Object> requestItems = new ArrayList<>();

        for (Map.Entry<String, NewReturnOrderViewHolder> stringNewReturnOrderViewHolderEntry : newReturnOrderViewMap.entrySet()) {

            @SuppressWarnings("rawtypes")
            Map.Entry pairs = stringNewReturnOrderViewHolderEntry;
            java.util.List<Object> newList = new ArrayList<>();
            NewReturnOrderViewHolder view = (NewReturnOrderViewHolder) pairs.getValue();
            mNewReturnsOrderDetail.getList().get(Integer.parseInt(pairs.getKey().toString()));

            if (view.newReturnItemCheckBox.isChecked()) {
                newList.add(mNewReturnsOrderDetail.getList().get(Integer.parseInt(pairs.getKey().toString())).getID());
                newList.add(Integer.parseInt(view.productQuantityLayout.getQuantity()));
                requestItems.add(newList);
            }
        }

        mNewReturnOrderRequestButton.setEnabled(false);
        CreateReturnRequest createReturnRequest = new CreateReturnRequest();
        createReturnRequest.invoiceNo = mReturnItem.getInvoiceNo().toString();
        createReturnRequest.reason = mNewReturnCreateReasonField.getText().toString();
        createReturnRequest.items = requestItems;
        mPresenter.addNewReturnOrderRequest(createReturnRequest);
    }

    private void validateRequestReturnForm() {
        boolean validReason = false;
        boolean validOrder = false;
        String reason = mNewReturnCreateReasonField.getText().toString();

        if (reason != null && !reason.trim().equals("")) {
            validReason = true;
        }

        newReturnOrderViewMap = mAdapter.getReturnMapView();

        for (Map.Entry<String, NewReturnOrderViewHolder> stringNewReturnOrderViewHolderEntry : newReturnOrderViewMap.entrySet()) {
            @SuppressWarnings("rawtypes")
            Map.Entry pairs = stringNewReturnOrderViewHolderEntry;
            NewReturnOrderViewHolder viewHolder = (NewReturnOrderViewHolder) pairs.getValue();

            if (viewHolder.newReturnItemCheckBox.isChecked()) {
                validOrder = true;
                break;
            }
        }

        if (validReason && validOrder) {
            performRequestReturn();
        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, "Please populate all fields.");
        }
    }

    @OnClick(R.id.new_return_create_button)
    void OnRequestReturn() {
        validateRequestReturnForm();
    }

}
