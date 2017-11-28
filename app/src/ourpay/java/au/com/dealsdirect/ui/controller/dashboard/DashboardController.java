package au.com.dealsdirect.ui.controller.dashboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.PastPayment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.ScheduledPlan;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.dashboard.plans.PastPaymentsController;
import au.com.dealsdirect.ui.controller.dashboard.plans.PaymentPlansController;
import au.com.dealsdirect.ui.controller.dashboard.plans.ScheduledPlansController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.TabLayoutUtils;
import butterknife.BindView;

/*
 * Created by Ayi on 05/06/2017.
 */

public class DashboardController extends BaseController implements DashboardMvpView, Serializable {

    public static final String TAG = "DashboardController";

    private static final int PAGE_COUNT = 3;

    @Inject
    DashboardMvpPresenter<DashboardMvpView> mPresenter;

    @BindView(R.id.controller_payment_tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.controller_payment_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.controller_payment_dashboard_active_text)
    TextView mActiveText;

    @BindView(R.id.controller_payment_dashboard_balance_text)
    TextView mBalanceText;

    @BindView(R.id.controller_payment_dashboard_credit_text)
    TextView mCreditText;

    @BindView(R.id.controller_payment_dashboard_overdue_text)
    TextView mOverdueText;

    ArrayList<PaymentPlan> mPaymentPlans = new ArrayList<>();

    ArrayList<ScheduledPlan> mScheduledPlans = new ArrayList<>();

    ArrayList<PastPayment> mPastPayments = new ArrayList<>();

    String[] mTabTitles;

    RouterPagerAdapter mAdapter;

    public static DashboardController newInstance() {

        return new DashboardController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public DashboardController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_payment, container, false);

        getControllerComponent().inject(this);

        mPresenter.onAttach(this);

        return view;
    }

    @Override
    public void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        setUp(view);
    }

    @Override
    protected void setUp(View view) {
        mPresenter.getPaymentPlans();
        mPresenter.getPastPayments();
        mPresenter.getScheduledPayments();

        mTabTitles = new String[]{getString(R.string.payment_plans), getString(R.string.scheduled_plans), getString(R.string.past_payments)};
        mAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    Controller page = PaymentPlansController.newInstance(DashboardController.this, mPaymentPlans);
                    switch (position) {
                        case 1:
                            page = ScheduledPlansController.newInstance(DashboardController.this, mScheduledPlans);
                            break;
                        case 2:
                            page = PastPaymentsController.newInstance(DashboardController.this, mPastPayments);
                            break;
                    }
                    router.setRoot(RouterTransaction.with(page));
                }
            }

            @Override
            public int getCount() {
                return PAGE_COUNT;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabTitles[position];
            }
        };
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);
        TabLayoutUtils.setupWithCustomFont(getActivity(), mTabLayout, mTabTitles, getString(R.string.font_lato_light));
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showPaymentPlans(GetPaymentPlansResponse paymentPlansResponse) {
        mActiveText.setText((String.format(Locale.getDefault(), "%d", paymentPlansResponse.getValue().getActivePlansCount())));
        mBalanceText.setText(String.format(Locale.getDefault(), "%f", paymentPlansResponse.getValue().getRemainingBalance()));
        mCreditText.setText(String.format(Locale.getDefault(), "%f", paymentPlansResponse.getValue().getRemainingCredit()));
        mOverdueText.setText(String.format(Locale.getDefault(), "%d", paymentPlansResponse.getValue().getOverduePlansCount()));

        mPaymentPlans.addAll(paymentPlansResponse.getValue().getPaymentPlans());
        refreshPager();
    }

    @Override
    public void showScheduledPlans(GetScheduledPlansResponse scheduledPaymentsResponse) {
        mScheduledPlans.addAll(scheduledPaymentsResponse.getScheduledPayment());
        refreshPager();
    }

    @Override
    public void showPastPayments(GetPastPaymentsResponse pastPaymentsResponse) {
        mPastPayments.addAll(pastPaymentsResponse.getPastPayment());
        refreshPager();
    }

    @Override
    public void refreshPager() {
        if (mPaymentPlans != null && mScheduledPlans != null && mPastPayments != null) {
            mAdapter = new RouterPagerAdapter(this) {
                @Override
                public void configureRouter(@NonNull Router router, int position) {
                    if (!router.hasRootController()) {
                        Controller page = PaymentPlansController.newInstance(DashboardController.this, mPaymentPlans);
                        switch (position) {
                            case 1:
                                page = ScheduledPlansController.newInstance(DashboardController.this, mScheduledPlans);
                                break;
                            case 2:
                                page = PastPaymentsController.newInstance(DashboardController.this, mPastPayments);
                                break;
                        }
                        router.setRoot(RouterTransaction.with(page));
                    }
                }

                @Override
                public int getCount() {
                    return PAGE_COUNT;
                }

                @Override
                public int getItemPosition(Object object) {
                    return POSITION_NONE;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return mTabTitles[position];
                }
            };
            mViewPager.setAdapter(mAdapter);
            mTabLayout.setupWithViewPager(mViewPager, true);
            TabLayoutUtils.setupWithCustomFont(getActivity(), mTabLayout, mTabTitles, getString(R.string.font_lato_light));
        }
    }

    @Override
    public void showPaymentDetailsController(Payment payment) {

    }
}
