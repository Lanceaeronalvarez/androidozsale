package au.com.dealsdirect.ui.controller.checkout;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.List;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.DeliveryAddress;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Item;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Summary;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Voucher;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.main.FetchTokenHandler;

import javax.inject.Inject;


/**
 * dp Created by Admin on 6/6/17.
 */

public class CheckoutController extends BaseController implements CheckoutMvpView {
    @Inject
    CheckoutMvpPresenter<CheckoutMvpView> mPresenter;

//    private final RouterPagerAdapter pagerAdapter = new RouterPagerAdapter(this) {
//        @Override
//        public void configureRouter(@NonNull Router router, int position) {
//            if (!router.hasRootController()) {
//
//                if (position==0){
//                    Controller firstView = CategoriesController.newInstance();
//                    router.setRoot(RouterTransaction.with(firstView));
//
//                }if (position==1){
//                    Controller page = new ShopsController();
//                    router.setRoot(RouterTransaction.with(page));
//                }
//
//            }
//        }
//
//        @Override
//        public int getCount() {
//            return PAGE_COLORS.length;
//        }
//
//        @Override
//        public CharSequence getPageTitle(int position) {
//            return "Page " + position;
//        }
//    };

    public CheckoutController() {

    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {

        View view = inflater.inflate(R.layout.controller_home, container, false);
        getControllerComponent().inject(this);
        mPresenter.onAttach(this);

        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
    }




    @Override
    protected void setUp(View view) {

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
}
