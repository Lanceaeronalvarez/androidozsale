package au.com.dealsdirect.ui.controller.address.viewaddress;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.Gson;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.AddressesItem;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by smartwave on 21/06/2017.
 */

public class ViewAddressController extends BaseController implements ViewAddressMvpView {

    private static final String CALLED_FROM_CART = "CalledFromCart";

    private static final String DELIVERY_ADDRESS = "ViewAddressController.DELIVERY_ADDRESS";

    private static final String CALLED_FROM_ORDER = "ViewAddressController.CALLED_FROM_ORDER";

    private static final String ORDER_ID = "ViewAddressController.ORDER_ID";

    @BindView(R.id.no_addresses_layout)
    RelativeLayout mAddressPlaceHolder;
    @BindView(R.id.view_addresses_layout)
    ViewGroup mViewAddessesLayout;
    @BindView(R.id.controller_addresses_recyclerview)
    RecyclerView mRecyclerView;
    @BindView(R.id.address_office_delivery_subtitle)
    TextView mAddressSubtitle;

    @BindView(R.id.partial_toolbar_left_view)
    View mToolbarLeftView;
    @BindView(R.id.partial_toolbar_title)
    TextView mViewAddressToolarTitle;
    private List<AddressesItem> mAddressList;
    boolean mCalledFromCart = false;
    private ViewAddressRecyclerViewAdapter mRecyclerViewAdapter;
    private List<DecorationInfoList> mDecorationInfoList;
    private boolean mAddressesLoaded = false;
    private DeliveryAddress mDeliveryAddress;
    boolean mCalledFromOrder = false;
    private String mOrderID = "";

    @Inject
    ViewAddressMvpPresenter<ViewAddressMvpView> mPresenter;

    public ViewAddressController() {

    }

    public ViewAddressController(Bundle args) {
        super(args);
    }

    public static ViewAddressController newInstance() {
        return new ViewAddressController(new BundleBuilder(new Bundle()).build());
    }

    public static ViewAddressController newInstance(Parameters parameters) {
        ViewAddressController controller = ViewAddressController.newInstance();

        controller.mCalledFromCart = ((Parameters.DisplayViewAddress) parameters).isCalledFromCart();
        controller.mDeliveryAddress = ((Parameters.DisplayViewAddress) parameters).getDeliveryAddress();
        controller.mCalledFromOrder = ((Parameters.DisplayViewAddress) parameters).isCalledFromOrder();
        controller.mOrderID = ((Parameters.DisplayViewAddress) parameters).getOrderID();

        return controller;
    }

    public abstract static class Parameters {
        private Parameters() {
        }

        public static final class DisplayViewAddress extends Parameters {
            private boolean mCalledFromCart;
            private DeliveryAddress mDeliveryAddress;
            private boolean mCalledFromOrder;
            private String mOrderID;

            public DisplayViewAddress(boolean calledFromCart, DeliveryAddress deliveryAddress,
                                      boolean calledFromOrder, String orderId) {
                mCalledFromCart = calledFromCart;
                mDeliveryAddress = deliveryAddress;
                mCalledFromOrder = calledFromOrder;
                mOrderID = orderId;
            }

            public boolean isCalledFromCart() {
                return mCalledFromCart;
            }

            public DeliveryAddress getDeliveryAddress() {
                return mDeliveryAddress;
            }

            public boolean isCalledFromOrder() {
                return mCalledFromOrder;
            }

            public String getOrderID() {
                return mOrderID;
            }
        }
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {
        showLoading();
        mPresenter.loadAddresses();


        mToolbarLeftView.setVisibility(mPresenter.isTablet() && !mCalledFromCart ? View.INVISIBLE : View.VISIBLE);
        mViewAddressToolarTitle.setText(getString(R.string.my_addresses_toolbar_title));
        mAddressList = new ArrayList<>();
        RecyclerViewSwipeManager swipeManager = new RecyclerViewSwipeManager();
        mRecyclerViewAdapter = new ViewAddressRecyclerViewAdapter(mCalledFromCart, mAddressList, mActivity, mDeliveryAddress, mPresenter,
                                                    mOrderID, mCalledFromOrder);
        RecyclerView.Adapter wrappedAdapter = swipeManager.createWrappedAdapter(mRecyclerViewAdapter);

        mRecyclerView.setAdapter(wrappedAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        swipeManager.attachRecyclerView(mRecyclerView);

        if (mCalledFromCart) {
            mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> {
                if (mAddressesLoaded) {
                    AddressesItem item = mAddressList.get(position);

                    mPresenter.applyDeliveryAddress(item.ID);

                    mRecyclerViewAdapter.updateDeliveryAddress(item);
                }
            }));
        }

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_view_address, container, false);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        mPresenter.loadAddresses();
    }

    @Override
    public void refreshContents() {
        super.refreshContents();
        mPresenter.loadAddresses();
    }

    @Override
    public void showAddresses(GetAddresses.ResponseValue responseValue) {
        Timber.d("ViewAddressController", "addresses response");
        if (responseValue.getD().getValue() != null) {

            //If status 0, not valid Address
            if (mAddressList != null) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mAddressPlaceHolder.setVisibility(View.GONE);
                for (int i = 0; i < responseValue.getD().getValue().getAddressesList().size(); i++) {
                    AddressesItem addressesItem = responseValue.getD().getValue().getAddressesList().get(i);
                    if (addressesItem.Status != 0 && !mAddressList.contains(addressesItem)) {
                        mAddressList.add(addressesItem);
                        addressesItem.setAddressNumericId(i);
                    }
                }

                mDecorationInfoList = responseValue.getD().getValue().getDecorationInfoList();
                mRecyclerViewAdapter.replaceData(mAddressList);
                mAddressesLoaded = true;

            } else {
                Timber.d("ViewAddressController", "mAddressList is null)");
                mRecyclerView.setVisibility(View.GONE);
                mAddressPlaceHolder.setVisibility(View.VISIBLE);
            }
        } else {
            Timber.d("ViewAddressController", "response.d.ScheduledPlan is null) error");
        }
        toggleLayoutVisibility();
    }

    private void toggleLayoutVisibility() {
        boolean hasAddress = mAddressList != null && mAddressList.size() > 0;

        mAddressSubtitle.setVisibility(hasAddress ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(hasAddress ? View.VISIBLE : View.GONE);
        mAddressPlaceHolder.setVisibility(hasAddress ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onUserDeliveryAddressDeleted(DeleteUserAddress.ResponseValue responseValue, AddressesItem deliveryId) {
        if (responseValue.d.getResult()) {
            CustomAlertDialog.showCustomAlertDialog(mActivity,
                    CustomAlertDialog.CustomDialogIconState.POSITIVE,
                    "Removed address");
            mAddressList.remove(deliveryId);

            toggleLayoutVisibility();
        } else {
            CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, responseValue.d.getMessage());
            deleteAddressFailed();
        }
    }

    @Override
    public void backToCheckout() {
        if (mCalledFromCart) {
            mActivity.onBackPressed();
        }
    }

    @Override
    public void deleteAddressFailed() {
        mRecyclerViewAdapter.replaceData(mAddressList);
    }

    @Override
    public void backToOrders() {
        if (mCalledFromOrder) {
            getRouter().handleBack();
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.partial_toolbar_left_view)
    public void onBackClick() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    public void showAddNewAddress() {
        Gson gson = new Gson();
        getRouter().pushController(RouterTransaction.with(new AddNewAddressController(gson.toJson(mDecorationInfoList), false))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @OnClick(R.id.controller_address_button)
    public void clickAddNewAddress() {
        showAddNewAddress();
    }
}
