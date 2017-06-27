package au.com.dealsdirect.data.network;


import android.content.Context;

import com.mysale.genie.utility.config.api.GetAppSettings;
import com.mysale.genie.utility.config.api.GetAppSettingsSection;
import com.mysale.genie.utility.config.api.GetServerSettings;

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

    Observable<GetPublicSalesBannerResponse> getPublicSalesBannerApiCall
            (GetPublicSalesBannerRequest getPublicSalesBannerRequest);

    Observable<GetPublicSalesCategoriesResponse> doGetPublicSalesCategoriesApiCall(GetPublicSalesCategoriesRequest request);

    Observable<GetPublicItemDetailsResponse> doGetPublicItemDetailsApiCall(GetPublicItemDetailsRequest request);

    Observable<GetPublicSaleDetailsResponse> doGetPublicSaleDetailsApiCall(GetPublicSaleDetailsRequest request);

    Observable<GetPublicSaleItemsResponse> getPublicSaleItemsApiCall(GetPublicSaleItemsRequest
            getPublicSaleItemsRequest);

//  CONFIG API CALLS
    Observable<GetServerSettings.ResponseValue> callGetServerSettings(Context context, String countryId);

    Observable<GetAppSettings.ResponseValue> callGetPublicAppSettings(Context context, String countryId);

    Observable<GetAppSettings.ResponseValue> callGetAppSettings(Context context, String countryId);

    Observable<GetAppSettingsSection.ResponseValue> callGetAppSettingsSection(Context context, String countryId);

    Observable<GetContactsResponse.Response> callGetContacts(String languageId);
//  LOGIN API CALLS

    Observable<LoginEmail.ResponseValue> callLoginViaEmail(LoginEmail.RequestValue requestValue);

    Observable<LoginFacebook.ResponseValue> callLoginViaFacebook(LoginFacebook.RequestValue requestValue);

    Observable<LoginEmail.ResponseValue> callLoginTicket(LoginTicket.RequestValue requestValue);

    Observable<Logout.ResponseValue> callLogout(Logout.RequestValue requestValue);

    Observable<GetAddresses.ResponseValue> callGetUserAddresses(GetAddresses.RequestValues requestValues);

    Observable<AddAddress.ResponseValue> callSetUserDeliveryAddress(AddAddress.RequestValues requestValues);

    Observable<ApplyAddressResponse> callApplyDeliveryAddress(ApplyAddressRequest requestValues);

    Observable<DeleteUserAddress.ResponseValue> callDeleteUserDeliveryAddress(DeleteUserAddress.RequestValues requestValues);

    Observable<GetUserDetailsResponse.Response> getSaveUserDetailsApiCall(SetUserDetailsRequest setUserDetailsRequest);

    Observable<GetUserDetailsResponse> getLoadUserDetailsApiCall();

    Observable<GetUserVoucherResponse> getUserVouchersApiCall(GetUserVouchersRequest getUserVouchersRequest);

    Observable<GetVouchersResponse> getVouchersApiCall();

    Observable<ClearVouchersResponse> getClearVouchersApiCall(ClearVouchersRequest clearVouchersRequest);

    Observable<ApplyVouchersResponse> getApplyVouchersApiCall(ApplyVouchersRequest applyVouchersRequest);

    Observable<AddVoucherByKeyResponse> getAddVoucherByKeyApiCall(AddVoucherByKeyRequest addVoucherByKeyRequest);

    Observable<AddAndApplyVoucherByKeyResponse> getAddAndApplyVoucherByKeyApiCall(AddAndApplyVoucherByKeyRequest addAndApplyVoucherByKeyRequest);
}
