package au.com.dealsdirect.data.network;

import com.google.gson.JsonObject;
import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsConsent;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getpublicpaymenttoken.GetPublicPaymentToken;
import com.rx2androidnetworking.Rx2ANRequest;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import au.com.dealsdirect.data.network.model.SampleRequest;
import au.com.dealsdirect.data.network.model.SampleResponse;
import au.com.dealsdirect.data.network.model.accountdata.AccountData;
import au.com.dealsdirect.data.network.model.address.ApplyAddressRequest;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.address.ChangeDeliveryAddressRequest;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.data.network.model.afterpay.AfterPayCreatePaymentRequest;
import au.com.dealsdirect.data.network.model.afterpay.CreateAfterpayOrderRequest;
import au.com.dealsdirect.data.network.model.afterpay.CreateAfterpayOrderResponse;
import au.com.dealsdirect.data.network.model.afterpay.GetAfterpayDataResponse;
import au.com.dealsdirect.data.network.model.agerestriction.SaveAgeRestrictedConsentDataRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.banner.GetLeaderboardBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetSaleBannerDetailsResponse;
import au.com.dealsdirect.data.network.model.banner.GetTopBrandsResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeRequest;
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
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionGPay;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionStripe;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionVco;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce.GetPaymentMethodNonceRequest;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateOrderRequest;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateOrderResponse;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionRequest;
import au.com.dealsdirect.data.network.model.checkout.klarna.KlarnaCreateSessionResponse;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.TicketSatisfactionRequest;
import au.com.dealsdirect.data.network.model.contacthistory.TicketSatisfactionResponse;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
import au.com.dealsdirect.data.network.model.contactorder.ContactOrderResponse;
import au.com.dealsdirect.data.network.model.contactreply.ReplyContactRequest;
import au.com.dealsdirect.data.network.model.contactsubject.ContactSubjectResponse;
import au.com.dealsdirect.data.network.model.createcontact.CreateContactRequest;
import au.com.dealsdirect.data.network.model.events.BannerClickEventRequest;
import au.com.dealsdirect.data.network.model.events.CategoryRequest;
import au.com.dealsdirect.data.network.model.events.CommonCheckoutRequest;
import au.com.dealsdirect.data.network.model.events.DeliveryPriceViewEventRequest;
import au.com.dealsdirect.data.network.model.events.FeatureUsageEventRequest;
import au.com.dealsdirect.data.network.model.events.ProductViewRequest;
import au.com.dealsdirect.data.network.model.events.RecentlyViewedEventRequest;
import au.com.dealsdirect.data.network.model.events.RecommendationEventRequest;
import au.com.dealsdirect.data.network.model.events.SaleEventRequest;
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.data.network.model.events.SellerLinkEventRequest;
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
import au.com.dealsdirect.data.network.model.legalities.TemplateTextResponse;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginFacebook;
import au.com.dealsdirect.data.network.model.login.LoginTicket;
import au.com.dealsdirect.data.network.model.login.LoginVisa;
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.data.network.model.lpay.ConfirmLPayTransactionRequest;
import au.com.dealsdirect.data.network.model.lpay.CreateLPayOrderRequest;
import au.com.dealsdirect.data.network.model.lpay.CreateLPayOrderResponse;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPaymentRequest;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPostTransactionRequest;
import au.com.dealsdirect.data.network.model.orders.CancelInvoiceItemRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrdersResponse;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedRequest;
import au.com.dealsdirect.data.network.model.orders.OrderReceivedSatisfactionResponse;
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
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeDefaultResponse;
import au.com.dealsdirect.data.network.model.productdetails.GetPostcodeShippingPriceResponse;
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
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponse;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.ReturnReceivedSatisfactionResponse;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturn;
import au.com.dealsdirect.data.network.model.returns.newreturn.ImageAttachment;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnItem;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.BuyBoxTemplateTextResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecentlyViewedItemResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.RecommendedItemsResponse;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetPublicSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.saleitems.SaleItemProduct;
import au.com.dealsdirect.data.network.model.setattachmentforcontact.SetAttachmentForContactRequest;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetEmailSubscriptionTemplatesResponse;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.data.network.model.userdetails.UpdateUserEmailSubscriptionRequest;
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
import au.com.dealsdirect.data.network.model.vouchers.RemoveVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.RemoveVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.wishlist.GetWishlistIdResponse;
import au.com.dealsdirect.data.network.model.zippay.ZipPayConfirmNzOrderRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreateChargeRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreateCheckoutRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreateCheckoutResponse;
import au.com.dealsdirect.data.network.model.zippay.ZipPayCreateNzOrderRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreatePublicChargeRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipCreatePublicCheckoutRequest;
import au.com.dealsdirect.data.network.model.zippay.ZipPayCreateNzOrderResponse;
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
    public Observable<GetBannerResponse> callGetLeaderboardBanner(GetLeaderboardBannerRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getSales2())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .getResponseOnlyFromNetwork()
                .build()
                .getObjectObservable(GetBannerResponse.class);
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
    public Observable<GetBannerResponse> callGetBanners2(
            GetBannerRequest getBannerRequest, boolean getOnlyFromNetwork) {
        if (getOnlyFromNetwork) {
            return Rx2AndroidNetworking.get(ApiEndPoint.getSales2())
                    .addHeaders(mApiHeader.get())
                    .addQueryParameter(getBannerRequest)
                    .getResponseOnlyFromNetwork()
                    .build()
                    .getObjectObservable(GetBannerResponse.class);
        } else {
            return Rx2AndroidNetworking.get(ApiEndPoint.getSales2())
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
    public Observable<JSONObject> callGetSaleBannerDetails2(String externalSaleId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getSaleBannerDetails2())
                .addHeaders(mApiHeader.get())
                .addQueryParameter("encodedId", externalSaleId)
                .build()
                .getJSONObjectObservable();
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
    public Observable<List<GetCategoryTreeResponse>> callGetCategories(GetCategoryTreeRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getCategoryTree())
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .build()
                .getObjectListObservable(GetCategoryTreeResponse.class);
    }

    @Override
    public Observable<List<GetTopBrandsResponse>> callGetTopBrands() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getTopBrands())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(GetTopBrandsResponse.class);
    }

    @Override
    public Observable<SaleItemDetails> callGetSaleItemDetails(String seoIdentifierId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getProductDetailsWithoutSales())
                .addHeaders(mApiHeader.get())
                .addPathParameter("seo_identifier", seoIdentifierId)
                .build()
                .getObjectObservable(SaleItemDetails.class);
    }

    @Override
    public Observable<SaleItemDetails> callGetSaleItemDetails(String saleId, String seoIdentifierId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getProductDetailsWithSales())
                .addHeaders(mApiHeader.get())
                .addPathParameter("seo_identifier", seoIdentifierId)
                .addPathParameter("sale_id", saleId)
                .build()
                .getObjectObservable(SaleItemDetails.class);
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
        return Rx2AndroidNetworking.post(ApiEndPoint.getPromoInfo())
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
                .doNotCacheResponse()
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
    public Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(String countryId, String sectionName) {
        Rx2ANRequest.GetRequestBuilder request = Rx2AndroidNetworking.get(ApiEndPoint.getAppSettingsSection())
                .addHeaders(mApiHeader.get());
        if (sectionName != null) {
            request = request.addQueryParameter(new GetAppSettingsSection.RequestValue(countryId, sectionName));
        } else {
            request = request.addQueryParameter(new GetAppSettingsSection.RequestValue(countryId));
        }
        return request.build()
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
    public Observable<String> callSetUserDeliveryAddress(JsonObject requestValues) {
        return Rx2AndroidNetworking.put(ApiEndPoint.setUserDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getStringObservable();
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
    public Observable<String> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues, String addressID) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.deleteUserDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addPathParameter("addressId", addressID)
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<List<GetOrdersResponse.Order>> callGetOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getOrders())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(GetOrdersResponse.Order.class);
    }

    @Override
    public Observable<GetOrdersResponse> callGetOrdersHistory(String dateTime, int months) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getOrdersHistory())
                .addHeaders(mApiHeader.get())
                .addPathParameter("from_date", dateTime)
                .addPathParameter("months", Integer.toString(months))
                .build()
                .getObjectObservable(GetOrdersResponse.class);
    }

    @Override
    public Observable<GetOrdersResponse.Order> callGetOrderDetails(int orderNumber) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getOrderDetails())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_number", Integer.toString(orderNumber))
                .build()
                .getObjectObservable(GetOrdersResponse.Order.class);
    }

    @Override
    public Observable<GetOrdersResponse.Order.Invoice.Delivery> callGetOrderTracking(int orderNumber, int invoiceNumber) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getOrderTracking())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", Integer.toString(orderNumber))
                .addPathParameter("invoice_number", Integer.toString(invoiceNumber))
                .build()
                .getObjectObservable(GetOrdersResponse.Order.Invoice.Delivery.class);
    }

    @Override
    public Observable<String> callChangeDeliveryAddress(ChangeDeliveryAddressRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.changeDeliveryAddress())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", request.getInvoiceId())
                .addPathParameter("invoice_number", Integer.toString(request.getInvoiceNumber()))
                .addPathParameter("address_id", request.getAddressId())
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<GetOrdersResponse.Order> callCancelInvoice(CancelInvoiceItemRequest request) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.cancelInvoice())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", request.getInvoiceId())
                .addPathParameter("invoice_number", Integer.toString(request.getInvoiceNumber()))
                .build()
                .getObjectObservable(GetOrdersResponse.Order.class);
    }

    @Override
    public Observable<GetOrdersResponse.Order> callCancelInvoiceItem(CancelInvoiceItemRequest request) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.cancelInvoiceItem())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", request.getInvoiceId())
                .addPathParameter("invoice_number", Integer.toString(request.getInvoiceNumber()))
                .addPathParameter("order_item_id", request.getOrderItemId())
                .addPathParameter("quantity", Integer.toString(request.getQuantity()))
                .build()
                .getObjectObservable(GetOrdersResponse.Order.class);
    }

    @Override
    public Observable<String> callSetOrderReceived(OrderReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.callSetOrderReceived())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", receivedRequest.getInvoiceId())
                .addPathParameter("invoice_number", Integer.toString(receivedRequest.getInvoiceNumber()))
                .addJSONObjectBody(new JSONObject(receivedRequest.getSatisfactionMap()))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callSetOrderNotReceived(OrderReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.callSetOrderNotReceived())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", receivedRequest.getInvoiceId())
                .addPathParameter("invoice_number", Integer.toString(receivedRequest.getInvoiceNumber()))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<OrderReceivedSatisfactionResponse> callGetOrderReceivedSatisfaction(OrderReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callGetOrderReceivedSatisfaction())
                .addHeaders(mApiHeader.get())
                .addPathParameter("order_id", receivedRequest.getInvoiceId())
                .addPathParameter("invoice_number", Integer.toString(receivedRequest.getInvoiceNumber()))
                .build()
                .getObjectObservable(OrderReceivedSatisfactionResponse.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getCurrentOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues, true))
                .doNotCacheResponse()
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
                .doNotCacheResponse()
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
    public Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionGPay(CreatePaymentTransactionGPay.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.createPaymentTransactionForGPay())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues, true))
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
    public Observable<List<ContactOrderResponse>> callGetContactOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContactInvoices())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(ContactOrderResponse.class);
    }

    @Override
    public Observable<List<ContactSubjectResponse>> callGetContactSubjects(boolean isPublic) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getContactSubjects())
                .addHeaders(mApiHeader.get())
                .addPathParameter("public", Boolean.toString(isPublic))
                .build()
                .getObjectListObservable(ContactSubjectResponse.class);
    }

    @Override
    public Observable<String> callGetContactSubjectsTemplates(String id) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getContactSubjectsTemplates())
                .addHeaders(mApiHeader.get())
                .addPathParameter("id", id)
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callCreateContact(CreateContactRequest createContactRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.createContact())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .build()
                .getObjectObservable(String.class);
    }

    @Override
    public Observable<String> callCreateContactPublic(CreateContactRequest createContactRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.createContactPublic())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .build()
                .getObjectObservable(String.class);
    }

    @Override
    public Observable<String> callReplyContact(ReplyContactRequest createContactRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.answerContact())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .addPathParameter("number", createContactRequest.getNumber().toString())
                .build()
                .getObjectObservable(String.class);
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
    public Observable<List<TemplateTextResponse>> callGetNZCommissionTemplateTexts() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getNZCommissionTemplateText())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(TemplateTextResponse.class);
    }

    @Override
    public Observable<List<BuyBoxTemplateTextResponse>> callGetBuyboxTemplateTexts() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getBuyboxTemplateTexts())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(BuyBoxTemplateTextResponse.class);
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
    public Observable<List<GetContactsResponse>> callGetContacts(String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContacts())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettingsSection.RequestValue(languageId)))
                .build()
                .getObjectListObservable(GetContactsResponse.class);
    }

    @Override
    public Observable<String> callGetContactHistory(GetContactHistoryRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getContact())
                .addHeaders(mApiHeader.get())
                .addPathParameter("number", request.getNumber().toString())
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callEscalateContact(GetContactHistoryRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.escalateContact())
                .addHeaders(mApiHeader.get())
                .addPathParameter("number", request.getNumber().toString())
                .build()
                .getStringObservable();
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
    public Observable<String> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.saveUserDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.loadUserDetails())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(GetUserDetailsResponse.class);
    }

    @Override
    public Observable<String> callUpdateUserEmailSubscription(UpdateUserEmailSubscriptionRequest request) {
        return Rx2AndroidNetworking.put(ApiEndPoint.upddateEmailSubscription())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<GetEmailSubscriptionTemplatesResponse> getEmailSubscriptionTemplates() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getEmailSubscriptionTemplates())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectObservable(GetEmailSubscriptionTemplatesResponse.class);
    }

    @Override
    public Observable<String> callAccountDeletion(String userDetailsId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.accountDeletion())
                .addHeaders(mApiHeader.get())
                .addPathParameter("customer_id", userDetailsId)
                .build()
                .getStringObservable();
    }

    public Observable<List<GetUserVoucherResponse.Response>> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getUserVouchers())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getUserVouchersRequest))
                .build()
                .getObjectListObservable(GetUserVoucherResponse.Response.class);
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
    public Observable<RemoveVoucherByKeyResponse> callGetRemoveVoucherByKey(RemoveVoucherByKeyRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.removeVoucherByKey())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getObjectObservable(RemoveVoucherByKeyResponse.class);
    }

    @Override
    public Observable<List<CurrentReturn>> callGetCurrentReturns() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturns())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectListObservable(CurrentReturn.class);
    }

    @Override
    public Observable<List<GetReturnOrders>> callGetReturnOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturnOrders())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectListObservable(GetReturnOrders.class);
    }

    @Override
    public Observable<CurrentReturn> callGetReturnDetails(String returnId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturnDetails())
                .addHeaders(mApiHeader.get())
                .addPathParameter("return_id", returnId)
                .build()
                .getObjectObservable(CurrentReturn.class);
    }

    @Override
    public Observable<String> callSetContactForReturns(String returnId, int contactNumber) {
        return Rx2AndroidNetworking.put(ApiEndPoint.setContactForReturns())
                .addHeaders(mApiHeader.get())
                .addPathParameter("return_id", returnId)
                .addPathParameter("contact_number", Integer.toString(contactNumber))
                .doNotCacheResponse()
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<List<NewReturnItem>> callGetNewReturnOrderDetail(String invoiceNumber) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getReturnOrderDetail())
                .addHeaders(mApiHeader.get())
                .addPathParameter("invoice_number", invoiceNumber)
                .build()
                .getObjectListObservable(NewReturnItem.class);
    }

    @Override
    public Observable<CreateReturnRequestResponse> callCreateReturnRequest(CreateReturnRequest createReturnRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.createReturn())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createReturnRequest))
                .doNotCacheResponse()
                .build()
                .getObjectObservable(CreateReturnRequestResponse.class);
    }

    @Override
    public Observable<String> callSetReturnReceived(ReturnReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.callSetReturnReceived())
                .addHeaders(mApiHeader.get())
                .addPathParameter("return_id", receivedRequest.getReturnId())
                .addJSONObjectBody(new JSONObject(receivedRequest.getSatisfactionMap()))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callSetReturnNotReceived(ReturnReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.callSetReturnNotReceived())
                .addHeaders(mApiHeader.get())
                .addPathParameter("return_id", receivedRequest.getReturnId())
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<ReturnReceivedSatisfactionResponse> callGetReturnReceivedSatisfaction(ReturnReceivedRequest receivedRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callGetReturnReceivedSatisfaction())
                .addHeaders(mApiHeader.get())
                .addPathParameter("return_id", receivedRequest.getReturnId())
                .build()
                .getObjectObservable(ReturnReceivedSatisfactionResponse.class);
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
    public Observable<String> callStartCheckoutEvent(CommonCheckoutRequest request) {
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
    public Observable<String> callDeliveryPriceViewEvent(DeliveryPriceViewEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getDeliveryPriceViewEvent())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callSellerLinkEvent(SellerLinkEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getSellerLinkEvent())
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
    public Observable<JSONObject> callConfirmLPayTransaction(ConfirmLPayTransactionRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callConfirmLPayTransaction())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable(); //TODO: replace with response object CreateLPayOrderResponse
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
    public Observable<CreateLPayOrderResponse> callCreateLPayOrder(CreateLPayOrderRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreateLPayOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(CreateLPayOrderResponse.class);
    }
    @Override
    public Observable<String> setAttachment(String returnId, List<ImageAttachment> setAttachmentRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.setAttachment())
                .addHeaders(mApiHeader.get())
                .addPathParameter("return_id", returnId)
                .addJSONArrayBody(JsonUtils.convertToJsonArray(setAttachmentRequest))
                .doNotCacheResponse()
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> setAttachmentForContact(SetAttachmentForContactRequest setAttachmentRequest) {
        return Rx2AndroidNetworking.put(ApiEndPoint.setAttachmentForContact())
                .addHeaders(mApiHeader.get())
                .addJSONArrayBody(JsonUtils.convertToJsonArray(setAttachmentRequest.getItems()))
                .addPathParameter("number", Integer.toString(setAttachmentRequest.getNumber()))
                .addPathParameter("id", setAttachmentRequest.getMessageId())
                .build()
                .getObjectObservable(String.class);
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
                .doNotCacheResponse()
                .build()
                .getObjectListObservable(GetWishlistIdResponse.class);
    }

    @Override
    public Observable<List<SaleItemProduct>> callGetWishlistPaginated(int limit, int offset) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getWishlistPaginated())
                .addHeaders(mApiHeader.get())
                .addPathParameter("limit", Integer.toString(limit))
                .addPathParameter("offset", Integer.toString(offset))
                .build()
                .getObjectListObservable(SaleItemProduct.class);
    }

    @Override
    public Observable<List<SaleItemProduct>> callGetWishlistAll() {
        return Rx2AndroidNetworking.get(ApiEndPoint.getWishlistAll())
                .addHeaders(mApiHeader.get())
                .doNotCacheResponse()
                .build()
                .getObjectListObservable(SaleItemProduct.class);
    }

    @Override
    public Observable<String> callAddToWishlist(String productId, String seoIdentifier) {
        return Rx2AndroidNetworking.post(ApiEndPoint.addToWishlist())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(
                        new CallAddToWishlistRequest(productId, seoIdentifier)))
                .doNotCacheResponse()
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callRemoveFromWishlist(String productId) {
        return Rx2AndroidNetworking.delete(ApiEndPoint.removeFromWishlist())
                .addHeaders(mApiHeader.get())
                .addPathParameter("product_id", productId)
                .doNotCacheResponse()
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

    @Override
    public Observable<TicketSatisfactionResponse> callGetTicketSatisfaction(String number) {
        return Rx2AndroidNetworking.post(ApiEndPoint.getTicketSatisfaction())
                .addHeaders(mApiHeader.get())
                .addPathParameter("number", number)
                .build()
                .getObjectObservable(TicketSatisfactionResponse.class);
    }

    @Override
    public Observable<String> callCloseTicketSatisfaction(int global, String contactNumber) {
        return Rx2AndroidNetworking.put(ApiEndPoint.closeTicketSatisfaction())
                .addHeaders(mApiHeader.get())
                .addPathParameter("number", contactNumber)
                .addJSONObjectBody(JsonUtils.convertToJsonObject(
                        new TicketSatisfactionRequest(global)))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<GetPostcodeDefaultResponse> getPostcodeDefault() {
        return Rx2AndroidNetworking.post(ApiEndPoint.getPostcodeDefault())
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectObservable(GetPostcodeDefaultResponse.class);
    }

    @Override
    public Observable<GetPostcodeShippingPriceResponse> getPostcodeShippingPrice(String postcode, String skuid, float price, int weight, int width, int height) {
        return Rx2AndroidNetworking.get(ApiEndPoint.getPostcodeShippingPrice())
                .addHeaders(mApiHeader.get())
                .addPathParameter("postcode", postcode)
                .addPathParameter("skuid", skuid)
                .addPathParameter("price", Float.toString(price))
                .addPathParameter("weight", Integer.toString(weight))
                .addPathParameter("width", Integer.toString(width))
                .addPathParameter("height", Integer.toString(height))
                .build()
                .getObjectObservable(GetPostcodeShippingPriceResponse.class);
    }

    @Override
    public Observable<String> callSaveAgeRestrictedConsentData(SaveAgeRestrictedConsentDataRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.saveAgeRestrictedConsentData())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<KlarnaCreateSessionResponse> callCreateKlarnaSession(KlarnaCreateSessionRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreateKlarnaSession())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getObjectObservable(KlarnaCreateSessionResponse.class);
    }

    @Override
    public Observable<KlarnaCreateOrderResponse> callCreateKlarnaOrder(KlarnaCreateOrderRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreateKlarnaOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getObjectObservable(KlarnaCreateOrderResponse.class);
    }

    @Override
    public Observable<ZipCreateCheckoutResponse> callCreateZipCheckout(ZipCreateCheckoutRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreateZipCheckout())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getObjectObservable(ZipCreateCheckoutResponse.class);
    }

    @Override
    public Observable<JSONObject> callCreateZipCharge(ZipCreateChargeRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreateZipCharge())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<JSONObject> callCreatePublicZipCheckout(ZipCreatePublicCheckoutRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreatePublicZipCheckout())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<JSONObject> callCreatePublicZipCharge(ZipCreatePublicChargeRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreatePublicZipCharge())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<ZipPayCreateNzOrderResponse> callCreateZipPayNzOrder(ZipPayCreateNzOrderRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callCreateZipPayNzOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getObjectObservable(ZipPayCreateNzOrderResponse.class);
    }

    @Override
    public Observable<JSONObject> callConfirmZipPayNzOrder(ZipPayConfirmNzOrderRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.callConfirmZipPayNzOrder())
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request, true))
                .build()
                .getJSONObjectObservable();
    }
}

