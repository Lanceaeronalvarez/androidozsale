package au.com.dealsdirect.ui.controller.account;

import android.app.Activity;
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
import au.com.dealsdirect.ui.controller.account.adapter.AccountItemAdapter;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.ui.main.MainMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * dp Created by Admin on 6/6/17.
 */

public class AccountController extends BaseController implements AccountMvpView, Serializable {

    public static final String TAG = "AccountController";
    private static final String KEY_TEXT = "AccountController.KEY_TEXT";
    private AccountMvpView mAccountMvpView;
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

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_account, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);
        mAccountMvpView = this;

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
        ((MainActivity) getActivity()).getMainController().showBottomNav();
        ((MainActivity) getActivity()).setDraggableViewPager(false);

        mPresenter.loadAccountItems();
        mTitleTextView.setText("My Account");
        mArrowButton.setVisibility(View.INVISIBLE);

        initLoginDrawable();
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }

    @Override
    protected void onActivityResumed(@NonNull Activity activity) {
        super.onActivityResumed(activity);
        mPresenter.onAttach(this);
        mAccountMvpView = this;

    }

    @Override
    public void showAccountItems(List<String> accountItems, int[] accountImages) {


        accountItemAdapter = new AccountItemAdapter(accountItems, accountImages, getActivity(), mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mAccountRecyclerView.setItemAnimator(new DefaultItemAnimator());
        accountItemAdapter.notifyDataSetChanged();
    }

    @Override
    public void showMyDetailsController() {
        getRouter().pushController(RouterTransaction.with(DetailsController.newInstance())
                .pushChangeHandler(new HorizontalChangeHandler(false))
                .popChangeHandler(new HorizontalChangeHandler(false)));
    }

    @Override
    public void showMyAddressesController() {
        getRouter().pushController(RouterTransaction.with(new ViewAddressController(false, null))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }

    @Override
    public void showMyOrders() {
//        getRouter().popController(this);
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
        getRouter().pushController(RouterTransaction.with(new PaymentSelectController("", false))
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
    public void showLegalities(String key, String title) {
        getRouter().pushController(RouterTransaction.with(new LegalitiesController(key, title))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler()));
    }


    @Override
    public void triggerLogin(String option) {
        AccountMvpView mvpView = this;

        ((MainMvpView) getActivity()).showLoginController(getRouter(), new AuthHandler() {
            @Override
            public void success() {
                mPresenter.onAttach(mvpView);
                mPresenter.onAccountItemClick(option);
                ((MainActivity) getActivity()).callGCMRegisterSubscriber();
                mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_logout));
                ((MainActivity) getActivity()).getMainController().getHomeController().getPresenter().callGetBasketItemsQuantity();
                ((MainActivity) getActivity()).getMainController().getHomeController().initControllers(true);
            }

            @Override
            public void error() {
                mPresenter.onAttach(mvpView);
            }
        });
    }

    @Override
    public void triggerLogout() {
        ((MainMvpView) getActivity()).callLogout(new AuthHandler() {
            @Override
            public void success() {
                mPresenter.loadAccountItems();
                ((MainActivity) getActivity()).getMainController().getHomeController().setIsResetCheckout(true);
                ((MainActivity) getActivity()).getMainController().getHomeController().removeBasketItemCount();

                ((MainActivity) getActivity()).getMainController().getHomeController().showShopController();
                CustomAlertDialog.showCustomAlertDialog(getActivity(),
                        CustomAlertDialog.CustomDialogIconState.POSITIVE,
                        "Logout Successful");  
         }

            @Override
            public void error() {

                CustomAlertDialog.showCustomAlertDialog(getActivity(),
                        CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                        "Logout Failed");
            }
        });

    }

    @Override
    public void initLoginDrawable() {
        if(mPresenter.getIsAuthorized()) {
            mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_logout));

        } else {
            mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));
        }
    }

    @OnClick(R.id.partial_toolbar_filter_view)
    public void promptLogin(){
        if(mPresenter.getIsAuthorized()) {
            mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));
            triggerLogout();
            CartUtil.setValueToCart(0);
        } else {
            ((MainMvpView)getActivity()).showLoginController(getRouter(),new AuthHandler() {
                @Override
                public void success() {
                    mPresenter.onAttach(AccountController.this);
                    ((MainMvpView)getActivity()).callGCMRegisterSubscriber();
                    mFilterButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_logout));
                    ((MainActivity) getActivity()).getMainController().getHomeController().getPresenter().callGetBasketItemsQuantity();
                    ((MainActivity) getActivity()).getMainController().getHomeController().initControllers(true);
                }

                @Override
                public void error() {
                }
            });
        }
    }
}

         

