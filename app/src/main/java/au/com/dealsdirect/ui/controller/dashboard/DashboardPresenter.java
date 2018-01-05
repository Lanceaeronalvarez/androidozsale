package au.com.dealsdirect.ui.controller.dashboard;
/*
 * Created by CodeineBot on 5/15/17.
 */

import com.google.gson.Gson;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.ourpaydashboard.deliveryservice.GetDeliveryServiceResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class DashboardPresenter<V extends DashboardMvpView> extends BasePresenter<V> implements DashboardMvpPresenter<V> {

    private static final String SAMPLE_PAYMENT_PLANS = "{  \n" +
            "   \"d\":{  \n" +
            "      \"IsAuthenticated\":true,\n" +
            "      \"Value\":{  \n" +
            "         \"PaymentPlans\":[  \n" +
            "            {  \n" +
            "               \"ID\":\"e4gcepcntl2eomc6ujvnoe5qw\",\n" +
            "               \"Name\":\"Ozsale\",\n" +
            "               \"OrderNo\":\"24311424\",\n" +
            "               \"OrderBalance\":12.75,\n" +
            "               \"TotalAmount\":17.00,\n" +
            "               \"RefundAmount\":0,\n" +
            "               \"Currency\":\"AUD\",\n" +
            "               \"CurrencySign\":\"$\",\n" +
            "               \"IsOverdue\":false,\n" +
            "               \"PlannedTransactions\":[  \n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1511308800000)\\/\",\n" +
            "                     \"Amount\":4.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Successful\",\n" +
            "                     \"Number\":1\n" +
            "                  },\n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1512518400000)\\/\",\n" +
            "                     \"Amount\":4.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Pending\",\n" +
            "                     \"Number\":2\n" +
            "                  },\n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1513728000000)\\/\",\n" +
            "                     \"Amount\":4.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Pending\",\n" +
            "                     \"Number\":3\n" +
            "                  },\n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1514937600000)\\/\",\n" +
            "                     \"Amount\":4.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Pending\",\n" +
            "                     \"Number\":4\n" +
            "                  }\n" +
            "               ]\n" +
            "            },\n" +
            "            {  \n" +
            "               \"ID\":\"5wq6xtf7zsra3gbmgdgb99q34\",\n" +
            "               \"Name\":\"Ozsale\",\n" +
            "               \"OrderNo\":\"24311425\",\n" +
            "               \"OrderBalance\":51.75,\n" +
            "               \"TotalAmount\":69.00,\n" +
            "               \"RefundAmount\":0,\n" +
            "               \"Currency\":\"AUD\",\n" +
            "               \"CurrencySign\":\"$\",\n" +
            "               \"IsOverdue\":false,\n" +
            "               \"PlannedTransactions\":[  \n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1511308800000)\\/\",\n" +
            "                     \"Amount\":17.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Successful\",\n" +
            "                     \"Number\":1\n" +
            "                  },\n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1512518400000)\\/\",\n" +
            "                     \"Amount\":17.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Pending\",\n" +
            "                     \"Number\":2\n" +
            "                  },\n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1513728000000)\\/\",\n" +
            "                     \"Amount\":17.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Pending\",\n" +
            "                     \"Number\":3\n" +
            "                  },\n" +
            "                  {  \n" +
            "                     \"PlannedDate\":\"\\/Date(1514937600000)\\/\",\n" +
            "                     \"Amount\":17.25,\n" +
            "                     \"Currency\":\"AUD\",\n" +
            "                     \"State\":\"Pending\",\n" +
            "                     \"Number\":4\n" +
            "                  }\n" +
            "               ]\n" +
            "            }\n" +
            "         ],\n" +
            "         \"Currency\":\"AUD\",\n" +
            "         \"CurrencySign\":\"$\",\n" +
            "         \"OverduePlansCount\":0,\n" +
            "         \"RemainingBalance\":64.5,\n" +
            "         \"ActivePlansCount\":2,\n" +
            "         \"RemainingCredit\":185.5\n" +
            "      },\n" +
            "      \"Result\":true,\n" +
            "      \"Message\":\"\"\n" +
            "   }\n" +
            "}";

    private static final String SAMPLE_SCHEDULED_PLANS = "{  \n" +
            "   \"d\":{  \n" +
            "      \"IsAuthenticated\":true,\n" +
            "      \"Value\":[  \n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1512518400000)\\/\",\n" +
            "            \"Amount\":4.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Pending\",\n" +
            "            \"Number\":2,\n" +
            "            \"OrderNo\":\"24311424\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1512518400000)\\/\",\n" +
            "            \"Amount\":17.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Pending\",\n" +
            "            \"Number\":2,\n" +
            "            \"OrderNo\":\"24311425\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1513728000000)\\/\",\n" +
            "            \"Amount\":4.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Pending\",\n" +
            "            \"Number\":3,\n" +
            "            \"OrderNo\":\"24311424\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1513728000000)\\/\",\n" +
            "            \"Amount\":17.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Pending\",\n" +
            "            \"Number\":3,\n" +
            "            \"OrderNo\":\"24311425\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1514937600000)\\/\",\n" +
            "            \"Amount\":4.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Pending\",\n" +
            "            \"Number\":4,\n" +
            "            \"OrderNo\":\"24311424\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1514937600000)\\/\",\n" +
            "            \"Amount\":17.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Pending\",\n" +
            "            \"Number\":4,\n" +
            "            \"OrderNo\":\"24311425\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         }\n" +
            "      ],\n" +
            "      \"Result\":true,\n" +
            "      \"Message\":\"\"\n" +
            "   }\n" +
            "}";

    private static final String SAMPLE_PAST_PAYMENTS = "{  \n" +
            "   \"d\":{  \n" +
            "      \"IsAuthenticated\":true,\n" +
            "      \"Value\":[  \n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1511308800000)\\/\",\n" +
            "            \"Amount\":4.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Successful\",\n" +
            "            \"Number\":1,\n" +
            "            \"OrderNo\":\"24311424\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         },\n" +
            "         {  \n" +
            "            \"PlannedDate\":\"\\/Date(1511308800000)\\/\",\n" +
            "            \"Amount\":17.25,\n" +
            "            \"Currency\":\"$\",\n" +
            "            \"State\":\"Successful\",\n" +
            "            \"Number\":1,\n" +
            "            \"OrderNo\":\"24311425\",\n" +
            "            \"PaymentMethod\":\"Visa\",\n" +
            "            \"Name\":\"Ozsale\",\n" +
            "            \"MaskedNumber\":\"400000******0002\"\n" +
            "         }\n" +
            "      ],\n" +
            "      \"Result\":true,\n" +
            "      \"Message\":\"\"\n" +
            "   }\n" +
            "}";

    private static final String SAMPLE_DELIVERY = "{  \n" +
            "   \"d\":{  \n" +
            "      \"IsAuthenticated\":true,\n" +
            "      \"Value\":[  \n" +
            "         {  \n" +
            "            \"InitialCount\":10,\n" +
            "            \"RemainingCount\":10,\n" +
            "            \"ExpiryDate\":\"\\/Date(1543386151743)\\/\"\n" +
            "         }\n" +
            "      ],\n" +
            "      \"Result\":true,\n" +
            "      \"Message\":\"\"\n" +
            "   }\n" +
            "}";

    // Ayv - Just to make it easier to flip between mock and api data during testing
    private boolean useMockData = true;

    @Inject
    public DashboardPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadAll() {
        getPaymentPlans();
        getScheduledPayments();
        getPastPayments();
        getDeliveryService();
    }

    @Override
    public void getPaymentPlans() {
        if (useMockData) {
            getMvpView().showPaymentPlans(new Gson().fromJson(SAMPLE_PAYMENT_PLANS, GetPaymentPlansResponse.class));
        } else {
            doApiCallForResponse(getDataManager().callGetPaymentPlans(), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    getMvpView().showPaymentPlans((GetPaymentPlansResponse) response);
                }
            });
        }
    }

    @Override
    public void getScheduledPayments() {
        if (useMockData) {
            getMvpView().showScheduledPlans(new Gson().fromJson(SAMPLE_SCHEDULED_PLANS, GetScheduledPlansResponse.class));
        } else {
            doApiCallForResponse(getDataManager().callGetScheduledPlans(), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    getMvpView().showScheduledPlans((GetScheduledPlansResponse) response);
                }
            });
        }
    }

    @Override
    public void getPastPayments() {
        if (useMockData) {
            getMvpView().showPastPayments(new Gson().fromJson(SAMPLE_PAST_PAYMENTS, GetPastPaymentsResponse.class));
        } else {
            doApiCallForResponse(getDataManager().callGetPastPayments(), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    getMvpView().showPastPayments((GetPastPaymentsResponse) response);
                }
            });
        }
    }

    @Override
    public void getDeliveryService() {
        if (useMockData) {
            getMvpView().showDeliveryService(new Gson().fromJson(SAMPLE_DELIVERY, GetDeliveryServiceResponse.class));
        } else {
            doApiCallForResponse(getDataManager().callGetDeliveryService(), new AppApiCallback() {
                @Override
                public void onSuccess(Object response) {
                    super.onSuccess(response);
                    getMvpView().showDeliveryService((GetDeliveryServiceResponse) response);
                }
            });
        }
    }
}
