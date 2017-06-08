package au.com.dealsdirect.data;

import android.content.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.data.network.ApiHeader;
import au.com.dealsdirect.data.network.ApiHelper;
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
import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.di.ApplicationContext;
import io.reactivex.Observable;


@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          PreferencesHelper preferencesHelper,
                          ApiHelper apiHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHelper.getApiHeader();
    }

    @Override
    public Observable<SampleResponse> doSampleApiCall(SampleRequest request) {
        return mApiHelper.doSampleApiCall(request);
    }

    @Override public Observable<GetPublicSalesBannerResponse> getPublicSalesBannerApiCall(GetPublicSalesBannerRequest getPublicSalesBannerRequest) {
        return mApiHelper.getPublicSalesBannerApiCall(getPublicSalesBannerRequest);
    }

    @Override
    public Observable<GetPublicSalesCategoriesResponse> doGetPublicSalesCategoriesApiCall(GetPublicSalesCategoriesRequest request) {
        return mApiHelper.doGetPublicSalesCategoriesApiCall(request);
    }

    @Override public Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(
            GetPublicSaleItemsRequest getPublicSaleItemsRequest) {
        return mApiHelper.getPublicSaleItemsApiCall(getPublicSaleItemsRequest);
    }

    @Override
    public Observable<GetPublicItemDetailsResponse> doGetPublicItemDetailsApiCall(GetPublicItemDetailsRequest request) {
        return mApiHelper.doGetPublicItemDetailsApiCall(request);
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return 0;
    }

    @Override
    public void updateApiHeader(Long userId, String accessToken) {

    }

    @Override
    public void setUserAsLoggedOut() {

    }

    @Override
    public Observable<Boolean> seedDatabaseQuestions() {
        return null;
    }

    @Override
    public Observable<Boolean> seedDatabaseOptions() {
        return null;
    }

    @Override
    public void updateUserInfo(String accessToken, Long userId, LoggedInMode loggedInMode, String userName, String email, String profilePicPath) {

    }
}
