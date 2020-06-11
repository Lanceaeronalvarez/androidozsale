package au.com.dealsdirect.data;

import android.content.Context;
import android.util.Log;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsConsent;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getpublicpaymenttoken.GetPublicPaymentToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.R;
import au.com.dealsdirect.data.auth.AuthHelper;
import au.com.dealsdirect.data.cachedresponses.CachableRequest;
import au.com.dealsdirect.data.cachedresponses.CachableResponse;
import au.com.dealsdirect.data.cachedresponses.CachedResponseHelper;
import au.com.dealsdirect.data.network.ApiHeader;
import au.com.dealsdirect.data.network.ApiHelper;
import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.accountdata.AccountData;
import au.com.dealsdirect.data.network.model.address.AddAddress;
import au.com.dealsdirect.data.network.model.address.ApplyAddressRequest;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.data.network.model.afterpay.AfterPayCreatePaymentRequest;
import au.com.dealsdirect.data.network.model.afterpay.CreateAfterpayOrderRequest;
import au.com.dealsdirect.data.network.model.afterpay.CreateAfterpayOrderResponse;
import au.com.dealsdirect.data.network.model.afterpay.GetAfterpayDataResponse;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetSaleBannerDetailsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.checkout.AdjustOrderItem;
import au.com.dealsdirect.data.network.model.checkout.ApplyVouchers;
import au.com.dealsdirect.data.network.model.checkout.BasketQuantityResponse;
import au.com.dealsdirect.data.network.model.checkout.ClearOrder;
import au.com.dealsdirect.data.network.model.checkout.ClearVouchers;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentIntentStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethodStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionVco;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce.GetPaymentMethodNonceRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrders;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactResponse;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjects;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectsRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactResponse;
import au.com.dealsdirect.data.network.model.deeplinkdata.DeepLinkDataRequest;
import au.com.dealsdirect.data.network.model.deeplinkdata.DeepLinkDataResponse;
import au.com.dealsdirect.data.network.model.events.BannerClickEventRequest;
import au.com.dealsdirect.data.network.model.events.CategoryRequest;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.events.SaleEventRequest;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.data.network.model.events.StartCheckoutRequest;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.fcm.NotificationEvent;
import au.com.dealsdirect.data.network.model.fcm.RegisterDevice;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordRequest;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordResponseBody;
import au.com.dealsdirect.data.network.model.gdpr.consentdata.GetConsentDataResponse;
import au.com.dealsdirect.data.network.model.gdpr.consentdata.SaveConsentDataResponse;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesRequest;
import au.com.dealsdirect.data.network.model.gdpr.savereceivesales.SaveReceiveSalesResponse;
import au.com.dealsdirect.data.network.model.invite.GetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextResponse;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextsResponse;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginFacebook;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPaymentRequest;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPostTransactionRequest;
import au.com.dealsdirect.data.network.model.orders.CreateRefundRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.deliveryservice.GetDeliveryServiceResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataRequest;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataResponse;
import au.com.dealsdirect.data.network.model.ourpaydata.ProcessOurpayInstallmentRequest;
import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmRequest;
import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmResponseBody;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneRequest;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneResponseBody;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetYouMayAlsoLikeResponse;
import au.com.dealsdirect.data.network.model.promoinfo.PromoInfoResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.data.network.model.returns.FileSettingsResponse;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.SetAttachmentResponse;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsResponse;
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
import au.com.dealsdirect.data.network.model.wishlist.GetWishlistIdResponse;
import au.com.dealsdirect.data.pref.PreferencesHelper;
import au.com.dealsdirect.data.wishlist.WishlistChangeListener;
import au.com.dealsdirect.data.wishlist.WishlistHelper;
import au.com.dealsdirect.data.wishlist.WishlistObject;
import au.com.dealsdirect.di.ApplicationContext;
import io.reactivex.Observable;


@Singleton
public class AppDataManager implements DataManager {

    private static final String TAG = "AppDataManager";

    private final Context mContext;
    private final PreferencesHelper mPreferencesHelper;
    private final ApiHelper mApiHelper;
    private final AuthHelper mAuthHelper;
    private final WishlistHelper mWishlistHelper;
    private final CachedResponseHelper mCachedResponseHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          PreferencesHelper preferencesHelper,
                          ApiHelper apiHelper,
                          AuthHelper authHelper,
                          WishlistHelper wishlistHelper,
                          CachedResponseHelper cachedResponseHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
        mAuthHelper = authHelper;
        mWishlistHelper = wishlistHelper;
        mCachedResponseHelper = cachedResponseHelper;
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
    public Observable<GetBannerResponse> callGetBanners(GetBannerRequest getBannerRequest, boolean getOnlyFromNetwork) {
        return mApiHelper.callGetBanners(getBannerRequest, getOnlyFromNetwork);
    }

    @Override
    public Observable<GetSaleBannerDetailsResponse> callGetSaleBannerDetails(String saleId) {
        return mApiHelper.callGetSaleBannerDetails(saleId);
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
    public Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String saleId, String seoIdentifierId) {
        return mApiHelper.callGetSaleItemDetails(saleId, seoIdentifierId);

    }

    @Override
    public Observable<OurpayDataResponse> callGetOurpayData(OurpayDataRequest request) {
        return mApiHelper.callGetOurpayData(request);
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
    public Observable<String> callDynamicDiscount(String skuId) {
        return mApiHelper.callDynamicDiscount(skuId);
    }

    @Override
    public Observable<AddToCartResponse.Response> callAddItemToCart(AddToCartRequest requestValues) {
        return mApiHelper.callAddItemToCart(requestValues);
    }

    @Override
    public Observable<PromoInfoResponse> callPromoInfo(String skuId) {
        return mApiHelper.callPromoInfo(skuId);
    }

    @Override
    public Observable<GetServerSettings.ResponseValue> callGetServerSettings(String countryId) {
        return mApiHelper.callGetServerSettings(countryId);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(String countryId) {
        return mApiHelper.callGetPublicAppSettings(countryId);
    }

    @Override
    public Observable<GetPublicPaymentToken.ResponseValue> callGetPublicPaymentToken(String countryId, String languageId) {

        return mApiHelper.callGetPublicPaymentToken(countryId, languageId);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetAppSettings(String countryId) {
        return mApiHelper.callGetAppSettings(countryId);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(String countryId) {
        return mApiHelper.callGetAppSettingsSection(countryId);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetPublicAppSettingsSections(String countryId) {
        return mApiHelper.callGetPublicAppSettingsSections(countryId);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetPublicAppSettingsSections(String countryId, String sectionName) {
        return mApiHelper.callGetPublicAppSettingsSections(countryId, sectionName);
    }

    @Override
    public Observable<GetAppSettingsConsent.ResponseValue> callGetAppSettingsConsent(String countryId) {
        return mApiHelper.callGetAppSettingsConsent(countryId);
    }

    @Override
    public Observable<GetConsentDataResponse> callGetConsentData(String countryId) {
        return mApiHelper.callGetConsentData(countryId);
    }

    @Override
    public Observable<SaveConsentDataResponse> callSaveConsentData(String countryId) {
        return mApiHelper.callSaveConsentData(countryId);
    }

    @Override
    public Observable<SaveReceiveSalesResponse> callSaveReceiveSales(SaveReceiveSalesRequest saveReceiveSalesRequest) {
        return mApiHelper.callSaveReceiveSales(saveReceiveSalesRequest);
    }

    @Override
    public Observable<GetAppSettingsConsent.ResponseValue> callGetPublicAppSettingsConsent(String countryId) {
        return mApiHelper.callGetPublicAppSettingsConsent(countryId);
    }

    @Override
    public Observable<GetContactsResponse> callGetContacts(String languageId) {
        return mApiHelper.callGetContacts(languageId);
    }

    @Override
    public Observable<GetContactHistoryResponse.ResponseValue> callGetContactHistory(GetContactHistoryRequest getContactHistoryRequest) {
        return mApiHelper.callGetContactHistory(getContactHistoryRequest);
    }

    @Override
    public Observable<AccountData> callGetAccountData() {
        return mApiHelper.callGetAccountData();
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue) {
        return mApiHelper.callLoginViaEmail(requestValue);
    }

    @Override
    public Observable<JSONObject> callLoginViaFacebook(LoginFacebook.RequestValue requestValue) {
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
    public Observable<LoginVisa.ResponseValue> callLoginVisaCheckout(LoginVisa.RequestValue requestValue) {
        return mApiHelper.callLoginVisaCheckout(requestValue);
    }

    @Override
    public Observable<RegisterUserResponse> callRegister(RegisterUserRequest requestValue) {
        return mApiHelper.callRegister(requestValue);

    }

    @Override
    public Observable<ForgotPasswordResponseBody> callForgotPassword(ForgotPasswordRequest requestValue) {
        return mApiHelper.callForgotPassword(requestValue);

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
    public Observable<SetUserDetailsResponse> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
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
        return mApiHelper.callAdjustQuantityOrderItem(url, requestValue);
    }

    @Override
    public Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentMethod(requestValue);
    }

    @Override
    public Observable<CreatePaymentMethodStripe.ResponseValue> callCreatePaymentMethodStripe(CreatePaymentMethodStripe.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentMethodStripe(requestValue);
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
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionVco(CreatePaymentTransactionVco.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentTransactionVco(requestValue);
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionStripe(CreatePaymentTransactionStripe.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentTransactionStripe(requestValue);
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentIntentStripe(CreatePaymentIntentStripe.RequestValue requestValue) {
        return mApiHelper.callCreatePaymentIntentStripe(requestValue);
    }

    @Override
    public Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue requestValue) {
        return mApiHelper.callRemoveUserPaymentMethod(requestValue);
    }

    @Override
    public Observable<GetDeliveryServicePackageDetails.ResponseValue> callGetDeliveryServicePackageDetails(GetDeliveryServicePackageDetails.RequestValue requestValue) {
        return mApiHelper.callGetDeliveryServicePackageDetails(requestValue);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callSetDeliveryOption(SetDeliveryOption setDeliveryOption) {
        return mApiHelper.callSetDeliveryOption(setDeliveryOption);
    }

    @Override
    public Observable<BasketQuantityResponse> callGetBasketItemsQuantity() {
        return mApiHelper.callGetBasketItemsQuantity();
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
    public Observable<ContactOrders> callGetContactOrders() {
        return mApiHelper.callGetContactOrders();
    }

    @Override
    public Observable<ContactSubjects> callGetContactSubjects(ContactSubjectsRequest contactSubjectsRequest) {
        return mApiHelper.callGetContactSubjects(contactSubjectsRequest);

    }

    @Override
    public Observable<CreateContactResponse> callCreateContact(CreateContactRequest createContactRequest) {
        return mApiHelper.callCreateContact(createContactRequest);

    }

    @Override
    public Observable<ReplyContactResponse> callReplyContact(ReplyContactRequest createContactRequest) {
        return mApiHelper.callReplyContact(createContactRequest);

    }


    @Override
    public Observable<CurrentReturnResponseBody> callGetCurrentReturns() {
        return mApiHelper.callGetCurrentReturns();
    }

    @Override
    public Observable<GetReturnOrders> callGetReturnOrders() {
        return mApiHelper.callGetReturnOrders();
    }

    @Override
    public Observable<GetReturnDetailsResponse> callGetReturnDetails(GetReturnDetailRequest getReturnDetailRequest) {
        return mApiHelper.callGetReturnDetails(getReturnDetailRequest);

    }

    @Override
    public Observable<NewReturnOrderDetailResponseBody> callGetNewReturnOrderDetail(NewReturnOrderDetailRequest newReturnOrderDetailRequest) {
        return mApiHelper.callGetNewReturnOrderDetail(newReturnOrderDetailRequest);

    }

    @Override
    public Observable<CreateReturnRequestResponseBody> callCreateReturnRequest(CreateReturnRequest createReturnRequest) {
        return mApiHelper.callCreateReturnRequest(createReturnRequest);

    }

    @Override
    public Observable<VerificationNormalizePhoneResponseBody> callNormalizePhone(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest) {
        return mApiHelper.callNormalizePhone(verificationNormalizePhoneRequest);

    }

    @Override
    public Observable<VerificationNormalizePhoneResponseBody> callVerificationCodeSend(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest) {
        return mApiHelper.callVerificationCodeSend(verificationNormalizePhoneRequest);
    }

    @Override
    public Observable<VerificationCodeConfirmResponseBody> callVerificationCodeConfirm(VerificationCodeConfirmRequest verificationCodeConfirmRequest) {
        return mApiHelper.callVerificationCodeConfirm(verificationCodeConfirmRequest);
    }

    @Override
    public Observable<JSONObject> callMasterpassPayment(MasterPassPaymentRequest request) {
        return mApiHelper.callMasterpassPayment(request);
    }

    @Override
    public Observable<JSONObject> callMasterpassPostTransaction(MasterPassPostTransactionRequest request) {
        return mApiHelper.callMasterpassPostTransaction(request);
    }

    @Override
    public Observable<String> callSearchEvent(SearchEventRequest request) {
        return mApiHelper.callSearchEvent(request);
    }

    @Override
    public Observable<String> callProductViewEvent(ProductViewRequest request) {
        return mApiHelper.callProductViewEvent(request);
    }

    @Override
    public Observable<String> callCategoryEvent(CategoryRequest request) {
        return mApiHelper.callCategoryEvent(request);
    }

    @Override
    public Observable<String> callSaleEvent(SaleEventRequest request) {
        return mApiHelper.callSaleEvent(request);
    }

    @Override
    public Observable<String> callEventUser() {
        return mApiHelper.callEventUser();
    }

    @Override
    public Observable<String> callBannerClickEvent(BannerClickEventRequest request) {
        return mApiHelper.callBannerClickEvent(request);
    }

    @Override
    public Observable<String> callWishlistEvent(WishlistEventRequest request) {
        return mApiHelper.callWishlistEvent(request);
    }

    @Override
    public Observable<JSONObject> callGetPaymentMethodNonce(GetPaymentMethodNonceRequest request) {
        return mApiHelper.callGetPaymentMethodNonce(request);
    }

    @Override
    public Observable<String> callStartCheckoutEvent(StartCheckoutRequest request) {
        return mApiHelper.callStartCheckoutEvent(request);
    }

    @Override
    public Observable<GetPaymentPlansResponse> callGetPaymentPlans(String countryId, String langaugeId) {
        return mApiHelper.callGetPaymentPlans(countryId, langaugeId);
    }

    @Override
    public Observable<GetScheduledPlansResponse> callGetScheduledPlans(String countryId, String langaugeId) {
        return mApiHelper.callGetScheduledPlans(countryId, langaugeId);
    }

    @Override
    public Observable<GetPastPaymentsResponse> callGetPastPayments(String countryId, String langaugeId) {
        return mApiHelper.callGetPastPayments(countryId, langaugeId);
    }

    @Override
    public Observable<GetDeliveryServiceResponse> callGetDeliveryService() {
        return mApiHelper.callGetDeliveryService();
    }

    @Override
    public Observable<GetScheduledPlansResponse> processOurpayInstallment(ProcessOurpayInstallmentRequest request) {
        return mApiHelper.processOurpayInstallment(request);
    }

    @Override
    public Observable<CreateAfterpayOrderResponse> createAfterpayOrder(CreateAfterpayOrderRequest request) {
        return mApiHelper.createAfterpayOrder(request);
    }

    @Override
    public Observable<JSONObject> callAfterPayCreatePayment(AfterPayCreatePaymentRequest request) {
        return mApiHelper.callAfterPayCreatePayment(request);
    }

    @Override
    public Observable<GetAfterpayDataResponse> callGetAfterpayData(String price) {
        return mApiHelper.callGetAfterpayData(price);
    }

    @Override
    public Observable<DeepLinkDataResponse> callGetDeepLinkData(DeepLinkDataRequest request) {
        return mApiHelper.callGetDeepLinkData(request);
    }

    @Override
    public Observable<String> callChangeDeliveryAddress(ChangeDeliveryAddressRequest request) {
        return mApiHelper.callChangeDeliveryAddress(request);
    }

    @Override
    public Observable<String> callCreateRefund(CreateRefundRequest request) {
        return mApiHelper.callCreateRefund(request);
    }

    @Override
    public Observable<OrderReceivedResponse> callOrderReceived(OrderReceivedRequest receivedRequest) {
        return mApiHelper.callOrderReceived(receivedRequest);
    }

    @Override
    public Observable<SetAttachmentResponse> setAttachment(SetAttachmentRequest setAttachmentRequest) {
        return mApiHelper.setAttachment(setAttachmentRequest);
    }

    @Override
    public Observable<FileSettingsResponse> callGetFileSettings() {
        return mApiHelper.callGetFileSettings();
    }

    @Override
    public Observable<List<GetWishlistIdResponse>> callGetWishlistIdsOnly() {
        return mApiHelper.callGetWishlistIdsOnly();
    }

    @Override
    public Observable<List<GetSaleItemsResponse.Products>> callGetWishlist() {
        return mApiHelper.callGetWishlist();
    }

    @Override
    public Observable callAddToWishlist(String productId, String seoIdentifier) {
        return mApiHelper.callAddToWishlist(productId, seoIdentifier);
    }

    @Override
    public Observable callRemoveFromWishlist(String productId) {
        return mApiHelper.callRemoveFromWishlist(productId);
    }

    @Override
    public Observable<List<RecommendedItemsResponse>> callRecommendedItems() {
        return mApiHelper.callRecommendedItems();
    }

    @Override
    public Observable<List<GetYouMayAlsoLikeResponse>> callYouMayAlsoLike(String skuId) {
        return mApiHelper.callYouMayAlsoLike(skuId);
    }

    @Override
    public Observable<GetTemplateTextResponse> callGetTemplateText(GetTemplateTextRequest templateTextRequest) {
        return mApiHelper.callGetTemplateText(templateTextRequest);
    }

    @Override
    public Observable<GetTemplateTextsResponse> callGetTemplateTexts(GetTemplateTextsRequest templateTextRequest) {
        return mApiHelper.callGetTemplateTexts(templateTextRequest);
    }

    @Override
    public Observable<RegisterDevice.ResponseValue> callRegisterDevice(RegisterDevice.RequestValue requestValue) {
        return mApiHelper.callRegisterDevice(requestValue);
    }

    @Override
    public Observable<NotificationEvent.ResponseValue> callNotificationEvent(NotificationEvent.RequestValue requestValue) {
        return mApiHelper.callNotificationEvent(requestValue);
    }

    @Override
    public Observable<JSONObject> callRegisterSubscriber(HashMap<String, Object> param) {
        return mApiHelper.callRegisterSubscriber(param);
    }


    @Override
    public Observable<List<SortingResponse>> callSortingFacets() {
        return mApiHelper.callSortingFacets();
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
    public void setCountryIso(String countryIso) {
        mPreferencesHelper.setCountryIso(countryIso);
    }

    @Override
    public String getCountryIso() {
        return mPreferencesHelper.getCountryIso();
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
    public String getLegacyCountryId() {
        return mPreferencesHelper.getLegacyCountryId();
    }

    @Override
    public String getLegacyLanguageId() {
        return mPreferencesHelper.getLegacyLanguageId();
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
        mPreferencesHelper.setIsMasterpassEnabled(val);
    }

    @Override
    public boolean isMasterpassEnabled() {
        return mPreferencesHelper.isMasterpassEnabled();
    }

    @Override
    public void setIsOurpayEnabled(boolean val) {
        mPreferencesHelper.setIsOurpayEnabled(val);
    }

    @Override
    public boolean isOurpayEnabled() {
        return mPreferencesHelper.isOurpayEnabled();
    }

    @Override
    public void setIsAfterpayEnabled(boolean val) {
        mPreferencesHelper.setIsAfterpayEnabled(val);
    }

    @Override
    public boolean isAfterpayEnabled() {
        return mPreferencesHelper.isAfterpayEnabled();
    }

    @Override
    public void setAfterpayScriptUri(String uri) {
        mPreferencesHelper.setAfterpayScriptUri(uri);
    }

    @Override
    public String getAfterpayScriptUri() {
        return mPreferencesHelper.getAfterpayScriptUri();
    }

    @Override
    public void setAfterpayLightboxImgUrl(String url) {
        mPreferencesHelper.setAfterpayLightboxImgUrl(url);
    }

    @Override
    public String getAfterpayLightboxImageUrl() {
        return mPreferencesHelper.getAfterpayLightboxImageUrl();
    }

    @Override
    public void setAfterpayTermsLink(String link) {
        mPreferencesHelper.setAfterpayTermsLink(link);
    }

    @Override
    public String getAfterpayTermsLink() {
        return mPreferencesHelper.getAfterpayTermsLink();
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
    public void setGCMRegistrationId(String registrationId) {
        mPreferencesHelper.setGCMRegistrationId(registrationId);
    }

    @Override
    public String getGCMRegistrationId() {
        return mPreferencesHelper.getGCMRegistrationId();
    }

    @Override
    public void setGCMAppVersion(int appVersion) {
        mPreferencesHelper.setGCMAppVersion(appVersion);
    }

    @Override
    public int getGCMAppVersion() {
        return mPreferencesHelper.getGCMAppVersion();
    }

    @Override
    public void setIsMyPayEnabled(boolean isMyPayEnabled) {
        mPreferencesHelper.setIsMyPayEnabled(isMyPayEnabled);
    }

    @Override
    public boolean getIsMyPayEnabled() {
        return mPreferencesHelper.getIsMyPayEnabled();
    }

    @Override
    public void setIsVisaCheckoutEnabled(boolean isVisaCheckoutEnabled) {
        mPreferencesHelper.setIsVisaCheckoutEnabled(isVisaCheckoutEnabled);
    }

    @Override
    public boolean getIsVisaCheckoutEnabled() {
        return mPreferencesHelper.getIsVisaCheckoutEnabled();
    }

    @Override
    public void setVisaCheckoutApiKey(String visaCheckoutApiKey) {
        mPreferencesHelper.setVisaCheckoutApiKey(visaCheckoutApiKey);
    }

    @Override
    public String getVisaCheckoutApiKey() {
        return mPreferencesHelper.getVisaCheckoutApiKey();
    }

    @Override
    public void setVisaCheckoutApiUrl(String visaCheckoutApiUrl) {
        mPreferencesHelper.setVisaCheckoutApiUrl(visaCheckoutApiUrl);
    }

    @Override
    public String getVisaCheckoutApiUrl() {
        return mPreferencesHelper.getVisaCheckoutApiUrl();
    }

    @Override
    public void setVisaCheckoutProviderType(int visaCheckoutProviderType) {
        mPreferencesHelper.setVisaCheckoutProviderType(visaCheckoutProviderType);
    }

    @Override
    public int getVisaCheckoutProviderType() {
        return mPreferencesHelper.getVisaCheckoutProviderType();
    }

    @Override
    public void setPersonalisationTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        mPreferencesHelper.setPersonalisationTemplateTexts(value);
    }

    @Override
    public String getPersonalisationTemplateTexts() {
        return mPreferencesHelper.getPersonalisationTemplateTexts();
    }

    @Override
    public void setConsentTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        mPreferencesHelper.setConsentTemplateTexts(value);
    }

    @Override
    public String getConsentTemplateTexts(String key) {
        return mPreferencesHelper.getConsentTemplateTexts(key);
    }

    @Override
    public void setAppSettingsConsent(GetAppSettingsConsent.ResponseValue value) {
        mPreferencesHelper.setAppSettingsConsent(value);
    }

    @Override
    public String getAppSettingsConsentText(String key) {
        return mPreferencesHelper.getAppSettingsConsentText(key);
    }

    @Override
    public int getAppSettingsConsentMode() {
        return mPreferencesHelper.getAppSettingsConsentMode();
    }

    @Override
    public boolean getAppSettingsConsentIsChecked(String key) {
        return mPreferencesHelper.getAppSettingsConsentIsChecked(key);
    }

    @Override
    public void setMyPayTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        Log.d("Checkout", "set my pay template texts");
        mPreferencesHelper.setMyPayTemplateTexts(value);

    }

    @Override
    public void setDeliveryOptionsTemplateTexts(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        mPreferencesHelper.setDeliveryOptionsTemplateTexts(value);
    }

    @Override
    public String getMyPayTemplateTexts(String detailKey) {
        return mPreferencesHelper.getMyPayTemplateTexts(detailKey);
    }

    @Override
    public void setIsInitialLaunch(boolean isInitialLaunch) {
        mPreferencesHelper.setIsInitialLaunch(isInitialLaunch);
    }

    @Override
    public boolean getIsInitialLaunch() {
        return mPreferencesHelper.getIsInitialLaunch();
    }

    @Override
    public void setIsMultiLanguage(boolean isMultiLanguage) {
        mPreferencesHelper.setIsMultiLanguage(isMultiLanguage);
    }

    @Override
    public boolean getIsMultiLanguage() {
        return mPreferencesHelper.getIsMultiLanguage();
    }

    @Override
    public void setIsMultiCountry(boolean isMultiCountry) {
        mPreferencesHelper.setIsMultiCountry(isMultiCountry);
    }

    @Override
    public boolean getIsMultiCountry() {
        return mPreferencesHelper.getIsMultiCountry();
    }

    @Override
    public void setPublicPaymentToken(String publicPaymentToken) {
        mPreferencesHelper.setPublicPaymentToken(publicPaymentToken);
    }

    @Override
    public String getPublicPaymentToken() {
        return mPreferencesHelper.getPublicPaymentToken();
    }

    @Override
    public void setPublicPaymentType(String publicPaymentType) {
        mPreferencesHelper.setPublicPaymentType(publicPaymentType);
    }

    @Override
    public String getPublicPaymentType() {
        return mPreferencesHelper.getPublicPaymentType();
    }

    @Override
    public void setIsNotificationsEnabled(boolean isNotificationsEnabled) {
        mPreferencesHelper.setIsNotificationsEnabled(isNotificationsEnabled);
    }

    @Override
    public boolean getIsNotificationsEnabled() {
        return mPreferencesHelper.getIsNotificationsEnabled();
    }

    @Override
    public void setIsPaypalCreditEnabled(boolean isPaypalCreditEnabled) {
        mPreferencesHelper.setIsPaypalCreditEnabled(isPaypalCreditEnabled);
    }

    @Override
    public boolean isPaypalCreditEnabled() {
        return mPreferencesHelper.isPaypalCreditEnabled();
    }

    @Override
    public void setIsSortingEnabled(boolean isSortingEnabled) {
        mPreferencesHelper.setIsSortingEnabled(isSortingEnabled);
    }

    @Override
    public boolean getIsSortingEnabled() {
        return mPreferencesHelper.getIsSortingEnabled();
    }

    @Override
    public void setLastRedirection(String lastRedirection) {
        mPreferencesHelper.setLastRedirection(lastRedirection);
    }

    @Override
    public String getLastRedirection() {
        return mPreferencesHelper.getLastRedirection();
    }

    @Override
    public void setIsNewUser(boolean isNewUser) {
        mPreferencesHelper.setIsNewUser(isNewUser);
    }

    @Override
    public boolean getIsNewUser() {
        return mPreferencesHelper.getIsNewUser();
    }

    @Override
    public void setHasActiveCheckoutSession(boolean hasActiveCheckoutSession) {
        mPreferencesHelper.setHasActiveCheckoutSession(hasActiveCheckoutSession);
    }

    @Override
    public boolean hasActiveCheckoutSession() {
        return mPreferencesHelper.hasActiveCheckoutSession();
    }

    @Override
    public void setCartHashCode(int hashCode) {
        mPreferencesHelper.setCartHashCode(hashCode);
    }

    @Override
    public int getCartHashCode() {
        return mPreferencesHelper.getCartHashCode();
    }

    @Override
    public void resetAddToCartJourneyFlags() {
        mPreferencesHelper.resetAddToCartJourneyFlags();
    }

    @Override
    public void setHasViewedSale(boolean hasViewedSale) {
        mPreferencesHelper.setHasViewedSale(hasViewedSale);
    }

    @Override
    public boolean hasViewedSale() {
        return mPreferencesHelper.hasViewedSale();
    }

    @Override
    public void setHasViewedProductCategory(boolean hasViewedProductCategory) {
        mPreferencesHelper.setHasViewedProductCategory(hasViewedProductCategory);
    }

    @Override
    public boolean hasViewedProductCategory() {
        return mPreferencesHelper.hasViewedProductCategory();
    }

    @Override
    public void setHasViewedProduct(boolean hasViewedProduct) {
        mPreferencesHelper.setHasViewedProduct(hasViewedProduct);
    }

    @Override
    public boolean hasViewedProduct() {
        return mPreferencesHelper.hasViewedProduct();
    }

    @Override
    public void setHasAddedToCart(boolean hasAddedToCart) {
        mPreferencesHelper.setHasAddedToCart(hasAddedToCart);
    }

    @Override
    public boolean hasAddedToCart() {
        return mPreferencesHelper.hasAddedToCart();
    }

    @Override
    public void setHasViewedCart(boolean hasViewedCart) {
        mPreferencesHelper.setHasViewedCart(hasViewedCart);
    }

    @Override
    public boolean hasViewedCart() {
        return mPreferencesHelper.hasViewedCart();
    }

    @Override
    public void setUserHasRateApp(boolean userHasRateApp) {
        mPreferencesHelper.setUserHasRateApp(userHasRateApp);
    }

    @Override
    public boolean userHasRateApp() {
        return mPreferencesHelper.userHasRateApp();
    }

    @Override
    public void setShouldShowStrictConsent(boolean shouldShowStrictConsent) {
        mPreferencesHelper.setShouldShowStrictConsent(shouldShowStrictConsent);
    }

    @Override
    public boolean shouldShowStrictConsent() {
        return mPreferencesHelper.shouldShowStrictConsent();
    }

    @Override
    public void setIsOurpayDashboardEnabled(boolean enabled) {
        mPreferencesHelper.setIsOurpayDashboardEnabled(enabled);
    }

    @Override
    public boolean getIsOurpayDashboardEnabled() {
        return mPreferencesHelper.getIsOurpayDashboardEnabled();
    }

    @Override
    public void setReCaptchaSiteKey(String key) {
        mPreferencesHelper.setReCaptchaSiteKey(key);
    }

    @Override
    public String getReCaptchaSiteKey() {
        return mPreferencesHelper.getReCaptchaSiteKey();
    }

    @Override
    public void setIsGoogleAdsEnabled(boolean isGoogleAdsEnabled) {
        mPreferencesHelper.setIsGoogleAdsEnabled(isGoogleAdsEnabled);
    }

    @Override
    public boolean isGoogleAdsEnabled() {
        return mPreferencesHelper.isGoogleAdsEnabled();
    }

    @Override
    public void setLastColumnSelected(int columnCount) {
        mPreferencesHelper.setLastColumnSelected(columnCount);
    }

    @Override
    public int getLastColumnSelected() {
        return mPreferencesHelper.getLastColumnSelected();
    }

    public void setLastTimeStamp(String timeStamp) {
        mPreferencesHelper.setLastTimeStamp(timeStamp);
    }

    @Override
    public String getLastTimeStamp() {
        return mPreferencesHelper.getLastTimeStamp();
    }

    @Override
    public void setVoucherStatusTemplateText(GetTemplateTextsResponse.GetTemplateTextsValue value) {
        mPreferencesHelper.setVoucherStatusTemplateText(value);
    }

    @Override
    public String getVoucherStatusTemplateText(GetUserVoucherResponse.Status status) {
        return mPreferencesHelper.getVoucherStatusTemplateText(status);
    }

    @Override
    public int getFileSizeLimit() {
        return mPreferencesHelper.getFileSizeLimit();
    }

    @Override
    public void setFileSizeLimit(int fileSizeLimit) {
        mPreferencesHelper.setFileSizeLimit(fileSizeLimit);
    }

    @Override
    public boolean hasWishlistBeenAccessed() {
        return mPreferencesHelper.hasWishlistBeenAccessed();
    }

    @Override
    public void setHasWishlistBeenAccessed(boolean isAccessed) {
        mPreferencesHelper.setHasWishlistBeenAccessed(isAccessed);
    }

    @Override
    public String getStripePublicKey() {
        return mPreferencesHelper.getStripePublicKey();
    }

    @Override
    public void setStripePublicKey(String stripePublicKey) {
        mPreferencesHelper.setStripePublicKey(stripePublicKey);
    }

    @Override
    public boolean isStripeEnabled() {
        return mPreferencesHelper.isStripeEnabled();
    }

    @Override
    public void setStripeEnabled(boolean stripeEnabled) {
        mPreferencesHelper.setStripeEnabled(stripeEnabled);
    }

    @Override
    public String getStripePaymentMethodId() {
        return mPreferencesHelper.getStripePaymentMethodId();
    }

    @Override
    public void setStripePaymentMethodId(String paymentMethodId) {
        mPreferencesHelper.setStripePaymentMethodId(paymentMethodId);
    }

    @Override
    public void setEventUserId(String userId) {
        mPreferencesHelper.setEventUserId(userId);
    }

    @Override
    public String getEventUserId() {
        return mPreferencesHelper.getEventUserId();
    }

    @Override
    public HashSet<String> getCookies() {
        return mPreferencesHelper.getCookies();
    }

    @Override
    public boolean isTablet() {
        return mContext.getResources().getBoolean(R.bool.is_tablet);
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

    @Override
    public void setWishlist(List<WishlistObject> wishlist) {
        mWishlistHelper.setWishlist(wishlist);
    }

    @Override
    public List<WishlistObject> getWishlist() {
        return mWishlistHelper.getWishlist();
    }

    @Override
    public void addToWishlist(WishlistObject object, WishlistChangeDelayedCallback delayedCallback) {
        mWishlistHelper.addToWishlist(object, delayedCallback);
    }

    @Override
    public void removeFromWishlist(String productId, WishlistChangeDelayedCallback delayedCallback) {
        mWishlistHelper.removeFromWishlist(productId, delayedCallback);
    }

    @Override
    public boolean isProductInWishlist(String productId) {
        return mWishlistHelper.isProductInWishlist(productId);
    }

    @Override
    public void setWishlistChangeListener(WishlistChangeListener listener) {
        mWishlistHelper.setWishlistChangeListener(listener);
    }

    @Override
    public void setCheckoutHasWishlistItem(boolean hasWishlistItem) {
        mWishlistHelper.setCheckoutHasWishlistItem(hasWishlistItem);
    }

    @Override
    public boolean doesCheckoutHaveWishlistItem() {
        return mWishlistHelper.doesCheckoutHaveWishlistItem();
    }

    @Override
    public void updateWishlistCount() {
        mWishlistHelper.updateWishlistCount();
    }

    @Override
    public <T extends CachableRequest, V extends CachableResponse> void setCachedResponse(T request, V response) {
        mCachedResponseHelper.setCachedResponse(request, response);
    }

    @Override
    public <T extends CachableRequest, V extends CachableResponse> V getCachedResponse(T request, Class<V> responseClass) {
        return mCachedResponseHelper.getCachedResponse(request, responseClass);
    }

    @Override
    public void pruneCachedResponses() {
        mCachedResponseHelper.pruneCachedResponses();
    }

    @Override
    public <T extends CachableRequest> void pruneCachedResponse(T request) {
        mCachedResponseHelper.pruneCachedResponse(request);
    }

    @Override
    public void storeCache() {
        mCachedResponseHelper.storeCache();
    }

    @Override
    public void fetchCache() {
        mCachedResponseHelper.fetchCache();
    }
}
