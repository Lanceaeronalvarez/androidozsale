package au.com.dealsdirect.data.network;

import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import java.util.LinkedHashMap;
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
        return Rx2AndroidNetworking.post(ApiEndPoint.SAMPLE_API)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addBodyParameter(request)
                .build()
                .getObjectObservable(SampleResponse.class);
    }

    @Override
    public Observable<List<GetBannerResponse>> callGetBanners(
            GetBannerRequest getBannerRequest) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SALES)
                                   .addHeaders(mApiHeader.getPublicApiHeader())
                                   .addQueryParameter(getBannerRequest)
                                   .build()
                                   .getObjectListObservable(GetBannerResponse.class);
    }

    @Override
    public Observable<GetPublicSalesCategoriesResponse> callGetPublicSalesCategories(GetPublicSalesCategoriesRequest request){
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SHOP_CATEGORIES)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSalesCategoriesResponse.class);
    }

    @Override
    public Observable<List<GetCategoryTreeResponse>> callGetGetCategories() {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_CATEGORY_TREE)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .build()
                .getObjectListObservable(GetCategoryTreeResponse.class);
    }

    @Override
    public Observable<GetSaleItemDetailsResponse> callGetSaleItemDetails(String seoIdentifierId) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PRODUCT_DETAILS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addPathParameter("seo_identifier", seoIdentifierId)
                .build()
                .getObjectObservable(GetSaleItemDetailsResponse.class);
    }

    @Override
    public Observable<GetPublicSaleDetailsResponse> callGetPublicSaleDetails(GetPublicSaleDetailsRequest request) {
        return Rx2AndroidNetworking.get(ApiEndPoint.GET_SALES_ITEM_SALE_DETAILS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addQueryParameter(request)
                .build()
                .getObjectObservable(GetPublicSaleDetailsResponse.class);
    }


   @Override public Observable<GetPublicSaleItemsResponse> callGetPublicSaleItems(
            GetPublicSaleItemsRequest getPublicSaleItemsRequest) {

        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PUBLIC_SALE_ITEMS)
                                   .addHeaders(mApiHeader.getPublicApiHeader())
                                   .addQueryParameter(getPublicSaleItemsRequest)
                                   .build()
                                   .getObjectObservable(GetPublicSaleItemsResponse.class);
    }

    @Override
    public Observable<GetSaleItemsResponse> callGetSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest) {
        LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("q", getSaleItemsRequest.getQuery());
        linkedHashMap.put("pn", getSaleItemsRequest.getPageNumber());
        linkedHashMap.put("ps", getSaleItemsRequest.getPageSize());
        linkedHashMap.put("c", getSaleItemsRequest.getCategoryKey());
        linkedHashMap.put("ff", getSaleItemsRequest.getFacetFilter());
        linkedHashMap.put("sa", getSaleItemsRequest.getLanguageID());

        return Rx2AndroidNetworking.get(ApiEndPoint.GET_PRODUCTS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addQueryParameter(linkedHashMap)
                .build()
                .getObjectObservable(GetSaleItemsResponse.class);
    }

    @Override
    public Observable<GetServerSettings.ResponseValue> callGetServerSettings(Context context, String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_SERVER_SETTING_TEST)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetServerSettings.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetServerSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(Context context, String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PUBLIC_APP_SETTINGS_TEST)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettings.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetAppSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettings.ResponseValue> callGetAppSettings(Context context, String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_APP_SETTINGS_TEST)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettings.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetAppSettings.ResponseValue.class);
    }

    @Override
    public Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(Context context, String countryId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_APP_SETTINGS_SECTION_TEST)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettingsSection.RequestValue(countryId)))
                .build()
                .getObjectObservable(GetAppSettingsSection.ResponseValue.class);
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGIN_EMAIL)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginEmail.ResponseValue.class);
    }

    @Override
    public Observable<LoginFacebook.ResponseValue> callLoginViaFacebook(LoginFacebook.RequestValue requestValue) {
        return null;
    }

    @Override
    public Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGIN_TICKET)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(LoginEmail.ResponseValue.class);
    }

    @Override
    public Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOGOUT)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(Logout.ResponseValue.class);
    }

    @Override
    public Observable<RegisterUserResponse> callRegiser(RegisterUserRequest requestValue) {
        return Rx2AndroidNetworking.post(ApiEndPoint.REGISTRATION)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValue))
                .build()
                .getObjectObservable(RegisterUserResponse.class);
    }

    @Override
    public Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_USER_ADDRESSES)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetAddresses.ResponseValue.class);
    }

    @Override
    public Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SET_USER_DELIVERY_ADDRESS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(AddAddress.ResponseValue.class);
    }

    @Override
    public Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.APPLY_DELIVERY_ADDRESS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(ApplyAddressResponse.class);
    }

    @Override
    public Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.DELETE_USER_DELIVERY_ADDRESS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(DeleteUserAddress.ResponseValue.class);
    }

    @Override
    public Observable<GetPaymentsList.ResponseValue> callGetPaymentsList(GetPaymentsList.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PAYMENTS_LIST)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetPaymentsList.ResponseValue.class);
    }

    @Override
    public Observable<GetOrderPaymentDetails.ResponseValue> callGetOrderPaymentDetails(GetOrderPaymentDetails.RequestValues requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_ORDER_PAYMENT_DETAILS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetOrderPaymentDetails.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callGetCurrentOrder(GetCurrentOrder.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CURRENT_ORDER)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<GetUserPaymentMethods.ResponseValue> callGetUserPaymentMethods(GetUserPaymentMethods.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_USER_PAYMENT_METHODS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetUserPaymentMethods.ResponseValue.class);
    }

    @Override
    public Observable<GetCurrentOrder.ResponseValue> callAdjustQuantityOrderItem(String url, AdjustOrderItem.RequestValue requestValues) {
        String endPoint;
        switch (url){
            case "IncreaseOrderItem":
                endPoint = ApiEndPoint.INCREASE_ORDER_ITEM;
                break;
            case "DecreaseOrderItem":
                endPoint = ApiEndPoint.DECREASE_ORDER_ITEM;
                break;
            default:
                endPoint="";
                break;
        }
        return Rx2AndroidNetworking.post(endPoint)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(GetCurrentOrder.ResponseValue.class);
    }

    @Override
    public Observable<CreatePaymentMethod.ResponseValue> callCreatePaymentMethod(CreatePaymentMethod.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.CREATE_PAYMENT_METHOD)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentMethod.ResponseValue.class);
    }

    @Override
    public Observable<GetPaymentToken.ResponseValue> callGetPaymentToken(GetPaymentToken.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_PAYMENT_TOKEN)
                .addHeaders(mApiHeader.getPublicApiHeader())
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
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(CreatePaymentTransaction.ResponseValue.class);
    }

    @Override
    public Observable<RemoveUserPaymentMethod.ResponseValue> callRemoveUserPaymentMethod(RemoveUserPaymentMethod.RequestValue requestValues) {
        return Rx2AndroidNetworking.post(ApiEndPoint.REMOVE_USER_PAYMENT_METHOD)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(requestValues))
                .build()
                .getObjectObservable(RemoveUserPaymentMethod.ResponseValue.class);
    }

    @Override
    public Observable<GetInviteResponse> callGetInvite(GetInviteRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_INVITE)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(GetInviteResponse.class);
    }

    @Override
    public Observable<SetInviteResponse> callSetInvite(SetInviteRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SET_INVITE)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(SetInviteResponse.class);
    }


    @Override
    public Observable<GetContactsResponse> callGetContacts(String languageId) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CONTACTS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(new GetAppSettingsSection.RequestValue(languageId)))
                .build()
                .getObjectObservable(GetContactsResponse.class);
    }

    @Override
    public Observable<GetContactHistoryResponse> callGetContactHistory(GetContactHistoryRequest request) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_CONTACT)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(request))
                .build()
                .getObjectObservable(GetContactHistoryResponse.class);
    }

    @Override
    public Observable<GetUserDetailsResponse.Response> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.SAVE_USER_DETAILS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(GetUserDetailsResponse.Response.class);
    }

    @Override
    public Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.LOAD_USER_DETAILS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(setUserDetailsRequest))
                .build()
                .getObjectObservable(GetUserDetailsResponse.class);
    }

    public Observable<GetUserVoucherResponse> callGetUserVouchers(GetUserVouchersRequest getUserVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_USER_VOUCHERS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(getUserVouchersRequest))
                .build()
                .getObjectObservable(GetUserVoucherResponse.class);
    }

    @Override
    public Observable<GetVouchersResponse> callGetVouchers() {
        return Rx2AndroidNetworking.post(ApiEndPoint.GET_VOUCHERS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(null))
                .build()
                .getObjectObservable(GetVouchersResponse.class);
    }

    @Override
    public Observable<ClearVouchersResponse> callGetClearVouchers(ClearVouchersRequest clearVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.CLEAR_VOUCHERS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(clearVouchersRequest))
                .build()
                .getObjectObservable(ClearVouchersResponse.class);
    }

    @Override
    public Observable<ApplyVouchersResponse> callGetApplyVouchers(ApplyVouchersRequest applyVouchersRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.APPLY_VOUCHERS)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(applyVouchersRequest))
                .build()
                .getObjectObservable(ApplyVouchersResponse.class);
    }

    @Override
    public Observable<AddVoucherByKeyResponse> callGetAddVoucherByKey(AddVoucherByKeyRequest addVoucherByKeyRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ADD_VOUCHER_BY_KEY)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(addVoucherByKeyRequest))
                .build()
                .getObjectObservable(AddVoucherByKeyResponse.class);
    }

    @Override
    public Observable<AddAndApplyVoucherByKeyResponse> callGetAddAndApplyVoucherByKey(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest) {
        return Rx2AndroidNetworking.post(ApiEndPoint.ADD_AND_APPLY_VOUCHER)
                .addHeaders(mApiHeader.getPublicApiHeader())
                .addJSONObjectBody(JsonUtils.convertToJsonObject(addAndApplyVoucherByKeyRequest))
                .build()
                .getObjectObservable(AddAndApplyVoucherByKeyResponse.class);
    }
}

