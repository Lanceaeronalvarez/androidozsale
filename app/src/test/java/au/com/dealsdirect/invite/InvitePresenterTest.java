package au.com.dealsdirect.invite;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.invite.GetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.GetInviteResponse;
import au.com.dealsdirect.data.network.model.invite.SetInviteRequest;
import au.com.dealsdirect.data.network.model.invite.SetInviteResponse;
import au.com.dealsdirect.ui.controller.invite.InviteMvpView;
import au.com.dealsdirect.ui.controller.invite.InvitePresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/4/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class InvitePresenterTest {

    @Mock
    DataManager mMockDataManager;

    @Mock
    InviteMvpView inviteMvpView;

    InvitePresenter<InviteMvpView> mPresenter;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        mPresenter = new InvitePresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(inviteMvpView);
    }

    @Test
    public void getInviteLinkTest() {

        GetInviteRequest getInviteRequest = new GetInviteRequest();
        getInviteRequest.countryId = mMockDataManager.getCountryId();
        getInviteRequest.languageId = mMockDataManager.getLanguageId();

        GetInviteResponse getInviteResponse = new GetInviteResponse();
        doReturn(Observable.just(getInviteResponse)).when(mMockDataManager).callGetInvite(getInviteRequest);
        mPresenter.getInviteLink(getInviteRequest);
        testScheduler.triggerActions();

        verify(inviteMvpView).showInviteLink(getInviteResponse);

    }

    @Test
    public void setInviteLinkTest() {
        SetInviteRequest setInviteRequest = new SetInviteRequest();
        SetInviteResponse setInviteResponse = new SetInviteResponse();
        ArgumentCaptor<SetInviteRequest> setInviteRequestCaptor = ArgumentCaptor.forClass(SetInviteRequest.class);

        doReturn(Observable.just(setInviteResponse)).when(mMockDataManager).callSetInvite(setInviteRequestCaptor.capture());

        mPresenter.setInviteLink(setInviteRequest);
        testScheduler.triggerActions();

        verify(inviteMvpView).onInviteLinkSet(setInviteResponse);
    }
}
