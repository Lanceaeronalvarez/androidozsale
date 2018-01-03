package au.com.dealsdirect.saleitems;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsRequest;
import au.com.dealsdirect.data.network.model.saleitems.GetSaleItemsResponse;
import au.com.dealsdirect.data.network.model.sorting.SortingResponse;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsMvpView;
import au.com.dealsdirect.ui.controller.saleitems.SaleItemsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by Ayi on 10/6/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SaleItemsPresenterTest {

    @Mock
    SaleItemsMvpView mMockView;

    @Mock
    DataManager mMockDataManager;

    private SaleItemsPresenter<SaleItemsMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mPresenter = new SaleItemsPresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockView);
    }

    @Test
    public void SaleItemsPresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }

    @Test
    public void loadSaleItemsTestNoFilters() {
        GetSaleItemsResponse getSaleItemsResponse = new GetSaleItemsResponse();

        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();
        getSaleItemsRequest.setHasFilters(false);

        doReturn(Observable.just(getSaleItemsResponse)).when(mMockDataManager).callGetSaleItemsRequest(getSaleItemsRequest);

        mPresenter.loadSaleItems(getSaleItemsRequest);
        mTestScheduler.triggerActions();

        verify(mMockView).showSaleItems(getSaleItemsResponse, true);
    }

    @Test
    public void loadSaleItemsTestHasFilters(){
        GetSaleItemsRequest getSaleItemsRequest = new GetSaleItemsRequest();
        getSaleItemsRequest.setHasFilters(true);

        GetSaleItemsResponse getSaleItemsResponse = new GetSaleItemsResponse();

        doReturn(Observable.just(getSaleItemsResponse)).when(mMockDataManager).callGetSaleItemsRequest(getSaleItemsRequest);

        mPresenter.loadSaleItems(getSaleItemsRequest);
        mTestScheduler.triggerActions();

        verify(mMockView).showSaleItems(getSaleItemsResponse, false);
    }

    @Test
    public void loadSortingFacetsTest() {
        List<SortingResponse> sortingResponse = new ArrayList<>();
        doReturn(Observable.just(sortingResponse)).when(mMockDataManager).callSortingFacets();

        mPresenter.loadSortingFacets();
        mTestScheduler.triggerActions();

        verify(mMockView).onLoadSortingFacetsFinished(sortingResponse);
    }
}
