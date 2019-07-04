package au.com.dealsdirect.ui.controller.ourpay;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiCallback;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydata.ProcessOurpayInstallmentRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class MyAccountsOurpayPresenter<V extends MyAccountsOurpayMvpView> extends BasePresenter<V> implements MyAccountsOurpayMvpPresenter<V> {

    private boolean mHasReceivedPaymentPlans = false;
    private boolean mHasReceivedScheduledPayments = false;
    private boolean mHasRecievedPastPayments = false;
    private boolean mShouldReloadPastPayment = false;

    @Inject
    MyAccountsOurpayPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);

    }


    public void fetchDataForSummary() {
        doApiCallForResponse(getDataManager()
                .callGetPaymentPlans(getDataManager().getCountryId(),
                        getDataManager().getLanguageId()), new ApiCallback() {
            @Override
            public void onSuccess() {
                getMvpView().showEmptyView();
                getMvpView().setDataForSummary(null);
            }

            @Override
            public void onSuccess(Object o) {
                if (!(o instanceof GetPaymentPlansResponse)) {
                    getMvpView().showEmptyView();
                    getMvpView().setDataForSummary(null);
                    getMvpView().setDataForPaymentPlans(null);
                    return;
                }

                GetPaymentPlansResponse response = (GetPaymentPlansResponse) o;

                if (response.getValue() != null) {
                    if (response.getValue().getActivePlansCount() > 0) {
                        getMvpView().showTabbedView();
                    } else {
                        getMvpView().showEmptyView();
                    }

                    getMvpView().setDataForSummary(MyAccountsOurpayResponseReducer.createSummary(response));

                    mHasReceivedPaymentPlans = true;
                    getMvpView().setDataForPaymentPlans(response);
                } else {
                    getMvpView().showEmptyView();
                    getMvpView().setDataForSummary(null);
                    getMvpView().setDataForPaymentPlans(null);
                }
            }

            @Override
            public void onSuccess(List<?> list) {
                getMvpView().showEmptyView();
                getMvpView().setDataForSummary(null);
            }

            @Override
            public void onFailure(Throwable t) {
                getMvpView().showEmptyView();
                getMvpView().setDataForSummary(null);
            }
        });
    }

    public void fetchDataForPaymentPlans() {
        if (mHasReceivedPaymentPlans) {
            return;
        }

        doApiCallForResponse(getDataManager()
                .callGetPaymentPlans(getDataManager().getCountryId(),
                        getDataManager().getLanguageId()), new ApiCallback() {
            @Override
            public void onSuccess() {
                getMvpView().setDataForPaymentPlans(null);
            }

            @Override
            public void onSuccess(Object o) {
                mHasReceivedPaymentPlans = true;
                GetPaymentPlansResponse response = (GetPaymentPlansResponse) o;
                getMvpView().setDataForPaymentPlans(response);
            }

            @Override
            public void onSuccess(List<?> list) {
                getMvpView().setDataForPaymentPlans(null);
            }

            @Override
            public void onFailure(Throwable t) {
                getMvpView().setDataForPaymentPlans(null);
            }
        });
    }

    public void fetchDataForScheduledPayments() {
        if (mHasReceivedScheduledPayments) {
            return;
        }

        doApiCallForResponse(getDataManager()
                .callGetScheduledPlans(getDataManager().getCountryId(),
                        getDataManager().getLanguageId()), new ApiCallback() {
            @Override
            public void onSuccess() {
                getMvpView().setDataForScheduledPayments(null);
            }

            @Override
            public void onSuccess(Object o) {
                mHasReceivedScheduledPayments = true;
                GetScheduledPlansResponse response = (GetScheduledPlansResponse) o;
                getMvpView().setDataForScheduledPayments(response);
            }

            @Override
            public void onSuccess(List<?> list) {
                getMvpView().setDataForScheduledPayments(null);
            }

            @Override
            public void onFailure(Throwable t) {
                getMvpView().setDataForScheduledPayments(null);
            }
        });
    }

    public void fetchDataForPastPayments() {
        if (mHasRecievedPastPayments && !mShouldReloadPastPayment) {
            return;
        }

        doApiCallForResponse(getDataManager()
                .callGetPastPayments(getDataManager().getCountryId(),
                        getDataManager().getLanguageId()), new ApiCallback() {
            @Override
            public void onSuccess() {
                getMvpView().setDataForScheduledPayments(null);
            }

            @Override
            public void onSuccess(Object o) {
                mHasRecievedPastPayments = true;
                mShouldReloadPastPayment = false;
                GetPastPaymentsResponse response = (GetPastPaymentsResponse) o;
                getMvpView().setDataForPastPayments(response);
            }

            @Override
            public void onSuccess(List<?> list) {
                getMvpView().setDataForScheduledPayments(null);
            }

            @Override
            public void onFailure(Throwable t) {
                getMvpView().setDataForScheduledPayments(null);
            }
        });
    }

    @Override
    public void processOurpayInstallment(ProcessOurpayInstallmentRequest request) {
        doApiCallForResponse(getDataManager()
                .processOurpayInstallment(request), new ApiCallback() {
            @Override
            public void onSuccess() {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                }
            }

            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    if (isViewAttached()) {
                        getMvpView().hideLoading();
                    }
                    mShouldReloadPastPayment = true;
                    GetScheduledPlansResponse response = (GetScheduledPlansResponse) o;
                    getMvpView().setDataForScheduledPayments(response);
                }
            }

            @Override
            public void onSuccess(List<?> list) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                }
                getMvpView().setDataForScheduledPayments(null);
            }

            @Override
            public void onFailure(Throwable t) {
                if (isViewAttached()) {
                    getMvpView().hideLoading();
                }
                getMvpView().setDataForScheduledPayments(null);
            }
        });
    }
}
