package au.com.dealsdirect;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessMvpView;
import au.com.dealsdirect.ui.controller.checkout.paymentsuccess.PaymentSuccessPresenter;
import au.com.dealsdirect.ui.controller.language.LanguageMvpView;
import au.com.dealsdirect.ui.controller.language.LanguagePresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * dp Created by Admin on 10/10/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class PaymentSuccessPresenterTest {

    @Mock
    PaymentSuccessMvpView mMockPaymentSuccessMvpView;

    @Mock
    DataManager mMockDataManager;

    private PaymentSuccessPresenter<PaymentSuccessMvpView> mPresenter;
    private TestScheduler mTestScheduler;


    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();


        mPresenter = new PaymentSuccessPresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockPaymentSuccessMvpView);
    }

    @Test
    public void dontShowRatePopUpFromFirstIncrement(){

        doReturn(0).when(mMockDataManager).getPaymentCount();
        mPresenter.incrementPayCount();
        mTestScheduler.triggerActions();
        verifyZeroInteractions(mMockPaymentSuccessMvpView);
    }


    @Test
    public void showRatePopUpAtSecondIncrement(){

        doReturn(1).when(mMockDataManager).getPaymentCount();
        mPresenter.incrementPayCount();
        mTestScheduler.triggerActions();
        verify(mMockPaymentSuccessMvpView).showRatePopUp();

    }

    @Test
    public void generateOurpayWhenMyPayEnabledTest(){

        doReturn(true).when(mMockDataManager).getIsMyPayEnabled();
        mPresenter.generateOurpay();
        mTestScheduler.triggerActions();
        verify(mMockPaymentSuccessMvpView).showOurpay();
    }

    @Test
    public void noOurpayInteractionWhenMyPayDisabledTest(){

        doReturn(false).when(mMockDataManager).getIsMyPayEnabled();
        mPresenter.generateOurpay();
        mTestScheduler.triggerActions();
        verifyZeroInteractions(mMockPaymentSuccessMvpView);
    }
}
