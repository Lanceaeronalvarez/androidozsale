package au.com.dealsdirect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.address.AddAddress;
import au.com.dealsdirect.data.network.model.address.ApplyAddressRequest;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressMvpView;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ViewAddressPresenterTest {


    @Mock
    ViewAddressMvpView mvpView;

    @Mock
    DataManager mMockDataManager;

    private ViewAddressPresenter<ViewAddressMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider mTestSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new ViewAddressPresenter<>(mMockDataManager, mTestSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mvpView);
    }

    @Test
    public void loadAddressesTest() {
        GetAddresses.ResponseValue responseValue = new GetAddresses.ResponseValue();

        ArgumentCaptor<GetAddresses.RequestValues> requestCaptor = ArgumentCaptor.forClass(GetAddresses.RequestValues.class);
        doReturn(Observable.just(responseValue)).when(mMockDataManager).callGetUserAddresses(requestCaptor.capture());

        mPresenter.loadAddresses();
        mTestScheduler.triggerActions();

        verify(mvpView).showAddresses(responseValue);
    }

    @Test
    public void applyDeliveryAddress() {
        ApplyAddressResponse applyAddressResponse = new ApplyAddressResponse();
        ArgumentCaptor<ApplyAddressRequest> applyAddressCaptor =ArgumentCaptor.forClass(ApplyAddressRequest.class);
        doReturn(Observable.just(applyAddressResponse)).when(mMockDataManager).callApplyDeliveryAddress(applyAddressCaptor.capture());

        mPresenter.applyDeliveryAddress("");
        mTestScheduler.triggerActions();

    }


}
