package au.com.dealsdirect.returns;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequest;
import au.com.dealsdirect.data.network.model.returns.createreturn.CreateReturnRequestResponseBody;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailRequest;
import au.com.dealsdirect.data.network.model.returns.newreturn.NewReturnOrderDetailResponseBody;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnMvpView;
import au.com.dealsdirect.ui.controller.returns.newreturn.NewReturnPresenter;
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
public class NewReturnPresenterTest {

    @Mock
    NewReturnMvpView mMockNewReturnView;

    @Mock
    DataManager mMockDataManager;

    private NewReturnPresenter<NewReturnMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mPresenter = new NewReturnPresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockNewReturnView);
    }

    @Test
    public void addNewReturnOrderRequestTest() {
        CreateReturnRequestResponseBody getCreateReturnResponse = new CreateReturnRequestResponseBody();
        ArgumentCaptor<CreateReturnRequest> setReturnCaptor = ArgumentCaptor.forClass(CreateReturnRequest.class);
        doReturn(Observable.just(getCreateReturnResponse)).when(mMockDataManager).callCreateReturnRequest(setReturnCaptor.capture());

        CreateReturnRequest createReturnRequest = new CreateReturnRequest();
        createReturnRequest.invoiceNo = "10002";
        createReturnRequest.reason = "test reason";
        createReturnRequest.items = new ArrayList<>();

        mPresenter.addNewReturnOrderRequest(createReturnRequest);
        mTestScheduler.triggerActions();

        verify(mMockNewReturnView).finishCreateReturnRequest(getCreateReturnResponse);
    }

    @Test
    public void ReturnDetailsPresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }

    @Test
    public void getReturnOrderDetailTest() {
        NewReturnOrderDetailResponseBody getNewReturnDetailResponse = new NewReturnOrderDetailResponseBody();
        ArgumentCaptor<NewReturnOrderDetailRequest> setReturnCaptor = ArgumentCaptor.forClass(NewReturnOrderDetailRequest.class);
        doReturn(Observable.just(getNewReturnDetailResponse)).when(mMockDataManager).callGetNewReturnOrderDetail(setReturnCaptor.capture());

        mPresenter.getReturnOrderDetail(1);
        mTestScheduler.triggerActions();

        verify(mMockNewReturnView).loadReturnOrderDetail(getNewReturnDetailResponse.getNewReturnOrderDetailResponse());
    }

}
