package au.com.dealsdirect.ui.controller.orders.orderdetails;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetailsRequest;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetailsResponse;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsPresenter<V extends OrderDetailsMvpView> extends BasePresenter<V> implements OrderDetailsMvpPresenter<V> {

    String mockCall ="{\n" +
            "  \"d\": {\n" +
            "    \"Message\": \"\",\n" +
            "    \"Result\": true,\n" +
            "    \"Value\": {\n" +
            "      \"ApprovedDate\": \"\\/Date(1436336006470)\\/\",\n" +
            "      \"Orders\": [\n" +
            "        {\n" +
            "          \"Items\": [\n" +
            "            {\n" +
            "              \"Qty\": 3,\n" +
            "              \"ItemID\": \"dc606e22-e3c6-44ef-aa76-50065c3089d7\",\n" +
            "              \"Description\": \"Baleno Kids Bedding\",\n" +
            "              \"BrandID\": \"02c1da83-994e-4fa3-a41b-d07bf8a2e265\",\n" +
            "              \"SubTotal\": {\n" +
            "                \"ItemsCount\": 3,\n" +
            "                \"ItemsAmount\": 105\n" +
            "              },\n" +
            "              \"ImageID\": \"d34a4ee7-df2f-487c-b8aa-4ff8ffb8af54\",\n" +
            "              \"Item\": \"100x120cm Forest Friends Crib Duvet Cover-4194-ForestFriends\",\n" +
            "              \"FileName\": \"4194-ForestFriends-a_225x360.JPG\",\n" +
            "              \"DeliveryAddress\": \"1 Cheeseman Ave - East , Melbourne, VIC, 3001\",\n" +
            "              \"Price\": 35,\n" +
            "              \"ID\": \"cfff9942-92c4-4e0f-bdb1-a98e46dca1d7\",\n" +
            "              \"Size\": \"\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"Customer\": \"Jeline Peña\",\n" +
            "          \"Description\": \"Baleno Kids Bedding\",\n" +
            "          \"RefundItems\": null,\n" +
            "          \"InvoiceNo\": 16892953,\n" +
            "          \"OrderID\": \"bf4621f5-ce5b-4cd0-aa19-8c2809cbaafe\",\n" +
            "          \"DeliveryAddress\": \"1 Cheeseman Ave - East , Melbourne, VIC, 3001\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"Items\": [\n" +
            "            {\n" +
            "              \"Qty\": 2,\n" +
            "              \"ItemID\": \"95a13276-8f15-4f59-aa53-7530d7ceb89f\",\n" +
            "              \"Description\": \"Trendy Tech\",\n" +
            "              \"BrandID\": \"b628511f-6be9-41a1-bcb5-e76c0157b8d7\",\n" +
            "              \"SubTotal\": {\n" +
            "                \"ItemsCount\": 2,\n" +
            "                \"ItemsAmount\": 24\n" +
            "              },\n" +
            "              \"ImageID\": \"6b9e191a-d322-4bff-9fe0-c40d70c13b8a\",\n" +
            "              \"Item\": \"Acesori PowerStick 2600mAh Powerbanks - Metallic Silver-A-STK-SIL-MetallicSilver\",\n" +
            "              \"FileName\": \"A-STK-SIL-MetallicSilver-a_225x360.JPG\",\n" +
            "              \"DeliveryAddress\": \"1 Cheeseman Ave - East , Melbourne, VIC, 3001\",\n" +
            "              \"Price\": 12,\n" +
            "              \"ID\": \"6c27f622-b04e-4203-a55d-db21dbe347d8\",\n" +
            "              \"Size\": \"\"\n" +
            "            }\n" +
            "          ],\n" +
            "          \"Customer\": \"Jeline Peña\",\n" +
            "          \"Description\": \"Trendy Tech\",\n" +
            "          \"RefundItems\": null,\n" +
            "          \"InvoiceNo\": 16892952,\n" +
            "          \"OrderID\": \"f25bd7e1-f790-4ea1-9629-f9bc7b055f6b\",\n" +
            "          \"DeliveryAddress\": \"1 Cheeseman Ave - East , Melbourne, VIC, 3001\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"Labels\": {\n" +
            "        \"TaxIncluded\": false,\n" +
            "        \"CreditCardAmount\": \"Total Credit Card\",\n" +
            "        \"TotalAmounExclVat\": \"Total excl GST:\",\n" +
            "        \"DeliveryAmount\": \"Delivery\",\n" +
            "        \"TotalAmount\": \"Total\",\n" +
            "        \"InternationalDeliveryAmount\": null,\n" +
            "        \"TaxAmount\": \"GST 10.00%:\",\n" +
            "        \"DiscountAmount\": \"Total Voucher\",\n" +
            "        \"ItemsAmount\": \"Subtotal\"\n" +
            "      },\n" +
            "      \"PaymentReferenceNo\": 16589978,\n" +
            "      \"Total\": {\n" +
            "        \"ItemsCount\": 5,\n" +
            "        \"CreditCardAmount\": 131,\n" +
            "        \"TotalAmounExclVat\": 131,\n" +
            "        \"DeliveryAmount\": 2,\n" +
            "        \"TotalAmount\": 131,\n" +
            "        \"InternationalDeliveryAmount\": 0,\n" +
            "        \"TaxAmount\": 0,\n" +
            "        \"DiscountAmount\": 0,\n" +
            "        \"ItemsAmount\": 129\n" +
            "      },\n" +
            "      \"PaymentGroupID\": 16589978\n" +
            "    },\n" +
            "    \"IsAuthenticated\": true\n" +
            "  }\n" +
            "}\n";

    @Inject
    public OrderDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadOrderDetails(String invoiceNo) {
//        GetOrderPaymentDetails.RequestValues requestValues = new GetOrderPaymentDetails.RequestValues(new GetOrderPaymentDetailsRequest(invoiceNo));
//        getCompositeDisposable().add(getDataManager()
//                .callGetOrderPaymentDetails(requestValues)
//                .subscribeOn(getSchedulerProvider().io())
//                .observeOn(getSchedulerProvider().ui())
//                .subscribe(new Consumer<GetOrderPaymentDetails.ResponseValue>() {
//                    @Override
//                    public void accept(@NonNull GetOrderPaymentDetails.ResponseValue responseValue) throws Exception {
//                        if (!isViewAttached()) {
//                            return;
//                        }
//
//                        if(responseValue.getOrderPaymentDetailsResponse().getNewReturnOrderDetailResponse().getResult()){
//                            getMvpView().showOrderDetails(responseValue.getOrderPaymentDetailsResponse());
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(@NonNull Throwable throwable) throws Exception {
//                        if (!isViewAttached()) {
//                            return;
//                        }
//
//                        getMvpView().hideLoading();
//                        getMvpView().onError(throwable.getMessage());
//
//                        // handle load accounts error here
//                        if (throwable instanceof ANError) {
//                            ANError anError = (ANError) throwable;
//                            handleApiError(anError);
//                        }
//                    }
//                })
//        );

//        MOCK API CALL
        GetOrderPaymentDetailsResponse response= new Gson().fromJson(mockCall,GetOrderPaymentDetailsResponse.class);
//        GetOrderPaymentDetailsResponse testOrderDetails = responseValue.getOrderPaymentDetailsResponse();



        getMvpView().showOrderDetails(response);
    }
}
