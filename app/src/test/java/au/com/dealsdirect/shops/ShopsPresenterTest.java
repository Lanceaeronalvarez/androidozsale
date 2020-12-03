package au.com.dealsdirect.shops;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpView;
import au.com.dealsdirect.ui.controller.shops.ShopsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 03/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShopsPresenterTest {
    @Mock
    ShopsMvpView mMockShopsMvpView;
    @Mock
    DataManager mMockDataManager;

    private ShopsMvpPresenter<ShopsMvpView> mShopsPresenter;
    private TestScheduler mTestScheduler;

    @BeforeClass
    public static void onlyOnce() throws Exception {
    }


    @Before
    public void setUp() throws Exception {

        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mShopsPresenter = new ShopsPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mShopsPresenter.onAttach(mMockShopsMvpView);
    }

    @Test
    public void testLoadShopsBanner(){

        GetBannerRequest getBannerRequest = new GetBannerRequest();
        getBannerRequest.setOffset(String.valueOf(0));
        getBannerRequest.setLimit(String.valueOf(10));

        List<GetBannerResponse> getBannerResponse = new ArrayList<>();
        doReturn(Observable.just(getBannerResponse))
                .when(mMockDataManager)
                .callGetBanners(getBannerRequest,true);

        mShopsPresenter.loadShopsBanner(getBannerRequest,true);

        mTestScheduler.triggerActions();

        ArgumentCaptor<List<GetBannerResponse>> argument = ArgumentCaptor.forClass((Class) List.class);

        verify(mMockShopsMvpView).hideLoading();
        verify(mMockShopsMvpView).showShopBanners(argument.capture());
        Assert.assertNotNull(argument.getValue());
    }

    @Test
    public void loadCategories(){
        List<GetCategoryTreeResponse> response = new ArrayList<>();
        doReturn(Observable.just(response))
                .when(mMockDataManager)
                .callGetCategories();

        mShopsPresenter.loadCategoryTree();

        mTestScheduler.triggerActions();

        verify(mMockShopsMvpView).hideLoading();
        verify(mMockShopsMvpView).storeCategories(response);
    }

    @After
    public void tearDown() throws Exception {
        mShopsPresenter.onDetach();
    }
}
