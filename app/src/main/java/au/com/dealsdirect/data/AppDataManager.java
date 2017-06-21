package au.com.dealsdirect.data;

import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getserversettings.Language;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.data.auth.AuthHelper;
import au.com.dealsdirect.data.network.ApiHeader;
import au.com.dealsdirect.data.network.ApiHelper;
import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.address.AddAddress;
import au.com.dealsdirect.data.network.model.address.ApplyAddressRequest;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetPublicSalesBannerResponse;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginFacebook;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicItemDetailsResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.data.network.model.viewcontactitem.GetContactsResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.di.ApplicationContext;
import io.reactivex.Observable;


@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;
    private final AuthHelper mAuthHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          PreferencesHelper preferencesHelper,
                          ApiHelper apiHelper,
                          AuthHelper authHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
        mAuthHelper = authHelper;
    }

    @Override
    public ApiHeader getApiHeader() {
        return mApiHelper.getApiHeader();
    }

    @Override
    public Observable<SampleResponse> doSampleApiCall(SampleRequest request) {
        return mApiHelper.doSampleApiCall(request);
    }

    @Override
    public Observable<GetPublicSalesBannerResponse> getPublicSalesBannerApiCall(GetPublicSalesBannerRequest getPublicSalesBannerRequest) {
        return mApiHelper.getPublicSalesBannerApiCall(getPublicSalesBannerRequest);
    }

    @Override
    public Observable<GetPublicSalesCategoriesResponse> doGetPublicSalesCategoriesApiCall(GetPublicSalesCategoriesRequest request) {
        return mApiHelper.doGetPublicSalesCategoriesApiCall(request);
    }

    @Override
    public Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(
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
    public Observable<GetContactsResponse.Response> callGetContacts(String languageId) {
        return mApiHelper.callGetContacts(languageId);
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue) {
        return mApiHelper.callLoginViaEmail(requestValue);
    }

    @Override
    public Observable<LoginFacebook.ResponseValue> callLoginViaFacebook(LoginFacebook.RequestValue requestValue) {
        return mApiHelper.callLoginViaFacebook(requestValue);
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue) {
        return mApiHelper.callLoginTicket(requestValue);
    }

    @Override
    public Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue) {
        return mApiHelper.callLogout(requestValue);
    }

    @Override
    public Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues) {
        return mApiHelper.callGetUserAddresses(requestValues);
    }

    @Override
    public Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues) {
        return mApiHelper.callSetUserDeliveryAddress(requestValues);
    }

    @Override
    public Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues) {
        return mApiHelper.callApplyDeliveryAddress(requestValues);
    }

    @Override
    public Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues) {
        return mApiHelper.callDeleteUserDeliveryAddress(requestValues);
    }

    @Override
    public Observable<GetUserDetailsResponse.Response> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return mApiHelper.getSaveUserDetailsApiCall(setUserDetailsRequest);
    }

    @Override
    public Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall() {
        return mApiHelper.getLoadUserDetailsApiCall();
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


    /*
    CONFIG PREFS METHODS
     */
    @Override
    public void setUserAgent() {
        mPreferencesHelper.setUserAgent();
    }

    @Override
    public String getUserAgent() {
        return mPreferencesHelper.getUserAgent();
    }

    @Override
    public void setCountryId(String countryId) {
        mPreferencesHelper.setCountryId(countryId);
    }

    @Override
    public String getCountryId() {
        return mPreferencesHelper.getCountryId();
    }

    @Override
    public void setLanguageId(String languageId) {
        mPreferencesHelper.setLanguageId(languageId);
    }

    @Override
    public String getLanguageId() {
        return mPreferencesHelper.getLanguageId();
    }

    @Override
    public void setLanguages(List<Language> languages) {
        mPreferencesHelper.setLanguages(languages);
    }

    @Override
    public List<Language> getLanguages() {
        return mPreferencesHelper.getLanguages();
    }

    @Override
    public void setSiteName(String siteName) {
        mPreferencesHelper.setSiteName(siteName);
    }

    @Override
    public String getSiteName() {
        return mPreferencesHelper.getSiteName();
    }

    @Override
    public void setCurrency(String currency) {
        mPreferencesHelper.setCurrency(currency);
    }

    @Override
    public String getCurrency() {
        return mPreferencesHelper.getCurrency();
    }

    @Override
    public void setCurrencySign(String currencySign) {
        mPreferencesHelper.setCurrencySign(currencySign);
    }

    @Override
    public String getCurrencySign() {
        return mPreferencesHelper.getCurrencySign();
    }

    @Override
    public void setFollowUsFbLink(String followUsFbLink) {
        mPreferencesHelper.setFollowUsFbLink(followUsFbLink);
    }

    @Override
    public String getFollowUsFbLink() {
        return mPreferencesHelper.getFollowUsFbLink();
    }

    @Override
    public void setFollowUsTwitterLink(String followUsTwitterLink) {
        mPreferencesHelper.setFollowUsTwitterLink(followUsTwitterLink);
    }

    @Override
    public String getFollowUsTwitterLink() {
        return mPreferencesHelper.getFollowUsTwitterLink();
    }

    @Override
    public void setImageServerUrl(String imageServerUrl) {
        mPreferencesHelper.setImageServerUrl(imageServerUrl);
    }

    @Override
    public String getImageServerUrl() {
        return mPreferencesHelper.getImageServerUrl();
    }

    @Override
    public void setIsPaypalEnabled(boolean val) {
        mPreferencesHelper.setIsPaypalEnabled(val);
    }

    @Override
    public boolean isPaypalEnabled() {
        return mPreferencesHelper.isPaypalEnabled();
    }

    @Override
    public void setIsMasterpassEnabled(boolean val) {
        mPreferencesHelper.setIsPaypalEnabled(val);
    }

    @Override
    public boolean isMasterpassEnabled() {
        return mPreferencesHelper.isMasterpassEnabled();
    }

    @Override
    public void setIsAmexEnabled(boolean val) {
        mPreferencesHelper.setIsAmexEnabled(val);
    }

    @Override
    public boolean isAmexEnabled() {
        return mPreferencesHelper.isAmexEnabled();
    }

    @Override
    public void setIsKountEnabled(boolean val) {
        mPreferencesHelper.setIsKountEnabled(val);
    }

    @Override
    public boolean isKountEnabled() {
        return mPreferencesHelper.isKountEnabled();
    }

    @Override
    public void setKountMerchantId(String kountMerchantId) {
        mPreferencesHelper.setKountMerchantId(kountMerchantId);
    }

    @Override
    public String getKountMerchantId() {
        return mPreferencesHelper.getKountMerchantId();
    }

    @Override
    public void setSearchMaxPrice(int searchMaxPrice) {
        mPreferencesHelper.setSearchMaxPrice(searchMaxPrice);
    }

    @Override
    public int getSearchMaxPrice() {
        return mPreferencesHelper.getSearchMaxPrice();
    }

    @Override
    public void setAccessAnonymousEnabled(boolean accessAnonymousEnabled) {
        mPreferencesHelper.setAccessAnonymousEnabled(accessAnonymousEnabled);
    }

    @Override
    public boolean getAccessAnonymousEnabled() {
        return mPreferencesHelper.getAccessAnonymousEnabled();
    }

    @Override
    public void setFbSecret(String fbSecret) {
        mPreferencesHelper.setFbSecret(fbSecret);
    }

    @Override
    public String getFbSecret() {
        return mPreferencesHelper.getFbSecret();
    }

    @Override
    public boolean isDebugMode() {
        return mPreferencesHelper.isDebugMode();
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

    @Override
    public void acknowledgeAuth(String loginTicket) {
        mAuthHelper.acknowledgeAuth(loginTicket);
    }

    @Override
    public void revokeAuth() {
        mAuthHelper.revokeAuth();
    }

    @Override
    public void setLoginTicket(String loginTicket) {
        mAuthHelper.setLoginTicket(loginTicket);
    }

    @Override
    public String getLoginTicket() {
        return mAuthHelper.getLoginTicket();
    }

    @Override
    public boolean isAuthorized() {
        return mAuthHelper.isAuthorized();
    }
}
