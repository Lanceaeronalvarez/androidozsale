package au.com.dealsdirect.ui.controller.dashboard.details;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.Payment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.PastPayment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.ScheduledPlan;
import au.com.dealsdirect.ui.base.BaseController;
import au.com.dealsdirect.utils.BundleBuilder;
import au.com.dealsdirect.utils.DateUtils;
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

    @BindView(R.id.controller_payment_details_date)
    TextView mDateText;

    @BindView(R.id.controller_payment_details_balance)
    TextView mBalanceText;

    @BindView(R.id.controller_payment_details_recycler)
    RecyclerView mRecyclerView;

    PaymentPlan mPaymentPlan;

    ScheduledPlan mScheduledPlan;

    PastPayment mPastPayment;

    PaymentDetailsAdapter mAdapter;

    public static PaymentDetailsController newInstance(PaymentPlan paymentPlan) {

        return new PaymentDetailsController(
                new BundleBuilder(new Bundle())
                        .putParcelable(KEY_PAYMENT, paymentPlan)
                        .build());
    }

    public static PaymentDetailsController newInstance(ScheduledPlan scheduledPlan) {

        return new PaymentDetailsController(
                new BundleBuilder(new Bundle())
                        .putParcelable(KEY_PAYMENT, scheduledPlan)
                        .build());
    }

    public static PaymentDetailsController newInstance(PastPayment pastPayment) {

        return new PaymentDetailsController(
                new BundleBuilder(new Bundle())
                        .putParcelable(KEY_PAYMENT, pastPayment)
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
        Parcelable payment = getArgs().getParcelable(KEY_PAYMENT);
        if (payment instanceof PaymentPlan) {

            mPaymentPlan = (PaymentPlan) payment;
            mTitleText.setText(mPaymentPlan.getName());
            mIdText.setText(mPaymentPlan.getOrderNo());
            mDateText.setText(DateFormat.format("MM/dd/yyyy, hh:mm a", Calendar.getInstance()));
            mBalanceText.setText(String.format(Locale.getDefault(), "%s%.2f", mPaymentPlan.getCurrencySign(), mPaymentPlan.getOrderBalance()));

        } else if (payment instanceof ScheduledPlan) {

            mScheduledPlan = (ScheduledPlan) payment;
            mTitleText.setText(mScheduledPlan.getName());
            mIdText.setText(mScheduledPlan.getOrderNo());
            mDateText.setText(DateUtils.getDateFromStringInFormat(mScheduledPlan.getPlannedDate(), "MM/dd/yyyy, hh:mm a"));
            mBalanceText.setText(String.format(Locale.getDefault(), "%s%.2f", mScheduledPlan.getCurrency(), mScheduledPlan.getAmount()));

        } else if (payment instanceof PastPayment) {

            mPastPayment = (PastPayment) payment;
            mTitleText.setText(mPastPayment.getName());
            mIdText.setText(mPastPayment.getOrderNo());
            mDateText.setText(DateUtils.getDateFromStringInFormat(mPastPayment.getPlannedDate(), "MM/dd/yyyy, hh:mm a"));
            mBalanceText.setText(String.format(Locale.getDefault(), "%s%.2f", mPastPayment.getCurrency(), mPastPayment.getAmount()));

        }

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
