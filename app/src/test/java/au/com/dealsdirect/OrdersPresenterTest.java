package au.com.dealsdirect;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.data.network.model.orders.GetPaymentsList;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersMvpView;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;

/**
 * Created by Paul on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class OrdersPresenterTest {

    @Mock
    DataManager dataManager;

    @Mock
    OrdersMvpView ordersMvpView;

    TestScheduler mTestScheduler;
    OrdersPresenter<OrdersMvpView> mPresenter;

    String mockCall ="{\n" +
            "  \"d\": {\n" +
            "    \"List\": [\n" +
            "      {\n" +
            "        \"PaymentReferenceNo\": 16589978,\n" +
            "        \"Total\": {\n" +
            "          \"TotalAmount\": 131,\n" +
            "          \"ItemsCount\": 5,\n" +
            "          \"DiscountAmount\": 0,\n" +
            "          \"CreditCardAmount\": 131,\n" +
            "          \"DeliveryAmount\": 2,\n" +
            "          \"ItemsAmount\": 129\n" +
            "        },\n" +
            "        \"Orders\": [\n" +
            "          {\n" +
            "            \"Status\": \"Approved\",\n" +
            "            \"SubTotal\": {\n" +
            "              \"ItemsCount\": 3,\n" +
            "              \"DeliveryAmount\": 1,\n" +
            "              \"ItemsAmount\": 105\n" +
            "            },\n" +
            "            \"Description\": \"Baleno Kids Bedding\",\n" +
            "            \"Link\": \"\",\n" +
            "            \"InvoiceNo\": 16892953,\n" +
            "            \"Tracker\": {\n" +
            "              \"ApprovedDate\": \"\\/Date(1436336007240)\\/\",\n" +
            "              \"Step\": 1\n" +
            "            },\n" +
            "            \"ConsignmentNo\": null,\n" +
            "            \"OrderID\": \"bf4621f5-ce5b-4cd0-aa19-8c2809cbaafe\",\n" +
            "            \"EstimatedDeliveryText\": \"Estimated Delivery: 03\\/08\\/15 - 07\\/08\\/15\",\n" +
            "            \"isLastOrder\": false,\n" +
            "            \"PaymentReferenceNo\": \"16589978\",\n" +
            "            \"Total\": {\n" +
            "              \"TotalAmount\": 131,\n" +
            "              \"ItemsCount\": 5,\n" +
            "              \"DiscountAmount\": 0,\n" +
            "              \"CreditCardAmount\": 131,\n" +
            "              \"DeliveryAmount\": 2,\n" +
            "              \"ItemsAmount\": 129\n" +
            "            }\n" +
            "          },\n" +
            "          {\n" +
            "            \"Status\": \"Approved\",\n" +
            "            \"SubTotal\": {\n" +
            "              \"ItemsCount\": 2,\n" +
            "              \"DeliveryAmount\": 1,\n" +
            "              \"ItemsAmount\": 24\n" +
            "            },\n" +
            "            \"Description\": \"Trendy Tech\",\n" +
            "            \"Link\": \"\",\n" +
            "            \"InvoiceNo\": 16892952,\n" +
            "            \"Tracker\": {\n" +
            "              \"ApprovedDate\": \"\\/Date(1436336006470)\\/\",\n" +
            "              \"Step\": 1\n" +
            "            },\n" +
            "            \"ConsignmentNo\": null,\n" +
            "            \"OrderID\": \"f25bd7e1-f790-4ea1-9629-f9bc7b055f6b\",\n" +
            "            \"EstimatedDeliveryText\": \"Estimated Delivery: 27\\/07\\/15 - 01\\/08\\/15\",\n" +
            "            \"isLastOrder\": false,\n" +
            "            \"PaymentReferenceNo\": \"16589978\",\n" +
            "            \"Total\": {\n" +
            "              \"TotalAmount\": 131,\n" +
            "              \"ItemsCount\": 5,\n" +
            "              \"DiscountAmount\": 0,\n" +
            "              \"CreditCardAmount\": 131,\n" +
            "              \"DeliveryAmount\": 2,\n" +
            "              \"ItemsAmount\": 129\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"PaymentGroupID\": \"bf4621f5ce5b4cd0aa198c2809cbaafef25bd7e1f7904ea19629f9bc7b055f6b\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"PaymentReferenceNo\": 16589977,\n" +
            "        \"Total\": {\n" +
            "          \"TotalAmount\": 96.95,\n" +
            "          \"ItemsCount\": 3,\n" +
            "          \"DiscountAmount\": 0,\n" +
            "          \"CreditCardAmount\": 96.95,\n" +
            "          \"DeliveryAmount\": 9.95,\n" +
            "          \"ItemsAmount\": 87\n" +
            "        },\n" +
            "        \"Orders\": [\n" +
            "          {\n" +
            "            \"Status\": \"Dispatched\",\n" +
            "            \"SubTotal\": {\n" +
            "              \"ItemsCount\": 3,\n" +
            "              \"DeliveryAmount\": 9.95,\n" +
            "              \"ItemsAmount\": 87\n" +
            "            },\n" +
            "            \"Description\": \"O.P.I, Max factor & More\",\n" +
            "            \"Link\": \"\",\n" +
            "            \"InvoiceNo\": 16892951,\n" +
            "            \"Tracker\": {\n" +
            "              \"ApprovedDate\": \"\\/Date(1436334494867)\\/\",\n" +
            "              \"Step\": 3,\n" +
            "              \"DispatchedDate\": \"\\/Date(1436337898310)\\/\",\n" +
            "              \"ClosedDate\": \"\\/Date(1436424298310)\\/\",\n" +
            "              \"StockDate\": \"\\/Date(1436337891370)\\/\"\n" +
            "            },\n" +
            "            \"ConsignmentNo\": \"\",\n" +
            "            \"OrderID\": \"cce813fe-a411-439a-bc3c-e8f9122a4191\",\n" +
            "            \"EstimatedDeliveryText\": \"Estimated Delivery: 30\\/07\\/15 - 04\\/08\\/15\",\n" +
            "            \"isLastOrder\": false,\n" +
            "            \"PaymentReferenceNo\": \"16589977\",\n" +
            "            \"Total\": {\n" +
            "              \"TotalAmount\": 96.95,\n" +
            "              \"ItemsCount\": 3,\n" +
            "              \"DiscountAmount\": 0,\n" +
            "              \"CreditCardAmount\": 96.95,\n" +
            "              \"DeliveryAmount\": 9.95,\n" +
            "              \"ItemsAmount\": 87\n" +
            "            }\n" +
            "          }\n" +
            "        ],\n" +
            "        \"PaymentGroupID\": \"cce813fea411439abc3ce8f9122a4191\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"Message\": \"\",\n" +
            "    \"Result\": true,\n" +
            "    \"IsAuthenticated\": true\n" +
            "  }\n" +
            "}";

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider =
                new TestSchedulerProvider(mTestScheduler);
        mPresenter = new OrdersPresenter<>(dataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(ordersMvpView);
    }

    @Test
    public void applyDeliveryAddress() {
        GetPaymentsList.ResponseValue responseValue = new Gson().fromJson(mockCall, GetPaymentsList.ResponseValue.class);
        ArgumentCaptor<GetPaymentsList.RequestValues> requestCaptor = ArgumentCaptor.forClass(GetPaymentsList.RequestValues.class);
        doReturn(Observable.just(responseValue)).when(dataManager).callGetPaymentsList(requestCaptor.capture());

        mPresenter.loadOrders();
        mTestScheduler.triggerActions();

        ordersMvpView.showOrders(responseValue.getD().getList());
    }
}
