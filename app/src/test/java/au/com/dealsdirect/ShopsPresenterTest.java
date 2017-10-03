package au.com.dealsdirect;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.banner.GetBannerRequest;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpPresenter;
import au.com.dealsdirect.ui.controller.shops.ShopsMvpView;
import au.com.dealsdirect.ui.controller.shops.ShopsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
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

        GetBannerResponse getBannerResponse = new GetBannerResponse();
        doReturn(Observable.just(new ArrayList<GetBannerResponse>()))
                .when(mMockDataManager)
                .callGetBanners(getBannerRequest,true);

        mShopsPresenter.loadShopsBanner("","",0,10);

        mTestScheduler.triggerActions();

        verify(mMockShopsMvpView).showLoading();
        verify(mMockShopsMvpView).hideLoading();
        verify(mMockShopsMvpView).showShopBanners(new ArrayList<GetBannerResponse>());
    }
}
