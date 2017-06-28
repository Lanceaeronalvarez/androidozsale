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
import au.com.dealsdirect.ui.main.MainActivity;
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
    View mBraintreeLoading;
    View mButtonHolder;
    Button mPayButton;
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
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).hideBottomNavigationView();


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

        mBraintreeLoading = mFooterView.findViewById(R.id.partial_checkout_bt_loading);
        mButtonHolder = mFooterView.findViewById(R.id.partial_checkout_button_holder);
        mPayButton = (Button) mFooterView.findViewById(R.id.partial_checkout_button_pay);
        mPaypalButton = (RelativeLayout) mFooterView.findViewById(R.id.partial_checkout_button_paypal);


        setUp(view);
    }


    @Override
    public void onDetach(View view) {
        super.onDetach(view);

        assert (getActivity()) != null;
        ((BaseActivity) getActivity()).showBottomNavigationView();
    }

    @Override
    protected void setUp(View view) {

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

        loadCart();

    }

    private void loadCart(){
        if(mPresenter.checkIsLoggedIn() && !((MainActivity)getActivity()).isBraintreeInitialized()){
            ((MainActivity)getActivity()).fetchAuthorization(new FetchTokenHandler() {
                @Override
                public void onSuccess() {
                    mPresenter.start();
                }

                @Override
                public void onFailure() {
                    hidePaymentButtons();
                }
            });
        }else if (mPresenter.checkIsLoggedIn() && ((MainActivity)getActivity()).isBraintreeInitialized()) {

            mPresenter.start();

        } else {


            showNoCartItemsLayout();

        }
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
