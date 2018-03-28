package au.com.dealsdirect.ui.controller.dashboard.paymentplans;

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
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/*
 * Created by Ayi on 05/06/2017.
 */

public class PaymentPlansController extends BaseController implements PaymentPlansMvpView {

    public static final String TAG = "PaymentPlansController";

    private static final String KEY_PLANS = "PaymentPlansController.KEY_PLANS";

    @Inject
    PaymentPlansMvpPresenter<PaymentPlansMvpView> mPresenter;

    @BindView(R.id.controller_payment_plans_recycler)
    RecyclerView mRecyclerView;

    PaymentPlansAdapter mAdapter;

    List<PaymentPlan> mPaymentPlans;

    DashboardController mParentController;

    public static PaymentPlansController newInstance(ArrayList<PaymentPlan> paymentPlans) {

        return new PaymentPlansController(
                new BundleBuilder(new Bundle())
                        .putParcelableArrayList(KEY_PLANS, paymentPlans)
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
        mPaymentPlans = getArgs().getParcelableArrayList(KEY_PLANS);

        mAdapter = new PaymentPlansAdapter(mPaymentPlans);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

}
