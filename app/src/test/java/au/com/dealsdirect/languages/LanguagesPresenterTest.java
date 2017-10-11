package au.com.dealsdirect.languages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mysale.genie.utility.config.model.getserversettings.Language;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import au.com.dealsdirect.data.DataManager;
import au.com.dealsdirect.data.network.model.legalities.GetTemplateTextRequest;
import au.com.dealsdirect.ui.controller.language.LanguageMvpView;
import au.com.dealsdirect.ui.controller.language.LanguagePresenter;
import au.com.dealsdirect.utils.rx.TestSchedulerProvider;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * dp Created by Admin on 10/10/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class LanguagesPresenterTest {

    @Mock
    LanguageMvpView mMockLanguageMvpView;

    @Mock
    DataManager mMockDataManager;

    private LanguagePresenter<LanguageMvpView> mPresenter;
    private TestScheduler mTestScheduler;


    @Before
    public void setup() throws Exception {
        mTestScheduler = new TestScheduler();
        TestSchedulerProvider schedulerProvider = new TestSchedulerProvider(mTestScheduler);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        mPresenter = new LanguagePresenter<>(mMockDataManager, schedulerProvider, compositeDisposable);
        mPresenter.onAttach(mMockLanguageMvpView);
    }

    @Test
    public void showLanguagesTest() {
        String languagesJsonString = "[{'Culture':'en-au','ID':'en','Name':'English'}]";

        doReturn(languagesJsonString).when(mMockDataManager).getLanguages();
        doReturn("en").when(mMockDataManager).getLanguageId();


        mPresenter.getUserLanguages();
        mTestScheduler.triggerActions();
        verify(mMockLanguageMvpView).showLanguages(anyList(),anyString());
        verify(mMockLanguageMvpView).hideLoading();

    }

    @Test
    public void onLanguageItemClickTest(){
        Language language = new Language();
        language.setID("testId");
        language.setCulture("testCulture");
        language.setName("testName");

        mPresenter.onLanguageItemClick(language);
        mTestScheduler.triggerActions();
        verify(mMockLanguageMvpView).showLanguageLanguageDialog(language.getName());
        verify(mMockLanguageMvpView).onBackPress();
    }

    @Test
    public void LanguagePresenterNullCheck() {
        Assert.assertNotNull(mPresenter);
    }
}
