package au.com.dealsdirect.ui.controller.account;

import android.content.res.TypedArray;
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
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.adapter.AccountItemAdapter;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
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
    AccountItemAdapter accountItemAdapter;


    @BindView(R.id.partial_toolbar_arrow_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mLeftToolbarButton;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mRightToolbarButton;

    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    private ArrayList<Integer> titles;
    private ArrayList<Integer> drawables;

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
        if (getActivity().getPackageName().equals("au.com.buyinvite.rc") ||
                getActivity().getPackageName().equals("au.com.buyinvite.test") ){

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


        TypedArray title = mActivity.getResources().obtainTypedArray(R.array.account_title_array);
        titles = new ArrayList<>();
        for (int i = 0; i < title.length(); i++) {
            titles.add(title.getResourceId(i, 0));
        }
        TypedArray drawable = mActivity.getResources().obtainTypedArray(R.array.account_drawable_array);
        drawables = new ArrayList<>();
        for (int i = 0; i < drawable.length(); i++) {
            drawables.add(drawable.getResourceId(i, 0));
        }
        mPresenter.loadAccountItems(titles, drawables);

        mTitleTextView.setText(R.string.my_account);
        mLeftToolbarButton.setVisibility(View.INVISIBLE);

        initLoginDrawable();
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }


    @Override
    public void showAccountItems(List<Integer> accountItems, List<Integer> accountImages) {
        accountItemAdapter = new AccountItemAdapter(mActivity, accountItems, accountImages, mPresenter);
        mAccountRecyclerView.setAdapter(accountItemAdapter);
        mAccountRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResource().getInteger(R.integer.account_column_count)));
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
        if (getResources().getBoolean(R.bool.is_account_contact_visible)) {
            getRouter().pushController(RouterTransaction.with(ViewContactsController.newInstance())
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
    }

    @Override
    public void showTutorial() {

    }

    @Override
    public void showInviteAFriend() {
        if (getResources().getBoolean(R.bool.is_account_invite_visible)) {
            getRouter().pushController(RouterTransaction.with(InviteSendController.newInstance())
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        }
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
                mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));
                mActivity.getMainController().getHomeController().resetRouters();
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
                mPresenter.loadAccountItems(titles, drawables);
                CartUtil.setValueToCart(0);
                mActivity.getMainController().getHomeController().removeBasketItemCount();
                mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));

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
            mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));

        } else {
            mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));
        }
    }

    @Override
    public boolean isChangeInProgress() {
        return false;
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void promptLogin() {
        if (mPresenter.isAuthorized()) {
            triggerLogout();
        } else {
            mActivity.showLoginController(getRouter(), new AuthHandler() {
                @Override
                public void success() {
                    mPresenter.onAttach(AccountController.this);
                    mActivity.callGCMRegisterSubscriber();
                    mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));
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

         

