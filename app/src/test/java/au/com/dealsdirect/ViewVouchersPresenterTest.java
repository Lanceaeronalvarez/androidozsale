package au.com.dealsdirect;

import android.support.v4.util.Pair;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersMvpView;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.PublishSubject;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Paul on 10/3/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ViewVouchersPresenterTest {

    @Mock
    ViewVouchersMvpView mMockViewVouchersMvpView;

    @Mock
    DataManager mMockDataManager;

    @Mock
    ViewVouchersPresenter<ViewVouchersMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);

        mPresenter = new ViewVouchersPresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockViewVouchersMvpView);
    }

    @Test
    public void testLoadMyVouchers() {
        final PublishSubject<List<GetUserVoucherResponse.Voucher>> selectSubject
                = PublishSubject.create();
        final PublishSubject<GetVouchersResponse> selectSubject2
                = PublishSubject.create();

        mTestScheduler.triggerActions();

        ArgumentCaptor<GetUserVouchersRequest> getUserVouchersRequestCaptor =
                ArgumentCaptor.forClass(GetUserVouchersRequest.class);
        ArgumentCaptor<Pair<List<GetUserVoucherResponse.Voucher>,GetVouchersResponse>>
                pairCaptor = ArgumentCaptor.forClass(Pair.class);

        doReturn(selectSubject).when(mMockDataManager).callGetUserVouchers(getUserVouchersRequestCaptor.capture());
        doReturn(selectSubject2).when(mMockDataManager).callGetVouchers(getUserVouchersRequestCaptor.capture());


        mPresenter.loadMyVouchers();
//        verify(mMockViewVouchersMvpView, times(2)).updateVoucherList(pairCaptor.capture());
    }

    @Test
    public void ViewVoucherPresenterNotNull() {
        Assert.assertNotNull(mPresenter);
    }
}
