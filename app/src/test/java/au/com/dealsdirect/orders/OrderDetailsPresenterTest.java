package au.com.dealsdirect.orders;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsMvpView;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/6/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderDetailsPresenterTest {
    String mockResopnse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"List\": [{\n" +
            "\t\t\t\"PaymentGroupID\": \"47f84262618440d083b8a13515e758a7\",\n" +
            "\t\t\t\"PaymentReferenceNo\": 24051906,\n" +
            "\t\t\t\"Orders\": [{\n" +
            "\t\t\t\t\"OrderID\": \"47f84262-6184-40d0-83b8-a13515e758a7\",\n" +
            "\t\t\t\t\"InvoiceNo\": 27341735,\n" +
            "\t\t\t\t\"Status\": \"Approved\",\n" +
            "\t\t\t\t\"Description\": \"Bottoms Bonanza\",\n" +
            "\t\t\t\t\"ConsignmentNo\": null,\n" +
            "\t\t\t\t\"Link\": \"\",\n" +
            "\t\t\t\t\"EstimatedDeliveryText\": \"Estimated Delivery: 02/11/17 - 07/11/17\",\n" +
            "\t\t\t\t\"SubTotal\": {\n" +
            "\t\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\t\"ItemsAmount\": 19.99,\n" +
            "\t\t\t\t\t\"DeliveryAmount\": 9.95\n" +
            "\t\t\t\t},\n" +
            "\t\t\t\t\"Tracker\": {\n" +
            "\t\t\t\t\t\"Step\": 1,\n" +
            "\t\t\t\t\t\"ApprovedDate\": \"\\/Date(1507519086870)\\/\"\n" +
            "\t\t\t\t}\n" +
            "\t\t\t}],\n" +
            "\t\t\t\"Total\": {\n" +
            "\t\t\t\t\"CreditCardAmount\": 29.94,\n" +
            "\t\t\t\t\"DiscountAmount\": 0.00,\n" +
            "\t\t\t\t\"ItemsCount\": 1,\n" +
            "\t\t\t\t\"ItemsAmount\": 19.99,\n" +
            "\t\t\t\t\"DeliveryAmount\": 9.95,\n" +
            "\t\t\t\t\"TotalAmount\": 29.94\n" +
            "\t\t\t}\n" +
            "\t\t}],\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";
    @Mock
    DataManager dataManager;

    @Mock
    OrderDetailsMvpView mvpView;

    OrderDetailsPresenter<OrderDetailsMvpView> mPresenter;
    TestScheduler mTestScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new OrderDetailsPresenter(dataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mvpView);
    }

    @Test
    public void loadOrderDetailsTest() {
        GetOrderPaymentDetails.ResponseValue responseValue = new Gson().fromJson(mockResopnse, GetOrderPaymentDetails.ResponseValue.class);
        GetOrderPaymentDetails.RequestValues requestValues = new GetOrderPaymentDetails.RequestValues("0");
        doReturn(Observable.just(responseValue)).when(dataManager).callGetOrderDetails(requestValues);

        mPresenter.loadOrderDetails(requestValues);
        mTestScheduler.triggerActions();

        verify(mvpView).showLoading();
        verify(mvpView).showOrderDetails(responseValue);
    }

    @Test
    public void PresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }
}
