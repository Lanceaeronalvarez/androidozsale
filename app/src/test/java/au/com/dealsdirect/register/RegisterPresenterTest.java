package au.com.dealsdirect.register;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.register.RegisterUserRequest;
import au.com.dealsdirect.data.network.model.register.RegisterUserResponse;
import au.com.dealsdirect.ui.controller.register.RegisterMvpView;
import au.com.dealsdirect.ui.controller.register.RegisterPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Paul on 10/5/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterPresenterTest {


    @Mock
    DataManager mMockDataManager;

    @Mock
    RegisterMvpView mMvpView;

    RegisterPresenter<RegisterMvpView> mPresenter;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        mPresenter = new RegisterPresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMvpView);
    }

    @Test
    public void register() {
        RegisterUserResponse registerUserResponse = new RegisterUserResponse();
        ArgumentCaptor<RegisterUserRequest> registerRequestCaptor =
                ArgumentCaptor.forClass(RegisterUserRequest.class);

        doReturn(Observable.just(registerUserResponse)).when(mMockDataManager).callRegiser(registerRequestCaptor.capture());
        mPresenter.registerUser("", "", "", "", true);
        testScheduler.triggerActions();

        verify(mMvpView).showLoginSuccessful(registerUserResponse.getTicket());
    }
}
