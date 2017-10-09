package au.com.dealsdirect.categories;

import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.category.GetCategoryTreeResponse;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpPresenter;
import au.com.dealsdirect.ui.controller.categories.CategoriesMvpView;
import au.com.dealsdirect.ui.controller.categories.CategoriesPresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by smartwave on 06/10/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class CategoriesPresenterTest {

    @Mock
    CategoriesMvpView mMockCategoriesView;

    @Mock
    DataManager mMockDataManager;

    private CategoriesMvpPresenter<CategoriesMvpView> mPresenter;
    private TestScheduler mTestScheduler;
    Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider testSchedulerProvider = new TestSchedulerProvider(mTestScheduler);
        mPresenter = new CategoriesPresenter<>(
                mMockDataManager,
                testSchedulerProvider,
                compositeDisposable);
        mPresenter.onAttach(mMockCategoriesView);
    }

    @Test
    public void testLoadCategories(){
        List<GetCategoryTreeResponse> response = new ArrayList<>();
        doReturn(Observable.just(response))
                .when(mMockDataManager).callGetGetCategories();

        mPresenter.callGetCategoryTree();
        mTestScheduler.triggerActions();

        verify(mMockCategoriesView).hideLoading();
        verify(mMockCategoriesView).showCategories(response);
        verify(mMockCategoriesView).hideNoNetworklayout();

    }

    @Test
    public void testCallGetCategoriesErrorOccured(){
        String errMsg = "error";

        doReturn(Observable.error(new Exception(errMsg)))
                .when(mMockDataManager).callGetGetCategories();

        mPresenter.callGetCategoryTree();
        mTestScheduler.triggerActions();

        verify(mMockCategoriesView).hideLoading();
        verify(mMockCategoriesView).onError(errMsg);

    }

    @After
    public void tearDown() throws Exception {
        mPresenter.onDetach();
    }
}
