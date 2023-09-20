package au.com.dealsdirect.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.login.LoginEmail;
import au.com.dealsdirect.data.network.model.login.LoginFacebook;
import au.com.dealsdirect.ui.controller.checkout.checkout.CheckoutPresenter;
import au.com.dealsdirect.ui.controller.login.LoginMvpPresenter;
import au.com.dealsdirect.ui.controller.login.LoginMvpView;
import au.com.dealsdirect.ui.controller.login.LoginPresenter;
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
public class LoginPresenterTest {

    private static final String mMockLoginResponse = "{\n" +
            "\t\"d\": {\n" +
            "\t\t\"IsAuthenticated\": true,\n" +
            "\t\t\"ScheduledPlan\": {\n" +
            "\t\t\t\"Ticket\": \"46375e5a-23bb-498a-8c5a-749b46e8418c.^ChtwbwjF4flvkAfwhgw-gW3ZRo_\",\n" +
            "\t\t\t\"ReadTerms\": false\n" +
            "\t\t},\n" +
            "\t\t\"Result\": true,\n" +
            "\t\t\"Message\": \"\"\n" +
            "\t}\n" +
            "}";

    @Mock
    DataManager mMockDataManager;

    @Mock
    LoginMvpView mMockLoginView;

    LoginMvpPresenter<LoginMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new LoginPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockLoginView);
    }

    @Test
    public void testLoginViaEmailSuccess() {

        String userName = "jpaseneta";
        String password = "12345678";

        LoginEmail.ResponseValue loginResponse = gson.fromJson(mMockLoginResponse, LoginEmail.ResponseValue.class);

        ArgumentCaptor<LoginEmail.RequestValue> requestCaptor = ArgumentCaptor.forClass(LoginEmail.RequestValue.class);

        doReturn(Observable.just(loginResponse))
                .when(mMockDataManager).callLoginViaEmail(requestCaptor.capture());

        mPresenter.loginViaEmail(null, userName, password);
        mTestScheduler.triggerActions();

        verify(mMockDataManager).acknowledgeAuth(loginResponse.getTicket());
        verify(mMockLoginView).showLoginSuccessful(loginResponse.getTicket(), false);
    }

    @Test
    public void testLoginViaFacebook() {

        JsonParser jsonParser = new JsonParser();
        JsonObject jo = (JsonObject) jsonParser.parse(mMockLoginResponse);
        JsonObject d = jo.getAsJsonObject("d");

        doReturn(Observable.just(jo))
                .when(mMockDataManager).callLoginViaFacebook(any(LoginFacebook.RequestValue.class));

        mPresenter.loginViaFacebook("", "", "", "", "");
        mTestScheduler.triggerActions();

        Assert.assertEquals(d.get("IsAuthenticated").getAsBoolean() && d.get("Result").getAsBoolean(), true);
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
