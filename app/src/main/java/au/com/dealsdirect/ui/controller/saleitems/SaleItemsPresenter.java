package au.com.dealsdirect.ui.controller.saleitems;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * dp Created by Admin on 6/8/17.
 */

public class SaleItemsPresenter<V extends SaleItemsMvpView> extends BasePresenter<V>
        implements SaleItemsMvpPresenter<V> {

    String mSaleId="";
    @Inject
    public SaleItemsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider,
            CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadSaleItems(String categoryKey, JSONObject facetFilter) {
        getMvpView().showLoading();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();
        getSaleItemsRequest.setFacetFilter(facetFilter);
        getSaleItemsRequest.setCategoryKey(categoryKey);
        getSaleItemsRequest.setLanguageID(getDataManager().getLanguageId());
        getSaleItemsRequest.setPageNumber(String.valueOf(10));
        getSaleItemsRequest.setQuery("");
        getSaleItemsRequest.setPageSize("");

        getCompositeDisposable()
                .add(getDataManager()
                        .getSaleItemsRequest(getSaleItemsRequest)
                        .subscribeOn(getSchedulerProvider().io())
                        .observeOn(getSchedulerProvider().ui())
                        .subscribe(new Consumer<GetSaleItemsResponse>() {
                            @Override public void accept(
                                    @NonNull GetSaleItemsResponse getSaleItemsResponse)
                                    throws Exception {

                                if (!isViewAttached()) {
                                    return;
                                }
                                getMvpView().hideLoading();
                                if(!getSaleItemsResponse.products.isEmpty()){

                                    getMvpView().showSaleItems(getSaleItemsResponse);
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
    public void loadProductDetails(String imageUrl, String itemId, String saleId){
        getMvpView().showProductDetails(imageUrl,itemId,saleId);
    }

}
