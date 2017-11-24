package au.com.dealsdirect.ui.controller.dashboard.plans;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.ScheduledPlan;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardMvpPresenter;
import au.com.dealsdirect.ui.controller.dashboard.DashboardMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/*
 * Created by Ayi on 05/06/2017.
 */

public class ScheduledPlansController extends BaseController implements DashboardMvpView {

    public static final String TAG = "ScheduledPlansController";

    @Inject
    DashboardMvpPresenter<DashboardMvpView> mPresenter;

    @BindView(R.id.controller_payment_plans_recycler)
    RecyclerView mRecyclerView;

    ScheduledPlansAdapter mAdapter;

    List<ScheduledPlan> mScheduledPlans;

    DashboardController mParentController;

    public static ScheduledPlansController newInstance(DashboardController parentController, List<ScheduledPlan> scheduledPlans) {

        return new ScheduledPlansController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public ScheduledPlansController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_payment_plans, container, false);

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
        ArrayList<Payment> data = new ArrayList<>();
        data.add(new Payment("12", "January", "lifeandlookstyle"));
        data.add(new Payment("12", "January", "ozsale"));
        data.add(new Payment("01", "February", "mysale"));
        data.add(new Payment("10", "February", "cocosa"));
        data.add(new Payment("10", "February", "cocosa"));

        mAdapter = new ScheduledPlansAdapter(this, data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @Override
    public void showPaymentPlans(GetPaymentPlansResponse paymentPlansResponse) {

    }

    @Override
    public void showScheduledPayments(GetScheduledPlansResponse scheduledPaymentsResponse) {

    }

    @Override
    public void showPastPayments(GetPastPaymentsResponse pastPaymentsResponse) {

    }

    @Override
    public void refreshPager() {

    }

    @Override
    public void showPaymentDetailsController(Payment payment) {

    }
}
