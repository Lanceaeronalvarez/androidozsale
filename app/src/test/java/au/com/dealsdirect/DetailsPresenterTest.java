package au.com.dealsdirect;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.userdetails.GetUserDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.ui.controller.details.DetailsMvpView;
import au.com.dealsdirect.ui.controller.details.DetailsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by Paul on 10/4/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest {

    @Mock
    DetailsMvpView mMockDetailsView;

    @Mock
    DataManager mMockDataManager;

    private DetailsPresenter<DetailsMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mPresenter = new DetailsPresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockDetailsView);
    }

    @Test
    public void loadUserTest() {
        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        ArgumentCaptor<SetUserDetailsRequest> setUserCaptor = ArgumentCaptor.forClass(SetUserDetailsRequest.class);
        doReturn(Observable.just(setUserDetailsRequest)).when(mMockDataManager).getLoadUserDetailsApiCall(setUserCaptor.capture());

        mPresenter.loadUser(setUserDetailsRequest);
        GetUserDetailsResponse getUserDetailsResponse = new GetUserDetailsResponse();
        verify(mMockDetailsView).loadDetails(getUserDetailsResponse);
    }

    @Test
    public void DetailsPresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }

    @Test
    public void sendUserDetailsTest() {
        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();
        ArgumentCaptor<SetUserDetailsRequest> setUserCaptor = ArgumentCaptor.forClass(SetUserDetailsRequest.class);
        doReturn(Observable.just(setUserDetailsRequest)).when(mMockDataManager).getSaveUserDetailsApiCall(setUserCaptor.capture());

        mPresenter.sendUserDetails("", "", "", "", true, "", "", "", "");

        ArgumentCaptor<GetUserDetailsResponse> arg = ArgumentCaptor.forClass(GetUserDetailsResponse.class);
        GetUserDetailsResponse getUserDetailsResponse = new GetUserDetailsResponse();

//        verify(mMockDetailsView, times(2)).loadDetails(getUserDetailsResponse);
    }

    @Test
    public void saveUserLanguageTest() {
        SetUserDetailsRequest setUserDetailsRequest = new SetUserDetailsRequest();

        mPresenter.saveUser(setUserDetailsRequest);
    }

}
