package au.com.dealsdirect.register;

import com.google.gson.Gson;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/5/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterPresenterTest {

    private static final String mMockRegisterUserResponseSuccess = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "      \"Ticket\" : \"12345678\",\n" +
            "      \"Registered\" : true\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockRegisterUserResponseFailure = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"Value\": {\n" +
            "    },\n" +
            "    \"Result\": false,\n" +
            "    \"Message\": \"error\"\n" +
            "  }\n" +
            "}";

    @Mock
    DataManager mMockDataManager;

    @Mock
    RegisterMvpView mMvpView;

    RegisterPresenter<RegisterMvpView> mPresenter;
    TestScheduler testScheduler;
    Gson gson = new Gson();

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        mPresenter = new RegisterPresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMvpView);
    }

    @Test
    public void testRegisterUserSuccess() {
        RegisterUserResponse registerUserResponse = gson.fromJson(mMockRegisterUserResponseSuccess,RegisterUserResponse.class);

        doReturn(Observable.just(registerUserResponse)).when(mMockDataManager).callRegister(any(RegisterUserRequest.class));
        mPresenter.registerUser("", "", "", "", true);
        testScheduler.triggerActions();

        verify(mMockDataManager).acknowledgeAuth(registerUserResponse.getTicket());
        verify(mMvpView).showLoginSuccessful(registerUserResponse.getTicket());
    }

    @Test
    public void testRegisterUserFailure() {
        RegisterUserResponse registerUserResponse = gson.fromJson(mMockRegisterUserResponseFailure, RegisterUserResponse.class);

        doReturn(Observable.just(registerUserResponse)).when(mMockDataManager).callRegister(any(RegisterUserRequest.class));
        mPresenter.registerUser("", "", "", "", true);
        testScheduler.triggerActions();

        verify(mMvpView).showLoginError(registerUserResponse.getMessage());
    }
}
