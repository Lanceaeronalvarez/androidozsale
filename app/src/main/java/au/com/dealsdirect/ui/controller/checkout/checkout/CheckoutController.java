package au.com.dealsdirect.ui.controller.checkout.checkout;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
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
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.DefaultCallback;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.ui.main.PaymentInfo;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.IntrospectionUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;

import static au.com.dealsdirect.service.ourpay.OurpayTemplateText.KEY_OURPAY_TC_VALIDATION_FAILED;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends BaseController implements CheckoutMvpView, DefaultCallback {
    public static final String CARD_PAYPAL = "Paypal";
    public static final String CARD_MASTERPASS = "Masterpass";
    public static final String CARD_MASTERCARD = "MasterCard";
    public static final String CARD_VISA = "Visa";

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    @BindView(R.id.fragment_checkout_list)
    ListView mListView;

    private View mFooterView;

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

    private RelativeLayout mPaypalButton;
    private RelativeLayout mMasterpassButton;
    private LinearLayout mOurpayHolder;
    private RelativeLayout mButtonOurpay;
    private CheckBox mCheckBoxOurpayTC;

    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;
    @BindView(R.id.partial_checkout_empty_button)
    Button mShopNowButton;
    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleTextView;
    @BindView(R.id.checkout_scrollview)
    NestedScrollView mNestedScrollView;

    private ArrayList<Item> mItemList = new ArrayList<>();
    private ArrayList<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private ArrayList<DecorationInfoList> mDecorationInfoList = new ArrayList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();
    private CheckoutOrderAdapter mAdapter;

    private boolean mIsVoucherAdded = false;
    private String mCartPhone;
    private Value mValue;

    private MainActivity mActivity;

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
                getRouter().pushController(RouterTransaction.with(new ViewAddressController(true, mDeliveryAddress))
                        .pushChangeHandler(new HorizontalChangeHandler(false))
                        .popChangeHandler(new HorizontalChangeHandler()));

            } else if (view.getId() == mPaymentChangeText.getId()
                    || view.getId() == mAddNewPaymentLayout.getId()) {

                if (mPaymentList.size() > 1) {
//                  //push to payment select
                    getRouter().pushController(RouterTransaction.with(new PaymentSelectController(new Gson().toJson(mPaymentList), true, Double.toString(mValue.getSummary().total)))
                            .pushChangeHandler(new HorizontalChangeHandler(false))
                            .popChangeHandler(new HorizontalChangeHandler()));
                } else {
                    //push controller to add payment
                    if (!isAddressValid()) {
                        //push add new address fragment
                        CustomAlertDialog.showCustomAlertDialog(getActivity(), CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                        showAddAddressController();
                    } else {
                        showAddPaymentMethodController();
                    }
                }

            } else if (view.getId() == mVoucherChangeText.getId()
                    || view.getId() == mAddNewVoucherLayout.getId()) {

                getRouter().pushController(RouterTransaction.with(AddVouchersController.newInstance(new Gson().toJson(mVouchers), mIsVoucherAdded))
                        .pushChangeHandler(new HorizontalChangeHandler(false))
                        .popChangeHandler(new HorizontalChangeHandler()));
            }

        }
    };

    public CheckoutController() {

    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_checkout, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mFooterView = inflater.inflate(R.layout.partial_checkout_footer, container, false);
        return view;
    }


    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        mActivity = ((MainActivity) getActivity());
        if (mActivity != null) {
            mActivity.performResetWithAuthFetch();
        }

        mAddNewAddressLayout = mFooterView.findViewById(R.id.partial_checkout_address_new_address);
        mAddNewPaymentLayout = mFooterView.findViewById(R.id.partial_checkout_payment_new_payment);
        mAddNewVoucherLayout = mFooterView.findViewById(R.id.partial_checkout_voucher_new_code);

        mAddressLayout = mFooterView.findViewById(R.id.partial_checkout_address_container);
        mPaymentLayout = mFooterView.findViewById(R.id.partial_checkout_payment_container);
        mVoucherLayout = mFooterView.findViewById(R.id.partial_checkout_voucher_container);
        mSummaryLayout = mFooterView.findViewById(R.id.partial_checkout_summary_container);

        mAddressChangeText = mFooterView.findViewById(R.id.partial_checkout_address_change);
        mPaymentChangeText = mFooterView.findViewById(R.id.partial_checkout_payment_change);
        mVoucherChangeText = mFooterView.findViewById(R.id.partial_checkout_voucher_change);

        mButtonHolder = mFooterView.findViewById(R.id.partial_checkout_button_holder);
        mPayButton = mFooterView.findViewById(R.id.partial_checkout_button_pay);
        mPaypalButton = mFooterView.findViewById(R.id.partial_checkout_button_paypal);
        mMasterpassButton = mFooterView.findViewById(R.id.partial_checkout_button_masterpass);
        mOurpayHolder = mFooterView.findViewById(R.id.partial_checkout_ourpay_panel_holder);

        setUp(view);
    }


    @Override
    public void onDetach(View view) {
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    protected void setUp(View view) {

        if (getActivity() != null) {
            mActivity.getMainController().showBottomNav();
            mActivity.getMainController().setViewpagerDraggable(false);
        }


        mTitleTextView.setText(R.string.checkout_page_toolbar_title);

        mAdapter = new CheckoutOrderAdapter(getActivity(), R.layout.partial_checkout_item, mItemList, mPresenter);
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(mFooterView, null, false);

        mAddNewAddressLayout.setOnClickListener(mChangeClickListener);
        mAddNewPaymentLayout.setOnClickListener(mChangeClickListener);
        mAddNewVoucherLayout.setOnClickListener(mChangeClickListener);

        mAddressChangeText.setOnClickListener(mChangeClickListener);
        mPaymentChangeText.setOnClickListener(mChangeClickListener);
        mVoucherChangeText.setOnClickListener(mChangeClickListener);

        mPayButton.setOnClickListener(view1 -> onPayButtonClick());
        mPaypalButton.setOnClickListener(view2 -> onPaypalButtonClick());
        mMasterpassButton.setOnClickListener(view3 -> onMasterpassButtonClick());

        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (to instanceof CheckoutController && mActivity.isAuthorized()) {
                    loadCart();
                }
            }
        });

        loadCart();
        mListView.setVisibility(View.GONE);

    }

    public void loadCart() {
        if (mPresenter.checkIsLoggedIn()) {
            RxBus.instance().post(IntrospectionUtils.EVENT_CHECKOUT_SCREEN);

            if (!mActivity.isBraintreeInitialized()) {
                mActivity.fetchAuthorization(this);
            } else {
                loadCartContent();
            }

        } else {

            showNoCartItemsLayout();
        }
    }

    @Override
    public void showMyPayDetails(Value value, Ourpay ourpay) {

        if (value != null) {
            PaymentMethod paymentMethod = mActivity.getPaymentMethodSelected();
            boolean isMyPayEnabled = mActivity.getIsMyPayEnabled();

            if (ourpay != null && isMyPayEnabled) {

                OurpayStateManager.setOurpayAccordingToPaymentMethod(ourpay, paymentMethod);
                PaymentInfo.setOurpay(ourpay);

                OurpayPanel ourpayPanel = new OurpayPanel((BaseActivity) getActivity(), getRouter());
                mOurpayHolder.removeAllViews();
                if (mOurpayHolder.getChildCount() == 0) { //add view if there is no childview yet
                    mOurpayHolder.addView(ourpayPanel.generatePanel(PaymentInfo.getOurpay(), isRowVisible -> {
                        if (isRowVisible) {
                            new Handler().postDelayed(() -> mNestedScrollView.fullScroll(View.FOCUS_DOWN), 400);
                        }
                    }));
                }

                mButtonOurpay = mOurpayHolder.findViewById(R.id.rl_button_ourpay);
                mButtonOurpay.setOnClickListener(view -> onOurpayButtonClick());

                if (ourpay.getTermsAndConditionsCheckboxState() != 0) {
                    mCheckBoxOurpayTC = mOurpayHolder.findViewById(R.id.ourpay_checkbox_tc);
                }
            }else{
                Log.d(CheckoutController.class.getName(),"mypay disabled");
            }
        }
    }

    @Override
    public void showCartDetails(List<Item> items) {
        mPresenter.generateOurpay(mValue);

        if (items == null) { //do nothing (ie. when increasing order quantity, returns a soldout/out of stock message)
            return;
        } else if (items.isEmpty()) {
            //no items
            showNoCartItemsLayout();
        } else {
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

        if (paymentMethod == null) {
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

            ImageUtils.loadImage(getActivity()
                    , paymentMethod.getImageUrl()
                    , mPaymentLayout.findViewById(R.id.partial_checkout_payment_image));

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

        if (mValue!=null){
            mPresenter.generateOurpay(mValue);
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
        ((MainMvpView) getActivity()).callLoginTicket();
    }

    @Override
    public void updateCheckoutBadge() {
        HomeController homeController = mActivity.getMainController().getHomeController();
        if(mActivity.isAuthorized()) {
            homeController.getPresenter().callGetBasketItemsQuantity();
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

        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void onOurpayButtonClick() {
        RxBus.instance().post(IntrospectionUtils.EVENT_PAY);

        assert (getActivity()) != null;

        if (mActivity.isBraintreeInitialized()) {

            if (!isAddressValid()) {

                CustomAlertDialog.showCustomAlertDialog(getActivity(), CustomAlertDialog.CustomDialogIconState.NEGATIVE, mActivity.getString(R.string.add_address_before_payment));
                showAddAddressController();

            } else if (mActivity.getPaymentMethodSelected() == null) {

                showAddPaymentMethodController();

            } else {

                if (!mActivity.getPaymentMethodSelected().getPaymentType().equalsIgnoreCase(CARD_PAYPAL) && PaymentInfo.getOurpay().isCanUse()) {

                    if (mCheckBoxOurpayTC != null && !mCheckBoxOurpayTC.isChecked()) {
                        CustomAlertDialog.showCustomAlertDialog(getActivity(), CustomAlertDialog.CustomDialogIconState.NEGATIVE, OurpayTemplateText.getText(mActivity, KEY_OURPAY_TC_VALIDATION_FAILED));
                        return;
                    }

                    if (PaymentInfo.getOurpay().isPhoneVerificationRequired()) {

                        getRouter().pushController(RouterTransaction.with(OurpaySMSVerificationController.newInstance(mCartPhone))
                                .pushChangeHandler(new HorizontalChangeHandler(false))
                                .popChangeHandler(new HorizontalChangeHandler()));
                    } else {
                        ourpayPaymentSubmit();
                    }
                }
            }
        }
    }

    private boolean isAddressValid() {
        return mDeliveryAddress != null;
    }

    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {
        mActivity.setShopsAsVisibleContainer();
    }

    private String formAddressDetails(DeliveryAddress deliveryAddress) {

        return deliveryAddress.addressLines + ", "
                + deliveryAddress.suburb + ", "
                + deliveryAddress.state + ", "
                + deliveryAddress.postcode + ", "
                + deliveryAddress.phone;
    }

    private void showNoCartItemsLayout() {
        hidePaymentButtons();
        mNoCartItemsLayout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void hidePaymentButtons() {
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mButtonHolder.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess() {
        if (!isAttached()) return;
        loadCartContent();
    }

    @Override
    public void onFailure() {
        if (!isAttached()) return;
        hidePaymentButtons();
    }

    private void ourpayPaymentSubmit() {

        PaymentInfo.setPaymentType(PaymentInfo.TYPE_MYPAY);
        mActivity.callCreatePaymentTransaction(PaymentInfo.getPaymentType(), "", PaymentInfo.getPaymentMethod().getToken());
    }

    private void loadCartContent() {
//        if (mActivity.getMainController() != null && mActivity.getMainController().getHomeController() != null) {
//            if (mActivity.getMainController().getHomeController().getIsResetCheckout()) {
//                showLoading();
//                mPresenter.start();
//            }
//        } else {

            showLoading();
            mPresenter.start();
//        }
    }


    private void showAddAddressController() {
        getRouter().pushController(RouterTransaction.with(new AddNewAddressController(new Gson().toJson(mDecorationInfoList), true))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private void showAddPaymentMethodController() {
        getRouter().pushController(RouterTransaction.with(new AddPaymentController(true, Double.toString(mValue.getSummary().total)))
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler()));
    }
}

