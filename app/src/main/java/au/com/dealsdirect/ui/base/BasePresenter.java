package au.com.dealsdirect.ui.base;


import com.androidnetworking.error.ANError;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiCallback;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private static final String TAG = "BasePresenter";

    private final DataManager mDataManager;
    private final SchedulerProvider mSchedulerProvider;
    private final CompositeDisposable mCompositeDisposable;

    private V mMvpView;

    @Inject
    public BasePresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = compositeDisposable;
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void onDetach() {
        mCompositeDisposable.dispose();
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null;
    }

    public V getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new MvpViewNotAttachedException();
    }

    public DataManager getDataManager() {
        return mDataManager;
    }

    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    public void handleApiError(ANError error) {

//        if (error == null || error.getErrorBody() == null) {
//            getMvpView().onError(R.string.api_default_error);
//            return;
//        }
//
//        if (error.getErrorCode() == AppConstants.API_STATUS_CODE_LOCAL_ERROR
//                && error.getErrorDetail().equals(ANConstants.CONNECTION_ERROR)) {
//            getMvpView().onError(R.string.connection_error);
//            return;
//        }
//
//        if (error.getErrorCode() == AppConstants.API_STATUS_CODE_LOCAL_ERROR
//                && error.getErrorDetail().equals(ANConstants.REQUEST_CANCELLED_ERROR)) {
//            getMvpView().onError(R.string.api_retry_error);
//            return;
//        }
//
//        final GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
//        final Gson gson = builder.create();
//
//        try {
//            ApiError apiError = gson.fromJson(error.getErrorBody(), ApiError.class);
//
//            if (apiError == null || apiError.getMessage() == null) {
//                getMvpView().onError(R.string.api_default_error);
//                return;
//            }
//
//            switch (error.getErrorCode()) {
//                case HttpsURLConnection.HTTP_UNAUTHORIZED:
//                case HttpsURLConnection.HTTP_FORBIDDEN:
//                    setUserAsLoggedOut();
//                    getMvpView().openActivityOnTokenExpire();
//                case HttpsURLConnection.HTTP_INTERNAL_ERROR:
//                case HttpsURLConnection.HTTP_NOT_FOUND:
//                default:
//                    getMvpView().onError(apiError.getMessage());
//            }
//        } catch (JsonSyntaxException | NullPointerException e) {
//            Log.e(TAG, "handleApiError", e);
//            getMvpView().onError(R.string.api_default_error);
//        }
    }

    @Override
    public boolean isTablet() {
        return getDataManager().isTablet();
    }

    @Override
    public void doApiCallForResponse(Observable observable, final ApiCallback callback) {
//        getMvpView().showLoading();

//        if(getMvpView() instanceof BasePullToRefreshController) {
//            if(getMvpView().isNetworkConnected()) {
//                ((BasePullToRefreshController) getMvpView()).hideNoNetworkLayout();
//            } else {
//                ((BasePullToRefreshController) getMvpView()).showNoNetworkLayout();
//            }
//        }

        getMvpView().hideNoNetworkLayout();

        getCompositeDisposable().add(observable
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (response instanceof List) {
                            callback.onSuccess((List) response);
                        } else if (response != null) {
                            callback.onSuccess(response);
                        } else {
                            callback.onSuccess();
                        }

                        if(getMvpView() instanceof BasePullToRefreshController) {
                            ((BasePullToRefreshController) getMvpView()).hideNoNetworkLayout();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if(throwable instanceof SocketTimeoutException || throwable instanceof UnknownHostException){
                            getMvpView().showNoNetworkLayout();
                        }

                        getMvpView().onError(throwable.getMessage());

                        callback.onFailure(throwable);

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                }));
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.onAttach(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
