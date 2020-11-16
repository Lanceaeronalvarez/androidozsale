package au.com.dealsdirect.ui.controller.ourpay;

import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesController;
import au.com.dealsdirect.ui.controller.ourpay.MyAccountsOurpayDataSource.Item.PaymentPlan;
import au.com.dealsdirect.ui.controller.ourpay.MyAccountsOurpayDataSource.Item.ScheduledPayment;
import au.com.dealsdirect.ui.custom.CustomAlertDialog;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.BundleKeys;
import au.com.dealsdirect.utils.ViewUtils;
import au.com.dealsdirect.utils.module.ControllerFactory;
import au.com.dealsdirect.utils.module.GateKeeper;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Timed;

public class MyAccountsOurpayController extends BaseController
        implements MyAccountsOurpayMvpView, MyAccountsOurpayDataSource, MyAccountsOurpayListener {

    private final static TimeUnit EXECUTOR_SERVICE_TIME_UNIT = TimeUnit.MILLISECONDS;
    private final static float HEADER_SNAP_SPEED_MULTIPLIER = EXECUTOR_SERVICE_TIME_UNIT.convert(1, TimeUnit.SECONDS);
    @BindView(R.id.partial_toolbar_field_title_textview)
    TextView mTitle;
    @BindView(R.id.myAccountOurpayOverlay)
    MyAccountsOurpayOverlayView mOverlayView;
    @Inject
    MyAccountsOurpayMvpPresenter<MyAccountsOurpayMvpView> mPresenter;
    private View mView;
    private View mEmptyView;
    private View mTabbedView;
    private TabLayout mTabLayout;
    private RecyclerView mRecyclerView;
    private MyAccountsOurpayRecyclerViewPagerAdapter mRecyclerViewAdapter;
    private ArrayList<MyAccountsOurpayCellAdapter> mCellAdapters;
    private View mHeaderView;
    private int mCurrentRecyclerViewPosition = 0;
    // pixels per EXECUTOR_SERVICE_TIME_UNIT, which would be milliseconds
    private float mHeaderHeight = 0;
    private float mHeaderHeightSpeed = 0;
    private CompositeDisposable mHeaderRunLoopDisposable = new CompositeDisposable();
    private Consumer<Timed<Long>> mHeaderRunLoop = new Consumer<Timed<Long>>() {
        @Override
        public void accept(Timed<Long> longTimed) throws Exception {
            // displacement
            setHeaderHeight(getHeaderHeight() + getHeaderHeightSpeed());

            // friction
            setHeaderHeightSpeed(getHeaderHeightSpeed() * 0.9975f);

            if (!mOverlayView.getIsTouching() &&
                    Math.floor(Math.abs(getHeaderHeightSpeed())) < getSnapHeightSpeedThreshold()) {
                if (getHeaderHeight() != getExpandedHeaderHeight() ||
                        getHeaderHeight() != getContractedHeaderHeight()) {
                    snapHeaderHeight();
                }
            }

            if (getHeaderHeight() >= getExpandedHeaderHeight()) {
                setHeaderHeight(getExpandedHeaderHeight());
                setHeaderHeightSpeed(0f);

                if (!mOverlayView.getIsTouching()) {
                    endHeaderRunLoop();
                }
            }

            if (getHeaderHeight() <= getContractedHeaderHeight()) {
                setHeaderHeight(getContractedHeaderHeight());
                setHeaderHeightSpeed(0f);

                if (!mOverlayView.getIsTouching()) {
                    endHeaderRunLoop();
                }
            }
        }
    };

    private List<Group<? extends Item>> mPaymentPlans;
    private List<Group<? extends Item>> mScheduledPayments;
    private List<Group<? extends Item>> mPastPayments;

    public MyAccountsOurpayController(Bundle args) {
        super(args);
    }

    public static MyAccountsOurpayController newInstance() {
        return new MyAccountsOurpayController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    private float getExpandedHeaderHeight() {
        return Objects.requireNonNull(getResources()).getDimension(R.dimen.ourpay_expanded_header);
    }

    private float getContractedHeaderHeight() {
        return Objects.requireNonNull(getResources()).getDimension(R.dimen.ourpay_contracted_header);
    }

    private float getHeaderHeight() {
        return mHeaderHeight;
    }

    private void setHeaderHeight(float height) {
        mHeaderHeight = height;
        if (mHeaderView != null) {
            ViewGroup.LayoutParams layoutParams = mHeaderView.getLayoutParams();
            layoutParams.height = Math.round(mHeaderHeight);
            mHeaderView.setLayoutParams(layoutParams);
        }
    }

    private float getHeaderHeightSpeed() {
        return mHeaderHeightSpeed;
    }

    private void setHeaderHeightSpeed(float speed) {
        mHeaderHeightSpeed = speed;
    }

    private void snapHeaderHeight() {
        if ((getHeaderHeight() - getContractedHeaderHeight()) /
                (getExpandedHeaderHeight() - getContractedHeaderHeight()) < 0.5f) {
            // snap to contracted height
            setHeaderHeightSpeed(Math.min(getHeaderHeightSpeed(),
                    (getContractedHeaderHeight() - getHeaderHeight()) /
                            (HEADER_SNAP_SPEED_MULTIPLIER * 0.3f)));

            if (Math.round(getHeaderHeight() - getContractedHeaderHeight()) == 0) {
                setHeaderHeight(getContractedHeaderHeight());
            }
        } else {
            setHeaderHeightSpeed(Math.max(getHeaderHeightSpeed(),
                    (getExpandedHeaderHeight() - getHeaderHeight()) /
                            (HEADER_SNAP_SPEED_MULTIPLIER * 0.3f)));

            if (Math.round(getHeaderHeight() - getExpandedHeaderHeight()) == 0) {
                setHeaderHeight(getExpandedHeaderHeight());
            }
        }
    }

    private float getSnapHeightSpeedThreshold() {
        return Objects.requireNonNull(getResources()).getDimension(R.dimen.ourpay_snap_height_speed_threshold) /
                (float) TimeUnit.SECONDS.convert(1, EXECUTOR_SERVICE_TIME_UNIT);
    }

    private void startHeaderRunLoop() {
        if (mHeaderRunLoopDisposable.size() == 0) {
            mHeaderRunLoopDisposable.add(Observable.interval(1, EXECUTOR_SERVICE_TIME_UNIT)
                    .timeInterval()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mHeaderRunLoop));
        }
    }

    private void endHeaderRunLoop() {
        mHeaderRunLoopDisposable.clear();
    }

    @Override
    public void showLoadingDialog() {
        if (isViewAttached()) {
            mActivity.showOurpayLoading();
        }
    }

    private static class HeightProperty extends Property<View, Integer> {
        private HeightProperty() {
            super(Integer.class, "height");
        }

        @Override
        public Integer get(View view) {
            return view.getHeight();
        }

        @Override
        public void set(View view, Integer value) {
            view.getLayoutParams().height = value;
            view.setLayoutParams(view.getLayoutParams());
        }
    }

    private void animateHeaderHeightTo(int height) {
        if (mHeaderView == null) return;

        final int currentHeight = mHeaderView.getHeight();
        ObjectAnimator animator = ObjectAnimator.ofInt(mHeaderView, new HeightProperty(), currentHeight, height);
        animator.setDuration(300L);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
        mHeaderHeight = height;
    }

    @Override
    public void onViewWillAppear(Controller previousController) {
        super.onViewWillAppear(previousController);
    }

    @Override
    public void onViewDidAppear(Controller previousController) {
        super.onViewDidAppear(previousController);

        if (!(previousController instanceof LegalitiesController)) {
            mPresenter.fetchDataForSummary();
        } else if ((mEmptyView == null || mEmptyView.getVisibility() == View.GONE) &&
                (mTabbedView == null || mTabbedView.getVisibility() != View.GONE)) {
            mPresenter.fetchDataForSummary();
        }
    }

    @Override
    public void refreshContents() {
        super.refreshContents();

        if ((mEmptyView == null || mEmptyView.getVisibility() == View.GONE) &&
                (mTabbedView == null || mTabbedView.getVisibility() != View.GONE)) {
            mPresenter.fetchDataForSummary();
        }
    }

    @Override
    public void onOrientationChanged(Configuration newConfiguration) {
        super.onOrientationChanged(newConfiguration);

        resetHeaderHeight(newConfiguration.orientation);
    }

    private void resetHeaderHeight(int orientation) {
        if (!Objects.requireNonNull(getResources()).getBoolean(R.bool.is_tablet)) {
            switch (orientation) {
                case Configuration.ORIENTATION_LANDSCAPE:
                    animateHeaderHeightTo((int) getContractedHeaderHeight());
                    break;
                default:
                    animateHeaderHeightTo((int) getExpandedHeaderHeight());
                    break;
            }
        }
    }

    @Override
    protected void setUp(View view) {
        mTitle.setText(mTitle.getContext().getResources().getString(R.string.ourpay_title));

        // when router pops to this, one of these views would usually not null
        // but would need to be setup again
        if (mEmptyView != null && mEmptyView.getVisibility() != View.GONE) {
            mEmptyView = null;
            showEmptyView();
        } else if (mTabbedView != null && mTabbedView.getVisibility() != View.GONE) {
            mTabbedView = null;
            showTabbedView();
        }
    }

    private void setupOverlayView() {
        mOverlayView.setTimeUnit(EXECUTOR_SERVICE_TIME_UNIT);

        mOverlayView.setListener(
                new MyAccountsOurpayOverlayView.TouchMoveListeneer() {
                    @Override
                    public boolean onTouchDown(MotionEvent event) {
                        mTabbedView.dispatchTouchEvent(event);
                        return true;
                    }

                    @Override
                    public boolean onTouchMoved(MotionEvent event, float xSpeed, float ySpeed) {
                        mTabbedView.dispatchTouchEvent(event);
                        if (!isTablet() && isLandscape() &&
                                (isHeaderNotContracted() ||
                                        (isRecyclerViewScrolledToTop() &&
                                                isHeaderNotExpanded() && ySpeed > 0))) {
                            startHeaderRunLoop();
                            if (getCurrentRecyclerView() != null) {
                                getCurrentRecyclerView().setScrollY(0);
                            }
                            setHeaderHeightSpeed(ySpeed);
                        }
                        return true;
                    }

                    @Override
                    public boolean onTouchUp(MotionEvent event) {
                        mTabbedView.dispatchTouchEvent(event);
                        if (!isTablet() && isLandscape() &&
                                (isHeaderNotContracted() ||
                                        (isRecyclerViewScrolledToTop() &&
                                                isHeaderNotExpanded()))) {
                            if (getCurrentRecyclerView() != null) {
                                getCurrentRecyclerView().smoothScrollToPosition(0);
                            }
                        }
                        return true;
                    }

                    private boolean isTablet() {
                        return Objects.requireNonNull(getResources()).getBoolean(R.bool.is_tablet);
                    }

                    private boolean isLandscape() {
                        return Objects.requireNonNull(getResources())
                                .getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
                    }

                    private boolean isRecyclerViewScrolledToTop() {
                        RecyclerView recyclerView = getCurrentRecyclerView();
                        if (recyclerView == null) {
                            return true;
                        } else {
                            return ((LinearLayoutManager) Objects.requireNonNull(recyclerView
                                    .getLayoutManager())).findFirstCompletelyVisibleItemPosition() == 0;
                        }
                    }

                    private boolean isHeaderNotContracted() {
                        return getHeaderHeight() > getContractedHeaderHeight();
                    }

                    private boolean isHeaderNotExpanded() {
                        return getHeaderHeight() < getExpandedHeaderHeight();
                    }
                }
        );
    }

    private void setupTabbedView() {
        mTabLayout = mTabbedView.findViewById(R.id.myAccountOurpayTabLayout);
        mRecyclerView = mTabbedView.findViewById(R.id.myAccountOurpayRecyclerView);
        mHeaderView = mTabbedView.findViewById(R.id.myAccountOurpayTabbedHeader);
        mHeaderHeight = getExpandedHeaderHeight();

        ViewUtils.changeFontInViewGroup(mTabLayout,
                mTabLayout.getContext().getString(R.string.font_app_regular),
                mTabLayout.getContext().getResources().getDimension(R.dimen.text_size_body));

        mRecyclerViewAdapter = new MyAccountsOurpayRecyclerViewPagerAdapter();

        mRecyclerView.setAdapter(mRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView.getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));
        mRecyclerView.setLayoutParams(new LinearLayout
                .LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();

                if (layoutManager == null) {
                    return;
                }
                int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (position != RecyclerView.NO_POSITION &&
                        mCurrentRecyclerViewPosition != position) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(position);
                    if (mTabLayout.getSelectedTabPosition() != position && tab != null) {
                        tab.select();
                    }
                    mCurrentRecyclerViewPosition = position;
                    fetchDataForIndex(position);
                }
            }
        });

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mCellAdapters = new ArrayList<>();
        mCellAdapters.add(new MyAccountsOurpayCellAdapter(
                mPresenter,
                this,
                "PaymentPlans",
                MyAccountsOurpayCellAdapter.VIEW_TYPE_PAYMENT_PLANS,
                this,
                (adapter, viewType, indexPath, event) -> {
                    if (adapter == null || adapter.get() == null) {
                        return false;
                    }

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // don't do anything yet, just capture the touch event
                            // https://stackoverflow.com/a/16495363
                            return true;
                        case MotionEvent.ACTION_UP:
                            MyAccountsOurpayCellAdapter.RowMode current;
                            if (adapter.get().getRowModes().containsKey(indexPath)) {
                                current = adapter.get().getRowModes().get(indexPath);
                            } else {
                                current = new MyAccountsOurpayCellAdapter.RowMode();
                            }

                            if (current == null) {
                                break;
                            }

                            Integer position = adapter.get().getPositionFromIndexPath(indexPath);

                            switch (viewType) {
                                case MyAccountsOurpayCellAdapter
                                        .OnItemViewTouchEventListener.PAYMENT_PLANS_CHEVRON:
                                    adapter.get().getRowModes().put(indexPath,
                                            current.toggle(MyAccountsOurpayCellAdapter
                                                    .RowMode.EXPANDED_INSTALLMENTS));
                                    adapter.get().notifyItemChanged(indexPath);

                                    if (position != null && getCurrentRecyclerView() != null) {
                                        getRecyclerViewAt(0).smoothScrollToPosition(position);
                                    }

                                    return true;
                                case MyAccountsOurpayCellAdapter
                                        .OnItemViewTouchEventListener.PAYMENT_PLANS_DOWN_ARROW:
                                    adapter.get().getRowModes().put(indexPath,
                                            current.toggle(MyAccountsOurpayCellAdapter
                                                    .RowMode.EXPANDED_REFUND));
                                    adapter.get().notifyItemChanged(indexPath);

                                    if (position != null && getCurrentRecyclerView() != null) {
                                        getRecyclerViewAt(0).smoothScrollToPosition(position);
                                    }

                                    return true;
                            }
                            break;
                    }
                    return false;
                }));
        mCellAdapters.add(new MyAccountsOurpayCellAdapter(mPresenter, this,"ScheduledPayments",
                MyAccountsOurpayCellAdapter.VIEW_TYPE_SCHEDULED_PAYMENTS, this, null));
        mCellAdapters.add(new MyAccountsOurpayCellAdapter(mPresenter, this,"PastPayments",
                MyAccountsOurpayCellAdapter.VIEW_TYPE_SCHEDULED_PAYMENTS, this, null));

        mRecyclerViewAdapter.setCellAdapters(mCellAdapters);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mRecyclerView.smoothScrollToPosition(tab.getPosition());
                resetHeaderHeight(Objects.requireNonNull(getResources())
                        .getConfiguration().orientation);

                switch (tab.getPosition()) {
                    case 0:
                        mPresenter.fetchDataForPaymentPlans();
                        break;
                    case 1:
                        mPresenter.fetchDataForScheduledPayments();
                        break;
                    case 2:
                        mPresenter.fetchDataForPastPayments();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        mView = inflater.inflate(R.layout.controller_myaccount_ourpay, container, false);
        getControllerComponent().inject(this);
        return mView;
    }

    @Override
    protected void onChangeEnded(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        super.onChangeEnded(changeHandler, changeType);


    }

    @Override
    protected void onAttach(@NonNull View view) {
        mPresenter.onAttach(this);
        super.onAttach(view);
    }

    @Override
    public void onDetach(View view) {
        super.onDetach(view);
        endHeaderRunLoop();
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        setUp(view);
    }

    @OnClick(R.id.partial_toolbar_field_title_left_option)
    public void onBackClick() {
        if (mActivity != null) {
            mActivity.onBackPressed();
        }
    }

    private RecyclerView getRecyclerViewAt(int position) {
        RecyclerView recyclerView = null;
        if (mRecyclerView != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager != null) {
                recyclerView = (RecyclerView) layoutManager.findViewByPosition(position);
            }
        }
        return recyclerView;
    }


    private RecyclerView getCurrentRecyclerView() {
        return getRecyclerViewAt(mCurrentRecyclerViewPosition);
    }

    @Override
    public List<Group<? extends Item>> getData(MyAccountsOurpayCellAdapter adapter) {

        switch (adapter.getId()) {
            case "PaymentPlans":
                return mPaymentPlans;
            case "ScheduledPayments":
                return mScheduledPayments;
            case "PastPayments":
                return mPastPayments;
        }

        return null;
    }

    @Override
    public void showEmptyView() {
        if (mEmptyView == null) {
            mEmptyView = ((ViewStub) mView.findViewById(R.id.myAccountOurpayEmptyViewStub))
                    .inflate();
        }

        mEmptyView.setVisibility(View.VISIBLE);
        if (mTabbedView != null) {
            mTabbedView.setVisibility(View.GONE);
        }
        mOverlayView.setListener(null);

        mEmptyView.findViewById(R.id.myAccountsOurpayInfoButton)
                .setOnClickListener(v -> openInfoPage());
        mEmptyView.findViewById(R.id.myAccountsOurpayTermsAndConditionsButton)
                .setOnClickListener(v -> openTermsAndConditionsPage());
    }

    @Override
    public void showTabbedView() {
        if (mTabbedView == null) {
            mTabbedView = ((ViewStub) mView.findViewById(R.id.myAccountOurpayTabbedViewStub))
                    .inflate();
        }

        if (mEmptyView != null) {
            mEmptyView.setVisibility(View.GONE);
        }
        mTabbedView.setVisibility(View.VISIBLE);
        setupTabbedView();
        setupOverlayView();

        // preload them anyway
        for (int i = 0; i < 3; i += 1) {
            fetchDataForIndex(i);
        }
    }

    @Override
    public void setDataForSummary(Summary summary) {
        if (summary == null || mTabbedView == null) {
            return;
        }

        TextView activePlansView = mTabbedView.findViewById(R.id.activePlansDetailValue);
        activePlansView.setText(summary.getActivePlans());

        TextView balanceOwingView = mTabbedView.findViewById(R.id.balanceOwingDetailValue);
        balanceOwingView.setText(summary.getBalanceOwing());

        TextView remainingCreditView = mTabbedView.findViewById(R.id.remainingCreditDetailValue);
        remainingCreditView.setText(summary.getRemainingCredit());
    }

    @Override
    public void setDataForPaymentPlans(GetPaymentPlansResponse response) {
        List<Group<PaymentPlan>> list =
                MyAccountsOurpayResponseReducer
                        .getInstance(getActivity()).createPaymentPlans(response);

        if (list != null) {
            mPaymentPlans = new ArrayList<>(list);
        } else {
            mPaymentPlans = null;
        }

        if (mCellAdapters != null && mCellAdapters.size() > 0) {
            mCellAdapters.get(0).reloadData();
        }
    }

    @Override
    public void setDataForScheduledPayments(GetScheduledPlansResponse response) {
        List<Group<ScheduledPayment>> list =
                MyAccountsOurpayResponseReducer
                        .getInstance(getActivity()).createScheduledPayments(response);

        if (list != null) {
            mScheduledPayments = new ArrayList<>(list);
        } else {
            mScheduledPayments = null;
        }
        if (mCellAdapters != null && mCellAdapters.size() > 1) {
            mCellAdapters.get(1).reloadData();
        }
    }

    @Override
    public void setDataForPastPayments(GetPastPaymentsResponse response) {
        List<Group<ScheduledPayment>> list =
                MyAccountsOurpayResponseReducer
                        .getInstance(getActivity()).createPastPayments(response);

        if (list != null) {
            mPastPayments = new ArrayList<>(list);
        } else {
            mPastPayments = null;
        }

        if (mCellAdapters != null && mCellAdapters.size() > 2) {
            mCellAdapters.get(2).reloadData();
        }
    }

    @Override
    public void showMessage(String message) {
        CustomAlertDialog.showCustomAlertDialog(mActivity,
                CustomAlertDialog.CustomDialogIconState.NEGATIVE, message);
    }

    private void fetchDataForIndex(int index) {
        switch (index) {
            case 0:
                mPresenter.fetchDataForPaymentPlans();
                break;
            case 1:
                mPresenter.fetchDataForScheduledPayments();
                break;
            case 2:
                mPresenter.fetchDataForPastPayments();
                break;
        }
    }

    void openInfoPage() {
        String title = getResources().getString(R.string.ourpay_info_page_title);
        String infoPageUrl = getResources().getString(R.string.ourpay_info_page_url);

        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.KEY_WEBVIEW_CONTROLLER_URL, infoPageUrl)
                .putString(BundleKeys.KEY_WEBVIEW_CONTROLLER_TITLE, title)
                .build();

        RouterTransaction transaction = RouterTransaction
                .with(ControllerFactory
                        .getInstance(GateKeeper.Destination.COMMON_WEBVIEW, bundle))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());
        getRouter().pushController(transaction);
    }

    void openTermsAndConditionsPage() {
        String title = getResources().getString(R.string.ourpay_tnc_title);

        Bundle bundle = new BundleBuilder(new Bundle())
                .putString(BundleKeys.TEMPLATE_KEY, BundleKeys.TEMPLATE_KEY_OURPAY_TNC)
                .putString(BundleKeys.LEGALITIES_TITLE, title)
                .build();

        RouterTransaction transaction = RouterTransaction
                .with(ControllerFactory
                        .getInstance(GateKeeper.Destination.LEGALITIES, bundle))
                .pushChangeHandler(new HorizontalChangeHandler())
                .popChangeHandler(new HorizontalChangeHandler());
        getRouter().pushController(transaction);
    }
}
