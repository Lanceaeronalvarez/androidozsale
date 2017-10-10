package au.com.dealsdirect.vouchers;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyRequest;
import au.com.dealsdirect.data.network.model.vouchers.AddAndApplyVoucherByKeyResponse;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ApplyVouchersResponse;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.ClearVouchersResponse;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersMvpPresenter;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersMvpView;
import au.com.dealsdirect.ui.controller.vouchers.Add.AddVouchersPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/5/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class AddVoucherPresenterTest {
    @Mock
    DataManager mMockDataManager;

    @Mock
    AddVouchersMvpView mMvpView;

    AddVouchersMvpPresenter<AddVouchersMvpView> mPresenter;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        testScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(testScheduler);
        mPresenter = new AddVouchersPresenter<>(mMockDataManager, testSchedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMvpView);
    }

    @Test
    public void clearVouchers() {
        ArgumentCaptor<ClearVouchersRequest> clearCaptor = ArgumentCaptor.forClass(ClearVouchersRequest.class);
        ClearVouchersResponse clearVouchersResponse = new ClearVouchersResponse();

        doReturn(Observable.just(clearVouchersResponse)).when(mMockDataManager).callGetClearVouchers(clearCaptor.capture());
        mPresenter.clearVouchers(1);
        testScheduler.triggerActions();

        verify(mMvpView).showLoading();
        verify(mMvpView).onVouchersCleared(clearVouchersResponse);
    }

    @Test
    public void addAndApplyVoucherByKeyTest(){
        AddAndApplyVoucherByKeyResponse applyVoucherByKeyResponse =new AddAndApplyVoucherByKeyResponse();
        ArgumentCaptor<AddAndApplyVoucherByKeyRequest> captor = ArgumentCaptor.forClass(AddAndApplyVoucherByKeyRequest.class);

        doReturn(Observable.just(applyVoucherByKeyResponse)).when(mMockDataManager).callGetAddAndApplyVoucherByKey(captor.capture());

        mPresenter.addAndApplyVoucherByKey(1, "");
        testScheduler.triggerActions();

        verify(mMvpView).showLoading();
        verify(mMvpView).onAddAndAppliedVoucher(applyVoucherByKeyResponse);
    }

    @Test
    public void applyVouchersTest() {
        ApplyVouchersResponse applyVouchersResponse =new ApplyVouchersResponse();
        ArgumentCaptor<ApplyVouchersRequest> captor = ArgumentCaptor.forClass(ApplyVouchersRequest.class);

        doReturn(Observable.just(applyVouchersResponse)).when(mMockDataManager).callGetApplyVouchers(captor.capture());

        mPresenter.applyVouchers(1, new ArrayList<>());
        testScheduler.triggerActions();

        verify(mMvpView).showLoading();
        verify(mMvpView).onVouchersApplied(applyVouchersResponse);
    }

    @Test
    public void checkPresenter() {
        Assert.assertNotNull(mPresenter);
    }
}
