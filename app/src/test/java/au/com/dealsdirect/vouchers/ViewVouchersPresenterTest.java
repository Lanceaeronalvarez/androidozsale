package au.com.dealsdirect.vouchers;

import androidx.core.util.Pair;

import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVoucherResponse;
import au.com.dealsdirect.data.network.model.vouchers.GetUserVouchersRequest;
import au.com.dealsdirect.data.network.model.vouchers.GetVouchersResponse;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersMvpView;
import au.com.dealsdirect.ui.controller.vouchers.View.ViewVouchersPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Paul on 10/3/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class ViewVouchersPresenterTest {

    private static final String mMockGetUserVouchersResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"List\": [\n" +
            "      {\n" +
            "        \"Fullname\": \"Ozsale.com.au\",\n" +
            "        \"Activated\": true,\n" +
            "        \"FirstPurchase\": true,\n" +
            "        \"DiscountLeft\": \"$20.00\",\n" +
            "        \"Expired\": \"3/07/2017\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    private static final String mMockGetVouchersResponse = "{\n" +
            "  \"d\": {\n" +
            "    \"IsAuthenticated\": true,\n" +
            "    \"List\": [],\n" +
            "    \"Result\": true,\n" +
            "    \"Message\": \"\"\n" +
            "  }\n" +
            "}";

    @Mock
    ViewVouchersMvpView mMockViewVouchersMvpView;

    @Mock
    DataManager mMockDataManager;

    @Mock
    ViewVouchersPresenter<ViewVouchersMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

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

        doReturn("").when(mMockDataManager).getLanguageId();

        GetVouchersResponse getVouchersResponse = new GetVouchersResponse();
        GetUserVoucherResponse getUserVouchersResponse = gson.fromJson(mMockGetUserVouchersResponse,GetUserVoucherResponse.class);

        doReturn(Observable.just(getUserVouchersResponse)).when(mMockDataManager).callGetUserVouchers(any(GetUserVouchersRequest.class));
        doReturn(Observable.just(getVouchersResponse)).when(mMockDataManager).callGetVouchers(any(GetUserVouchersRequest.class));

        ArgumentCaptor<Pair> pairArgumentCaptor = ArgumentCaptor.forClass(Pair.class);

        mPresenter.loadMyVouchers();
        mTestScheduler.triggerActions();
        verify(mMockViewVouchersMvpView).updateVoucherList(pairArgumentCaptor.capture());
    }

    @Test
    public void ViewVoucherPresenterNotNull() {
        Assert.assertNotNull(mPresenter);
    }
}
