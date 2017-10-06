package au.com.dealsdirect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.orders.GetOrderPaymentDetails;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsMvpView;
import au.com.dealsdirect.ui.controller.orders.orderdetails.OrderDetailsPresenter;
import au.com.dealsdirect.ui.controller.orders.orders.OrdersMvpPresenter;
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
public class OrderDetailsPresenterTest {
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
        GetOrderPaymentDetails.ResponseValue responseValue = new GetOrderPaymentDetails.ResponseValue();
        ArgumentCaptor<GetOrderPaymentDetails.RequestValues> requestCaptor = ArgumentCaptor.forClass(GetOrderPaymentDetails.RequestValues.class);

        doReturn(Observable.just(responseValue)).when(dataManager).callGetOrderPaymentDetails(requestCaptor.capture());

        mPresenter.loadOrderDetails("");
        mTestScheduler.triggerActions();

        mvpView.showOrderDetails(responseValue);
    }
}
