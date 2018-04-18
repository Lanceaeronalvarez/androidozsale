package au.com.dealsdirect.ui.controller.bannerfilter;

import com.androidnetworking.error.ANError;

import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by pauldesilva on 4/13/18.
 */

public class BannerFiltersPresenter<V extends BannerFiltersMvpView> extends BasePresenter<V> implements
        BannerFiltersMvpPresenter<V> {

    @Inject
    public BannerFiltersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void callGetCategoryTree() {
        if (!getMvpView().isNetworkConnected()) {
            getMvpView().showNoNetworkLayout();
        } else {
            getMvpView().hideNoNetworklayout();
        }

        getCompositeDisposable().add(getDataManager()
                .callGetGetCategories()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    if (response != null) {

                        getMvpView().showCategories(response);
                    }

                    getMvpView().hideLoading();

                    getMvpView().hideNoNetworklayout();

                }, throwable -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    getMvpView().hideLoading();
                    getMvpView().onError(throwable.getMessage());

                    // handle load accounts error here
                    if (throwable instanceof ANError) {
                        ANError anError = (ANError) throwable;
                        handleApiError(anError);
                    }
                }));
    }
}
