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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.PastPayment;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PaymentPlan;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.PlannedTransaction;
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

    @BindView(R.id.controller_payment_details_schedule_container)
    LinearLayout mPaymentScheduleContainer;

    @BindView(R.id.controller_payment_details_recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.controller_payment_details_schedule_circle1)
    ImageView mPaymentScheduleCircle1;

    @BindView(R.id.controller_payment_details_schedule_circle2)
    ImageView mPaymentScheduleCircle2;

    @BindView(R.id.controller_payment_details_schedule_circle3)
    ImageView mPaymentScheduleCircle3;

    @BindView(R.id.controller_payment_details_schedule_circle4)
    ImageView mPaymentScheduleCircle4;

    @BindView(R.id.controller_payment_details_schedule_date1)
    TextView mPaymentScheduleDate1;

    @BindView(R.id.controller_payment_details_schedule_date2)
    TextView mPaymentScheduleDate2;

    @BindView(R.id.controller_payment_details_schedule_date3)
    TextView mPaymentScheduleDate3;

    @BindView(R.id.controller_payment_details_schedule_date4)
    TextView mPaymentScheduleDate4;

    @BindView(R.id.controller_payment_details_schedule_value1)
    TextView mPaymentScheduleValue1;

    @BindView(R.id.controller_payment_details_schedule_value2)
    TextView mPaymentScheduleValue2;

    @BindView(R.id.controller_payment_details_schedule_value3)
    TextView mPaymentScheduleValue3;

    @BindView(R.id.controller_payment_details_schedule_value4)
    TextView mPaymentScheduleValue4;

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

            mAdapter = new PaymentDetailsAdapter(mPaymentPlan, mPaymentPlan.getPlannedTransactions());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            mRecyclerView.setAdapter(mAdapter);
            populatePaymentSchedule();

        } else if (payment instanceof ScheduledPlan) {

            mScheduledPlan = (ScheduledPlan) payment;
            mTitleText.setText(mScheduledPlan.getName());
            mIdText.setText(mScheduledPlan.getOrderNo());
            mDateText.setText(DateUtils.getDateFromStringInFormat(mScheduledPlan.getPlannedDate(), "MM/dd/yyyy, hh:mm a"));
            mBalanceText.setText(String.format(Locale.getDefault(), "%s%.2f", mScheduledPlan.getCurrency(), mScheduledPlan.getAmount()));

            mPaymentScheduleContainer.setVisibility(View.GONE);

        } else if (payment instanceof PastPayment) {

            mPastPayment = (PastPayment) payment;
            mTitleText.setText(mPastPayment.getName());
            mIdText.setText(mPastPayment.getOrderNo());
            mDateText.setText(DateUtils.getDateFromStringInFormat(mPastPayment.getPlannedDate(), "MM/dd/yyyy, hh:mm a"));
            mBalanceText.setText(String.format(Locale.getDefault(), "%s%.2f", mPastPayment.getCurrency(), mPastPayment.getAmount()));

            mPaymentScheduleContainer.setVisibility(View.GONE);

        }
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

    @Override
    public void populatePaymentSchedule() {
        List<PlannedTransaction> plannedTransactions = mPaymentPlan.getPlannedTransactions();

        populatePaymentScheduleCell(plannedTransactions.get(0), mPaymentScheduleCircle1, mPaymentScheduleDate1, mPaymentScheduleValue1);

        populatePaymentScheduleCell(plannedTransactions.get(1), mPaymentScheduleCircle2, mPaymentScheduleDate2, mPaymentScheduleValue2);

        populatePaymentScheduleCell(plannedTransactions.get(2), mPaymentScheduleCircle3, mPaymentScheduleDate3, mPaymentScheduleValue3);

        populatePaymentScheduleCell(plannedTransactions.get(3), mPaymentScheduleCircle4, mPaymentScheduleDate4, mPaymentScheduleValue4);
    }

    @Override
    public void populatePaymentScheduleCell(PlannedTransaction transaction, ImageView circle, TextView date, TextView value) {

        if (transaction.getState().equalsIgnoreCase("successful")) {
            circle.setImageResource(R.drawable.ic_schedule_successful);
        } else if (transaction.getState().equalsIgnoreCase("pending")) {
            circle.setImageResource(R.drawable.ic_schedule_pending);
        } else if (transaction.getState().equalsIgnoreCase("cancelled")) {
            circle.setImageResource(R.drawable.ic_schedule_cancelled);
        }

        date.setText(DateUtils.getDateFromStringInFormat(transaction.getPlannedDate(), "MMM dd").toLowerCase());

        value.setText(String.format(Locale.getDefault(), "%s%.2f", mPaymentPlan.getCurrencySign(), transaction.getAmount()));
    }
}
