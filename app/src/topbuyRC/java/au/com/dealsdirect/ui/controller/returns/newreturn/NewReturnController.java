package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.List;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.returns.newreturn.adapter.NewReturnOrdersAdapter;
import au.com.dealsdirect.ui.controller.returns.newreturn.listener.NewReturnOrderUpdateListener;
import au.com.dealsdirect.ui.controller.returns.newreturn.viewholder.NewReturnOrderViewHolder;
import au.com.dealsdirect.ui.controller.saleitems.KeyboardHeightObserver;
import au.com.dealsdirect.ui.controller.saleitems.KeyboardHeightProvider;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.ViewUtils;
import butterknife.BindView;
import butterknife.OnFocusChange;
import butterknife.OnTouch;

/*
 * Created by Ayi on 05/06/2017.
 */

public class NewReturnController extends SwipeableBaseToolBarController implements NewReturnMvpView, NewReturnOrderUpdateListener, KeyboardHeightObserver {

    public static final String TAG = "NewReturnController";
    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";

    private View.OnClickListener onClickListener;

    private static List mReturnItem;

    @BindView(R.id.controller_new_return_field)
    EditText mNewReturnCreateReasonField;

    @BindView(R.id.new_returns_set_detail_recyclerview)
    RecyclerView mNewReturnOrderRecyclerView;

    @BindView(R.id.controller_new_return_create_button)
    Button mNewReturnSubmitButton;

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;

    NewReturnOrderDetailResponse mNewReturnsOrderDetail;
    private HashMap<Integer, java.util.List> updateList = new HashMap<>();

    private KeyboardHeightProvider mKeyboardHeightProvider;

    private RelativeLayout.LayoutParams layoutParams;

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
        View view = super.inflateView(inflater, container);
        fillContent(inflater.inflate(R.layout.controller_new_return, container, false));
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mToolbarTitle.setText("new returns");
        setUp(view);
        setupSwipingBehavior();
    }

    @Override
    protected void setUp(View view) {

        mPresenter.getReturnOrderDetail(mReturnItem.getInvoiceNo());
        onClickListener = view1 -> {

            int itemCount = mNewReturnOrderRecyclerView.getLayoutManager()
                    .getItemCount();

            for (int i = 0; i < itemCount; i++) {

                long viewID = mNewReturnOrderRecyclerView
                        .getAdapter().getItemId(i);
                NewReturnOrderViewHolder viewte = (NewReturnOrderViewHolder)
                        mNewReturnOrderRecyclerView
                                .findViewHolderForItemId(viewID);

            }

            CreateReturnRequest createReturnRequest = new CreateReturnRequest();
            createReturnRequest.invoiceNo = mReturnItem.getInvoiceNo().toString();
            createReturnRequest.reason = mNewReturnCreateReasonField.getText().toString();
            createReturnRequest.items = getUpdateRequestList();

            if (createReturnRequest.items.size() == 0) {
                CustomAlertDialog.showCustomAlertDialog(
                        mActivity,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        mActivity.getResources().getString(R.string.please_add_count_to_an_item)
                );

            } else if (createReturnRequest.reason.isEmpty()) {

                CustomAlertDialog.showCustomAlertDialog(
                        mActivity,
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getResources().getString(R.string.please_fill_up_field)
                );


            } else {

                mPresenter.addNewReturnOrderRequest(createReturnRequest);
            }
        };

        mNewReturnSubmitButton.setEnabled(false);
        mNewReturnSubmitButton.setOnClickListener(onClickListener);

        mKeyboardHeightProvider = new KeyboardHeightProvider(mActivity);
        mKeyboardHeightProvider.setKeyboardHeightObserver(this);
        mKeyboardHeightProvider.start();

        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mNewReturnOrderRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity,
                (v, position) -> {
                    hideKeyboard();
                    mNewReturnOrderRecyclerView.requestFocus();
        } ));
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        mKeyboardHeightProvider = new KeyboardHeightProvider(mActivity);
        mKeyboardHeightProvider.setKeyboardHeightObserver(this);
        mKeyboardHeightProvider.start();
    }

    @Override
    protected void onActivityPaused(@NonNull Activity activity) {
        super.onActivityPaused(activity);
        if(mKeyboardHeightProvider != null) {
            mKeyboardHeightProvider.close();
            mKeyboardHeightProvider = null;
        }
    }

    @OnFocusChange(R.id.controller_new_return_field)
    void onMessageFieldFocusChange(View view, boolean hasFocus) {
        assert mActivity != null;

        if (hasFocus) {
            assert mActivity.getMainController() != null;
            mActivity.getMainController().hideBottomNav();
        } else {
            assert mActivity.getMainController() != null;
            mActivity.getMainController().showBottomNav();
        }
    }

    @OnTouch(R.id.controller_new_return_field)
    public boolean onTouch(View v, MotionEvent event) {

        v.getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                v.getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return false;
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        if(mKeyboardHeightProvider != null) {
            mKeyboardHeightProvider.close();
            mKeyboardHeightProvider = null;
        }
        super.onDestroyView(view);
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

    @Override
    public void finishCreateReturnRequest(CreateReturnRequestResponseBody createReturnRequest) {

        if (createReturnRequest.getCreateReturnRequestResponse().getResult()) {
            CustomAlertDialog.showCustomAlertDialog(
                    mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    getResources().getString(R.string.return_request_submitted));
        }

        getRouter().popToTag(CurrentReturnsController.TAG);
        hideKeyboard();

    }

    @Override
    public void loadReturnOrderDetail(NewReturnOrderDetailResponse newReturnsOrderDetail) {

        mNewReturnsOrderDetail = newReturnsOrderDetail;

        final NewReturnOrdersAdapter adapter
                = new NewReturnOrdersAdapter
                (newReturnsOrderDetail.getList(), mActivity, this);

        mNewReturnOrderRecyclerView.setAdapter(adapter);
        mNewReturnOrderRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,
                LinearLayoutManager.VERTICAL,
                false));
        mNewReturnOrderRecyclerView.setNestedScrollingEnabled(false);

        mNewReturnSubmitButton.setEnabled(true);
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

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        if(height == 0) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            layoutParams.height = ViewUtils.dpToPx(200);
        }
        mNewReturnOrderRecyclerView.setLayoutParams(layoutParams);
    }
}
