package au.com.dealsdirect.ui.controller.checkout.checkout;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

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
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersController;
import butterknife.BindView;

/**
 * Created by smartwave on 02/11/2017.
 */

public class CheckoutController extends BaseController implements CheckoutMvpView {

    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

    @BindView(R.id.fragment_checkout_list)
    ListView mListView;

    @BindView(R.id.no_cart_items_layout)
    RelativeLayout mNoCartItemsLayout;

    private View mButtonHolder;

    private View mFooterView;

    private RelativeLayout mAddNewVoucherLayout;

    private CheckoutOrderAdapter mAdapter;
    private ArrayList<Item> mItemList = new ArrayList<>();



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

        mButtonHolder = mFooterView.findViewById(R.id.partial_checkout_button_holder);
        mActivity.setCheckoutRouter(getRouter());
        mAdapter = new CheckoutOrderAdapter(mActivity, R.layout.partial_checkout_item, mItemList, mPresenter);
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(mFooterView, null, false);
        mAddNewVoucherLayout = mFooterView.findViewById(R.id.partial_checkout_voucher_new_code);

        mAddNewVoucherLayout.setOnClickListener(view1 -> getRouter()
                .pushController(RouterTransaction.with(AddVouchersController
                        .newInstance(AddVouchersController.testVouchersString, true, true))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler())));


        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        showNoCartItemsLayout();
    }

    @Override
    public void showMyPayDetails(Value value, Ourpay ourpay) {

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
    public void storeCartDetails(Value value) {

    }

    @Override
    public void triggerLoginTicket() {

    }

    @Override
    public void updateCheckoutBadge() {

    }

    @Override
    public boolean isCartLoading() {
        return false;
    }

    @Override
    public void setCartIsLoading(boolean val) {

    }

    private void showNoCartItemsLayout() {
        showPaymentButtons();
        mNoCartItemsLayout.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
        mPresenter.resetIsCartAlreadyLoaded();
    }

    private void hidePaymentButtons() {
        mButtonHolder.setVisibility(View.GONE);
    }

    private void showPaymentButtons() {
        mButtonHolder.setVisibility(View.VISIBLE);
    }

}
