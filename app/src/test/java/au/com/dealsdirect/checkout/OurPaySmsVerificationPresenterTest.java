package au.com.dealsdirect.checkout;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.verificationcodeconfirm.VerificationCodeConfirmRequest;
import au.com.dealsdirect.data.network.model.verificationcodeconfirm.VerificationCodeConfirmResponseBody;
import au.com.dealsdirect.data.network.model.verificationnormalizephone.VerificationNormalizePhoneRequest;
import au.com.dealsdirect.data.network.model.verificationnormalizephone.VerificationNormalizePhoneResponseBody;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationMvpPresenter;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationMvpView;
import au.com.dealsdirect.ui.controller.checkout.ourpay.OurpaySMSVerificationPresenter;
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
public class OurPaySmsVerificationPresenterTest {

    @Mock
    OurpaySMSVerificationMvpView mMockOurPaySmsView;
    @Mock
    DataManager mMockDataManager;

    private OurpaySMSVerificationMvpPresenter<OurpaySMSVerificationMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new OurpaySMSVerificationPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockOurPaySmsView);
    }

    @Test
    public void testCallNormalizePhone(){
        VerificationNormalizePhoneResponseBody responseBody = new VerificationNormalizePhoneResponseBody();
        doReturn(Observable.just(responseBody))
                .when(mMockDataManager).callNormalizePhone(any(VerificationNormalizePhoneRequest.class));

        mPresenter.callNormalizePhone("");
        mTestScheduler.triggerActions();

        verify(mMockOurPaySmsView).callNormalizePhoneResponse(responseBody);
    }

    @Test
    public void testCallVerificationCodeConfirm(){
        VerificationCodeConfirmResponseBody responseBody = new VerificationCodeConfirmResponseBody();

        ArgumentCaptor<VerificationCodeConfirmRequest> requestArgumentCaptor = ArgumentCaptor.forClass(VerificationCodeConfirmRequest.class);


        doReturn(Observable.just(responseBody))
                .when(mMockDataManager).callVerificationCodeConfirm(requestArgumentCaptor.capture());

        mPresenter.callVerificationCodeConfirm("9351230176","63","5678");
        mTestScheduler.triggerActions();

        verify(mMockOurPaySmsView).callVerificationCodeConfirmResponse(responseBody);
        Assert.assertEquals(requestArgumentCaptor.getValue().getCode(),"5678");
        Assert.assertEquals(requestArgumentCaptor.getValue().getPhone(),"639351230176");

    }

    @Test
    public void testCallVerificationCodeSend(){
        VerificationNormalizePhoneResponseBody responseBody = new VerificationNormalizePhoneResponseBody();

        ArgumentCaptor<VerificationNormalizePhoneRequest> requestArgumentCaptor = ArgumentCaptor.forClass(VerificationNormalizePhoneRequest.class);


        doReturn(Observable.just(responseBody))
                .when(mMockDataManager).callVerificationCodeSend(requestArgumentCaptor.capture());

        mPresenter.callVerificationCodeSend("5678","9351230176","63");
        mTestScheduler.triggerActions();

        verify(mMockOurPaySmsView).callVerificationCodeSendResponse(responseBody);
        Assert.assertEquals(requestArgumentCaptor.getValue().getCode(),"5678");
        Assert.assertEquals(requestArgumentCaptor.getValue().getPhone(),"+639351230176");

    }



    @After
    public void tearDown() throws Exception{

    }

}
