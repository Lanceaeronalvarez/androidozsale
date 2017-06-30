package au.com.dealsdirect.ui.controller.returns.returnorders;
/*
 * Created by CodeineBot on 5/15/17.
 */


import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.ui.base.BasePresenter;
import au.com.dealsdirect.utils.rx.SchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;

public class ReturnOrdersPresenter<V extends ReturnOrdersMvpView> extends BasePresenter<V> implements ReturnOrdersMvpPresenter<V> {

    @Inject
    public ReturnOrdersPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void loadOrders() {

        String mockCall = "{\n" +
                "\t\"d\": {\n" +
                "\t\t\"IsAuthenticated\": true,\n" +
                "\t\t\"List\": [{\n" +
                "\t\t\t\"InvoiceNo\": 23883356,\n" +
                "\t\t\t\"OrderNumber\": 21655447,\n" +
                "\t\t\t\"InvoiceNoRef\": 23883356,\n" +
                "\t\t\t\"ItemsCount\": 2,\n" +
                "\t\t\t\"Total\": 178.0000,\n" +
                "\t\t\t\"Status\": \"Dispatched\",\n" +
                "\t\t\t\"ConsignmentNo\": \".\",\n" +
                "\t\t\t\"ReturnRequested\": false,\n" +
                "\t\t\t\"Description\": \"Bolle \\u0026 Serengeti Eyewear\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"InvoiceNo\": 23883357,\n" +
                "\t\t\t\"OrderNumber\": 21655448,\n" +
                "\t\t\t\"InvoiceNoRef\": 23883357,\n" +
                "\t\t\t\"ItemsCount\": 1,\n" +
                "\t\t\t\"Total\": 49.2500,\n" +
                "\t\t\t\"Status\": \"Dispatched\",\n" +
                "\t\t\t\"ConsignmentNo\": \"3432432432\",\n" +
                "\t\t\t\"ReturnRequested\": false,\n" +
                "\t\t\t\"Description\": \"Fendi Frames \\u0026 Sunglasses\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"InvoiceNo\": 23883359,\n" +
                "\t\t\t\"OrderNumber\": 21655450,\n" +
                "\t\t\t\"InvoiceNoRef\": 23883359,\n" +
                "\t\t\t\"ItemsCount\": 3,\n" +
                "\t\t\t\"Total\": 147.2500,\n" +
                "\t\t\t\"Status\": \"Dispatched\",\n" +
                "\t\t\t\"ConsignmentNo\": \".\",\n" +
                "\t\t\t\"ReturnRequested\": false,\n" +
                "\t\t\t\"Description\": \"Fendi Frames \\u0026 Sunglasses\"\n" +
                "\t\t}],\n" +
                "\t\t\"Result\": true,\n" +
                "\t\t\"Message\": \"\"\n" +
                "\t}\n" +
                "}";

        GetReturnOrders responseValue = new Gson().fromJson(mockCall,GetReturnOrders.class);
        List<au.com.dealsdirect.data.network.model.returns.returnorders.List> testOrders
                = responseValue.getGetReturnOrdersBody().getList();
        getMvpView().showOrders(testOrders);

    }
}
