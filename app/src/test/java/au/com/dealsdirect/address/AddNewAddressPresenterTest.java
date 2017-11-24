package au.com.dealsdirect.address;

import android.view.View;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.address.AddAddress;
import au.com.dealsdirect.data.network.model.address.DecorationInfoList;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressMvpPresenter;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressMvpView;
import au.com.dealsdirect.ui.controller.address.addnewaddress.AddNewAddressPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 09/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddNewAddressPresenterTest {

    private static final String setUserAddressMockResponseSuccess = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"ScheduledPayment\": \"09502262-fd7b-4580-a271-08bf291c29b0\",\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";
    private static final String setUserAddressMockResponseError = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"ScheduledPayment\": \"\",\n" +
            "\t\t\"Result\": false,\n" +
            "\t\t\"Message\": \"An error occured\"\n" +
            "\t}\n" +
            "}";

    @Mock
    AddNewAddressMvpView mMockAddNewAddressView;

    @Mock
    DataManager mMockDataManager;

    private AddNewAddressMvpPresenter<AddNewAddressMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new AddNewAddressPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockAddNewAddressView);
    }


    @Test
    public void testAddNewAddressSuccess(){

        HashMap<DecorationInfoList, View> mViewMap = new HashMap<>();

        AddAddress.ResponseValue response = gson.fromJson(setUserAddressMockResponseSuccess,AddAddress.ResponseValue.class);

        doReturn(Observable.just(response))
                .when(mMockDataManager).callSetUserDeliveryAddress(any(AddAddress.RequestValues.class));

        mPresenter.addNewAddress(mViewMap);
        mTestScheduler.triggerActions();

        verify(mMockAddNewAddressView).addNewAddressSuccessful();
    }

    @Test
    public void testAddNewAddressError(){

        HashMap<DecorationInfoList, View> mViewMap = new HashMap<>();

        AddAddress.ResponseValue response = gson.fromJson(setUserAddressMockResponseError,AddAddress.ResponseValue.class);

        doReturn(Observable.just(response))
                .when(mMockDataManager).callSetUserDeliveryAddress(any(AddAddress.RequestValues.class));

        mPresenter.addNewAddress(mViewMap);
        mTestScheduler.triggerActions();

        verify(mMockAddNewAddressView).showErrorMessage(response.d.getMessage());
        verify(mMockAddNewAddressView).onError(response.d.getMessage());
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
