package au.com.dealsdirect.checkout;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.GetUserPaymentMethods;
import au.com.dealsdirect.data.network.model.checkout.RemoveUserPaymentMethod;
import au.com.dealsdirect.data.network.model.checkout.getuserpaymentmethods.PaymentMethod;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectMvpView;
import au.com.dealsdirect.ui.controller.checkout.paymentselect.PaymentSelectPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 10/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentSelectPresenterTest {

    private static final String mMockUserPaymentMethodResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "      \"PaymentMethods\": [\n" +
            "        {\n" +
            "          \"PaymentType\": \"Visa\",\n" +
            "          \"Description\": \"400000******0002\",\n" +
            "          \"Token\": \"fv5wqwr\",\n" +
            "          \"ImageUrl\": \"https://assets.braintreegateway.com/payment_method_logo/visa.png?environment=production\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"LastPaidToken\": \"fv5wqwr\"\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetUserPaymentMethodResponseFail = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "      \"PaymentMethods\": []\n" +
            "    },\n" +
            "    \"Result\": false,\n" +
            "    \"Message\": \"error\"\n" +
            "  }\n" +
            "}";

    private static final String mMockRemoveUserPaymentMethodResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"__type\": \"OzSale.PublicAPI.ApiResult\",\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": null\n" +
            "  }\n" +
            "}";

    @Mock
    PaymentSelectMvpView mMockPaymentSelectView;
    @Mock
    DataManager mMockDataManager;

    private PaymentSelectMvpPresenter<PaymentSelectMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new PaymentSelectPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockPaymentSelectView);
    }

    @Test
    public void testFetchUserPaymentMethodsSuccess(){
        GetUserPaymentMethods.ResponseValue response = gson.fromJson(mMockUserPaymentMethodResponse, GetUserPaymentMethods.ResponseValue.class);

        doReturn(Observable.just(response))
                .when(mMockDataManager).callGetUserPaymentMethods(any(GetUserPaymentMethods.RequestValue.class));

        mPresenter.fetchUserPaymentMethods();
        mTestScheduler.triggerActions();

        verify(mMockPaymentSelectView).showPaymentList(response.getUserPaymentMethods());
    }

    @Test
    public void testFetchUserPaymentMethodsFail(){
        GetUserPaymentMethods.ResponseValue response = gson.fromJson(mMockGetUserPaymentMethodResponseFail, GetUserPaymentMethods.ResponseValue.class);

        doReturn(Observable.just(response))
                .when(mMockDataManager).callGetUserPaymentMethods(any(GetUserPaymentMethods.RequestValue.class));

        mPresenter.fetchUserPaymentMethods();
        mTestScheduler.triggerActions();

        verify(mMockPaymentSelectView).onError(response.getD().getMessage());

        Assert.assertEquals(response.getD().getMessage(),"error");
    }

    @Test
    public void testRemoveUserPaymentMethod(){
        RemoveUserPaymentMethod.ResponseValue response = gson.fromJson(mMockRemoveUserPaymentMethodResponse, RemoveUserPaymentMethod.ResponseValue.class);

        doReturn(Observable.just(response))
                .when(mMockDataManager).callRemoveUserPaymentMethod(any(RemoveUserPaymentMethod.RequestValue.class));

        PaymentMethod paymentMethod = new PaymentMethod();

        mPresenter.removeUserPaymentMethod(paymentMethod);
        mTestScheduler.triggerActions();

        verify(mMockPaymentSelectView).showRemovePaymentMethodResult(paymentMethod,response.getResult(),response.getMessage());
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
