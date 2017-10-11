package au.com.dealsdirect.legalities;

import junit.framework.Assert;

import net.bytebuddy.implementation.bytecode.Throw;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.banner.GetBannerResponse;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextRequest;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextResponse;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailRequest;
import au.com.dealsdirect.data.network.model.returns.returndetails.GetReturnDetailsResponse;
import au.com.dealsdirect.data.network.model.userdetails.SetUserDetailsRequest;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesMvpView;
import au.com.dealsdirect.ui.controller.legalities.LegalitiesPresenter;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsMvpView;
import au.com.dealsdirect.ui.controller.returns.returndetails.ReturnDetailsPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * dp Created by Admin on 10/9/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class LegalitiesPresenterTest {

    @Mock
    LegalitiesMvpView mMockLegalitiesMvpView;

    @Mock
    DataManager mMockDataManager;

    private LegalitiesPresenter<LegalitiesMvpView> mPresenter;
    private TestScheduler mTestScheduler;

    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mPresenter = new LegalitiesPresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockLegalitiesMvpView);
    }

    @Test
    public void loadTextTest() {
        GetTemplateTextResponse getTemplateTextResponse = new GetTemplateTextResponse();
        ArgumentCaptor<GetTemplateTextRequest> loadTextRequestCaptor = ArgumentCaptor.forClass(GetTemplateTextRequest.class);

        doReturn(Observable.just(getTemplateTextResponse))
                .when(mMockDataManager)
                .callGetTemplateText(loadTextRequestCaptor.capture());

        mPresenter.loadText("_OurPayThankYouTextMobileApp");
        mTestScheduler.triggerActions();

        verify(mMockLegalitiesMvpView).onError(null);

    }

    @Test
    public void LegalitiesPresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }

}


