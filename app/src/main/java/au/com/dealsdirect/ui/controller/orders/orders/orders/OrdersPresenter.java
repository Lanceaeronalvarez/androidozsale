package au.com.dealsdirect.ui.controller.orders.orders.orders;

import com.google.gson.Gson;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by smartwave on 22/06/2017.
 */

public class OrdersPresenter<V extends OrdersMvpView> extends BasePresenter<V> implements OrdersMvpPresenter<V> {
    String mockCall ="{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [{\n" +
            "\t\t\t\"PaymentGroupID\": \"96929ade69aa4b518d49e90c42f592c9\",\n" +
            "\t\t\t\"PaymentReferenceNo\": 24288004,\n" +
            "\t\t\t\"Orders\": [{\n" +
            "\t\t\t\t\"OrderID\": \"96929ade-69aa-4b51-8d49-e90c42f592c9\",\n" +
            "\t\t\t\t\"InvoiceNo\": 27699910,\n" +
            "\t\t\t\t\"Status\": \"Dispatched\",\n" +
            "\t\t\t\t\"Description\": \"Cesare Paciotti\",\n" +
            "\t\t\t\t\"ConsignmentNo\": \"\",\n" +
            "\t\t\t\t\"Link\": \"\",\n" +
            "\t\t\t\t\"EstimatedDeliveryText\": \"Estimated Delivery: 15/11/17 - 20/11/17\",\n" +
            "\t\t\t\t\"SubTotal\": {\n" +
            "\t\t\t\t\t\"ItemsCount\": 2,\n" +
            "\t\t\t\t\t\"ItemsAmount\": 20.00,\n" +
            "\t\t\t\t\t\"DeliveryAmount\": 0.00\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Tracker\": {\n" +
            "\t\t\t\t\t\"Step\": 3,\n" +
            "\t\t\t\t\t\"ApprovedDate\": \"\\/Date(1509942688160)\\/\",\n" +
            "\t\t\t\t\t\"StockDate\": \"\\/Date(1509943116517)\\/\",\n" +
            "\t\t\t\t\t\"DispatchedDate\": \"\\/Date(1509943261763)\\/\",\n" +
            "\t\t\t\t\t\"ClosedDate\": \"\\/Date(1510029661763)\\/\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Total\": {\n" +
            "\t\t\t\t\"CreditCardAmount\": 20.00,\n" +
            "\t\t\t\t\"DiscountAmount\": 0.00,\n" +
            "\t\t\t\t\"ItemsCount\": 2,\n" +
            "\t\t\t\t\"ItemsAmount\": 20.00,\n" +
            "\t\t\t\t\"DeliveryAmount\": 0.00,\n" +
            "\t\t\t\t\"TotalAmount\": 20.00\n" +
            "\t\t\t}\n" +
            "\t\t}, {\n" +
            "\t\t\t\"PaymentGroupID\": \"042db62195bb4a31830c2fd5a5892b94\",\n" +
            "\t\t\t\"PaymentReferenceNo\": 24288003,\n" +
            "\t\t\t\"Orders\": [{\n" +
            "\t\t\t\t\"OrderID\": \"042db621-95bb-4a31-830c-2fd5a5892b94\",\n" +
            "\t\t\t\t\"InvoiceNo\": 27699909,\n" +
            "\t\t\t\t\"Status\": \"Dispatched\",\n" +
            "\t\t\t\t\"Description\": \"Cesare Paciotti\",\n" +
            "\t\t\t\t\"ConsignmentNo\": \"\",\n" +
            "\t\t\t\t\"Link\": \"\",\n" +
            "\t\t\t\t\"EstimatedDeliveryText\": \"Estimated Delivery: 15/11/17 - 20/11/17\",\n" +
            "\t\t\t\t\"SubTotal\": {\n" +
            "\t\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\t\"ItemsAmount\": 10.00,\n" +
            "\t\t\t\t\t\"DeliveryAmount\": 9.95\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Tracker\": {\n" +
            "\t\t\t\t\t\"Step\": 3,\n" +
            "\t\t\t\t\t\"ApprovedDate\": \"\\/Date(1509942507723)\\/\",\n" +
            "\t\t\t\t\t\"StockDate\": \"\\/Date(1509943322903)\\/\",\n" +
            "\t\t\t\t\t\"DispatchedDate\": \"\\/Date(1509943356043)\\/\",\n" +
            "\t\t\t\t\t\"ClosedDate\": \"\\/Date(1510029756043)\\/\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Total\": {\n" +
            "\t\t\t\t\"CreditCardAmount\": 19.95,\n" +
            "\t\t\t\t\"DiscountAmount\": 0.00,\n" +
            "\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\"ItemsAmount\": 10.00,\n" +
            "\t\t\t\t\"DeliveryAmount\": 9.95,\n" +
            "\t\t\t\t\"TotalAmount\": 19.95\n" +
            "\t\t\t}\n" +
            "\t\t}, {\n" +
            "\t\t\t\"PaymentGroupID\": \"2585ec5622054623b07b0d1c8145d1e2\",\n" +
            "\t\t\t\"PaymentReferenceNo\": 24283033,\n" +
            "\t\t\t\"Orders\": [{\n" +
            "\t\t\t\t\"OrderID\": \"2585ec56-2205-4623-b07b-0d1c8145d1e2\",\n" +
            "\t\t\t\t\"InvoiceNo\": 27694785,\n" +
            "\t\t\t\t\"Status\": \"Dispatched\",\n" +
            "\t\t\t\t\"Description\": \"Skins\",\n" +
            "\t\t\t\t\"ConsignmentNo\": \"\",\n" +
            "\t\t\t\t\"Link\": \"\",\n" +
            "\t\t\t\t\"EstimatedDeliveryText\": \"Estimated Delivery: 02/11/17 - 06/11/17\",\n" +
            "\t\t\t\t\"SubTotal\": {\n" +
            "\t\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\t\"ItemsAmount\": 49.00,\n" +
            "\t\t\t\t\t\"DeliveryAmount\": 0.00\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Tracker\": {\n" +
            "\t\t\t\t\t\"Step\": 3,\n" +
            "\t\t\t\t\t\"ApprovedDate\": \"\\/Date(1509342359920)\\/\",\n" +
            "\t\t\t\t\t\"StockDate\": \"\\/Date(1509943373640)\\/\",\n" +
            "\t\t\t\t\t\"DispatchedDate\": \"\\/Date(1509943395277)\\/\",\n" +
            "\t\t\t\t\t\"ClosedDate\": \"\\/Date(1510029795277)\\/\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Total\": {\n" +
            "\t\t\t\t\"CreditCardAmount\": 49.00,\n" +
            "\t\t\t\t\"DiscountAmount\": 0.00,\n" +
            "\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\"ItemsAmount\": 49.00,\n" +
            "\t\t\t\t\"DeliveryAmount\": 0.00,\n" +
            "\t\t\t\t\"TotalAmount\": 49.00\n" +
            "\t\t\t}\n" +
            "\t\t}, {\n" +
            "\t\t\t\"PaymentGroupID\": \"1889ca3453674405b187fcbc1b0674bd\",\n" +
            "\t\t\t\"PaymentReferenceNo\": 24283032,\n" +
            "\t\t\t\"Orders\": [{\n" +
            "\t\t\t\t\"OrderID\": \"1889ca34-5367-4405-b187-fcbc1b0674bd\",\n" +
            "\t\t\t\t\"InvoiceNo\": 27694784,\n" +
            "\t\t\t\t\"Status\": \"Approved\",\n" +
            "\t\t\t\t\"Description\": \"SpaRoom Diffusers, Oils \\u0026 More\",\n" +
            "\t\t\t\t\"ConsignmentNo\": null,\n" +
            "\t\t\t\t\"Link\": \"\",\n" +
            "\t\t\t\t\"EstimatedDeliveryText\": \"Estimated Delivery: 21/11/17 - 26/11/17\",\n" +
            "\t\t\t\t\"SubTotal\": {\n" +
            "\t\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\t\"ItemsAmount\": 15.00,\n" +
            "\t\t\t\t\t\"DeliveryAmount\": 9.95\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Tracker\": {\n" +
            "\t\t\t\t\t\"Step\": 1,\n" +
            "\t\t\t\t\t\"ApprovedDate\": \"\\/Date(1509342067133)\\/\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Total\": {\n" +
            "\t\t\t\t\"CreditCardAmount\": 24.95,\n" +
            "\t\t\t\t\"DiscountAmount\": 0.00,\n" +
            "\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\"ItemsAmount\": 15.00,\n" +
            "\t\t\t\t\"DeliveryAmount\": 9.95,\n" +
            "\t\t\t\t\"TotalAmount\": 24.95\n" +
            "\t\t\t}\n" +
            "\t\t}],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";
    @Inject
    public OrdersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadOrders() {

        Gson g = new Gson();
        GetPaymentsList.ResponseValue mockResponse = g.fromJson(mockCall, GetPaymentsList.ResponseValue.class);
        getMvpView().showOrders(mockResponse.getD().getList());
//
//        doApiCallForResponse(getDataManager()
//                .callGetPaymentsList(new GetPaymentsList.RequestValues()), new AppApiCallback(){
//            @Override
//            public void onSuccess(Object response) {
//                super.onSuccess(response);
//                if(((GetPaymentsList.ResponseValue) response).getD().getResult()){
//                    getMvpView().showOrders(((GetPaymentsList.ResponseValue) response).getD().getList());
//                }
//            }
//        });
    }
}
