package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.module.GateKeeper;
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
        view.setPadding(0, mActivity.getStatusBarHeight(), 0, 0);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        assert (mActivity) != null;
        mActivity.setDraggableViewPager(true);
        mPresenter.loadAccountItems(mAccountItems, mAccountIcons);
    }

    @Override
    public void showAccountItems(List<String> accountItems, int[] accountImages) {
        if (mPresenter.isAuthorized()) {
            accountItems.add(AccountItems.LOGOUT);
        } else {
            if (accountItemAdapter != null) {
                accountItemAdapter.getData().remove(AccountItems.LOGOUT);
            }
        }

        accountItemAdapter = new AccountItemAdapter(accountItems, accountImages, mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        mAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());
        accountItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMyDetailsController() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.DETAILS,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void showMyAddressesController() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.VIEW_ADDRESSES,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void showMyOrders() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.ORDERS,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());
    }

    @Override
    public void showMyVouchers() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.VIEW_VOUCHERS,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void showMyReturns() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.CURRENT_RETURNS,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void showMyPaymentsController() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.PAYMENT_SELECT,
                new BundleBuilder(new Bundle())
                        .putBoolean(BundleKeys.IS_FROM_CART, false)
                        .putString(BundleKeys.PAYMENT_METHODS, "")
                        .putString(BundleKeys.CART_TOTAL_COST, "")
                        .build(),
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());
    }

    @Override
    public void showLanguage() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LANGUAGE,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());
    }

    @Override
    public void showContactUs() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.CONTACT_US,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());
    }

    @Override
    public void showTutorial() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(
                getRouter(),
                GateKeeper.Destination.TUTORIAL,
                new BundleBuilder(new Bundle())
                        .putBoolean(BundleKeys.FROM_MY_ACCOUNTS, true)
                        .build(),
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void showInviteAFriend() {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.INVITE,
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void showLegalities(String key, String title) {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LEGALITIES,
                new BundleBuilder(new Bundle())
                        .putString(BundleKeys.TEMPLATE_KEY, key)
                        .putString(BundleKeys.TITLE, title)
                        .build(),
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());
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
                mPresenter.loadAccountItems(mAccountItems,mAccountIcons);
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
                mPresenter.loadAccountItems(mAccountItems, mAccountIcons);
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
