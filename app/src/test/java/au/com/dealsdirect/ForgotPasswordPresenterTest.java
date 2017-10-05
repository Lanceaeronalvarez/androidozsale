package au.com.dealsdirect;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordRequest;
import au.com.dealsdirect.data.network.model.forgotpassword.ForgotPasswordResponseBody;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordMvpView;
import au.com.dealsdirect.ui.controller.forgotpassword.ForgotPasswordPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/5/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ForgotPasswordPresenterTest {

    @Mock
    DataManager mMockDataManager;

    @Mock
    ForgotPasswordMvpView mMvpView;

    ForgotPasswordPresenter<ForgotPasswordMvpView> mPresenter;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        mPresenter = new ForgotPasswordPresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMvpView);
    }

    @Test
    public void ForgotPresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }

    @Test
    public void forgotPasswordTest() {

        ForgotPasswordResponseBody forgotPasswordResponseBody = new ForgotPasswordResponseBody();
        ArgumentCaptor<ForgotPasswordRequest> forgotCaptor = ArgumentCaptor.forClass(ForgotPasswordRequest.class);

        doReturn(Observable.just(forgotPasswordResponseBody)).when(mMockDataManager).callForgotPassword(forgotCaptor.capture());
        mPresenter.forgotPassword("dd0091@dd.dd");
        testScheduler.triggerActions();

        verify(mMvpView).showForgotPasswordResponse(forgotPasswordResponseBody);
    }
}
