package au.com.dealsdirect.returns;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponse;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsMvpView;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * dp Created by smartwave on 06/10/2017.
 */

@RunWith(MockitoJUnitRunner.class)
public class ReturnDetailsPresenterTest {

    @Mock
    ReturnDetailsMvpView mMockDetailsView;

    @Mock
    DataManager mMockDataManager;


    private ReturnDetailsPresenter<ReturnDetailsMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mPresenter = new ReturnDetailsPresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockDetailsView);
    }

    @Test
    public void loadCurrentReturnDetailsTest() {
        GetReturnDetailsResponse getReturnDetailsResponse = new GetReturnDetailsResponse();
        ArgumentCaptor<GetReturnDetailRequest> setReturnCaptor = ArgumentCaptor.forClass(GetReturnDetailRequest.class);
        doReturn(Observable.just(getReturnDetailsResponse)).when(mMockDataManager).callGetReturnDetails(setReturnCaptor.capture());

        mPresenter.loadCurrentReturnDetails("14231");
        mTestScheduler.triggerActions();

        verify(mMockDetailsView).showCurrentReturnDetails(getReturnDetailsResponse.getGetReturnDetailsResponseBody());
    }

    @Test
    public void ReturnDetailsPresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }

}
