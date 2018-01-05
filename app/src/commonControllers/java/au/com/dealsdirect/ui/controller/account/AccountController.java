package au.com.dealsdirect.ui.controller.account;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CartUtil;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountController extends BaseController implements AccountMvpView, Serializable {

    public static final String TAG = "AccountController";
    private static final String KEY_TEXT = "AccountController.KEY_TEXT";
    AccountItemAdapter accountItemAdapter;


    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_arrow_view)
    ImageButton mArrowButton;

    @BindView(R.id.partial_toolbar_filter_view)
    ImageButton mFilterButton;

    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    public static AccountController newInstance() {

        return new AccountController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public AccountController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        super.onAttach(view);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_account, container, false);

        getControllerComponent().inject(this);

        //MOCK MULTI COUNTRY in my accounts temporarily for BA
        if (getActivity().getPackageName().equals("au.com.buyinvite.rc")){
            mPresenter.setMultiCountry(true);
        }

        mPresenter.onAttach(this);
        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
        hideKeyboard();


    }


    @Override
    protected void setUp(View view) {
        // Setup views here
        assert (getActivity()) != null;
        mActivity.getMainController().showBottomNav();
        mActivity.setDraggableViewPager(false);

        mPresenter.loadAccountItems();
        mTitleTextView.setText(R.string.my_account);
        mArrowButton.setVisibility(View.INVISIBLE);

        initLoginDrawable();
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }


    @Override
    public void showAccountItems(List<Integer> accountItems, List<Integer> accountImages) {


        accountItemAdapter = new AccountItemAdapter(mActivity,accountItems, accountImages, mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());
        accountItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMyDetailsController() {
        getRouter().pushController(RouterTransaction.with(DetailsController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyAddressesController() {
        getRouter().pushController(RouterTransaction.with(new ViewAddressController(false, null))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyOrders() {
        getRouter().pushController(RouterTransaction.with(new OrdersController())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyVouchers() {
        getRouter().pushController(RouterTransaction.with(ViewVouchersController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyReturns() {
        getRouter().pushController(RouterTransaction.with(CurrentReturnsController.newInstance())
                .tag("CurrentReturnController")
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyPaymentsController() {
        getRouter().pushController(RouterTransaction.with(new PaymentSelectController("", false, ""))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showLanguage() {
        getRouter().pushController(RouterTransaction.with(LanguageController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));

    }

    @Override
    public void showContactUs() {

    }

    @Override
    public void showTutorial() {

    }

    @Override
    public void showInviteAFriend() {

    }

    public void showCountry() {
        getRouter().pushController(RouterTransaction.with(CountryController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

      @Override
    public void showLegalities(String key, int title) {

          GateKeeper.push(getRouter(),
                  GateKeeper.Destination.LEGALITIES,
                  new BundleBuilder(new Bundle())
                          .putString(BundleKeys.TEMPLATE_KEY, key)
                          .putString(BundleKeys.TITLE, getResources().getString(title))
                          .build(),
                  new HorizontalChangeHandler(false),
                  new HorizontalChangeHandler());

//
//
//          getRouter().pushController(RouterTransaction.with(new LegalitiesController(key, title))
//                .pushChangeHandler(new HorizontalChangeHandler())
//                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void triggerLogin(int option) {
        AccountMvpView mvpView = this;

        mActivity.showLoginController(getRouter(), new AuthHandler() {
            @Override
            public void success() {
                mPresenter.onAttach(mvpView);
                mPresenter.onAccountItemClick(option);
                mActivity.callGCMRegisterSubscriber();
                mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));
                mActivity.getMainController().getHomeController().resetInviteRouter();
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
                mActivity.getMainController().getHomeController().removeBasketItemCount();
                mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));

//                commented. in iOS when logging out, it stays on my accounts.
//                ((MainActivity) getActivity()).getMainController().getHomeController().showShopController();

                //reset routers with unique user info
                mActivity.getMainController().getHomeController().resetRouters();
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
        if (mPresenter.isAuthorized()) {
            mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));

        } else {
            mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));
        }
    }

    @Override
    public boolean isChangeInProgress() {
        return false;
    }

    @OnClick(R.id.partial_toolbar_filter_view)
    public void promptLogin() {
        if (mPresenter.isAuthorized()) {
            triggerLogout();
        } else {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mPresenter.onAttach(AccountController.this);
                    mActivity.callGCMRegisterSubscriber();
                    mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));
                    mActivity.getMainController().getHomeController().initControllers(true);
                }

                @Override
                public void error() {
                }
            });
        }
    }

    @Override
    public int getBackstackSize() {
        return getRouter().getBackstackSize();
    }
}

         

