package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import butterknife.BindView;

/**
 * Created by smartwave on 02/11/2017.
 */

public class AccountController extends BaseController implements AccountMvpView {

    public static final String TAG = "AccountController";
    private static final String KEY_TEXT = "AccountController.KEY_TEXT";
    AccountItemAdapter accountItemAdapter;


    private int[] mAccountIcons = new int[]{
            R.drawable.ic_account_my_orders,
            R.drawable.ic_account_my_payments,
            R.drawable.ic_account_my_details,
            R.drawable.ic_account_my_addresses,
            R.drawable.ic_account_my_returns,
            R.drawable.ic_account_my_vouchers,
            R.drawable.ic_account_invite_a_friend,
            R.drawable.ic_account_language,
            R.drawable.ic_account_contact_us,
            R.drawable.ic_account_about_us,
            R.drawable.ic_account_privacy_policy,
            R.drawable.ic_account_terms_and_conditions,
            R.drawable.ic_account_tutorial,
            R.drawable.ic_account_logout
    };

    private ArrayList<String> mAccountItems = new ArrayList(Arrays.asList(
                    //"my cart",
                    AccountItems.ORDERS,
            AccountItems.PAYMENTS,
            AccountItems.DETAILS,
            AccountItems.ADDRESSES,
            AccountItems.RETURNS,
            AccountItems.VOUCHERS,
            AccountItems.INVITE_FRIEND,
            AccountItems.LANGUAGE,
            AccountItems.CONTACT_US,
            AccountItems.ABOUT_US,
            AccountItems.PRIVACY_POLICY,
            AccountItems.TNC,
            AccountItems.TUTORIAL));


    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    @Inject
    MainActivity mActivity;

    public static AccountController newInstance() {

        return new AccountController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public AccountController(Bundle args) {
        super(args);
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_account, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);
        return view;
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        mActivity.setAccountsRouter(getRouter());
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        assert (mActivity) != null;

        mPresenter.loadAccountItems(mAccountItems,mAccountIcons);
    }

    @Override
    public void showAccountItems(List<String> accountItems, int[] accountImages) {
        if(mPresenter.isAuthorized()){
            accountItems.add(AccountItems.LOGOUT);
        }else{
            if(accountItemAdapter!=null){
                accountItemAdapter.getData().remove(AccountItems.LOGOUT);
            }
        }

        accountItemAdapter = new AccountItemAdapter(accountItems, accountImages, mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity,LinearLayoutManager.VERTICAL,false));
        mAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());
        accountItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMyDetailsController() {

    }

    @Override
    public void showMyAddressesController() {

    }

    @Override
    public void showMyOrders() {
        getRouter().pushController(RouterTransaction.with(new OrdersController())
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void showMyVouchers() {
        getRouter().pushController(RouterTransaction.with(ViewVouchersController.newInstance())
                    .pushChangeHandler(new VerticalChangeHandler())
                    .popChangeHandler(new VerticalChangeHandler()));

    }

    @Override
    public void showMyReturns() {
        getRouter().pushController(RouterTransaction.with(CurrentReturnsController.newInstance())
                .tag(CurrentReturnsController.TAG)
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void showMyPaymentsController() {

    }

    @Override
    public void showLanguage() {

    }

    @Override
    public void showContactUs() {
        getRouter().pushController(RouterTransaction.with(ViewContactsController.newInstance())
                .tag(CurrentReturnsController.TAG)
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    @Override
    public void showTutorial() {

    }

    @Override
    public void showInviteAFriend() {
        getRouter().pushController(RouterTransaction.with(InviteSendController.newInstance())
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));

    }

    @Override
    public void showLegalities(String key, String Title) {

    }

    @Override
    public void triggerLogin(String option) {
        AccountMvpView mvpView = this;
        mActivity.showLoginController(getRouter(), new AuthHandler() {
            @Override
            public void success() {
                mPresenter.onAttach(mvpView);
                mPresenter.onAccountItemClick(option);
                mActivity.callGCMRegisterSubscriber();
            }

            @Override
            public void error() {
                mPresenter.onAttach(mvpView);
            }
        });
    }

    @Override
    public void triggerLogout() {
        mActivity.callLogout(new AuthHandler() {
            @Override
            public void success() {
                mPresenter.loadAccountItems(mAccountItems,mAccountIcons);
                CartUtil.setValueToCart(0);
//                mActivity.getMainController().getHomeController().removeBasketItemCount();

//                commented. in iOS when logging out, it stays on my accounts.
//                ((MainActivity) getActivity()).getMainController().getHomeController().showShopController();

                //reset routers with unique user info
//                mActivity.getMainController().getHomeController().resetRouters();
                CustomAlertDialog.showCustomAlertDialog(getActivity(),
                        CustomAlertDialog.CustomDialogIconState.POSITIVE,
                        getActivity().getString(R.string.logout_successful));
            }

            @Override
            public void error() {
                CustomAlertDialog.showCustomAlertDialog(getActivity(),
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        getActivity().getString(R.string.logout_failed));
            }
        });
    }

    @Override
    public void initLoginDrawable() {

    }
}
