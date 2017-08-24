package au.com.dealsdirect.ui.controller.returns.newreturn;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

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
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class NewReturnController extends BaseController implements NewReturnMvpView, NewReturnOrderUpdateListener {

    public static final String TAG = "NewReturnController";
    private static final String KEY_TEXT = "NewReturnController.KEY_TEXT";
    public static final String VIEW_FRAGMENT_NEW_RETURNS_ORDER_DETAIL_ID =
            "NEW_RETURNS_ORDER_SET_DETAILS_FRAGMENT_ID";

    private View.OnClickListener onClickListener;

    private static List mReturnItem;

    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mNewReturnToolbarTitle;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageButton mNewReturnToolbarRightOption;

    @BindView(R.id.new_return_create_button)
    Button mNewReturnCreateSubmitButton;

    @BindView(R.id.controller_new_return_field)
    EditText mNewReturnCreateReasonField;

    @BindView(R.id.new_returns_set_detail_recyclerview)
    RecyclerView mNewReturnOrderRecyclerView;

    @Inject
    NewReturnMvpPresenter<NewReturnMvpView> mPresenter;

    NewReturnOrderDetailResponse mNewReturnsOrderDetail;
    View currentView;

    private HashMap<Integer,java.util.List> updateList = new HashMap<>();


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
        // Setup views here
        //mPresenter.loadSample(new SampleRequest());
        mNewReturnToolbarTitle.setText("Create Return");
        mNewReturnToolbarRightOption.setVisibility(View.INVISIBLE);


        mPresenter.getReturnOrderDetail(mReturnItem.getInvoiceNo());
        onClickListener = view1 -> {
//                TextView quantityText
//                        = (TextView) productQuantityLayout.findViewById(R.id.quantity_text);

            int itemCount = mNewReturnOrderRecyclerView.getLayoutManager()
                    .getItemCount();

            java.util.List requestList = new ArrayList<>();

            for (int i = 0; i < itemCount; i++){

                long viewID = mNewReturnOrderRecyclerView
                        .getAdapter().getItemId(i);
                NewReturnOrderViewHolder viewte = (NewReturnOrderViewHolder)
                        mNewReturnOrderRecyclerView
                                .findViewHolderForItemId(viewID);


//                    TextView status = (TextView) viewte.findViewById(R.id.new_return_request_item_name);
//                    TextView totalCost = (TextView) viewte
//                            .findViewById(R.id.new_return_request_item_total_cost);
//                    TextView itemCounttext = (TextView)  viewte.findViewById(R.id.new_returns_order_item_count_value);
//                    TextView itemSizeTextView = (TextView) viewte.findViewById(R.id.new_returns_order_size_return_value);
//                    TextView itemIdTextView = (TextView) viewte.findViewById(R.id.new_returns_order_item_id_value);
//                    ProductQuantityLayout itemCountUpdatedQuantityLayout
//                            = (ProductQuantityLayout) viewte.findViewById(R.id.new_returns_select_order_item_quantaty_selector);


//                    TextView status = (TextView) viewte.findViewById(R.id.new_return_request_item_name);
//                    TextView totalCost = (TextView) viewte
//                            .newReturnItemPriceTextView;
//                    TextView itemCounttext = (TextView)  viewte.newReturnItemCountTextView;
//                    TextView itemSizeTextView = (TextView) viewte.newReturnItemSizeTextView;
//                    TextView itemIdTextView = (TextView) viewte.newReturnItemIdTextView;
//                    ProductQuantityLayout itemCountUpdatedQuantityLayout
//                            = (ProductQuantityLayout) viewte.productQuantityLayout;
//
//                    String itemIdValue = itemIdTextView.getText().toString();
//                    String itemSizeValue = itemSizeTextView.getText().toString();
//                    String itemTotalCost = totalCost.getText().toString();
//                    String itemCountValue = itemCounttext.getText().toString();
//                    String itemUpdatedCount = itemCountUpdatedQuantityLayout.getQuantity();

//                    java.util.List<Object> list1 = new ArrayList<>();
//
//                    if (Integer.parseInt(itemUpdatedCount) != 0){
//
//                        GDebug.log("returns", "entered count iterated");
//                        list1.add(itemIdValue);
//                        list1.add(itemCountValue);
//                        requestList.add(list1);
//                    }

//                    GDebug.log("returns", "item id = "+ itemIdTextView   .getText().toString());
//                    GDebug.log("returns", "item size = "+ itemSizeTextView.getText().toString());
//                    GDebug.log("returns", "item total cost = "+ totalCost.getText().toString());
//                    GDebug.log("returns", "item name = "+status.getText());
//                    GDebug.log("returns", "item count = "+itemCounttext.getText());
            }

            CreateReturnRequest createReturnRequest = new CreateReturnRequest();
            createReturnRequest.invoiceNo = mReturnItem.getInvoiceNo().toString();
            createReturnRequest.reason = mNewReturnCreateReasonField.getText().toString();
            createReturnRequest.items = getUpdateRequestList();

            if (createReturnRequest.items.size() == 0){
                CustomAlertDialog.showCustomAlertDialog(
                        getActivity(),
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        "Please add count to an item to request return"
                );

            }else if (createReturnRequest.reason.isEmpty()){

                CustomAlertDialog.showCustomAlertDialog(
                        getActivity(),
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        "Please fill up the field below"
                );


            }else {

                mPresenter.addNewReturnOrderRequest(createReturnRequest);
            }
        };

//        newReturnOrderSetDetailSendBottomButton.setOnClickListener(onClickListener);

        mNewReturnCreateSubmitButton.setOnClickListener(onClickListener);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        hideKeyboard();
        super.onDestroyView(view);
    }


    @OnClick(R.id.partial_toolbar_arrow_view)
    void onBackClick(){
        hideKeyboard();
        if (getActivity()!=null)
            getActivity().onBackPressed();

    }

    private java.util.List getUpdateRequestList(){

        //updateList.put(position, newList);
        java.util.List newRequestList = new ArrayList<>();
        if(updateList.size() != 0){

            for ( int key : updateList.keySet() ) {

                java.util.List<Object> tempList = updateList.get(key);


                if (Integer.valueOf(tempList.get(1).toString()) != 0 ){
                    newRequestList.add(tempList);
                }
            }

            return newRequestList;
        }

        return newRequestList;
    }

    @Override
    public void finishCreateReturnRequest(CreateReturnRequestResponseBody createReturnRequest) {

        if(createReturnRequest.getCreateReturnRequestResponse().getResult()){
            CustomAlertDialog.showCustomAlertDialog(
                    getActivity(),
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Return request submitted");
        }

        getRouter().popToTag("CurrentReturnController");

    }

    @Override
    public void loadReturnOrderDetail(NewReturnOrderDetailResponse newReturnsOrderDetail) {

        Log.d("NewReturnController" , " value = "+newReturnsOrderDetail.getMessage() + " , "+newReturnsOrderDetail.getList().size());
        mNewReturnsOrderDetail = newReturnsOrderDetail;

        //        List<au.com.topbuy.myreturnsmodule.NewReturnOrderSetDetailsFragmentMVP.Domain.Model.GetReturnOrderDetail.List> lists = newReturnsOrderDetail.getList();
        final NewReturnOrdersAdapter adapter
                = new NewReturnOrdersAdapter
                (newReturnsOrderDetail.getList(),getActivity(),this);

        mNewReturnOrderRecyclerView.setAdapter(adapter);
        mNewReturnOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    @Override
    public void onReturnValueUpdated(NewReturnOrderViewHolder holder, int position, String returnId, boolean isAdding, int productQuantityValue) {

        String itemId = holder.newReturnItemIdTextView.getText().toString();

        if (productQuantityValue != 0){

            java.util.List<Object> newList = new ArrayList<>();
            newList.add(itemId);
            newList.add(productQuantityValue);


            updateList.put(position, newList);
        }else{
            updateList.remove(position);

        }
    }
}
