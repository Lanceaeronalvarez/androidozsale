package au.com.dealsdirect.address;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.address.ApplyAddressRequest;
import au.com.dealsdirect.data.network.model.address.ApplyAddressResponse;
import au.com.dealsdirect.data.network.model.address.DeleteUserAddress;
import au.com.dealsdirect.data.network.model.address.GetAddresses;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressMvpView;
import au.com.dealsdirect.ui.controller.address.viewaddress.ViewAddressPresenter;
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
public class ViewAddressPresenterTest {

    private static final String mMockDefaultResponseSuccess = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPayment\": {\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetUserAddressesResponseFailure = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPayment\": {\n" +
            "    },\n" +
            "    \"Result\": false,\n" +
            "    \"Message\": \"error\"\n" +
            "  }\n" +
            "}";

    @Mock
    ViewAddressMvpView mvpView;

    @Mock
    DataManager mMockDataManager;

    private ViewAddressPresenter<ViewAddressMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider mTestSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new ViewAddressPresenter<>(mMockDataManager, mTestSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mvpView);

        doReturn("").when(mMockDataManager).getLanguageId();
    }

    @Test
    public void loadAddressesTestSuccess() {
        GetAddresses.ResponseValue responseValue = gson.fromJson(mMockDefaultResponseSuccess, GetAddresses.ResponseValue.class);

        ArgumentCaptor<GetAddresses.RequestValues> requestCaptor = ArgumentCaptor.forClass(GetAddresses.RequestValues.class);
        doReturn(Observable.just(responseValue)).when(mMockDataManager).callGetUserAddresses(requestCaptor.capture());

        mPresenter.loadAddresses();
        mTestScheduler.triggerActions();

        verify(mvpView).showAddresses(responseValue);
    }

    @Test
    public void loadAddressesTestFailure() {
        GetAddresses.ResponseValue responseValue = gson.fromJson(mMockGetUserAddressesResponseFailure, GetAddresses.ResponseValue.class);

        ArgumentCaptor<GetAddresses.RequestValues> requestCaptor = ArgumentCaptor.forClass(GetAddresses.RequestValues.class);
        doReturn(Observable.just(responseValue)).when(mMockDataManager).callGetUserAddresses(requestCaptor.capture());

        mPresenter.loadAddresses();
        mTestScheduler.triggerActions();

        verify(mvpView).onError(responseValue.getD().getMessage());
    }

    @Test
    public void testApplyDeliveryAddress() {
        ApplyAddressResponse applyAddressResponse = gson.fromJson(mMockDefaultResponseSuccess,ApplyAddressResponse.class);

        doReturn(Observable.just(applyAddressResponse)).when(mMockDataManager).callApplyDeliveryAddress(any(ApplyAddressRequest.class));

        mPresenter.applyDeliveryAddress("");
        mTestScheduler.triggerActions();

        verify(mvpView).backToCheckout();

    }

    @Test
    public void testDeleteUserAddress() {
        DeleteUserAddress.ResponseValue responseValue = gson.fromJson(mMockDefaultResponseSuccess,DeleteUserAddress.ResponseValue.class);

        doReturn(Observable.just(responseValue)).when(mMockDataManager).callDeleteUserDeliveryAddress(any(DeleteUserAddress.RequestValues.class));

        mPresenter.deleteUserDeliveryAddress("");
        mTestScheduler.triggerActions();

        verify(mvpView).onUserDeliveryAddressDeleted(responseValue);

    }




}
