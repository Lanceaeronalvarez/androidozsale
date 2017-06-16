package au.com.dealsdirect.data.network;


import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;

import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsResponse;
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

    Observable<GetPublicSaleDetailsResponse> doGetPublicSaleDetailsApiCall(GetPublicSaleDetailsRequest request);

    Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(GetPublicSaleItemsRequest
            getPublicSaleItemsRequest);

    Observable<GetServerSettings.ResponseValue> callGetServerSettings(Context context, String countryId);

    Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(Context context, String countryId);

    Observable<GetAppSettings.ResponseValue> callGetAppSettings(Context context, String countryId);

    Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(Context context, String countryId);
}
