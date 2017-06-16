package au.com.dealsdirect.data;

import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.data.network.ApiHeader;
import au.com.dealsdirect.data.network.ApiHelper;
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
    public Observable<GetServerSettings.ResponseValue> callGetServerSettings(Context context, String countryId) {
        return mApiHelper.callGetServerSettings(context,countryId);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(Context context, String countryId) {
        return mApiHelper.callGetPublicAppSettings(context,countryId);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetAppSettings(Context context, String countryId) {
        return mApiHelper.callGetAppSettings(context,countryId);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(Context context, String countryId) {
        return mApiHelper.callGetAppSettingsSection(context,countryId);
    }


    @Override
    public Observable<GetPublicItemDetailsResponse> doGetPublicItemDetailsApiCall(GetPublicItemDetailsRequest request) {
        return mApiHelper.doGetPublicItemDetailsApiCall(request);
    }

    @Override
    public Observable<GetPublicSaleDetailsResponse> doGetPublicSaleDetailsApiCall(GetPublicSaleDetailsRequest request) {
        return mApiHelper.doGetPublicSaleDetailsApiCall(request);
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return 0;
    }

    @Override
    public String getCountryId() {
        return null;
    }

    @Override
    public String getLanguageId() {
        return null;
    }

    @Override
    public List<Language> getLanguages() {
        return null;
    }

    @Override
    public String getCurrency() {
        return null;
    }

    @Override
    public String getCurrencySign() {
        return null;
    }

    @Override
    public String getFollowUsFbLink() {
        return null;
    }

    @Override
    public String getFollowUsTwitterLink() {
        return null;
    }

    @Override
    public boolean isPaypalEnabled() {
        return false;
    }

    @Override
    public boolean isAmexEnabled() {
        return false;
    }

    @Override
    public boolean isMasterpassEnabled() {
        return false;
    }

    @Override
    public boolean isKountEnabled() {
        return false;
    }

    @Override
    public String getKountMerchantId() {
        return null;
    }

    @Override
    public boolean isDebugMode() {
        return false;
    }

    @Override
    public String getFbSecret() {
        return null;
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
