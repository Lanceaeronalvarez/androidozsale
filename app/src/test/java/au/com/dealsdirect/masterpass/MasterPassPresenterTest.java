package au.com.dealsdirect.masterpass;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPaymentRequest;
import au.com.dealsdirect.data.network.model.masterpass.MasterPassPostTransactionRequest;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassMvpPresenter;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassMvpView;
import au.com.dealsdirect.ui.controller.masterpass.MasterpassPresenter;
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
public class MasterPassPresenterTest {

    private static final String mMockGetMasterpassPaymentResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "      \"PaymentURL\": \"http://testpayment.com\"\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockConfirmPaymentResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "      \"invoiceNo\" : \"12345678\",\n" +
            "      \"AddressString\" : \"test address\",\n" +
            "      \"EstimatedDeliveryText\" : \"01/01/2001\",\n" +
            "      \"OrderInfoResult\" : {\n" +
            "          \"Total\" : \"100.0\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    @Mock
    DataManager mMockDataManager;

    @Mock
    MasterpassMvpView mMockMasterPassView;

    MasterpassMvpPresenter<MasterpassMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new MasterpassPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockMasterPassView);
    }

    @Test
    public void testGetMasterpassPayment() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mMockGetMasterpassPaymentResponse);
        } catch (Exception e) {

        }

        doReturn(Observable.just(jsonObject))
                .when(mMockDataManager).callMasterpassPayment(any(MasterPassPaymentRequest.class));

        mPresenter.getMasterpassPayment();
        mTestScheduler.triggerActions();

        ArgumentCaptor<String> paymentUrlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> urlHostCaptor = ArgumentCaptor.forClass(String.class);

        verify(mMockMasterPassView).hideLoadingDialog();
        verify(mMockMasterPassView).loadMasterpassUrl(paymentUrlCaptor.capture(), urlHostCaptor.capture());

        Assert.assertEquals(paymentUrlCaptor.getValue(), "http://testpayment.com");
    }

    @Test
    public void testConfirmPayment() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mMockConfirmPaymentResponse);
        } catch (Exception e) {

        }

        doReturn(Observable.just(jsonObject))
                .when(mMockDataManager).callMasterpassPostTransaction(any(MasterPassPostTransactionRequest.class));

        mPresenter.confirmPayment("","","");
        mTestScheduler.triggerActions();

        verify(mMockMasterPassView).showPaymentSuccess("test address","100.0","12345678","01/01/2001");


    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
