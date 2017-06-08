package au.com.dealsdirect.ui.controller.shops;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.banner.BannerRequest;
import au.com.dealsdirect.data.network.model.banner.BannerResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class ShopsPresenter<V extends ShopsMvpView> extends BasePresenter<V> implements
        ShopsMvpPresenter<V> {

    @Inject
    public ShopsPresenter(
            DataManager dataManager,
            SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {

        super(dataManager, schedulerProvider, compositeDisposable);
    }


    @Override public void loadShopsBanner(BannerRequest request) {
        getMvpView().showLoading();

        getCompositeDisposable()
                .add(getDataManager()
                             .getPublicSalesBanner(request)
                             .subscribeOn(getSchedulerProvider().io())
                             .observeOn(getSchedulerProvider().ui())
                             .subscribe(new Consumer<BannerResponse>() {

                                 @Override
                                 public void accept(BannerResponse response) throws Exception {

                                  if (!isViewAttached()) {
                                      return;
                                  }

                                  getMvpView().hideLoading();

                                  if (response.getBanner().getList().isEmpty()) {
//                                      getMvpView().showNoResultsLayout();

                                  } else {
                                      getMvpView().showShopBanners(response);
                                  }

                                 }
                             }, new Consumer<Throwable>() {


                                 @Override
                                 public void accept(Throwable throwable) throws Exception {

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
                                 }
                             }));
    }
}

