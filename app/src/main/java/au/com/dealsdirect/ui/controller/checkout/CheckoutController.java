package au.com.dealsdirect.ui.controller.checkout;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.exceptions.AuthenticationException;
import com.braintreepayments.api.exceptions.AuthorizationException;
import com.braintreepayments.api.exceptions.ConfigurationException;
import com.braintreepayments.api.exceptions.DownForMaintenanceException;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.ServerException;
import com.braintreepayments.api.exceptions.UnexpectedException;
import com.braintreepayments.api.exceptions.UpgradeRequiredException;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.Gson;
import com.mysale.genie.utility.RxBus;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseActivity;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.FetchTokenHandler;
import au.com.dealsdirect.ui.main.MainMvpView;
import butterknife.BindView;
import butterknife.ButterKnife;

import javax.inject.Inject;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends BaseController implements CheckoutMvpView {
    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    View mFooterView;

    @BindView(R.id.fragment_checkout_list)
    ListView mListView;
    @BindView(R.id.partial_checkout_address_new_address)
    RelativeLayout mAddNewAddressLayout;
    @BindView(R.id.partial_checkout_payment_new_payment)
    RelativeLayout mAddNewPaymentLayout;
    @BindView(R.id.partial_checkout_voucher_new_code)
    RelativeLayout mAddNewVoucherLayout;
    @BindView(R.id.partial_checkout_address_container)
    LinearLayout mAddressLayout;
    @BindView(R.id.partial_checkout_payment_container)
    LinearLayout mPaymentLayout;
    @BindView(R.id.partial_checkout_voucher_container)
    LinearLayout mVoucherLayout;
    @BindView(R.id.partial_checkout_summary_container)
    LinearLayout mSummaryLayout;

    @BindView(R.id.partial_checkout_address_change)
    TextView mAddressChangeText;
    @BindView(R.id.partial_checkout_payment_change)
    TextView mPaymentChangeText;
    @BindView(R.id.partial_checkout_voucher_change)
    TextView mVoucherChangeText;

    @BindView(R.id.partial_checkout_bt_loading)
    View mBraintreeLoading;
    @BindView(R.id.partial_checkout_button_holder)
    View mButtonHolder;
    @BindView(R.id.partial_checkout_button_pay)
    Button mPayButton;
    @BindView(R.id.partial_checkout_button_paypal)
    RelativeLayout mPaypalButton;

    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;

    @BindView(R.id.partial_toolbar_title_view)
    TextView mTitleTextView;

    private ArrayList<Item> mItemList = new ArrayList<>();
    private ArrayList<PaymentMethod> mPaymentList = new ArrayList<>();
    private DeliveryAddress mDeliveryAddress = null;
    private ArrayList<DecorationInfoList> mDecorationInfoList = new ArrayList<>();
    private CheckoutOrderAdapter mAdapter;

    private View.OnClickListener mChangeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

//            BaseFragment fragment = null;
//
//            if (view.getId() == mAddNewAddressLayout.getId()) {
//
//                fragment = AddNewAddressFragment.newInstance(activity, mDecorationInfoList, true);
//
//            } else if (view.getId() == mAddressChangeText.getId()) {
//
//                fragment = ViewMyAddressFragment.newInstance(activity, true);
//                new ViewMyAddressPresenter(UseCaseHandler.getInstance(),
//                        (ViewMyAddressFragment)fragment,
//                        au.com.topbuy.deliveryaddressmodule.Injection.provideAddNewAddress(activity),
//                        au.com.topbuy.deliveryaddressmodule.Injection.provideGetAddresses(activity),
//                        au.com.topbuy.deliveryaddressmodule.Injection.provideApplyAddress(activity),
//                        au.com.topbuy.deliveryaddressmodule.Injection.provideDeleteUserAddress(activity));
//
//            } else if (view.getId() == mPaymentChangeText.getId()
//                    || view.getId() == mAddNewPaymentLayout.getId()) {
//
//                if (mPaymentList.size() > 1) {
//                    fragment = PaymentSelectFragment.newInstance(activity, mPaymentList, true);
//                } else {
//                    fragment = AddPaymentFragment.newInstance(activity, mPaymentList, true);
//                    mCheckoutBaseActivity.setAddPaymentFragment((AddPaymentFragment) fragment);
//                }
//
//            } else if (view.getId() == mVoucherChangeText.getId()
//                    || view.getId() == mAddNewVoucherLayout.getId()) {
//                fragment = MyVouchersFragment.newInstance(activity, new Gson().toJson(mVouchers));
//
//            }
//
//
//            activity.switchFragment(fragment);

        }
    };

    public CheckoutController() {

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_checkout, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);
        mFooterView = inflater.inflate(R.layout.partial_checkout_footer, container, false);
        ButterKnife.bind(this, mFooterView);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).hideBottomNavigationView();
        setUp(view);
    }




    @Override
    protected void setUp(View view) {

        mTitleTextView.setText(R.string.checkout_page_toolbar_title);

        mAdapter = new CheckoutOrderAdapter(getActivity(), R.layout.partial_checkout_item, mItemList, mPresenter);
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(mFooterView, null, false);

        mAddressChangeText.setOnClickListener(mChangeClickListener);
        mPaymentChangeText.setOnClickListener(mChangeClickListener);
        mVoucherChangeText.setOnClickListener(mChangeClickListener);

        mPayButton.setOnClickListener(view1 -> onPayButtonClick());
        mPaypalButton.setOnClickListener(view2 -> onPaypalButtonClick());

    }


    @Override
    public void showCartDetails(List<Item> items) {

    }

    @Override
    public void showAddressDetails(DeliveryAddress deliveryAddress, List<DecorationInfoList> decorationInfoList) {

    }

    @Override
    public void showPaymentDetails(PaymentMethod paymentMethod) {

    }

    @Override
    public void showVoucherDetails(List<Voucher> vouchers) {

    }

    @Override
    public void showSummaryDetails(Summary summary) {

    }

    @Override
    public void setPaymentList(List<PaymentMethod> paymentList) {

    }

    @Override
    public void triggerLoginTicket() {
        ((MainMvpView)getActivity()).callLoginTicket();
    }

    public void onPayButtonClick() {

//        if (!isAddressValid()) {
//            getBaseActivity().switchFragment(AddNewAddressFragment.newInstance(getBaseActivity(), mDecorationInfoList, true));
//            return;
//        }
//
//        RxBus.instance().post(GVersion.EVENT_PAY);
//
//        if (mCheckoutBaseActivity.isBraintreeInitialized()) {
//            if (mCheckoutBaseActivity.getPaymentMethodSelected() == null) {
//                AddPaymentFragment fragment = AddPaymentFragment.newInstance(activity, mPaymentList, false);
//                mCheckoutBaseActivity.setAddPaymentFragment(fragment);
//                activity.switchFragment(fragment);
//            } else {
//                mCheckoutBaseActivity.callCreatePaymentTransaction("", mCheckoutBaseActivity.getPaymentMethodSelected().getToken());
//            }
//        }
    }

    public void onPaypalButtonClick() {

//        if (!isAddressValid()) {
//            getBaseActivity().switchFragment(AddNewAddressFragment.newInstance(getBaseActivity(), mDecorationInfoList, true));
//            return;
//        }
//
//        RxBus.instance().post(GVersion.EVENT_PAY);
//
//        if (mCheckoutBaseActivity.isBraintreeInitialized()) {
//            //If no selected payment method displayed, call paypal
//            if (mCheckoutBaseActivity.getPaymentMethodSelected() == null) {
//                mCheckoutBaseActivity.startPaypalPayment();
//            } else {
//                mCheckoutBaseActivity.callCreatePaymentTransaction("", mCheckoutBaseActivity.getPaymentMethodSelected().getToken());
//            }
//        }
    }

    public void showNoCartItemsLayout() {
        hidePaymentButtons();
        mNoCartItemsLayout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void hidePaymentButtons() {
        mBraintreeLoading.setVisibility(View.VISIBLE);
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mBraintreeLoading.setVisibility(View.GONE);
        mButtonHolder.setVisibility(View.VISIBLE);

    }
}
