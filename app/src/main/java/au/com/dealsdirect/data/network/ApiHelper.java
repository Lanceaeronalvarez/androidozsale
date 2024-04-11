package au.com.dealsdirect.data.network;


import com.google.gson.JsonObject;
import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsConsent;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.mysale.genie.utility.config.model.getpublicpaymenttoken.GetPublicPaymentToken;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

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
import io.reactivex.Observable;

public interface ApiHelper {

    //Elv - declare api calls here

    ApiHeader getApiHeader();

    Observable<SampleResponse> doSampleApiCall(SampleRequest request);

    Observable<GetBannerResponse> callGetLeaderboardBanner(GetLeaderboardBannerRequest request);

    Observable<GetBannerResponse> callGetBanners(GetBannerRequest getPublicSalesBannerRequest, boolean getOnlyFromNetwork);

    Observable<GetBannerResponse> callGetBanners2(GetBannerRequest getPublicSalesBannerRequest, boolean getOnlyFromNetwork);

    Observable<GetSaleBannerDetailsResponse> callGetSaleBannerDetails(String saleId);

    Observable<JSONObject> callGetSaleBannerDetails2(String externalSaleId);

    Observable<GetPublicSalesCategoriesResponse> callGetPublicSalesCategories(GetPublicSalesCategoriesRequest request);

    Observable<List<GetCategoryTreeResponse>> callGetCategories(GetCategoryTreeRequest request);

    Observable<List<GetTopBrandsResponse>> callGetTopBrands();

    Observable<SaleItemDetails> callGetSaleItemDetails(String saleId, String seoIdentifierId);

    Observable<SaleItemDetails> callGetSaleItemDetails(String seoIdentifierId);

    Observable<OurpayDataResponse> callGetOurpayData(OurpayDataRequest request);

    Observable<GetPublicSaleDetailsResponse> callGetPublicSaleDetails(GetPublicSaleDetailsRequest request);

    Observable<GetPublicSaleItemsResponse> callGetPublicSaleItems(GetPublicSaleItemsRequest getPublicSaleItemsRequest);

    Observable<GetSaleItemsResponse> callGetSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest);

    Observable<String> callDynamicDiscount(String skuId);

    Observable<AddToCartResponse.Response> callAddItemToCart(AddToCartRequest requestValues);

    Observable<PromoInfoResponse> callPromoInfo(String skuId);

    // CONFIG API CALLS
    Observable<GetServerSettings.ResponseValue> callGetServerSettings(String countryId);

    Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(String countryId);

    Observable<GetPublicPaymentToken.ResponseValue> callGetPublicPaymentToken(String countryId, String languageId);

    Observable<GetAppSettings.ResponseValue> callGetAppSettings(String countryId);

    Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(String countryId, String sectionName);

    Observable<GetAppSettingsSection.ResponseValue> callGetPublicAppSettingsSections(String countryId);

    Observable<GetAppSettingsSection.ResponseValue> callGetPublicAppSettingsSections(String countryId, String sectionName);

    Observable<GetAppSettingsConsent.ResponseValue> callGetAppSettingsConsent(String countryId);

    Observable<GetConsentDataResponse> callGetConsentData(String countryId);

    Observable<SaveConsentDataResponse> callSaveConsentData(String countryId);

    Observable<SaveReceiveSalesResponse> callSaveReceiveSales(SaveReceiveSalesRequest saveReceiveSalesRequest);

    Observable<GetAppSettingsConsent.ResponseValue> callGetPublicAppSettingsConsent(String countryId);

    Observable<List<GetContactsResponse>> callGetContacts(String languageId);

    Observable<String> callGetContactHistory(GetContactHistoryRequest getContactHistoryRequest);

    Observable<AccountData> callGetAccountData();
    // LOGIN API CALLS

    Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue);

    Observable<JSONObject> callLoginViaFacebook(LoginFacebook.RequestValue requestValue);

    Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue);

    Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue);

    Observable<LoginVisa.ResponseValue> callLoginVisaCheckout(LoginVisa.RequestValue requestValue);

    // REGISTER API CALLS

    Observable<RegisterUserResponse> callRegister(RegisterUserRequest requestValue);

    // FORGOT PASSWORD API CALL

    Observable<ForgotPasswordResponseBody> callForgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    // ADDRESSES API CALLS

    Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues);

    Observable<String> callSetUserDeliveryAddress(JsonObject requestValues);

    Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues);

    Observable<String> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues, String addressID);

    // USER DETAILS

    Observable<String> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest);

    Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest);

    Observable<String> callAccountDeletion(String userDetailsId);

    Observable<String> callUpdateUserEmailSubscription(UpdateUserEmailSubscriptionRequest request);

    Observable<GetEmailSubscriptionTemplatesResponse> getEmailSubscriptionTemplates();

    // MY ORDERS API CALLS

    Observable<List<GetOrdersResponse.Order>> callGetOrders();

    Observable<GetOrdersResponse> callGetOrdersHistory(String dateTime, int months);

    Observable<GetOrdersResponse.Order> callGetOrderDetails(int orderNumber);

    Observable<GetOrdersResponse.Order.Invoice.Delivery> callGetOrderTracking(int orderNumber, int invoiceNumber);

    Observable<String> callChangeDeliveryAddress(ChangeDeliveryAddressRequest request);

    Observable<GetOrdersResponse.Order> callCancelInvoice(CancelInvoiceItemRequest request);

    Observable<GetOrdersResponse.Order> callCancelInvoiceItem(CancelInvoiceItemRequest request);

    Observable<String> callSetOrderReceived(OrderReceivedRequest receivedRequest);

    Observable<String> callSetOrderNotReceived(OrderReceivedRequest receivedRequest);

    Observable<OrderReceivedSatisfactionResponse> callGetOrderReceivedSatisfaction(OrderReceivedRequest receivedRequest);

    // VOUCHERS API CALLS

    Observable<GetVouchersResponse> callGetVouchers(GetUserVouchersRequest getUserVouchersRequest);

    Observable<List<GetUserVoucherResponse.Response>> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest);

    Observable<ClearVouchersResponse> callGetClearVouchers(ClearVouchersRequest clearVouchersRequest);

    Observable<ApplyVouchersResponse> callGetApplyVouchers(ApplyVouchersRequest applyVouchersRequest);

    Observable<AddVoucherByKeyResponse> callGetAddVoucherByKey(AddVoucherByKeyRequest addVoucherByKeyRequest);

    Observable<AddAndApplyVoucherByKeyResponse> callGetAddAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest);

    Observable<RemoveVoucherByKeyResponse> callGetRemoveVoucherByKey(RemoveVoucherByKeyRequest request);

    // CHECKOUT API CALLS
    Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue model);

    Observable<GetUserPaymentMethods.ResponseValue> callGetUserPaymentMethods(GetUserPaymentMethods.RequestValue model);

    Observable<GetCurrentOrder.ResponseValue> callAdjustQuantityOrderItem(String url, AdjustOrderItem.RequestValue model);

    Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue model);

    Observable<CreatePaymentMethodStripe.ResponseValue> callCreatePaymentMethodStripe(CreatePaymentMethodStripe.RequestValue model);

    Observable<GetPaymentToken.ResponseValue> callGetPaymentToken(GetPaymentToken.RequestValue model);

    Observable<GetCurrentOrder.ResponseValue> callApplyVouchers(ApplyVouchers.RequestValue model);

    Observable<GetCurrentOrder.ResponseValue> callClearVouchers(ClearVouchers.RequestValue model);

    Observable<ClearOrder.ResponseValue> callClearOrder(ClearOrder.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransaction(CreatePaymentTransaction.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionGPay(CreatePaymentTransactionGPay.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionVco(CreatePaymentTransactionVco.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionStripe(CreatePaymentTransactionStripe.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentIntentStripe(CreatePaymentIntentStripe.RequestValue model);

    Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue model);

    Observable<GetDeliveryServicePackageDetails.ResponseValue> callGetDeliveryServicePackageDetails(GetDeliveryServicePackageDetails.RequestValue requestValue);

    Observable<GetCurrentOrder.ResponseValue> callSetDeliveryOption(SetDeliveryOption setDeliveryOption);

    Observable<BasketQuantityResponse> callGetBasketItemsQuantity();

    //  INVITE API CALLS
    Observable<GetInviteResponse> callGetInvite(GetInviteRequest request);

    Observable<SetInviteResponse> callSetInvite(SetInviteRequest request);

    // Contact Us Api Call
    Observable<List<ContactOrderResponse>> callGetContactOrders();

    Observable<List<ContactSubjectResponse>> callGetContactSubjects(boolean isPublic);

    Observable<String> callGetContactSubjectsTemplates(String id);

    Observable<String> callCreateContact(CreateContactRequest createContactRequest);

    Observable<String> callCreateContactPublic(CreateContactRequest createContactRequest);

    Observable<String> callReplyContact(ReplyContactRequest createContactRequest);

    Observable<String> callEscalateContact(GetContactHistoryRequest getContactHistoryRequest);

    // LEGALITIES API CALLS
    Observable<GetTemplateTextResponse> callGetTemplateText(GetTemplateTextRequest templateTextRequest);

    Observable<GetTemplateTextsResponse> callGetTemplateTexts(GetTemplateTextsRequest templateTextRequest);

    Observable<List<TemplateTextResponse>> callGetNZCommissionTemplateTexts();

    public Observable<List<BuyBoxTemplateTextResponse>> callGetBuyboxTemplateTexts();

    //GCM api calls
    Observable<RegisterDevice.ResponseValue> callRegisterDevice(RegisterDevice.RequestValue requestValue);

    Observable<NotificationEvent.ResponseValue> callNotificationEvent(NotificationEvent.RequestValue requestValue);

    Observable<JSONObject> callRegisterSubscriber(HashMap<String, Object> param);

    //SORTING Facet Api Call
    Observable<List<SortingResponse>> callSortingFacets();

    // RETURN API CALLS
    Observable<List<CurrentReturn>> callGetCurrentReturns();

    Observable<List<GetReturnOrders>> callGetReturnOrders();

    Observable<CurrentReturn> callGetReturnDetails(String returnId);

    Observable<String> callSetContactForReturns(String returnId, int contactNumber);

    Observable<List<NewReturnItem>> callGetNewReturnOrderDetail(String invoiceNumber);

    Observable<CreateReturnRequestResponse> callCreateReturnRequest(CreateReturnRequest createReturnRequest);

    Observable<String> callSetReturnReceived(ReturnReceivedRequest receivedRequest);

    Observable<String> callSetReturnNotReceived(ReturnReceivedRequest receivedRequest);

    Observable<ReturnReceivedSatisfactionResponse> callGetReturnReceivedSatisfaction(ReturnReceivedRequest receivedRequest);


    // SMS VERIFICATION
    Observable<VerificationNormalizePhoneResponseBody> callNormalizePhone(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest);

    Observable<VerificationNormalizePhoneResponseBody> callVerificationCodeSend(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest);

    Observable<VerificationCodeConfirmResponseBody> callVerificationCodeConfirm(VerificationCodeConfirmRequest verificationCodeConfirmRequest);


    // MASTERPASS CALLS
    Observable<JSONObject> callMasterpassPayment(MasterPassPaymentRequest request);

    Observable<JSONObject> callMasterpassPostTransaction(MasterPassPostTransactionRequest request);

    // EVENT
    Observable<String> callSearchEvent(SearchEventRequest request);

    Observable<String> callProductViewEvent(ProductViewRequest request);

    Observable<String> callCategoryEvent(CategoryRequest request);

    Observable<String> callSaleEvent(SaleEventRequest request);

    Observable<String> callRecentlyViewedEvent(RecentlyViewedEventRequest request);

    Observable<String> callEventUser();

    Observable<String> callBannerClickEvent(BannerClickEventRequest request);

    Observable<String> callWishlistEvent(WishlistEventRequest request);

    Observable<String> callRecommendationClickEvent(RecommendationEventRequest request);

    Observable<JSONObject> callGetPaymentMethodNonce(GetPaymentMethodNonceRequest request);

    Observable<String> callStartCheckoutEvent(CommonCheckoutRequest request);

    Observable<String> callYouMayAlsoLikeEvent(YouMayAlsoLikeEventRequest request);

    Observable<String> callFeatureUsageEvent(FeatureUsageEventRequest request);

    Observable<String> callDeliveryPriceViewEvent(DeliveryPriceViewEventRequest request);

    Observable<String> callSellerLinkEvent(SellerLinkEventRequest request);

    // OURPAY
    Observable<GetPaymentPlansResponse> callGetPaymentPlans(String countryId, String languageId);

    Observable<GetScheduledPlansResponse> callGetScheduledPlans(String countryId, String languageId);

    Observable<GetPastPaymentsResponse> callGetPastPayments(String countryId, String languageId);

    Observable<GetDeliveryServiceResponse> callGetDeliveryService();

    Observable<GetScheduledPlansResponse> processOurpayInstallment(ProcessOurpayInstallmentRequest request);

    // AFTERPAY

    Observable<CreateAfterpayOrderResponse> createAfterpayOrder(CreateAfterpayOrderRequest request);

    Observable<JSONObject> callAfterPayCreatePayment(AfterPayCreatePaymentRequest request);

    Observable<GetAfterpayDataResponse> callGetAfterpayData(String price);

    // LPAY

    Observable<CreateLPayOrderResponse> callCreateLPayOrder(CreateLPayOrderRequest request);

    Observable<JSONObject> callConfirmLPayTransaction(ConfirmLPayTransactionRequest request);

    // ATTACHMENTS
    Observable<String> setAttachment(String returnId, List<ImageAttachment> setAttachmentRequest);

    Observable<String> setAttachmentForContact(SetAttachmentForContactRequest setAttachmentRequest);

    // FILE SETTINGS
    Observable<FileSettingsResponse> callGetFileSettings();

    // WISHLIST
    Observable<List<GetWishlistIdResponse>> callGetWishlistIdsOnly();

    Observable<List<SaleItemProduct>> callGetWishlistAll();

    Observable<List<SaleItemProduct>> callGetWishlistPaginated(int limit, int offset);

    Observable<String> callAddToWishlist(String productId, String seoIdentifier);

    Observable<String> callRemoveFromWishlist(String productId);

    Observable<List<RecommendedItemsResponse>> callRecommendedItems();

    Observable<List<GetYouMayAlsoLikeResponse>> callYouMayAlsoLike(String skuId);

    Observable<String> callAddToRecentlyViewedItems(RecentlyViewedItemRequest request);

    Observable<List<RecentlyViewedItemResponse>> callRecentlyViewedItems();

    Observable<TicketSatisfactionResponse> callGetTicketSatisfaction(String number);

    Observable<String> callCloseTicketSatisfaction(int global, String contactNumber);

    Observable<GetPostcodeDefaultResponse> getPostcodeDefault();

    Observable<GetPostcodeShippingPriceResponse> getPostcodeShippingPrice(String postcode, String skuid, float price, int weight, int width, int height);

    Observable<String> callSaveAgeRestrictedConsentData(SaveAgeRestrictedConsentDataRequest request);

    // klarna
    Observable<KlarnaCreateSessionResponse> callCreateKlarnaSession(KlarnaCreateSessionRequest request);

    Observable<KlarnaCreateOrderResponse> callCreateKlarnaOrder(KlarnaCreateOrderRequest request);
}
