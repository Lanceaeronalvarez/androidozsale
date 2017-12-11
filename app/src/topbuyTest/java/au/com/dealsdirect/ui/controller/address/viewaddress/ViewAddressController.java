package au.com.dealsdirect.ui.controller.address.viewaddress;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.AddressesItem;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.custom.RecyclerOnTouchListener;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.JsonUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import timber.log.Timber;

import static au.com.dealsdirect.utils.BundleKeys.DECORATION_INFO_LIST;
import static au.com.dealsdirect.utils.BundleKeys.DELIVERY_ADDRESS;
import static au.com.dealsdirect.utils.BundleKeys.IS_FROM_CART;

/**
 * Created by smartwave on 21/06/2017.
 */

public class ViewAddressController extends SwipeableBaseToolBarController implements ViewAddressMvpView {


    @BindView(R.id.no_addresses_layout)
    RelativeLayout mAddressPlaceHolder;
    @BindView(R.id.view_addresses_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.address_office_delivery_subtitle)
    TextView mViewAddressSubHeader;

    private List<AddressesItem> mAddressList;
    boolean mCalledFromCart;
    private int recyclerTempItemPosition;
    private ViewAddressRecyclerViewAdapter mRecyclerViewAdapter;
    private List<DecorationInfoList> mDecorationInfoList;
    private boolean mAddressesLoaded = false;
    private DeliveryAddress mDeliveryAddress;
    private int loadCounter = 0;

    @Inject
    ViewAddressMvpPresenter<ViewAddressMvpView> mPresenter;

    public ViewAddressController(boolean mCalledFromCart, DeliveryAddress deliveryAddress) {
        this(new BundleBuilder(new Bundle())
                .putBoolean(IS_FROM_CART, mCalledFromCart)
                .putParcelable(DELIVERY_ADDRESS, deliveryAddress)
                .build());
    }

    public ViewAddressController(Bundle args) {
        super(args);
        mCalledFromCart = args.getBoolean(IS_FROM_CART);
        mDeliveryAddress =  JsonUtils.convertStringToObject(args.getString(DELIVERY_ADDRESS), new TypeToken<DeliveryAddress>(){}.getType());
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setupSwipingBehavior();
        mToolbarTitle.setText(R.string.my_addresses);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mPresenter.loadAddresses();

        mAddressList = new ArrayList<>();
        mRecyclerViewAdapter = new ViewAddressRecyclerViewAdapter(mCalledFromCart, this, mAddressList, mActivity, mDeliveryAddress, mPresenter);

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(itemAnimator);

        if (mCalledFromCart) {
            mRecyclerView.addOnItemTouchListener(new RecyclerOnTouchListener(mActivity, (v, position) -> {
                if (mAddressesLoaded) {
                    //showAddNewAddressFragment();
                    AddressesItem item = mAddressList.get(position);

                    showProgressDialog("Setting address. Please wait.");
                    mPresenter.applyDeliveryAddress(item.ID);

                    mRecyclerViewAdapter.updateDeliveryAddress(item);
                }
            }));
        }

        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                loadCounter++;
                if (loadCounter==1){
                    mPresenter.loadAddresses();
                    loadCounter = 0;
                }
            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to instanceof ViewAddressController && mActivity.isAuthorized()) {

                }
            }
        });
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);

        fillContent(inflater.inflate(R.layout.controller_view_address, container, false));
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
    public void showAddresses(GetAddresses.ResponseValue responseValue) {
        Timber.d("ViewAddressController", "addresses response");
        if (responseValue.getD().getValue() != null) {

            //If status 0, not valid Address
            if (mAddressList != null) {
                mViewAddressSubHeader.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mAddressPlaceHolder.setVisibility(View.GONE);
                for (AddressesItem addressesItem : responseValue.getD().getValue().getAddressesList()) {
                    if (addressesItem.Status != 0 && !mAddressList.contains(addressesItem)) {
                        mAddressList.add(addressesItem);
                    }
                }
                if (mAddressList.size() == 0) {
                    Timber.d("ViewAddressController", "mAddressList size is zero");
                    mViewAddressSubHeader.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                    mAddressPlaceHolder.setVisibility(View.VISIBLE);
                }
                mDecorationInfoList = responseValue.getD().getValue().getDecorationInfoList();
                mRecyclerViewAdapter.replaceData(mAddressList);
                mAddressesLoaded = true;
                Gson gson = new Gson();

                String decorationList = gson.toJson(mDecorationInfoList);
                Bundle bundle = new Bundle();
                bundle.putString(DECORATION_INFO_LIST, decorationList);
                bundle.putBoolean(IS_FROM_CART, mCalledFromCart);

                setupDefaultBottomButton("add delivery address",
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mAddressesLoaded) {

                                    GateKeeper.push(
                                            getRouter(),
                                            GateKeeper.Destination.ADD_NEW_ADDRESS,
                                            bundle,
                                            new VerticalChangeHandler(false),
                                            new VerticalChangeHandler());

                                }
                            }
                        });

            } else {
                Timber.d("ViewAddressController", "mAddressList is null)");
                mViewAddressSubHeader.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mAddressPlaceHolder.setVisibility(View.VISIBLE);
            }
        } else {
            Timber.d("ViewAddressController", "response.d.Value is null) error");
        }
    }

    @Override
    public void onUserDeliveryAddressDeleted(DeleteUserAddress.ResponseValue responseValue) {

        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.POSITIVE,
                "Removed address");


        mRecyclerViewAdapter.notifyItemRemoved(recyclerTempItemPosition);
        mRecyclerViewAdapter.removeItemAtPosition(recyclerTempItemPosition);
        mRecyclerViewAdapter.notifyItemChanged(recyclerTempItemPosition);

        if (mRecyclerViewAdapter.addressList.size() == 0) {
            mRecyclerView.setVisibility(View.GONE);
            mAddressPlaceHolder.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteItemClicked(DeleteUserAddress.RequestValues deleteUserAddressRequest, int position) {
        recyclerTempItemPosition = position;

        mPresenter.deleteUserDeliveryAddress(deleteUserAddressRequest.getAddressID());
    }

    @Override
    public void backToCheckout() {
        if (mCalledFromCart) {
            mActivity.onBackPressed();
            hideProgressDialog();
        }
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

}
