package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ShopsPresenter<V extends ShopsMvpView> extends BasePresenter<V> implements
        ShopsMvpPresenter<V> {

    @Inject
    public ShopsPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadShopsBanner(String categoryName, String categoryId) {

        GetBannerRequest getBannerRequest = new GetBannerRequest();
        getBannerRequest.setOffset(String.valueOf(0));
        getBannerRequest.setLimit(String.valueOf(10));

        getBannerRequest.setCategory(categoryName);
        getBannerRequest.setCategoryId(categoryId);
//
//        if (categoryName==null)
//            getBannerRequest.setCategory("");
//        if (categoryId==null)
//            getBannerRequest.setCategoryId("");



        getMvpView().showLoading();

        getCompositeDisposable()
                .add(getDataManager()
                        .doGetBannersApiCall(getBannerRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(response -> {

                            if (!isViewAttached()) {
                                return;
                            }

                            getMvpView().hideLoading();

                            if (response.isEmpty()) {
//                             getMvpView().showNoResultsLayout();

                            } else {
                                getMvpView().showShopBanners(response);
                            }

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

