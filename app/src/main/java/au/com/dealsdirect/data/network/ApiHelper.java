package au.com.dealsdirect.data.network;


import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import io.reactivex.Observable;

public interface ApiHelper {

    //Elv - declare api calls here

    ApiHeader getApiHeader();

    Observable<SampleResponse> doSampleApiCall(SampleRequest request);

    Observable<GetPublicSalesBannerResponse> getPublicSalesBannerApiCall
            (GetPublicSalesBannerRequest getPublicSalesBannerRequest);

    Observable<GetPublicSalesCategoriesResponse> doGetPublicSalesCategoriesApiCall(GetPublicSalesCategoriesRequest request);

    Observable<GetPublicItemDetailsResponse> doGetPublicItemDetailsApiCall(GetPublicItemDetailsRequest request);
  
    Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(GetPublicSaleItemsRequest
            getPublicSaleItemsRequest);
}
