package au.com.dealsdirect.ui.controller.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.main.MainActivity;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.CartUtil;
import butterknife.BindView;

/**
 * dp Created by Admin on 6/6/17.
 */

public class HomeController extends BaseController implements HomeMvpView {

    public static final String TAG = "HomeController";

    private static final String KEY_TEXT = "HomeController.KEY_TEXT";

    @Inject
    HomeMvpPresenter<HomeMvpView> mPresenter;

    @BindView(R.id.product_details_add_to_cart)
    ImageView mImageAddToCartAnimation;

    @BindView(R.id.controller_home_frame)
    ViewGroup mShopContainer;

    @BindView(R.id.controller_accounts_frame)
    ViewGroup mAccountsContainer;

    @BindView(R.id.controller_contacts_frame)
    ViewGroup mContactsContainer;

    @BindView(R.id.controller_checkout_frame)
    ViewGroup mCheckoutContainer;

    @BindView(R.id.controller_invites_frame)
    ViewGroup mInvitesContainer;

    public AHBottomNavigation getBottomNavigationView() {
        return mBottomNavigationView;
    }

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    private View mRoot;
    private Router mShopRouter;
    private Router mAccountsRouter;
    private Router mContactsRouter;
    private Router mInvitesRouter;
    private Router mCheckoutRouter;
    private List<Router> mRouterList;
    private List<ViewGroup> mContainersList;
    private MainActivity mActivity;

    public Router getAccountsRouter() {
        return mAccountsRouter;
    }

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;
    private int currentVisibleIndex = 1;
    private int previousVisibleIndex = 0;

    public boolean mIsResetCart = true;

    private boolean isLoginVisible = false;

    public static HomeController newInstance() {

        return new HomeController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public HomeController(Bundle args) {
        super(args);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        mPresenter.onAttach(this);
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
        mActivity = (MainActivity) getActivity();
        mRoot = view;

//        mShopRouter = Conductor.attachRouter(getActivity(), mShopContainer, null);
//        mAccountsRouter = Conductor.attachRouter(getActivity(), mAccountsContainer, null);
//        mContactsRouter = Conductor.attachRouter(getActivity(), mContactsContainer, null);
//        mInvitesRouter = Conductor.attachRouter(getActivity(), mInvitesContainer, null);
//        mCheckoutRouter = Conductor.attachRouter(getActivity(), mCheckoutContainer, null);

        mShopRouter = getChildRouter(mShopContainer);
        if (!mShopRouter.hasRootController()) {
            ShopsController shopsController = new ShopsController();
            mActivity.setShopController(shopsController);
            mShopRouter.setRoot(RouterTransaction.with(shopsController)
                    .tag(ShopsController.TAG));
        }

        mAccountsRouter = getChildRouter(mAccountsContainer);

        if (!mAccountsRouter.hasRootController()) {
            mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
            mActivity.setAccountsRouter(mAccountsRouter);
        }

        mContactsRouter = getChildRouter(mContactsContainer);

        if (!mContactsRouter.hasRootController()) {
            mContactsRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                    .tag(ViewContactsController.TAG));
        }

        mInvitesRouter = getChildRouter(mInvitesContainer);

        if (!mInvitesRouter.hasRootController()) {
            mInvitesRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                    .tag(getActivity().getResources().getString(R.string.invite_friends_tag)));
        }

        mCheckoutRouter = getChildRouter(mCheckoutContainer);

        if (!mCheckoutRouter.hasRootController()) {
            mCheckoutRouter.setRoot(RouterTransaction.with(new CheckoutController())
                    .tag(getActivity().getResources().getString(R.string.checkout_controller)));
            ((MainActivity) getActivity()).setCheckoutRouter(mCheckoutRouter);
        }

        mContainersList = new ArrayList<>();
        mContainersList.add(mShopContainer);
        mContainersList.add(mAccountsContainer);
        mContainersList.add(mContactsContainer);
        mContainersList.add(mInvitesContainer);
        mContainersList.add(mCheckoutContainer);
        setVisibleContainer(0);

        mRouterList = new ArrayList<>();
        mRouterList.add(mShopRouter);
        mRouterList.add(mAccountsRouter);
        mRouterList.add(mContactsRouter);
        mRouterList.add(mInvitesRouter);
        mRouterList.add(mCheckoutRouter);

        mActivity.setHomeRouter(mShopRouter);
        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(getActivity(), R.menu.bottom_navigation_menu);
        navigationAdapter.setupWithBottomNavigation(mBottomNavigationView);
        mBottomNavigationView.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        mBottomNavigationView.setCurrentItem(0);
        setUp(view);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    public HomeMvpPresenter<HomeMvpView> getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setUp(View view) {
        if (mPresenter.isAuthorized()) {
            mPresenter.callGetBasketItemsQuantity();
        }

        mShopRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if (mBottomNavigationView == null) return;

                if (to instanceof HomeController || to instanceof ShopsController) {
                    mBottomNavigationView.setCurrentItem(0);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setCurrentItem(1);
                } else if (to instanceof ViewContactsController) {
                    mBottomNavigationView.setCurrentItem(2);
                } else if (to instanceof InviteSendController) {
                    mBottomNavigationView.setCurrentItem(3);
                } else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setCurrentItem(4);
                }
            }
        });

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {

            if (mCheckoutRouter!=null){
                Controller controller = getCurrentControllerOnRouter(mCheckoutRouter);
                if (controller instanceof CheckoutController) {
                    ((CheckoutController) controller).removeOurpayView();
                }
            }

            if (isAttached())
                mActivity.setIsFromCategories(false);

            if (!wasSelected) {

                Controller checkoutController = getCurrentControllerOnRouter(mCheckoutRouter);
                if (position != 4 && checkoutController instanceof PaymentSuccessController) {
                    mCheckoutRouter.popToRoot();
                }

                switch (position) {
                    case 0:
                        showShopController();
                        break;

                    case 1:
                        mActivity.setDraggableViewPager(false);
                        showAccountController();
                        break;

                    case 2:
                    case 3:
                    case 4:
                        mActivity.setDraggableViewPager(false);
                        proceedToController(position);

                        break;
                }
            } else {
                if (position == 0) {
                    mShopRouter.popToRoot();
                } else if (position == 1) {
                    mAccountsRouter.popToRoot();
                }
            }
            return true;
        });

    }

    public void initControllers(boolean includeShop) {
        if (includeShop) {
            ShopsController shopsController = new ShopsController();
            mActivity.setShopController(shopsController);
            mShopRouter.setRoot(RouterTransaction.with(shopsController).tag(ShopsController.TAG));
        }

        mAccountsRouter = getChildRouter(mAccountsContainer);
        mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
        mActivity.setAccountsRouter(mAccountsRouter);

        mContactsRouter = getChildRouter(mContactsContainer);
        mContactsRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                .tag(ViewContactsController.TAG));

        mInvitesRouter = getChildRouter(mInvitesContainer);
        mInvitesRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                .tag(getActivity().getResources().getString(R.string.invite_friends_tag)));

        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mCheckoutRouter.setRoot(RouterTransaction.with(new CheckoutController())
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    public void resetInviteRouter() {
        mInvitesRouter = getChildRouter(mInvitesContainer);
        mInvitesRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                .tag(getActivity().getResources().getString(R.string.invite_friends_tag)));

    }

    public void resetContactsRouter() {
        mContactsRouter = getChildRouter(mContactsContainer);
        mContactsRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                .tag(getActivity().getResources().getString(R.string.view_contacts_controller)));

    }

    public void resetCheckoutRouter() {
        mCheckoutRouter = getChildRouter(mCheckoutContainer);
        mCheckoutRouter.setRoot(RouterTransaction.with(new CheckoutController())
                .tag(getActivity().getResources().getString(R.string.checkout_controller)));
    }

    @Override
    public void showShopController() {
        if (getActivity()!=null)
            ((MainActivity)getActivity()).getMainController().setViewpagerDraggable(true);
        setVisibleContainer(0);
    }

    @Override
    public void showAccountController() {
        setVisibleContainer(1);
        int size = mAccountsRouter.getBackstack().size();
        if (mAccountsRouter.getBackstack().get(size - 1).controller() instanceof AccountMvpView)
            ((AccountMvpView) mAccountsRouter.getBackstack().get(size - 1).controller()).initLoginDrawable();
    }

    @Override
    public void showContactController() {
        setVisibleContainer(2);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mContactsRouter, new AuthHandler() {
                @Override
                public void success() {
                    resetContactsRouter();
                }

                @Override
                public void error() {

                }
            });
        }
    }

    @Override
    public void showInviteController() {
        setVisibleContainer(3);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mInvitesRouter, new AuthHandler() {
                @Override
                public void success() {
                    resetInviteRouter();
                }

                @Override
                public void error() {

                }
            });
        }
    }

    @Override
    public void showCheckoutController() {
        setVisibleContainer(4);

        if (!mActivity.isAuthorized()) {
            mActivity.showLoginController(mCheckoutRouter, new AuthHandler() {
                @Override
                public void success() {
//                                    initControllers(true);
                    resetInviteRouter();
                }

                @Override
                public void error() {

                }
            });
        } else {
            Controller controller = getCurrentControllerOnRouter(mCheckoutRouter);
            if (controller instanceof CheckoutController) {

                if ((getActivity()) != null)
                    ((MainActivity) getActivity()).getMainController().getHomeController().setIsResetCheckout(true);
                ((CheckoutController) controller).loadCart();
            }
        }
    }

    public boolean isAccountsActive() {
        return mAccountsContainer != null && mAccountsContainer.isShown();
    }

    public boolean isShopActive() {
        return mShopContainer != null && mShopContainer.isShown();
    }

    @Override
    public void updateBasketItemCount() {
        if (CartUtil.getCartValue() == 0) {
            removeBasketItemCount();
        } else {
            AHNotification notification = new AHNotification.Builder()
                    .setText(CartUtil.getCartValue() + "")
                    .setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark))
                    .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                    .build();
            getBottomNavigationView().setNotification(notification, 4);
        }
    }

    public void removeBasketItemCount() {
        getBottomNavigationView().setNotification("", 4);
    }

    private void proceedToController(int id) {
        if (id == 2) {
            showContactController();
        } else if (id == 3) {
            showInviteController();
        } else if (id == 4) {
            showCheckoutController();
        }
    }

    public void hideBottomNav() {
        if (mBottomNavigationView != null)
            mBottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNav() {
        if (mBottomNavigationView != null && mBottomNavigationView.getVisibility() == View.GONE) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.bringToFront();
        }
    }

    public void setVisibleContainer(int i) {
        mContainersList.get(currentVisibleIndex).setVisibility(View.GONE);
        previousVisibleIndex = currentVisibleIndex;
        mContainersList.get(i).setVisibility(View.VISIBLE);
        mBottomNavigationView.setCurrentItem(i, false);
        currentVisibleIndex = i;
    }

    public void resetVisibleContainer() {
        if (!(getCurrentRouter() == mAccountsRouter || getCurrentRouter() == mShopRouter)) {
            mContainersList.get(currentVisibleIndex).setVisibility(View.GONE);
            mContainersList.get(previousVisibleIndex).setVisibility(View.VISIBLE);
            mBottomNavigationView.setCurrentItem(previousVisibleIndex, false);
            currentVisibleIndex = previousVisibleIndex;
        }
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
    }

    public Router getCurrentRouter() {
        return mRouterList.get(currentVisibleIndex);
    }

    public Controller getCurrentControllerOnRouter(Router router) {
        int topIndex = router.getBackstackSize() - 1;
        if (topIndex >= 0) {
            return router.getBackstack().get(topIndex).controller();
        }

        return null;
    }

    public boolean getIsResetCheckout() {
        return mIsResetCart;
    }

    public void setIsResetCheckout(boolean resetCheckout) {
        mIsResetCart = resetCheckout;
    }

    public ImageView getAddToCartImage() {
        return mImageAddToCartAnimation;
    }

    public Router getCheckoutRouter() {
        return mCheckoutRouter;
    }
}
