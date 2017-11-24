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
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardMvpPresenter;
import au.com.dealsdirect.ui.controller.dashboard.DashboardMvpView;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/*
 * Created by Ayi on 05/06/2017.
 */

public class PaymentPlansController extends BaseController implements DashboardMvpView {

    public static final String TAG = "PaymentPlansController";

    @Inject
    DashboardMvpPresenter<DashboardMvpView> mPresenter;

    @BindView(R.id.controller_payment_plans_recycler)
    RecyclerView mRecyclerView;

    PaymentPlansAdapter mAdapter;

    List<PaymentPlan> mPaymentPlans;

    DashboardController mParentController;

    public static PaymentPlansController newInstance(DashboardController parentController, List<PaymentPlan> paymentPlans) {

        return new PaymentPlansController(
                new BundleBuilder(new Bundle())
                        .build());
    }

    public PaymentPlansController(Bundle args) {
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
        data.add(new Payment("mysale"));
        data.add(new Payment("lifeandlookstyle"));
        data.add(new Payment("nzsale"));
        data.add(new Payment("cocosa"));
        data.add(new Payment("mysale"));
        data.add(new Payment("lifeandlookstyle"));

        mAdapter = new PaymentPlansAdapter(this, data);
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
