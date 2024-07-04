package au.com.dealsdirect.ui.controller.brands;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class TopBrandsPresenter<V extends TopBrandsMvpView> extends BasePresenter<V> implements
        TopBrandsMvpPresenter<V> {

    private Disposable mPreviousLoadShopsBannerRequest = null;

    @Inject
    public TopBrandsPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadTopBrands() {
        doApiCallForResponse(getDataManager().callGetTopBrands(), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                if (response != null && isViewAttached()) {
                    getMvpView().showTopBrands((List<GetTopBrandsResponse>) response);
                }
            }
        });
    }

    @Override
    public void loadTrendingBrands(GetBannerRequest request) {
        doApiCallForResponse(
                getDataManager().callGetBanners2(request, false), new AppApiCallback() {
                    @Override
                    public void onSuccess(Object response) {
                        super.onSuccess(response);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showTrendingBrands((GetBannerResponse) response);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        super.onFailure(t);
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().showTrendingBrands(null);
                    }
                });
    }

    @Override
    public void cancelRequest() {
        cancel();
    }

    @Override
    public boolean isAuthorized() {
        return getDataManager().isAuthorized();
    }

    @Override
    public int getBannerColumnCount() {
        return isTablet() ? getDataManager().getMobileTabletBannerColumns() : getDataManager().getMobilePhoneBannerColumns();
    }
}

