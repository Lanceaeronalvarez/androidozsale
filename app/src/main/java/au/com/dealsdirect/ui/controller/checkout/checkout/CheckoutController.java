package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.addpayment.AddPaymentController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.home.HomeController;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassController;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.ImageUtils;
import au.com.dealsdirect.utils.PriceUtils;
import butterknife.BindView;
import butterknife.OnClick;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends BaseController implements CheckoutMvpView,FetchTokenHandler {
    public static final String CARD_PAYPAL = "Paypal";
    public static final String CARD_MASTERPASS = "Masterpass";
    public static final String CARD_MASTERCARD = "MasterCard";
    public static final String CARD_VISA = "Visa";

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    View mFooterView;

    @BindView(R.id.fragment_checkout_list)
    ListView mListView;

    RelativeLayout mAddNewAddressLayout;
    RelativeLayout mAddNewPaymentLayout;
    RelativeLayout mAddNewVoucherLayout;
    LinearLayout mAddressLayout;
    LinearLayout mPaymentLayout;
    LinearLayout mVoucherLayout;
    LinearLayout mSummaryLayout;

    TextView mAddressChangeText;
    TextView mPaymentChangeText;
    TextView mVoucherChangeText;
//    View mBraintreeLoading;
    View mButtonHolder;
    Button mPayButton;
    RelativeLayout mPaypalButton;
    RelativeLayout mMasterpassButton;

    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;
    @BindView(R.id.partial_checkout_empty_button)
    Button mShopNowButton;

    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleTextView;

    private ArrayList<Item> mItemList = new ArrayList<>();
    private ArrayList<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private ArrayList<DecorationInfoList> mDecorationInfoList = new ArrayList<>();
    private ArrayList<Voucher> mVouchers = new ArrayList<>();
    private CheckoutOrderAdapter mAdapter;

    private boolean mIsVoucherAdded = false;

    MainActivity mActivity;

    private View.OnClickListener mChangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (view.getId() == mAddNewAddressLayout.getId()) {
                //push controller to add new address
                getRouter().pushController(RouterTransaction.with(new AddNewAddressController(new Gson().toJson(mDecorationInfoList), true))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));


            } else if (view.getId() == mAddressChangeText.getId()) {
                //push controller to view my address
                getRouter().pushController(RouterTransaction.with(new ViewAddressController(true, mDeliveryAddress))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));

            } else if (view.getId() == mPaymentChangeText.getId()
                    || view.getId() == mAddNewPaymentLayout.getId()) {

                if (mPaymentList.size() > 1) {
//                  //push to payment select
                    getRouter().pushController(RouterTransaction.with(new PaymentSelectController(new Gson().toJson(mPaymentList), true))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                } else {
                    //push controller to add payment
                    getRouter().pushController(RouterTransaction.with(new AddPaymentController(true))
                            .pushChangeHandler(new HorizontalChangeHandler())
                            .popChangeHandler(new HorizontalChangeHandler()));
                }

            } else if (view.getId() == mVoucherChangeText.getId()
                    || view.getId() == mAddNewVoucherLayout.getId()) {

                getRouter().pushController(RouterTransaction.with(AddVouchersController.newInstance(new Gson().toJson(mVouchers), mIsVoucherAdded))
                        .pushChangeHandler(new HorizontalChangeHandler())
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
//
//        mBraintreeLoading = mFooterView.findViewById(R.id.partial_checkout_bt_loading);
        mButtonHolder = mFooterView.findViewById(R.id.partial_checkout_button_holder);
        mPayButton = (Button) mFooterView.findViewById(R.id.partial_checkout_button_pay);
        mPaypalButton = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_button_paypal);
        mMasterpassButton = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_button_masterpass);

        setUp(view);
    }


    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        hideLoading();
        super.onDetach(view);
    }

    @Override
    protected void setUp(View view) {

        assert (getActivity()) != null;
        ((MainActivity)getActivity()).getMainController().showBottomNav();
        ((MainActivity)getActivity()).getMainController().setViewpagerDraggable(false);

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

        loadCart();
        mListView.setVisibility(View.GONE);
//        showNoCartItemsLayout();

    }

    public void loadCart() {

        if(mPresenter.checkIsLoggedIn()){
            showLoading();
            if(!mActivity.isBraintreeInitialized()){
                ((MainMvpView) getActivity()).fetchAuthorization(this);
            }else{
                mPresenter.start();
            }
            mActivity.getMainController().getHomeController().getPresenter().callGetBasketItemsQuantity();
        }else{
            showNoCartItemsLayout();
        }
    }

    @Override
    public void showCartDetails(List<Item> items) {
        if (items == null || items.isEmpty()) {

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

            return;

        } else if (((MainActivity) getActivity()).getPaymentMethodSelected() == null && paymentMethod != null) {
            ((MainActivity) getActivity()).setPaymentMethodSelected(paymentMethod);
        }

        paymentMethod = ((MainActivity) getActivity()).getPaymentMethodSelected();
        if (paymentMethod != null) {

            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_name)).setText(paymentMethod.getPaymentType());
            ((TextView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_details)).setText(paymentMethod.getDescription());

            ImageUtils.loadImage(getActivity()
                    , paymentMethod.getImageUrl()
                    , (ImageView) mPaymentLayout.findViewById(R.id.partial_checkout_payment_image));

            mAddNewPaymentLayout.setVisibility(View.GONE);
            mPaymentLayout.setVisibility(View.VISIBLE);
            mPaymentChangeText.setVisibility(View.VISIBLE);
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
    public void triggerLoginTicket() {
        ((MainMvpView) getActivity()).callLoginTicket();
    }

    @Override
    public void updateCheckoutBadge() {
        HomeController homeController = ((MainActivity) getActivity()).getMainController().getHomeController();
        homeController.updateBasketItemCount();
    }

    public void onPayButtonClick() {

        if (!isAddressValid()) {

            //push add new address fragment.
            getRouter().pushController(RouterTransaction.with(new AddNewAddressController(new Gson().toJson(mDecorationInfoList), true))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
            return;
        }

//        RxBus.instance().post(GVersion.EVENT_PAY);

        if (mActivity.isBraintreeInitialized()) {
            if (mActivity.getPaymentMethodSelected() == null) {
                getRouter().pushController(RouterTransaction.with(new AddPaymentController(false))
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler()));

            } else {
                ((MainMvpView) mActivity).callCreatePaymentTransaction("");
            }
        }
    }

    public void onPaypalButtonClick() {

        if (!isAddressValid()) {
            //push add new address fragment
            getRouter().pushController(RouterTransaction.with(new AddNewAddressController(new Gson().toJson(mDecorationInfoList), true))
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
            return;
        }
//
//        RxBus.instance().post(GVersion.EVENT_PAY);
//
        if (mActivity.isBraintreeInitialized()) {
            //If no selected payment method displayed, call paypal
            if (mActivity.getPaymentMethodSelected() == null) {
                mActivity.startPaypalPayment();
            } else {
                ((MainMvpView) mActivity).callCreatePaymentTransaction("");
            }
        }
    }

    public void onMasterpassButtonClick() {
        getRouter().pushController(RouterTransaction.with(MasterpassController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    private boolean isAddressValid() {
        return mDeliveryAddress != null;
    }

    @OnClick(R.id.partial_checkout_empty_button)
    void shopNow() {

        assert (getActivity()) != null;
        ((MainActivity)getActivity()).goToShops();
    }

    private String formAddressDetails(DeliveryAddress deliveryAddress) {

        return deliveryAddress.addressLines + ", "
                + deliveryAddress.suburb + ", "
                + deliveryAddress.state + ", "
                + deliveryAddress.postcode + ", "
                + deliveryAddress.phone;
    }

    public void showNoCartItemsLayout() {
        hidePaymentButtons();
        mNoCartItemsLayout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void hidePaymentButtons() {
//        mBraintreeLoading.setVisibility(View.VISIBLE);
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
//        mBraintreeLoading.setVisibility(View.GONE);
        mButtonHolder.setVisibility(View.VISIBLE);

    }

    @Override
    public void onSuccess() {
        if (!isAttached()) return;
        mPresenter.start();
    }

    @Override
    public void onFailure() {
        if (!isAttached()) return;
        hidePaymentButtons();
    }

}
