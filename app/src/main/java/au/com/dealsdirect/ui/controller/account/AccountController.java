package au.com.dealsdirect.ui.controller.account;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHandler;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.service.datacollection.core.DataCollector;
import au.com.dealsdirect.service.datacollection.enums.EventTypeId;
import au.com.dealsdirect.service.datacollection.enums.Events;
import au.com.dealsdirect.service.datacollection.enums.FeatureUsageEventType;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.account.adapter.AccountItemAdapter;
import au.com.dealsdirect.ui.controller.account.model.AccountItem;
import au.com.dealsdirect.ui.controller.account.model.AccountOption;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressController;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectController;
import au.com.dealsdirect.ui.controller.contact.viewcontacts.ViewContactsController;
import au.com.dealsdirect.ui.controller.country.CountryController;
import au.com.dealsdirect.ui.controller.details.DetailsController;
import au.com.dealsdirect.ui.controller.information.InformationMenuController;
import au.com.dealsdirect.ui.controller.invite.InviteSendController;
import au.com.dealsdirect.ui.controller.language.LanguageController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.controller.main.Settings;
import au.com.dealsdirect.ui.controller.notification.NotificationController;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersController;
import au.com.dealsdirect.ui.controller.returns.currentreturns.CurrentReturnsController;
import au.com.dealsdirect.ui.controller.returns.returnspolicy.ReturnsPolicyViewController;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersController;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.CommonUtils;
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
    View mLeftToolbarButton;

    @BindView(R.id.partial_toolbar_right_view)
    TextView mRightToolbarButton;

    @BindView(R.id.account_recycler_view)
    RecyclerView mAccountRecyclerView;

    @Nullable
    @BindView(R.id.account_detail_container)
    ViewGroup mAccountDetailContainer;

    @BindView(R.id.footer_content_container)
    ViewGroup mAdView;

    @BindView(R.id.rl_footer)
    RelativeLayout mAdFooter;

    @Inject
    AccountMvpPresenter<AccountMvpView> mPresenter;

    private RecyclerViewExpandableItemManager mRecyclerViewExpandableItemManager;
    private RecyclerView.LayoutManager mLayoutManager;
    private Router mAccountDetailRouter;
    private boolean mIsChangeInProgress = false;

    private ArrayList<AccountItem> mAccountItems;
    private Map<AccountItem, Integer> mAccountItemsMap = new HashMap<>();
    private boolean mIsLoginSuccessful;
    private AccountOption mChosenOption = null;

    private boolean mHasSavedInstance = false;

    private Boolean userDetailsLoggedOut = null;
    private final AuthHandler userDetailsLogoutAuthHandler = new AuthHandler() {
        @Override
        public void success() {
            setupLoginButton(false);
            userDetailsLoggedOut = true;
        }

        @Override
        public void error() {

        }
    };

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
        if (mActivity.getPackageName().equals("au.com.buyinvite.rc") ||
                mActivity.getPackageName().equals("au.com.buyinvite.test")) {
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
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);
        mIsChangeInProgress = true;
    }


    @Override
    public void onViewWillDisappear(Controller nextController) {
        super.onViewWillDisappear(nextController);
        mIsChangeInProgress = true;
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        if (mHasSavedInstance) {
            mActivity.getMainController().setSavedCurrentItem();
        }

        mIsChangeInProgress = false;
        if (mIsLoginSuccessful) {
            mPresenter.onAccountItemClick(mActivity, mChosenOption);
            mChosenOption = null;
            mIsLoginSuccessful = false;
        }
    }

    @Override
    protected void setUp(View view) {
        // Setup views here
        mActivity.setAccountController(this);

        if (mPresenter.isTablet()) {
            mAccountDetailRouter = getChildRouter(mAccountDetailContainer);
            CommonControllerChangeListener.addToRouter(mAccountDetailRouter);
        }

        createAccountItems();
        setupAccountMenu(mAccountItems);

        mTitleTextView.setText(R.string.my_account);
        mLeftToolbarButton.setVisibility(View.INVISIBLE);

        initLoginDrawable();

        if (mPresenter.isGoogleAdsEnabled()) {
            displayAds();
        }

        if (mPresenter.isTablet()) {
            mTitleTextView.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        super.onOrientationChanged(newConfiguration);

        if (mPresenter.isGoogleAdsEnabled()) {
            displayAds();
        }
    }

    private void displayAds() {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.4);

        if (mPresenter.isTablet()) {
            CommonUtils.showAdmob(mActivity, mAdView,
                    mActivity.getResources().getString(R.string.admob_account_id), width);
        } else {
            CommonUtils.showAdmob(mActivity, mAdView,
                    mActivity.getResources().getString(R.string.admob_account_id));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE, true);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHasSavedInstance = savedInstanceState.getBoolean(BundleKeys.KEY_HAS_SAVED_INSTANCE);
    }

    public void reloadAccountItems() {
        createAccountItems();
        setupAccountMenu(mAccountItems);
    }

    private void createAccountItems() {
        TypedArray titles = mActivity.getResources().obtainTypedArray(R.array.account_title_array);
        mAccountItems = new ArrayList<>();
        AccountItem newAccountItem;
        for (int i = 0; i < titles.length(); i++) {
            AccountOption option = AccountOption.getFromStringResourceId(titles.getResourceId(i, -1));

            if (option == null) {
                continue;
            }

            //skip if multi country not enabled
            if (!Settings.getIsMultiCountry() && option.equals(AccountOption.COUNTRY)) {
                continue;
            }

            //skip if multi language not enabled
            if (!mPresenter.isMultiLanguage() && option.equals(AccountOption.LANGUAGE)) {
                continue;
            }

            newAccountItem = new AccountItem(option, Collections.emptyList());
            mAccountItems.add(newAccountItem);
            mAccountItemsMap.put(newAccountItem, i);
        }
    }

    private List<AccountItem> createSubAccountItems(int resourceArrayId) {
        List<AccountItem> accountSubItems = new ArrayList<>();

        TypedArray subItemTitles = getResources().obtainTypedArray(resourceArrayId);

        for (int i = 0; i < subItemTitles.length(); i++) {
            AccountOption option = AccountOption.getFromStringResourceId(subItemTitles.getResourceId(i, -1));
            if (option != null) {
                accountSubItems.add(new AccountItem(option, Collections.emptyList()));
            }
        }

        return accountSubItems;
    }

    @Override
    public void onDetach(View view) {
        mPresenter.onDetach();

        super.onDetach(view);
    }


    @Override
    public void onTabSwitch(boolean intoThisView) {
        super.onTabSwitch(intoThisView);
        if (intoThisView) {
            if (!mActivity.isAuthorized() && mPresenter.isTablet() && mAccountItemAdapter != null) {
                int selectedPosition = mAccountItemAdapter.getSelectedPosition() == null ? 0 : mAccountItemAdapter.getSelectedPosition();
                mAccountItemAdapter.setSelectedPosition(selectedPosition);
                mPresenter.onAccountItemClick(mActivity, mAccountItemAdapter.getAccountItem(selectedPosition).getOption());
            }
        }
    }

    public void setupAccountMenu(List<AccountItem> accountItems) {
        if (!isViewBound()) {
            return;
        }

        mRecyclerViewExpandableItemManager = new RecyclerViewExpandableItemManager(null);
        mLayoutManager = new LinearLayoutManager(mActivity);

//        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();
//        animator.setSupportsChangeAnimations(false);

        mAccountItemAdapter = new AccountItemAdapter(mActivity, accountItems, mPresenter);

        mAccountRecyclerView.setAdapter(mRecyclerViewExpandableItemManager.createWrappedAdapter(mAccountItemAdapter));
        mAccountRecyclerView.setLayoutManager(mLayoutManager);
        // NOTE: need to disable change animations to ripple effect work properly
        if (mAccountRecyclerView.getItemAnimator() instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) mAccountRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        }


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

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_details)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.DETAILS_MENU);

        mActivity.addAuthHandler(userDetailsLogoutAuthHandler, false);
    }

    @Override
    public void showMyAddressesController() {

        ViewAddressController.Parameters.DisplayViewAddress parameters = new ViewAddressController.Parameters
                .DisplayViewAddress(false, null, false, "");

        ViewAddressController controller = ViewAddressController.newInstance(parameters);

        if (!mPresenter.isTablet()) {
            getDisplayRouter().pushController(RouterTransaction.with(controller)
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.VIEW_ADDRESSES, RouterTransaction.with(controller));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_addresses)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.ADDRESSES_MENU);
    }

    @Override
    public void showMyOrders() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.ORDERS, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.ORDERS, RouterTransaction.with(OrdersController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_orders)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.ORDERS_MENU);
    }

    @Override
    public void showMyVouchers() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.VIEW_VOUCHERS, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.VIEW_VOUCHERS, RouterTransaction.with(ViewVouchersController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_vouchers)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.VOUCHERS_MENU);
    }

    @Override
    public void showMyReturns() {
        if (!mPresenter.isTablet()) {
            //needed to tag this transaction
            //for future improvement, allow setting tag in gatekeeper.
            getRouter().pushController(RouterTransaction.with(CurrentReturnsController.newInstance())
                    .tag(CurrentReturnsController.class.getName())
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.CURRENT_RETURNS, RouterTransaction.with(CurrentReturnsController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_returns)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.RETURNS_MENU);
    }

    @Override
    public void showReturnsPolicy() {
        if (!mPresenter.isTablet()) {
            getRouter().pushController(RouterTransaction.with(ReturnsPolicyViewController.newInstance())
                    .tag(CurrentReturnsController.class.getName())
                    .pushChangeHandler(new HorizontalChangeHandler())
                    .popChangeHandler(new HorizontalChangeHandler()));
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.RETURNS_POLICY, RouterTransaction.with(ReturnsPolicyViewController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_returns_policy)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.PRIVACY_POLICY);
    }

    @Override
    public void showMyPaymentsController() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.PAYMENT_SELECT, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.PAYMENT_SELECT, RouterTransaction.with(PaymentSelectController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_payments)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.PAYMENTS_MENU);
    }

    @Override
    public void showLanguage() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.LANGUAGE, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.LANGUAGE, RouterTransaction.with(LanguageController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_language)));
    }

    @Override
    public void showContactUs() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.CONTACT_US, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.CONTACT_US, RouterTransaction.with(ViewContactsController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_contact_us)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.CONTACT_MENU);
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

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_invite_friend)));

        logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.INVITE_A_FRIEND_MENU);
    }

    @Override
    public void showCountry() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.COUNTRY, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.COUNTRY, RouterTransaction.with(new CountryController(false)));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_country)));
    }

    @Override
    public void showNotification() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.NOTIFICATION, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.NOTIFICATION, RouterTransaction.with(NotificationController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_notification)));
    }

    @Override
    public void showLegalities(String key, AccountOption option) {
        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, key)
                .putString(BundleKeys.LEGALITIES_TITLE, mActivity.getResources().getString(option.getTitleResourceId()))
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

        mAccountItemAdapter.setSelectedPosition(null);

        switch (option) {
            case ABOUTUS:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.ABOUT_US);
                break;
            case TERMSANDCONDITIONS:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.TERMS_AND_CONDITIONS);
                break;
            case PRIVACYPOLICY:
                logMenuSelectFeatureUsageEvent(FeatureUsageEventType.Navigations.PRIVACY_POLICY);
                break;
            default:
                break;
        }
    }

    @Override
    public void showInformationMenu() {
        if (!mPresenter.isTablet()) {
            GateKeeper.push(getDisplayRouter(), GateKeeper.Destination.INFORMATION_MENU, new HorizontalChangeHandler(), new HorizontalChangeHandler());
        } else {
            GateKeeper.setRoot(getDisplayRouter(), GateKeeper.Destination.INFORMATION_MENU, RouterTransaction.with(InformationMenuController.newInstance()));
        }

        mAccountItemAdapter.setSelectedPosition(mAccountItemsMap.get(getResources().getString(R.string.account_information)));
    }

    @Override
    public void triggerLogin(AccountOption option) {
        AccountMvpView mvpView = this;

        mActivity.showLoginController(getDisplayRouter(), new AuthHandler() {
            @Override
            public void success() {
                mIsLoginSuccessful = true;
                mChosenOption = option;
                mPresenter.onAttach(mvpView);
                mActivity.callGCMRegisterSubscriber();

                setupLoginButton(true);

                if (mPresenter.isTablet()) {
                    mActivity.getMainController().resetAccountRouter();
                }

                userDetailsLoggedOut = null;
            }

            @Override
            public void error() {
                mIsLoginSuccessful = false;
                mPresenter.onAttach(mvpView);
            }
        });
    }

    @Override
    public void triggerLogout() {
        triggerLogout(true);
    }

    @Override
    public void triggerLogout(boolean showDialog) {
        mActivity.callLogout(new AuthHandler() {
            @Override
            public void success() {
                reloadAccountItems();
                setupLoginButton(false);

                if (showDialog) {
                    CustomAlertDialog.showCustomAlertDialog(mActivity,
                            CustomAlertDialog.CustomDialogIconState.POSITIVE,
                            mActivity.getString(R.string.logout_successful));
                }
            }

            @Override
            public void error() {
                if (showDialog) {
                    CustomAlertDialog.showCustomAlertDialog(mActivity,
                            CustomAlertDialog.CustomDialogIconState.NEGATIVE,
                            mActivity.getString(R.string.logout_failed));
                }
            }
        });

    }

    @Override
    public void initLoginDrawable() {
        if (userDetailsLoggedOut == null || !userDetailsLoggedOut) {
            setupLoginButton(mPresenter.isAuthorized());
        } else {
            setupLoginButton(false);
        }

        mRightToolbarButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean isChangeInProgress() {
        return mIsChangeInProgress;
    }

    @Override
    public Router getDisplayRouter() {
        return mPresenter.isTablet() ? mAccountDetailRouter : getRouter();
    }

    @Override
    public boolean handleBack() {
        if (mAccountDetailRouter != null && mAccountDetailRouter.getBackstackSize() == 1) {
            mActivity.getMainController().showShopController();
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
                    mIsLoginSuccessful = true;
                    mPresenter.onAttach(AccountController.this);
                    mActivity.callGCMRegisterSubscriber();
                    setupLoginButton(true);
                    mActivity.getMainController().resetShopRouter();
                    mActivity.getMainController().resetCategoriesRouter();
                    mActivity.getMainController().resetAccountRouter();
                    mActivity.getMainController().resetWishlistRouter();
                    mActivity.getMainController().resetBrandsRouter();

                    userDetailsLoggedOut = null;
                }

                @Override
                public void error() {
                    mIsLoginSuccessful = false;
                }
            });
        }
    }

    private void setupLoginButton(boolean isLoggedIn) {
        if (mRightToolbarButton != null) {
            mRightToolbarButton.setText(isLoggedIn ? mActivity.getResources().getString(R.string.myaccount_log_out) : mActivity.getResources().getString(R.string.myaccount_log_in));
        }
    }

    @Override
    public int getBackstackSize() {
        return getDisplayRouter().getBackstackSize();
    }

    private void logMenuSelectFeatureUsageEvent(int featureUsageEventType) {
        FeatureUsageEventRequest featureUsageEventRequest = new FeatureUsageEventRequest();
        featureUsageEventRequest.setEventType(EventTypeId.EVENT_FEATURE_USAGE);
        featureUsageEventRequest.setFeatureInfo(new FeatureUsageEventRequest.FeatureInfo(featureUsageEventType));

        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(DataCollector.EventParameters.APP_CONTEXT, mActivity);
        eventParameters.put(DataCollector.EventParameters.SCREEN_NAME, AccountController.class.getSimpleName());
        eventParameters.put(DataCollector.EventParameters.FEATURE_EVENT_REQUEST, featureUsageEventRequest);

        DataCollector.logEvent(Events.FeatureUsageEvent, eventParameters);
    }

    @Override
    public Router getDetailRouter() {
        return mAccountDetailRouter;
    }
}

         

