package au.com.dealsdirect.searchfilter;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpPresenter;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterMvpView;
import au.com.dealsdirect.ui.controller.searchfilter.SearchFilterPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

/**
 * Created by smartwave on 09/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class SearchFilterPresenterTest {

    @Mock
    SearchFilterMvpView mMockSearchFilterView;

    @Mock
    DataManager mMockDataManager;

    private SearchFilterMvpPresenter<SearchFilterMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new SearchFilterPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockSearchFilterView);
    }



    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
