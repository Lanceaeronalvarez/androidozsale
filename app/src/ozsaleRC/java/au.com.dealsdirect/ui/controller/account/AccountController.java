package au.com.dealsdirect.ui.controller.account;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.adapter.AccountItemAdapter;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;
import au.com.dealsdirect.ui.controller.account.model.AccountSubItem;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
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
    AccountItemAdapter mAccountItemAdapter;


    @BindView(R.id.partial_toolbar_title)
    TextView mTitleTextView;

    @BindView(R.id.partial_toolbar_left_view)
    ImageButton mLeftToolbarButton;

    @BindView(R.id.partial_toolbar_right_view)
    ImageButton mRightToolbarButton;

    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Nullable @BindView(R.id.account_detail_container)
    ViewGroup mAccountDetailContainer;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerView.LayoutManager mLayoutManager;
    private Router mAccountDetailRouter;
    private String mDefaultChosenAccountOption = "";
    private int mDefaultChosenAccountOptionPos = 0;

    private ArrayList<AccountItem> mAccountItems;

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
                getActivity().getPackageName().equals("au.com.buyinvite.test")) {
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
        mDefaultChosenAccountOption = getString(R.string.account_details);

        if (mPresenter.isTablet()) {
            mAccountDetailRouter = getChildRouter(mAccountDetailContainer);
        }

        mActivity.getMainController().showBottomNav();
        mActivity.setDraggableViewPager(false);

        createAccountItems();

        mPresenter.loadAccountItems(mAccountItems);

        mTitleTextView.setText(R.string.my_account);
        mLeftToolbarButton.setVisibility(View.INVISIBLE);

        initLoginDrawable();
    }

    private void createAccountItems() {
        TypedArray titles = mActivity.getResources().obtainTypedArray(R.array.account_title_array);
        mAccountItems = new ArrayList<>();
        AccountItem newAccountItem;
        for (int i = 0; i < titles.length(); i++) {
            String title = getString(titles.getResourceId(i, 0));

            //skip if multi country not enabled
            if (!mPresenter.isMultiCountry() && title.equals(getString(R.string.account_country))) {
                continue;
            }

            //skip if multi language not enabled
            if (!mPresenter.isMultiLanguage() && title.equals(getString(R.string.account_language))) {
                continue;
            }

            if (title.equals(getString(R.string.account_options))) {
                newAccountItem = new AccountItem(i, title, createSubAccountItems(R.array.account_options_sub_item_title_array));
            } else {
                newAccountItem = new AccountItem(i, title, Collections.emptyList());
            }
            mAccountItems.add(newAccountItem);
        }


//        TypedArray drawable = mActivity.getResources().obtainTypedArray(R.array.account_drawable_array);
//        drawables = new ArrayList<>();
//        for (int i = 0; i < drawable.length(); i++) {
//            drawables.add(drawable.getResourceId(i, 0));
//        }
    }

    private List<AccountSubItem> createSubAccountItems(int resourceArrayId) {
        List<AccountSubItem> accountSubItems = new ArrayList<>();

        TypedArray subItemTitles = getResources().obtainTypedArray(resourceArrayId);

        for (int i = 0; i < subItemTitles.length(); i++) {
            String title = getString(subItemTitles.getResourceId(i, 0));
            accountSubItems.add(new AccountSubItem(i, title));
        }

        return accountSubItems;
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();
        super.onDetach(view);
    }


    @Override
    public void showAccountItems(List<AccountItem> accountItems) {
        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
        mLayoutManager = new LinearLayoutManager(mActivity);

//        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
//        animator.setSupportsChangeAnimations(false);

        if (!getBoolean(R.bool.is_tablet)) {
            mAccountItemAdapter = new AccountItemAdapter(mActivity, accountItems, mPresenter);
        } else {
            mAccountItemAdapter = new AccountItemAdapter(mActivity, accountItems, mPresenter, true);
        }
        mAccountRecyclerView.setAdapter(mRecyclerViewExpandableItemManager.createWrappedAdapter(mAccountItemAdapter));
        mAccountRecyclerView.setLayoutManager(mLayoutManager);
        // NOTE: need to disable change animations to ripple effect work properly
        ((SimpleItemAnimator) mAccountRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);


        mRecyclerViewExpandableItemManager.attachRecyclerView(mAccountRecyclerView);
    }

    @Override
    public void showMyDetailsController() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.DETAILS, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.DETAILS, RouterTransaction.with(DetailsController.newInstance()).pushChangeHandler(new FadeChangeHandler())
                    .popChangeHandler(new FadeChangeHandler()));
        }
    }

    @Override
    public void showMyAddressesController() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.VIEW_ADDRESSES, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.VIEW_ADDRESSES, RouterTransaction.with(new ViewAddressController(false, null)));
        }
    }

    @Override
    public void showMyOrders() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.ORDERS, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.ORDERS, RouterTransaction.with(OrdersController.newInstance()));
        }
    }

    @Override
    public void showMyVouchers() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.VIEW_VOUCHERS, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.VIEW_VOUCHERS, RouterTransaction.with(ViewVouchersController.newInstance()));
        }
    }

    @Override
    public void showMyReturns() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.CURRENT_RETURNS, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.CURRENT_RETURNS, RouterTransaction.with(CurrentReturnsController.newInstance()));
        }
    }

    @Override
    public void showMyPaymentsController() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.PAYMENT_SELECT, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.PAYMENT_SELECT, RouterTransaction.with(PaymentSelectController.newInstance()));
        }
    }

    @Override
    public void showLanguage() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.LANGUAGE, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.LANGUAGE, RouterTransaction.with(LanguageController.newInstance()));
        }
    }

    @Override
    public void showContactUs() {
        if (getResources().getBoolean(R.bool.is_account_contact_visible)) {
            if (!mPresenter.isTablet()) {
                GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.CONTACT_US, new HorizontalChangeHandler(), new HorizontalChangeHandler());
            } else {
                GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.CONTACT_US, RouterTransaction.with(LanguageController.newInstance()));
            }
        }
    }

    @Override
    public void showTutorial() {

    }

    @Override
    public void showInviteAFriend() {
        if (getResources().getBoolean(R.bool.is_account_invite_visible)) {
            if (!mPresenter.isTablet()) {
                GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.INVITE, new HorizontalChangeHandler(), new HorizontalChangeHandler());
            } else {
                GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.INVITE, RouterTransaction.with(InviteSendController.newInstance()));
            }
        }
    }

    public void showCountry() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.COUNTRY, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.COUNTRY, RouterTransaction.with(CountryController.newInstance()));
        }
    }

    @Override
    public void showLegalities(String key, String title) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, key)
                .putString(BundleKeys.LEGALITIES_TITLE, title)
                .build();
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(),
                    GateKeeper.Destination.LEGALITIES,
                    bundle,
                    new HorizontalChangeHandler(false),
                    new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.LEGALITIES, RouterTransaction.with(new LegalitiesController(bundle)));
        }
    }

    @Override
    public void triggerLogin(String option, int position) {
        AccountMvpView mvpView = this;

        mActivity.showLoginController(getDisplayRouter(), new AuthHandler() {
            @Override
            public void success() {
                mPresenter.onAttach(mvpView);
                mActivity.callGCMRegisterSubscriber();
                if(mRightToolbarButton != null) {
                    mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_account_logout));
                }
                mActivity.getMainController().getHomeController().resetRouters();
                if(mPresenter.isTablet()){
                    mActivity.getMainController().getHomeController().resetAccountRouter();
                } else {
                    mPresenter.onAccountItemClick(mActivity,option,position);
                }
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
                mPresenter.loadAccountItems(mAccountItems);
                CartUtil.setValueToCart(0);
                mActivity.getMainController().getHomeController().removeBasketItemCount();
                mRightToolbarButton.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_login));

                //reset routers with unique user info
                mActivity.getMainController().getHomeController().resetRouters();
                if(mPresenter.isTablet()){
                    mActivity.setShopsAsVisibleContainer();
                }
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

        mRightToolbarButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isChangeInProgress() {
        return false;
    }

    @Override
    public Router getDisplayRouter() {
        return mPresenter.isTablet() ? mAccountDetailRouter : getRouter();
    }

    @Override
    public boolean handleBack() {
        if(mAccountDetailRouter != null && mAccountDetailRouter.getBackstackSize() == 1){
            mActivity.getHomeController().resetVisibleContainer();
            return true;
        }

        return super.handleBack();
    }

    @OnClick(R.id.partial_toolbar_right_view)
    public void promptLogin() {
        if (mPresenter.isAuthorized()) {
            triggerLogout();
        } else {
            mActivity.showLoginController(getDisplayRouter(), new AuthHandler() {
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
        return getDisplayRouter().getBackstackSize();
    }

}

         

