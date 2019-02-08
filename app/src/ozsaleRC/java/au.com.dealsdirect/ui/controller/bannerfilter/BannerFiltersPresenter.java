package au.com.dealsdirect.ui.controller.bannerfilter;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
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
        if (isViewAttached()) {
            if (!getMvpView().isNetworkConnected()) {
                getMvpView().showNoNetworkLayout();
            } else {
                getMvpView().hideNoNetworklayout();
            }
        }
        doApiCallForResponse(getDataManager()
                .callGetGetCategories(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showCategories((List<GetCategoryTreeResponse>) response);
            }
        });
    }
}
