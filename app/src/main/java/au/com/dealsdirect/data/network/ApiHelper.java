package au.com.dealsdirect.data.network;


import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;

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
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryRequest;
import au.com.dealsdirect.data.network.model.contacthistory.GetContactHistoryResponse;
import au.com.dealsdirect.data.network.model.contactitem.GetContactsResponse;
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
import io.reactivex.Observable;

public interface ApiHelper {

    //Elv - declare api calls here

    ApiHeader getApiHeader();

    Observable<SampleResponse> doSampleApiCall(SampleRequest request);

    Observable<List<GetBannerResponse>> doGetBannersApiCall(GetBannerRequest getPublicSalesBannerRequest);

    Observable<GetPublicSalesCategoriesResponse> doGetPublicSalesCategoriesApiCall(GetPublicSalesCategoriesRequest request);

    Observable<List<GetCategoryTreeResponse>> doGetGetCategoriesApiCall();

    Observable<GetSaleItemDetailsResponse> doGetSaleItemDetailsApiCall(String seoIdentifierId);

    Observable<GetPublicSaleDetailsResponse> doGetPublicSaleDetailsApiCall(GetPublicSaleDetailsRequest request);

    Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(GetPublicSaleItemsRequest
            getPublicSaleItemsRequest);

    Observable<GetSaleItemsResponse> getSaleItemsRequest(GetSaleItemsRequest getSaleItemsRequest);


//  CONFIG API CALLS
    Observable<GetServerSettings.ResponseValue> callGetServerSettings(Context context, String countryId);

    Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(Context context, String countryId);

    Observable<GetAppSettings.ResponseValue> callGetAppSettings(Context context, String countryId);

    Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(Context context, String countryId);

    Observable<GetContactsResponse> callGetContacts(String languageId);

    Observable<GetContactHistoryResponse> callGetContactHistory(GetContactHistoryRequest getContactHistoryRequest);

//  LOGIN API CALLS

    Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue);

    Observable<LoginFacebook.ResponseValue> callLoginViaFacebook(LoginFacebook.RequestValue requestValue);

    Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue);

    Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue);


//  REGISTER API CALLS

    Observable<RegisterUserResponse> callRegiser(RegisterUserRequest requestValue);


//   ADDRESSES API CALLS

    Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues);

    Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues);

    Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues);

    Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues);

    Observable<GetUserDetailsResponse.Response> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest);

    Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall();
//  MY ORDERS API CALLS

    Observable<GetPaymentsList.ResponseValue> callGetPaymentsList(GetPaymentsList.RequestValues requestValues);

    Observable<GetOrderPaymentDetails.ResponseValue> callGetOrderPaymentDetails(GetOrderPaymentDetails.RequestValues requestValues);

    Observable<GetUserVoucherResponse> getUserVouchersApiCall(GetUserVouchersRequest getUserVouchersRequest);

    Observable<GetVouchersResponse> getVouchersApiCall();

    Observable<ClearVouchersResponse> getClearVouchersApiCall(ClearVouchersRequest clearVouchersRequest);

    Observable<ApplyVouchersResponse> getApplyVouchersApiCall(ApplyVouchersRequest applyVouchersRequest);

    Observable<AddVoucherByKeyResponse> getAddVoucherByKeyApiCall(AddVoucherByKeyRequest addVoucherByKeyRequest);

    Observable<AddAndApplyVoucherByKeyResponse> getAddAndApplyVoucherByKeyApiCall(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest);
}
