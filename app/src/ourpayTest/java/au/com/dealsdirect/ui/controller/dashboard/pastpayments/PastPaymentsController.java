package au.com.dealsdirect.ui.controller.dashboard.pastpayments;

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
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.PastPayment;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.ui.controller.dashboard.DashboardController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;

/*
 * Created by Ayi on 05/06/2017.
 */

public class PastPaymentsController extends BaseController implements PastPaymentsMvpView {

    public static final String TAG = "PastPaymentsController";

    private static final String KEY_CONTROLLER = "PastPaymentsController.KEY_CONTROLLER";

    private static final String KEY_PLANS = "PastPaymentsController.KEY_PLANS";

    @Inject
    PastPaymentsMvpPresenter<PastPaymentsMvpView> mPresenter;

    @BindView(R.id.controller_payment_plans_recycler)
    RecyclerView mRecyclerView;

    PastPaymentsAdapter mAdapter;

    List<PastPayment> mPastPayments;

    DashboardController mParentController;

    public static PastPaymentsController newInstance(ArrayList<PastPayment> pastPayments) {

        return new PastPaymentsController(
                new BundleBuilder(new Bundle())
                        .putParcelableArrayList(KEY_PLANS, pastPayments)
                        .build());
    }

    public PastPaymentsController(Bundle args) {
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
        mPastPayments = getArgs().getParcelableArrayList(KEY_PLANS);

        mAdapter = new PastPaymentsAdapter(mPastPayments);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

}
