package au.com.dealsdirect.ui.controller.home;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.AccountController;
import au.com.dealsdirect.ui.controller.account.AccountMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.invite.InviteController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.login.LoginController;
import au.com.dealsdirect.ui.controller.shops.ShopsController;
import au.com.dealsdirect.ui.custom.transitions.SimpleChangeHandler;
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

    @BindView(R.id.controller_home_frame)
    ViewGroup mContainer;

    @BindView(R.id.controller_accounts_frame)
    ViewGroup mAccountsContainer;

    public AHBottomNavigation getBottomNavigationView() {
        return mBottomNavigationView;
    }

    @BindView(R.id.controller_home_bottom_nav)
    AHBottomNavigation mBottomNavigationView;

    private Router mRouter;
    private Router mAccountsRouter;

    public Router getAccountsRouter() {
        return mAccountsRouter;
    }

    private int mPreviousTab = R.id.action_shop;
    private int mCurrentTab = R.id.action_shop;


    private int mBottomNavItemSelectCounter = 0;

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

        mAccountsRouter = Conductor.attachRouter(getActivity(), mAccountsContainer, null);
        mRouter = Conductor.attachRouter(getActivity(), mContainer, null);

        if (!mRouter.hasRootController()) {
            ShopsController shopsController = new ShopsController();
            ((MainActivity) getActivity()).setShopController(shopsController);
            mRouter.setRoot(RouterTransaction.with(shopsController)
                    .tag(ShopsController.TAG));
        }

        mAccountsRouter = getChildRouter(mAccountsContainer);

        if(!mAccountsRouter.hasRootController()){
            mAccountsRouter.setRoot(RouterTransaction.with(AccountController.newInstance()));
            ((MainActivity) getActivity()).setAccountsRouter(mAccountsRouter);
        }


        ((MainActivity) getActivity()).setHomeRouter(mRouter);
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

    @Override
    protected void setUp(View view) {
        if(mPresenter.isAuthorized()) {
            mPresenter.callGetBasketItemsQuantity();
        }else{
            updateBasketItemCount();
        }

        mRouter.addChangeListener(new ControllerChangeHandler.ControllerChangeListener() {
            @Override
            public void onChangeStarted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {

            }

            @Override
            public void onChangeCompleted(@Nullable Controller to, @Nullable Controller from, boolean isPush, @NonNull ViewGroup container, @NonNull ControllerChangeHandler handler) {
                if(mBottomNavigationView == null) return;

                if (to instanceof HomeController || to instanceof ShopsController) {
                    mBottomNavigationView.setCurrentItem(0);
                } else if (to instanceof AccountController) {
                    mBottomNavigationView.setCurrentItem(1);
                } else if (to instanceof ViewContactsController) {
                    mBottomNavigationView.setCurrentItem(2);
                } else if (to instanceof InviteController) {
                    mBottomNavigationView.setCurrentItem(3);
                } else if (to instanceof CheckoutController) {
                    mBottomNavigationView.setCurrentItem(4);
                }
            }
        });

        mBottomNavigationView.setOnTabSelectedListener((position, wasSelected) -> {
            mBottomNavItemSelectCounter++;
            if (isAttached())
                ((MainActivity) getActivity()).setIsFromCategories(false);

            if (!wasSelected) {

                switch (position) {
                    case 0:
                        mBottomNavItemSelectCounter = 0;
                        showShopController();
                        break;

                    case 1:
                        mBottomNavItemSelectCounter = 0;
                        showAccountController();
                        break;

                    case 2:
                    case 3:
                        mBottomNavItemSelectCounter = 0;
                        showHomeContainer();
                        if (!((MainActivity) getActivity()).isAuthorized()) {
                            new Handler().postDelayed(()-> {
                                showLoginController(mRouter, new AuthHandler() {
                                    @Override
                                    public void success() {
                                        proceedToController(position);
                                    }

                                    @Override
                                    public void error() {

                                    }
                                });
                            },500);
                        } else {
                            proceedToController(position);
                        }
                        break;

                    case 4:
                        mBottomNavItemSelectCounter = 0;
                        showCheckoutController();
                        break;
                }
            } else {
                if(position == 0){
                    mRouter.popToRoot();
                }
            }
            return true;
        });

    }

    @Override
    public void showShopController() {
        showHomeContainer();
        mRouter.setRoot(RouterTransaction.with(new ShopsController())
                .tag(getActivity().getResources().getString(R.string.search_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new SimpleChangeHandler()));
    }

    @Override
    public void showAccountController() {
//        mRouter.setRoot(RouterTransaction.with(AccountController.newInstance())
//                .tag(AccountController.TAG)
//                .pushChangeHandler(new SimpleChangeHandler())
//                .popChangeHandler(new SimpleChangeHandler()));
        TransitionManager.beginDelayedTransition(mContainer,new Fade(Fade.OUT));
        TransitionManager.beginDelayedTransition(mAccountsContainer,new Fade(Fade.IN));
        mAccountsContainer.setVisibility(View.VISIBLE);
        int size = mAccountsRouter.getBackstack().size();
        if(mAccountsRouter.getBackstack().get(size - 1).controller() instanceof AccountMvpView)
            ((AccountMvpView) mAccountsRouter.getBackstack().get(size - 1).controller()).initLoginDrawable();
        mContainer.setVisibility(View.GONE);
    }

    @Override
    public void showContactController() {
        mRouter.setRoot(RouterTransaction.with(ViewContactsController.newInstance())
                .tag(ViewContactsController.TAG)
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new SimpleChangeHandler()));
    }

    @Override
    public void showInviteController() {
        mRouter.setRoot(RouterTransaction.with(InviteSendController.newInstance())
                .tag(getActivity().getResources().getString(R.string.invite_friends_tag))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new SimpleChangeHandler()));
    }

    @Override
    public void showCheckoutController() {
        showHomeContainer();
        mRouter.setRoot(RouterTransaction.with(new CheckoutController())
                .tag(getActivity().getResources().getString(R.string.checkout_controller))
                .pushChangeHandler(new SimpleChangeHandler())
                .popChangeHandler(new SimpleChangeHandler()));
    }

    @Override
    public void showLoginController(Router router, AuthHandler handler) {
        mRouter.pushController(RouterTransaction.with(LoginController.newInstance(handler))
                .pushChangeHandler(new VerticalChangeHandler())
                .popChangeHandler(new VerticalChangeHandler()));
    }

    private void showHomeContainer(){
        TransitionManager.beginDelayedTransition(mContainer,new Fade(Fade.IN).setDuration(200));
        TransitionManager.beginDelayedTransition(mAccountsContainer,new Fade(Fade.OUT).setStartDelay(200));
        mAccountsContainer.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);
    }

    public boolean isAccountsActive(){
        return mAccountsContainer != null && mAccountsContainer.isShown();
    }


    @Override
    public void updateBasketItemCount() {
        AHNotification notification = new AHNotification.Builder()
                .setText(CartUtil.getCartValue() + "")
                .setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_dark))
                .setTextColor(ContextCompat.getColor(getActivity(), R.color.white))
                .build();
        getBottomNavigationView().setNotification(notification, 4);
    }

    private void proceedToController(int id) {
        if (id == 2) {
            showContactController();
        } else if (id == 3) {
            showInviteController();
        }
    }

    public void hideBottomNav() {
        if (mBottomNavigationView != null)
            mBottomNavigationView.setVisibility(View.GONE);
    }

    public void showBottomNav() {
        if (mBottomNavigationView != null) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
            mBottomNavigationView.bringToFront();
        }
    }
}
