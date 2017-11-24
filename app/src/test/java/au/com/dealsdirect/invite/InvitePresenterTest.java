package au.com.dealsdirect.invite;

import com.google.gson.Gson;

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

    private static final String mMockDefaultResponseSuccess = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"ScheduledPlan\": {\n" +
            "    },\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";


    @Mock
    DataManager mMockDataManager;

    @Mock
    InviteMvpView inviteMvpView;

    InvitePresenter<InviteMvpView> mPresenter;
    TestScheduler testScheduler;
    Gson gson = new Gson();

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

        doReturn("DA").when(mMockDataManager).getCountryId();
        doReturn("EN").when(mMockDataManager).getLanguageId();

        GetInviteRequest request = new GetInviteRequest();
        GetInviteResponse getInviteResponse = gson.fromJson(mMockDefaultResponseSuccess,GetInviteResponse.class);

        doReturn(Observable.just(getInviteResponse)).when(mMockDataManager).callGetInvite(request);
        mPresenter.getInviteLink(request);
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
