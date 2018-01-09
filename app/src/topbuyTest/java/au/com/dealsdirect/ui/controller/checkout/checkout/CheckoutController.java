package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.google.gson.Gson;
import com.mysale.genie.utility.RxBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.service.ourpay.Ourpay;
import au.com.dealsdirect.service.ourpay.OurpayPanel;
import au.com.dealsdirect.service.ourpay.OurpayStateManager;
import au.com.dealsdirect.service.ourpay.OurpayTemplateText;
import au.com.dealsdirect.ui.base.SwipeableBaseToolBarController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsController;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsMvpView;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;

import static au.com.dealsdirect.service.ourpay.OurpayStateManager.CARD_PAYPAL;
import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.KEY_OURPAY_TC_VALIDATION_FAILED;
import static au.com.dealsdirect.utils.BundleKeys.CART_TOTAL_COST;
import static au.com.dealsdirect.utils.BundleKeys.PAYMENT_METHODS;

/**
 * Created by smartwave on 02/11/2017.
 */

public class CheckoutController extends SwipeableBaseToolBarController implements CheckoutMvpView, FetchTokenHandler {

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;
    @Inject
    SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mSaleItemDetailsPresenter;

    ListView mListView;

    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;

    private View mFooterView;

    private ArrayList<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private ArrayList<DecorationInfoList> mDecorationInfoList = new ArrayList<>();

    private RelativeLayout mAddNewAddressLayout;
    private RelativeLayout mAddNewPaymentLayout;
    private RelativeLayout mAddNewVoucherLayout;
    private LinearLayout mAddressLayout;
    private LinearLayout mPaymentLayout;
    private LinearLayout mVoucherLayout;
    private LinearLayout mSummaryLayout;

    private TextView mAddressChangeText;
    private TextView mPaymentChangeText;
    private TextView mVoucherChangeText;
    private View mButtonHolder;
    private Button mPayButton;

    private ArrayList<Voucher> mVouchers;

    @BindView(R.id.checkout_scrollview)
    NestedScrollView mNestedScrollView;

    private RelativeLayout mPaypalButton;
    private RelativeLayout mMasterpassButton;
    private LinearLayout mOurpayHolder;
    private RelativeLayout mButtonOurpay;
    private CheckBox mCheckBoxOurpayTC;

    private CheckoutOrderAdapter mAdapter;
    private ArrayList<Item> mItemList = new ArrayList<>();

    private boolean mIsVoucherAdded = false;
    private String mCartPhone;
    private Value mValue;

    private boolean mCartIsLoading = false;


    public OurpayPanel ourpayPanel;

    private View.OnClickListener mChangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            view.setOnClickListener(null);
            new Handler().postDelayed(() -> view.setOnClickListener(mChangeClickListener), 2000);

            if (view.getId() == mAddNewAddressLayout.getId()) {
                //push controller to add new address
                showAddAddressController();


            } else if (view.getId() == mAddressChangeText.getId()) {
                //push controller to view my address
                Bundle bundle = new Bundle();
                bundle.putBoolean(BundleKeys.IS_FROM_CART, true);
                bundle.putString(BundleKeys.DELIVERY_ADDRESS, new Gson().toJson(mDeliveryAddress));

                mActivity.setDraggableViewPager(false);
                GateKeeper.push(getRouter(), GateKeeper.Destination.VIEW_ADDRESSES, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

            } else if (view.getId() == mPaymentChangeText.getId()
                    || view.getId() == mAddNewPaymentLayout.getId()) {

                if (mPaymentList.size() > 1) {
//                  //push to payment select

                    Bundle bundle = new Bundle();
                    bundle.putBoolean(BundleKeys.IS_FROM_CART, true);
                    bundle.putDouble(CART_TOTAL_COST, mValue.getSummary().total);
                    bundle.putString(PAYMENT_METHODS, new Gson().toJson(mPaymentList));

                    mActivity.setDraggableViewPager(false);
                    GateKeeper.push(getRouter(), GateKeeper.Destination.PAYMENT_SELECT, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

                } else {
                    //push controller to add payment
                    if (!isAddressValid()) {
                        //push add new address fragment
                        CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                        showAddAddressController();
                    } else {
                        showAddPaymentMethodController();
                    }
                }

            } else if (view.getId() == mVoucherChangeText.getId()
                    || view.getId() == mAddNewVoucherLayout.getId()) {

                TextView discountTextView = (TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher);
                boolean isNoDiscount = true;
                if (discountTextView != null) {
                    if (!discountTextView.getText().toString().equals("$0")) {
                        isNoDiscount = false;
                    }
                }

                Bundle bundle = new Bundle();
                bundle.putString(BundleKeys.VOUCHERS, new Gson().toJson(mVouchers));
                bundle.putBoolean(BundleKeys.IS_VOUCHER_ADDED, mIsVoucherAdded);
                bundle.putBoolean(BundleKeys.IS_CART_NO_DISCOUNT, isNoDiscount);

                mActivity.setDraggableViewPager(false);
                GateKeeper.push(getRouter(), GateKeeper.Destination.ADD_VOUCHERS, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

            }

        }
    };


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = super.inflateView(inflater, container);
        View rootView = inflater.inflate(R.layout.controller_checkout, container, false);
        fillContent(rootView);

        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        mListView = (ListView) rootView.findViewById(R.id.fragment_checkout_list);
        mAdapter = new CheckoutOrderAdapter(mActivity, R.layout.partial_checkout_item, mItemList, mPresenter);
        mListView.setAdapter(mAdapter);

        mFooterView = inflater.inflate(R.layout.partial_checkout_footer, container, false);
        mListView.addFooterView(mFooterView, null, false);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        disableSwipingBehavior();
        mToolbarTitle.setText("my checkout");
        mButtonHolder = mFooterView.findViewById(R.id.partial_checkout_button_holder);
        mActivity.setCheckoutRouter(getRouter());

        mAddNewVoucherLayout = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_voucher_new_code);
        mAddNewPaymentLayout = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_payment_new_payment);

        if (mActivity != null) {
            mActivity.performResetWithAuthFetch();
        }

        mAddNewAddressLayout = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_address_new_address);
        mAddNewPaymentLayout = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_payment_new_payment);
        mAddNewVoucherLayout = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_voucher_new_code);

        mAddressLayout = (LinearLayout) mFooterView.findViewById(R.id.partial_checkout_address_container);
        mPaymentLayout = (LinearLayout) mFooterView.findViewById(R.id.partial_checkout_payment_container);
        mVoucherLayout = (LinearLayout) mFooterView.findViewById(R.id.partial_checkout_voucher_container);
        mSummaryLayout = (LinearLayout) mFooterView.findViewById(R.id.partial_checkout_summary_container);

        mAddressChangeText = (TextView) mFooterView.findViewById(R.id.partial_checkout_address_change);
        mPaymentChangeText = (TextView) mFooterView.findViewById(R.id.partial_checkout_payment_change);
        mVoucherChangeText = (TextView) mFooterView.findViewById(R.id.partial_checkout_voucher_change);

        mButtonHolder = mFooterView.findViewById(R.id.partial_checkout_button_holder);
        mPayButton = (Button) mFooterView.findViewById(R.id.partial_checkout_button_pay);
        mPaypalButton = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_button_paypal);
        mMasterpassButton = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_button_masterpass);
        mOurpayHolder = (LinearLayout) mFooterView.findViewById(R.id.partial_checkout_ourpay_panel_holder);


        mAddNewVoucherLayout.setOnClickListener(view12 -> {
            mActivity.setDraggableViewPager(false);
            GateKeeper.push(getRouter(),
                    GateKeeper.Destination.ADD_VOUCHERS,
                    new BundleBuilder(new Bundle())
                            .putString(BundleKeys.VOUCHERS, AddVouchersController.testVouchersString)
                            .putBoolean(BundleKeys.IS_VOUCHER_ADDED, true)
                            .putBoolean(BundleKeys.IS_CART_NO_DISCOUNT, false)
                            .build(),
                    new HorizontalChangeHandler(), new HorizontalChangeHandler());
        });


        mAddNewPaymentLayout.setOnClickListener(view1 -> {

            mActivity.setDraggableViewPager(false);
            GateKeeper.push(getRouter(),
                    GateKeeper.Destination.PAYMENT_ADD,
                    new BundleBuilder(new Bundle())
                            .putBoolean(BundleKeys.IS_FROM_CART, true)
                            .putString(CART_TOTAL_COST, "")
                            .build()
                    , new HorizontalChangeHandler()
                    , new HorizontalChangeHandler());
        });


        setUp(view);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);

    }

    @Override
    protected void setUp(View view) {

        loadCart();
        mAddNewAddressLayout.setOnClickListener(mChangeClickListener);
        mAddNewPaymentLayout.setOnClickListener(mChangeClickListener);
        mAddNewVoucherLayout.setOnClickListener(mChangeClickListener);

        mAddressChangeText.setOnClickListener(mChangeClickListener);
        mPaymentChangeText.setOnClickListener(mChangeClickListener);
        mVoucherChangeText.setOnClickListener(mChangeClickListener);

        mPayButton.setOnClickListener(view1 -> onPayButtonClick());
        mPaypalButton.setOnClickListener(view2 -> onPaypalButtonClick());
        mMasterpassButton.setOnClickListener(view3 -> onMasterpassButtonClick());

        //Code for returning to checkout, call reload
        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (from instanceof ViewAddressController && ((ViewAddressController) from).isNewAddressApplied()) {
                    mPresenter.resetIsCartAlreadyLoaded();
                }

                if (from instanceof AddNewAddressController && ((AddNewAddressController) from).isNewAddressApplied()) {
                    mPresenter.resetIsCartAlreadyLoaded();
                }
            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to instanceof CheckoutController && mActivity.isAuthorized()) {
                    loadCart();
                }
            }
        });

        mListView.setVisibility(View.GONE);

    }

    @Override
    public void showMyPayDetails(Value value, Ourpay ourpay) {

        if (value != null) {
            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
            boolean isMyPayEnabled = mActivity.getIsMyPayEnabled();

            if (ourpay != null && isMyPayEnabled) {

                OurpayStateManager.setOurpayAccordingToPaymentMethod(ourpay, paymentMethod);
                PaymentInfo.setOurpay(ourpay);

                ourpayPanel = new OurpayPanel(mActivity, getRouter());
                mOurpayHolder.removeAllViews();
                if (mOurpayHolder.getChildCount() == 0) { //add view if there is no childview yet
                    mOurpayHolder.addView(ourpayPanel.generatePanel(PaymentInfo.getOurpay(), isRowVisible -> {
                        if (isRowVisible) {
                            new Handler().postDelayed(() -> mNestedScrollView.fullScroll(View.FOCUS_DOWN), 400);
                        }
                    }));
                }

                mButtonOurpay = (RelativeLayout) mOurpayHolder.findViewById(R.id.rl_button_ourpay);
                mButtonOurpay.setOnClickListener(view -> onOurpayButtonClick());

                if (ourpay.getTermsAndConditionsCheckboxState() != 0) {
                    mCheckBoxOurpayTC = (CheckBox) mOurpayHolder.findViewById(R.id.ourpay_checkbox_tc);
                }

            } else {

                Log.d(CheckoutController.class.getName(), "mypay disabled");
            }
        }
    }

    @Override
    public void showCartDetails(List<Item> items) {
        if (items == null) { //do nothing (ie. when increasing order quantity, returns a soldout/out of stock message)
            return;
        } else if (items.isEmpty()) {
            //no items
            showNoCartItemsLayout();
        } else {
            hideBottomButton();
            mNoCartItemsLayout.setVisibility(View.GONE);
            showPaymentButtons();
            mListView.setVisibility(View.VISIBLE);
            mItemList.clear();
            mItemList.addAll(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {
        mDeliveryAddress = deliveryAddress;
        if (!decorationInfoList.isEmpty()) {
            mDecorationInfoList.clear();
            mDecorationInfoList.addAll(decorationInfoList);
        }

        if (deliveryAddress != null) {
            ((TextView) mAddressLayout.findViewById(R.id.partial_checkout_address_name)).setText(deliveryAddress.name);
            ((TextView) mAddressLayout.findViewById(R.id.partial_checkout_address_details)).setText(formAddressDetails(deliveryAddress));
            mCartPhone = deliveryAddress.phone;

            mAddNewAddressLayout.setVisibility(View.GONE);
            mAddressLayout.setVisibility(View.VISIBLE);
            mAddressChangeText.setVisibility(View.VISIBLE);
        } else {
            mAddNewAddressLayout.setVisibility(View.VISIBLE);
            mAddressLayout.setVisibility(View.GONE);
            mAddressChangeText.setVisibility(View.GONE);
        }
    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {

        Log.d("checkoutpayment", "paymentMethod entered");

        if (paymentMethod == null) {
            Log.d("checkoutpayment", "paymentMethod null");

            mAddNewPaymentLayout.setVisibility(View.VISIBLE);
            mPaymentLayout.setVisibility(View.GONE);
            mPaymentChangeText.setVisibility(View.GONE);

            mPayButton.setVisibility(View.VISIBLE);
            mPaypalButton.setVisibility(View.VISIBLE);
            mMasterpassButton.setVisibility(View.VISIBLE);
            mActivity.setPaymentMethodSelected(null);
            return;

        } else if (mActivity.getPaymentMethodSelected() == null) {
            mActivity.setPaymentMethodSelected(paymentMethod);
        }

        paymentMethod = mActivity.getPaymentMethodSelected();
        if (paymentMethod != null) {

            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).setText(paymentMethod.getPaymentType());
            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_details)).setText(paymentMethod.getDescription());

            ImageUtils.loadImage(mActivity
                    , paymentMethod.getImageUrl()
                    , (ImageView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_image));

            mAddNewPaymentLayout.setVisibility(View.GONE);
            mPaymentLayout.setVisibility(View.VISIBLE);
            mPaymentChangeText.setVisibility(View.VISIBLE);
            mMasterpassButton.setVisibility(View.GONE);
        }

        //Payment buttons
        if (paymentMethod == null) {

            mPayButton.setVisibility(View.VISIBLE);
            mPaypalButton.setVisibility(View.VISIBLE);
        } else {

            if (paymentMethod.getPaymentType().equalsIgnoreCase(CARD_PAYPAL)) {
                mPayButton.setVisibility(View.GONE);
                mPaypalButton.setVisibility(View.VISIBLE);
            } else {
                mPayButton.setVisibility(View.VISIBLE);
                mPaypalButton.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {
        mAddNewVoucherLayout.setVisibility(View.VISIBLE);
        mVoucherLayout.setVisibility(View.GONE);
        mVoucherChangeText.setVisibility(View.GONE);
        if (vouchers != null) {
            mVouchers = new ArrayList<>(vouchers);
        }
    }

    @Override
    public void showSummaryDetails(Summary summary) {
        if (summary != null) {
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_subtotal)).setText(PriceUtils.getPriceStringValue(summary.subtotal));
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_shipping_fee)).setText(PriceUtils.getPriceStringValue(summary.delivery));
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher)).setText(PriceUtils.getPriceStringValue(summary.discount));

            if (summary.tax > 0) {
                ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_tax)).setText(PriceUtils.getPriceStringValue(summary.tax));
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_tax_container).setVisibility(View.VISIBLE);
            } else
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_tax_container).setVisibility(View.GONE);


            if (summary.discount > 0) {
                ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher)).setText(PriceUtils.getPriceStringValue(summary.discount));
                mIsVoucherAdded = true;
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher_container).setVisibility(View.VISIBLE);
            } else {
                mIsVoucherAdded = false;
                mSummaryLayout.findViewById(R.id.partial_checkout_summary_voucher_container).setVisibility(View.GONE);
            }
            ((TextView) mSummaryLayout.findViewById(R.id.partial_checkout_summary_total)).setText(PriceUtils.getPriceStringValue(summary.total));
        }
    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {
        mPaymentList.clear();
        mPaymentList.addAll(paymentList);
    }

    @Override
    public void storeCartDetails(Value value) {
        //Set 3DS value
        if (value != null) {
            PaymentInfo.setThreeDSecureRequired(value.threeDSecureRequired);
            PaymentInfo.setCartCost(value.getSummary().total);
        }

        mValue = value;
        mPresenter.generateOurpay(mValue);
    }

    @Override
    public void triggerLoginTicket() {
        assert (mActivity) != null;
        mActivity.callLoginTicket();
    }

    @Override
    public void updateCheckoutBadge() {
        Controller topController = GateKeeper.getCurrentControllerOnRouter(mActivity.getSaleItemsRouter());
        if (topController instanceof SaleItemDetailsController) {
            mSaleItemDetailsPresenter.onAttach((SaleItemDetailsController)topController);
            mSaleItemDetailsPresenter.callGetBasketItemsQuantity();
        }
    }

    @Override
    public boolean isCartLoading() {
        return mCartIsLoading;
    }

    @Override
    public void setCartIsLoading(boolean val) {
        this.mCartIsLoading = val;

    }

    @Override
    public boolean isViewPagerOnCheckout() {
        return mActivity.getMainController().getHomeViewPager().getCurrentItem() == 2;
    }

    @Override
    public void onRefreshStart() {
        super.onRefreshStart();
        loadCart();
    }

    private void showNoCartItemsLayout() {
        hidePaymentButtons();
        mNoCartItemsLayout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
        setupDefaultBottomButton("shop now", view -> {
            mActivity.getMainController().getHomeViewPager().setCurrentItem(1);
        });
    }

    private void hidePaymentButtons() {
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mButtonHolder.setVisibility(View.VISIBLE);
    }

    private boolean isAddressValid() {
        return mDeliveryAddress != null;
    }

    private void showAddAddressController() {
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.DECORATION_INFO_LIST, new Gson().toJson(mDecorationInfoList));
        bundle.putBoolean(BundleKeys.IS_FROM_CART, true);

        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(), GateKeeper.Destination.ADD_NEW_ADDRESS, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

    }

    private void showAddPaymentMethodController() {
        Bundle bundle = new Bundle();
        bundle.putString(CART_TOTAL_COST, Double.toString(mValue.getSummary().total));
        bundle.putBoolean(BundleKeys.IS_FROM_CART, true);

        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(), GateKeeper.Destination.PAYMENT_ADD, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

    }


    public void loadCart() {

        if (mPresenter.checkIsLoggedIn()) {
            RxBus.instance().post(IntrospectionUtils.EVENT_CHECKOUT_SCREEN);

            if (!mActivity.isBraintreeInitialized()) {
                mActivity.fetchAuthorization(this);
            }

            if (!isCartLoading()) {
                setCartIsLoading(true);
                mPresenter.callCartContent();
            }

        } else {
            showNoCartItemsLayout();
        }
    }


    private void onPayButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (mActivity.getPaymentMethodSelected() == null) {
                showAddPaymentMethodController();
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
            }
        }
    }

    private void onPaypalButtonClick() {

        if (!isAddressValid()) {
            //push add new address fragment
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            //If no selected payment method displayed, call paypal
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalPayment();
            } else {
                PaymentInfo.setPaymentType(PaymentInfo.TYPE_BRAINTREE);
                mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
            }
        }
    }

    private void onMasterpassButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            showAddAddressController();
            return;
        }

        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(), GateKeeper.Destination.MASTERPASS, new VerticalChangeHandler(false), new VerticalChangeHandler());
    }

    private void onOurpayButtonClick() {
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        assert (mActivity) != null;

        if (mActivity.isBraintreeInitialized()) {

            if (!isAddressValid()) {

                CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                showAddAddressController();

            } else if (mActivity.getPaymentMethodSelected() == null) {

                showAddPaymentMethodController();

            } else {

                if (!mActivity.getPaymentMethodSelected().getPaymentType().equalsIgnoreCase(CARD_PAYPAL) && PaymentInfo.getOurpay().isCanUse()) {

                    if (mCheckBoxOurpayTC != null && !mCheckBoxOurpayTC.isChecked()) {
                        CustomAlertDialog.showCustomAlertDialog(mActivity, CustomAlertDialog.CustomDialogIconState.NEGATIVE, OurpayTemplateText.getText(mActivity, KEY_OURPAY_TC_VALIDATION_FAILED));
                        return;
                    }

                    if (PaymentInfo.getOurpay().isPhoneVerificationRequired()) {

                        Bundle bundle = new Bundle();
                        bundle.putString(BundleKeys.PHONE_KEY, mCartPhone);
                        mActivity.setDraggableViewPager(false);
                        GateKeeper.push(getRouter(), GateKeeper.Destination.SMS_VERIFICATION, bundle, new VerticalChangeHandler(false), new VerticalChangeHandler());

                    } else {
                        ourpayPaymentSubmit();
                    }
                }
            }
        }
    }

    private void ourpayPaymentSubmit() {

        PaymentInfo.setPaymentType(PaymentInfo.TYPE_MYPAY);
        mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
    }

    @Override
    public void onSuccess() {
        if (!isAttached()) return;
        //loadCartContent(); //Do we have to reload cart on bt token fetch?
    }

    @Override
    public void onFailure() {
        if (!isAttached()) return;
        hidePaymentButtons();
    }


    private String formAddressDetails(DeliveryAddress deliveryAddress) {

        return deliveryAddress.addressLines + ", "
                + deliveryAddress.suburb + ", "
                + deliveryAddress.state + ", "
                + deliveryAddress.postcode + ", "
                + deliveryAddress.phone;
    }
}
