package au.com.dealsdirect.data;

import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;

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
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.data.network.model.checkout.ApplyVouchers;
import au.com.dealsdirect.data.network.model.checkout.ClearOrder;
import au.com.dealsdirect.data.network.model.checkout.ClearVouchers;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.data.network.model.invite.GetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginFacebook;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.AddVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
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
    public Observable<List<GetBannerResponse>> callGetBanners(GetBannerRequest getBannerRequest) {
        return mApiHelper.callGetBanners(getBannerRequest);
    }

    @Override
    public Observable<GetPublicSalesCategoriesResponse> callGetPublicSalesCategories(GetPublicSalesCategoriesRequest request) {
        return mApiHelper.callGetPublicSalesCategories(request);
    }

    @Override
    public Observable<List<GetCategoryTreeResponse>> callGetGetCategories() {
        return mApiHelper.callGetGetCategories();
    }

    @Override
    public Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String seoIdentifierId) {
        return mApiHelper.callGetSaleItemDetails(seoIdentifierId);

    }

    @Override
    public Observable<GetPublicSaleItemsResponse> callGetPublicSaleItems(
            GetPublicSaleItemsRequest getPublicSaleItemsRequest) {
        return mApiHelper.callGetPublicSaleItems(getPublicSaleItemsRequest);
    }

    @Override
    public Observable<GetSaleItemsResponse> callGetSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest) {
        return mApiHelper.callGetSaleItemsRequest(getSaleItemsRequest);

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
    public Observable<GetContactsResponse> callGetContacts(String languageId) {
        return mApiHelper.callGetContacts(languageId);
    }

    @Override
    public Observable<GetContactHistoryResponse> callGetContactHistory(GetContactHistoryRequest getContactHistoryRequest) {
        return mApiHelper.callGetContactHistory(getContactHistoryRequest);
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
    public Observable<RegisterUserResponse> callRegiser(RegisterUserRequest requestValue) {
        return mApiHelper.callRegiser(requestValue);

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
    public Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return mApiHelper.getLoadUserDetailsApiCall(setUserDetailsRequest);
    }

    @Override
    public Observable<GetPaymentsList.ResponseValue> callGetPaymentsList(GetPaymentsList.RequestValues requestValues) {
        return mApiHelper.callGetPaymentsList(requestValues);
    }

    @Override
    public Observable<GetUserVoucherResponse> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return mApiHelper.callGetUserVouchers(getUserVouchersRequest);
    }

    @Override
    public Observable<GetVouchersResponse> callGetVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return mApiHelper.callGetVouchers(getUserVouchersRequest);
    }

    @Override
    public Observable<ClearVouchersResponse> callGetClearVouchers(ClearVouchersRequest clearVouchersRequest) {
        return mApiHelper.callGetClearVouchers(clearVouchersRequest);
    }

    @Override
    public Observable<ApplyVouchersResponse> callGetApplyVouchers(ApplyVouchersRequest applyVouchersRequest) {
        return mApiHelper.callGetApplyVouchers(applyVouchersRequest);
    }

    @Override
    public Observable<AddVoucherByKeyResponse> callGetAddVoucherByKey(AddVoucherByKeyRequest addVoucherByKeyRequest) {
        return mApiHelper.callGetAddVoucherByKey(addVoucherByKeyRequest);
    }

    @Override
    public Observable<AddAndApplyVoucherByKeyResponse> callGetAddAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest) {
        return mApiHelper.callGetAddAndApplyVoucherByKey(addAndApplyVoucherByKeyRequest);
    }


    @Override
    public Observable<GetOrderPaymentDetails.ResponseValue> callGetOrderPaymentDetails(GetOrderPaymentDetails.RequestValues requestValues) {
        return mApiHelper.callGetOrderPaymentDetails(requestValues);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue requestValue) {
        return mApiHelper.callGetCurrentOrder(requestValue);
    }

    @Override
    public Observable<GetUserPaymentMethods.ResponseValue> callGetUserPaymentMethods(GetUserPaymentMethods.RequestValue requestValue) {
        return mApiHelper.callGetUserPaymentMethods(requestValue);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callAdjustQuantityOrderItem(String url, AdjustOrderItem.RequestValue requestValue) {
        return mApiHelper.callAdjustQuantityOrderItem(url,requestValue);
    }

    @Override
    public Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentMethod(requestValue);
    }

    @Override
    public Observable<GetPaymentToken.ResponseValue> callGetPaymentToken(GetPaymentToken.RequestValue requestValue) {
        return mApiHelper.callGetPaymentToken(requestValue);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callApplyVouchers(ApplyVouchers.RequestValue requestValue) {
        return mApiHelper.callApplyVouchers(requestValue);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callClearVouchers(ClearVouchers.RequestValue requestValue) {
        return mApiHelper.callClearVouchers(requestValue);
    }

    @Override
    public Observable<ClearOrder.ResponseValue> callClearOrder(ClearOrder.RequestValue requestValue) {
        return mApiHelper.callClearOrder(requestValue);
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransaction(CreatePaymentTransaction.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentTransaction(requestValue);
    }

    @Override
    public Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue requestValue) {
        return mApiHelper.callRemoveUserPaymentMethod(requestValue);
    }

    @Override
    public Observable<GetInviteResponse> callGetInvite(GetInviteRequest request) {
        return mApiHelper.callGetInvite(request);
    }

    @Override
    public Observable<SetInviteResponse> callSetInvite(SetInviteRequest request) {
        return mApiHelper.callSetInvite(request);
    }


    @Override
    public Observable<GetPublicSaleDetailsResponse> callGetPublicSaleDetails(GetPublicSaleDetailsRequest request) {
        return mApiHelper.callGetPublicSaleDetails(request);
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
    public void setLanguages(String languagesString) {
        mPreferencesHelper.setLanguages(languagesString);
    }

    @Override
    public String getLanguages() {
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
    public void setPaymentCount(int count) {
        mPreferencesHelper.setPaymentCount(count);
    }

    @Override
    public int getPaymentCount() {
        return mPreferencesHelper.getPaymentCount();
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
