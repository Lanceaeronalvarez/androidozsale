package au.com.dealsdirect.data.network;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.data.network.model.checkout.GetPaymentToken;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
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
import au.com.dealsdirect.data.network.model.login.Logout;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPaymentRequest;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPostTransactionRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
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
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_APP_SETTINGS)
                .addHeaders(mApiHeader.get())
                .addBodyParameter(request)
                .build()
                .getObjectObservable(SampleResponse.class);
    }

    @Override
    public Observable<List<GetBannerResponse>> callGetBanners(
            GetBannerRequest getBannerRequest, boolean getOnlyFromNetwork) {
        if(getOnlyFromNetwork) {
            return Rx2AndroidNetworking.get(ApiEndPoint.GET_SALES)
                    .addHeaders(mApiHeader.get())
                    .addQueryParameter(getBannerRequest)
                    .getResponseOnlyFromNetwork()
                    .build()
                    .getObjectListObservable(GetBannerResponse.class);
        } else {
            return Rx2AndroidNetworking.get(ApiEndPoint.GET_SALES)
                    .addHeaders(mApiHeader.get())
                    .addQueryParameter(getBannerRequest)
                    .build()
                    .getObjectListObservable(GetBannerResponse.class);
        }
    }

    @Override
    public Observable<GetPublicSalesCategoriesResponse> callGetPublicSalesCategories(GetPublicSalesCategoriesRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SHOP_CATEGORIES)
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSalesCategoriesResponse.class);
    }

    @Override
    public Observable<List<GetCategoryTreeResponse>> callGetGetCategories() {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_CATEGORY_TREE)
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(GetCategoryTreeResponse.class);
    }

    @Override
    public Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String seoIdentifierId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PRODUCT_DETAILS)
                .addHeaders(mApiHeader.get())
                .addPathParameter("seo_identifier", seoIdentifierId)
                .build()
                .getObjectObservable(GetSaleItemDetailsResponse.class);
    }

    @Override
    public Observable<GetPublicSaleDetailsResponse> callGetPublicSaleDetails(GetPublicSaleDetailsRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SALES_ITEM_SALE_DETAILS)
                .addHeaders(mApiHeader.get())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSaleDetailsResponse.class);
    }


    @Override
    public Observable<GetPublicSaleItemsResponse> callGetPublicSaleItems(
            GetPublicSaleItemsRequest getPublicSaleItemsRequest) {

        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PUBLIC_SALE_ITEMS)
                .addHeaders(mApiHeader.get())
                .addQueryParameter(getPublicSaleItemsRequest)
                .build()
                .getObjectObservable(GetPublicSaleItemsResponse.class);
    }

    @Override
    public Observable<GetSaleItemsResponse> callGetSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PRODUCTS)
                .addHeaders(mApiHeader.get())
                .addQueryParameter(getSaleItemsRequest)
                .build()
                .getObjectObservable(GetSaleItemsResponse.class);
    }

    @Override
    public Observable<String> callAddItemToCart(AddToCartRequest requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ADD_TO_CART)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build().getStringObservable();
    }

    @Override
    public Observable<GetServerSettings.ResponseValue> callGetServerSettings(String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_SERVER_SETTINGS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetServerSettings.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetServerSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PUBLIC_APP_SETTINGS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettings.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetAppSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetAppSettings(String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_APP_SETTINGS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettings.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetAppSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_APP_SETTINGS_SECTION)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettingsSection.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetAppSettingsSection.ResponseValue.class);
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGIN_EMAIL)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginEmail.ResponseValue.class);
    }

    @Override
    public Observable<JSONObject> callLoginViaFacebook(LoginFacebook.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGIN_FB)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGIN_TICKET)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginEmail.ResponseValue.class);
    }

    @Override
    public Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGOUT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(Logout.ResponseValue.class);
    }

    @Override
    public Observable<RegisterUserResponse> callRegiser(RegisterUserRequest requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.REGISTRATION)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(RegisterUserResponse.class);
    }

    @Override
    public Observable<ForgotPasswordResponseBody> callForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.FORGOT_PASSWORD)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(forgotPasswordRequest))
                .build()
                .getObjectObservable(ForgotPasswordResponseBody.class);
    }

    @Override
    public Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_USER_ADDRESSES)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetAddresses.ResponseValue.class);
    }

    @Override
    public Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SET_USER_DELIVERY_ADDRESS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(AddAddress.ResponseValue.class);
    }

    @Override
    public Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.APPLY_DELIVERY_ADDRESS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(ApplyAddressResponse.class);
    }

    @Override
    public Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.DELETE_USER_DELIVERY_ADDRESS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(DeleteUserAddress.ResponseValue.class);
    }

    @Override
    public Observable<GetPaymentsList.ResponseValue> callGetPaymentsList(GetPaymentsList.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PAYMENTS_LIST)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetPaymentsList.ResponseValue.class);
    }

    @Override
    public Observable<GetOrderPaymentDetails.ResponseValue> callGetOrderPaymentDetails(GetOrderPaymentDetails.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_ORDER_PAYMENT_DETAILS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetOrderPaymentDetails.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CURRENT_ORDER)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<GetUserPaymentMethods.ResponseValue> callGetUserPaymentMethods(GetUserPaymentMethods.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_USER_PAYMENT_METHODS)
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
                endPoint = ApiEndPoint.INCREASE_ORDER_ITEM;
                break;
            case "DecreaseOrderItem":
                endPoint = ApiEndPoint.DECREASE_ORDER_ITEM;
                break;
            default:
                endPoint = "";
                break;
        }
        return Rx2AndroidNetworking.post(endPoint)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.CREATE_PAYMENT_METHOD)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentMethod.ResponseValue.class);
    }

    @Override
    public Observable<GetPaymentToken.ResponseValue> callGetPaymentToken(GetPaymentToken.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PAYMENT_TOKEN)
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
        return Rx2AndroidNetworking.post(ApiEndPoint.CREATE_PAYMENT_TRANSACTION)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentTransaction.ResponseValue.class);
    }

    @Override
    public Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.REMOVE_USER_PAYMENT_METHOD)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(RemoveUserPaymentMethod.ResponseValue.class);
    }

    @Override
    public Observable<BasketQuantityResponse> callGetBasketItemsQuantity() {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_BASKET_QUANTITY)
                .addHeaders(mApiHeader.get())
                .doNotCacheResponse()
                .build()
                .getObjectObservable(BasketQuantityResponse.class);
    }


    @Override
    public Observable<GetInviteResponse> callGetInvite(GetInviteRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_INVITE)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(GetInviteResponse.class);
    }

    @Override
    public Observable<SetInviteResponse> callSetInvite(SetInviteRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SET_INVITE)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(SetInviteResponse.class);
    }


    @Override
    public Observable<ContactOrders> callGetContactOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CONTACT_INVOICES)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(ContactOrders.class);
    }

    @Override
    public Observable<ContactSubjects> callGetContactSubjects(ContactSubjectsRequest contactSubjectsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CONTACT_SUBJECTS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(contactSubjectsRequest))
                .build()
                .getObjectObservable(ContactSubjects.class);
    }

    @Override
    public Observable<CreateContactResponse> callCreateContact(CreateContactRequest createContactRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.CREATE_CONTACT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .build()
                .getObjectObservable(CreateContactResponse.class);
    }

    @Override
    public Observable<ReplyContactResponse> callReplyContact(ReplyContactRequest createContactRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ANSWER_CONTACT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createContactRequest))
                .build()
                .getObjectObservable(ReplyContactResponse.class);
    }

    @Override
    public Observable<GetTemplateTextResponse> callGetTemplateText(GetTemplateTextRequest templateTextRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_LEGALITIES_TEXT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(templateTextRequest))
                .build()
                .getObjectObservable(GetTemplateTextResponse.class);
    }

    @Override
    public Observable<GetTemplateTextsResponse> callGetTemplateTexts(GetTemplateTextsRequest templateTextRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_TEMPLATE_TEXTS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(templateTextRequest))
                .build()
                .getObjectObservable(GetTemplateTextsResponse.class);
    }

    @Override
    public Observable<RegisterDevice.ResponseValue> callRegisterDevice(RegisterDevice.RequestValue requestValue) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GCM_REGISTER_DEVICE)
                .addHeaders(mApiHeader.get())
                .addQueryParameter(requestValue)
                .build()
                .getObjectObservable(RegisterDevice.ResponseValue.class);
    }

    @Override
    public Observable<NotificationEvent.ResponseValue> callNotificationEvent(NotificationEvent.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GCM_NOTIFICATION_EVENT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(NotificationEvent.ResponseValue.class);
    }

    @Override
    public Observable<JSONObject> callRegisterSubscriber(HashMap<String, Object> param) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GCM_REGISTER_SUBSCRIBER)
                .addHeaders(mApiHeader.get())
                .addQueryParameter(param)
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<List<SortingResponse>> callSortingFacets() {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SORTING)
                .addHeaders(mApiHeader.get())
                .build()
                .getObjectListObservable(SortingResponse.class);
    }

    @Override
    public Observable<GetContactsResponse> callGetContacts(String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CONTACTS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettingsSection.RequestValue(languageId)))
                .build()
                .getObjectObservable(GetContactsResponse.class);
    }

    @Override
    public Observable<GetContactHistoryResponse> callGetContactHistory(GetContactHistoryRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CONTACT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(GetContactHistoryResponse.class);
    }

    @Override
    public Observable<GetUserDetailsResponse> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SAVE_USER_DETAILS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(GetUserDetailsResponse.class);
    }

    @Override
    public Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOAD_USER_DETAILS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(GetUserDetailsResponse.class);
    }

    public Observable<GetUserVoucherResponse> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_USER_VOUCHERS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getUserVouchersRequest))
                .build()
                .getObjectObservable(GetUserVoucherResponse.class);
    }

    @Override
    public Observable<GetVouchersResponse> callGetVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_VOUCHERS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getUserVouchersRequest))
                .build()
                .getObjectObservable(GetVouchersResponse.class);
    }

    @Override
    public Observable<ClearVouchersResponse> callGetClearVouchers(ClearVouchersRequest clearVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.CLEAR_VOUCHERS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(clearVouchersRequest))
                .build()
                .getObjectObservable(ClearVouchersResponse.class);
    }

    @Override
    public Observable<ApplyVouchersResponse> callGetApplyVouchers(ApplyVouchersRequest applyVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.APPLY_VOUCHERS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(applyVouchersRequest))
                .build()
                .getObjectObservable(ApplyVouchersResponse.class);
    }

    @Override
    public Observable<AddVoucherByKeyResponse> callGetAddVoucherByKey(AddVoucherByKeyRequest addVoucherByKeyRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ADD_VOUCHER_BY_KEY)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(addVoucherByKeyRequest))
                .build()
                .getObjectObservable(AddVoucherByKeyResponse.class);
    }

    @Override
    public Observable<AddAndApplyVoucherByKeyResponse> callGetAddAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ADD_AND_APPLY_VOUCHER)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(addAndApplyVoucherByKeyRequest))
                .build()
                .getObjectObservable(AddAndApplyVoucherByKeyResponse.class);
    }

    @Override
    public Observable<CurrentReturnResponseBody> callGetCurrentReturns() {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_RETURNS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(CurrentReturnResponseBody.class);
    }

    @Override
    public Observable<GetReturnOrders> callGetReturnOrders() {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_RETURN_ORDERS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(GetReturnOrders.class);
    }

    @Override
    public Observable<GetReturnDetailsResponse> callGetReturnDetails(GetReturnDetailRequest getReturnDetailRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_RETURN_DETAILS)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getReturnDetailRequest))
                .build()
                .getObjectObservable(GetReturnDetailsResponse.class);
    }

    @Override
    public Observable<NewReturnOrderDetailResponseBody> callGetNewReturnOrderDetail(NewReturnOrderDetailRequest newReturnOrderDetailRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_RETURN_ORDER_DETAIL)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(newReturnOrderDetailRequest))
                .build()
                .getObjectObservable(NewReturnOrderDetailResponseBody.class);
    }

    @Override
    public Observable<CreateReturnRequestResponseBody> callCreateReturnRequest(CreateReturnRequest createReturnRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.CREATE_RETURN)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(createReturnRequest))
                .build()
                .getObjectObservable(CreateReturnRequestResponseBody.class);
    }

    @Override
    public Observable<VerificationNormalizePhoneResponseBody> callNormalizePhone(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SMS_VERIFICATION_NORMALIZE_PHONE)
                .addJSONObjectBody(JsonUtils.convertToJsonObject(verificationNormalizePhoneRequest))
                .build()
                .getObjectObservable(VerificationNormalizePhoneResponseBody.class);
    }

    @Override
    public Observable<VerificationNormalizePhoneResponseBody> callVerificationCodeSend(VerificationNormalizePhoneRequest verificationNormalizePhoneRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SMS_VERIFICATION_CODE_SEND)
                .addJSONObjectBody(JsonUtils.convertToJsonObject(verificationNormalizePhoneRequest))
                .build()
                .getObjectObservable(VerificationNormalizePhoneResponseBody.class);
    }

    @Override
    public Observable<VerificationCodeConfirmResponseBody> callVerificationCodeConfirm(VerificationCodeConfirmRequest verificationCodeConfirmRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SMS_VERIFICATION_CODE_CONFRIM)
                .addJSONObjectBody(JsonUtils.convertToJsonObject(verificationCodeConfirmRequest))
                .build()
                .getObjectObservable(VerificationCodeConfirmResponseBody.class);    }

    @Override
    public Observable<JSONObject> callMasterpassPayment(MasterPassPaymentRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.MASTERPASS_PAYMENT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<JSONObject> callMasterpassPostTransaction(MasterPassPostTransactionRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.MASTERPASS_POST_TRANSACTION)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }

    @Override
    public Observable<String> callSearchEvent(SearchEventRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SEARCH_EVENT)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<String> callEventUser() {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_EVENT_USER)
                .addHeaders(mApiHeader.get())
                .build()
                .getStringObservable();
    }

    @Override
    public Observable<JSONObject> callGetPaymentMethodNonce(GetPaymentMethodNonceRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PAYMENT_METHOD_NONCE)
                .addHeaders(mApiHeader.get())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getJSONObjectObservable();
    }
}

