package au.com.dealsdirect.ui.controller.categories;

import android.util.Log;

import com.androidnetworking.error.ANError;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/6/17.
 */

public class CategoriesPresenter<V extends CategoriesMvpView> extends BasePresenter<V> implements
        CategoriesMvpPresenter<V> {

    @Inject
    public CategoriesPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadPublicSalesCategories(final GetPublicSalesCategoriesRequest request) {
        getCompositeDisposable().add(getDataManager()
                .callGetPublicSalesCategories(request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<GetPublicSalesCategoriesResponse>() {
                    @Override
                    public void accept(GetPublicSalesCategoriesResponse response) throws Exception {

                        if (!isViewAttached()) {
                            return;
                        }

                        getMvpView().hideLoading();
                        if(response.d.result && response.d.list != null){
                            getMvpView().showPublicSalesCategories(response.d.list);
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

    @Override
    public void loadCategoryTree() {
        getCompositeDisposable().add(getDataManager()
                .callGetGetCategories()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {

                    if (!isViewAttached()) {
                        return;
                    }

                    Log.d("CategoryPresenter","success load category tree");

                    if (response != null) {

                        getMvpView().showCategories(response);
                    }

                    getMvpView().hideLoading();

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
