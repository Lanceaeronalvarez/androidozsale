package au.com.dealsdirect.returns;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrders;
import au.com.dealsdirect.data.network.model.returns.returnorders.GetReturnOrdersBody;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersMvpView;
import au.com.dealsdirect.ui.controller.returns.returnorders.ReturnOrdersPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 06/10/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ReturnOrdersPresenterTest {
    @Mock
    DataManager dataManager;

    @Mock
    ReturnOrdersMvpView mvpView;

    String mockResponse = "{\n" +
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

    ReturnOrdersPresenter<ReturnOrdersMvpView> mPresenter;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);

        mPresenter = new ReturnOrdersPresenter<>(dataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mvpView);
    }

    @Test
    public void loadOrdersTest() {
        GetReturnOrders getReturnOrders = new GetReturnOrders();
        GetReturnOrdersBody getReturnOrdersBody = new Gson().fromJson(mockResponse, GetReturnOrdersBody.class);
        getReturnOrders.setD(getReturnOrdersBody);
        doReturn(Observable.just(getReturnOrders)).when(dataManager).callGetReturnOrders();

        mPresenter.loadOrders();
        testScheduler.triggerActions();

        verify(mvpView).showOrders(getReturnOrders.getGetReturnOrdersBody().getList());
    }

    @Test
    public void PresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }
}
