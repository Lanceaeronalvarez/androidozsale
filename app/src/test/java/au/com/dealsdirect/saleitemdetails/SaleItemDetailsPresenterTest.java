package au.com.dealsdirect.saleitemdetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.saleitemdetails.AddToCartRequest;
import au.com.dealsdirect.data.network.model.saleitemdetails.SaleItemDetails;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsMvpPresenter;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsMvpView;
import au.com.dealsdirect.ui.controller.saleitemdetails.SaleItemDetailsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

/**
 * Created by smartwave on 09/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaleItemDetailsPresenterTest {

    @Mock
    SaleItemDetailsMvpView mMockSaleItemDetailsView;
    @Mock
    DataManager mMockDataManager;

    private SaleItemDetailsMvpPresenter<SaleItemDetailsMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new SaleItemDetailsPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockSaleItemDetailsView);
    }

    @Test
    public void testLoadSaleItemDetails(){
        SaleItemDetails response = new SaleItemDetails();

        doReturn(Observable.just(response))
                .when(mMockDataManager).callGetSaleItemDetails("");

        mPresenter.loadProductDetails("");
        mTestScheduler.triggerActions();

        verify(mMockSaleItemDetailsView).showProductDetails(response);
    }

    @Test
    public void testAddToCart(){

        doReturn(Observable.just("")).when(mMockDataManager).callAddItemToCart(any(AddToCartRequest.class));
        mPresenter.addToCart(new AddToCartRequest(""), null);
        mTestScheduler.triggerActions();

        verify(mMockSaleItemDetailsView).showAddToCartResponse(true);
    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
