package au.com.dealsdirect.ui.controller.returns.returndetails;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.Item;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacthistory.ViewContactHistoryController;
import au.com.dealsdirect.ui.controller.returns.returndetails.adapter.ReturnDetailsAdapter;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class ReturnDetailsController extends BaseController implements ReturnDetailsMvpView {

    public static final String TAG = "ReturnDetailsController";

    private static final String KEY_TEXT = "ReturnDetailsController.KEY_TEXT";
    private static final String KEY_ORDER_NUMBER = "ReturnDetailsController.KEY_ORDER_NUMBER";
    private static final String KEY_PRODUCT_NAME = "ReturnDetailsController.KEY_PRODUCT_NAME";
    private static final String KEY_REQUEST_DATE = "ReturnDetailsController.REQUEST_DATE";
    private static final String KEY_IS_APPROVED = "ReturnDetailsController.IS_APPROVED";
    private static final String KEY_STATUS = "ReturnDetailsController.STATUS";
    private static final String KEY_RAN = "ReturnDetailsController.RAN";
    private static final String KEY_RETURN_ID = "ReturnDetailsController.RETURN_ID";
    private static final String KEY_IS_FROM_ORDERS = "ReturnDetailsController.KEY_IS_FROM_ORDERS";

    private String mProductName = "";
    private int mOrderNumber = 0;
    private String mReturnID = "";
    private String mRequestDate = "";
    private String mIsApproved = "";
    private String mStatus = "";
    private String mRAN = "";
    private boolean isFromOrders = false;
    private String contactNumber;

    @BindView(R.id.controller_return_details_order_number)
    TextView mOrderNumberTextView;

    @BindView(R.id.controller_return_details_order_product_delivery_from_date_value)
    TextView mReturnDetailsControllerRequestDateValue;

    @BindView(R.id.controller_return_details_product_is_approved_value)
    TextView mReturnDetailsControllerisApprovedValue;

    @BindView(R.id.controller_return_details_item_status_value)
    TextView mReturnDetailsControllerItemStatus;

    @BindView(R.id.my_current_return_item_RAN_value)
    TextView mReturnDetailsControllerRanValue;

    @BindView(R.id.controller_return_details_recyclerview)
    RecyclerView mReturnDetailsControllerRecyclerView;

    @BindView(R.id.partial_toolbar_title)
    TextView mReturnDetailsControllerToolbarTitle;

    @BindView(R.id.partial_toolbar_right_view)
    ImageView mReturnDetailsControllerToolbarRightOption;

    @BindView(R.id.controller_return_details_total_container)
    ViewGroup mReturnDetailsTotalContainer;

    @BindView(R.id.controller_return_details_total_value)
    TextView mReturnDetailsControllerTotalValue;

    @BindView(R.id.controller_return_details_reason_container)
    RelativeLayout mReturnDetailsReasonContainer;

    @BindView(R.id.controller_return_details_contact_text)
    TextView mContactNumberText;

    @BindView(R.id.controller_return_details_reason_value)
    TextView mReturnDetailsReasonText;

    @BindView(R.id.contact_history_message_text_view)
    TextView mContactMessageText;

    @BindView(R.id.controller_return_details_contact_date)
    TextView mContactMessageDate;

    @BindView(R.id.controller_return_details_to_contact_layout)
    RelativeLayout mReturnToContactLayout;

    @BindView(R.id.controller_return_details_subject_text)
    TextView mReturnDetailsSubjectText;

    @BindView(R.id.controller_return_details_contact_layout)
    FrameLayout mContactMessageContainer;

    @BindView(R.id.controller_return_details_date_container)
    RelativeLayout mContactDateContainer;

    @Inject
    ReturnDetailsMvpPresenter<ReturnDetailsMvpView> mPresenter;

    public static ReturnDetailsController newInstance(
            String productName,
            int orderNumber,
            String returnID,
            String requestDate,
            String isApproved,
            String status,
            String RAN) {

        return new ReturnDetailsController(
                new BundleBuilder(new Bundle())
                        .putString(KEY_PRODUCT_NAME,productName)
                        .putInt(KEY_ORDER_NUMBER, orderNumber)
                        .putString(KEY_RETURN_ID, returnID)
                        .putString(KEY_REQUEST_DATE, requestDate)
                        .putString(KEY_IS_APPROVED, isApproved)
                        .putString(KEY_STATUS, status)
                        .putString(KEY_RAN, RAN)
                        .build());
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

    public ReturnDetailsController(Bundle args) {
        super(args);
        if (args.containsKey(KEY_PRODUCT_NAME)) {
            mProductName = args.getString(KEY_PRODUCT_NAME);
        }

        if (args.containsKey(KEY_ORDER_NUMBER)) {
            mOrderNumber = args.getInt(KEY_ORDER_NUMBER);
        }

        mReturnID = args.getString(KEY_RETURN_ID);

        if (args.containsKey(KEY_REQUEST_DATE)) {
            mRequestDate = args.getString(KEY_REQUEST_DATE);
        }

        if (args.containsKey(KEY_IS_APPROVED)) {
            mIsApproved = args.getString(KEY_IS_APPROVED);
        }

        if (args.containsKey(KEY_STATUS)) {
            mStatus = args.getString(KEY_STATUS);
        }

        if (args.containsKey(KEY_RAN)) {
            mRAN = args.getString(KEY_RAN);
        }

        if (args.containsKey(KEY_IS_FROM_ORDERS)) {
            isFromOrders = args.getBoolean(KEY_IS_FROM_ORDERS);
        }
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
    protected void setUp(View view) {

        mReturnDetailsControllerToolbarRightOption.setVisibility(View.INVISIBLE);
        mReturnDetailsControllerToolbarTitle.setText(mProductName);
        mOrderNumberTextView.append(" " + mOrderNumber);
        mReturnDetailsControllerRequestDateValue.setText(mRequestDate);
        mReturnDetailsControllerisApprovedValue.setText(mIsApproved);
        mReturnDetailsControllerItemStatus.setText(mStatus);
        mReturnDetailsControllerRanValue.setText(mRAN);

        mPresenter.loadCurrentReturnDetails(mReturnID);
    }

    @Override
    public void onDestroyView(View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showCurrentReturnDetails(GetReturnDetailsResponseBody getReturnDetailsResponseBody) {

        List<Item> items = getReturnDetailsResponseBody.getValue().getItems();
        Double subTotal = getReturnDetailsResponseBody.getValue().getTotal();

        ReturnDetailsAdapter adapter = new ReturnDetailsAdapter(items, subTotal, mActivity);

        mReturnDetailsControllerTotalValue.setText(PriceUtils.getPriceStringValue(subTotal));
        mReturnDetailsControllerRecyclerView.setAdapter(adapter);
        mReturnDetailsControllerRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        mReturnDetailsTotalContainer.setVisibility(View.VISIBLE);
        mContactMessageContainer.setVisibility(getReturnDetailsResponseBody.getValue().getContactNumber() != 0 ?
                View.VISIBLE : View.GONE);
        mContactDateContainer.setVisibility(getReturnDetailsResponseBody.getValue().getContactNumber() != 0 ?
                View.VISIBLE : View.GONE);

        mReturnDetailsReasonText.setText(getReturnDetailsResponseBody.getValue().getReason());

        if (getReturnDetailsResponseBody.getValue().getContactNumber() != 0) {
            contactNumber = String.valueOf(getReturnDetailsResponseBody.getValue().getContactNumber());
            String concatenateContactNo = mActivity.getResources().getString(R.string.contact_number_return_details) + contactNumber;
            mContactNumberText.setText(concatenateContactNo);
            GetContactHistoryRequest getContactHistoryRequest = new GetContactHistoryRequest();
            getContactHistoryRequest.contactNo = getReturnDetailsResponseBody.getValue().getContactNumber();
            mPresenter.loadReturnContacts(getContactHistoryRequest);
        }
    }

    @Override
    public void showContactMessageReturn(GetContactHistoryResponse.ResponseValue responseValue) {

        au.com.dealsdirect.data.network.model.contacthistory.List lastItemPosition = responseValue.getList().get(0);

        mContactMessageDate.setText(DateUtils.getDateForContactMessages(lastItemPosition.getDate()));
        mContactMessageText.setText(lastItemPosition.getText());
        mReturnDetailsSubjectText.setText(lastItemPosition.getSubject());

        mReturnToContactLayout.setOnClickListener(v -> {
           getRouter().pushController(RouterTransaction.with(ViewContactHistoryController.newInstance(
                    lastItemPosition.getSubject(),
                    "",
                    lastItemPosition.getInvoiceNo(),
                    DateUtils.getDateForContactMessages(lastItemPosition.getDate()),
                    Integer.parseInt(contactNumber),
                   true))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        });
    }


    @OnClick(R.id.partial_toolbar_left_view)
    void onBackClick(){
        mActivity.onBackPressed();
    }

}
