package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.login.LoginController;
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

    private boolean mChangeInProgress = false;
    private AccountMvpView mvpView;
    private int mSelectedItemFromLogin;


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
        mvpView = this;
        view.setPadding(0, mActivity.getStatusBarHeight(), 0, 0);
        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        assert (mActivity) != null;
        mActivity.setDraggableViewPager(true);
        mPresenter.loadAccountItems();

        getRouter().addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                mChangeInProgress = true;
                mAccountRecyclerView.setClickable(false);
            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                mChangeInProgress = false;
                mAccountRecyclerView.setClickable(true);
                if (from instanceof LoginController) {
                    mPresenter.onAttach(mvpView);
                    if(mPresenter.isAuthorized()) {
                        mPresenter.loadAccountItems();
                        mPresenter.onAccountItemClick(mSelectedItemFromLogin);
                    }
                }
            }
        });
    }

    @Override
    public boolean isChangeInProgress() {
        return mChangeInProgress;
    }

    @Override
    public int getBackstackSize() {
        return 0;
    }

    @Override
    public void showAccountItems(List<Integer> accountItems, List<Integer> accountImages) {
        if (mPresenter.isAuthorized()) {
            accountItems.add(R.string.account_logout);
        } else {
            if (accountItemAdapter != null) {
                accountItemAdapter.getData().remove(R.string.account_logout);
            }
        }

        accountItemAdapter = new AccountItemAdapter(mActivity, accountItems, accountImages, mPresenter);
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
    public void showCountry() {

    }

    @Override
    public void showLegalities(String key, int option) {
        mActivity.setDraggableViewPager(false);
        GateKeeper.push(getRouter(),
                GateKeeper.Destination.LEGALITIES,
                new BundleBuilder(new Bundle())
                        .putString(BundleKeys.TEMPLATE_KEY, key)
                        .putString(BundleKeys.TITLE, getResources().getString(option))
                        .build(),
                new VerticalChangeHandler(false),
                new VerticalChangeHandler());

    }

    @Override
    public void triggerLogin(int option) {
        mSelectedItemFromLogin = option;
        mActivity.showLoginController(getRouter(), new AuthHandler() {
            @Override
            public void success() {
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
                mPresenter.loadAccountItems();
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
