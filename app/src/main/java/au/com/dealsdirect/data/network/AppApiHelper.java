package au.com.dealsdirect.data.network;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsConsent;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getpublicpaymenttoken.GetPublicPaymentToken;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.events.RecentlyViewedEventRequest;
import au.com.dealsdirect.data.network.model.events.RecommendationEventRequest;
import au.com.dealsdirect.data.network.model.events.SaleEventRequest;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.data.network.model.events.StartCheckoutRequest;
import au.com.dealsdirect.data.network.model.events.WishlistEventRequest;
import au.com.dealsdirect.data.network.model.events.YouMayAlsoLikeEventRequest;
import au.com.dealsdirect.data.network.model.fcm.NotificationEvent;
import au.com.dealsdirect.data.network.model.fcm.RegisterDevice;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordRequest;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordResponseBody;
import au.com.dealsdirect.data.network.model.gdpr.consentdata.ConsentDataRequest;
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
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
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
import au.com.dealsdirect.data.wishlist.CallAddToWishlistRequest;
import au.com.dealsdirect.utils.AppConstants;
import au.com.dealsdirect.utils.JsonUtils;
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
        return Rx2AndroidNetworking.post(ApiEndPoint.getAppSettings())
                .addHeaders(mApiHeader.get())
                .addBodyParameter(request)
                .build()
                .getObjectObservable(SampleResponse.class);
    }

    @Override
    public Observable<GetBannerResponse> callGetBanners(
            GetBannerRequest getBannerRequest, boolean getOnlyFromNetwork) {
        if (getOnlyFromNetwork) {
            return Rx2AndroidNetworking.get(ApiEndPoint.getSales())
                    .addHeaders(mApiHeader.get())
                    .addQueryParameter(getBannerRequest)
                    .getResponseOnlyFromNetwork()
                    .build()
                    .getObjectObservable(GetBannerResponse.class);
        } else {
            return Rx2AndroidNetworking.get(ApiEndPoint.getSales())
                    .addHeaders(mApiHeader.get())
                    .addQueryParameter(getBannerRequest)
                    .build()
                    .getObjectObservable(GetBannerResponse.class);
        }
    }

    @Override
    public Observable<GetSaleBannerDetailsResponse> callGetSaleBannerDetails(String saleId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getSaleBannerDetails())
                .addHeaders(mApiHeader.get())
                .addPathParameter("sale_id", saleId)
                .build()
                .getObjectObservable(GetSaleBannerDetailsResponse.class);
    }

    @Override
    public Observable<GetPublicSalesCategoriesResponse> callGetPublicSalesCategories(GetPublicSalesCategoriesRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getShopCategories())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSalesCategoriesResponse.class);
    }

    @Override
    public Observable<List<GetCategoryTreeResponse>> callGetGetCategories() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getCategoryTree())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(GetCategoryTreeResponse.class);
    }

    @Override
    public Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String seoIdentifierId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getProductDetails())
                .addHeaders(mApiHeader.get())
                .addPathParameter("seo_identifier", seoIdentifierId)
                .build()
                .getObjectObservable(GetSaleItemDetailsResponse.class);
    }

    @Override
    public Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String saleId, String seoIdentifierId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getProductDetails())
                .addHeaders(mApiHeader.get())
                .addPathParameter("seo_identifier", seoIdentifierId)
                .addPathParameter("sale_id", saleId)
                .build()
                .getObjectObservable(GetSaleItemDetailsResponse.class);
    }

    @Override
    public Observable<OurpayDataResponse> callGetOurpayData(OurpayDataRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getOurpayData())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(OurpayDataResponse.class);
    }

    @Override
    public Observable<GetPublicSaleDetailsResponse> callGetPublicSaleDetails(GetPublicSaleDetailsRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getSalesItemSaleDetails())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSaleDetailsResponse.class);
    }


    @Override
    public Observable<GetPublicSaleItemsResponse> callGetPublicSaleItems(
            GetPublicSaleItemsRequest getPublicSaleItemsRequest) {

        return Rx2AndroidNetworking.get(ApiEndPoint.getPublicSaleItems())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(getPublicSaleItemsRequest)
                .build()
                .getObjectObservable(GetPublicSaleItemsResponse.class);
    }

    @Override
    public Observable<GetSaleItemsResponse> callGetSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getProducts())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(getSaleItemsRequest)
                .build()
                .getObjectObservable(GetSaleItemsResponse.class);
    }

    @Override
    public Observable<String> callDynamicDiscount(String skuId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getProductDetailDynamicDiscount())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(AppConstants.PARAM_SKUID, skuId)
                .build()
                .getObjectObservable(String.class);
    }

    @Override
    public Observable<AddToCartResponse.Response> callAddItemToCart(AddToCartRequest requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getAddToCart())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(AddToCartResponse.Response.class);
    }

    @Override
    public Observable<PromoInfoResponse> callPromoInfo(String skuId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getPromoInfo())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(AppConstants.PARAM_SKUID, skuId)
                .build()
                .getObjectObservable(PromoInfoResponse.class);
    }

    @Override
    public Observable<GetServerSettings.ResponseValue> callGetServerSettings(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getServerSettings())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetServerSettings.RequestValue(countryId))
                .build()
                .getObjectObservable(GetServerSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getPublicAppSettings())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettings.RequestValue(countryId))
                .build()
                .getObjectObservable(GetAppSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetPublicPaymentToken.ResponseValue> callGetPublicPaymentToken(String countryId, String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPublicPaymentToken())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetPublicPaymentToken.RequestValue(countryId, languageId)))
                .build()
                .getObjectObservable(GetPublicPaymentToken.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetAppSettings(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getAppSettings())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettings.RequestValue(countryId))
                .build()
                .getObjectObservable(GetAppSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getAppSettingsSection())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettingsSection.RequestValue(countryId))
                .build()
                .getObjectObservable(GetAppSettingsSection.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetPublicAppSettingsSections(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getPublicAppSettingsSection())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettingsSection.RequestValue(countryId))
                .build()
                .getObjectObservable(GetAppSettingsSection.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetPublicAppSettingsSections(String countryId, String sectionName) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getPublicAppSettingsSection())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettingsSection.RequestValue(countryId, sectionName))
                .build()
                .getObjectObservable(GetAppSettingsSection.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettingsConsent.ResponseValue> callGetAppSettingsConsent(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getAppSettingsSection())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettingsConsent.RequestValue(countryId))
                .build()
                .getObjectObservable(GetAppSettingsConsent.ResponseValue.class);
    }

    @Override
    public Observable<GetConsentDataResponse> callGetConsentData(String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getConsentData())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new ConsentDataRequest(countryId)))
                .build()
                .getObjectObservable(GetConsentDataResponse.class);
    }

    @Override
    public Observable<SaveConsentDataResponse> callSaveConsentData(String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.saveConsentData())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new ConsentDataRequest(countryId)))
                .build()
                .getObjectObservable(SaveConsentDataResponse.class);
    }

    @Override
    public Observable<SaveReceiveSalesResponse> callSaveReceiveSales(SaveReceiveSalesRequest saveReceiveSalesRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.saveReceiveSales())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(saveReceiveSalesRequest))
                .build()
                .getObjectObservable(SaveReceiveSalesResponse.class);
    }

    @Override
    public Observable<GetAppSettingsConsent.ResponseValue> callGetPublicAppSettingsConsent(String countryId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getPublicAppSettingsSection())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(new GetAppSettingsConsent.RequestValue(countryId))
                .build()
                .getObjectObservable(GetAppSettingsConsent.ResponseValue.class);
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.loginEmail())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginEmail.ResponseValue.class);
    }

    @Override
    public Observable<JSONObject> callLoginViaFacebook(LoginFacebook.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.loginFb())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.loginTicket())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginEmail.ResponseValue.class);
    }

    @Override
    public Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.logout())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(Logout.ResponseValue.class);
    }

    @Override
    public Observable<LoginVisa.ResponseValue> callLoginVisaCheckout(LoginVisa.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.visaCheckoutLogin())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginVisa.ResponseValue.class);
    }

    @Override
    public Observable<RegisterUserResponse> callRegister(RegisterUserRequest requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.registration())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(RegisterUserResponse.class);
    }

    @Override
    public Observable<ForgotPasswordResponseBody> callForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.forgotPassword())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(forgotPasswordRequest))
                .build()
                .getObjectObservable(ForgotPasswordResponseBody.class);
    }

    @Override
    public Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getUserAddresses())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetAddresses.ResponseValue.class);
    }

    @Override
    public Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.setUserDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(AddAddress.ResponseValue.class);
    }

    @Override
    public Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.applyDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(ApplyAddressResponse.class);
    }

    @Override
    public Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.deleteUserDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(DeleteUserAddress.ResponseValue.class);
    }

    @Override
    public Observable<GetPaymentsList.ResponseValue> callGetPaymentsList(GetPaymentsList.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPaymentsList())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetPaymentsList.ResponseValue.class);
    }

    @Override
    public Observable<GetOrderPaymentDetails.ResponseValue> callGetOrderPaymentDetails(GetOrderPaymentDetails.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getOrderPaymentDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetOrderPaymentDetails.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getCurrentOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues, true))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<GetUserPaymentMethods.ResponseValue> callGetUserPaymentMethods(GetUserPaymentMethods.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getUserPaymentMethods())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetUserPaymentMethods.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callAdjustQuantityOrderItem(String url, AdjustOrderItem.RequestValue requestValues) {
        String endPoint;
        switch (url) {
            case "IncreaseOrderItem":
                endPoint = ApiEndPoint.increaseOrderItem();
                break;
            case "DecreaseOrderItem":
                endPoint = ApiEndPoint.decreaseOrderItem();
                break;
            default:
                endPoint = "";
                break;
        }
        return Rx2AndroidNetworking.post(endPoint)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues, true))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentMethod())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentMethod.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentMethodStripe.ResponseValue> callCreatePaymentMethodStripe(CreatePaymentMethodStripe.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentMethod())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(CreatePaymentMethodStripe.ResponseValue.class);
    }

    @Override
    public Observable<GetPaymentToken.ResponseValue> callGetPaymentToken(GetPaymentToken.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPaymentToken())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetPaymentToken.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callApplyVouchers(ApplyVouchers.RequestValue requestValues) {
        return null;
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callClearVouchers(ClearVouchers.RequestValue requestValues) {
        return null;
    }

    @Override
    public Observable<ClearOrder.ResponseValue> callClearOrder(ClearOrder.RequestValue requestValues) {
        return null;
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransaction(CreatePaymentTransaction.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentTransaction())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentTransaction.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionVco(CreatePaymentTransactionVco.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentTransaction())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentTransaction.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionStripe(CreatePaymentTransactionStripe.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentTransaction())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentTransaction.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentIntentStripe(CreatePaymentIntentStripe.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentTransaction())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentTransaction.ResponseValue.class);
    }

    @Override
    public Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.removeUserPaymentMethod())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(RemoveUserPaymentMethod.ResponseValue.class);
    }

    @Override
    public Observable<GetDeliveryServicePackageDetails.ResponseValue> callGetDeliveryServicePackageDetails(GetDeliveryServicePackageDetails.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getDeliveryServicePackageDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(GetDeliveryServicePackageDetails.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callSetDeliveryOption(SetDeliveryOption setDeliveryOption) {
        return Rx2AndroidNetworking.post(ApiEndPoint.setDeliveryOption())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setDeliveryOption, true))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<BasketQuantityResponse> callGetBasketItemsQuantity() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getBasketQuantity())
                .addHeaders(mApiHeader.get())
                .doNotCacheResponse()
                .build()
                .getObjectObservable(BasketQuantityResponse.class);
    }


    @Override
    public Observable<GetInviteResponse> callGetInvite(GetInviteRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getInvite())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(GetInviteResponse.class);
    }

    @Override
    public Observable<SetInviteResponse> callSetInvite(SetInviteRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.setInvite())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(SetInviteResponse.class);
    }


    @Override
    public Observable<ContactOrders> callGetContactOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContactInvoices())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(ContactOrders.class);
    }

    @Override
    public Observable<ContactSubjects> callGetContactSubjects(ContactSubjectsRequest contactSubjectsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContactSubjects())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(contactSubjectsRequest))
                .build()
                .getObjectObservable(ContactSubjects.class);
    }

    @Override
    public Observable<CreateContactResponse> callCreateContact(CreateContactRequest createContactRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createContact())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .build()
                .getObjectObservable(CreateContactResponse.class);
    }

    @Override
    public Observable<ReplyContactResponse> callReplyContact(ReplyContactRequest createContactRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.answerContact())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .build()
                .getObjectObservable(ReplyContactResponse.class);
    }

    @Override
    public Observable<GetTemplateTextResponse> callGetTemplateText(GetTemplateTextRequest templateTextRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getLegalitiesText())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(templateTextRequest))
                .build()
                .getObjectObservable(GetTemplateTextResponse.class);
    }

    @Override
    public Observable<GetTemplateTextsResponse> callGetTemplateTexts(GetTemplateTextsRequest templateTextRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getTemplateTexts())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(templateTextRequest))
                .build()
                .getObjectObservable(GetTemplateTextsResponse.class);
    }

    @Override
    public Observable<RegisterDevice.ResponseValue> callRegisterDevice(RegisterDevice.RequestValue requestValue) {
        return Rx2AndroidNetworking.get(ApiEndPoint.gcmRegisterDevice())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(requestValue)
                .build()
                .getObjectObservable(RegisterDevice.ResponseValue.class);
    }

    @Override
    public Observable<NotificationEvent.ResponseValue> callNotificationEvent(NotificationEvent.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.gcmNotificationEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(NotificationEvent.ResponseValue.class);
    }

    @Override
    public Observable<JSONObject> callRegisterSubscriber(HashMap<String, Object> param) {
        /*
            3/23/2018 - feature/android-3308-registersubscriber
            added - doNotCacheResponse() to fresh call register subscriber
         */
        return Rx2AndroidNetworking.get(ApiEndPoint.gcmRegisterSubscriber())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(param)
                .doNotCacheResponse()
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<List<SortingResponse>> callSortingFacets() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getSorting())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(SortingResponse.class);
    }

    @Override
    public Observable<GetContactsResponse> callGetContacts(String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContacts())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettingsSection.RequestValue(languageId)))
                .build()
                .getObjectObservable(GetContactsResponse.class);
    }

    @Override
    public Observable<GetContactHistoryResponse.ResponseValue> callGetContactHistory(GetContactHistoryRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContact())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(GetContactHistoryResponse.ResponseValue.class);
    }

    @Override
    public Observable<AccountData> callGetAccountData() {
        return Rx2AndroidNetworking.get(ApiEndPoint.accountData())
                .addHeaders(mApiHeader.get())
                .doNotCacheResponse()
                .build()
                .getObjectObservable(AccountData.class);
    }

    @Override
    public Observable<SetUserDetailsResponse> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.saveUserDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(SetUserDetailsResponse.class);
    }

    @Override
    public Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.loadUserDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(GetUserDetailsResponse.class);
    }

    public Observable<GetUserVoucherResponse> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getUserVouchers())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getUserVouchersRequest))
                .build()
                .getObjectObservable(GetUserVoucherResponse.class);
    }

    @Override
    public Observable<GetVouchersResponse> callGetVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getVouchers())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getUserVouchersRequest))
                .build()
                .getObjectObservable(GetVouchersResponse.class);
    }

    @Override
    public Observable<ClearVouchersResponse> callGetClearVouchers(ClearVouchersRequest clearVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.clearVouchers())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(clearVouchersRequest, true))
                .build()
                .getObjectObservable(ClearVouchersResponse.class);
    }

    @Override
    public Observable<ApplyVouchersResponse> callGetApplyVouchers(ApplyVouchersRequest applyVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.applyVouchers())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(applyVouchersRequest, true))
                .build()
                .getObjectObservable(ApplyVouchersResponse.class);
    }

    @Override
    public Observable<AddVoucherByKeyResponse> callGetAddVoucherByKey(AddVoucherByKeyRequest addVoucherByKeyRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.addVoucherByKey())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(addVoucherByKeyRequest, true))
                .build()
                .getObjectObservable(AddVoucherByKeyResponse.class);
    }

    @Override
    public Observable<AddAndApplyVoucherByKeyResponse> callGetAddAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.addAndApplyVoucher())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(addAndApplyVoucherByKeyRequest, true))
                .build()
                .getObjectObservable(AddAndApplyVoucherByKeyResponse.class);
    }

    @Override
    public Observable<CurrentReturnResponseBody> callGetCurrentReturns() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturns())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(CurrentReturnResponseBody.class);
    }

    @Override
    public Observable<GetReturnOrders> callGetReturnOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturnOrders())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(GetReturnOrders.class);
    }

    @Override
    public Observable<GetReturnDetailsResponse> callGetReturnDetails(GetReturnDetailRequest getReturnDetailRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturnDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getReturnDetailRequest))
                .build()
                .getObjectObservable(GetReturnDetailsResponse.class);
    }

    @Override
    public Observable<NewReturnOrderDetailResponseBody> callGetNewReturnOrderDetail(NewReturnOrderDetailRequest newReturnOrderDetailRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturnOrderDetail())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(newReturnOrderDetailRequest))
                .build()
                .getObjectObservable(NewReturnOrderDetailResponseBody.class);
    }

    @Override
    public Observable<CreateReturnRequestResponseBody> callCreateReturnRequest(CreateReturnRequest createReturnRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createReturn())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createReturnRequest))
                .build()
                .getObjectObservable(CreateReturnRequestResponseBody.class);
    }

    @Override
    public Observable<VerificationNormalizePhoneResponseBody> callNormalizePhone(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSmsVerificationNormalizePhone())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(verificationNormalizePhoneRequest))
                .build()
                .getObjectObservable(VerificationNormalizePhoneResponseBody.class);
    }

    @Override
    public Observable<VerificationNormalizePhoneResponseBody> callVerificationCodeSend(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSmsVerificationCodeSend())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(verificationNormalizePhoneRequest))
                .build()
                .getObjectObservable(VerificationNormalizePhoneResponseBody.class);
    }

    @Override
    public Observable<VerificationCodeConfirmResponseBody> callVerificationCodeConfirm(VerificationCodeConfirmRequest verificationCodeConfirmRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSmsVerificationCodeConfirm())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(verificationCodeConfirmRequest))
                .build()
                .getObjectObservable(VerificationCodeConfirmResponseBody.class);
    }

    @Override
    public Observable<JSONObject> callMasterpassPayment(MasterPassPaymentRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.masterpassPayment())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<JSONObject> callMasterpassPostTransaction(MasterPassPostTransactionRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.masterpassPostTransaction())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<String> callSearchEvent(SearchEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callProductViewEvent(ProductViewRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callCategoryEvent(CategoryRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callSaleEvent(SaleEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callRecentlyViewedEvent(RecentlyViewedEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callEventUser() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getEventUser())
                .addHeaders(mApiHeader.get())
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callBannerClickEvent(BannerClickEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callWishlistEvent(WishlistEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getWishlistEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callRecommendationClickEvent(RecommendationEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<JSONObject> callGetPaymentMethodNonce(GetPaymentMethodNonceRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPaymentMethodNonce())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<String> callStartCheckoutEvent(StartCheckoutRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callYouMayAlsoLikeEvent(YouMayAlsoLikeEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSearchEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callFeatureUsageEvent(FeatureUsageEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getFeatureUsageEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<GetPaymentPlansResponse> callGetPaymentPlans(String countryId, String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPaymentPlans())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(
                        new GetPublicPaymentToken.RequestValue(countryId, languageId)))
                .build()
                .getObjectObservable(GetPaymentPlansResponse.class);
    }

    @Override
    public Observable<GetScheduledPlansResponse> callGetScheduledPlans(String countryId, String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getScheduledPlans())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(
                        new GetPublicPaymentToken.RequestValue(countryId, languageId)))
                .build()
                .getObjectObservable(GetScheduledPlansResponse.class);
    }

    @Override
    public Observable<GetPastPaymentsResponse> callGetPastPayments(String countryId, String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPastPayments())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(
                        new GetPublicPaymentToken.RequestValue(countryId, languageId)))
                .build()
                .getObjectObservable(GetPastPaymentsResponse.class);
    }

    @Override
    public Observable<GetDeliveryServiceResponse> callGetDeliveryService() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getDeliveryService())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectObservable(GetDeliveryServiceResponse.class);
    }

    @Override
    public Observable<GetScheduledPlansResponse> processOurpayInstallment(ProcessOurpayInstallmentRequest processOurpayInstallmentRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.processOurpayInstallment())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(processOurpayInstallmentRequest))
                .build()
                .getObjectObservable(GetScheduledPlansResponse.class);
    }

    @Override
    public Observable<CreateAfterpayOrderResponse> createAfterpayOrder(CreateAfterpayOrderRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createAfterpayOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(CreateAfterpayOrderResponse.class);
    }

    @Override
    public Observable<JSONObject> callAfterPayCreatePayment(AfterPayCreatePaymentRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callAfterPayCreatePayment())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<GetAfterpayDataResponse> callGetAfterpayData(String price) {
        return Rx2AndroidNetworking.get(ApiEndPoint.callGetAfterpayData())
                .addHeaders(mApiHeader.get())
                .addPathParameter("amount", price)
                .build()
                .getObjectObservable(GetAfterpayDataResponse.class);
    }

    @Override
    public Observable<DeepLinkDataResponse> callGetDeepLinkData(DeepLinkDataRequest deepLinkDataRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.deepLink())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(deepLinkDataRequest))
                .build()
                .getObjectObservable(DeepLinkDataResponse.class);
    }

    @Override
    public Observable<String> callChangeDeliveryAddress(ChangeDeliveryAddressRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.changeDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callCreateRefund(CreateRefundRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createRefund())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<OrderReceivedResponse> callOrderReceived(OrderReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callOrderReceived())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(receivedRequest))
                .build()
                .getObjectObservable(OrderReceivedResponse.class);
    }

    @Override
    public Observable<SetAttachmentResponse> setAttachment(SetAttachmentRequest setAttachmentRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.setAttachment())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setAttachmentRequest))
                .build()
                .getObjectObservable(SetAttachmentResponse.class);
    }

    @Override
    public Observable<FileSettingsResponse> callGetFileSettings() {
        return Rx2AndroidNetworking.get(ApiEndPoint.fileSettings())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectObservable(FileSettingsResponse.class);
    }

    @Override
    public Observable<List<GetWishlistIdResponse>> callGetWishlistIdsOnly() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getWishlistIdsOnly())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(GetWishlistIdResponse.class);
    }

    @Override
    public Observable<List<GetSaleItemsResponse.Products>> callGetWishlist() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getWishlist())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(GetSaleItemsResponse.Products.class);
    }

    @Override
    public Observable<String> callAddToWishlist(String productId, String seoIdentifier) {
        return Rx2AndroidNetworking.post(ApiEndPoint.addToWishlist())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(
                        new CallAddToWishlistRequest(productId, seoIdentifier)))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callRemoveFromWishlist(String productId) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.removeFromWishlist())
                .addHeaders(mApiHeader.get())
                .addPathParameter("product_id", productId)
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<List<RecommendedItemsResponse>> callRecommendedItems() {
        return Rx2AndroidNetworking.post(ApiEndPoint.recommendedItems())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(RecommendedItemsResponse.class);
    }

    @Override
    public Observable<List<GetYouMayAlsoLikeResponse>> callYouMayAlsoLike(String skuId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callYouMakeAlsoLike())
                .addHeaders(mApiHeader.get())
                .addPathParameter("sku_id", skuId)
                .build()
                .getObjectListObservable(GetYouMayAlsoLikeResponse.class);
    }

    @Override
    public Observable<String> callAddToRecentlyViewedItems(RecentlyViewedItemRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.addToRecentlyViewedItems())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<List<RecentlyViewedItemResponse>> callRecentlyViewedItems() {
        return Rx2AndroidNetworking.post(ApiEndPoint.recentlyViewedItems())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(RecentlyViewedItemResponse.class);
    }
}

