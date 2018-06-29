package au.com.dealsdirect.data.network;


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
import au.com.dealsdirect.data.network.model.checkout.BasketQuantityResponse;
import au.com.dealsdirect.data.network.model.checkout.ClearOrder;
import au.com.dealsdirect.data.network.model.checkout.ClearVouchers;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransaction;
import au.com.dealsdirect.data.network.model.checkout.CreatePaymentTransactionVco;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetDeliveryServicePackageDetails;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.SetDeliveryOption;
import au.com.dealsdirect.data.network.model.checkout.getpaymentmethodnonce.GetPaymentMethodNonceRequest;
import au.com.dealsdirect.data.network.model.consentdata.GetConsentDataResponse;
import au.com.dealsdirect.data.network.model.consentdata.SaveConsentDataResponse;
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
import au.com.dealsdirect.data.network.model.events.SearchEventRequest;
import au.com.dealsdirect.data.network.model.fcm.NotificationEvent;
import au.com.dealsdirect.data.network.model.fcm.RegisterDevice;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordRequest;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordResponseBody;
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
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.data.network.model.ourpaydashboard.deliveryservice.GetDeliveryServiceResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.pastpayments.GetPastPaymentsResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.paymentplans.GetPaymentPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydashboard.scheduledplans.GetScheduledPlansResponse;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataRequest;
import au.com.dealsdirect.data.network.model.ourpaydata.OurpayDataResponse;
import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmRequest;
import au.com.dealsdirect.data.network.model.ourpayverificationcodeconfirm.VerificationCodeConfirmResponseBody;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneRequest;
import au.com.dealsdirect.data.network.model.ourpayverificationnormalizephone.VerificationNormalizePhoneResponseBody;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsRequest;
import au.com.dealsdirect.data.network.model.productdetails.GetPublicSaleDetailsResponse;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesRequest;
import au.com.dealsdirect.data.network.model.publicsalescategories.GetPublicSalesCategoriesResponse;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.currentreturn.CurrentReturnResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponseBody;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponse;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.GetSaleItemDetailsResponse;
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
import io.reactivex.Observable;

public interface ApiHelper {

    //Elv - declare api calls here

    ApiHeader getApiHeader();

    Observable<SampleResponse> doSampleApiCall(SampleRequest request);

    Observable<List<GetBannerResponse>> callGetBanners(GetBannerRequest getPublicSalesBannerRequest, boolean getOnlyFromNetwork);

    Observable<GetPublicSalesCategoriesResponse> callGetPublicSalesCategories(GetPublicSalesCategoriesRequest request);

    Observable<List<GetCategoryTreeResponse>> callGetGetCategories();

    Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String seoIdentifierId);

    Observable<OurpayDataResponse> callGetOurpayData(OurpayDataRequest request);

    Observable<GetPublicSaleDetailsResponse> callGetPublicSaleDetails(GetPublicSaleDetailsRequest request);

    Observable<GetPublicSaleItemsResponse> callGetPublicSaleItems(GetPublicSaleItemsRequest getPublicSaleItemsRequest);

    Observable<GetSaleItemsResponse> callGetSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest);

    Observable<String> callAddItemToCart(AddToCartRequest requestValues);

    // CONFIG API CALLS
    Observable<GetServerSettings.ResponseValue> callGetServerSettings(String countryId);

    Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(String countryId);

    Observable<GetPublicPaymentToken.ResponseValue> callGetPublicPaymentToken(String countryId, String languageId);

    Observable<GetAppSettings.ResponseValue> callGetAppSettings(String countryId);

    Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(String countryId);

    Observable<GetAppSettingsConsent.ResponseValue> callGetAppSettingsConsent(String countryId);

    Observable<GetConsentDataResponse> callGetConsentData(String countryId);

    Observable<SaveConsentDataResponse> callSaveConsentData(String countryId);

    Observable<GetAppSettingsConsent.ResponseValue> callGetPublicAppSettingsConsent(String countryId);

    Observable<GetContactsResponse> callGetContacts(String languageId);

    Observable<GetContactHistoryResponse.ResponseValue> callGetContactHistory(GetContactHistoryRequest getContactHistoryRequest);

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

    Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues);

    Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues);

    Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues);

    Observable<SetUserDetailsResponse> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest);

    Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest);

    // MY ORDERS API CALLS

    Observable<GetPaymentsList.ResponseValue> callGetPaymentsList(GetPaymentsList.RequestValues requestValues);

    Observable<GetOrderPaymentDetails.ResponseValue> callGetOrderPaymentDetails(GetOrderPaymentDetails.RequestValues requestValues);

    // VOUCHERS API CALLS

    Observable<GetVouchersResponse> callGetVouchers(GetUserVouchersRequest getUserVouchersRequest);

    Observable<GetUserVoucherResponse> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest);

    Observable<ClearVouchersResponse> callGetClearVouchers(ClearVouchersRequest clearVouchersRequest);

    Observable<ApplyVouchersResponse> callGetApplyVouchers(ApplyVouchersRequest applyVouchersRequest);

    Observable<AddVoucherByKeyResponse> callGetAddVoucherByKey(AddVoucherByKeyRequest addVoucherByKeyRequest);

    Observable<AddAndApplyVoucherByKeyResponse> callGetAddAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest);

    // CHECKOUT API CALLS
    Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue model);

    Observable<GetUserPaymentMethods.ResponseValue> callGetUserPaymentMethods(GetUserPaymentMethods.RequestValue model);

    Observable<GetCurrentOrder.ResponseValue> callAdjustQuantityOrderItem(String url, AdjustOrderItem.RequestValue model);

    Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue model);

    Observable<GetPaymentToken.ResponseValue> callGetPaymentToken(GetPaymentToken.RequestValue model);

    Observable<GetCurrentOrder.ResponseValue> callApplyVouchers(ApplyVouchers.RequestValue model);

    Observable<GetCurrentOrder.ResponseValue> callClearVouchers(ClearVouchers.RequestValue model);

    Observable<ClearOrder.ResponseValue> callClearOrder(ClearOrder.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransaction(CreatePaymentTransaction.RequestValue model);

    Observable<CreatePaymentTransaction.ResponseValue> callCreatePaymentTransactionVco(CreatePaymentTransactionVco.RequestValue model);

    Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue model);

    Observable<GetDeliveryServicePackageDetails.ResponseValue> callGetDeliveryServicePackageDetails(GetDeliveryServicePackageDetails.RequestValue requestValue);

    Observable<GetCurrentOrder.ResponseValue> callSetDeliveryOption(SetDeliveryOption setDeliveryOption);

    Observable<BasketQuantityResponse> callGetBasketItemsQuantity();

    //  INVITE API CALLS
    Observable<GetInviteResponse> callGetInvite(GetInviteRequest request);

    Observable<SetInviteResponse> callSetInvite(SetInviteRequest request);

    // Contact Us Api Call
    Observable<ContactOrders> callGetContactOrders();

    Observable<ContactSubjects> callGetContactSubjects(ContactSubjectsRequest contactSubjectsRequest);

    Observable<CreateContactResponse> callCreateContact(CreateContactRequest createContactRequest);

    Observable<ReplyContactResponse> callReplyContact(ReplyContactRequest createContactRequest);

    // LEGALITIES API CALLS
    Observable<GetTemplateTextResponse> callGetTemplateText(GetTemplateTextRequest templateTextRequest);

    Observable<GetTemplateTextsResponse> callGetTemplateTexts(GetTemplateTextsRequest templateTextRequest);


    //GCM api calls
    Observable<RegisterDevice.ResponseValue> callRegisterDevice(RegisterDevice.RequestValue requestValue);

    Observable<NotificationEvent.ResponseValue> callNotificationEvent(NotificationEvent.RequestValue requestValue);

    Observable<JSONObject> callRegisterSubscriber(HashMap<String, Object> param);

    //SORTING Facet Api Call
    Observable<List<SortingResponse>> callSortingFacets();

    // RETURN API CALLS
    Observable<CurrentReturnResponseBody> callGetCurrentReturns();

    Observable<GetReturnOrders> callGetReturnOrders();

    Observable<GetReturnDetailsResponse> callGetReturnDetails(GetReturnDetailRequest getReturnDetailRequest);

    Observable<NewReturnOrderDetailResponseBody> callGetNewReturnOrderDetail(NewReturnOrderDetailRequest newReturnOrderDetailRequest);

    Observable<CreateReturnRequestResponseBody> callCreateReturnRequest(CreateReturnRequest createReturnRequest);

    // SMS VERIFICATION
    Observable<VerificationNormalizePhoneResponseBody> callNormalizePhone(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest);

    Observable<VerificationNormalizePhoneResponseBody> callVerificationCodeSend(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest);

    Observable<VerificationCodeConfirmResponseBody> callVerificationCodeConfirm(VerificationCodeConfirmRequest verificationCodeConfirmRequest);


    // MASTERPASS CALLS
    Observable<JSONObject> callMasterpassPayment(MasterPassPaymentRequest request);

    Observable<JSONObject> callMasterpassPostTransaction(MasterPassPostTransactionRequest request);

    // EVENT
    Observable<String> callSearchEvent(SearchEventRequest request);

    Observable<String> callEventUser();

    Observable<JSONObject> callGetPaymentMethodNonce(GetPaymentMethodNonceRequest request);

    // OURPAY
    Observable<GetPaymentPlansResponse> callGetPaymentPlans();

    Observable<GetScheduledPlansResponse> callGetScheduledPlans();

    Observable<GetPastPaymentsResponse> callGetPastPayments();

    Observable<GetDeliveryServiceResponse> callGetDeliveryService();

    // DEEPLINK
    Observable<DeepLinkDataResponse> callGetDeepLinkData(DeepLinkDataRequest request);
}
