package au.com.dealsdirect;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutMvpView;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutPresenter;
import au.com.dealsdirect.data.network.model.checkout.getcurrentorder.Value;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by smartwave on 06/10/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPresenterTest {
    private final static String getCurrentOrderMockString = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"Value\": {\n" +
            "\t\t\t\"IsEmpty\": false\n" +
            "\t\t},\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    @Mock
    CheckoutMvpView mMockCheckoutMvpView;
    @Mock
    DataManager mMockDataManager;

    private CheckoutMvpPresenter<CheckoutMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new CheckoutPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockCheckoutMvpView);
    }

    @Test
    public void testfetchCartDetailsNotEmptyResponse(){
        GetCurrentOrder.ResponseValue response = gson.fromJson(getCurrentOrderMockString,GetCurrentOrder.ResponseValue.class);

        doReturn(Observable.just(response))
                .when(mMockDataManager).callGetCurrentOrder(any(GetCurrentOrder.RequestValue.class));

        mPresenter.fetchCartDetails();

        mTestScheduler.triggerActions();

        ArgumentCaptor<Value> argument = ArgumentCaptor.forClass(Value.class);

        verify(mMockCheckoutMvpView).hideLoading();
        verify(mMockCheckoutMvpView).storeCartDetails(argument.capture());
        verify(mMockCheckoutMvpView).showCartDetails(argument.getValue().getItems());
        verify(mMockCheckoutMvpView).showAddressDetails(argument.getValue().getDeliveryAddress(),argument.getValue().getDecorationInfoList());
        verify(mMockCheckoutMvpView).showVoucherDetails(argument.getValue().getVouchers());
        verify(mMockCheckoutMvpView).showSummaryDetails(argument.getValue().getSummary());
    }

    @Test
    public void testFetchCartDetailsErrorOccured(){
        String errMsg = "error";

        doReturn(Observable.error(new Exception(errMsg)))
                .when(mMockDataManager).callGetCurrentOrder(any(GetCurrentOrder.RequestValue.class));

        mPresenter.fetchCartDetails();
        mTestScheduler.triggerActions();

        verify(mMockCheckoutMvpView).onError(errMsg);

    }


    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
