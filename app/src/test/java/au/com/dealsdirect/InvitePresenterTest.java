package au.com.dealsdirect;

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

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        TestScheduler testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        mPresenter = new InvitePresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(inviteMvpView);
    }

    @Test
    public void getInviteLinkTest() {

        GetInviteRequest getInviteRequest = new GetInviteRequest();
        ArgumentCaptor<GetInviteRequest> inviteRequestCaptor = ArgumentCaptor.forClass(GetInviteRequest.class);
        doReturn(Observable.just(getInviteRequest)).when(mMockDataManager).callGetInvite(inviteRequestCaptor.capture());

        mPresenter.getInviteLink(getInviteRequest);

    }

    @Test
    public void setInviteLinkTest() {
        SetInviteRequest setInviteRequest = new SetInviteRequest();
        ArgumentCaptor<SetInviteRequest> setInviteRequestCaptor = ArgumentCaptor.forClass(SetInviteRequest.class);
        doReturn(Observable.just(setInviteRequest)).when(mMockDataManager).callSetInvite(setInviteRequestCaptor.capture());
        mPresenter.setInviteLink(setInviteRequest);
    }
}
