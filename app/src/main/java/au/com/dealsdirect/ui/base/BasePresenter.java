package au.com.dealsdirect.ui.base;


import com.androidnetworking.error.ANError;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.ApiCallback;
import au.com.dealsdirect.data.network.ApiEndPoint;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
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

    private boolean cancelled;

    private V mMvpView;

    @Inject
    public BasePresenter(DataManager dataManager,
                         SchedulerProvider schedulerProvider,
                         CompositeDisposable compositeDisposable) {
        this.mDataManager = dataManager;
        this.mSchedulerProvider = schedulerProvider;
        this.mCompositeDisposable = compositeDisposable;
        this.cancelled = false;
    }

    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
        cancelled = false;
    }

    @Override
    public void onDetach() {
        mCompositeDisposable.dispose();
        mMvpView = null;
    }

    public boolean isViewAttached() {
        return mMvpView != null && mMvpView.isViewAttached();
    }

    public V getMvpView() throws MvpViewNotAttachedException {
        if (mMvpView == null) {
            throw new MvpViewNotAttachedException();
        }
        return mMvpView;
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

    public void cancel() {
        cancelled = true;
    }

    @Override
    public void handleApiError(ANError error) {

    }

    @Override
    public void setLastCartRedirection(String lastRedirection) {
        getDataManager().setLastRedirection(lastRedirection);
    }

    @Override
    public boolean hasActiveCheckoutSession() {
        return getDataManager().hasActiveCheckoutSession();
    }

    @Override
    public void setActiveCheckoutSessionFalse() {
        getDataManager().setHasActiveCheckoutSession(false);
    }

    @Override
    public boolean isTablet() {
        return getDataManager().isTablet();
    }

    @Override
    public boolean isGdprDisabled() {
        //consent mode 0, gdpr disabled. -1 default return value from preferences if no response is saved in preferences.
        return ApiEndPoint.LEGACY_API_VERSION < 3.24;
    }

    @Override
    public void setIsNewUser(boolean isNewUser) {
        getDataManager().setIsNewUser(isNewUser);
    }

    @Override
    public boolean getIsNewUser() {
        return getDataManager().getIsNewUser();
    }

    @Override
    public Disposable doApiCallForResponse(Observable observable, ApiCallback callback) {
        Disposable disposable = observable
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object response) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideNoNetworkLayout();
                        getMvpView().hideLoading();

                        if (response instanceof List && !cancelled) {
                            callback.onSuccess((List) response);
                        } else if (response != null && !cancelled) {
                            callback.onSuccess(response);
                        } else if (!cancelled) {
                            callback.onSuccess();
                        }

                        if (getMvpView() instanceof BasePullToRefreshController) {
                            getMvpView().hideNoNetworkLayout();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();

                        if (throwable.getCause() instanceof SocketTimeoutException || throwable.getCause() instanceof UnknownHostException) {
                            getMvpView().showNoNetworkLayout();
                        }

                        getMvpView().onError(throwable.getMessage());

                        if (!cancelled) {
                            callback.onFailure(throwable);
                        }

                        // handle load accounts error here
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError);
                        }
                    }
                });

        getCompositeDisposable().add(disposable);

        return disposable;
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("Please call Presenter.onAttach(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
