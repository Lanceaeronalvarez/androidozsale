package au.com.dealsdirect.checkout;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.checkout.GetCurrentOrder;
import au.com.dealsdirect.service.ourpay.Ourpay;
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

/**
 * Created by smartwave on 06/10/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPresenterTest {
    private final static String getCurrentOrderMockString = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"ScheduledPayment\": {\n" +
            "\t\t\t\"IsEmpty\": false\n" +
            "\t\t},\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    private final static String ourPayTestingMockValue = "{\n" +
            "  \"Summary\": {\n" +
            "    \"Subtotal\": 11,\n" +
            "    \"Delivery\": 9.95,\n" +
            "    \"Discount\": 0,\n" +
            "    \"Total\": 20.95,\n" +
            "    \"Tax\": 0\n" +
            "  },\n" +
            "  \"MyPayDetails\": {\n" +
            "        \"Enabled\": true,\n" +
            "        \"Message\": \"\",\n" +
            "        \"ReasonCode\": \"\",\n" +
            "        \"TermsAndConditions\": 2,\n" +
            "        \"PaymentConditions\": {\n" +
            "          \"MaxAmountThreshold\": 500,\n" +
            "          \"MinAmountThreshold\": 0\n" +
            "        },\n" +
            "        \"BillingAgreement\": {\n" +
            "          \"ID\": \"a2rjyz1hxjtc1mkzjm85oqg5k\",\n" +
            "          \"PlannedTransactions\": [\n" +
            "            {\n" +
            "              \"Number\": 1,\n" +
            "              \"Amount\": 5.24,\n" +
            "              \"PlannedDate\": \"/Date(1507507200000)/\",\n" +
            "              \"State\": 0\n" +
            "            },\n" +
            "            {\n" +
            "              \"Number\": 2,\n" +
            "              \"Amount\": 5.24,\n" +
            "              \"PlannedDate\": \"/Date(1508716800000)/\",\n" +
            "              \"State\": 0\n" +
            "            },\n" +
            "            {\n" +
            "              \"Number\": 3,\n" +
            "              \"Amount\": 5.24,\n" +
            "              \"PlannedDate\": \"/Date(1509926400000)/\",\n" +
            "              \"State\": 0\n" +
            "            },\n" +
            "            {\n" +
            "              \"Number\": 4,\n" +
            "              \"Amount\": 5.23,\n" +
            "              \"PlannedDate\": \"/Date(1511136000000)/\",\n" +
            "              \"State\": 0\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"Amount\": 5.24,\n" +
            "        \"BillingPeriod\": {\n" +
            "          \"Ticks\": 12096000000000,\n" +
            "          \"Days\": 14,\n" +
            "          \"Hours\": 0,\n" +
            "          \"Milliseconds\": 0,\n" +
            "          \"Minutes\": 0,\n" +
            "          \"Seconds\": 0,\n" +
            "          \"TotalDays\": 14,\n" +
            "          \"TotalHours\": 336,\n" +
            "          \"TotalMilliseconds\": 1209600000,\n" +
            "          \"TotalMinutes\": 20160,\n" +
            "          \"TotalSeconds\": 1209600\n" +
            "        },\n" +
            "        \"TransactionCount\": 4,\n" +
            "        \"PaymentSchemeDescription\": \"4 interest free payments over 6 weeks. <br/> We will dispatch your order as soon as possible.\",\n" +
            "        \"IsOurPayThreeDSecureRequired\": false\n" +
            "      }\n" +
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
        ArgumentCaptor<Ourpay> ourpayArgumentCaptor = ArgumentCaptor.forClass(Ourpay.class);

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

    @Test
    public void testGenerateOurPay(){
        Value getCurrentOrderValue = gson.fromJson(ourPayTestingMockValue,Value.class);

        mPresenter.generateOurpay(getCurrentOrderValue);

        ArgumentCaptor<Ourpay> ourpayArgumentCaptor = ArgumentCaptor.forClass(Ourpay.class);
        ArgumentCaptor<Value> valueArgumentCaptor = ArgumentCaptor.forClass(Value.class);

        verify(mMockCheckoutMvpView).showMyPayDetails(valueArgumentCaptor.capture(),ourpayArgumentCaptor.capture());

        Assert.assertEquals(getCurrentOrderValue.getMyPayDetails().getBillingAgreement().getID()
                ,valueArgumentCaptor.getValue().getMyPayDetails().getBillingAgreement().getID());
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
