package au.com.dealsdirect.ui.controller.orders.orderdetails;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.AppApiCallback;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrderDetailsPresenter<V extends OrderDetailsMvpView> extends BasePresenter<V> implements OrderDetailsMvpPresenter<V> {
//
//    String mockCall = "{\n" +
//            "\t\"d\": {\n" +
//            "\t\t\"IsAuthenticated\": true,\n" +
//            "\t\t\"Value\": {\n" +
//            "\t\t\t\"PaymentGroupID\": 24288004,\n" +
//            "\t\t\t\"PaymentReferenceNo\": 24288004,\n" +
//            "\t\t\t\"ApprovedDate\": \"\\/Date(1509942688160)\\/\",\n" +
//            "\t\t\t\"Orders\": [{\n" +
//            "\t\t\t\t\"OrderID\": \"96929ade-69aa-4b51-8d49-e90c42f592c9\",\n" +
//            "\t\t\t\t\"InvoiceNo\": 27699910,\n" +
//            "\t\t\t\t\"Description\": \"Cesare Paciotti\",\n" +
//            "\t\t\t\t\"Customer\": \"\",\n" +
//            "\t\t\t\t\"DeliveryAddress\": \"aeofiaj, ioajefoij, NSW, 1131\",\n" +
//            "\t\t\t\t\"Items\": [{\n" +
//            "\t\t\t\t\t\"ID\": \"0aa089e3-99cb-4d29-be69-3b76957cca90\",\n" +
//            "\t\t\t\t\t\"ItemID\": \"e2cb8214-d561-4abc-8ee0-189b7da40ec0\",\n" +
//            "\t\t\t\t\t\"Item\": \"Herald Round Neck Cotton Crest Logo T-Shirt In Black\",\n" +
//            "\t\t\t\t\t\"Size\": \"XX-Large\",\n" +
//            "\t\t\t\t\t\"Qty\": 1,\n" +
//            "\t\t\t\t\t\"Price\": 10.0000,\n" +
//            "\t\t\t\t\t\"SubTotal\": {\n" +
//            "\t\t\t\t\t\t\"ItemsAmount\": 10.0000,\n" +
//            "\t\t\t\t\t\t\"ItemsCount\": 1\n" +
//            "\t\t\t\t\t},\n" +
//            "\t\t\t\t\t\"BrandID\": \"ec0be7bb-1877-4924-a559-1593a6c322f3\",\n" +
//            "\t\t\t\t\t\"ImageID\": \"14ec253d-bd28-4b80-a615-a389d60322d6\",\n" +
//            "\t\t\t\t\t\"FileName\": \"76f273de-4103-4497-a84b-f54d0a8902fb_50x50.JPG\"\n" +
//            "\t\t\t\t}, {\n" +
//            "\t\t\t\t\t\"ID\": \"58d71bf0-c84c-4bd1-a946-fda82dd63afc\",\n" +
//            "\t\t\t\t\t\"ItemID\": \"a2214758-e475-46c8-b935-cc20b84ce716\",\n" +
//            "\t\t\t\t\t\"Item\": \"Deep Neck Cotton Plain T-Shirt In Grey Melange\",\n" +
//            "\t\t\t\t\t\"Size\": \"X-Large\",\n" +
//            "\t\t\t\t\t\"Qty\": 1,\n" +
//            "\t\t\t\t\t\"Price\": 10.0000,\n" +
//            "\t\t\t\t\t\"SubTotal\": {\n" +
//            "\t\t\t\t\t\t\"ItemsAmount\": 10.0000,\n" +
//            "\t\t\t\t\t\t\"ItemsCount\": 1\n" +
//            "\t\t\t\t\t},\n" +
//            "\t\t\t\t\t\"BrandID\": \"ec0be7bb-1877-4924-a559-1593a6c322f3\",\n" +
//            "\t\t\t\t\t\"ImageID\": \"6652a1ba-bef8-4f44-94d7-33809d60ae13\",\n" +
//            "\t\t\t\t\t\"FileName\": \"2e0d6872-5284-4bda-892c-77ca0f38094b_50x50.JPG\"\n" +
//            "\t\t\t\t}],\n" +
//            "\t\t\t\t\"RefundItems\": null\n" +
//            "\t\t\t}],\n" +
//            "\t\t\t\"Total\": {\n" +
//            "\t\t\t\t\"CreditCardAmount\": 20.00,\n" +
//            "\t\t\t\t\"DiscountAmount\": 0.00,\n" +
//            "\t\t\t\t\"ItemsCount\": 2,\n" +
//            "\t\t\t\t\"ItemsAmount\": 20.00,\n" +
//            "\t\t\t\t\"DeliveryAmount\": 0.00,\n" +
//            "\t\t\t\t\"InternationalDeliveryAmount\": 0,\n" +
//            "\t\t\t\t\"TotalAmount\": 20.00,\n" +
//            "\t\t\t\t\"TotalAmounExclVat\": 20.00,\n" +
//            "\t\t\t\t\"TaxAmount\": 0.00\n" +
//            "\t\t\t},\n" +
//            "\t\t\t\"Labels\": {\n" +
//            "\t\t\t\t\"CreditCardAmount\": \"Total Credit Card\",\n" +
//            "\t\t\t\t\"DiscountAmount\": \"Total Voucher\",\n" +
//            "\t\t\t\t\"ItemsAmount\": \"Subtotal\",\n" +
//            "\t\t\t\t\"DeliveryAmount\": \"Delivery\",\n" +
//            "\t\t\t\t\"InternationalDeliveryAmount\": null,\n" +
//            "\t\t\t\t\"TotalAmount\": \"Total\",\n" +
//            "\t\t\t\t\"TotalAmounExclVat\": \"Total excl GST:\",\n" +
//            "\t\t\t\t\"TaxAmount\": \"GST 10.00%:\",\n" +
//            "\t\t\t\t\"TaxIncluded\": false\n" +
//            "\t\t\t}\n" +
//            "\t\t},\n" +
//            "\t\t\"Result\": true,\n" +
//            "\t\t\"Message\": \"\"\n" +
//            "\t}\n" +
//            "}";

    @Inject
    public OrderDetailsPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadOrderDetails(GetOrderPaymentDetails.RequestValues requestValues) {
        getMvpView().showLoading();

//        Gson g = new Gson();
//        GetOrderPaymentDetails.ResponseValue mockResponse = g.fromJson(mockCall, GetOrderPaymentDetails.ResponseValue.class);
//        getMvpView().showOrderDetails(mockResponse);

        doApiCallForResponse(getDataManager().callGetOrderPaymentDetails(requestValues), new AppApiCallback() {
            @Override
            public void onSuccess(Object response) {
                GetOrderPaymentDetails.ResponseValue responseValue = (GetOrderPaymentDetails.ResponseValue) response;

                if (responseValue.getD().getResult()) {
                    getMvpView().showOrderDetails(responseValue);
                }
            }
        });
    }
}
