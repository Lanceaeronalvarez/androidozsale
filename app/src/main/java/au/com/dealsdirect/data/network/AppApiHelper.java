package au.com.dealsdirect.data.network;

import com.rx2androidnetworking.Rx2AndroidNetworking;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.banner.BannerRequest;
import au.com.dealsdirect.data.network.model.banner.BannerResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import io.reactivex.Observable;

@Singleton
public class AppApiHelper implements ApiHelper {

    private ApiHeader mApiHeader;

    @Inject
    public AppApiHelper(ApiHeader apiHeader) {
        mApiHeader = apiHeader;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHeader;
    }

    //Override apiHelper exposed methods here

    @Override
    public Observable<SampleResponse> doSampleApiCall(SampleRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SAMPLE_API)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectObservable(SampleResponse.class);
    }

    @Override public Observable<GetPublicSalesBannerResponse> getPublicSalesBannerApiCall(
            GetPublicSalesBannerRequest getPublicSalesBannerRequest) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SHOP_BANNERS)
                                   .addHeaders(mApiHeader.getPublicApiHeader())
                                   .addQueryParameter(getPublicSalesBannerRequest)
                                   .build()
                                   .getObjectObservable(GetPublicSalesBannerResponse.class);
    }


    @Override
    public Observable<GetPublicSalesCategoriesResponse> doGetPublicSalesCategoriesApiCall(GetPublicSalesCategoriesRequest request){
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SHOP_CATEGORIES)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSalesCategoriesResponse.class);
    }

    @Override
    public Observable<GetPublicItemDetailsResponse> doGetPublicItemDetailsApiCall(GetPublicItemDetailsRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SALES_ITEM_DETAILS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicItemDetailsResponse.class);
 


   @Override public Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(
            GetPublicSaleItemsRequest getPublicSaleItemsRequest) {

        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PUBLIC_SALE_ITEMS)
                                   .addHeaders(mApiHeader.getPublicApiHeader())
                                   .addQueryParameter(getPublicSaleItemsRequest)
                                   .build()
                                   .getObjectObservable(GetPublicSaleItemsResponse.class);
    }
}

