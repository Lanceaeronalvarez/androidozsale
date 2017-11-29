package au.com.dealsdirect.ui.controller.dashboard.details;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import butterknife.BindView;
import butterknife.OnClick;

/*
 * Created by Ayi on 05/06/2017.
 */

public class PaymentDetailsController extends BaseController implements PaymentDetailsMvpView {

    public static final String TAG = "PaymentDetailsController";

    private static final String KEY_PAYMENT = "PaymentDetailsController.KEY_PAYMENT";

    @Inject
    PaymentDetailsMvpPresenter<PaymentDetailsMvpView> mPresenter;

    @BindView(R.id.controller_payment_details_title)
    TextView mTitleText;

    @BindView(R.id.controller_payment_details_id)
    TextView mIdText;

    @BindView(R.id.controller_payment_details_recycler)
    RecyclerView mRecyclerView;

    Payment mPayment;

    PaymentDetailsAdapter mAdapter;

    public static PaymentDetailsController newInstance(Payment payment) {

        return new PaymentDetailsController(
                new BundleBuilder(new Bundle())
                        .putParcelable(KEY_PAYMENT, payment)
                        .build());
    }

    public PaymentDetailsController(Bundle args) {
        super(args);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.controller_payment_details, container, false);

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
        mPayment = getArgs().getParcelable(KEY_PAYMENT);

        mTitleText.setText(mPayment.getTitle());
        mIdText.setText(mPayment.getId());

        ArrayList<Payment> data = new ArrayList<>();
        data.add(new Payment("01", "February", "lifeandlookstyle"));
        data.add(new Payment("14", "February", "ozsale"));
        data.add(new Payment("28", "February", "mysale"));
        data.add(new Payment("14", "March", "cocosa"));

        mAdapter = new PaymentDetailsAdapter(this, data);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView(@NonNull View view) {
        mPresenter.onDetach();
        super.onDestroyView(view);
    }

    @OnClick(R.id.controller_back)
    void onClickBack(View view) {
        if (getActivity() != null) {
            getActivity().onBackPressed();
        }
    }
}
