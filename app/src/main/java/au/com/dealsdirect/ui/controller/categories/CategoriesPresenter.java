package au.com.dealsdirect.ui.controller.categories;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeRequest;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

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
    public void callGetCategoryTree() {
        if (!getMvpView().isNetworkConnected()) {
            getMvpView().showNoNetworkLayout();
        } else {
            getMvpView().hideNoNetworklayout();
        }

        doApiCallForResponse(getDataManager()
                .callGetCategories(new GetCategoryTreeRequest()), new AppApiCallback() {
            @Override
            public void onSuccess(List<?> response) {
                super.onSuccess(response);
                getMvpView().showCategories((List<GetCategoryTreeResponse>) response);
            }
        });
    }
}
