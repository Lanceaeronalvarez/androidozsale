package au.com.dealsdirect.ui.controller.checkout.deliveryoptions;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryServicePackageDetail;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.custom.SimpleDividerItemDecoration;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.DateUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

public class DeliveryOptionsController extends BaseController implements DeliveryOptionsMvpView {

    @Inject
    DeliveryOptionsMvpPresenter<DeliveryOptionsMvpView> mPresenter;

    @BindView(R.id.delivery_options_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.partial_toolbar_title)
    TextView mToolbarTitleTextView;
    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mToolbarRightButton;

    private DeliveryOptionsAdapter mAdapter;
    private List<DeliveryOption> mDeliveryOptions;
    private DeliveryServicePackageDetail mDeliveryServicePackageDetail;
    private String mDeliveryAddressId;
    private CheckoutMvpView mCheckoutMvpView;
    private boolean mIsAddressValid;

    public static DeliveryOptionsController newInstance() {
        return new DeliveryOptionsController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public DeliveryOptionsController(Bundle args) {
        super(args);
        mDeliveryOptions = new Gson().fromJson(args.getString(BundleKeys.DELIVERY_OPTIONS_LIST, ""), new TypeToken<List<DeliveryOption>>() {
        }.getType());
        mDeliveryServicePackageDetail = new Gson().fromJson(args.getString(BundleKeys.DELIVERY_OPTIONS_DELIVERY_SERVICE_PACKAGE_DETAIL, ""), DeliveryServicePackageDetail.class);
        mDeliveryAddressId = args.getString(BundleKeys.DELIVERY_OPTIONS_DELIVERY_ADDRESS_ID, "");
        mIsAddressValid = args.getBoolean(BundleKeys.DELIVERY_OPTIONS_IS_ADDRESS_VALID, true);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_delivery_options, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mCheckoutMvpView = (CheckoutMvpView) getRouter().getControllerWithTag(CheckoutController.class.getName());
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mToolbarTitleTextView.setText(getString(R.string.deliver_options_title));
        mToolbarRightButton.setVisibility(View.INVISIBLE);

        final ArrayList<DeliveryOption> deliveryOptions = new ArrayList<>();
        for (DeliveryOption option : mDeliveryOptions) {
            if (!(option.getPostServiceId() != null && option.getPostServiceId().equals("OURPAYSELECT")) ||
                    isSelectAvailableAfterDecommission()) {
                deliveryOptions.add(option);
            }
        }
        mDeliveryOptions = deliveryOptions;

        mAdapter = new DeliveryOptionsAdapter(
                mActivity,
                mRecyclerView,
                mDeliveryOptions,
                mDeliveryAddressId,
                mDeliveryServicePackageDetail,
                mIsAddressValid,
                mPresenter);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(mActivity, SimpleDividerItemDecoration.VERTICAL_LIST));
    }

    @OnClick(R.id.partial_toolbar_left_view)
    void onBackPressed() {
        mActivity.onBackPressed();
    }

    @Override
    public void onDeliveryServicePackageDetailsLoaded(List<GetDeliveryServicePackageDetails.ResponseValue.Value> ourpaySelectDeliveryOptions) {
        mAdapter.setOurPaySelectDeliveryOptions(ourpaySelectDeliveryOptions);
    }

    @Override
    public void onSetDeliveryOption(GetCurrentOrder.ResponseValue responseValue) {
        onBackPressed();
    }

    @Override
    public void showTermsAndConditionsController() {
        GateKeeper.push(getRouter(), GateKeeper.Destination.LEGALITIES,
                new BundleBuilder(new Bundle())
                        .putString(BundleKeys.TEMPLATE_KEY, OurpayTemplateText.KEY_OPS_TNC_FULL_TEXT)
                        .putString(BundleKeys.LEGALITIES_TITLE, getString(R.string.my_basket))
                        .build(),
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());
    }

    private boolean isSelectAvailableAfterDecommission() {
        final Date currentDate = new Date();
        return mDeliveryServicePackageDetail != null &&
                mDeliveryServicePackageDetail.getPurchased() &&
                mDeliveryServicePackageDetail.getRemainingCount() > 0 &&
                currentDate.before(DateUtils.dateFromServerDateString(mDeliveryServicePackageDetail.getExpiryDate()));
    }
}
